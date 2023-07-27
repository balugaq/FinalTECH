package io.taraxacum.finaltech.util;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.core.dto.StringItemCardCache;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * We should make suer that
 * one item will have "item" key and "amount" key in the same time
 * or one item will not have only one of them.
 * @author Final_ROOT
 */
public class StringItemUtil {
    public static final NamespacedKey ITEM_KEY = new NamespacedKey(FinalTech.getPlugin(FinalTech.class), "item");
    public static final NamespacedKey AMOUNT_KEY = new NamespacedKey(FinalTech.getPlugin(FinalTech.class), "amount");

    public static int pullItemFromCard(@Nonnull StringItemCardCache stringItemCardCache, @Nonnull Inventory inventory, @Nonnull int[] slots) {
        if(!stringItemCardCache.storeItem()) {
            return 0;
        }

        ItemWrapper stringItem = stringItemCardCache.getTemplateStringItem();
        String amount = stringItemCardCache.getAmount();
        int maxStackSize = stringItem.getItemStack().getMaxStackSize();
        int validAmount = StringNumberUtil.compare(amount, String.valueOf(maxStackSize * slots.length)) >= 1 ? maxStackSize * slots.length : Integer.parseInt(amount);
        if (validAmount == 0) {
            stringItemCardCache.clearWithoutUpdate();
            return 0;
        }
        int validAmount1 = validAmount;

        amount = StringNumberUtil.sub(amount, String.valueOf(validAmount));
        ItemStack targetItem;
        int itemAmount;
        for (int slot : slots) {
            targetItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(targetItem)) {
                itemAmount = Math.min(validAmount, maxStackSize);
                if (itemAmount > 0) {
                    inventory.setItem(slot, stringItem.getItemStack());
                    ItemStack itemStack = inventory.getItem(slot);
                    itemStack.setAmount(itemAmount);
                    validAmount -= itemAmount;
                    if (validAmount == 0) {
                        break;
                    }
                }
            }
        }

        amount = StringNumberUtil.add(amount, String.valueOf(validAmount));
        if(StringNumberUtil.ZERO.equals(amount)) {
            stringItemCardCache.clearWithoutUpdate();
        } else {
            stringItemCardCache.setWithoutUpdate(amount);
        }

