package io.taraxacum.libs.plugin.recipe;

import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.is.SimpleItemComparator;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;

public interface CraftRecipe {

    @Nonnull
    NamespacedKey getId();

    @Nonnull
    SimpleItemComparator[] getImports();

    @Nonnull
    ItemAmountWrapper[] getExports();
}