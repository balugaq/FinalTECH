package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LogicItem;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.LogicCrafterInventory;
import io.taraxacum.finaltech.core.item.unusable.DigitalNumber;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class LogicCrafter extends AbstractMachine implements RecipeItem {
    public LogicCrafter(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new LogicCrafterInventory(this);
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
        if (inventory == null || InventoryUtil.slotCount(inventory, this.getOutputSlot()) == this.getOutputSlot().length) {
            return;
        }

        int digit = 0;
        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                return;
            }
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem instanceof LogicItem logicItem) {
                boolean logic = logicItem.getLogic();
                digit = digit << 1;
                digit += logic ? 1 : 0;
            } else {
                return;
            }
        }

        SlimefunItem result = DigitalNumber.getByDigit(digit);
        if (result != null && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), result.getItem())) {
            for (int slot : this.getInputSlot()) {
                ItemStack itemStack = inventory.getItem(slot);
                if (ItemStackUtil.isItemNull(itemStack)) {
                    return;
                }
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        this.registerRecipe(FinalTechItemStacks.LOGIC_TRUE, ItemStackUtil.AIR);
        this.registerRecipe(FinalTechItemStacks.LOGIC_FALSE, ItemStackUtil.AIR);
    }
}
