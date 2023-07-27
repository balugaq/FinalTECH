package io.taraxacum.finaltech.core.item.machine.range.point;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.*;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.ItemMetaBuilder;
import io.taraxacum.libs.plugin.dto.ItemStackBuilder;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author Final_ROOT
 */
public class EquivalentConcept extends AbstractPointMachine implements RecipeItem, SimpleValidItem {
    public final String keyLife = "l";
    public final String keyRange = "r";
    private final double attenuationRate = ConfigUtil.getOrDefaultItemSetting(0.95, this, "attenuation-rate");
    private final double life = ConfigUtil.getOrDefaultItemSetting(4.0, this, "life");
    private final int range = ConfigUtil.getOrDefaultItemSetting(2, this, "range");

    private final ItemStackBuilder itemStackBuilder;
    public EquivalentConcept(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType) {
        super(itemGroup, item, recipeType, new ItemStack[0]);

        this.itemStackBuilder = ItemStackBuilder.fromItemStack(this.getItem());
        this.itemStackBuilder.amount(null);
        ItemMetaBuilder itemMetaBuilder = this.itemStackBuilder.getItemMetaBuilder();
        itemMetaBuilder.setData(FinalTech.getItemService().getIdKey(), PersistentDataType.STRING, this.getId());

        String validKey = FinalTech.getConfigManager().getOrDefault(String.valueOf(FinalTech.getRandom().nextDouble(FinalTech.getSeed())), "item-valid-key", this.getId());
        itemMetaBuilder.setData(new NamespacedKey(FinalTech.getInstance(), this.getId()), PersistentDataType.STRING, validKey);

        this.addItemHandler(new ItemUseHandler() {
            @Override
            @EventHandler(priority = EventPriority.LOWEST)
            public void onRightClick(PlayerRightClickEvent e) {
                e.cancel();
            }
        });
        this.addItemHandler(new WeaponUseHandler() {
            @Override
            @EventHandler(priority = EventPriority.LOWEST)
            public void onHit(@Nonnull EntityDamageByEntityEvent e, @Nonnull Player player, @Nonnull ItemStack item) {
                e.setCancelled(true);
            }
        });
        this.addItemHandler(new EntityInteractHandler() {
            @Override
            @EventHandler(priority = EventPriority.LOWEST)
            public void onInteract(PlayerInteractEntityEvent e, ItemStack item, boolean offHand) {
                e.setCancelled(true);
            }
        });
        this.addItemHandler(new ToolUseHandler() {
            @Override
            @EventHandler(priority = EventPriority.LOWEST)
            public void onToolUse(BlockBreakEvent e, ItemStack tool, int fortune, List<ItemStack> drops) {
                e.setCancelled(true);
            }
        });
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return null;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler();
    }

    @Nonnull
    @Override
    public Collection<ItemStack> getDrops() {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(this.getValidItem());
        return drops;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        if (!BlockTickerUtil.subSleep(FinalTech.getLocationDataService(), locationData)) {
            return;
        }

        String lifeStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyLife);
        double life = lifeStr != null ? Double.parseDouble(lifeStr) : 0;
        if (life < 1) {
            Location location = block.getLocation();
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyLife, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyRange, null);
            BlockTickerUtil.subSleep(FinalTech.getLocationDataService(), locationData);
            FinalTech.getLocationDataService().clearLocationData(location);

            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(javaPlugin, () -> BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), () -> {
                if (!location.getBlock().getType().isAir()
                        && FinalTech.getLocationDataService().getLocationData(location) == null
                        && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    slimefunLocationDataService.getOrCreateEmptyLocationData(location, FinalTechItems.JUSTIFIABILITY.getId());
                }
            }, block.getLocation()), Slimefun.getTickerTask().getTickRate() + 1);
            return;
        }

        String rangeStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyRange);
        final int range = rangeStr != null ? Integer.parseInt(rangeStr) : this.range;

        while (life > 1) {
            final double finalLife = life--;
            this.pointFunction(block, range, location -> {
                BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(this.getId()), () -> {
                    Block targetBlock = location.getBlock();
                    if (FinalTech.getLocationDataService().getLocationData(location) == null
                            && targetBlock.getType().isAir()
                            && FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                        LocationData tempLocationData = slimefunLocationDataService.getOrCreateEmptyLocationData(location, this.getId());
                        FinalTech.getLocationDataService().setLocationData(locationData, keyLife, String.valueOf(finalLife * attenuationRate));
                        FinalTech.getLocationDataService().setLocationData(locationData, keyRange, String.valueOf(range + 1));
                        BlockTickerUtil.setSleep(FinalTech.getLocationDataService(), tempLocationData, this.life - finalLife);
                        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                        javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> targetBlock.setType(this.getItem().getType()));
                    }
                }, location);
                return 0;
            });
        }

        FinalTech.getLocationDataService().setLocationData(locationData, this.keyLife, String.valueOf(0));
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Nonnull
    @Override
    public Location getTargetLocation(@Nonnull Location location, int range) {
        int y = location.getBlockY() - range + FinalTech.getRandom().nextInt(range + range);
        y = Math.min(location.getWorld().getMaxHeight(), y);
        y = Math.max(location.getWorld().getMinHeight(), y);
        return new Location(location.getWorld(), location.getX() - range + FinalTech.getRandom().nextInt(range + range), y, location.getZ() - range + new Random().nextInt(range + range));
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }

    @Nonnull
    @Override
    public ItemStack getValidItem() {
        return this.itemStackBuilder.build();
    }

    @Override
    public boolean verifyItem(@Nonnull ItemStack itemStack) {
        return this.itemStackBuilder.softCompare(itemStack);
    }

    @Override
    public boolean verifyItem(@Nonnull ItemWrapper itemWrapper) {
        return this.itemStackBuilder.softCompare(itemWrapper);
    }
}
