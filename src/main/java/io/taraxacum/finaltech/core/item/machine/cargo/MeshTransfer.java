package io.taraxacum.finaltech.core.item.machine.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.cargo.MeshTransferInventory;
import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.dto.SimpleCargoDTO;
import io.taraxacum.finaltech.core.option.*;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.CargoUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public class MeshTransfer extends AbstractCargo implements RecipeItem, LogicInjectableItem {
    private final double particleDistance = 0.25;
    private final int particleInterval = 2;
    private int[] itemMatch;
    private BiConsumer<Inventory, LocationData> logicInjectInventoryUpdater;

    public MeshTransfer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        MeshTransferInventory meshTransferInventory = new MeshTransferInventory(this);
        this.itemMatch = meshTransferInventory.itemMatchSlot;
        this.logicInjectInventoryUpdater = meshTransferInventory::updateCargoFilter;
        return meshTransferInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {
                Block block = blockPlaceEvent.getBlock();
                Location location = block.getLocation();
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if(locationData != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, ConstantTableUtil.CONFIG_UUID, blockPlaceEvent.getPlayer().getUniqueId().toString());

                    CargoFilter.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchMode.MESH_INPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchMode.MESH_OUTPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);

                    FinalTech.getLocationDataService().setLocationData(locationData, PositionInfo.KEY, "");
                }
            }
        };
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.itemMatch);
    }

    @Override
    public void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        boolean drawParticle = !inventory.getViewers().isEmpty() || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

        BlockFace[] outputBlockFaces = PositionInfo.getBlockFaces(FinalTech.getLocationDataService(), locationData, PositionInfo.VALUE_OUTPUT, PositionInfo.VALUE_INPUT_AND_OUTPUT);
        BlockFace[] inputBlockFaces = PositionInfo.getBlockFaces(FinalTech.getLocationDataService(), locationData, PositionInfo.VALUE_INPUT, PositionInfo.VALUE_INPUT_AND_OUTPUT);
        Block[] outputBlocks = new Block[outputBlockFaces.length];
        Block[] inputBlocks = new Block[inputBlockFaces.length];
        String outputBlockSearchMode = BlockSearchMode.MESH_OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
        String inputBlockSearchMode = BlockSearchMode.MESH_INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

        if (javaPlugin.getServer().isPrimaryThread()) {
            for (int i = 0; i < outputBlocks.length; i++) {
                outputBlocks[i] = this.searchBlock(block, outputBlockFaces[i], outputBlockSearchMode, drawParticle);
                if(outputBlocks[i] == null) {
                    return;
                }
            }
            for (int i = 0; i < inputBlocks.length; i++) {
                inputBlocks[i] = this.searchBlock(block, inputBlockFaces[i], inputBlockSearchMode, drawParticle);
                if(inputBlocks[i] == null) {
                    return;
                }
            }

            if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(inputBlocks)) || !PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(outputBlocks))) {
                return;
            }

            if (drawParticle) {
                javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
                    ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, inputBlocks);
                    ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, outputBlocks);
                }, Slimefun.getTickerTask().getTickRate());
            }

            // parse block storage

            String outputSize = SlotSearchSize.OUTPUT_OPTION.defaultValue();
            String outputOrder = SlotSearchOrder.OUTPUT_OPTION.defaultValue();
            int outputCargoNumber = Integer.parseInt(CargoNumber.OUTPUT_OPTION.defaultValue());
            String outputCargoNumberMode = CargoNumberMode.OUTPUT_OPTION.defaultValue();
            String outputCargoLimit = CargoLimit.OUTPUT_OPTION.defaultValue();

            String inputSize = SlotSearchSize.INPUT_OPTION.defaultValue();
            String inputOrder = SlotSearchOrder.INPUT_OPTION.defaultValue();
            int inputCargoNumber = Integer.parseInt(CargoNumber.INPUT_OPTION.defaultValue());
            String inputCargoNumberMode = CargoNumberMode.INPUT_OPTION.defaultValue();
            String inputCargoLimit = CargoLimit.INPUT_OPTION.defaultValue();

            String cargoFilter = CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

            InvWithSlots sourceInputMap = new InvWithSlots(inventory, this.getInputSlot());
            InvWithSlots sourceOutputMap = new InvWithSlots(inventory, this.getOutputSlot());

            // do cargo for outputs

            SimpleCargoDTO simpleCargoDTO = new SimpleCargoDTO();
            simpleCargoDTO.setLocationDataService(FinalTech.getLocationDataService());
            simpleCargoDTO.setCargoFilter(cargoFilter);
            simpleCargoDTO.setFilterInv(inventory);
            simpleCargoDTO.setFilterSlots(this.itemMatch);

            simpleCargoDTO.setInputMap(sourceOutputMap);
            simpleCargoDTO.setInputBlock(block);
            simpleCargoDTO.setInputSize(SlotSearchSize.VALUE_OUTPUTS_ONLY);
            simpleCargoDTO.setInputOrder(SlotSearchOrder.VALUE_ASCENT);

            simpleCargoDTO.setOutputSize(outputSize);
            simpleCargoDTO.setOutputOrder(outputOrder);
            simpleCargoDTO.setCargoLimit(outputCargoLimit);

            for (Block outputBlock : outputBlocks) {
                InvWithSlots outputMap;
                if (FinalTech.getLocationDataService().getInventory(outputBlock.getLocation()) != null) {
                    outputMap = null;
                } else {
                    outputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), outputBlock, outputSize, outputOrder);
                    if (outputMap == null) {
                        continue;
                    }
                }
                simpleCargoDTO.setOutputMap(outputMap);
                simpleCargoDTO.setOutputBlock(outputBlock);
                simpleCargoDTO.setCargoNumber(outputCargoNumber);
                int result = CargoUtil.doSimpleCargoInputMain(simpleCargoDTO);
                if (CargoNumberMode.VALUE_UNIVERSAL.equals(outputCargoNumberMode)) {
                    outputCargoNumber -= result;
                    if (outputCargoNumber == 0) {
                        break;
                    }
                }
            }

            // do cargo for itself

            simpleCargoDTO.setInputMap(sourceInputMap);
