package io.taraxacum.finaltech.core.item.machine.tower;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitalItem;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.SimulateClickMachineInventory;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class SimulateClickMachine extends AbstractTower implements RecipeItem {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final double rangeRate = ConfigUtil.getOrDefaultItemSetting(0.4, this, "range-rate");

    public SimulateClickMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new SimulateClickMachineInventory(this);
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

        ItemStack inputItemStack = inventory.getItem(this.getInputSlot()[0]);
        ItemStack outputItemStack = inventory.getItem(this.getOutputSlot()[0]);
        if (ItemStackUtil.isItemNull(inputItemStack) || !ItemStackUtil.isItemNull(outputItemStack)) {
            return;
        }

        int digit = 0;
        SlimefunItem sfItem = SlimefunItem.getByItem(inputItemStack);
        if(sfItem instanceof DigitalItem digitalItem) {
            digit = digitalItem.getDigit();
        }

        if(digit > 0) {
            Location location = block.getLocation();
            location.setY(location.getY() - 1);
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);

            if(tempLocationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                    && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), new ItemAmountWrapper(inputItemStack, 1))) {
                inputItemStack.setAmount(inputItemStack.getAmount() - 1);

                double range = digit * this.rangeRate;

                JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> {
                    if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                        BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                        if(blockMenu != null) {
                            Block targetBlock = location.getBlock();
                            for (Entity entity : location.getWorld().getNearbyEntities(LocationUtil.getCenterLocation(targetBlock), range, range, range, entity -> entity instanceof Player)) {
                                if(blockMenu.canOpen(targetBlock, (Player) entity)) {
                                    blockMenu.open((Player) entity);
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.rangeRate));
    }
}
