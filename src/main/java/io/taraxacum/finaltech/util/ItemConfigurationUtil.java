package io.taraxacum.finaltech.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.option.IgnorePermission;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.libs.plugin.file.ConfigFileManager;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class ItemConfigurationUtil {
    private static boolean init = false;
    private static Set<String> allowedItemId = new HashSet<>();
    // key: itemId
    // value: group
    private static Map<String, String> itemGroupMap = new HashMap<>();
    // keu: group
    // value: set of itemId
    private static Map<String, Set<String>> groupItemMap = new HashMap<>();
    private static Map<String, Set<String>> itemForbidKeyMap = new HashMap<>();

    private static final NamespacedKey KEY_CONFIG = new NamespacedKey(FinalTech.getInstance(), "FINALTECH_CONFIGURATION");
    private static final NamespacedKey KEY_ID = new NamespacedKey(FinalTech.getInstance(), "FINALTECH_BLOCK_STORAGE_ID");

    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
    private static final Type TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private static void init() {
        Set<String> allowedItemId = new HashSet<>();
        Map<String, String> itemGroupMap = new HashMap<>();
        Map<String, Set<String>> itemForbidKeyMap = new HashMap<>();
        Map<String, Set<String>> groupItemMap = new HashMap<>();

        ConfigFileManager itemManager = FinalTech.getItemManager();
        List<String> groupList = itemManager.getStringList(FinalTechItems.MACHINE_CONFIGURATOR.getId(), "item-config");

        for(String group : groupList) {
            List<String> itemIdList = itemManager.getStringList(FinalTechItems.MACHINE_CONFIGURATOR.getId(), "item-config", group);

            for(String itemId : itemIdList) {
                allowedItemId.add(itemId);
                itemGroupMap.put(itemId, group);
                if(groupItemMap.containsKey(group)) {
                    Set<String> itemIdSet = groupItemMap.get(group);
                    itemIdSet.add(itemId);
                } else {
                    Set<String> itemIdSet = new HashSet<>();
                    itemIdSet.add(itemId);
                    groupItemMap.put(group, itemIdSet);
                }

                List<String> keyList = itemManager.getStringList(FinalTechItems.MACHINE_CONFIGURATOR.getId(), "item-config", group, itemId);
                Set<String> keySet = new HashSet<>(keyList);

                keySet.add(ConstantTableUtil.CONFIG_ID);
                keySet.add(ConstantTableUtil.CONFIG_CHARGE);
                keySet.add(ConstantTableUtil.CONFIG_UUID);
                keySet.add(IgnorePermission.KEY);

                itemForbidKeyMap.put(itemId, keySet);
            }
        }

        ItemConfigurationUtil.allowedItemId = allowedItemId;
        ItemConfigurationUtil.itemGroupMap = itemGroupMap;
        ItemConfigurationUtil.groupItemMap = groupItemMap;
        ItemConfigurationUtil.itemForbidKeyMap = itemForbidKeyMap;

        init = true;
    }

    public static Set<String> getAllowedItemId() {
        if(!init) {
            init();
        }

        return ItemConfigurationUtil.allowedItemId;
    }

    public static Map<String, Set<String>> getGroupItemMap() {
        if(!init) {
            init();
        }

        return ItemConfigurationUtil.groupItemMap;
    }

    public static boolean isAllowItemId(@Nonnull String itemId) {
        if(!init) {
            init();
        }

        return ItemConfigurationUtil.allowedItemId.contains(itemId);
    }

    public static boolean isSameGroup(@Nullable String itemId1, @Nullable String itemId2) {
        if(!init) {
            init();
        }

        String group1 = ItemConfigurationUtil.itemGroupMap.get(itemId1);
        String group2 = ItemConfigurationUtil.itemGroupMap.get(itemId2);
        return group1 != null && group1.equals(group2);
    }

    public static Map<String, String> filterByItem(@Nonnull String itemId, @Nonnull Map<String, String> map) {
        if(!init) {
            init();
        }

        Set<String> forbidKeySet = itemForbidKeyMap.get(itemId);

        if(forbidKeySet == null) {
            return new HashMap<>();
        }

        Map<String, String> resultMap = new HashMap<>();

        for(Map.Entry<String, String> entry : map.entrySet()) {
            if(!forbidKeySet.contains(entry.getKey())) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }

        return resultMap;
    }

    public static boolean saveConfigToItem(@Nonnull ItemStack itemStack, @Nonnull LocationDataService locationDataService, @Nonnull LocationData locationData) {
        String id = LocationDataUtil.getId(locationDataService, locationData);
        if(!ItemConfigurationUtil.allowedItemId.contains(id) || id == null) {
            return false;
        }

        Map<String, String> configMap = new HashMap<>();
        for (String key : locationDataService.getKeys(locationData)) {
            configMap.put(key, locationDataService.getLocationData(locationData, key));
        }

        configMap = ItemConfigurationUtil.filterByItem(id, configMap);

        ItemConfigurationUtil.setConfigurationToItem(itemStack, configMap);

        ItemConfigurationUtil.setIdToItem(itemStack, id);

        return true;
    }

    public static boolean loadConfigFromItem(@Nonnull LocationDataService locationDataService, @Nonnull ItemStack itemStack, @Nonnull LocationData locationData) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            return false;
        }
        String itemId = ItemConfigurationUtil.getIdFromItem(itemMeta);

        if(locationData == null || itemId == null || !ItemConfigurationUtil.isSameGroup(itemId, LocationDataUtil.getId(locationDataService, locationData))) {
            return false;
        }

        Map<String, String> configMap = ItemConfigurationUtil.getConfigurationFromItem(itemMeta);
        configMap = ItemConfigurationUtil.filterByItem(itemId, configMap);

        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            if(locationDataService.getLocationData(locationData, entry.getKey()) != null) {
                locationDataService.setLocationData(locationData, entry.getKey(), entry.getValue());
            }
        }

        return true;
    }

    @Nullable
    public static String getIdFromItem(@Nonnull ItemStack itemStack) {
        if(!itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return ItemConfigurationUtil.getIdFromItem(itemMeta);
    }

    @Nullable
    public static String getIdFromItem(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if(persistentDataContainer.has(KEY_ID, PersistentDataType.STRING)) {
            return persistentDataContainer.get(KEY_ID, PersistentDataType.STRING);
        }
        return null;
    }

    public static void setIdToItem(@Nonnull ItemStack itemStack, @Nonnull String id) {
        if(!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemConfigurationUtil.setIdToItem(itemMeta, id);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setIdToItem(@Nonnull ItemMeta itemMeta, @Nonnull String id) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(KEY_ID, PersistentDataType.STRING, id);
    }

    @Nonnull
    public static Map<String, String> getConfigurationFromItem(@Nonnull ItemStack itemStack) {
        if(!itemStack.hasItemMeta()) {
            return new HashMap<>();
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return ItemConfigurationUtil.getConfigurationFromItem(itemMeta);
    }

    @Nonnull
    public static Map<String, String> getConfigurationFromItem(@Nonnull ItemMeta itemMeta) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if(persistentDataContainer.has(KEY_CONFIG, PersistentDataType.STRING)) {
            String s = persistentDataContainer.get(KEY_CONFIG, PersistentDataType.STRING);
            return GSON.fromJson(s, TYPE);
        }
        return new HashMap<>();
    }

    public static void setConfigurationToItem(@Nonnull ItemStack itemStack, @Nonnull Map<String, String> map) {
        if(!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemConfigurationUtil.setConfigurationToItem(itemMeta, map);
        itemStack.setItemMeta(itemMeta);
    }

    public static void setConfigurationToItem(@Nonnull ItemMeta itemMeta, @Nonnull Map<String, String> map) {
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(KEY_CONFIG, PersistentDataType.STRING, GSON.toJson(map));
    }
}
