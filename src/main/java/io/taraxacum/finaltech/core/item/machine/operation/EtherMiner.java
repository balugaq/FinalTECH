package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.common.util.MathUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.EtherMinerInventory;
import io.taraxacum.finaltech.core.operation.EtherMinerOperation;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.OptionalInt;

/**
 * @author Final_ROOT
 */
public class EtherMiner extends AbstractOperationMachine implements RecipeItem, MenuUpdater {
    // time = baseTime / logN(supplies * mul + 1)
    private final double baseTime = ConfigUtil.getOrDefaultItemSetting(60, this, "time");
    private final double logN = ConfigUtil.getOrDefaultItemSetting(100, this, "logN");
    private final double mul = ConfigUtil.getOrDefaultItemSetting(0.01, this, "mul");
    private final double add = ConfigUtil.getOrDefaultItemSetting(0.08, this, "add");
    private final double random = ConfigUtil.getOrDefaultItemSetting(0.1, this, "random");
    private int statusSlot;

    public EtherMiner(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        EtherMinerInventory etherMinerInventory = new EtherMinerInventory(this);
        this.statusSlot = etherMinerInventory.statusSlot;
        return etherMinerInventory;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> drops) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(EtherMiner.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, EtherMiner.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, EtherMiner.this.getOutputSlot());
                    }
                }

                EtherMiner.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }

        Location location = block.getLocation();

        OptionalInt supplies = Slimefun.getGPSNetwork().getResourceManager().getSupplies(FinalTechItems.ETHER, block.getWorld(), block.getX() >> 4, block.getZ() >> 4);
        int etherAmount = supplies.isPresent() ? supplies.getAsInt() : -1;

        EtherMinerOperation etherMinerOperation = (EtherMinerOperation)this.getMachineProcessor().getOperation(location);
        if (etherMinerOperation != null) {
            etherMinerOperation.addProgress(1);
            if (etherMinerOperation.isFinished()) {
                if (etherMinerOperation.getEtherAmount() == etherAmount) {
                    ItemStack outputItemStack = FinalTechItems.ETHER.getValidItem();
                    if (InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), outputItemStack)) {
                        FinalTech.getLogService().addItem(FinalTechItems.ETHER.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                        Slimefun.getGPSNetwork().getResourceManager().setSupplies(FinalTechItems.ETHER, block.getWorld(), block.getX() >> 4, block.getZ() >> 4, Math.max(0, etherAmount - 1));
                    }
                } else {
                    this.getMachineProcessor().endOperation(location);
                    etherMinerOperation = null;
                }
            }
        } else if (etherAmount > 0) {
            ItemStack unorderedDustItemStack = null;
            for (int slot : this.getInputSlot()) {
                ItemStack itemStack = inventory.getItem(slot);
                if (!ItemStackUtil.isItemNull(itemStack) && FinalTechItems.UNORDERED_DUST.verifyItem(itemStack)) {
                    unorderedDustItemStack = itemStack;
                    break;
                }
            }
            if (unorderedDustItemStack != null) {
                etherMinerOperation = new EtherMinerOperation((int) (this.baseTime / MathUtil.getLog(this.logN, etherAmount * this.mul + 1 + this.add) * (1 + FinalTech.getRandom().nextDouble(this.random))), etherAmount);
                boolean startOperation = this.getMachineProcessor().startOperation(location, etherMinerOperation);
                if (startOperation) {
                    unorderedDustItemStack.setAmount(unorderedDustItemStack.getAmount() - 1);
                    FinalTech.getLogService().subItem(FinalTechItems.UNORDERED_DUST.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                } else {
                    etherMinerOperation = null;
                }
            }
        } else if (etherAmount == 0) {
            etherAmount = FinalTechItems.ETHER.getDefaultSupply(block.getWorld().getEnvironment(), block.getBiome()) + FinalTech.getRandom().nextInt(1 + FinalTechItems.ETHER.getMaxDeviation());
            Slimefun.getGPSNetwork().getResourceManager().setSupplies(FinalTechItems.ETHER, block.getWorld(), block.getX() >> 4, block.getZ() >> 4, Math.max(0, etherAmount));
        }

        if (!inventory.getViewers().isEmpty()) {
            int progress = 0;
            String totalTicksStr;
            if (etherMinerOperation != null) {
                progress = etherMinerOperation.getProgress();
                totalTicksStr = String.valueOf(etherMinerOperation.getTotalTicks());
            } else {
                totalTicksStr = "?";
            }
            this.updateInv(inventory, this.statusSlot, this,
                    String.valueOf(progress),
                    totalTicksStr,
                    String.valueOf(etherAmount));
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this,
                String.valueOf(this.baseTime));

        this.registerRecipe(FinalTechItemStacks.UNORDERED_DUST, FinalTechItemStacks.ETHER);
    }
}
