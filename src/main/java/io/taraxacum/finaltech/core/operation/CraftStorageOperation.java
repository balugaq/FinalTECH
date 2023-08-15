package io.taraxacum.finaltech.core.operation;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;

import javax.annotation.Nonnull;
import java.math.BigInteger;

public class CraftStorageOperation implements MachineOperation {
    private final SlimefunItem slimefunItem;
    private final ItemAmountWrapper[] itemAmountWrappers;
    private final BigInteger[] amount;

    public CraftStorageOperation(@Nonnull SlimefunItem slimefunItem, @Nonnull ItemAmountWrapper[] itemAmountWrappers) {
        this.slimefunItem = slimefunItem;
        this.itemAmountWrappers = itemAmountWrappers;
        this.amount = new BigInteger[this.itemAmountWrappers.length];
        for(int i = 0; i < this.amount.length; i++) {
            this.amount[i] = new BigInteger("0");
        }
    }

    @Nonnull
    public BigInteger addAmount(int index, int amount) {
        if(index < this.amount.length) {
            return this.amount[index].add(new BigInteger(String.valueOf(amount)));
        }
        return new BigInteger("0");
    }

    public SlimefunItem getSlimefunItem() {
        return slimefunItem;
    }

    public ItemAmountWrapper[] getItemAmountWrappers() {
        return itemAmountWrappers;
    }

    public BigInteger[] getAmount() {
        return amount;
    }

    public BigInteger calMaxMatch() {
        BigInteger bigInteger = this.amount[0].divide(new BigInteger(String.valueOf(this.itemAmountWrappers[0].getAmount())));

        for(int i = 1; i < this.amount.length; i++) {
            bigInteger = bigInteger.min(this.amount[i].divide(new BigInteger(String.valueOf(this.itemAmountWrappers[i].getAmount()))));
        }

        return bigInteger;
    }

    @Override
    public void addProgress(int ticks) {

    }

    @Override
    public int getProgress() {
        return 0;
    }

    @Override
    public int getTotalTicks() {
        return 0;
    }
}
