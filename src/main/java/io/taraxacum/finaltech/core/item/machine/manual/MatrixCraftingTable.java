package io.taraxacum.finaltech.core.item.machine.manual;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.MatrixCraftingTableInventory;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.setup.FinalTechRecipeTypes;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class MatrixCraftingTable extends AbstractManualMachine implements RecipeItem {
    public MatrixCraftingTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        return new MatrixCraftingTableInventory(this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, block.getLocation());
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        RecipeUtil.registerRecipeByRecipeType(this, FinalTechRecipeTypes.MATRIX_CRAFTING_TABLE);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDisplayRecipes() {
        List<ItemStack> itemStackList = new ArrayList<>();
        for(SlimefunItem slimefunItem : RecipeTypeRegistry.getInstance().getByRecipeType(FinalTechRecipeTypes.MATRIX_CRAFTING_TABLE)) {
            itemStackList.add(Icon.BORDER_ICON);
            itemStackList.add(slimefunItem.getRecipeOutput());
        }
        return itemStackList;
    }
}
