package io.taraxacum.finaltech.core.inventory.limit;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.core.option.Icon;

import javax.annotation.Nonnull;

public class DustFactoryInventory extends AbstractLimitMachineInventory {
    private final int[] border = new int[] {3, 4, 5, 12, 14, 21, 22, 23, 30, 31, 32};
    private final int[] inputBorder = new int[] {0, 1, 2, 11, 20, 27, 28, 29};
    private final int[] outputBorder = new int[] {6, 7, 8, 15, 24, 33, 34, 35};
    private final int[] inputSlot = new int[] {9, 10, 18, 19};
    private final int[] outputSlot = new int[] {16, 17, 25, 26};

    public final int statusSlot = 22;

    public DustFactoryInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
    }

    @Override
    protected void initSelf() {
        super.initSelf();
        this.defaultItemStack.put(this.statusSlot, Icon.STATUS_ICON);
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
    public int[] getInputSlot() {
        return this.inputSlot;
    }

    @Override
    public int[] getOutputSlot() {
        return this.outputSlot;
    }

    @Override
    public int getSize() {
        return 36;
    }
}
