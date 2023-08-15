package io.taraxacum.libs.plugin.is;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class ItemMetaBuilder {

    @Nullable
    private String displayName;

    @Nullable
    private String localizedName;

    @Nullable
    private List<String> lore;

    @Nullable
    private Integer customModelData;

    @Nullable
    private Map<Enchantment, Integer> enchants;

    @Nullable
    private Set<ItemFlag> itemFlags;

    @Nullable
    private Boolean unbreakable;

    @Nonnull
    private Map<NamespacedKey, Map<PersistentDataType, Object>> persistentDataContainer;

    public ItemMetaBuilder() {
        this.persistentDataContainer = new HashMap<>();
    }

    @Nonnull
    public ItemMetaBuilder displayName(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Nonnull
    public ItemMetaBuilder localizedName(@Nullable String localizedName) {
        this.localizedName = localizedName;
        return this;
    }

    @Nonnull
    public ItemMetaBuilder lore(@Nullable List<String> lore) {
        this.lore = lore;
        return this;
    }

    @Nonnull
    public ItemMetaBuilder customModelData(@Nullable Integer customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    @Nonnull
    public ItemMetaBuilder enchants(@Nullable Map<Enchantment, Integer> enchants) {
        this.enchants = enchants;
        return this;
    }

    @Nullable
    public Map<Enchantment, Integer> getEnchants() {
        return this.enchants;
    }

    @Nonnull
    public ItemMetaBuilder itemFlags(@Nullable Set<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;
        return this;
    }

    @Nullable
    public Set<ItemFlag> getItemFlags() {
        return this.itemFlags;
    }

    @Nonnull
    public ItemMetaBuilder unbreakable(@Nullable Boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public <T, Z> ItemMetaBuilder setData(@Nonnull NamespacedKey namespacedKey, @Nonnull PersistentDataType<T, Z> persistentDataType, @Nullable Z object) {
        Map<PersistentDataType, Object> persistentDataTypeObjectMap = this.persistentDataContainer.get(namespacedKey);
        if (persistentDataTypeObjectMap == null) {
            persistentDataTypeObjectMap = new HashMap<>();
        }

        if (object != null) {
            if (persistentDataType.getPrimitiveType() == object.getClass()) {
                persistentDataTypeObjectMap.put(persistentDataType, object);
                this.persistentDataContainer.put(namespacedKey, persistentDataTypeObjectMap);
            } else {
                throw new IllegalStateException("The persistentDataType.getPrimitiveType is not equal to object.getClass");
            }
        } else {
            persistentDataTypeObjectMap.remove(persistentDataType);
            if (persistentDataTypeObjectMap.isEmpty()) {
                this.persistentDataContainer.remove(namespacedKey);
            }
        }

        return this;
    }

    /**
     * Compare the item meta.
     * If one property is not existed, it will not be compared.
     * @return true if they are same
     */
    public boolean softCompare(@Nonnull ItemMeta itemMeta) {
        if (this.displayName != null) {
            if (!itemMeta.hasDisplayName() || !this.displayName.equals(itemMeta.getDisplayName())) {
                return false;
            }
        }

        if (this.localizedName != null) {
            if (!itemMeta.hasLocalizedName() || !this.localizedName.equals(itemMeta.getLocalizedName())) {
                return false;
            }
        }

        if (this.lore != null) {
            if (!itemMeta.hasLore()) {
                return false;
            }

            List<String> lore = itemMeta.getLore();
            if (lore.size() != this.lore.size()) {
                return false;
            }

            for (int i = 0; i < this.lore.size(); i++) {
                if (!this.lore.get(i).equals(lore.get(i))) {
                    return false;
                }
            }
        }

        if (this.customModelData != null) {
            if (!itemMeta.hasCustomModelData() || !this.customModelData.equals(itemMeta.getCustomModelData())) {
                return false;
            }
        }

        if (this.enchants != null) {
            if (!itemMeta.hasEnchants()) {
                return false;
            }

            Map<Enchantment, Integer> enchants = itemMeta.getEnchants();
            if (enchants.size() != this.enchants.size()) {
                return false;
            }

            for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                if (!entry.getValue().equals(enchants.get(entry.getKey()))) {
                    return false;
                }
            }
        }

        if (this.itemFlags != null) {
            Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
            if (itemFlags.size() != this.itemFlags.size()) {
                return false;
            }

            for (ItemFlag itemFlag : this.itemFlags) {
                if (!itemFlags.contains(itemFlag)) {
                    return false;
                }
            }
        }

        if (this.unbreakable != null) {
            if (!this.unbreakable.equals(itemMeta.isUnbreakable())) {
                return false;
            }
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        for (NamespacedKey namespacedKey : this.persistentDataContainer.keySet()) {
            Map<PersistentDataType, Object> persistentDataTypeObjectMap = this.persistentDataContainer.get(namespacedKey);
            for (Map.Entry<PersistentDataType, Object> persistentDataTypeObjectEntry : persistentDataTypeObjectMap.entrySet()) {
                if (!persistentDataTypeObjectEntry.getValue().equals(persistentDataContainer.get(namespacedKey, persistentDataTypeObjectEntry.getKey()))) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Compare the item meta.
     * @return true if they are same
     */
    public boolean hardCompare(@Nonnull ItemMeta itemMeta) {
        if (this.displayName != null) {
            if (!itemMeta.hasDisplayName() || !this.displayName.equals(itemMeta.getDisplayName())) {
                return false;
            }
        } else if (itemMeta.hasDisplayName()) {
            return false;
        }

        if (this.localizedName != null) {
            if (!itemMeta.hasLocalizedName() || !this.localizedName.equals(itemMeta.getLocalizedName())) {
                return false;
            }
        } else if (itemMeta.hasLocalizedName()) {
            return false;
        }

        if (this.lore != null) {
            if (!itemMeta.hasLore()) {
                return false;
            }

            List<String> lore = itemMeta.getLore();
            if (lore.size() != this.lore.size()) {
                return false;
            }

            for (int i = 0; i < this.lore.size(); i++) {
                if (!this.lore.get(i).equals(lore.get(i))) {
                    return false;
                }
            }
        } else if (itemMeta.hasLore()) {
            return false;
        }

        if (this.customModelData != null) {
            if (!itemMeta.hasCustomModelData() || !this.customModelData.equals(itemMeta.getCustomModelData())) {
                return false;
            }
        } else if (itemMeta.hasCustomModelData()) {
            return false;
        }

        if (this.enchants != null) {
            if (!itemMeta.hasEnchants()) {
                return false;
            }

            Map<Enchantment, Integer> enchants = itemMeta.getEnchants();
            if (enchants.size() != this.enchants.size()) {
                return false;
            }

            for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                if (!entry.getValue().equals(enchants.get(entry.getKey()))) {
                    return false;
                }
            }
        } else if (itemMeta.hasEnchants()) {
            return false;
        }

        if (this.itemFlags != null) {
            Set<ItemFlag> itemFlags = itemMeta.getItemFlags();
            if (itemFlags.size() != this.itemFlags.size()) {
                return false;
            }

            for (ItemFlag itemFlag : this.itemFlags) {
                if (!itemFlags.contains(itemFlag)) {
                    return false;
                }
            }
        }

        if (this.unbreakable != null) {
            if (!this.unbreakable.equals(itemMeta.isUnbreakable())) {
                return false;
            }
        } else if (itemMeta.isUnbreakable()) {
            return false;
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        for (NamespacedKey namespacedKey : this.persistentDataContainer.keySet()) {
            Map<PersistentDataType, Object> persistentDataTypeObjectMap = this.persistentDataContainer.get(namespacedKey);
            for (Map.Entry<PersistentDataType, Object> persistentDataTypeObjectEntry : persistentDataTypeObjectMap.entrySet()) {
                if (!persistentDataTypeObjectEntry.getValue().equals(persistentDataContainer.get(namespacedKey, persistentDataTypeObjectEntry.getKey()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Nullable
    public ItemMeta buildFromMaterial(@Nonnull Material material) {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        if (itemMeta != null) {
            if (this.displayName != null) {
                itemMeta.setDisplayName(this.displayName);
            }

            if (this.localizedName != null) {
                itemMeta.setLocalizedName(this.localizedName);
            }

            if (this.lore != null) {
                itemMeta.setLore(this.lore);
            }

            if (this.customModelData != null) {
                itemMeta.setCustomModelData(this.customModelData);
            }

            if (this.enchants != null) {
                for (Map.Entry<Enchantment, Integer> entry : this.enchants.entrySet()) {
                    if (entry.getValue() != null) {
                        itemMeta.addEnchant(entry.getKey(), entry.getValue(), true);
                    }
                }
            }

            if (this.itemFlags != null) {
                itemMeta.addItemFlags(this.itemFlags.toArray(new ItemFlag[0]));
            }

            if (this.unbreakable != null) {
                itemMeta.setUnbreakable(this.unbreakable);
            }

            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            for (Map.Entry<NamespacedKey, Map<PersistentDataType, Object>> entry : this.persistentDataContainer.entrySet()) {
                NamespacedKey namespacedKey = entry.getKey();
                for (Map.Entry<PersistentDataType, Object> typeObjectEntry : entry.getValue().entrySet()) {
                    if (typeObjectEntry.getKey().getPrimitiveType() == typeObjectEntry.getValue().getClass()) {
                        persistentDataContainer.set(namespacedKey, typeObjectEntry.getKey(), typeObjectEntry.getValue());
                    }
                }
            }
        }

        return itemMeta;
    }

    /**
     * PersistentDataContainer will not be stored
     */
    @Nonnull
    public static ItemMetaBuilder fromItemMeta(@Nonnull ItemMeta itemMeta) {
        ItemMetaBuilder itemMetaBuilder = new ItemMetaBuilder();

        if (itemMeta.hasDisplayName()) {
            itemMetaBuilder.displayName = itemMeta.getDisplayName();
        }

        if (itemMeta.hasLocalizedName()) {
            itemMetaBuilder.localizedName = itemMeta.getLocalizedName();
        }

        if (itemMeta.hasLore()) {
            itemMetaBuilder.lore = new ArrayList<>();
            itemMetaBuilder.lore.addAll(itemMeta.getLore());
        }

        if (itemMeta.hasCustomModelData()) {
            itemMetaBuilder.customModelData = itemMeta.getCustomModelData();
        }

        if (itemMeta.hasEnchants()) {
            itemMetaBuilder.enchants = new HashMap<>();
            itemMetaBuilder.enchants.putAll(itemMeta.getEnchants());
        }

        itemMetaBuilder.itemFlags = new HashSet<>();
        itemMetaBuilder.itemFlags.addAll(itemMeta.getItemFlags());

        if (itemMeta.isUnbreakable()) {
            itemMetaBuilder.unbreakable = itemMeta.isUnbreakable();
        }

        return itemMetaBuilder;
    }
}
