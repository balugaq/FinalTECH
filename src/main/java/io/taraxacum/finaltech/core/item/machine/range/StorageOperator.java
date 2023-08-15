package io.taraxacum.finaltech.core.item.machine.range;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.StorageOperatorInventory;
import io.taraxacum.finaltech.core.option.EnableOption;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.libs.plugin.is.ItemWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public abstract class StorageOperator extends AbstractRangeMachine implements LocationMachine, LogicInjectableItem {
    protected final String keyLocation = "l";
    protected final String keyItem = "item";
    protected final Map<Location, ItemWithLocation> locationMap;

    protected final double particleDistance = 0.25;
    protected final int particleInterval = 2;

    protected BiConsumer<Inventory, LocationData> logicInjectInventoryUpdater;

    public StorageOperator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
        this.locationMap = FinalTech.isAsyncSlimefunItem(this.getId()) ? new ConcurrentHashMap<>() : new HashMap<>();
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StorageOperatorInventory storageOperatorInventory = new StorageOperatorInventory(this);
        this.logicInjectInventoryUpdater = storageOperatorInventory::updateEnable;
        return storageOperatorInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return new BlockPlaceHandler(false) {
            @Override
            public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {
                Block block = blockPlaceEvent.getBlock();
                Location location = block.getLocation();
                LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                if (locationData != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, ConstantTableUtil.CONFIG_UUID, blockPlaceEvent.getPlayer().getUniqueId().toString());

                    EnableOption.OPTION.checkOrSetDefault(FinalTech.getLocationDataService(), locationData);
                    FinalTech.getLocationDataService().setLocationData(locationData, StorageOperator.this.keyLocation, "");
                    FinalTech.getLocationDataService().setLocationData(locationData, StorageOperator.this.keyItem, "");
                }
            }
        };
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> drops) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if (FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if (blockMenu != null && blockMenu.getPreset().getID().equals(StorageOperator.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, StorageOperator.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, StorageOperator.this.getOutputSlot());
                    }
                }

                StorageOperator.this.locationMap.remove(location);
            }
        };
    }

    @Nullable
    public ItemWithLocation getInfo(@Nonnull Location location) {
        ItemWithLocation itemWithLocation = this.locationMap.get(location);
        if (itemWithLocation == null) {
            String locationStr = FinalTech.getLocationDataService().getLocationData(location, this.keyLocation);
            String itemStr = FinalTech.getLocationDataService().getLocationData(location, this.keyItem);
            if (locationStr != null && itemStr != null && !"".equals(locationStr) && !"".equals(itemStr)) {
                Location targetLocation = LocationUtil.stringToLocation(locationStr);
                ItemStack itemStack = ItemStackUtil.stringToItemStack(itemStr);
                if (targetLocation != null && itemStack != null) {
                    itemWithLocation = new ItemWithLocation(itemStr, new ItemWrapper(itemStack), targetLocation);
                    this.locationMap.put(location, itemWithLocation);
                } else {
                    FinalTech.getLocationDataService().setLocationData(location, this.keyLocation, "");
                    FinalTech.getLocationDataService().setLocationData(location, this.keyItem, "");
                }
            }
        }
        return itemWithLocation;
    }

    @Nonnull
    public ItemWithLocation getOrGenerateInfo(@Nonnull Location location) {
        ItemWithLocation itemWithLocation = this.locationMap.get(location);
        if (itemWithLocation == null) {
            itemWithLocation = new ItemWithLocation();
            this.locationMap.put(location, itemWithLocation);
        }
        return itemWithLocation;
    }

    @Nonnull
    public String getKeyLocation() {
        return this.keyLocation;
    }

    @Nonnull
    public String getKeyItem() {
        return this.keyItem;
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        String locationStr = FinalTech.getLocationDataService().getLocationData(sourceLocation, this.getKeyLocation());
        if (locationStr == null) {
            return new Location[0];
        }
        Location location = LocationUtil.stringToLocation(locationStr);
        if (location == null) {
            return new Location[0];
        }

        return new Location[] {location};
    }

    @Override
    public void injectLogic(@Nonnull LocationData locationData, boolean logic) {
        if (logic) {
            EnableOption.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, EnableOption.VALUE_TRUE);
        } else {
            EnableOption.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, EnableOption.VALUE_FALSE);
        }

        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if (inventory != null) {
            this.logicInjectInventoryUpdater.accept(inventory, locationData);
        }
    }

    public static final class ItemWithLocation {
        String itemStr;
        private ItemWrapper itemWrapper;
        Location location;

        public ItemWithLocation() {

        }

        public ItemWithLocation(@Nonnull String itemStr, @Nonnull ItemWrapper itemWrapper, @Nonnull Location location) {
            this.itemStr = itemStr;
            this.itemWrapper = itemWrapper;
            this.location = location;
        }

        public String getItemStr() {
            return itemStr;
        }

        public void setItemStr(String itemStr) {
            this.itemStr = itemStr;
        }

        public ItemWrapper getItemWrapper() {
            return itemWrapper;
        }

        public void setItemWrapper(ItemWrapper itemWrapper) {
            this.itemWrapper = itemWrapper;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }
}
