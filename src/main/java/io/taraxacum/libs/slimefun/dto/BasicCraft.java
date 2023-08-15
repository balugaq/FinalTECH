package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Final_ROOT
 */
public class BasicCraft {
    @Nonnull
    private final SlimefunItem matchItem;

    private int matchAmount;

    BasicCraft(@Nonnull SlimefunItem matchItem, int matchAmount) {
        this.matchItem = matchItem;
        this.matchAmount = matchAmount;
    }

    @Nonnull
    public SlimefunItem getMatchItem() {
        return matchItem;
    }

    public int getMatchAmount() {
        return matchAmount;
    }

    public void setMatchAmount(int matchAmount) {
        this.matchAmount = matchAmount;
    }

    public void consumeItem(@Nonnull Inventory inventory, int[] slots) {
        ItemStack[] itemStacks = this.matchItem.getRecipe();
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack existedItem = inventory.getItem(slots[i]);
            if (!ItemStackUtil.isItemNull(existedItem)) {
                existedItem.setAmount(existedItem.getAmount() - itemStacks[i].getAmount() * this.getMatchAmount());
            }
        }
    }

    @Nullable
    public static BasicCraft doCraftBySlimefunItem(@Nonnull List<SlimefunItem> slimefunItemList, @Nonnull Inventory inventory, int[] slots) {
        int matchAmount;

        Map<Integer, ItemWrapper> indexItemMap = new HashMap<>();
        ItemStack itemStack;
        for (int i = 0; i < slots.length; i++) {
            itemStack = inventory.getItem(slots[i]);
            if (!ItemStackUtil.isItemNull(itemStack)) {
                indexItemMap.put(i, new ItemWrapper(itemStack));
            }
        }

        for (SlimefunItem slimefunItem : slimefunItemList) {
            ItemStack[] itemStacks = slimefunItem.getRecipe();
            if (itemStacks.length <= slots.length) {
                matchAmount = ConstantTableUtil.ITEM_MAX_STACK;
                for (int i = 0; i < itemStacks.length; i++) {
                    itemStack = itemStacks[i];

                    if (ItemStackUtil.isItemNull(itemStack) == indexItemMap.containsKey(i)) {
                        matchAmount = 0;
                        break;
                    }

                    if (ItemStackUtil.isItemNull(itemStack)) {
                        continue;
                    }

                    ItemWrapper itemWrapper = indexItemMap.get(i);
                    if (itemWrapper.getItemStack().getAmount() < itemStack.getAmount() || !ItemStackUtil.isEnchantmentSame(itemWrapper.getItemStack(), itemStack)) {
                        matchAmount = 0;
                        break;
                    }

                    SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                    if (sfItem instanceof ValidItem validItem) {
                        if(!validItem.verifyItem(itemWrapper)) {
                            matchAmount = 0;
                            break;
                        }
                    } else if(!ItemStackUtil.isItemSimilar(itemStack, itemWrapper)) {
                        matchAmount = 0;
                        break;
                    }
                    matchAmount = Math.min(matchAmount, itemWrapper.getItemStack().getAmount() / itemStack.getAmount());
                }

                if (matchAmount > 0) {
                    return new BasicCraft(slimefunItem, matchAmount);
                }
            }
        }
        return null;
    }
}
