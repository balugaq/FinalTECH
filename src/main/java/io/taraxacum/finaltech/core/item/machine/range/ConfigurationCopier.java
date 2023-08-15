package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.*;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.ConfigurationWorkerInventory;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.*;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class ConfigurationCopier extends AbstractRangeMachine implements RecipeItem, PointMachine, LineMachine, LocationMachine {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final int range = ConfigUtil.getOrDefaultItemSetting(16, this, "range");
    private int digitSlot;

    public ConfigurationCopier(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        ConfigurationWorkerInventory configurationWorkerInventory = new ConfigurationWorkerInventory(this);
        this.digitSlot = configurationWorkerInventory.digitalSlot;
        return configurationWorkerInventory;
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
        boolean hasViewer = !inventory.getViewers().isEmpty();
        boolean drawParticle = hasViewer || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));

        if (!ItemStackUtil.isItemNull(inventory.getItem(this.getOutputSlot()[0]))) {
            return;
        }

        ItemStack itemConfigurator = inventory.getItem(this.getInputSlot()[0]);
        SlimefunItem machineConfigurator = SlimefunItem.getByItem(itemConfigurator);
        if (FinalTechItems.MACHINE_CONFIGURATOR != machineConfigurator) {
            return;
        }

        ItemStack digitalItemStack = inventory.getItem(this.digitSlot);
        int digital = SlimefunItem.getByItem(digitalItemStack) instanceof DigitalItem digitalItem ? digitalItem.getDigit() : 0;

        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            Runnable runnable = () -> {
                ItemStack outputItem = ItemStackUtil.cloneItem(itemConfigurator, 1);

                RangeFunction rangeFunction = location -> {
                    if (!location.getChunk().isLoaded()) {
                        return -1;
                    }

                    LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
                    if (tempLocationData != null) {
                        String id = LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData);
                        if (id == null || this.notAllowedId.contains(id)) {
                            return -1;
                        }

                        if (ItemConfigurationUtil.saveConfigToItem(outputItem, FinalTech.getLocationDataService(), tempLocationData)) {
                            SlimefunItem sfItem = LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData);
                            if (sfItem != null) {
                                ItemStackUtil.setLore(outputItem, sfItem.getItemName());
                            }

                            if (drawParticle) {
                                JavaPlugin javaPlugin = ConfigurationCopier.this.getAddon().getJavaPlugin();
                                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, tempLocationData.getLocation().getBlock()));
                            }
                        }
                        return -1;
                    }

                    return 0;
                };

                if (digital == 0) {
                    this.lineFunction(block, this.range, rangeFunction);
                } else {
                    this.pointFunction(block, digital, rangeFunction);
                }

                if (InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), outputItem)) {
                    itemConfigurator.setAmount(itemConfigurator.getAmount() - 1);
                }
            };

            BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), runnable, () -> {
                if (digital > 0) {
                    return new Location[] {block.getLocation(), block.getRelative(directional.getFacing(), digital).getLocation()};
                } else if (digital == 0) {
                    return this.getLocations(block.getLocation());
                } else {
                    return new Location[0];
                }
            });
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    @Nonnull
    @Override
    public Location getTargetLocation(@Nonnull Location location, int range) {
        Block block = location.getBlock();
        BlockData blockData = block.getBlockData();
        return blockData instanceof Directional directional ? block.getRelative(directional.getFacing(), range).getLocation() : location;
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        Block block = sourceLocation.getBlock();
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Directional directional) {
            BlockFace blockFace = directional.getFacing();
            Location[] locations = new Location[this.range + 1];
            int i = 0;
            locations[i++] = sourceLocation;
            while (i < locations.length) {
                locations[i++] = block.getRelative(blockFace, i).getLocation();
            }
            return locations;
        }
        return new Location[0];
    }
}
