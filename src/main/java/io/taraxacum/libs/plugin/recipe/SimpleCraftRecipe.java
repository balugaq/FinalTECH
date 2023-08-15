package io.taraxacum.libs.plugin.recipe;

import io.taraxacum.libs.plugin.is.ItemAmountWrapper;

import javax.annotation.Nonnull;

public interface SimpleCraftRecipe extends CraftRecipe {
    @Nonnull
    ItemAmountWrapper getResult();

    @Nonnull
    @Override
    default ItemAmountWrapper[] getExports() {
        return new ItemAmountWrapper[] {this.getResult()};
    }
}
