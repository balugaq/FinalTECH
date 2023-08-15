package io.taraxacum.finaltech.core.inventory.clicker;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.clicker.AbstractClickerMachine;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AreaAccessorInventory extends AbstractClickerInventory {
    private final int[] border = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8};

    private final int[] tempContent = new int[] {0, 1, 2, 3, 4, 5, 6, 9, 10, 11, 12, 13, 14, 15, 18, 19, 20, 21, 22, 23, 24, 27, 28, 29, 30, 31, 32, 33, 36, 37, 38, 39, 40, 41, 42, 45, 46, 47, 48, 49, 50, 51};
    private final int[] tempBorder = new int[] {7, 16, 25, 34, 43, 52};
    private final int[] tempPreviousPage = new int[] {8, 17, 26};
    private final int[] tempNextPage = new int[] {35, 44, 53};

    private final int range;
    public AreaAccessorInventory(@Nonnull AbstractClickerMachine abstractClickerMachine, int range) {
        super(abstractClickerMachine);
        this.range = range;
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Override
    protected void initSelf() {

    }

    @Override
    protected void openFunction(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        this.openInventoryTo(player, location, this.range, 0);
    }

    @Override
    public int getSize() {
        return 9;
    }

    /**
     * @param page begin from 0
     */
    public void openInventoryTo(@Nonnull Player player, @Nonnull Location location, int range, int page) {
        World world = location.getWorld();
        if (world == null) {
            return;
        }

        Map<Integer, List<Location>> distanceLocationMap = new HashMap<>(range * 3);
        Location tempLocation = location.clone();

        int minX = location.getBlockX() - range;
        int minY = Math.max(location.getBlockY() - range, world.getMinHeight());
        int minZ = location.getBlockZ() - range;
        int maxX = location.getBlockX() + range;
        int maxY  = Math.min(location.getBlockY() + range, world.getMaxHeight());
        int maxZ = location.getBlockZ() + range;
        for (int x = minX; x <= maxX; x++) {
            tempLocation.setX(x);
            for (int y = minY; y <= maxY; y++) {
                tempLocation.setY(y);
                for (int z = minZ; z <= maxZ; z++) {
                    tempLocation.setZ(z);
                    if (tempLocation.getChunk().isLoaded()
                            && FinalTech.getLocationDataService().getRawInventory(tempLocation) != null
                            && FinalTech.getLocationDataService().getLocationData(tempLocation) != null) {
                        int distance = Math.abs(tempLocation.getBlockX() - location.getBlockX())
                                + Math.abs(tempLocation.getBlockY() - location.getBlockY())
                                + Math.abs(tempLocation.getBlockZ() - location.getBlockZ());
                        List<Location> locationList = distanceLocationMap.computeIfAbsent(distance, d -> new ArrayList<>(d * d * 4 + 2));
                        locationList.add(tempLocation.clone());
                    }
                }
            }
        }

        List<Location> locationList = new ArrayList<>();
        for (int i = 0; i < range * 3; i++) {
            if (distanceLocationMap.containsKey(i)) {
                locationList.addAll(distanceLocationMap.get(i));
            }
        }

        SimpleVirtualInventory simpleVirtualInventory = new SimpleVirtualInventory(54, " ");
        for (int i = 0; i < this.tempContent.length; i++) {
            if (i + page * this.tempContent.length >= locationList.size()) {
                simpleVirtualInventory.drawBackground(this.tempContent[i], Icon.BORDER_ICON);
                continue;
            }
            final Location finalLocation = locationList.get((i + page * this.tempContent.length) % locationList.size());
            LocationData locationData = FinalTech.getLocationDataService().getLocationData(finalLocation);
            if(locationData != null
                    && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                SlimefunItem slimefunItem = LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData);
                BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu((SlimefunLocationData) locationData);
                if (slimefunItem != null && blockMenu != null) {
                    ItemStack icon = MachineUtil.cloneAsDescriptiveItemWithLore(slimefunItem.getItem(),
                            FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", this.getId(), "temp-icon", "lore"),
                                String.valueOf(finalLocation.getBlockX() - location.getBlockX()),
                                String.valueOf(finalLocation.getBlockY() - location.getBlockY()),
                                String.valueOf(finalLocation.getBlockZ() - location.getBlockZ())));
                    simpleVirtualInventory.getInventory().setItem(this.tempContent[i], icon);
                    simpleVirtualInventory.setOnClick(this.tempContent[i], inventoryClickEvent -> {
                        inventoryClickEvent.setCancelled(true);
                        if (!finalLocation.getChunk().isLoaded()) {
                            player.closeInventory();
                            return;
                        }
                        // BlockMenu may be updated after the menu generated.
                        LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(finalLocation);
                        if (tempLocationData != null
                                && FinalTech.getLocationDataService().getRawInventory(tempLocationData) != null
                                && blockMenu.canOpen(finalLocation.getBlock(), player)) {
                            JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
                            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, blockMenu.getBlock()));
                            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.WAX_OFF, 0, 0.25, LocationUtil.getCenterLocation(location.getBlock()), LocationUtil.getCenterLocation(blockMenu.getBlock())));
                            blockMenu.open(player);
                        } else {
                            player.sendMessage(FinalTech.getLanguageString("items", AreaAccessorInventory.this.getId(), "message", "no-permission", "location"));
                        }
                    });
                    continue;
                }
            }
            simpleVirtualInventory.drawBackground(this.tempContent[i], Icon.ERROR_ICON);
        }
        for (int slot : this.tempBorder) {
            simpleVirtualInventory.drawBackground(slot, Icon.BORDER_ICON);
        }
        for (int slot : this.tempNextPage) {
            simpleVirtualInventory.getInventory().setItem(slot, Icon.NEXT_PAGE_ICON);
            simpleVirtualInventory.setOnClick(slot, inventoryClickEvent -> this.openInventoryTo(player, location, range, Math.min(page + 1, locationList.size() / tempContent.length)));
        }
        for (int slot : this.tempPreviousPage) {
            simpleVirtualInventory.getInventory().setItem(slot, Icon.PREVIOUS_PAGE_ICON);
            simpleVirtualInventory.setOnClick(slot, inventoryClickEvent -> this.openInventoryTo(player, location, range, Math.max(page - 1, 0)));
        }

        simpleVirtualInventory.open(player);
    }
}
