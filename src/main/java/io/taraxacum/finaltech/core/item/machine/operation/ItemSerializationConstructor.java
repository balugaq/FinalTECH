package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.interfaces.UnCopiableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.ItemSerializationConstructorInventory;
import io.taraxacum.finaltech.core.operation.ItemCopyCardOperation;
import io.taraxacum.finaltech.core.operation.ItemPhonyOperation;
import io.taraxacum.finaltech.core.operation.ItemSerializationConstructorOperation;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.SqlUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a slimefun machine
 * it will be used in gameplay
 * It's not a function class!
 * @author Final_ROOT
 */
@Deprecated
public class ItemSerializationConstructor extends AbstractOperationMachine {
    private final ItemStack nullInfoIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "null-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "null-icon", "lore"));
    private final String keyItem = "i";
    private final String keyAmount = "a";
    private final String keyDifficulty = "d";

    private final AtomicInteger count = new AtomicInteger();
    private int lastCount;

    private int statusSlot;

    public ItemSerializationConstructor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        ItemSerializationConstructorInventory itemSerializationConstructorInventory = new ItemSerializationConstructorInventory(this);
        this.statusSlot = itemSerializationConstructorInventory.statusSlot;
        return itemSerializationConstructorInventory;
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
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(ItemSerializationConstructor.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, ItemSerializationConstructor.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, ItemSerializationConstructor.this.getOutputSlot());
                    }
                }

                ItemSerializationConstructor.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }

        this.count.getAndIncrement();
        Location location = block.getLocation();

        int count = this.lastCount;
        if(FinalTech.getTps() < ConstantTableUtil.WARNING_TPS) {
            count += 21 - FinalTech.getTps();
            count *= 21 - FinalTech.getTps();
        }
        count = FinalTech.getRandom().nextInt(count + 1);
        String difficultyStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyDifficulty);
        long difficulty = difficultyStr == null ? count : Long.parseLong(difficultyStr) + count;
        FinalTech.getLocationDataService().setLocationData(locationData, this.keyDifficulty, String.valueOf(difficulty));

        ItemSerializationConstructorOperation operation = (ItemSerializationConstructorOperation) this.getMachineProcessor().getOperation(block);

        String itemString = FinalTech.getLocationDataService().getLocationData(locationData, this.keyItem);
        if (operation == null && itemString != null) {
            ItemStack stringItem = ItemStackUtil.stringToItemStack(itemString);
            if (!ItemStackUtil.isItemNull(stringItem) && ItemSerializationConstructorOperation.getType(stringItem) == ItemSerializationConstructorOperation.COPY_CARD) {
                operation = ItemSerializationConstructorOperation.newInstance(stringItem);
                if (operation != null) {
                    this.getMachineProcessor().startOperation(block, operation);
                    long amount = (long) Double.parseDouble(FinalTech.getLocationDataService().getLocationData(locationData, this.keyAmount));
                    ((ItemCopyCardOperation)operation).setCount(amount);
                }
            }
        }

        if (operation instanceof ItemCopyCardOperation itemCopyCardOperation) {
            itemCopyCardOperation.setDifficulty(difficulty);
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
            if (operation == null) {
                SlimefunItem sfItem = SlimefunItem.getByItem(inputItem);
                if (sfItem == null || sfItem instanceof UnCopiableItem) {
                    break;
                }

                operation = ItemSerializationConstructorOperation.newInstance(inputItem);
                if (operation == null) {
                    break;
                }
                this.getMachineProcessor().startOperation(block, operation);
            } else {
                operation.addItem(inputItem);
            }
        }

        if (operation != null && operation.isFinished() && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), operation.getResult())) {
            if (operation instanceof ItemCopyCardOperation) {
                FinalTech.getLogService().addItem(FinalTechItems.COPY_CARD.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
            } else if(operation instanceof ItemPhonyOperation) {
                SlimefunItem sfItem = SlimefunItem.getByItem(operation.getResult());
                if(sfItem != null) {
                    FinalTech.getLogService().addItem(sfItem.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                }
            }
            this.getMachineProcessor().endOperation(block);
            operation = null;
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount, null);
        }

        if (operation instanceof ItemCopyCardOperation itemCopyCardOperation) {
            itemString = FinalTech.getLocationDataService().getLocationData(locationData, this.keyItem);
            if (itemString == null) {
                itemString = FinalTech.safeSql() ? SqlUtil.getSafeSql(ItemStackUtil.itemStackToString(itemCopyCardOperation.getMatchItem())) : ItemStackUtil.itemStackToString(itemCopyCardOperation.getMatchItem());
                if (itemString != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, itemString);
                }
            }
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount, String.valueOf(itemCopyCardOperation.getCount()));
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
    protected void uniqueTick() {
        super.uniqueTick();

        this.lastCount = this.count.getAndSet(0);
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(ConstantTableUtil.ITEM_COPY_CARD_AMOUNT),
                String.valueOf(ConstantTableUtil.ITEM_SINGULARITY_AMOUNT),
                String.valueOf(ConstantTableUtil.ITEM_SPIROCHETE_AMOUNT));
    }
}
