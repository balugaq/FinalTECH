package io.taraxacum.finaltech.core.dto;

import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class StringItemCardCache {
    @Nonnull
    private ItemStack cardItem;

    private ItemMeta cardItemMeta;

    @Nullable
    private ItemWrapper templateStringItem;

    @Nullable
    private String amount;

    public StringItemCardCache(@Nonnull ItemStack cardItem) {
        this.cardItem = cardItem;
        this.cardItemMeta = cardItem.getItemMeta();
        if(this.cardItemMeta != null) {
            ItemStack stringItem = StringItemUtil.parseItemInCard(this.cardItemMeta);
            this.templateStringItem = ItemStackUtil.isItemNull(stringItem) ? null : new ItemWrapper(stringItem);
            this.amount = this.templateStringItem == null ? null : StringItemUtil.parseAmountInCard(this.cardItemMeta);
        } else {
            this.templateStringItem = null;
            this.amount = null;
        }
    }

    @Nonnull
    public ItemStack getCardItem() {
        return cardItem;
    }

    @Nonnull
    public ItemMeta getCardItemMeta() {
        return cardItemMeta;
    }

    @Nullable
    public ItemWrapper getTemplateStringItem() {
        return templateStringItem;
    }

    @Nullable
    public String getAmount() {
        return amount;
    }

    public void clearWithoutUpdate() {
        this.templateStringItem = null;
        this.amount = null;
    }

    public void setWithoutUpdate(@Nonnull ItemWrapper stringItemWrapper, @Nonnull String amount) {
        this.templateStringItem = stringItemWrapper;
        this.amount = amount;
    }

    public void setWithoutUpdate(@Nonnull String amount) {
        this.amount = amount;
    }

    public void setAndUpdate(@Nonnull ItemWrapper stringItemWrapper, @Nonnull String amount) {
        this.templateStringItem = stringItemWrapper;
        this.amount = amount;
        PersistentDataContainer persistentDataContainer = this.cardItemMeta.getPersistentDataContainer();
        persistentDataContainer.set(StringItemUtil.ITEM_KEY, PersistentDataType.STRING, ItemStackUtil.itemStackToString(this.templateStringItem.getItemStack()));
        persistentDataContainer.set(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING, amount);
        this.cardItem.setItemMeta(this.cardItemMeta);
    }

    public void setAndUpdate(@Nonnull String amount) {
        this.amount = amount;
        PersistentDataContainer persistentDataContainer = this.cardItemMeta.getPersistentDataContainer();
        persistentDataContainer.set(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING, amount);
        this.cardItem.setItemMeta(this.cardItemMeta);
    }

    public void updateCardItem() {
        PersistentDataContainer persistentDataContainer = this.cardItemMeta.getPersistentDataContainer();
        if(this.storeItem()) {
            persistentDataContainer.set(StringItemUtil.ITEM_KEY, PersistentDataType.STRING, ItemStackUtil.itemStackToString(this.templateStringItem.getItemStack()));
            persistentDataContainer.set(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING, amount);
        } else {
            persistentDataContainer.remove(StringItemUtil.ITEM_KEY);
            persistentDataContainer.remove(StringItemUtil.AMOUNT_KEY);
        }
        this.cardItem.setItemMeta(this.cardItemMeta);
    }

    public void updateAmountCardItem() {
        PersistentDataContainer persistentDataContainer = this.cardItemMeta.getPersistentDataContainer();
        if(this.storeItem()) {
            persistentDataContainer.set(StringItemUtil.AMOUNT_KEY, PersistentDataType.STRING, amount);
        } else {
            persistentDataContainer.remove(StringItemUtil.AMOUNT_KEY);
        }
        this.cardItem.setItemMeta(this.cardItemMeta);
    }

    public void updateCardItemMeta() {
        this.cardItem.setItemMeta(this.cardItemMeta);
    }

    public boolean storeItem() {
        return this.amount != null && this.templateStringItem != null;
    }

    public void newWrap(@Nonnull ItemStack cardItem) {
        this.cardItem = cardItem;
        this.cardItemMeta = cardItem.getItemMeta();
        if(this.cardItemMeta != null) {
            ItemStack stringItem = StringItemUtil.parseItemInCard(this.cardItemMeta);
            if(!ItemStackUtil.isItemNull(stringItem)) {
                if(this.templateStringItem != null) {
                    this.templateStringItem.newWrap(stringItem);
                } else {
                    this.templateStringItem = new ItemWrapper(stringItem);
                }
                this.amount = StringItemUtil.parseAmountInCard(this.cardItemMeta);
            } else {
                this.templateStringItem = null;
                this.amount = null;
            }
        }
    }
}