package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class EntropySeed extends AbstractMachine implements RecipeItem {
    private final double equivalentConceptLife = ConfigUtil.getOrDefaultItemSetting(8.0, this, "life");
    private final int equivalentConceptRange = ConfigUtil.getOrDefaultItemSetting(2, this, "range");
    private final String key = "key";
    private final String value = "value";

    public EntropySeed(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return null;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent e) {
                Location location = e.getBlock().getLocation();
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if(locationData != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, EntropySeed.this.key, EntropySeed.this.value);
                }
            }
        };
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                blockBreakEvent.setDropItems(false);
                drops.clear();
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        // TODO optimization

        Location location = block.getLocation();
        if (this.value.equals(FinalTech.getLocationDataService().getLocationData(locationData, this.key))) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.key, null);
            FinalTech.getLocationDataService().clearLocationData(location);
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
                if (location.getChunk().isLoaded()
                        && location.getBlock().getType().equals(this.getItem().getType())
                        && FinalTech.getLocationDataService().getLocationData(location) == null
                        && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    LocationData tempLocationdata = slimefunLocationDataService.getOrCreateEmptyLocationData(location, FinalTechItemStacks.EQUIVALENT_CONCEPT.getItemId());
                    FinalTech.getLocationDataService().setLocationData(tempLocationdata, FinalTechItems.EQUIVALENT_CONCEPT.keyLife, String.valueOf(this.equivalentConceptLife));
                    FinalTech.getLocationDataService().setLocationData(tempLocationdata, FinalTechItems.EQUIVALENT_CONCEPT.keyRange, String.valueOf(this.equivalentConceptRange));
                }
            }, Slimefun.getTickerTask().getTickRate() + 1);
        } else {
            FinalTech.getLocationDataService().clearLocationData(location);
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> {
                if (location.getChunk().isLoaded()
                        && location.getBlock().getType().equals(this.getItem().getType())
                        && FinalTech.getLocationDataService().getLocationData(location) == null
                        && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    slimefunLocationDataService.getOrCreateEmptyLocationData(location, FinalTechItemStacks.JUSTIFIABILITY.getItemId());
                }
            }, Slimefun.getTickerTask().getTickRate() + 1);
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
}
