package io.taraxacum.finaltech.core.item.machine.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.dto.SimpleCargoDTO;
import io.taraxacum.finaltech.core.interfaces.DigitInjectableItem;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.cargo.AdvancedLineTransferInventory;
import io.taraxacum.finaltech.core.option.*;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.*;
import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public class AdvancedLineTransfer extends AbstractCargo implements RecipeItem, DigitInjectableItem, LogicInjectableItem {
    private final int particleInterval = 2;
    private int[] itemMatch;
    private BiConsumer<Inventory, LocationData> logicInjectInventoryUpdater;
    private BiConsumer<Inventory, LocationData> digitInjectInventoryUpdater;

    public AdvancedLineTransfer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        AdvancedLineTransferInventory advancedLineTransferInventory = new AdvancedLineTransferInventory(this);
        this.itemMatch = advancedLineTransferInventory.itemMatchSlot;
        this.logicInjectInventoryUpdater = advancedLineTransferInventory::updateCargoFilter;
        this.digitInjectInventoryUpdater = advancedLineTransferInventory::updateCargoNumber;
        return advancedLineTransferInventory;
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

                    BlockSearchMode.LINE_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchOrder.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    CargoOrder.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchCycle.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    BlockSearchSelf.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);

                    CargoNumber.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    CargoNumberMode.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    CargoMode.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    CargoFilter.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);

                    SlotSearchSize.INPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    SlotSearchOrder.INPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    CargoLimit.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    SlotSearchSize.OUTPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    SlotSearchOrder.OUTPUT_OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
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
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }
        Location location = block.getLocation();
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        boolean drawParticle = !inventory.getViewers().isEmpty() || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

        if (javaPlugin.getServer().isPrimaryThread()) {
            BlockData blockData = block.getState().getBlockData();
            if (!(blockData instanceof Directional directional)) {
                return;
            }

            BlockFace blockFace = directional.getFacing();
            List<Block> blockList = this.searchBlock(block, blockFace, BlockSearchMode.LINE_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

            if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(blockList))) {
                return;
            }

            switch (BlockSearchSelf.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) {
                case BlockSearchSelf.VALUE_START -> blockList.add(0, block);
                case BlockSearchSelf.VALUE_END -> blockList.add(block);
            }

            final List<Block> finalBlockList;
            switch (BlockSearchOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) {
                case BlockSearchOrder.VALUE_POSITIVE -> finalBlockList = blockList;
                case BlockSearchOrder.VALUE_REVERSE -> finalBlockList = JavaUtil.reserve(blockList);
                case BlockSearchOrder.VALUE_RANDOM -> finalBlockList = JavaUtil.shuffle(blockList);
                default -> finalBlockList = null;
            }
            if (finalBlockList == null) {
                return;
            }

            if (BlockSearchCycle.VALUE_TRUE.equals(BlockSearchCycle.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) && finalBlockList.size() > 1) {
                if(CargoOrder.VALUE_REVERSE.equals(CargoOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData))) {
                    finalBlockList.add(0, finalBlockList.get(finalBlockList.size() - 1));
                } else {
                    finalBlockList.add(finalBlockList.get(0));
                }
            }

            if (drawParticle && finalBlockList.size() > 0 && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.ELECTRIC_SPARK, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / finalBlockList.size(), finalBlockList));
            }

            int cargoNumber = Integer.parseInt(CargoNumber.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
            String cargoNumberMode = CargoNumberMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String cargoOrder = CargoOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String cargoMode = CargoMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String inputSize = SlotSearchSize.INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String inputOrder = SlotSearchOrder.INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String outputSize = SlotSearchSize.OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
            String outputOrder = SlotSearchOrder.OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

            int number;
            Block inputBlock;
            Block outputBlock;
            InvWithSlots inputMap;
            InvWithSlots outputMap;

            SimpleCargoDTO simpleCargoDTO = new SimpleCargoDTO();
            simpleCargoDTO.setLocationDataService(FinalTech.getLocationDataService());
            simpleCargoDTO.setInputSize(inputSize);
            simpleCargoDTO.setInputOrder(inputOrder);
            simpleCargoDTO.setOutputSize(outputSize);
            simpleCargoDTO.setOutputOrder(outputOrder);
            simpleCargoDTO.setCargoLimit(CargoLimit.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
            simpleCargoDTO.setCargoFilter(CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
            simpleCargoDTO.setFilterInv(inventory);
            simpleCargoDTO.setFilterSlots(this.itemMatch);

            for (int i = 0, size = finalBlockList.size(); i < size - 1; i++) {
                switch (cargoOrder) {
                    case CargoOrder.VALUE_POSITIVE -> {
                        inputBlock = finalBlockList.get(i);
                        outputBlock = finalBlockList.get((i + 1) % size);
                    }
                    case CargoOrder.VALUE_REVERSE -> {
                        inputBlock = finalBlockList.get((i + 1) % size);
                        outputBlock = finalBlockList.get(i);
                    }
                    default -> {
                        continue;
                    }
                }

                if (LocationUtil.isSameLocation(inputBlock.getLocation(), outputBlock.getLocation())) {
                    continue;
                }

                if (CargoMode.VALUE_INPUT_MAIN.equals(cargoMode) && FinalTech.getLocationDataService().getInventory(outputBlock.getLocation()) != null) {
                    outputMap = null;
                } else {
                    outputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), outputBlock, outputSize, outputOrder);
                }
                if (CargoMode.VALUE_OUTPUT_MAIN.equals(cargoMode) && FinalTech.getLocationDataService().getInventory(inputBlock.getLocation()) != null) {
                    inputMap = null;
                } else {
                    inputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), inputBlock, inputSize, inputOrder);
                }

                if(inputMap != null && outputMap != null) {
                    continue;
                }

                simpleCargoDTO.setInputBlock(inputBlock);
                simpleCargoDTO.setInputMap(inputMap);
                simpleCargoDTO.setOutputBlock(outputBlock);
                simpleCargoDTO.setOutputMap(outputMap);
                simpleCargoDTO.setCargoNumber(cargoNumber);

                number = CargoUtil.doSimpleCargo(simpleCargoDTO, cargoMode);

                if (CargoNumberMode.VALUE_UNIVERSAL.equals(cargoNumberMode)) {
                    cargoNumber -= number;
                    if(cargoNumber <= 0) {
                        break;
                    }
                }
            }
        } else {
            javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
                BlockData blockData = block.getState().getBlockData();
                if (!(blockData instanceof Directional directional)) {
                    return;
                }

                BlockFace blockFace = directional.getFacing();
                final List<Block> blockList = AdvancedLineTransfer.this.searchBlock(block, blockFace, BlockSearchMode.LINE_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
                if(blockList.isEmpty()) {
                    return;
                }

                List<Inventory> vanillaInventories = new ArrayList<>();
                for (Block b : blockList) {
                    vanillaInventories.add(CargoUtil.getVanillaInventory(b));
                }

                FinalTech.getLocationRunnableFactory().waitThenRun(() -> {
                    if (FinalTech.getLocationDataService().getLocationData(location) == null) {
                        return;
                    }

                    if (!PermissionUtil.checkOfflinePermission(FinalTech.getLocationDataService(), locationData, LocationUtil.transferToLocation(blockList))) {
                        return;
                    }

                    switch (BlockSearchSelf.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) {
                        case BlockSearchSelf.VALUE_START -> {
                            blockList.add(0, block);
                            vanillaInventories.add(0, null);
                        }
                        case BlockSearchSelf.VALUE_END -> {
                            blockList.add(block);
                            vanillaInventories.add(null);
                        }
                    }

                    final List<Block> finalBlockList;
                    final List<Inventory> finalVanillaInventories;
                    switch (BlockSearchOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) {
                        case BlockSearchOrder.VALUE_POSITIVE -> {
                            finalBlockList = blockList;
                            finalVanillaInventories = vanillaInventories;
                        }
                        case BlockSearchOrder.VALUE_REVERSE -> {
                            finalBlockList = JavaUtil.reserve(blockList);
                            finalVanillaInventories = JavaUtil.reserve(vanillaInventories);
                        }
                        case BlockSearchOrder.VALUE_RANDOM -> {
                            int[] key = JavaUtil.generateRandomInts(blockList.size());
                            finalBlockList = JavaUtil.shuffleByInts(blockList, key);
                            finalVanillaInventories = JavaUtil.shuffleByInts(vanillaInventories, key);
                        }
                        default -> {
                            finalBlockList = null;
                            finalVanillaInventories = null;
                        }
                    }
                    if (finalBlockList == null) {
                        return;
                    }

                    if (BlockSearchCycle.VALUE_TRUE.equals(BlockSearchCycle.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)) && finalBlockList.size() > 1) {
                        if(CargoOrder.VALUE_REVERSE.equals(CargoOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData))) {
                            finalBlockList.add(0, finalBlockList.get(finalBlockList.size() - 1));
                        } else {
                            finalBlockList.add(finalBlockList.get(0));
                        }
                    }

                    if (drawParticle && finalBlockList.size() > 0 && FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
                        javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.ELECTRIC_SPARK, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L / finalBlockList.size(), finalBlockList));
                    }

                    int cargoNumber = Integer.parseInt(CargoNumber.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
                    String cargoNumberMode = CargoNumberMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String cargoOrder = CargoOrder.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String cargoMode = CargoMode.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String inputSize = SlotSearchSize.INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String inputOrder = SlotSearchOrder.INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String outputSize = SlotSearchSize.OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                    String outputOrder = SlotSearchOrder.OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);

                    int number;
                    Block inputBlock;
                    Block outputBlock;
                    InvWithSlots inputMap;
                    InvWithSlots outputMap;

                    SimpleCargoDTO simpleCargoDTO = new SimpleCargoDTO();
                    simpleCargoDTO.setLocationDataService(FinalTech.getLocationDataService());
                    simpleCargoDTO.setInputSize(inputSize);
                    simpleCargoDTO.setInputOrder(inputOrder);
                    simpleCargoDTO.setOutputSize(outputSize);
                    simpleCargoDTO.setOutputOrder(outputOrder);
                    simpleCargoDTO.setCargoLimit(CargoLimit.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
                    simpleCargoDTO.setCargoFilter(CargoFilter.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
                    simpleCargoDTO.setFilterInv(inventory);
                    simpleCargoDTO.setFilterSlots(this.itemMatch);

                    int input;
                    int output;
                    for (int i = 0, size = finalBlockList.size(); i < size - 1; i++) {
                        switch (cargoOrder) {
                            case CargoOrder.VALUE_POSITIVE -> {
                                input = i;
                                output = (i + 1) % size;
                            }
                            case CargoOrder.VALUE_REVERSE -> {
                                input = (i + 1) % size;
                                output = i;
                            }
                            default -> {
                                continue;
                            }
                        }

                        inputBlock = finalBlockList.get(input);
                        outputBlock = finalBlockList.get(output);
                        if (LocationUtil.isSameLocation(inputBlock.getLocation(), outputBlock.getLocation())) {
                            continue;
                        }

                        Inventory inputInventory = FinalTech.getLocationDataService().getInventory(inputBlock.getLocation());
                        if (CargoMode.VALUE_OUTPUT_MAIN.equals(cargoMode) && inputInventory != null) {
                            inputMap = null;
                        } else if (inputInventory != null) {
                            inputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), inputBlock, inputSize, inputOrder);
                        } else if (finalVanillaInventories.get(input) != null) {
                            inputMap = CargoUtil.calInvWithSlots(finalVanillaInventories.get(input), inputOrder);
                        } else {
                            continue;
                        }

                        Inventory outputInventory = FinalTech.getLocationDataService().getInventory(outputBlock.getLocation());
                        if (CargoMode.VALUE_INPUT_MAIN.equals(cargoMode) && outputInventory != null) {
                            outputMap = null;
                        } else if (outputInventory != null) {
                            outputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), outputBlock, outputSize, outputOrder);
                        } else if (finalVanillaInventories.get(output) != null) {
                            outputMap = CargoUtil.calInvWithSlots(finalVanillaInventories.get(output), outputOrder);
                        } else {
                            continue;
                        }

                        if(inputMap == null && outputMap == null) {
                            continue;
                        }

                        simpleCargoDTO.setInputBlock(inputBlock);
                        simpleCargoDTO.setInputMap(inputMap);
                        simpleCargoDTO.setOutputBlock(outputBlock);
                        simpleCargoDTO.setOutputMap(outputMap);
                        simpleCargoDTO.setCargoNumber(cargoNumber);

                        number = CargoUtil.doSimpleCargo(simpleCargoDTO, cargoMode);

                        if (CargoNumberMode.VALUE_UNIVERSAL.equals(cargoNumberMode)) {
                            cargoNumber -= number;
                            if(cargoNumber <= 0) {
                                break;
                            }
                        }
                    }
                }, LocationUtil.transferToLocation(blockList));
            });
        }
    }

    @Nonnull
    public List<Block> searchBlock(@Nonnull Block begin, @Nonnull BlockFace blockFace, @Nonnull String blockSearchMode) {
        List<Block> list = new ArrayList<>();
        Block block = begin.getRelative(blockFace);
        if (!block.getChunk().isLoaded()) {
            return new ArrayList<>();
        }
        if (BlockSearchMode.VALUE_ZERO.equals(blockSearchMode)) {
            if (CargoUtil.hasInventory(FinalTech.getLocationDataService(), block.getLocation())) {
                list.add(block);
            }
            block = block.getRelative(blockFace);
            if (!block.getChunk().isLoaded()) {
                return new ArrayList<>();
            }
            if (CargoUtil.hasInventory(FinalTech.getLocationDataService(), block.getLocation())) {
                list.add(block);
            }
            return list;
        }
        while (CargoUtil.hasInventory(FinalTech.getLocationDataService(), block.getLocation())) {
            if (FinalTech.getLocationDataService().getLocationData(block.getLocation()) instanceof SlimefunLocationData slimefunLocationData
                    && this.getId().equals(slimefunLocationData.getId())) {
                if (BlockSearchMode.VALUE_PENETRATE.equals(blockSearchMode)) {
                    block = block.getRelative(blockFace);
                    continue;
                } else if (BlockSearchMode.VALUE_INTERRUPT.equals(blockSearchMode)) {
                    list.add(block);
                    break;
                }
            }
            list.add(block);
            block = block.getRelative(blockFace);
            if (!block.getChunk().isLoaded()) {
                return new ArrayList<>();
            }
        }
        return list;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    @Override
    public void injectDigit(@Nonnull LocationData locationData, int digit) {
        CargoNumber.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, String.valueOf(digit));

        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory != null) {
            this.digitInjectInventoryUpdater.accept(inventory, locationData);
        }
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