//            simpleCargoDTO.setInputBlock(block);
            simpleCargoDTO.setInputSize(SlotSearchSize.VALUE_INPUTS_ONLY);
//            simpleCargoDTO.setInputOrder(SlotSearchOrder.VALUE_ASCENT);

            simpleCargoDTO.setOutputMap(sourceOutputMap);
            simpleCargoDTO.setOutputBlock(block);
            simpleCargoDTO.setOutputSize(SlotSearchSize.VALUE_OUTPUTS_ONLY);
            simpleCargoDTO.setOutputOrder(SlotSearchOrder.VALUE_ASCENT);

            simpleCargoDTO.setCargoNumber(576);
            simpleCargoDTO.setCargoLimit(CargoLimit.VALUE_ALL);

            CargoUtil.doSimpleCargoStrongSymmetry(simpleCargoDTO);

            // do cargo for input

            simpleCargoDTO.setInputSize(inputSize);
            simpleCargoDTO.setInputOrder(inputOrder);
            simpleCargoDTO.setOutputMap(sourceInputMap);
//            simpleCargoDTO.setOutputBlock(block);
            simpleCargoDTO.setOutputSize(SlotSearchSize.VALUE_INPUTS_ONLY);
//            simpleCargoDTO.setOutputOrder(SlotSearchOrder.VALUE_ASCENT);
            simpleCargoDTO.setCargoLimit(inputCargoLimit);
