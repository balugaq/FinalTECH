package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitalItem;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.DigitAdderInventory;
import io.taraxacum.finaltech.core.item.unusable.DigitalNumber;
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
public class DigitAdder extends AbstractMachine implements RecipeItem {
    public DigitAdder(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new DigitAdderInventory(this);
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

        ItemStack itemStack;
        int digit = 0;
        for (int slot : this.getInputSlot()) {
            itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                return;
            }
            if (SlimefunItem.getByItem(itemStack) instanceof DigitalItem digitalItem) {
                digit += digitalItem.getDigit();
            } else {
                return;
            }
        }

        if (digit > 15) {
            SlimefunItem digitItem = DigitalNumber.getByDigit(digit / 16);
            if (digitItem != null) {
                inventory.setItem(this.getOutputSlot()[0], digitItem.getItem());
            }
        }

        SlimefunItem digitItem = DigitalNumber.getByDigit(digit % 16);
        if (digitItem != null) {
            for (int slot : this.getInputSlot()) {
                itemStack = inventory.getItem(slot);
                if (ItemStackUtil.isItemNull(itemStack)) {
                    continue;
                }
                itemStack.setAmount(itemStack.getAmount() - 1);
            }
            inventory.setItem(this.getOutputSlot()[1], digitItem.getItem());
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (SlimefunItem slimefunItem : DigitalNumber.getAll()) {
            this.registerRecipe(slimefunItem.getItem(), ItemStackUtil.AIR);
        }
    }
}
