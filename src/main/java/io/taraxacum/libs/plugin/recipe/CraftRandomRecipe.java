package io.taraxacum.libs.plugin.recipe;

import io.taraxacum.libs.plugin.is.ItemAmountWrapper;

import javax.annotation.Nonnull;

/**
 * The exports of it may be random.
 */
public interface CraftRandomRecipe extends CraftRecipe {
    @Nonnull
    ItemAmountWrapper[][] getAllExports();
}