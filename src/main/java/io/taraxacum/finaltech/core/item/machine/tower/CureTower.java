package io.taraxacum.finaltech.core.item.machine.tower;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 */
public class CureTower extends AbstractTower implements RecipeItem, MenuUpdater {
    private final double baseRange = ConfigUtil.getOrDefaultItemSetting(1.6, this, "range-base");
    private final double mulRange = ConfigUtil.getOrDefaultItemSetting(0.1, this, "range-mul");
    private final double health = ConfigUtil.getOrDefaultItemSetting(0.025, this, "health");
    private int statusSlot;

    public CureTower(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusL2Inventory = new StatusL2Inventory(this);
        this.statusSlot = statusL2Inventory.statusSlot;
        return statusL2Inventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }


        double range = this.baseRange;
        for(int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemSimilar(itemStack, this.getItem())) {
                range += itemStack.getAmount() * this.mulRange;
            }
        }
        final double finalRange = range;

        Location location = block.getLocation();
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
            int count = 0;
            for (Entity entity : location.getWorld().getNearbyEntities(LocationUtil.getCenterLocation(block), finalRange, finalRange, finalRange, entity -> entity instanceof LivingEntity)) {
                LivingEntity livingEntity = (LivingEntity) entity;
                FinalTech.getEntityRunnableFactory().waitThenRun(() -> {
                    if (livingEntity.getHealth() > 0) {
                        livingEntity.setHealth(Math.min(livingEntity.getHealth() + CureTower.this.health, livingEntity.getMaxHealth()));
                    }
                }, livingEntity);
                count++;
            }

            if (!inventory.getViewers().isEmpty()) {
                this.updateInv(inventory, this.statusSlot, this,
                        String.valueOf(count),
                        String.valueOf(finalRange));
            }
        });
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.baseRange),
                String.valueOf(this.mulRange),
                String.valueOf(this.health));
    }
}
