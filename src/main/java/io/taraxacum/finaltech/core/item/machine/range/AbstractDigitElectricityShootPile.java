package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.*;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Final_ROOT
 */
public abstract class AbstractDigitElectricityShootPile extends AbstractRangeMachine implements RecipeItem, MenuUpdater, PointMachine, LineMachine, LocationMachine {
    private final int range = ConfigUtil.getOrDefaultItemSetting(16, this, "range");
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    protected int statusSlot;

    public AbstractDigitElectricityShootPile(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusL2Inventory = new StatusL2Inventory(this);
        this.statusSlot = statusL2Inventory.statusSlot;
        return statusL2Inventory;
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
        int digit = this.getDigit(locationData);
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory == null) {
            return;
        }
        boolean hasViewer = !inventory.getViewers().isEmpty();

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional && digit != -1) {
            boolean drawParticle = hasViewer || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
            Runnable runnable = () -> {
                int capacitorEnergy = 0;
                AtomicInteger transferEnergy = new AtomicInteger(0);

                LocationData capacitorLocationData = FinalTech.getLocationDataService().getLocationData(block.getRelative(directional.getFacing().getOppositeFace()).getLocation());
                if (capacitorLocationData != null
                        && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), capacitorLocationData) instanceof EnergyNetComponent energyNetComponent
                        && JavaUtil.matchOnce(energyNetComponent.getEnergyComponentType(), EnergyNetComponentType.CAPACITOR, EnergyNetComponentType.GENERATOR)) {
                    capacitorEnergy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), capacitorLocationData));
                }

                if (capacitorEnergy == 0) {
                    if (hasViewer) {
                        this.updateInv(inventory, this.statusSlot, this,
                                String.valueOf(capacitorEnergy),
                                String.valueOf(transferEnergy));
                    }
                    return;
                }

                RangeMachine.RangeFunction rangeFunction = location -> {
                    if (!capacitorLocationData.getLocation().getChunk().isLoaded() || !location.getChunk().isLoaded()) {
                        return -1;
                    }

                    int capacitorEnergyNow = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), capacitorLocationData));
                    if (capacitorEnergyNow <= 0) {
                        return -1;
                    }

                    LocationData energyLocationData = FinalTech.getLocationDataService().getLocationData(location);
                    if (energyLocationData != null
                            && !notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), energyLocationData))
                            && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), energyLocationData) instanceof EnergyNetComponent energyNetComponent
                            && !EnergyNetComponentType.NONE.equals(energyNetComponent.getEnergyComponentType())) {
                        int componentCapacity = energyNetComponent.getCapacity();
                        if (componentCapacity > 0) {
                            int componentEnergy = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), energyLocationData));
                            if (componentEnergy < componentCapacity) {
                                transferEnergy.set(Math.min(capacitorEnergyNow, componentCapacity - componentEnergy));
                                capacitorEnergyNow = capacitorEnergyNow - transferEnergy.get();
                                EnergyUtil.setCharge(FinalTech.getLocationDataService(), energyLocationData, String.valueOf(componentEnergy + transferEnergy.get()));
                                EnergyUtil.setCharge(FinalTech.getLocationDataService(), capacitorLocationData, String.valueOf(capacitorEnergyNow));
                            }

                            if (drawParticle) {
                                final Location finalLocation = location.clone();
                                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, finalLocation.getBlock()));
                            }
                            return -1;
                        }
                    }

                    return 0;
                };

                if (capacitorEnergy > 0) {
                    if (digit == 0) {
                        this.lineFunction(block, this.range, directional.getFacing(), rangeFunction);
                    } else {
                        this.pointFunction(block, digit, rangeFunction);
                    }
                }

                if (hasViewer) {
                    this.updateInv(inventory, this.statusSlot, this,
                            String.valueOf(capacitorEnergy),
                            String.valueOf(transferEnergy));
                }
            };

            BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), runnable, () -> {
                if (digit > 0) {
                    Location[] locations = new Location[3];
                    locations[0] = block.getRelative(directional.getFacing().getOppositeFace()).getLocation();
                    locations[1] = block.getLocation();
                    locations[2] = block.getRelative(directional.getFacing(), digit).getLocation();
                    return locations;
                } else if (digit == 0) {
                    return this.getLocations(block.getLocation());
                } else {
                    return new Location[0];
                }
            });
        } else if (hasViewer) {
            this.updateInv(inventory, this.statusSlot, this,
                    "0",
                    "0");
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        Block block = sourceLocation.getBlock();
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            BlockFace blockFace = directional.getFacing();
            Location[] locations = new Location[this.range + 2];
            int i = 0;
            locations[i++] = block.getRelative(blockFace.getOppositeFace()).getLocation();
            locations[i++] = sourceLocation;
            while (i < locations.length) {
                locations[i++] = block.getRelative(blockFace, i - 2).getLocation();
            }
            return locations;
        }
        return new Location[0];
    }

    @Nonnull
    @Override
    public Location getTargetLocation(@Nonnull Location location, int range) {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            return block.getRelative(directional.getFacing(), range).getLocation();
        }
        return location;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    /**
     * @return -1 if we can do nothing
     */
    protected abstract int getDigit(@Nonnull LocationData locationData);
}
