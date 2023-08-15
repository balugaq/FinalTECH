package io.taraxacum.libs.slimefun.service.impl;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.LocationDatabaseData;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class may be updated in the future.
 * This class is only used for the forked version of Slimefun by GuGuProject.
 * @see BlockDataController
 * @author Final_ROOT
 */
public class DatabaseDataService implements SlimefunLocationDataService {
    private final BlockDataController blockDataController;

    public DatabaseDataService(@Nonnull BlockDataController blockDataController) {
        this.blockDataController = blockDataController;
    }

    @Nullable
    @Override
    public String getLocationData(@Nonnull Location location, @Nonnull String key) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return slimefunBlockData == null ? null : slimefunBlockData.getData(key);
    }

    @Nullable
    @Override
    public String getLocationData(@Nonnull LocationData locationData, @Nonnull String key) {
        return ((LocationDatabaseData) locationData).getSlimefunBlockData().getData(key);
    }

    @Nonnull
    @Override
    public Set<String> getKeys(@Nonnull Location location) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return slimefunBlockData == null ? new HashSet<>() : slimefunBlockData.getDataKeys();
    }

    @Nonnull
    @Override
    public Set<String> getKeys(@Nonnull LocationData locationData) {
        return ((LocationDatabaseData) locationData).getSlimefunBlockData().getDataKeys();
    }

    @Override
    public void setLocationData(@Nonnull Location location, @Nonnull String key, @Nullable String value) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        if(slimefunBlockData != null) {
            if(value != null) {
                slimefunBlockData.setData(key, value);
            } else {
                slimefunBlockData.removeData(key);
            }
        }
    }

    @Override
    public void setLocationData(@Nonnull LocationData locationData, @Nonnull String key, @Nullable String value) {
        if(value != null) {
            ((LocationDatabaseData) locationData).getSlimefunBlockData().setData(key, value);
        } else {
            ((LocationDatabaseData) locationData).getSlimefunBlockData().removeData(key);
        }
    }

    @Override
    public void setLocationData(@Nonnull LocationData locationData) {
        this.blockDataController.setBlockDataLocation(((LocationDatabaseData) locationData).getSlimefunBlockData(), locationData.getLocation());
    }

    @Nullable
    public Inventory getRawInventory(@Nonnull LocationData locationData) {
        BlockMenu blockMenu = ((LocationDatabaseData) locationData).getSlimefunBlockData().getBlockMenu();
        return blockMenu == null ? null : blockMenu.toInventory();
    }

    @Nullable
    @Override
    public Inventory getRawInventory(@Nonnull Location location) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        if(slimefunBlockData != null) {
            BlockMenu blockMenu = slimefunBlockData.getBlockMenu();
            if(blockMenu != null) {
                return blockMenu.toInventory();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public LocationDatabaseData getLocationData(@Nonnull Location location) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockData(location);
        return slimefunBlockData == null ? null : new LocationDatabaseData(location, slimefunBlockData);
    }

    @Nonnull
    @Override
    public LocationDatabaseData getOrCreateEmptyLocationData(@Nonnull Location location) {
        LocationDatabaseData locationData = this.getLocationData(location);
        if(locationData == null) {
            throw new NullPointerException("Can not create empty location data. May be you should use getOrCreateEmptyLocationData(Location, String)}.");
        }
        return locationData;
    }

    @Nonnull
    @Override
    public LocationDatabaseData createLocationData(@Nonnull Location location, @Nonnull Map<String, Object> map) {
        Object o = map.get("id");
        if(o == null) {
            throw new NullPointerException("Can not create location data. The id is needed.");
        }
        String id = o.toString();
        SlimefunBlockData slimefunBlockData = this.blockDataController.createBlock(location, id);
        return new LocationDatabaseData(location, slimefunBlockData);
    }

    @Override
    public void clearLocationData(@Nonnull Location location) {
        this.blockDataController.removeBlock(location);
    }

    @Nonnull
    @Override
    public LocationDatabaseData getOrCreateEmptyLocationData(@Nonnull Location location, @Nonnull String slimefunItemId) {
        LocationDatabaseData locationData = this.getLocationData(location);
        if(locationData == null) {
            SlimefunBlockData slimefunBlockData = this.blockDataController.createBlock(location, slimefunItemId);
            locationData = new LocationDatabaseData(location, slimefunBlockData);
        }
        return locationData;
    }

    @Nullable
    @Override
    public SlimefunItem getSlimefunItem(@Nonnull Location location) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return slimefunBlockData == null ? null : SlimefunItem.getById(slimefunBlockData.getSfId());
    }

    @Nullable
    @Override
    public BlockMenu getBlockMenu(@Nonnull Location location) {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return slimefunBlockData == null ? null : slimefunBlockData.getBlockMenu();
    }

    @Nullable
    @Override
    public BlockMenu getBlockMenu(@Nonnull SlimefunLocationData locationData) {
        return ((LocationDatabaseData) locationData).getSlimefunBlockData().getBlockMenu();
    }
}
