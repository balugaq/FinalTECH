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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LineTransferInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {5, 9, 10, 11, 14, 18, 19, 20, 21, 22, 23};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int[] inputSlot = new int[] {36, 37, 38, 39, 40, 41, 42, 43, 44};
    private final int[] outputSlot = new int[] {45, 46, 47, 48, 49, 50, 51, 52, 53};
    private final int[] specialBorder = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35};

    private final int blockSearchModeSlot = 0;
    private final int blockSearchOrderSlot = 1;
    private final int cargoOrderSlot = 2;
    private final int blockSearchCycleSlot = 3;
    private final int blockSearchSelfSlot = 4;

    private final int cargoModeSlot = 12;
    private final int cargoFilterSlot = 13;

    public final int[] itemMatchSlot = new int[] {6, 7, 8, 15, 16, 17, 24, 25, 26};

    public LineTransferInventory(@Nonnull SlimefunItem slimefunItem) {
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
            case blockSearchModeSlot -> BlockSearchMode.LINE_OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case blockSearchOrderSlot -> BlockSearchOrder.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoOrderSlot -> CargoOrder.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case blockSearchCycleSlot ->  BlockSearchCycle.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case blockSearchSelfSlot -> BlockSearchSelf.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoModeSlot -> CargoMode.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            case cargoFilterSlot -> CargoFilter.OPTION.getHandler(FinalTech.getLocationDataService(), location, this.slimefunItem);
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        for (int slot : this.specialBorder) {
            this.defaultItemStack.put(slot, Icon.SPECIAL_BORDER_ICON);
        }

        this.defaultItemStack.put(this.blockSearchModeSlot, BlockSearchMode.LINE_OPTION.defaultIcon());
        this.defaultItemStack.put(this.blockSearchOrderSlot, BlockSearchOrder.OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoOrderSlot, CargoOrder.OPTION.defaultIcon());
        this.defaultItemStack.put(this.blockSearchCycleSlot, BlockSearchCycle.OPTION.defaultIcon());
        this.defaultItemStack.put(this.blockSearchSelfSlot, BlockSearchSelf.OPTION.defaultIcon());

        this.defaultItemStack.put(this.cargoModeSlot, CargoMode.OPTION.defaultIcon());
        this.defaultItemStack.put(this.cargoFilterSlot, CargoFilter.OPTION.defaultIcon());
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
        BlockSearchMode.LINE_OPTION.checkAndUpdateIcon(inventory, this.blockSearchModeSlot, FinalTech.getLocationDataService(), location);
        BlockSearchOrder.OPTION.checkAndUpdateIcon(inventory, this.blockSearchOrderSlot, FinalTech.getLocationDataService(), location);
        CargoOrder.OPTION.checkAndUpdateIcon(inventory, this.cargoOrderSlot, FinalTech.getLocationDataService(), location);
        BlockSearchCycle.OPTION.checkAndUpdateIcon(inventory, this.blockSearchCycleSlot, FinalTech.getLocationDataService(), location);
        BlockSearchSelf.OPTION.checkAndUpdateIcon(inventory, this.blockSearchSelfSlot, FinalTech.getLocationDataService(), location);

        CargoMode.OPTION.checkAndUpdateIcon(inventory, this.cargoModeSlot, FinalTech.getLocationDataService(), location);
        CargoFilter.OPTION.checkAndUpdateIcon(inventory, this.cargoFilterSlot, FinalTech.getLocationDataService(), location);
    }

    public void updateCargoFilter(@Nonnull Inventory inventory, @Nonnull LocationData locationData) {
        CargoFilter.OPTION.checkAndUpdateIcon(inventory, this.cargoFilterSlot, FinalTech.getLocationDataService(), locationData);
    }
}
