package io.taraxacum.libs.slimefun.util;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class LocationDataUtil {

    @Nullable
    public static String getId(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        if(locationData instanceof SlimefunLocationData slimefunLocationData) {
            return slimefunLocationData.getId();
        } else {
            return locationDataService.getLocationData(locationData, "id");
        }
    }

    @Nullable
    public static SlimefunItem getSlimefunItem(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        if(locationData instanceof SlimefunLocationData slimefunLocationData) {
            return slimefunLocationData.getSlimefunItem();
        } else {
            String id = locationDataService.getLocationData(locationData, "id");
            return id == null ? null : SlimefunItem.getById(id);
        }
    }
}
