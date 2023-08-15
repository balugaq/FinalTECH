package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusInventory;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public abstract class AbstractChargeBase extends AbstractFaceMachine implements MenuUpdater, LocationMachine {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    protected int statusSlot;

    public AbstractChargeBase(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusInventory statusInventory = new StatusInventory(this);
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
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        this.pointFunction(block, 1, location -> {
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
            if (tempLocationData != null ) {
                String id = LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData);
                if (id != null && !this.notAllowedId.contains(id)) {
                    BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(id), () -> this.doCharge(block, tempLocationData), locationData.getLocation(), location);
                    return 0;
                }
            }

            Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
            if (inventory != null && !inventory.getViewers().isEmpty()) {
                this.updateInv(inventory, this.statusSlot, this,
                        "0",
                        "0");
            }
            return 0;
        });
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Nonnull
    @Override
    protected BlockFace getBlockFace() {
        return BlockFace.UP;
    }

    protected abstract void doCharge(@Nonnull Block block, @Nonnull LocationData locationData);

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        return new Location[] {this.getTargetLocation(sourceLocation, 1)};
    }
}
