package io.taraxacum.libs.plugin.is;

import io.taraxacum.common.pref.LazyCacheValue;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Including an itemStack.
 * We will get its itemMeta when we need it.
 * The itemMeta we got will be cached, and will be got in next time.
 * @author Final_ROOT
 */
public class ItemLazyWrapper {
    @Nonnull
    private final ItemStack itemStack;

    @Nonnull
    private final LazyCacheValue<ItemMeta> itemMetaCache;

    public static final ItemStack AIR = new ItemStack(Material.AIR);

    public ItemLazyWrapper() {
        this.itemStack = AIR;
        this.itemMetaCache = new LazyCacheValue<>(() -> null);
    }

    public ItemLazyWrapper(@Nonnull ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMetaCache = new LazyCacheValue<>(() -> this.itemStack.hasItemMeta() ? this.itemStack.getItemMeta() : null);
    }

    public ItemLazyWrapper(@Nonnull ItemStack itemStack, @Nullable ItemMeta itemMeta) {
        this.itemStack = itemStack;
        this.itemMetaCache = new LazyCacheValue<>(() -> itemMeta);
    }

    @Nonnull
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Nullable
    public ItemMeta getItemMeta() {
        return this.itemMetaCache.getResult();
    }

    public boolean hasItemMeta() {
        return this.itemMetaCache.getResult() != null;
    }

    @Nullable
    public ItemMeta reGetItemMeta() {
        return this.itemMetaCache.resetAndGetResult();
    }

    public void updateItemMeta() {
        this.itemStack.setItemMeta(this.itemMetaCache.getResult());
    }

    public void setItemMeta(@Nonnull ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);
        this.itemMetaCache.reset(() -> itemMeta);
    }

    @Override
    public int hashCode() {
        int hash = 31 + this.itemStack.getType().hashCode();
        hash = hash * 31 + this.itemStack.getAmount();
        hash = hash * 31 + (this.itemStack.getDurability() & 0xffff);
        hash = hash * 31 + (this.itemMetaCache.getResult() != null ? (this.itemMetaCache.getResult().hashCode()) : 0);
        return hash;
    }

    @Override
    public boolean equals(@Nonnull Object obj) {
        if (obj instanceof ItemLazyWrapper itemLazyWrapper) {
            return this.itemStack.equals(itemLazyWrapper.itemStack);
        }

        return false;
    }

    @Nonnull
    public static ItemStack[] getItemArray(@Nonnull ItemLazyWrapper[] itemWrappers) {
        ItemStack[] itemStacks = new ItemStack[itemWrappers.length];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = itemWrappers[i].getItemStack();
        }
        return itemStacks;
    }
    @Nonnull
    public static ItemStack[] getItemArray(@Nonnull List<? extends ItemLazyWrapper> itemWrapperList) {
        ItemStack[] itemStacks = new ItemStack[itemWrapperList.size()];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = itemWrapperList.get(i).getItemStack();
        }
        return itemStacks;
    }
    @Nonnull
    public static ItemStack[] getCopiedItemArray(@Nonnull List<? extends ItemLazyWrapper> itemWrapperList) {
        ItemStack[] itemStacks = new ItemStack[itemWrapperList.size()];
        for (int i = 0, length = itemStacks.length; i < length; i++) {
            itemStacks[i] = ItemStackUtil.cloneItem(itemWrapperList.get(i).getItemStack());
        }
        return itemStacks;
    }

    @Nonnull
    public static List<ItemStack> getItemList(@Nonnull ItemLazyWrapper[] itemWrappers) {
        List<ItemStack> itemStackList = new ArrayList<>(itemWrappers.length);
        for (ItemLazyWrapper itemWrapper : itemWrappers) {
            itemStackList.add(itemWrapper.getItemStack());
        }
        return itemStackList;
    }
    @Nonnull
    public static List<ItemStack> getItemList(@Nonnull List<? extends ItemLazyWrapper> itemWrapperList) {
        List<ItemStack> itemStackList = new ArrayList<>(itemWrapperList.size());
        for (ItemLazyWrapper itemWrapper : itemWrapperList) {
            itemStackList.add(itemWrapper.getItemStack());
        }
        return itemStackList;
    }
}
