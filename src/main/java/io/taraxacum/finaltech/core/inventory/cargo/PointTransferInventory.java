package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.BlockSearchMode;
import io.taraxacum.finaltech.core.option.CargoFilter;
import io.taraxacum.finaltech.core.option.CargoMode;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PointTransferInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 14, 15, 16, 17, 21, 22, 23, 27, 28, 29, 33, 34, 35, 36, 37, 38, 42, 43, 44, 45, 46, 47, 51, 52, 53};
    private final int[] inputBorder = new int[] {18, 20};
    private final int[] outputBorder = new int[] {24, 26};
    private final int[] inputSlot = new int[] {30, 31, 32, 39, 40, 41, 48, 49, 50};
    private final int[] outputSlot = new int[] {30, 31, 32, 39, 40, 41, 48, 49, 50};

    private final int cargoFilterSlot = 12;
    private final int cargoModeSlot = 13;

    private final int inputBlockSearchModeSlot = 19;

    private final int outputBlockSearchModeSlot = 25;

    public final int[] itemMatchSlot = new int[] {30, 31, 32, 39, 40, 41, 48, 49, 50};

    public PointTransferInventory(SlimefunItem slimefunItem) {
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
            case cargoFilterSlot -> CargoFilter.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoModeSlot -> CargoMode.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case inputBlockSearchModeSlot -> BlockSearchMode.POINT_INPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case outputBlockSearchModeSlot -> BlockSearchMode.POINT_OUTPUT_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.cargoFilterSlot, CargoFilter.OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoModeSlot, CargoMode.OPTION.defaultIcon());
        this.defaultItemStack.put(this.inputBlockSearchModeSlot, BlockSearchMode.POINT_INPUT_OPTION.defaultIcon());
        this.defaultItemStack.put(this.outputBlockSearchModeSlot, BlockSearchMode.POINT_OUTPUT_OPTION.defaultIcon());
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
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
        CargoMode.OPTION.checkAndUpdateIcon(inventory, this.cargoModeSlot, FinalTech.getLocationDataService(), location);
        BlockSearchMode.POINT_INPUT_OPTION.checkAndUpdateIcon(inventory, this.inputBlockSearchModeSlot, FinalTech.getLocationDataService(), location);
        BlockSearchMode.POINT_OUTPUT_OPTION.checkAndUpdateIcon(inventory, this.outputBlockSearchModeSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoFilter(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoFilter.OPTION.checkAndUpdateIcon(inventory, this.cargoFilterSlot, FinalTech.getLocationDataService(), locationData);
    }
}