        return validAmount1 - validAmount;
    }

    public static int storageItemToCard(@Nonnull StringItemCardCache stringItemCardCache, @Nonnull Inventory inventory, @Nonnull int[] slots) {
        ItemWrapper stringItem = stringItemCardCache.getTemplateStringItem();
        int totalAmount = 0;
        List<ItemStack> sourceItemList = new ArrayList<>(slots.length);
        ItemStack sourceItem;
        for (int slot : slots) {
            sourceItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(sourceItem)) {
                continue;
            }
            if (stringItem == null || ItemStackUtil.isItemNull(stringItem.getItemStack())) {
                if(StringItemUtil.storableItem(sourceItem)) {
                    stringItem = new ItemWrapper(ItemStackUtil.cloneItem(sourceItem, 1));
                    totalAmount += sourceItem.getAmount();
                    sourceItemList.add(sourceItem);
                }
            } else if (ItemStackUtil.isItemSimilar(stringItem, sourceItem)) {
                totalAmount += sourceItem.getAmount();
                sourceItemList.add(sourceItem);
            }
        }

        totalAmount = totalAmount - totalAmount % stringItemCardCache.getCardItem().getAmount();
        int count = totalAmount / stringItemCardCache.getCardItem().getAmount();
        for (ItemStack itemStack : sourceItemList) {
            if (itemStack.getAmount() < totalAmount) {
                totalAmount -= itemStack.getAmount();
                itemStack.setAmount(0);
            } else {
                itemStack.setAmount(itemStack.getAmount() - totalAmount);
                break;
            }
        }

        if(count > 0) {
            if(!stringItemCardCache.storeItem()) {
                stringItemCardCache.setWithoutUpdate(stringItem, String.valueOf(count));
            } else {
                stringItemCardCache.setWithoutUpdate(StringNumberUtil.add(stringItemCardCache.getAmount(), String.valueOf(count)));
            }
        }

        return count;
    }

    @Nullable
    public static ItemStack parseItemInCard(@Nonnull ItemStack cardItem) {
        if (!cardItem.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = cardItem.getItemMeta();
        return StringItemUtil.parseItemInCard(itemMeta);
    }
    @Nullable
    public static ItemStack parseItemInCard(@Nonnull ItemMeta cardItemMeta) {
        PersistentDataContainer persistentDataContainer = cardItemMeta.getPersistentDataContainer();
        String itemString = persistentDataContainer.get(ITEM_KEY, PersistentDataType.STRING);
        if (itemString != null) {
            ItemStack stringItem = ItemStackUtil.stringToItemStack(itemString);
            if(stringItem != null) {
                stringItem.setAmount(1);
                return stringItem;
            }
        }
        return null;
    }

    @Nonnull
    public static String parseAmountInCard(@Nonnull ItemStack cardItem) {
        if (!cardItem.hasItemMeta()) {
            return StringNumberUtil.ZERO;
        }
        ItemMeta itemMeta = cardItem.getItemMeta();
        return StringItemUtil.parseAmountInCard(itemMeta);
    }
    @Nonnull
    public static String parseAmountInCard(@Nonnull ItemMeta cardItemMeta) {
        PersistentDataContainer persistentDataContainer = cardItemMeta.getPersistentDataContainer();
        if (persistentDataContainer.has(AMOUNT_KEY, PersistentDataType.STRING)) {
            return persistentDataContainer.get(AMOUNT_KEY, PersistentDataType.STRING);
        }
        return StringNumberUtil.ZERO;
    }

    public static void setItemInCard(@Nonnull ItemStack cardItem, @Nonnull ItemStack stringItem) {
        if(!cardItem.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = cardItem.getItemMeta();
        StringItemUtil.setItemInCard(itemMeta, stringItem);
        cardItem.setItemMeta(itemMeta);
    }

    public static void setItemInCard(@Nonnull ItemMeta itemMeta, @Nonnull ItemStack stringItem) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if(persistentDataContainer.has(AMOUNT_KEY, PersistentDataType.STRING)) {
            persistentDataContainer.set(ITEM_KEY, PersistentDataType.STRING, ItemStackUtil.itemStackToString(stringItem));
        }
    }

    public static void setItemInCard(@Nonnull ItemStack cardItem, @Nonnull ItemStack stringItem, int amount) {
        StringItemUtil.setItemInCard(cardItem, stringItem, String.valueOf(amount));
    }
    public static void setItemInCard(@Nonnull ItemStack cardItem, @Nonnull ItemStack stringItem, @Nonnull String amount) {
        if (!cardItem.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = cardItem.getItemMeta();
        StringItemUtil.setItemInCard(itemMeta, stringItem, amount);
        cardItem.setItemMeta(itemMeta);
    }
    public static void setItemInCard(@Nonnull ItemMeta itemMeta, @Nonnull ItemStack stringItem, @Nonnull String amount) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (StringNumberUtil.compare(amount, StringNumberUtil.ZERO) == 1) {
            persistentDataContainer.set(ITEM_KEY, PersistentDataType.STRING, ItemStackUtil.itemStackToString(stringItem));
            persistentDataContainer.set(AMOUNT_KEY, PersistentDataType.STRING, amount);
        } else {
            persistentDataContainer.remove(ITEM_KEY);
            persistentDataContainer.remove(AMOUNT_KEY);
        }
    }

    public static void setAmountInCard(@Nonnull ItemStack cardItem, @Nonnull String amount) {
        if (!cardItem.hasItemMeta()) {
            return;
        }
        ItemMeta cardItemMeta = cardItem.getItemMeta();
        PersistentDataContainer persistentDataContainer = cardItemMeta.getPersistentDataContainer();
        if (persistentDataContainer.has(ITEM_KEY, PersistentDataType.STRING) && persistentDataContainer.has(AMOUNT_KEY, PersistentDataType.STRING)) {
            if (StringNumberUtil.compare(amount, StringNumberUtil.ZERO) == 1) {
                persistentDataContainer.set(AMOUNT_KEY, PersistentDataType.STRING, amount);
            } else {
                persistentDataContainer.remove(ITEM_KEY);
                persistentDataContainer.remove(AMOUNT_KEY);
            }
        }
        cardItem.setItemMeta(cardItemMeta);
    }

    public static void clearCard(@Nonnull ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        StringItemUtil.clearCard(itemMeta);
        itemStack.setItemMeta(itemMeta);
    }

    public static void clearCard(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.remove(ITEM_KEY);
        persistentDataContainer.remove(AMOUNT_KEY);
    }

    public static boolean storableItem(@Nonnull ItemStack itemStack) {
        Material material = itemStack.getType();
        return !Tag.SHULKER_BOXES.isTagged(material) && !Material.BUNDLE.equals(material);
    }
}
