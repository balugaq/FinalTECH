package io.taraxacum.finaltech.core.inventory.simple;

import io.taraxacum.finaltech.core.inventory.AbstractOrdinaryMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class MatrixReactorInventory extends AbstractOrdinaryMachineInventory {
    private final int[] border = new int[] {0, 2, 3, 5, 6, 8, 12, 13, 14, 21, 23};
    private final int[] inputBorder = new int[] {9, 10, 11, 18, 20, 27, 29, 36, 38, 45, 46, 47, 15, 16, 17, 24, 26, 33, 35, 42, 44, 51, 52, 53};
    private final int[] outputBorder = new int[] {30, 31, 32, 39, 41, 48, 50};
    private final int[] outputSlot = new int[] {40};

    private final int orderedDustSlot = 1;
    private final ItemStack orderedDustIcon = MachineUtil.cloneAsDescriptiveItem(FinalTechItemStacks.UNORDERED_DUST);
    public final int[] orderedDustInputSlot = new int[] {25, 34, 43};

    private final int unorderedDustSlot = 7;
    private final ItemStack unorderedDustIcon = MachineUtil.cloneAsDescriptiveItem(FinalTechItemStacks.ORDERED_DUST);
    public final int[] unorderedDustInputSlot = new int[] {19, 28, 37};

    public final int[] itemPhonyInputSlot = new int[] {4};
    public final int[] itemInputSlot = new int[] {22};

    public final int statusSlot = 49;

    public MatrixReactorInventory(@Nonnull AbstractMachine machine) {
        super(machine);
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

    @Nonnull
    @Override
    public int[] getInputSlot() {
        return this.itemInputSlot;
    }

    @Nonnull
    @Override
    public int[] getOutputSlot() {
        return this.outputSlot;
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.orderedDustSlot, this.orderedDustIcon);
        this.defaultItemStack.put(this.unorderedDustSlot, this.unorderedDustIcon);
        this.defaultItemStack.put(this.statusSlot, Icon.STATUS_ICON);
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType, @Nonnull ItemStack itemStack, @Nonnull Inventory inventory, @Nonnull Location location) {
        if (RequestType.OUTPUT.equals(requestType)) {
            return this.outputSlot;
        } else if (!RequestType.INPUT.equals(requestType)) {
            return new int[0];
        }

        ItemWrapper itemWrapper = new ItemWrapper(itemStack);
        if (FinalTechItems.ORDERED_DUST.verifyItem(itemWrapper)) {
            return orderedDustInputSlot;
        } else if (FinalTechItems.UNORDERED_DUST.verifyItem(itemWrapper)) {
            return unorderedDustInputSlot;
        } else if (FinalTechItems.ITEM_PHONY.verifyItem(itemWrapper)) {
            return itemPhonyInputSlot;
        } else {
            return itemInputSlot;
        }
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }
}
