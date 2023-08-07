package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.finaltech.util.ConfigUtil;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class OperationAccelerator extends AbstractOperationAccelerator {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final int capacity = ConfigUtil.getOrDefaultItemSetting(20000000, this, "capacity");
    private final int efficiency = ConfigUtil.getOrDefaultItemSetting(1, this, "efficiency");

    public OperationAccelerator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    @Nonnull
    Set<String> getNotAllowedId() {
        return this.notAllowedId;
    }

    @Override
    int getBaseEfficiency() {
        return 0;
    }

    @Override
    int getRandomEfficiency() {
        return this.efficiency;
    }
}
