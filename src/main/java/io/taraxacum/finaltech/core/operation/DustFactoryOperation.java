package io.taraxacum.finaltech.core.operation;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class DustFactoryOperation implements MachineOperation {
    private int amountCount = 0;
    private int typeCount = 0;
    private final int amountDifficulty;
    private final int typeDifficulty;
    private final ItemWrapper[] matchItemList;

    public DustFactoryOperation(int amountDifficulty, int typeDifficulty) {
        this.amountDifficulty = amountDifficulty;
        this.typeDifficulty = typeDifficulty;
        this.matchItemList = new ItemWrapper[typeDifficulty + 1];
    }

    public void addItem(@Nullable ItemStack itemStack) {
        if (ItemStackUtil.isItemNull(itemStack)) {
            return;
        }

        this.amountCount = Math.min(this.amountCount + itemStack.getAmount(), this.amountDifficulty + 1);

        ItemWrapper itemWrapper = new ItemWrapper(itemStack);
        if (this.typeCount <= this.typeDifficulty) {
            boolean newItem = true;
            for (int i = 0; i < this.typeCount; i++) {
                ItemWrapper existedItem = this.matchItemList[i];
                if (ItemStackUtil.isItemSimilar(itemWrapper, existedItem)) {
                    newItem = false;
                    break;
                }
            }
            if (newItem) {
                itemWrapper.setItemStack(itemStack.clone());
                this.matchItemList[this.typeCount++] = itemWrapper;
            }
        }
    }

    public int getAmountCount() {
        return this.amountCount;
    }

    public int getTypeCount() {
        return this.typeCount;
    }

    public int getAmountDifficulty() {
        return this.amountDifficulty;
    }

    public int getTypeDifficulty() {
        return this.typeDifficulty;
    }

    @Override
    public boolean isFinished() {
        return this.amountCount >= this.amountDifficulty && this.typeCount >= this.typeDifficulty;
    }

    @Nullable
    public ItemStack getResult() {
        if (this.amountCount == this.amountDifficulty && this.typeCount == this.typeDifficulty) {
            return FinalTechItems.ORDERED_DUST.getValidItem();
        } else if (this.isFinished()) {
            return FinalTechItems.UNORDERED_DUST.getValidItem();
        } else {
            return null;
        }
    }

    @Deprecated
    @Override
    public void addProgress(int i) {

    }

    @Deprecated
    @Override
    public int getProgress() {
        return this.amountCount * this.typeCount;
    }

    @Deprecated
    @Override
    public int getTotalTicks() {
        return this.amountDifficulty * this.typeDifficulty;
    }
}
