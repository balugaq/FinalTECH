package io.taraxacum.finaltech.core.inventory;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;

import javax.annotation.Nonnull;

public abstract class AbstractOrdinaryMachineInventory extends AbstractMachineInventory implements LogicInventory {
    protected AbstractOrdinaryMachineInventory(@Nonnull SlimefunItem slimefunItem) {
        super(slimefunItem);
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
            case INPUT -> this.getInputSlot();
            case OUTPUT -> this.getOutputSlot();
            default -> new int[0];
        };
    }

    @Nonnull
    protected abstract int[] getInputSlot();

    @Nonnull
    protected abstract int[] getOutputSlot();
}
