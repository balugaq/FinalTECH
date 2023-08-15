package io.taraxacum.libs.plugin.ld;

import io.taraxacum.libs.plugin.item.IdItem;
import org.bukkit.Location;

import javax.annotation.Nonnull;

public class ItemLocationData<T extends IdItem> extends IdLocationData {
    private final T idItem;

    public ItemLocationData(@Nonnull Location location, @Nonnull T idItem) {
        super(location, idItem.getId().getKey());
        this.idItem = idItem;
    }

    @Nonnull
    public T getIdItem() {
        return this.idItem;
    }
}
