package io.taraxacum.libs.plugin.is;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * We could use this to compare itemStack easily
 * @author Final_ROOT
 */
public interface ItemComparator {
    default boolean compareTo(@Nonnull ItemStack itemStack) {
        return this.compareTo(new ItemLazyWrapper(itemStack));
    }

    boolean compareTo(@Nonnull ItemLazyWrapper itemLazyWrapper);

    /**
     * It may be shown to player, so we need this method
     * @return the itemStack to show to player
     */
    @Nonnull
    default ItemStack getShowItem() {
        return this.getShowItem(0);
    }

    /**
     * We may show itemStack dynamically
     * @param time  {@link #getShowItemSize()}
     * @return the itemStack to show to player
     */
    @Nonnull
    ItemStack getShowItem(int time);

    /**
     * @return How many items we can show
     */
    int getShowItemSize();
}
