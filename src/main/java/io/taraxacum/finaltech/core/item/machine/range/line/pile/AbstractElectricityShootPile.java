package io.taraxacum.finaltech.core.item.machine.range.line.pile;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusInventory;
import io.taraxacum.finaltech.core.item.machine.range.AbstractRangeMachine;
import io.taraxacum.finaltech.core.item.machine.range.line.AbstractLineMachine;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public abstract class AbstractElectricityShootPile extends AbstractLineMachine implements RecipeItem, MenuUpdater, LocationMachine {
    protected final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private int statusSlot;

    public AbstractElectricityShootPile(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
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
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            Runnable runnable = () -> {
                Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
                if (inventory == null) {
                    return;
                }
                boolean hasViewer = !inventory.getViewers().isEmpty() || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

                int count = 0;
                Summary summary = new Summary(hasViewer || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData)));
                LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(block.getRelative(directional.getFacing().getOppositeFace()).getLocation());
                if(tempLocationData != null
                        && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                        && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof EnergyNetComponent energyNetComponent
                        && JavaUtil.matchOnce(energyNetComponent.getEnergyComponentType(), EnergyNetComponentType.CAPACITOR, EnergyNetComponentType.GENERATOR)) {
                    int capacitorEnergy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), tempLocationData));
                    summary.capacitorEnergy = capacitorEnergy;

                    count = this.lineFunction(block, this.getRange(), this.doFunction(summary));

                    if(capacitorEnergy != summary.capacitorEnergy) {
                        EnergyUtil.setCharge(FinalTech.getLocationDataService(), tempLocationData, String.valueOf(summary.capacitorEnergy));
                    }
                }

                if (hasViewer) {
                    this.updateInv(inventory, this.statusSlot, this,
                            String.valueOf(count),
                            summary.getEnergyCharge(),
                            String.valueOf(summary.capacitorEnergy));
                }
            };

            BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), runnable, () -> this.getLocations(block.getLocation()));
        }
    }

    @Override
    protected final boolean isSynchronized() {
        return false;
    }

    protected void updateInv(@Nonnull Inventory inventory, int count, @Nonnull Summary summary) {
        ItemStack itemStack = inventory.getItem(this.statusSlot);
        ItemStackUtil.setLore(itemStack, ConfigUtil.getStatusMenuLore(FinalTech.getLanguageManager(), this,
                String.valueOf(count),
                summary.getEnergyCharge()));
    }

    public abstract int getRange();

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        Block block = sourceLocation.getBlock();
        BlockData blockData = block.getBlockData();
        if(blockData instanceof Directional directional) {
            BlockFace blockFace = directional.getFacing();
            Location[] locations = new Location[this.getRange() + 1];
            int i = 0;
            locations[i++] = sourceLocation;
            while (i < locations.length) {
                locations[i++] = block.getRelative(blockFace, i - 1).getLocation();
            }
            return locations;
        }
        return new Location[0];
    }

    @Nonnull
    protected abstract AbstractRangeMachine.RangeFunction doFunction(@Nonnull Summary summary);

    protected static class Summary {
        private String energyCharge;
        private int capacitorEnergy;

        private final boolean drawParticle;

        Summary(boolean drawParticle) {
            this.drawParticle = drawParticle;
            this.energyCharge = StringNumberUtil.ZERO;
        }

        public String getEnergyCharge() {
            return energyCharge;
        }

        public void setEnergyCharge(String energyCharge) {
            this.energyCharge = energyCharge;
        }

        public int getCapacitorEnergy() {
            return capacitorEnergy;
        }

        public void setCapacitorEnergy(int capacitorEnergy) {
            this.capacitorEnergy = capacitorEnergy;
        }

        public boolean isDrawParticle() {
            return drawParticle;
        }
    }
}
