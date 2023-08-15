package io.taraxacum.libs.slimefun.interfaces;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.LocationBlockStorageData;
import io.taraxacum.libs.slimefun.dto.LocationDatabaseData;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public interface BeautifulEnergyProvider extends EnergyNetProvider {

    default int getGeneratedOutput(@Nonnull Location location, @Nonnull SlimefunBlockData data) {
        LocationData locationData = new LocationDatabaseData(location, data);
        return this.getGeneratedOutput(locationData);
    }

    default int getGeneratedOutput(@Nonnull Location location, @Nonnull Config data) {
        String id = data.getString("id");
        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if(slimefunItem != null) {
            LocationData locationData = new LocationBlockStorageData(location, data, id, slimefunItem);
            return this.getGeneratedOutput(locationData);
        } else {
            return 0;
        }
    }

    int getGeneratedOutput(@Nonnull LocationData locationData);

    default boolean willExplode(@Nonnull Location l, @Nonnull SlimefunBlockData data) {
        return false;
    }

    default boolean willExplode(@Nonnull Location l, @Nonnull Config data) {
        return false;
    }
}
