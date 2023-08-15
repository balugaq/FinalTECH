package io.taraxacum.libs.slimefun.service;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.ItemLocationDataService;
import io.taraxacum.libs.slimefun.dto.SlimefunItemWrapper;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SlimefunLocationDataService<T extends SlimefunLocationData> extends ItemLocationDataService<SlimefunItemWrapper, T> {

    @Nonnull
    T getOrCreateEmptyLocationData(@Nonnull Location location, @Nonnull String slimefunItemId);

    @Nullable
    @Deprecated
    default SlimefunItem getSlimefunItem(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData == null ? null : locationData.getSlimefunItem();
    }

    @Nullable
    @Deprecated
    default BlockMenu getBlockMenu(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData == null ? null : this.getBlockMenu(locationData);
    }

    @Nullable
    BlockMenu getBlockMenu(@Nonnull T locationData);
}
