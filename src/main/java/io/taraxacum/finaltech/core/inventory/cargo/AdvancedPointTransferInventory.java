package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.option.*;
import io.taraxacum.libs.plugin.ld.LocationData;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AdvancedPointTransferInventory extends PointTransferInventory {
    private final int[] border = new int[] {21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private final int[] inputBorder = new int[] {0, 2, 9, 11, 18, 20};
    private final int[] outputBorder = new int[] {6, 8, 15, 17, 24, 26};

    private final int cargoNumberSubSlot = 3;
    private final int cargoNumberSlot = 4;
    private final int cargoNumberAddSlot = 5;
    private final int cargoLimitSlot = 14;

    private final int inputSlotSearchSizeSlot = 1;
    private final int inputSlotSearchOrderSlot = 10;

    private final int outputSlotSearchSizeSlot = 7;
    private final int outputSlotSearchOrderSlot = 16;

    public AdvancedPointTransferInventory(SlimefunItem slimefunItem) {
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
    @Nullable
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case cargoNumberSubSlot -> CargoNumber.OPTION.getPreviousHandler(FinalTech.getLocationDataService(), location, this.cargoNumberSlot, this.slimefunItem);
            case cargoNumberAddSlot -> CargoNumber.OPTION.getNextHandler(FinalTech.getLocationDataService(), location, this.cargoNumberSlot, this.slimefunItem);
            case cargoLimitSlot -> CargoLimit.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputSlotSearchSizeSlot -> SlotSearchSize.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputSlotSearchOrderSlot -> SlotSearchOrder.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputSlotSearchSizeSlot -> SlotSearchSize.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputSlotSearchOrderSlot -> SlotSearchOrder.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        super.initSelf();

        this.defaultItemStack.put(this.cargoNumberSubSlot, CargoNumber.CARGO_NUMBER_SUB_ICON);
        this.defaultItemStack.put(this.cargoNumberSlot, CargoNumber.CARGO_NUMBER_ICON);
        this.defaultItemStack.put(this.cargoNumberAddSlot, CargoNumber.CARGO_NUMBER_ADD_ICON);
        this.defaultItemStack.put(this.cargoLimitSlot, CargoLimit.OPTION.defaultIcon());

        this.defaultItemStack.put(this.inputSlotSearchSizeSlot, SlotSearchSize.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputSlotSearchOrderSlot, SlotSearchOrder.INPUT_OPTION.defaultIcon());

        this.defaultItemStack.put(this.outputSlotSearchSizeSlot, SlotSearchSize.OUTPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputSlotSearchOrderSlot, SlotSearchOrder.OUTPUT_OPTION.defaultIcon());
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), location);
        CargoLimit.OPTION.checkAndUpdateIcon(inventory, this.cargoLimitSlot, FinalTech.getLocationDataService(), location);

        SlotSearchSize.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);

        SlotSearchSize.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoNumber(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), locationData);
    }
}
