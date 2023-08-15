package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.OrderedDustFactoryStoneInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
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
@Deprecated
public class DustFactoryStone extends AbstractMachine implements RecipeItem {
    private final double sleep = ConfigUtil.getOrDefaultItemSetting(4, this, "sleep");

    public DustFactoryStone(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new OrderedDustFactoryStoneInventory(this);
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
        // TODO make it more fun!

        if (!BlockTickerUtil.subSleep(FinalTech.getLocationDataService(), locationData)) {
            return;
        }

        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory == null || InventoryUtil.slotCount(inventory, this.getInputSlot()) != this.getInputSlot().length) {
            return;
        }

        Set<Integer> amountList = new HashSet<>(this.getInputSlot().length);
        ItemWrapper firstItem = new ItemWrapper(inventory.getItem(this.getInputSlot()[0]));
        boolean allSameItem = true;

        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            amountList.add(itemStack.getAmount());
            if (allSameItem && !ItemStackUtil.isItemSimilar(firstItem, itemStack)) {
                allSameItem = false;
            }
        }

        for (int slot : this.getInputSlot()) {
            inventory.clear(slot);
        }

        if (amountList.size() == this.getInputSlot().length && allSameItem) {
            if(InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), FinalTechItems.ORDERED_DUST.getValidItem())) {
                FinalTech.getLogService().addItem(FinalTechItems.ORDERED_DUST.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
                BlockTickerUtil.setSleep(FinalTech.getLocationDataService(), locationData, this.sleep);
            }
        } else if (Math.random() < (double)(amountList.size()) / this.getInputSlot().length) {
            if(InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), FinalTechItems.UNORDERED_DUST.getValidItem())) {
                FinalTech.getLogService().addItem(FinalTechItems.UNORDERED_DUST.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
                BlockTickerUtil.setSleep(FinalTech.getLocationDataService(), locationData, this.sleep);
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.format("%.2f", 100.0 / this.getInputSlot().length));
    }
}
