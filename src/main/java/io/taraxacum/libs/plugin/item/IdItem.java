package io.taraxacum.libs.plugin.item;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;

public interface IdItem {

    @Nonnull
    Plugin getPlugin();

    @Nonnull
    NamespacedKey getId();

    @Nonnull
    String getName();
}
