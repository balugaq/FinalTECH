package io.taraxacum.finaltech.core.item.machine.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.cargo.PointTransferInventory;
import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.dto.CargoDTO;
import io.taraxacum.finaltech.core.dto.SimpleCargoDTO;
import io.taraxacum.finaltech.core.option.*;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.CargoUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public class PointTransfer extends AbstractCargo implements RecipeItem, LogicInjectableItem {
    private final double particleDistance = 0.25;
    private final int particleInterval = 2;
    private final int range = ConfigUtil.getOrDefaultItemSetting(8, this, "range");
    private int[] matchSlot;
    private BiConsumer<Inventory, LocationData> logicInjectInventoryUpdater;

    public PointTransfer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        PointTransferInventory pointTransferInventory = new PointTransferInventory(this);
        this.matchSlot = pointTransferInventory.itemMatchSlot;
        this.logicInjectInventoryUpdater = pointTransferInventory::updateCargoFilter;
        return pointTransferInventory;
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
                    CargoMode.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);

                    BlockSearchMode.POINT_INPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchMode.POINT_OUTPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                }
            }
        };
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.matchSlot);
    }

    @Override
    public void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData)  {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        boolean drawParticle = !inventory.getViewers().isEmpty() || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

        if (javaPlugin.getServer().isPrimaryThread()) {
            BlockData blockData = block.getState().getBlockData();
            if (!(blockData instanceof Directional directional)) {
                return;
            }
            BlockFace blockFace = directional.getFacing();
            Block inputBlock = this.searchBlock(block, BlockSearchMode.POINT_INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), blockFace.getOppositeFace(), true, drawParticle);
            Block outputBlock = this.searchBlock(block, BlockSearchMode.POINT_OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), blockFace, false, drawParticle);

            if (inputBlock == null || outputBlock == null || LocationUtil.isSameLocation(inputBlock.getLocation(), (outputBlock.getLocation()))) {
                return;
            }

            if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(inputBlock, outputBlock))) {
                return;
            }

            if (drawParticle) {
                javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, inputBlock, outputBlock), Slimefun.getTickerTask().getTickRate());
            }

            String inputSlotSearchSize = SlotSearchSize.INPUT_OPTION.defaultValue();
            String inputSlotSearchOrder = SlotSearchOrder.INPUT_OPTION.defaultValue();

            String outputSlotSearchSize = SlotSearchSize.OUTPUT_OPTION.defaultValue();
            String outputSlotSearchOrder = SlotSearchOrder.OUTPUT_OPTION.defaultValue();

            int cargoNumber = Integer.parseInt(CargoNumber.OPTION.defaultValue());
            String cargoFilter = CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String cargoMode = CargoMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String cargoLimit = CargoLimit.OPTION.defaultValue();

            CargoUtil.doCargo(new CargoDTO(javaPlugin, FinalTech.getLocationDataService(), inputBlock, inputSlotSearchSize, inputSlotSearchOrder, outputBlock, outputSlotSearchSize, outputSlotSearchOrder, cargoNumber, cargoLimit, cargoFilter, inventory, this.matchSlot), cargoMode);
        } else {
            javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
                BlockData blockData = block.getState().getBlockData();
                if (!(blockData instanceof Directional directional)) {
                    return;
                }
                BlockFace blockFace = directional.getFacing();
                Block inputBlock = PointTransfer.this.searchBlock(block, BlockSearchMode.POINT_INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), blockFace.getOppositeFace(), true, drawParticle);
                Block outputBlock = PointTransfer.this.searchBlock(block, BlockSearchMode.POINT_OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), blockFace, false, drawParticle);

                if (inputBlock == null || outputBlock == null || LocationUtil.isSameLocation(inputBlock.getLocation(), (outputBlock.getLocation()))) {
                    return;
                }

                Inventory inputInventory = CargoUtil.getVanillaInventory(inputBlock);
                Inventory outputInventory = CargoUtil.getVanillaInventory(outputBlock);

                FinalTech.getLocationRunnableFactory().waitThenRun(() -> {
                    if (FinalTech.getLocationDataService().getLocationData(block.getLocation()) == null) {
                        return;
                    }

                    if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(inputBlock, outputBlock))) {
                        return;
                    }

                    String inputSize = SlotSearchSize.INPUT_OPTION.defaultValue();
                    String inputOrder = SlotSearchOrder.INPUT_OPTION.defaultValue();

                    String outputSize = SlotSearchSize.OUTPUT_OPTION.defaultValue();
                    String outputOrder = SlotSearchOrder.OUTPUT_OPTION.defaultValue();

                    String cargoMode = CargoMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

                    InvWithSlots inputMap;
                    if (FinalTech.getLocationDataService().getRawInventory(inputBlock.getLocation()) != null) {
                        if (CargoMode.VALUE_OUTPUT_MAIN.equals(cargoMode)) {
                            inputMap = null;
                        } else {
                            inputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), inputBlock, inputSize, inputOrder);
                        }
                    } else if (inputInventory != null) {
                        inputMap = CargoUtil.calInvWithSlots(inputInventory, inputOrder);
                    } else {
                        return;
                    }

                    InvWithSlots outputMap;
                    if (FinalTech.getLocationDataService().getRawInventory(outputBlock.getLocation()) != null) {
                        if (CargoMode.VALUE_INPUT_MAIN.equals(cargoMode)) {
                            outputMap = null;
                        } else {
                            outputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), outputBlock, outputSize, outputOrder);
                        }
                    } else if (outputInventory != null) {
                        outputMap = CargoUtil.calInvWithSlots(outputInventory, outputOrder);
                    } else {
                        return;
                    }

                    if (drawParticle) {
                        javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, inputBlock, outputBlock), Slimefun.getTickerTask().getTickRate());
                    }

                    int cargoNumber = Integer.parseInt(CargoNumber.OPTION.defaultValue());
                    String cargoFilter = CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String cargoLimit = CargoLimit.OPTION.defaultValue();

                    CargoUtil.doSimpleCargo(new SimpleCargoDTO(FinalTech.getLocationDataService(), inputMap, inputBlock, inputSize, inputOrder, outputMap, outputBlock, outputSize, outputOrder, cargoNumber, cargoLimit, cargoFilter, inventory, this.matchSlot), cargoMode);
                }, inputBlock.getLocation(), outputBlock.getLocation());
            });
        }
    }

    @Nullable
    private Block searchBlock(@Nonnull Block begin, @Nonnull String searchMode, @Nonnull BlockFace blockFace, boolean input, boolean drawParticle) {
        List<Location> particleLocationList = new ArrayList<>();
        particleLocationList.add(LocationUtil.getCenterLocation(begin));
        Block result = begin.getRelative(blockFace);
        if(!result.getChunk().isLoaded()) {
            return null;
        }
        int count = 1;
        if (BlockSearchMode.VALUE_ZERO.equals(searchMode)) {
            particleLocationList.add(LocationUtil.getCenterLocation(result));
            if (drawParticle && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.CRIT_MAGIC, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / particleLocationList.size(), this.particleDistance, input ? JavaUtil.reserve(particleLocationList) : particleLocationList));
            }
            return result;
        }
        Set<Location> locationSet = new HashSet<>();
        locationSet.add(begin.getLocation());
        while(true) {
            if(!result.getChunk().isLoaded()) {
                return null;
            }
            if (FinalTech.getLocationDataService().getRawInventory(result.getLocation()) != null && !result.getType().equals(FinalTechItemStacks.POINT_TRANSFER.getType())) {
                particleLocationList.add(LocationUtil.getCenterLocation(result));
                break;
            }
            if (PaperLib.getBlockState(result, false).getState() instanceof InventoryHolder) {
                particleLocationList.add(LocationUtil.getCenterLocation(result));
                break;
            }
            if (result.getType() == FinalTechItemStacks.POINT_TRANSFER.getType()) {
                particleLocationList.add(LocationUtil.getCenterLocation(result));
                count = 0;
                if (locationSet.contains(result.getLocation())) {
                    break;
                }
                locationSet.add(result.getLocation());
                if (BlockSearchMode.VALUE_INHERIT.equals(searchMode)) {
                    BlockData blockData = result.getState().getBlockData();
                    if (blockData instanceof Directional) {
                        blockFace = ((Directional) blockData).getFacing();
                        if (input) {
                            blockFace = blockFace.getOppositeFace();
                        }
                    }
                }
            }
            result = result.getRelative(blockFace);
            if(!result.getChunk().isLoaded()) {
                return null;
            }
            if (count++ > this.range) {
                particleLocationList.add(LocationUtil.getCenterLocation(result));
                break;
            }
        }
        if (drawParticle && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.CRIT_MAGIC, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / particleLocationList.size(), this.particleDistance, input ? JavaUtil.reserve(particleLocationList) : particleLocationList));
        }
        return result;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.range));
    }

    @Override
    public void injectLogic(@Nonnull LocationData locationData, boolean logic) {
        if (logic) {
            CargoFilter.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, CargoFilter.VALUE_BLACK);
        } else {
            CargoFilter.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, CargoFilter.VALUE_WHITE);
        }

        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null) {
            this.logicInjectInventoryUpdater.accept(inventory, locationData);
        }
    }
}
