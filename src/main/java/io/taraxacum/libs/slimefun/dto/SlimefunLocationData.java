package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.ItemLocationData;
import org.bukkit.Location;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class SlimefunLocationData extends ItemLocationData<SlimefunItemWrapper> {
    protected final SlimefunItem slimefunItem;

    public SlimefunLocationData(@Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        super(location, SlimefunItemWrapperFactory.getWrapper(slimefunItem));
        this.slimefunItem = slimefunItem;
    }

    @Nonnull
    public String getId() {
        return this.slimefunItem.getId();
    }

    @Nonnull
    public SlimefunItem getSlimefunItem() {
        return slimefunItem;
    }
}
