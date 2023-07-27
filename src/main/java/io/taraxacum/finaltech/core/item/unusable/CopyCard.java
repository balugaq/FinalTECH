package io.taraxacum.finaltech.core.item.unusable;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class CopyCard extends UnusableSlimefunItem implements RecipeItem, ValidItem {
    private final String itemLoreWithoutColor = "⌫⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌧⌦";
    private final String itemLore = TextUtil.colorPseudorandomString(itemLoreWithoutColor, FinalTech.getSeed());

    public CopyCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType) {
        super(itemGroup, item, recipeType, new ItemStack[0]);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(ConstantTableUtil.ITEM_COPY_CARD_AMOUNT),
                ConstantTableUtil.SLIMEFUN_TICK_INTERVAL);
    }

    @Override
    public boolean verifyItem(@Nonnull ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            return false;
        }

        for (String l : lore) {
            if (this.itemLoreWithoutColor.equals(ChatColor.stripColor(l))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean verifyItem(@Nonnull ItemWrapper itemWrapper) {
        ItemMeta itemMeta = itemWrapper.getItemMeta();
        if (!itemMeta.hasLore()) {
            return false;
        }
        List<String> lore = itemMeta.getLore();
        if (lore == null) {
            return false;
        }

        for (String l : lore) {
            if (this.itemLoreWithoutColor.equals(ChatColor.stripColor(l))) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public ItemStack getValidItem(@Nonnull ItemStack stringItem, @Nonnull String amount) {
        ItemStack result = ItemStackUtil.cloneItem(FinalTechItemStacks.COPY_CARD);
        ItemStack temp = new ItemStack(stringItem);

        result.setAmount(1);
        StringItemUtil.setItemInCard(result, temp, amount);
        List<String> loreList = JavaUtil.toList(ConfigUtil.getStatusMenuLore(FinalTech.getLanguageManager(), this.getId(),
                ItemStackUtil.getItemName(temp),
                amount));
        loreList.add(0, this.itemLore);
        ItemStackUtil.setLore(result, loreList);

        return result;
    }

    public boolean isTargetItem(@Nonnull ItemStack itemStack) {
        if (Tag.SHULKER_BOXES.isTagged(itemStack.getType()) || Material.BUNDLE.equals(itemStack.getType())) {
            return false;
        }

        return !FinalTechItems.SINGULARITY.verifyItem(itemStack) && !FinalTechItems.SPIROCHETE.verifyItem(itemStack) && !FinalTechItems.ITEM_PHONY.verifyItem(itemStack);
    }
}
