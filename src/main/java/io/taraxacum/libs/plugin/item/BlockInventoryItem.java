package io.taraxacum.libs.plugin.item;

import io.taraxacum.libs.plugin.inventory.template.InventoryTemplate;

import javax.annotation.Nonnull;

public interface BlockInventoryItem extends IdItem {

    @Nonnull
    InventoryTemplate getInventoryTemplate();
}
