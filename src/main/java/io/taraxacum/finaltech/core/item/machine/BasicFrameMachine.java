package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.dto.SimpleCargoDTO;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.limit.BasicFrameMachineInventory;
import io.taraxacum.finaltech.core.option.CargoFilter;
import io.taraxacum.finaltech.core.option.CargoLimit;
import io.taraxacum.finaltech.core.option.SlotSearchOrder;
import io.taraxacum.finaltech.core.option.SlotSearchSize;
import io.taraxacum.finaltech.util.CargoUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class BasicFrameMachine extends AbstractMachine implements RecipeItem {
    public BasicFrameMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new BasicFrameMachineInventory(this);
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

        InventoryUtil.stockSlots(inventory, this.getInputSlot());
        CargoUtil.doSimpleCargoStrongSymmetry(new SimpleCargoDTO(FinalTech.getLocationDataService(),
                new InvWithSlots(inventory, this.getInputSlot()),
                block,
                SlotSearchSize.VALUE_INPUTS_ONLY,
                SlotSearchOrder.VALUE_ASCENT,
                new InvWithSlots(inventory, this.getOutputSlot()),
                block,
                SlotSearchSize.VALUE_OUTPUTS_ONLY,
                SlotSearchOrder.VALUE_ASCENT,
                this.getInputSlot().length * 64,
                CargoLimit.VALUE_ALL,
                CargoFilter.VALUE_BLACK,
                inventory,
                new int[0]));
        InventoryUtil.stockSlots(inventory, this.getOutputSlot());
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
