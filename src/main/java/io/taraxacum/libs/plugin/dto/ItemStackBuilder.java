package io.taraxacum.libs.plugin.dto;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStackBuilder {
    @Nonnull
    private Material material;

    @Nullable
    private Integer amount;

    @Nullable
    private ItemMetaBuilder itemMetaBuilder;


    public ItemStackBuilder(@Nonnull Material material) {
        this.material = material;
    }

    @Nonnull
    public ItemStackBuilder material(@Nonnull Material material) {
        this.material = material;
        return this;
    }

    @Nonnull
    public ItemStackBuilder amount(@Nullable Integer amount) {
        this.amount = amount;
        return this;
    }

    /**
     * PersistentDataContainer of ItemMeta will not be stored
     * @return
     */
    @Nonnull
    public ItemStackBuilder itemMeta(@Nullable ItemMeta itemMeta) {
        this.itemMetaBuilder = itemMeta == null ? null : ItemMetaBuilder.fromItemMeta(itemMeta);
        return this;
    }

    @Nullable
    public ItemMetaBuilder getItemMetaBuilder() {
        return this.itemMetaBuilder;
    }

    public boolean softCompare(@Nonnull ItemStack itemStack) {
        if (this.material != itemStack.getType()) {
            return false;
        }

        if (this.amount != null) {
            if (this.amount != itemStack.getAmount()) {
                return false;
            }
        }

        if (this.itemMetaBuilder != null) {
            if (!itemStack.hasItemMeta() || !this.itemMetaBuilder.softCompare(itemStack.getItemMeta())) {
                return false;
            }
        }

        return true;
    }

    public boolean softCompare(@Nonnull ItemWrapper itemWrapper) {
        if (this.material != itemWrapper.getItemStack().getType()) {
            return false;
        }

        if (this.amount != null) {
            if (this.amount != itemWrapper.getItemStack().getAmount()) {
                return false;
            }
        }

        if (this.itemMetaBuilder != null) {
            if (!itemWrapper.hasItemMeta() || !this.itemMetaBuilder.softCompare(itemWrapper.getItemMeta())) {
                return false;
            }
        }

        return true;
    }

    public boolean hardCompare(@Nonnull ItemStack itemStack) {
        if (this.material != itemStack.getType()) {
            return false;
        }

        if (this.amount != null) {
            if (this.amount != itemStack.getAmount()) {
                return false;
            }
        }

        if (this.itemMetaBuilder != null) {
            if (!itemStack.hasItemMeta() || !this.itemMetaBuilder.hardCompare(itemStack.getItemMeta())) {
                return false;
            }
        }

        return true;
    }

    /**
     * If not set amount, the amount will be 1.
     */
    @Nonnull
    public ItemStack build() {
        ItemStack itemStack = new ItemStack(this.material);
        if (this.amount != null) {
            itemStack.setAmount(Math.min(this.amount, itemStack.getMaxStackSize()));
        }

        if (this.itemMetaBuilder != null) {
            ItemMeta itemMeta = this.itemMetaBuilder.buildFromMaterial(this.material);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

    /**
     * PersistentDataContainer of ItemMeta will not be stored
     * @return
     */
    @Nonnull
    public static ItemStackBuilder fromItemStack(@Nonnull ItemStack itemStack) {
        ItemStackBuilder itemStackBuilder = new ItemStackBuilder(itemStack.getType());

        itemStackBuilder.amount = itemStack.getAmount();

        if (itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemStackBuilder.itemMetaBuilder = ItemMetaBuilder.fromItemMeta(itemMeta);
        }

        return itemStackBuilder;
    }
}
