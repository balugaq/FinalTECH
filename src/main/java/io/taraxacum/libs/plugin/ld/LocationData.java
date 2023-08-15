package io.taraxacum.libs.plugin.ld;

import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * The data is bound with location.
 * @author Final_ROOT
 */
public interface LocationData {

    @Nonnull
    Location getLocation();
}