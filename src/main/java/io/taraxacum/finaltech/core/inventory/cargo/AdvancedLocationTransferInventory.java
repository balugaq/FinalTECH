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

public class AdvancedLocationTransferInventory extends LocationTransferInventory {
    private final int[] border = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35, 39, 41, 44, 48, 49, 50, 51, 52, 53};

    private final int cargoNumberSubSlot = 36;
    private final int cargoNumberSlot = 37;
    private final int cargoNumberAddSlot = 38;
    private final int slotSearchSizeSlot = 45;
    private final int slotSearchOrderSlot = 46;
    private final int cargoLimitSlot = 47;

    public AdvancedLocationTransferInventory(@Nonnull SlimefunItem slimefunItem) {
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
            case cargoNumberSubSlot -> CargoNumber.OPTION.getPreviousHandler(FinalTech.getLocationDataService(), location, cargoNumberSlot, this.slimefunItem);
            case cargoNumberAddSlot -> CargoNumber.OPTION.getNextHandler(FinalTech.getLocationDataService(), location, cargoNumberSlot, this.slimefunItem);
            case slotSearchSizeSlot -> SlotSearchSize.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case slotSearchOrderSlot -> SlotSearchOrder.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoLimitSlot -> CargoLimit.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        super.initSelf();

        this.defaultItemStack.put(this.cargoNumberSubSlot, CargoNumber.CARGO_NUMBER_SUB_ICON);
        this.defaultItemStack.put(this.cargoNumberSlot, CargoNumber.CARGO_NUMBER_ICON);
        this.defaultItemStack.put(this.cargoNumberAddSlot, CargoNumber.CARGO_NUMBER_ADD_ICON);

        this.defaultItemStack.put(this.slotSearchSizeSlot, SlotSearchSize.OPTION.defaultIcon());
        this.defaultItemStack.put(this.slotSearchOrderSlot, SlotSearchOrder.OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoLimitSlot, CargoLimit.OPTION.defaultIcon());
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), location);

        SlotSearchSize.OPTION.checkAndUpdateIcon(inventory, this.slotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.OPTION.checkAndUpdateIcon(inventory, this.slotSearchOrderSlot, FinalTech.getLocationDataService(), location);
        CargoLimit.OPTION.checkAndUpdateIcon(inventory, this.cargoLimitSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoNumber(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoNumber.OPTION.checkAndUpdateIcon(inventory, this.cargoNumberSlot, FinalTech.getLocationDataService(), locationData);
    }
}
