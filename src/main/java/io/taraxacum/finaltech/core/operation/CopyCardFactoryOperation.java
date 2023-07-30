package io.taraxacum.finaltech.core.operation;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class CopyCardFactoryOperation implements MachineOperation {
    private long count;
    private long baseDifficulty;
    private long dynamicDifficulty;
    private boolean lockDifficulty;
    private final ItemWrapper itemWrapper;
    private final ItemStack resultItemStack;
    private final ItemStack showItemStack;

    public CopyCardFactoryOperation(@Nonnull ItemStack itemStack) {
        this.count = itemStack.getAmount();
        this.baseDifficulty = ConstantTableUtil.ITEM_COPY_CARD_AMOUNT;
        this.dynamicDifficulty = 0;
        this.lockDifficulty = false;
        this.itemWrapper = new ItemWrapper(ItemStackUtil.cloneItem(itemStack, 1));
        this.resultItemStack = FinalTechItems.COPY_CARD.getValidItem(this.itemWrapper.getItemStack(), "1");
        this.showItemStack = ItemStackUtil.newItemStack(itemStack.getType(),
                // TODO ITEM_SERIALIZATION_CONSTRUCTOR
                FinalTech.getLanguageString("items", FinalTechItems.COPY_CARD_FACTORY.getId(), "copy-card", "name"));
        this.updateShowItem();
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setDynamicDifficulty(long dynamicDifficulty) {
        this.dynamicDifficulty = dynamicDifficulty;
    }

    public boolean isLockDifficulty() {
        return lockDifficulty;
    }

    public void setLockDifficulty(boolean lockDifficulty) {
        this.lockDifficulty = lockDifficulty;
    }

    @Nonnull
    public ItemWrapper getItemWrapper() {
        return itemWrapper;
    }

    @Nonnull
    public ItemStack getShowItemStack() {
        return this.showItemStack;
    }

    public void updateShowItem() {
        ItemStackUtil.setLore(this.showItemStack,
                FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", FinalTechItems.COPY_CARD_FACTORY.getId(), "copy-card", "lore"),
                        ItemStackUtil.getItemName(this.itemWrapper.getItemStack()),
                        String.valueOf(this.count),
                        String.valueOf(this.baseDifficulty + this.dynamicDifficulty)));
    }

    public int addItem(@Nonnull ItemStack itemStack) {
        if (!this.isFinished()) {
            ItemWrapper itemWrapper = new ItemWrapper(itemStack);
            if (ItemStackUtil.isItemSimilar(itemWrapper, this.itemWrapper)) {
                if (itemStack.getAmount() + this.count < this.dynamicDifficulty + this.baseDifficulty) {
                    int amount = itemStack.getAmount();
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    this.count += amount;
                    return amount;
                } else {
                    int amount = (int) (this.dynamicDifficulty + this.baseDifficulty - this.count);
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    this.count = this.dynamicDifficulty + this.baseDifficulty;
                    return amount;
                }
            } else if (FinalTechItems.ITEM_PHONY.verifyItem(itemWrapper)) {
                int amount = (int) Math.min(itemStack.getAmount(), this.dynamicDifficulty + this.baseDifficulty - this.count);
                itemStack.setAmount(itemStack.getAmount() - amount);
                this.count += amount;
                this.lockDifficulty = true;
                return amount;
            }
        }
        return 0;
    }

    @Override
    public boolean isFinished() {
        return this.count >= this.dynamicDifficulty + this.baseDifficulty;
    }

    @Nonnull
    public ItemStack getResult() {
        return this.resultItemStack;
    }

    @Deprecated
    @Override
    public void addProgress(int i) {

    }

    @Deprecated
    @Override
    public int getProgress() {
        return 0;
    }

    @Deprecated
    @Override
    public int getTotalTicks() {
        return 0;
    }
}
