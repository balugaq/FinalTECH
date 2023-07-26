package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.taraxacum.common.math.RandomUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.interfaces.UnCopiableItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.CopyCardFactoryInventory;
import io.taraxacum.finaltech.core.operation.CopyCardFactoryOperation;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.SqlUtil;
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
import java.util.concurrent.atomic.AtomicInteger;

public class CopyCardFactory extends AbstractOperationMachine {
    private final ItemStack nullInfoIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "null-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "null-icon", "lore"));

    private final String keyItem = "i";
    private final String keyAmount = "a";
    private final String keyDifficulty = "d";

    private final AtomicInteger count = new AtomicInteger();
    private int lastCount;

    private int statusSlot;

    public CopyCardFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        CopyCardFactoryInventory copyCardFactoryInventory = new CopyCardFactoryInventory(this);
        this.statusSlot = copyCardFactoryInventory.statusSlot;
        return copyCardFactoryInventory;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack item, @Nonnull List<ItemStack> drops) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if (FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if (blockMenu != null && blockMenu.getPreset().getID().equals(CopyCardFactory.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, CopyCardFactory.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, CopyCardFactory.this.getOutputSlot());
                    }
                }

                CopyCardFactory.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory == null) {
            return;
        }

        if (RandomUtil.compareTwoRandom(FinalTech.getRandom(), ConstantTableUtil.FULL_TPS, FinalTech.getTps())) {
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

        CopyCardFactoryOperation operation = (CopyCardFactoryOperation) this.getMachineProcessor().getOperation(block);

        if (operation == null) {
            String itemString = FinalTech.getLocationDataService().getLocationData(locationData, this.keyItem);
            String amountStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyAmount);
            if (itemString != null && amountStr != null) {
                ItemStack stringItem = ItemStackUtil.stringToItemStack(itemString);
                if (!ItemStackUtil.isItemNull(stringItem)) {
                    operation = new CopyCardFactoryOperation(stringItem);
                    this.getMachineProcessor().startOperation(block, operation);
                    long amount = (long) Double.parseDouble(amountStr);
                    operation.setCount(amount);
                }
            }
        }

        if (operation != null) {
            operation.setDifficulty(difficulty);
        }

        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                continue;
            }
            if (operation == null) {
                SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                if (sfItem == null || sfItem instanceof UnCopiableItem) {
                    break;
                }

                if (!FinalTechItems.COPY_CARD.isTargetItem(itemStack)) {
                    break;
                }

                operation = new CopyCardFactoryOperation(itemStack);
                this.getMachineProcessor().startOperation(block, operation);
            } else {
                operation.addItem(itemStack);
            }
        }

        if (operation != null && operation.isFinished() && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), operation.getResult())) {
            FinalTech.getLogService().addItem(FinalTechItems.COPY_CARD.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
            this.getMachineProcessor().endOperation(block);
            operation = null;
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount, null);
        }

        if (operation != null) {
            String itemString = FinalTech.getLocationDataService().getLocationData(locationData, this.keyItem);
            if (itemString == null) {
                itemString = ItemStackUtil.itemStackToString(operation.getItemWrapper().getItemStack());
                if (itemString != null && FinalTech.safeSql()) {
                    itemString = SqlUtil.getSafeSql(itemString);
                }
                if (itemString != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, itemString);
                }
            }
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyAmount, String.valueOf(operation.getCount()));
        }

        if (!inventory.getViewers().isEmpty()) {
            ItemStack showItem;
            if (operation != null) {
                operation.updateShowItem();
                showItem = operation.getShowItemStack();
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
                String.valueOf(ConstantTableUtil.ITEM_COPY_CARD_AMOUNT));
    }

    protected boolean allowedItem(@Nonnull ItemStack itemStack) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem == null || slimefunItem instanceof UnCopiableItem || slimefunItem instanceof ValidItem) {
            return false;
        }

        return true;
    }
}
