package io.taraxacum.libs.plugin.item;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public interface StackItem extends IdItem {

    @Nonnull
    ItemStack getItemStack();
}
