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

        ConfigFileManager visibleManager = FinalTech.getVisibleManager();
        if (visibleManager.containPath(this.getId())) {
            List<Function<Player, Boolean>> functionList = new ArrayList<>();
            if (visibleManager.containPath(this.getId(), "items")) {
                for (String itemId : visibleManager.getStringList(this.getId(), "items")) {
                    functionList.add(player -> {
                        SlimefunItem slimefunItem = SlimefunItem.getById(itemId);
                        if (slimefunItem instanceof VisibleItem visibleItem) {
                            return visibleItem.isVisible(player);
                        } else if (slimefunItem != null) {
                            return !slimefunItem.isHidden();
                        } else {
                            return true;
                        }
                    });
                }
            }
            if (visibleManager.containPath(this.getId(), "researches")) {
                for (String researchId : visibleManager.getStringList(this.getId(), "researches")) {
                    List<Research> researchList = Slimefun.getRegistry().getResearches();
                    Research targetResearch = null;
                    for (Research research : researchList) {
                        if (research.getKey().getKey().equals(researchId)) {
                            targetResearch = research;
                            break;
                        }
                    }
                    if (targetResearch != null) {
                        final Research finalResearch = targetResearch;
                        functionList.add(player -> {
                            Optional<PlayerProfile> optionalPlayerProfile = PlayerProfile.find(player);
                            return optionalPlayerProfile.map(playerProfile -> playerProfile.hasUnlocked(finalResearch)).orElse(true);
                        });
                    }
                }
            }
            if (visibleManager.containPath(this.getId(), "permissions")) {
                for (String permissionCode : visibleManager.getStringList(this.getId(), "permissions")) {
                    functionList.add(player -> player.hasPermission(permissionCode));
                }
            }
        }
    }

    @Nonnull
    public AbstractMySlimefunItem registerThis() {
        this.register(JavaPlugin.getPlugin(FinalTech.class));
        return this;
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
