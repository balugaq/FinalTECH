package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class LocationDataOption {
    public static final String CARGO_ID = "cargo";
    private static final String ERROR_VALUE = "0";

    private final String id;

    protected final String key;

    protected final List<String> valueList;

    public LocationDataOption(@Nonnull String id, @Nonnull String key, @Nonnull List<String> valueList) {
        this.id = id;
        this.key = key;
        this.valueList = valueList;
    }

    public LocationDataOption(@Nonnull SlimefunItem slimefunItem, @Nonnull String key, @Nonnull List<String> valueList) {
        this(slimefunItem.getId(), key, valueList);
    }

    @Nonnull
    public String defaultValue() {
        return this.valueList.isEmpty() ? ERROR_VALUE : this.valueList.get(0);
    }

    @Nonnull
    public String getOrDefaultValue(@Nonnull LocationDataService locationDataService, @Nonnull Location location) {
        String value = locationDataService.getLocationData(location, this.key);
        return value == null ? this.defaultValue() : value;
    }

    @Nonnull
    public String getOrDefaultValue(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        String value = locationDataService.getLocationData(locationData, this.key);
        return value == null ? this.defaultValue() : value;
    }

    public void setOrClearValue(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nullable String value) {
        locationDataService.setLocationData(location, this.key, value);
    }

    public void setOrClearValue(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData, @Nullable String value) {
        locationDataService.setLocationData(locationData, this.key, value);
    }

    @Nonnull
    public String offsetOrDefaultValue(@Nullable String value, int offset) {
        return this.valueList.contains(value) ? this.valueList.get(((this.valueList.indexOf(value) + offset) % this.valueList.size() + this.valueList.size()) % this.valueList.size()) : this.defaultValue();
    }

    @Nonnull
    public String nextOrDefaultValue(@Nullable String value) {
        return this.offsetOrDefaultValue(value, 1);
    }

    @Nonnull
    public String previousOrDefaultValue(@Nullable String value) {
        return this.offsetOrDefaultValue(value, -1);
    }

    public boolean validValue(@Nullable String value) {
        return valueList.contains(value);
    }

    public boolean checkOrSetDefault(@Nonnull LocationDataService locationDataService, @Nonnull Location location) {
        String value = locationDataService.getLocationData(location, this.key);
        if(!this.validValue(value)) {
            locationDataService.setLocationData(location, this.key, this.defaultValue());
            return false;
        }
        return true;
    }

    public boolean checkOrSetDefault(@Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        String value = locationDataService.getLocationData(locationData, this.key);
        if(!this.validValue(value)) {
            locationDataService.setLocationData(locationData, this.key, this.defaultValue());
            return false;
        }
        return true;
    }
}
