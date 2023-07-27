package io.taraxacum.finaltech.core.item.unusable;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.ItemMetaBuilder;
import io.taraxacum.libs.plugin.dto.ItemStackBuilder;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class ValidMaterialItem extends UnusableSlimefunItem implements SimpleValidItem, RecipeItem {
    private final ItemStackBuilder itemStackBuilder;

    public ValidMaterialItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);

        this.itemStackBuilder = ItemStackBuilder.fromItemStack(this.getItem());
        this.itemStackBuilder.amount(null);
        ItemMetaBuilder itemMetaBuilder = this.itemStackBuilder.getItemMetaBuilder();
        itemMetaBuilder.setData(FinalTech.getItemService().getIdKey(), PersistentDataType.STRING, this.getId());

        String validKey = FinalTech.getConfigManager().getOrDefault(String.valueOf(FinalTech.getRandom().nextDouble(FinalTech.getSeed())), "item-valid-key", this.getId());
        itemMetaBuilder.setData(new NamespacedKey(FinalTech.getInstance(), this.getId()), PersistentDataType.STRING, validKey);
    }

    public ValidMaterialItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType) {
        super(itemGroup, item, recipeType, new ItemStack[0]);

        this.itemStackBuilder = ItemStackBuilder.fromItemStack(this.getItem());
        this.itemStackBuilder.amount(null);
        ItemMetaBuilder itemMetaBuilder = this.itemStackBuilder.getItemMetaBuilder();
        itemMetaBuilder.setData(FinalTech.getItemService().getIdKey(), PersistentDataType.STRING, this.getId());

        String validKey = FinalTech.getConfigManager().getOrDefault(String.valueOf(FinalTech.getRandom().nextDouble(FinalTech.getSeed())), "item-valid-key", this.getId());
        itemMetaBuilder.setData(new NamespacedKey(FinalTech.getInstance(), this.getId()), PersistentDataType.STRING, validKey);
    }

    @Nonnull
    @Override
    public ItemStack getValidItem() {
        return this.itemStackBuilder.build();
    }

    @Override
    public boolean verifyItem(@Nonnull ItemStack itemStack) {
        return this.itemStackBuilder.softCompare(itemStack);
    }

    @Override
    public boolean verifyItem(@Nonnull ItemWrapper itemWrapper) {
        return this.itemStackBuilder.softCompare(itemWrapper);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    private void initItemStackBuilder(@Nonnull ItemStack itemStack) {
        ItemMetaBuilder itemMetaBuilder = this.itemStackBuilder.getItemMetaBuilder();
        itemMetaBuilder.setData(FinalTech.getItemService().getIdKey(), PersistentDataType.STRING, this.getId());

        String validKey = FinalTech.getConfigManager().getOrDefault(String.valueOf(FinalTech.getRandom().nextDouble(FinalTech.getSeed())), "item-valid-key", this.getId());
        itemMetaBuilder.setData(new NamespacedKey(FinalTech.getInstance(), this.getId()), PersistentDataType.STRING, validKey);
    }
}
