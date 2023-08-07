package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.PhonyFactoryInventory;
import io.taraxacum.finaltech.core.operation.ItemPhonyOperationV2;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class PhonyFactory extends AbstractOperationMachine {
    private final ItemStack nullInfoIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "null-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "null-icon", "lore"));

    private int statusSlot;

    public PhonyFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        PhonyFactoryInventory phonyFactoryInventory = new PhonyFactoryInventory(this);
        this.statusSlot = phonyFactoryInventory.statusSlot;
        return phonyFactoryInventory;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(PhonyFactory.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, PhonyFactory.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, PhonyFactory.this.getOutputSlot());
                    }
                }

                PhonyFactory.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        ItemPhonyOperationV2 operation = (ItemPhonyOperationV2) this.getMachineProcessor().getOperation(block);
        if (operation == null) {
            operation = new ItemPhonyOperationV2();
            this.getMachineProcessor().startOperation(locationData.getLocation(), operation);
        }

        int slotSize = (int) (this.getInputSlot().length * FinalTech.getTps() / 20);
        for (int slot : this.getInputSlot()) {
            if (slot >= slotSize) {
                break;
            }

            ItemStack inputItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(inputItem)) {
                continue;
            }

            if (operation.addItem(inputItem) > 0) {
                inventory.clear(slot);
            }
        }

        if (operation.isFinished() && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), operation.getResult())) {
            SlimefunItem sfItem = SlimefunItem.getByItem(operation.getResult());
            if(sfItem instanceof ValidItem) {
                FinalTech.getLogService().addItem(sfItem.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, locationData.getLocation(), this.getAddon().getJavaPlugin());
            }
            this.getMachineProcessor().endOperation(block);
            operation = null;
        }

        if (!inventory.getViewers().isEmpty()) {
            ItemStack showItem;
            if (operation != null) {
                operation.updateShowItem();
                showItem = operation.getShowItem();
            } else {
                showItem = this.nullInfoIcon;
            }
            inventory.setItem(this.statusSlot, showItem);
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(ConstantTableUtil.ITEM_SINGULARITY_AMOUNT),
                String.valueOf(ConstantTableUtil.ITEM_SPIROCHETE_AMOUNT));
    }
}
