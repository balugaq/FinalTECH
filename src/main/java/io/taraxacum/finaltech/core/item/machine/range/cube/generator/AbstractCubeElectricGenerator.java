package io.taraxacum.finaltech.core.item.machine.range.cube.generator;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.unit.StatusL2Inventory;
import io.taraxacum.finaltech.core.item.machine.range.cube.AbstractCubeMachine;
import io.taraxacum.finaltech.core.option.RouteShow;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.*;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
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
public abstract class AbstractCubeElectricGenerator extends AbstractCubeMachine implements RecipeItem, MenuUpdater, LocationMachine {
    protected final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final ItemWrapper itemWrapper = new ItemWrapper(this.getItem());
    protected int statusSlot;

    public AbstractCubeElectricGenerator(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        StatusL2Inventory statusInventory = new StatusL2Inventory(this);
        this.statusSlot = statusInventory.statusSlot;
        return statusInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_ALLOW;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        boolean hasViewer = !inventory.getViewers().isEmpty();
        boolean drawParticle = hasViewer || RouteShow.VALUE_TRUE.equals(RouteShow.OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData));
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();

        int energy = 0;
        for(int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemSimilar(itemStack, this.itemWrapper)) {
                energy = Integer.MAX_VALUE / this.getEnergy() < itemStack.getAmount() ? Integer.MAX_VALUE : this.getEnergy() * itemStack.getAmount();
                energy += this.getEnergy();
                break;
            } else if (!ItemStackUtil.isItemNull(itemStack) && FinalTechItems.STORAGE_CARD.verifyItem(itemStack) && ItemStackUtil.isItemSimilar(StringItemUtil.parseItemInCard(itemStack), this.itemWrapper)) {
                int amount = Integer.parseInt(StringNumberUtil.min(StringItemUtil.parseAmountInCard(itemStack), StringNumberUtil.INTEGER_MAX_VALUE));
                energy = Integer.MAX_VALUE / this.getEnergy() < amount ? Integer.MAX_VALUE : this.getEnergy() * amount;
                energy += this.getEnergy();
                break;
            } else {
                energy = this.getEnergy();
            }
        }

        int finalEnergy = energy;
        int count = this.cubeFunction(block, this.getRange(), location -> {
            if(!location.getChunk().isLoaded()) {
                return -1;
            }
            LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(location);
            if (tempLocationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), tempLocationData) instanceof EnergyNetComponent energyNetComponent
                    && !JavaUtil.matchOnce(energyNetComponent.getEnergyComponentType(), EnergyNetComponentType.CAPACITOR, EnergyNetComponentType.GENERATOR)) {
                tempLocationData.cloneLocation();
                BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData)), () -> this.chargeMachine(energyNetComponent, finalEnergy, tempLocationData), tempLocationData.getLocation());
                if (drawParticle) {
                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, tempLocationData.getLocation().getBlock()));
                }
                return 1;
            }
            return 0;
        });

        if (hasViewer) {
            this.updateInv(inventory, this.statusSlot, this,
                    String.valueOf(count),
                    String.valueOf(finalEnergy));
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    private void chargeMachine(@Nonnull EnergyNetComponent energyNetComponent, int chargeEnergy, @Nonnull LocationData locationData) {
        int capacity = energyNetComponent.getCapacity();
        String energyStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
        int energy = Integer.parseInt(energyStr);
        if(energy < capacity) {
            int transferEnergy = Math.min(capacity - energy, chargeEnergy);
            if(transferEnergy > 0) {
                EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(energy + transferEnergy));
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.getEnergy()),
                String.valueOf(this.getRange()),
                String.valueOf(ConstantTableUtil.SLIMEFUN_TICK_INTERVAL));
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        int i = 0;
        Location location = sourceLocation.clone();
        World world = location.getWorld();
        int minX = location.getBlockX() - this.getRange();
        int minY = Math.max(location.getBlockY() - this.getRange(), world.getMinHeight());
        int minZ = location.getBlockZ() - this.getRange();
        int maxX = location.getBlockX() + this.getRange();
        int maxY  = Math.min(location.getBlockY() + this.getRange(), world.getMaxHeight());
        int maxZ = location.getBlockZ() + this.getRange();
        Location[] locations = new Location[(maxX - minX + 1) * (maxY - minY + 1) + (maxZ - minZ + 1)];
        for (int x = minX; x <= maxX; x++) {
            location.setX(x);
            for (int y = minY; y <= maxY; y++) {
                location.setY(y);
                for (int z = minZ; z <= maxZ; z++) {
                    location.setZ(z);
                    locations[i++] = location.clone();
                }
            }
        }

        return locations;
    }

    @Override
    public void updateInv(@Nonnull Inventory inventory, int slot, @Nonnull SlimefunItem slimefunItem, @Nonnull String... text) {
        MenuUpdater.super.updateInv(inventory, slot, slimefunItem, text);
        ItemStack itemStack = inventory.getItem(slot);
        if(text.length > 0) {
            itemStack.setType(StringNumberUtil.ZERO.equals(text[0]) ? Material.RED_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE);
        }
    }

    protected abstract int getEnergy();

    protected abstract int getRange();
}