//            simpleCargoDTO.setCargoFilter(cargoFilter);
//            simpleCargoDTO.setFilterInv(inventory);
//            simpleCargoDTO.setFilterSlots(this.itemMatch);

            for (Block inputBlock : inputBlocks) {
                InvWithSlots inputMap;
                if (FinalTech.getLocationDataService().getInventory(inputBlock.getLocation()) != null) {
                    inputMap = null;
                } else {
                    inputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), inputBlock, inputSize, inputOrder);
                    if (inputMap == null) {
                        continue;
                    }
                }
                simpleCargoDTO.setInputMap(inputMap);
                simpleCargoDTO.setInputBlock(inputBlock);
                simpleCargoDTO.setCargoNumber(inputCargoNumber);
                int result = CargoUtil.doSimpleCargoOutputMain(simpleCargoDTO);
                if (CargoNumberMode.VALUE_UNIVERSAL.equals(inputCargoNumberMode)) {
                    inputCargoNumber -= result;
                    if (inputCargoNumber == 0) {
                        break;
                    }
                }
            }
        } else {
            String outputSize = SlotSearchSize.OUTPUT_OPTION.defaultValue();
            String outputOrder = SlotSearchOrder.OUTPUT_OPTION.defaultValue();
            String inputSize = SlotSearchSize.INPUT_OPTION.defaultValue();
            String inputOrder = SlotSearchOrder.INPUT_OPTION.defaultValue();

            javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
                for (int i = 0; i < outputBlocks.length; i++) {
                    outputBlocks[i] = this.searchBlock(block, outputBlockFaces[i], outputBlockSearchMode, drawParticle);
                    if(outputBlocks[i] == null) {
                        return;
                    }
                }
                for (int i = 0; i < inputBlocks.length; i++) {
                    inputBlocks[i] = this.searchBlock(block, inputBlockFaces[i], inputBlockSearchMode, drawParticle);
                    if (inputBlocks[i] == null) {
                        return;
                    }
                }

                Inventory[] outputVanillaInventories = new Inventory[outputBlocks.length];
                Inventory[] inputVanillaInventories = new Inventory[inputBlocks.length];
                Location[] locations = new Location[outputBlocks.length + inputBlocks.length + 1];
                int p = 0;
                for (; p < outputBlocks.length; p++) {
                    locations[p] = outputBlocks[p].getLocation();
                    outputVanillaInventories[p] = CargoUtil.getVanillaInventory(outputBlocks[p]);
                }
                for (int i = 0; i < inputBlocks.length; i++) {
                    locations[i + p] = inputBlocks[i].getLocation();
                    inputVanillaInventories[i] = CargoUtil.getVanillaInventory(inputBlocks[i]);
                }
                locations[locations.length - 1] = block.getLocation();
                Location[] finalLocations = new HashSet<>(Arrays.asList(locations)).toArray(new Location[0]);
                FinalTech.getLocationRunnableFactory().waitThenRun(() -> {
                    if (FinalTech.getLocationDataService().getLocationData(block.getLocation()) == null) {
                        return;
                    }

                    if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, finalLocations)) {
                        return;
                    }

                    if (drawParticle) {
                        javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
                            ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, inputBlocks);
                            ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, outputBlocks);
                        }, Slimefun.getTickerTask().getTickRate());
                    }

                    // parse block storage

                    int outputCargoNumber = Integer.parseInt(CargoNumber.OUTPUT_OPTION.defaultValue());
                    String outputCargoNumberMode = CargoNumberMode.OUTPUT_OPTION.defaultValue();
                    String outputCargoLimit = CargoLimit.OUTPUT_OPTION.defaultValue();

                    int inputCargoNumber = Integer.parseInt(CargoNumber.INPUT_OPTION.defaultValue());
                    String inputCargoNumberMode = CargoNumberMode.INPUT_OPTION.defaultValue();
                    String inputCargoLimit = CargoLimit.INPUT_OPTION.defaultValue();

                    String cargoFilter = CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

                    InvWithSlots sourceInputMap = new InvWithSlots(inventory, this.getInputSlot());
                    InvWithSlots sourceOutputMap = new InvWithSlots(inventory, this.getOutputSlot());

                    // do cargo for outputs

                    SimpleCargoDTO simpleCargoDTO = new SimpleCargoDTO();
                    simpleCargoDTO.setLocationDataService(FinalTech.getLocationDataService());
                    simpleCargoDTO.setCargoFilter(cargoFilter);
                    simpleCargoDTO.setFilterInv(inventory);
                    simpleCargoDTO.setFilterSlots(this.itemMatch);

                    simpleCargoDTO.setInputMap(sourceOutputMap);
                    simpleCargoDTO.setInputBlock(block);
                    simpleCargoDTO.setInputSize(SlotSearchSize.VALUE_OUTPUTS_ONLY);
                    simpleCargoDTO.setInputOrder(SlotSearchOrder.VALUE_ASCENT);

                    simpleCargoDTO.setOutputSize(outputSize);
                    simpleCargoDTO.setOutputOrder(outputOrder);
                    simpleCargoDTO.setCargoLimit(outputCargoLimit);

                    for (int i = 0; i < outputBlocks.length; i++) {
                        Block outputBlock = outputBlocks[i];
                        InvWithSlots outputMap;
                        if (FinalTech.getLocationDataService().getInventory(outputBlock.getLocation()) != null) {
                            outputMap = null;
                        } else if (outputVanillaInventories[i] != null) {
                            outputMap = CargoUtil.calInvWithSlots(outputVanillaInventories[i], outputOrder);
                        } else {
                            continue;
                        }
                        simpleCargoDTO.setOutputMap(outputMap);
                        simpleCargoDTO.setOutputBlock(outputBlock);
                        simpleCargoDTO.setCargoNumber(outputCargoNumber);
                        int result = CargoUtil.doSimpleCargoInputMain(simpleCargoDTO);
                        if (CargoNumberMode.VALUE_UNIVERSAL.equals(outputCargoNumberMode)) {
                            outputCargoNumber -= result;
                            if (outputCargoNumber == 0) {
                                break;
                            }
                        }
                    }

                    // do cargo for itself

                    simpleCargoDTO.setInputMap(sourceInputMap);
