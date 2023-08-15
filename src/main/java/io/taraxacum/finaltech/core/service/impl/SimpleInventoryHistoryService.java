package io.taraxacum.finaltech.core.service.impl;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.groups.FlexItemGroup;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.core.guide.SlimefunGuideMode;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.group.MainItemGroup;
import io.taraxacum.finaltech.core.group.SubFlexItemGroup;
import io.taraxacum.finaltech.core.inventory.common.MainItemGroupInventory;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.setup.FinalTechMenus;
import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.service.InventoryHistoryService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.*;

public class SimpleInventoryHistoryService implements InventoryHistoryService {
    Map<Player, List<Object>> inventoryHistoryMap = new HashMap<>();

    @Override
    public void openLast(@Nonnull Player player) {
        List<?> list = this.inventoryHistoryMap.get(player);
        if (list != null && !list.isEmpty()) {
            Object o = list.get(0);
            if (o instanceof SimpleVirtualInventory simpleVirtualInventory) {
                this.open(player, simpleVirtualInventory);
            } else {
                this.openHome(player);
            }
        } else {
            this.openHome(player);
        }
    }

    @Override
    public void openHome(@Nonnull Player player) {
        this.inventoryHistoryMap.remove(player);
        Optional<PlayerProfile> optionalPlayerProfile = PlayerProfile.find(player);
        PlayerProfile playerProfile;
        if (optionalPlayerProfile.isPresent()) {
            playerProfile = optionalPlayerProfile.get();
        } else {
            return;
        }

        MainItemGroup mainItemGroup = new MainItemGroup(FinalTechMenus.DYNAMIC_MAIN_MENU.getKey(), Icon.MAIN_ICON, this, 0, 1);
        mainItemGroup.addTo(FinalTechMenus.MAIN_ITEM_GROUP.generateByService(this),
                FinalTechMenus.MAIN_MENU_ITEM, FinalTechMenus.MAIN_MENU_ELECTRICITY_SYSTEM, FinalTechMenus.MAIN_MENU_CARGO_SYSTEM, FinalTechMenus.MAIN_MENU_FUNCTIONAL_MACHINE, FinalTechMenus.MAIN_MENU_PRODUCTIVE_MACHINE, FinalTechMenus.MAIN_MENU_DISC);

        List<String> pluginNameList = FinalTech.getConfigManager().getStringList("menu", "allowed-plugin-name");
        Map<Plugin, List<ItemGroup>> pluginItemGroupMap = new HashMap<>();
        for (ItemGroup itemGroup : Slimefun.getRegistry().getAllItemGroups()) {
            if (!itemGroup.isRegistered() || itemGroup.getAddon() == null) {
                continue;
            }
            String pluginName = itemGroup.getAddon().getName();
            if (pluginNameList.contains(pluginName)) {
                List<ItemGroup> itemGroupList = pluginItemGroupMap.computeIfAbsent(itemGroup.getAddon().getJavaPlugin(), k -> new ArrayList<>());
                itemGroupList.add(itemGroup);
            }
        }

        for (Map.Entry<Plugin, List<ItemGroup>> entry : pluginItemGroupMap.entrySet()) {
            // TODO torch?
            SubFlexItemGroup fatherItemGroup = new SubFlexItemGroup(new NamespacedKey(FinalTech.getInstance(), entry.getKey().getName() + "_FLEX"), new ItemStack(Material.TORCH), this);
            List<ItemGroup> sonItemGroupList = new ArrayList<>();
            for (ItemGroup itemGroup : entry.getValue()) {
                SubFlexItemGroup subFlexItemGroup = new SubFlexItemGroup(new NamespacedKey(FinalTech.getInstance(), itemGroup.getKey().getKey() + "_FLEX"), itemGroup.getItem(player), this);
                if (!(itemGroup instanceof FlexItemGroup)) {
                    subFlexItemGroup.addTo(itemGroup.getItems());
                }
                sonItemGroupList.add(subFlexItemGroup);
                fatherItemGroup.addFrom(subFlexItemGroup);
            }

            mainItemGroup.addTo(fatherItemGroup, sonItemGroupList);
        }

        MainItemGroupInventory mainItemGroupInventory = new MainItemGroupInventory(player, playerProfile, SlimefunGuideMode.SURVIVAL_MODE, this, Icon.MAIN_ICON, mainItemGroup.getFatherSonItemGroupList(), 1);
        mainItemGroupInventory.drawBackAsBorder();
        mainItemGroupInventory.open(player);
    }

    @Override
    public void addToLast(@Nonnull Player player, @Nonnull Object inventoryImpl) {
        List<Object> list = this.inventoryHistoryMap.computeIfAbsent(player, k -> new LinkedList<>());
        if (list.isEmpty() || list.get(0) != inventoryImpl) {
            list.add(0, inventoryImpl);
        }
    }

    @Override
    public void removeLast(@Nonnull Player player) {
        List<Object> list = this.inventoryHistoryMap.get(player);
        if (list != null && !list.isEmpty()) {
            list.remove(0);
            if (list.isEmpty()) {
                this.inventoryHistoryMap.remove(player);
            }
        }
    }

    @Override
    public void removeThenOpenLast(@Nonnull Player player) {
        this.removeLast(player);
        this.openLast(player);
    }

    @Override
    public boolean canBeAddToLast(@Nonnull Object inventoryImpl) {
        return inventoryImpl instanceof SimpleVirtualInventory;
    }

    protected void open(@Nonnull Player player, @Nonnull SimpleVirtualInventory simpleVirtualInventory) {
        simpleVirtualInventory.open(player);
    }
}
