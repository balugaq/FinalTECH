package io.taraxacum.libs.slimefun.interfaces;

import io.taraxacum.libs.plugin.is.ItemWrapper;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public interface ValidItem {
    boolean verifyItem(@Nonnull ItemStack itemStack);

    boolean verifyItem(@Nonnull ItemWrapper itemWrapper);
}
