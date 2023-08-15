package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class RouteViewer extends UsableSlimefunItem implements RecipeItem {
    private final Set<String> allowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "allowed-id"));

    private final int interval = ConfigUtil.getOrDefaultItemSetting(500, this, "interval");
    private final int threshold = ConfigUtil.getOrDefaultItemSetting(2, this, "threshold");

    public RouteViewer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    /**
     * The function the item will do
     * while a player hold the item and right click.
     */
    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        Optional<Block> clickedBlock = playerRightClickEvent.getClickedBlock();
        if(clickedBlock.isPresent()) {
            Block block = clickedBlock.get();
            Location location = block.getLocation();
            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if(locationData != null && this.allowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData))) {
                String value = RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData);
                value = RouteShow.OPTION.nextOrDefaultValue(value);
                RouteShow.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, value);
            }
        }
    }

    @Override
    int getThreshold() {
        return this.threshold;
    }

    @Override
    int getInterval() {
        return this.interval;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for(String id : this.allowedId) {
            SlimefunItem slimefunItem = SlimefunItem.getById(id);
            if(slimefunItem != null) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }
}
