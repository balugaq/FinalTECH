package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class LocationBlockStorageData extends SlimefunLocationData {
    private final Config config;

    public LocationBlockStorageData(@Nonnull Location location, @Nonnull Config config, @Nonnull SlimefunItem slimefunItem) {
        super(location, slimefunItem);
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }
}
