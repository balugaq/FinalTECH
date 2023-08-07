package io.taraxacum.finaltech.core.item.machine.electric.capacitor.expanded;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class MatrixExpandedCapacitor extends AbstractExpandedElectricCapacitor {
    private final int capacity = ConfigUtil.getOrDefaultItemSetting(2000000000, this, "capacity");
    private final int stack = ConfigUtil.getOrDefaultItemSetting(2000000000, this, "max-stack");

    public MatrixExpandedCapacitor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusL2Inventory = new StatusL2Inventory(this);
        this.statusSlot = statusL2Inventory.statusSlot;
        return statusL2Inventory;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory != null) {
            for (int slot : this.getInputSlot()) {
                ItemStack item = inventory.getItem(slot);
                if (!ItemStackUtil.isItemNull(item) && FinalTechItems.ITEM_PHONY.verifyItem(item)) {
                    String energyStack = String.valueOf(FinalTech.getLocationDataService().getLocationData(locationData, this.key));
                    FinalTech.getLocationDataService().setLocationData(locationData, this.key, StringNumberUtil.min(String.valueOf(this.stack), StringNumberUtil.add(energyStack, energyStack)));
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
        super.tick(block, slimefunItem, locationData);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getMaxStack() {
        return this.stack - 2;
    }
}
