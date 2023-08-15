package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.cargo.AdvancedAutoCraftFrameInventory;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Final_ROOT
 */
public class AdvancedAutoCraftFrame extends AbstractMachine implements RecipeItem {
    private final Map<Location, AdvancedMachineRecipe> locationRecipeMap = new HashMap<>();
    private AdvancedAutoCraftFrameInventory advancedAutoCraftFrameInventory;

    public AdvancedAutoCraftFrame(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        this.advancedAutoCraftFrameInventory = new AdvancedAutoCraftFrameInventory(this, this.locationRecipeMap);
        return this.advancedAutoCraftFrameInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.advancedAutoCraftFrameInventory.parseItemSlot);
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        if (this.advancedAutoCraftFrameInventory != null) {
            this.advancedAutoCraftFrameInventory.registerRecipe();
            for (String id : this.advancedAutoCraftFrameInventory.getRecipeMap().keySet()) {
                SlimefunItem slimefunItem = SlimefunItem.getById(id);
                if (slimefunItem != null) {
                    ItemStack itemStack = slimefunItem.getItem();
                    if (!ItemStackUtil.isItemNull(itemStack)) {
                        this.registerDescriptiveRecipe(itemStack);
                    }
                }
            }

            this.registerBorder();

            for (String recipeTypeId : this.advancedAutoCraftFrameInventory.getRecipeTypeIdList()) {
                RecipeType recipeType = RecipeTypeRegistry.getInstance().getRecipeTypeById(recipeTypeId);
                if (recipeType != null) {
                    ItemStack itemStack = JavaUtil.getFirstNotNull(recipeType.toItem(), recipeType.getMachine().getItem());
                    if (!ItemStackUtil.isItemNull(itemStack)) {
                        this.registerDescriptiveRecipe(itemStack);
                    }
                }
            }
        }
    }

    @Override
    public int getRegisterRecipeDelay() {
        return 2;
    }
}
