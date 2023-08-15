package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.*;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LocationTransferInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 41, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];

    private final int[] contentSlot = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
    private final int[][] lineSlots = new int[][] {
            {0, 1, 2, 3, 4, 5, 6, 7, 8},
            {9, 10, 11, 12, 13, 14, 15, 16, 17},
            {18, 19, 20, 21, 22, 23, 24, 25, 26}
    };

    private final int cargoModeSlot = 42;
    private final int cargoOrderSlot = 43;

    private final int line1Slot = 51;
    private final int line2Slot = 52;
    private final int line3Slot = 53;

    public final int locationRecorderSlot = 40;

    public LocationTransferInventory(@Nonnull SlimefunItem slimefunItem) {
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

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case cargoModeSlot -> CargoMode.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoOrderSlot -> CargoOrder.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case line1Slot -> SlotSearchLine.L1_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case line2Slot -> SlotSearchLine.L2_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case line3Slot -> SlotSearchLine.L3_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.cargoModeSlot, CargoMode.OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoOrderSlot, CargoOrder.OPTION.defaultIcon());

        this.defaultItemStack.put(this.line1Slot, SlotSearchLine.L1_ICON);
        this.defaultItemStack.put(this.line2Slot, SlotSearchLine.L2_ICON);
        this.defaultItemStack.put(this.line3Slot, SlotSearchLine.L3_ICON);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return this.contentSlot;
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return this.contentSlot;
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if (locationData != null) {
            int[] result = new int[0];
            int[] lines;
            if(requestType == RequestType.INPUT) {
                lines = SlotSearchLine.getLines(FinalTech.getLocationDataService(), locationData, SlotSearchLine.VALUE_INPUT, SlotSearchLine.VALUE_INPUT_AND_OUTPUT);
            } else if(requestType == RequestType.OUTPUT) {
                lines = SlotSearchLine.getLines(FinalTech.getLocationDataService(), locationData, SlotSearchLine.VALUE_OUTPUT, SlotSearchLine.VALUE_INPUT_AND_OUTPUT);
            } else {
                return result;
            }
            if(lines.length > 0) {
                result = lineSlots[lines[0]];
                for(int i = 1; i < lines.length; i++) {
                    result = JavaUtil.merge(result, lineSlots[lines[i]]);
                }
            }
            return result;
        }
        return new int[0];
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        CargoMode.OPTION.checkAndUpdateIcon(inventory, this.cargoModeSlot, FinalTech.getLocationDataService(), location);
        CargoOrder.OPTION.checkAndUpdateIcon(inventory, this.cargoOrderSlot, FinalTech.getLocationDataService(), location);

        SlotSearchLine.L1_OPTION.checkAndUpdateIcon(inventory, this.line1Slot, FinalTech.getLocationDataService(), location);
        SlotSearchLine.L2_OPTION.checkAndUpdateIcon(inventory, this.line2Slot, FinalTech.getLocationDataService(), location);
        SlotSearchLine.L3_OPTION.checkAndUpdateIcon(inventory, this.line3Slot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoOrder(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoOrder.OPTION.checkAndUpdateIcon(inventory, this.cargoOrderSlot, FinalTech.getLocationDataService(), locationData);
    }
}
