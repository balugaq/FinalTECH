package io.taraxacum.finaltech.core.item.unusable;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.TextUtil;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class StorageCard extends UnusableSlimefunItem implements RecipeItem, ValidItem {
    private final String itemLoreWithoutColor = "⌫⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌦";
    private final String itemLore = TextUtil.colorRandomString(this.itemLoreWithoutColor);

    public StorageCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
        ItemStackUtil.addLoreToFirst(item, this.itemLore);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    @Override
    public boolean verifyItem(@Nonnull ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            return this.verifyItem(itemStack.getItemMeta());
        }
        return false;
    }

    @Override
    public boolean verifyItem(@Nonnull ItemWrapper itemWrapper) {
        if (itemWrapper.hasItemMeta()) {
            return this.verifyItem(itemWrapper.getItemMeta());
        }
        return false;
    }

    public boolean verifyItem(@Nonnull ItemMeta itemMeta) {
        if (!itemMeta.hasLore()) {
            return false;
        }
        List<String> lore = itemMeta.getLore();
        return !lore.isEmpty() && this.itemLoreWithoutColor.equals(ChatColor.stripColor(lore.get(0)));
    }

    @Nonnull
    public ItemStack getValidItem(@Nonnull ItemStack itemStack, @Nonnull String amount) {
        ItemStack result = ItemStackUtil.cloneItem(FinalTechItemStacks.STORAGE_CARD);
        result.setAmount(1);
        StringItemUtil.setItemInCard(result, itemStack, amount);
        this.updateLore(result);
        return result;
    }

    public boolean isTargetItem(@Nonnull ItemStack itemStack) {
        Material material = itemStack.getType();
        return !Tag.SHULKER_BOXES.isTagged(material) && !Material.BUNDLE.equals(material);
    }

    public void updateLore(@Nonnull ItemStack cardItem) {
        if (!cardItem.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = cardItem.getItemMeta();
        this.updateLore(itemMeta);
        cardItem.setItemMeta(itemMeta);
    }

    /**
     * Use ItemStack.setItemMeta() after using this
     * @param cardItemMeta
     */
    public void updateLore(@Nonnull ItemMeta cardItemMeta) {
        PersistentDataContainer persistentDataContainer = cardItemMeta.getPersistentDataContainer();
        ItemStack stringItem = null;
        String amount = null;
        String itemString = persistentDataContainer.get(StringItemUtil.ITEM_KEY, PersistentDataType.STRING);
        if (itemString != null) {
            amount = persistentDataContainer.get(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING);
            stringItem = ItemStackUtil.stringToItemStack(itemString);
        }
        this.updateLore(cardItemMeta, stringItem, amount);
    }

    @Deprecated
    public void updateLore(@Nonnull ItemMeta cardItemMeta, @Nullable ItemStack stringItem) {
        PersistentDataContainer persistentDataContainer = cardItemMeta.getPersistentDataContainer();
        List<String> lore;
        if (persistentDataContainer.has(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING)) {
            String amount = persistentDataContainer.get(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING);
            lore = cardItemMeta.getLore();
            if (lore == null || lore.isEmpty()) {
                lore = new ArrayList<>(4);
                lore.add(this.itemLore);
            } else {
                lore.set(0, this.itemLore);
            }
            String name = ItemStackUtil.getItemName(stringItem);
            if (lore.size() == 1) {
                lore.add(TextUtil.getRandomColor() + name + "  " + TextUtil.colorRandomString(String.valueOf(amount)));
            } else {
                lore.set(1, TextUtil.getRandomColor() + name + "  " + TextUtil.colorRandomString(String.valueOf(amount)));
            }
        } else {
            lore = new ArrayList<>(1);
            lore.add(this.itemLore);
        }
        cardItemMeta.setLore(lore);
    }

    public void updateLore(@Nonnull ItemMeta cardItemMeta, @Nullable ItemStack stringItem, @Nullable String amount) {
        List<String> lore;
        if (amount != null) {
            lore = cardItemMeta.getLore();
            if (lore == null || lore.isEmpty()) {
                lore = new ArrayList<>(4);
                lore.add(this.itemLore);
            } else {
                lore.set(0, this.itemLore);
            }
            String name = ItemStackUtil.getItemName(stringItem);
            if (lore.size() == 1) {
                lore.add(TextUtil.getRandomColor() + name + "  " + TextUtil.colorRandomString(amount));
            } else {
                lore.set(1, TextUtil.getRandomColor() + name + "  " + TextUtil.colorRandomString(amount));
            }
        } else {
            lore = new ArrayList<>(1);
            lore.add(this.itemLore);
        }
        cardItemMeta.setLore(lore);
    }
}
