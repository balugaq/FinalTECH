package io.taraxacum.libs.plugin.ld;

import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This service will handle the data related to the location.
 * It is similar to the Slimefun {@link me.mrCookieSlime.Slimefun.api.BlockStorage}.
 * And may be used without Slimefun in the future.
 * @author Final_ROOT
 */
public interface LocationDataService<T extends LocationData> {

    /**
     * It will {@link #getLocationData(Location)} if location data is null.
     */
    @Nullable
    default String getLocationData(@Nonnull Location location, @Nonnull String key) {
        T locationData = this.getLocationData(location);
        return locationData == null ? null : this.getLocationData(locationData, key);
    }

    @Nullable
    String getLocationData(@Nonnull T locationData, @Nonnull String key);

    /**
     * @return empty set instead of null
     */
    @Nonnull
    default Set<String> getKeys(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData == null ? new HashSet<>() : this.getKeys(locationData);
    }

    @Nonnull
    Set<String> getKeys(@Nonnull T locationData);

    /**
     * It will {@link #getLocationData(Location)} if location data is null.
     * If location data is still null, it will just return.
     * @param value input null will remove the key.
     */
    default void setLocationData(@Nonnull Location location, @Nonnull String key, @Nullable String value) {
        T locationData = this.getLocationData(location);
        if(locationData != null) {
            this.setLocationData(locationData, key, value);
        }
    }

    void setLocationData(@Nonnull T locationData, @Nonnull String key, @Nullable String value);

    /**
     * It will set or replace the location data.
     */
    void setLocationData(@Nonnull T locationData);

    @Nullable
    Inventory getRawInventory(@Nonnull T locationData);

    @Nullable
    default Inventory getRawInventory(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData == null ? null : this.getRawInventory(locationData);
    }

    @Nullable
    InventoryTemplate getInventory(@Nonnull T locationData);

    @Nullable
    T getLocationData(@Nonnull Location location);

    @Nonnull
    T getOrCreateEmptyLocationData(@Nonnull Location location);

    @Nonnull
    T createLocationData(@Nonnull Location location, @Nonnull Map<String, Object> map);

    void clearLocationData(@Nonnull Location location);
}
