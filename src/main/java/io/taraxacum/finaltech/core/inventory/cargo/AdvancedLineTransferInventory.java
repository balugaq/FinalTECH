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

public class AdvancedLineTransferInventory extends LineTransferInventory {
    private final int[] border = new int[] {5, 14, 23};

    private final int cargoNumberSubSlot = 9;
    private final int cargoNumberSlot = 10;
    private final int cargoNumberAddSlot = 11;

    private final int inputSlotSearchSizeSlot = 18;
    private final int inputSlotSearchOrderSlot = 19;
    private final int cargoLimitSlot = 20;
    private final int outputSlotSearchSizeSlot = 21;
    private final int outputSlotSearchOrderSlot = 22;

    public AdvancedLineTransferInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case cargoNumberSubSlot -> CargoNumber.OPTION.getPreviousHandler(FinalTech.getLocationDataService(), location, this.cargoNumberSlot, this.slimefunItem);
            case cargoNumberSlot -> CargoNumberMode.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoNumberAddSlot -> CargoNumber.OPTION.getNextHandler(FinalTech.getLocationDataService(), location, this.cargoNumberSlot, this.slimefunItem);
            case inputSlotSearchSizeSlot -> SlotSearchSize.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputSlotSearchOrderSlot -> SlotSearchOrder.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoLimitSlot -> CargoLimit.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
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

        this.defaultItemStack.put(this.inputSlotSearchSizeSlot, SlotSearchSize.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputSlotSearchOrderSlot, SlotSearchOrder.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoLimitSlot, CargoLimit.OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputSlotSearchSizeSlot, SlotSearchSize.OUTPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputSlotSearchOrderSlot, SlotSearchOrder.OUTPUT_OPTION.defaultIcon());
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), location);
        CargoNumberMode.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), location);

        SlotSearchSize.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);
        CargoLimit.OPTION.checkAndUpdateIcon(inventory, this.cargoLimitSlot, FinalTech.getLocationDataService(), location);
        SlotSearchSize.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoNumber(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), locationData);
    }
}
