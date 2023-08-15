package io.taraxacum.finaltech.core.item.machine.template.extraction;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.ExtractionMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public abstract class AbstractExtractionMachine extends AbstractMachine implements RecipeItem {
    public AbstractExtractionMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new ExtractionMachineInventory(this);
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }
        int itemSlot = this.getInputSlot()[FinalTech.getRandom().nextInt(this.getInputSlot().length)];
        ItemStack itemStack = inventory.getItem(itemSlot);
        if (ItemStackUtil.isItemNull(itemStack)) {
            return;
        }

        ItemWrapper itemWrapper = new ItemWrapper(itemStack);
        int matchAmount = 0;
        ItemAmountWrapper[] outputItems = null;
        List<AdvancedMachineRecipe> advancedRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(this.getId());
        for (AdvancedMachineRecipe advancedMachineRecipe : advancedRecipeList) {
            if (itemWrapper.getItemStack().getAmount() >= advancedMachineRecipe.getInput()[0].getAmount() && ItemStackUtil.isItemSimilar(advancedMachineRecipe.getInput()[0], itemWrapper)) {
                outputItems = advancedMachineRecipe.getOutput();
                matchAmount = itemWrapper.getItemStack().getAmount() / advancedMachineRecipe.getInput()[0].getAmount();
                break;
            }
        }

        if (outputItems != null) {
            InventoryUtil.tryPushItem(inventory, this.getOutputSlot(), matchAmount, outputItems);
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerRecipe(@Nonnull MachineRecipe recipe) {
        if (recipe.getInput().length != 1) {
            throw new IllegalArgumentException("Register recipe for " + this.getItemName() + " has occurred a error: " + " input item type should be only just one");
        }

        if (recipe.getInput()[0].getAmount() > 1) {
            this.getAddon().getJavaPlugin().getServer().getLogger().info("Register recipe for " + this.getItemName() + " has occurred a error: " + " input item amount should be one");
            recipe.getInput()[0] = new CustomItemStack(recipe.getInput()[0], 1);
        }

        RecipeItem.super.registerRecipe(recipe);
    }
}
