package io.taraxacum.libs.plugin.is;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SimpleItemComparator implements ItemComparator {
    private final List<Function<ItemLazyWrapper, Boolean>> functionList;

    private final ItemStack[] showItems;

    public SimpleItemComparator(@Nonnull ItemStack... showItems) {
        this.functionList = new ArrayList<>();
        this.showItems = showItems.length == 0 ? new ItemStack[] {new ItemStack(Material.AIR)} : showItems;
    }

    @Override
    public boolean compareTo(@Nonnull ItemLazyWrapper itemLazyWrapper) {
        for (Function<ItemLazyWrapper, Boolean> function : this.functionList) {
            if (!function.apply(itemLazyWrapper)) {
                return false;
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public ItemStack getShowItem(int time) {
        return this.showItems[time % this.showItems.length];
    }

    @Override
    public int getShowItemSize() {
        return this.showItems.length;
    }

    @SafeVarargs
    public final void addFunction(@Nonnull Function<ItemLazyWrapper, Boolean>... functions) {
        this.functionList.addAll(List.of(functions));
    }
}
