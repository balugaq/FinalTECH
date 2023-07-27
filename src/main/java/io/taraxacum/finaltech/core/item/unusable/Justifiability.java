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
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Final_ROOT
 */
public class Justifiability extends UnusableSlimefunItem implements RecipeItem, SimpleValidItem {
    private final ItemStackBuilder itemStackBuilder;

    public Justifiability(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType) {
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
    public Collection<ItemStack> getDrops() {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(this.getValidItem());
        return drops;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
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
}
