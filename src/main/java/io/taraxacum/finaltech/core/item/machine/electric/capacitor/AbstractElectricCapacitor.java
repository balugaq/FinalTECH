package io.taraxacum.finaltech.core.item.machine.electric.capacitor;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusInventory;
import io.taraxacum.finaltech.core.item.machine.electric.AbstractElectricMachine;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public abstract class AbstractElectricCapacitor extends AbstractElectricMachine implements MenuUpdater {
    protected int statusSlot;

    public AbstractElectricCapacitor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusInventory statusInventory = new StatusInventory(this);
        this.statusSlot = statusInventory.statusSlot;
        return statusInventory;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null && !inventory.getViewers().isEmpty()) {
            this.updateInv(inventory, this.statusSlot, this, EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData));
        }
    }

    @Nonnull
    @Override
    public final EnergyNetComponentType getEnergyComponentType() {
        return EnergyNetComponentType.CAPACITOR;
    }
}
