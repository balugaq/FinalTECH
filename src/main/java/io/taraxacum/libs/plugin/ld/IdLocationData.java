package io.taraxacum.libs.plugin.ld;

import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class IdLocationData implements LocationData {
    private final String id;

    private Location location;

    public IdLocationData(@Nonnull Location location, @Nonnull String id) {
        this.location = location;
        this.id = id;
    }

    @Nonnull
    @Override
    public Location getLocation() {
        return this.location;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public void cloneLocation() {
        this.location = this.location.clone();
    }
}
