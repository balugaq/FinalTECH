package io.taraxacum.libs.plugin.ld;

import io.taraxacum.libs.plugin.item.IdItem;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ItemLocationDataService<V extends IdItem, T extends ItemLocationData<V>> extends IdLocationDataService<T> {
    @Nullable
    default V getIdItem(@Nonnull Location location) {
        T locationData = this.getLocationData(location);
        return locationData != null ? locationData.getIdItem() : null;
    }
}
