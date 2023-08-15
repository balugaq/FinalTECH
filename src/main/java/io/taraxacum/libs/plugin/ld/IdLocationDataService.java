package io.taraxacum.libs.plugin.ld;

import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IdLocationDataService<T extends IdLocationData> extends LocationDataService<T> {
    @Nullable
    default String getId(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData != null ? locationData.getId() : null;
    }
}
