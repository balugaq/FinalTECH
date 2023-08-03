package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.InfoFactoryInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.dto.ItemAmountWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class InfoFactory extends AbstractMachine {
    public InfoFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new InfoFactoryInventory(this);
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler();
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory == null || InventoryUtil.slotCount(inventory, this.getInputSlot()) != this.getInputSlot().length) {
            return;
        }

        Set<Integer> amountSet = new HashSet<>(this.getInputSlot().length);
        Set<String> materialSet = new HashSet<>(this.getInputSlot().length);

        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack)) {
                amountSet.add(itemStack.getAmount());
                SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                String name = sfItem == null ? itemStack.getType().name() : sfItem.getId();
                materialSet.add(name);
            } else {
                return;
            }
        }

        for (int slot : this.getInputSlot()) {
            inventory.clear(slot);
        }

        if (amountSet.size() > materialSet.size()) {
            int amount = InventoryUtil.tryPushItem(inventory, JavaUtil.shuffle(this.getOutputSlot()), amountSet.size(), new ItemAmountWrapper(FinalTechItems.BIT.getValidItem()));
            FinalTech.getLogService().addItem(FinalTechItems.BIT.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
        } else if (amountSet.size() < materialSet.size()) {
            int amount = InventoryUtil.tryPushItem(inventory, JavaUtil.shuffle(this.getOutputSlot()), materialSet.size(), new ItemAmountWrapper(FinalTechItems.META.getValidItem()));
            FinalTech.getLogService().addItem(FinalTechItems.BIT.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
        } else {
            int amount = InventoryUtil.tryPushItem(inventory, JavaUtil.shuffle(this.getOutputSlot()), amountSet.size(), new ItemAmountWrapper(FinalTechItems.BIT.getValidItem()));
            FinalTech.getLogService().addItem(FinalTechItems.BIT.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
            amount = InventoryUtil.tryPushItem(inventory, JavaUtil.shuffle(this.getOutputSlot()), materialSet.size(), new ItemAmountWrapper(FinalTechItems.META.getValidItem()));
            FinalTech.getLogService().addItem(FinalTechItems.BIT.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }
}
