package io.taraxacum.finaltech.core.item.unusable;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.geo.GEOResource;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.ItemMetaBuilder;
import io.taraxacum.libs.plugin.dto.ItemStackBuilder;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class Ether extends UnusableSlimefunItem implements GEOResource, SimpleValidItem, RecipeItem {
    private final NamespacedKey key = new NamespacedKey(FinalTech.getInstance(), this.getId());
    private final int baseAmountNormal = ConfigUtil.getOrDefaultItemSetting(16, this, "base-amount-normal");
    private final int baseAmountNether = ConfigUtil.getOrDefaultItemSetting(8, this, "base-amount-nether");
    private final int baseAmountTheEnd = ConfigUtil.getOrDefaultItemSetting(64, this, "base-amount-end");
    private final int baseAmountCustom = ConfigUtil.getOrDefaultItemSetting(16, this, "base-amount-custom");
    private final int maxDeviation = ConfigUtil.getOrDefaultItemSetting(8, this, "max-deviation");

    private final ItemStackBuilder itemStackBuilder;

    public Ether(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType) {
        super(itemGroup, item, recipeType, new ItemStack[0]);

        this.itemStackBuilder = ItemStackBuilder.fromItemStack(this.getItem());
        this.itemStackBuilder.amount(null);
        ItemMetaBuilder itemMetaBuilder = this.itemStackBuilder.getItemMetaBuilder();
        itemMetaBuilder.setData(FinalTech.getItemService().getIdKey(), PersistentDataType.STRING, this.getId());

        String validKey = FinalTech.getConfigManager().getOrDefault(String.valueOf(FinalTech.getRandom().nextDouble(FinalTech.getSeed())), "item-valid-key", this.getId());
        itemMetaBuilder.setData(new NamespacedKey(FinalTech.getInstance(), this.getId()), PersistentDataType.STRING, validKey);
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);
        this.register();
    }

    @Override
    public int getDefaultSupply(@Nonnull World.Environment environment, @Nonnull Biome biome) {
        return switch (environment) {
            case NORMAL -> this.baseAmountNormal;
            case NETHER -> this.baseAmountNether;
            case THE_END -> this.baseAmountTheEnd;
            default -> this.baseAmountCustom;
        };
    }

    @Override
    public int getMaxDeviation() {
        return this.maxDeviation;
    }

    @Nonnull
    @Override
    public String getName() {
        return this.getItemName();
    }

    @Override
    public boolean isObtainableFromGEOMiner() {
        return false;
    }

    @Nonnull
    @Override
    public NamespacedKey getKey() {
        return this.key;
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
}
