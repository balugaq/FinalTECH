package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.BlockSearchMode;
import io.taraxacum.finaltech.core.option.CargoFilter;
import io.taraxacum.finaltech.core.option.PositionInfo;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class MeshTransferInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {3, 4, 12, 13, 21, 22, 30, 31, 39, 40, 48, 49};
    private final int[] inputBorder = new int[] {5, 14, 23};
    private final int[] outputBorder = new int[] {32, 41, 50};
    private final int[] inputSlot = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};
    private final int[] outputSlot = new int[] {33, 34, 35, 42, 43, 44, 51, 52, 53};

    private final int cargoFilterSlot = 10;
    private final int inputBlockSearchModeSlot = 2;
    private final int outputBlockSearchModeSlot = 20;

    private final int positionNorthSlot = 1;
    private final int positionEastSlot = 11;
    private final int positionSouthSlot = 19;
    private final int positionWestSlot = 9;
    private final int positionUpSlot = 0;
    private final int positionDownSlot = 18;

    public final int[] itemMatchSlot = new int[] {27, 28, 29, 36, 37, 38, 45, 46, 47};

    public MeshTransferInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return this.inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return this.outputBorder;
    }

    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case cargoFilterSlot -> CargoFilter.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputBlockSearchModeSlot -> BlockSearchMode.MESH_INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputBlockSearchModeSlot -> BlockSearchMode.MESH_OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionNorthSlot -> PositionInfo.NORTH_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionEastSlot -> PositionInfo.EAST_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionSouthSlot -> PositionInfo.SOUTH_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionWestSlot -> PositionInfo.WEST_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionUpSlot -> PositionInfo.UP_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case positionDownSlot -> PositionInfo.DOWN_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.cargoFilterSlot, CargoFilter.OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputBlockSearchModeSlot, BlockSearchMode.MESH_INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputBlockSearchModeSlot, BlockSearchMode.MESH_OUTPUT_OPTION.defaultIcon());

        this.defaultItemStack.put(this.positionNorthSlot, PositionInfo.NORTH_ICON);
        this.defaultItemStack.put(this.positionEastSlot, PositionInfo.EAST_ICON);
        this.defaultItemStack.put(this.positionSouthSlot, PositionInfo.SOUTH_ICON);
        this.defaultItemStack.put(this.positionWestSlot, PositionInfo.WEST_ICON);
        this.defaultItemStack.put(this.positionUpSlot, PositionInfo.UP_ICON);
        this.defaultItemStack.put(this.positionDownSlot, PositionInfo.DOWN_ICON);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return JavaUtil.merge(this.inputSlot, this.outputSlot);
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return switch (requestType) {
            case INPUT -> this.inputSlot;
            case OUTPUT -> this.outputSlot;
            default -> this.requestSlots();
        };
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        CargoFilter.OPTION.checkAndUpdateIcon(inventory, this.cargoFilterSlot, FinalTech.getLocationDataService(), location);
        BlockSearchMode.MESH_INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputBlockSearchModeSlot, FinalTech.getLocationDataService(), location);
        BlockSearchMode.MESH_OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputBlockSearchModeSlot, FinalTech.getLocationDataService(), location);

        PositionInfo.NORTH_OPTION.checkAndUpdateIcon(inventory, this.positionNorthSlot, FinalTech.getLocationDataService(), location);
        PositionInfo.EAST_OPTION.checkAndUpdateIcon(inventory, this.positionEastSlot, FinalTech.getLocationDataService(), location);
        PositionInfo.SOUTH_OPTION.checkAndUpdateIcon(inventory, this.positionSouthSlot, FinalTech.getLocationDataService(), location);
        PositionInfo.WEST_OPTION.checkAndUpdateIcon(inventory, this.positionWestSlot, FinalTech.getLocationDataService(), location);
        PositionInfo.UP_OPTION.checkAndUpdateIcon(inventory, this.positionUpSlot, FinalTech.getLocationDataService(), location);
        PositionInfo.DOWN_OPTION.checkAndUpdateIcon(inventory, this.positionDownSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoFilter(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoFilter.OPTION.checkAndUpdateIcon(inventory, this.cargoFilterSlot, FinalTech.getLocationDataService(), locationData);
    }
}