//                    simpleCargoDTO.setInputBlock(block);
                    simpleCargoDTO.setInputSize(SlotSearchSize.VALUE_INPUTS_ONLY);
//                    simpleCargoDTO.setInputOrder(SlotSearchOrder.VALUE_ASCENT);
                    simpleCargoDTO.setOutputMap(sourceOutputMap);
                    simpleCargoDTO.setOutputBlock(block);
                    simpleCargoDTO.setOutputSize(SlotSearchSize.VALUE_OUTPUTS_ONLY);
                    simpleCargoDTO.setOutputOrder(SlotSearchOrder.VALUE_ASCENT);
                    simpleCargoDTO.setCargoNumber(576);
                    simpleCargoDTO.setCargoLimit(CargoLimit.VALUE_ALL);

                    CargoUtil.doSimpleCargoStrongSymmetry(simpleCargoDTO);

                    // do cargo for input

                    simpleCargoDTO.setInputSize(inputSize);
                    simpleCargoDTO.setInputOrder(inputOrder);
                    simpleCargoDTO.setOutputMap(sourceInputMap);
//                    simpleCargoDTO.setOutputBlock(block);
                    simpleCargoDTO.setOutputSize(SlotSearchSize.VALUE_INPUTS_ONLY);
//                    simpleCargoDTO.setOutputOrder(SlotSearchOrder.VALUE_ASCENT);
                    simpleCargoDTO.setCargoLimit(inputCargoLimit);
//                    simpleCargoDTO.setCargoFilter(cargoFilter);
//                    simpleCargoDTO.setFilterInv(inventory);
//                    simpleCargoDTO.setFilterSlots(this.itemMatch);

                    for (int i = 0; i < inputBlocks.length; i++) {
                        Block inputBlock = inputBlocks[i];
                        InvWithSlots inputMap;
                        if (FinalTech.getLocationDataService().getInventory(inputBlock.getLocation()) != null) {
                            inputMap = null;
                        } else if (inputVanillaInventories[i] != null) {
                            inputMap = CargoUtil.calInvWithSlots(inputVanillaInventories[i], inputOrder);
                        } else {
                            continue;
                        }
                        simpleCargoDTO.setInputMap(inputMap);
                        simpleCargoDTO.setInputBlock(inputBlock);
                        simpleCargoDTO.setCargoNumber(inputCargoNumber);
                        int result = CargoUtil.doSimpleCargoOutputMain(simpleCargoDTO);
                        if (CargoNumberMode.VALUE_UNIVERSAL.equals(inputCargoNumberMode)) {
                            inputCargoNumber -= result;
                            if (inputCargoNumber == 0) {
                                break;
                            }
                        }
                    }
                }, finalLocations);
            });
        }
    }

    @Nullable
    public Block searchBlock(@Nonnull Block sourceBlock, @Nonnull BlockFace blockFace, @Nonnull String searchMode, boolean drawParticle) {
        List<Location> particleLocationList = new ArrayList<>();
        particleLocationList.add(LocationUtil.getCenterLocation(sourceBlock));
        Block result = sourceBlock.getRelative(blockFace);
        if(!result.getChunk().isLoaded()) {
            return null;
        }
        if (BlockSearchMode.VALUE_ZERO.equals(searchMode)) {
            particleLocationList.add(LocationUtil.getCenterLocation(result));
            if (drawParticle && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.CRIT_MAGIC, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / particleLocationList.size(), this.particleDistance, particleLocationList));
            }
            return result;
        }
        while(true) {
            if(!result.getChunk().isLoaded()) {
                return null;
            }
            particleLocationList.add(LocationUtil.getCenterLocation(result));
            if (result.getType() == Material.CHAIN) {
                result = result.getRelative(blockFace);
                continue;
            }
            if (BlockSearchMode.VALUE_PENETRATE.equals(searchMode)
                    && FinalTech.getLocationDataService().getLocationData(result.getLocation()) instanceof SlimefunLocationData slimefunLocationData
                    && this.getId().equals(slimefunLocationData.getId())) {
                result = result.getRelative(blockFace);
                continue;
            }
            break;
        }
        if (drawParticle && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.CRIT_MAGIC, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / particleLocationList.size(), this.particleDistance, particleLocationList));
        }
        return result;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    @Override
    public void injectLogic(@Nonnull LocationData locationData, boolean logic) {
        if (logic) {
            CargoFilter.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, CargoFilter.VALUE_BLACK);
        } else {
            CargoFilter.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, CargoFilter.VALUE_WHITE);
        }

        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory != null) {
            this.logicInjectInventoryUpdater.accept(inventory, locationData);
        }
    }
}
