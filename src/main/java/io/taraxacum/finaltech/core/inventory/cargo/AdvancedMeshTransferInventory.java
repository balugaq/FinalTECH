package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.option.*;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class AdvancedMeshTransferInventory extends MeshTransferInventory {
    private final int[] border = new int[0];

    private final int inputCargoNumberSubSlot = 3;
    private final int inputCargoNumberModeSlot = 12;
    private final int inputCargoNumberAddSlot = 21;
    private final int inputSlotSearchSizeSlot = 4;
    private final int inputSlotSearchOrderSlot = 13;
    private final int inputCargoLimitSlot = 22;

    private final int outputCargoNumberSubSlot = 30;
    private final int outputCargoNumberModeSlot = 39;
    private final int outputCargoNumberAddSlot = 48;
    private final int outputSlotSearchSizeSlot = 31;
    private final int outputSlotSearchOrderSlot = 40;
    private final int outputCargoLimitSlot = 49;

    public AdvancedMeshTransferInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case inputCargoNumberSubSlot -> CargoNumber.INPUT_OPTION.getPreviousHandler(FinalTech.getLocationDataService(), location, this.inputCargoNumberModeSlot, this.slimefunItem);
            case inputCargoNumberModeSlot -> CargoNumberMode.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputCargoNumberAddSlot -> CargoNumber.INPUT_OPTION.getNextHandler(FinalTech.getLocationDataService(), location, this.inputCargoNumberModeSlot, this.slimefunItem);
            case inputSlotSearchSizeSlot -> SlotSearchSize.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputSlotSearchOrderSlot -> SlotSearchOrder.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputCargoLimitSlot -> CargoLimit.INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputCargoNumberSubSlot -> CargoNumber.OUTPUT_OPTION.getPreviousHandler(FinalTech.getLocationDataService(), location, outputCargoNumberModeSlot, this.slimefunItem);
            case outputCargoNumberModeSlot -> CargoNumberMode.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputCargoNumberAddSlot -> CargoNumber.OUTPUT_OPTION.getNextHandler(FinalTech.getLocationDataService(), location, outputCargoNumberModeSlot, this.slimefunItem);
            case outputSlotSearchSizeSlot -> SlotSearchSize.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputSlotSearchOrderSlot -> SlotSearchOrder.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputCargoLimitSlot -> CargoLimit.OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        super.initSelf();

        this.defaultItemStack.put(this.inputCargoNumberSubSlot, CargoNumber.CARGO_NUMBER_SUB_ICON);
        this.defaultItemStack.put(this.inputCargoNumberModeSlot, CargoNumber.CARGO_NUMBER_ICON);
        this.defaultItemStack.put(this.inputCargoNumberAddSlot, CargoNumber.CARGO_NUMBER_ADD_ICON);
        this.defaultItemStack.put(this.inputSlotSearchSizeSlot, SlotSearchSize.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputSlotSearchOrderSlot, SlotSearchOrder.INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputCargoLimitSlot, CargoLimit.INPUT_OPTION.defaultIcon());

        this.defaultItemStack.put(this.outputCargoNumberSubSlot, CargoNumber.CARGO_NUMBER_SUB_ICON);
        this.defaultItemStack.put(this.outputCargoNumberModeSlot, CargoNumber.CARGO_NUMBER_ICON);
        this.defaultItemStack.put(this.outputCargoNumberAddSlot, CargoNumber.CARGO_NUMBER_ADD_ICON);
        this.defaultItemStack.put(this.outputSlotSearchSizeSlot, SlotSearchSize.OUTPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputSlotSearchOrderSlot, SlotSearchOrder.OUTPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputCargoLimitSlot, CargoLimit.OUTPUT_OPTION.defaultIcon());
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        super.updateInventory(inventory, location);

        CargoNumber.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputCargoNumberModeSlot, FinalTech.getLocationDataService(), location);
        CargoNumberMode.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputCargoNumberModeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchSize.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);
        CargoLimit.INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputCargoLimitSlot, FinalTech.getLocationDataService(), location);

        CargoNumber.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputCargoNumberModeSlot, FinalTech.getLocationDataService(), location);
        CargoNumberMode.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputCargoNumberModeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchSize.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchSizeSlot, FinalTech.getLocationDataService(), location);
        SlotSearchOrder.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputSlotSearchOrderSlot, FinalTech.getLocationDataService(), location);
        CargoLimit.OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputCargoLimitSlot, FinalTech.getLocationDataService(), location);
    }
}
