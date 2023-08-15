package io.taraxacum.finaltech.core.operation;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
@Deprecated
public class ItemCopyCardOperation implements ItemSerializationConstructorOperation {
    private long count;
    private long difficulty;
    private final ItemStack matchItem;
    private final ItemWrapper matchItemWrapper;
    private final ItemStack copyCardItem;
    private final ItemStack showItem;

    protected ItemCopyCardOperation(@Nonnull ItemStack itemStack) {
        this.count = itemStack.getAmount();
        this.difficulty = ConstantTableUtil.ITEM_COPY_CARD_AMOUNT;
        this.matchItem = itemStack.clone();
        this.matchItem.setAmount(1);
        this.matchItemWrapper = new ItemWrapper(this.matchItem);
        this.copyCardItem = FinalTechItems.COPY_CARD.getValidItem(this.matchItem, "1");
        this.showItem = ItemStackUtil.newItemStack(itemStack.getType(),
                FinalTech.getLanguageString("items", FinalTechItems.ITEM_SERIALIZATION_CONSTRUCTOR.getId(), "copy-card", "name"));
        this.updateShowItem();
    }

    public double getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    @Nonnull
    public ItemStack getMatchItem() {
        return this.matchItem;
    }

    @Override
    public int getType() {
        return ItemSerializationConstructorOperation.COPY_CARD;
    }

    @Nonnull
    @Override
    public ItemStack getShowItem() {
        return this.showItem;
    }

    @Override
    public void updateShowItem() {
        ItemStackUtil.setLore(this.showItem,
                FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", FinalTechItems.ITEM_SERIALIZATION_CONSTRUCTOR.getId(), "copy-card", "lore"),
                        ItemStackUtil.getItemName(this.matchItem),
                        String.format("%.8f", this.count),
                        String.valueOf(this.difficulty)));
    }

    @Override
    public int addItem(@Nullable ItemStack itemStack) {
        if (!this.isFinished()) {
            if (ItemStackUtil.isItemSimilar(itemStack, this.matchItemWrapper)) {
                if (itemStack.getAmount() + this.count < this.difficulty) {
                    int amount = itemStack.getAmount();
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    this.count += amount;
                    return amount;
                } else {
                    int amount = (int) (this.difficulty - this.count);
                    itemStack.setAmount(itemStack.getAmount() - amount);
                    this.count = this.difficulty;
                    return amount;
                }
            } else if (FinalTechItems.ITEM_PHONY.verifyItem(itemStack)) {
                int amount = (int) Math.min(itemStack.getAmount(), this.difficulty - this.count);
                itemStack.setAmount(itemStack.getAmount() - amount);
                this.count += amount;
                return amount;
            }
        }
        return 0;
    }

    @Override
    public boolean isFinished() {
        return this.count >= this.difficulty;
    }

    @Nonnull
    @Override
    public ItemStack getResult() {
        return this.copyCardItem;
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
