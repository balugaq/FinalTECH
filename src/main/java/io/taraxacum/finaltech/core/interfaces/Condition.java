package io.taraxacum.finaltech.core.interfaces;

import io.taraxacum.finaltech.core.interfaces.impl.SimpleSpecialResearch;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @see SimpleSpecialResearch
 */
public interface Condition {

    @Nonnull
    List<String> getSuccessLore(@Nonnull Player player);

    @Nonnull
    List<String> getSuccessChat(@Nonnull Player player);

    @Nonnull
    List<String> getFailLore(@Nonnull Player player);

    @Nonnull
    List<String> getFailChat(@Nonnull Player player);

    boolean match(@Nonnull Player player);

    void afterResearch(@Nonnull Player player);
}
