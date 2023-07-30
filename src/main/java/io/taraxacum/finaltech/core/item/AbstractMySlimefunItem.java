package io.taraxacum.finaltech.core.item;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.interfaces.VisibleItem;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.dto.ConfigFileManager;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * We may add something soon
 * @author Final_ROOT
 */
public abstract class AbstractMySlimefunItem extends SlimefunItem implements VisibleItem {
    private final List<Function<Player, Boolean>> visibleFunctionList = new ArrayList<>();

    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item, ConfigUtil.getRecipeType(item.getItemId()), ConfigUtil.getRecipe(item.getItemId()));
    }

    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, ConfigUtil.getRecipe(item.getItemId()), recipeOutput);
    }

    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);
        if (this instanceof RecipeItem recipeItem) {
            int delay = recipeItem.getRegisterRecipeDelay();
            if(delay > 0) {
                this.getAddon().getJavaPlugin().getServer().getScheduler().runTaskLater((Plugin)addon, () -> {
                    (recipeItem).registerDefaultRecipes();
                    MachineRecipeFactory.getInstance().initAdvancedRecipeMap(this.getId());
                }, delay);
            } else {
                (recipeItem).registerDefaultRecipes();
                MachineRecipeFactory.getInstance().initAdvancedRecipeMap(this.getId());
            }
        }
    }

    @Nonnull
    public AbstractMySlimefunItem registerThis() {
        this.register(JavaPlugin.getPlugin(FinalTech.class));
        return this;
    }

    public void addVisibleFunction(@Nonnull Function<Player, Boolean> function) {
        this.visibleFunctionList.add(function);
    }

    @Override
    public boolean isVisible(@Nonnull Player player) {
        for (Function<Player, Boolean> function : this.visibleFunctionList) {
            if (!function.apply(player)) {
                return false;
            }
        }

        return true;
    }
}
