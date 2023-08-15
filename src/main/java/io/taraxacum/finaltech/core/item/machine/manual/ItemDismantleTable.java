package io.taraxacum.finaltech.core.item.machine.manual;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.ItemDismantleTableInventory;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class ItemDismantleTable extends AbstractManualMachine implements RecipeItem {
    private final Set<String> allowedRecipeType = new HashSet<>(ConfigUtil.getItemStringList(this, "allowed-recipe-type"));
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final Set<String> allowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "allowed-id"));

    private final String key = "c";
    private final String count = ConfigUtil.getOrDefaultItemSetting("600", this, "count");
    private final String limit = ConfigUtil.getOrDefaultItemSetting("900", this, "limit");

    public ItemDismantleTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        return new ItemDismantleTableInventory(this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        String count = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.key), StringNumberUtil.ZERO);
        if(StringNumberUtil.compare(count, this.limit) < 0) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.key, StringNumberUtil.add(count));
        }

        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, block.getLocation());
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeTypeRegistry.getInstance().reload();

        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (String id : this.allowedRecipeType) {
            RecipeType recipeType = RecipeTypeRegistry.getInstance().getRecipeTypeById(id);
            if (recipeType != null && !ItemStackUtil.isItemNull(recipeType.toItem())) {
                this.registerDescriptiveRecipe(recipeType.toItem());
            }
        }
    }

    public boolean calAllowed(@Nonnull SlimefunItem slimefunItem) {
        if (this.allowedId.contains(slimefunItem.getId())) {
            return true;
        } else if (this.notAllowedId.contains(slimefunItem.getId())) {
            return false;
        } else {
            String slimefunItemId = slimefunItem.getId();
            synchronized (this) {
                if (this.allowedId.contains(slimefunItemId)) {
                    return true;
                } else if (this.notAllowedId.contains(slimefunItemId)) {
                    return false;
                }

                if (!this.allowedRecipeType.contains(slimefunItem.getRecipeType().getKey().getKey())) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                if (slimefunItem.getRecipe().length > this.getOutputSlot().length) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                boolean hasRecipe = false;
                for (ItemStack itemStack : slimefunItem.getRecipe()) {
                    if (ItemStackUtil.isItemNull(itemStack)) {
                        continue;
                    }
                    hasRecipe = true;
                    SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                    if (sfItem == null && !ItemStackUtil.isItemSimilar(itemStack, new ItemStack(itemStack.getType()))) {
                        this.notAllowedId.add(slimefunItemId);
                        return false;
                    } else if(sfItem instanceof ValidItem) {
                        this.notAllowedId.add(slimefunItemId);
                        return false;
                    }
                }
                if (!hasRecipe) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                this.allowedId.add(slimefunItemId);
                return true;
            }
        }
    }

    public String getKey() {
        return key;
    }

    public String getCount() {
        return count;
    }
}
