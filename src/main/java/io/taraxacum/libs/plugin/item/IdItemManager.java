package io.taraxacum.libs.plugin.item;

import javax.annotation.Nonnull;

public interface IdItemManager<T extends IdItem> {

    T getById(@Nonnull String id);

    /**
     * @return true if register successfully
     */
    boolean registerItem(@Nonnull T idItem);

    /**
     * @return how many items are registered
     */
    int size();


}
