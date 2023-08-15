package io.taraxacum.libs.plugin.item;

import io.taraxacum.libs.plugin.ld.ItemLocationData;

import javax.annotation.Nonnull;

public interface BlockTaskItem extends IdItem {

    void task(@Nonnull ItemLocationData<IdItem> itemLocationData);
}