package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.NormalStorageUnitInventory;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class CobbleStoneFactory extends AbstractMachine implements RecipeItem {
    private final ItemWrapper cobbleStone = new ItemWrapper(new ItemStack(Material.COBBLESTONE));

    public CobbleStoneFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new NormalStorageUnitInventory(this);
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
        if (inventory == null) {
            return;
        }

        for (int slot : this.getInputSlot()) {
            ItemStack item = inventory.getItem(slot);
            if (ItemStackUtil.isItemSimilar(item, this.cobbleStone)) {
                item.setAmount(item.getMaxStackSize());
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        this.registerRecipe(Material.COBBLESTONE, new ItemStack(Material.COBBLESTONE, ConstantTableUtil.ITEM_MAX_STACK));
    }
}
