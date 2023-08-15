package io.taraxacum.finaltech.core.item.machine.logic;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.LogicComparatorInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public abstract class AbstractLogicComparator extends AbstractMachine {
    public AbstractLogicComparator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new LogicComparatorInventory(this);
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

        if(InventoryUtil.slotCount(inventory, this.getOutputSlot()) == this.getOutputSlot().length) {
            return;
        }

        ItemStack item1 = inventory.getItem(this.getInputSlot()[0]);
        ItemStack item2 = inventory.getItem(this.getInputSlot()[1]);

        if(this.preCompare(item1, item2)) {
            if(this.compare(item1, item2)) {
                InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), this.resultTrue());
            } else {
                InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), this.resultFalse());
            }
            for(int slot : this.getInputSlot()) {
                inventory.clear(slot);
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    protected abstract boolean preCompare(@Nullable ItemStack item1, @Nullable ItemStack item2);

    protected abstract boolean compare(@Nullable ItemStack item1, @Nullable ItemStack item2);

    @Nonnull
    protected abstract ItemStack resultTrue();

    @Nonnull
    protected abstract ItemStack resultFalse();
}
