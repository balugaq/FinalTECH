package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.item.machine.manual.storage.AbstractStorageMachine;
import io.taraxacum.finaltech.core.option.EnableOption;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Final_ROOT
 */
public class StorageImporter extends StorageOperator implements RecipeItem {
    public StorageImporter(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        String enable = EnableOption.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
        if (EnableOption.VALUE_FALSE.equals(enable)) {
            return;
        }

        ItemWithLocation itemWithLocation = this.getInfo(locationData.getLocation());
        if (itemWithLocation == null) {
            return;
        }

        Location targetLocation = itemWithLocation.location;
        String itemString = itemWithLocation.itemStr;
        if (targetLocation == null || itemString == null) {
            return;
        }

        LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(targetLocation);
        if (tempLocationData == null
                || !(LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof AbstractStorageMachine abstractStorageMachine)) {
            return;
        }

        Runnable runnable = () -> {
            if (this.getInfo(locationData.getLocation()) != itemWithLocation) {
                return;
            }

            Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
            if(inventory == null || InventoryUtil.slotCount(inventory, this.getInputSlot()) < this.getInputSlot().length / 2) {
                return;
            }

            LocationData targetLocationData = FinalTech.getLocationDataService().getLocationData(targetLocation);
            if (targetLocationData == null
                    || !abstractStorageMachine.getId().equals(LocationDataUtil.getId(FinalTech.getLocationDataService(), targetLocationData))) {
                return;
            }

            Map<String, AbstractStorageMachine.ItemWithAmount> itemMap = abstractStorageMachine.getItemMap(targetLocationData);
            abstractStorageMachine.input(itemMap, targetLocationData, itemString, inventory, this.getInputSlot());

            boolean drawParticle = !inventory.getViewers().isEmpty() || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
            if (drawParticle) {
                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, targetLocation.getBlock()));
                if (FinalTech.getSlimefunTickCount() % this.particleInterval == 0) {
                    List<Location> locationList = new ArrayList<>();
                    locationList.add(LocationUtil.getCenterLocation(targetLocation.getBlock()));
                    locationList.add(LocationUtil.getCenterLocation(block));
                    final List<Location> finalLocationList = JavaUtil.reserve(locationList);
                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.CRIT_MAGIC, this.particleInterval * Slimefun.getTickerTask().getTickRate() * 50L, this.particleDistance, finalLocationList));
                }
            }
        };

        BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(abstractStorageMachine.getId()), runnable, locationData.getLocation(), targetLocation);
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
