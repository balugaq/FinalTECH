package io.taraxacum.finaltech.core.item.machine.network;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNet;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.SadEnergyRegularInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.core.networks.AlteredEnergyNet;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class SadEnergyRegulator extends AbstractMachine implements MenuUpdater {
    private int statusSlot;
    public SadEnergyRegulator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        SadEnergyRegularInventory statusInventory = new SadEnergyRegularInventory(this);
        this.statusSlot = statusInventory.statusSlot;
        return statusInventory;
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
        // todo async

        Location location = block.getLocation();
        EnergyNet energyNetwork = AlteredEnergyNet.getNetworkFromLocationOrCreate(location);
        if (energyNetwork instanceof AlteredEnergyNet alteredEnergyNet) {
            alteredEnergyNet.tick(locationData, (inventory, summary) -> {
                this.updateInv(inventory, this.statusSlot, slimefunItem,
                        String.valueOf(summary.getConsumerAmount()),
                        String.valueOf(summary.getConsumerEnergy()),
                        String.valueOf(summary.getConsumerCapacity()),
                        String.valueOf(summary.getGeneratorAmount()),
                        String.valueOf(summary.getGeneratorEnergy()),
                        String.valueOf(summary.getGeneratorCapacity()),
                        String.valueOf(summary.getCapacitorAmount()),
                        String.valueOf(summary.getCapacitorEnergy()),
                        String.valueOf(summary.getCapacitorCapacity()),
                        String.valueOf(summary.getGeneratedEnergy()),
                        String.valueOf(summary.getTransferredEnergy()));
            }, block, slimefunItem);
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void uniqueTick() {
        AlteredEnergyNet.uniqueTick();
    }
}
