package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.limit.DustFactoryInventory;
import io.taraxacum.finaltech.core.operation.DustFactoryOperation;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.LocationData;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Final_ROOT
 */
public class DustFactory extends AbstractOperationMachine implements RecipeItem, MenuUpdater {
    private final int baseAmountDifficulty = ConfigUtil.getOrDefaultItemSetting(1024, this, "difficulty", "base", "amount");
    private final int baseTypeDifficulty = ConfigUtil.getOrDefaultItemSetting(16, this, "difficulty", "base", "type");
    private final int multiAmountDifficulty = ConfigUtil.getOrDefaultItemSetting(64, this, "difficulty", "multi", "amount");
    private final int multiTypeDifficulty = ConfigUtil.getOrDefaultItemSetting(1, this, "difficulty", "multi", "type");
    private final int deviationDifficulty = ConfigUtil.getOrDefaultItemSetting(-4, this, "difficulty", "deviation");

    private final String key = "d";
    private final int maxDynamicDifficulty = (Integer.MAX_VALUE - this.baseAmountDifficulty) / this.baseTypeDifficulty / 2;
    private final List<Location> locationList;
    private int statusSlot;

    public DustFactory(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
        this.locationList = FinalTech.isAsyncSlimefunItem(this.getId()) ? new ArrayList<>() : new CopyOnWriteArrayList<>();
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        DustFactoryInventory dustFactoryInventory = new DustFactoryInventory(this);
        this.statusSlot = dustFactoryInventory.statusSlot;
        return dustFactoryInventory;
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
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(DustFactory.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, DustFactory.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, DustFactory.this.getOutputSlot());
                    }
                }

                DustFactory.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        String dynamicDifficultyStr = FinalTech.getLocationDataService().getLocationData(locationData, this.key);
        int dynamicDifficulty = dynamicDifficultyStr == null ? 0 : Integer.parseInt(dynamicDifficultyStr);
        DustFactoryOperation operation = (DustFactoryOperation) this.getMachineProcessor().getOperation(block);

        for (int slot : this.getInputSlot()) {
            ItemStack inputItem = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(inputItem)) {
                continue;
            }

            if (operation == null) {
                int amount = this.baseAmountDifficulty;
                int type = this.baseTypeDifficulty;
                int dynamicDeviation = this.deviationDifficulty + dynamicDifficulty;
                if(dynamicDeviation != 0) {
                    int deviation = dynamicDeviation / Math.abs(dynamicDeviation) * FinalTech.getRandom().nextInt(Math.abs(dynamicDeviation) + 1);
                    amount += this.multiAmountDifficulty * (dynamicDeviation - deviation);
                    type += this.multiTypeDifficulty * deviation;
                }

                operation = new DustFactoryOperation(amount, type);
                this.getMachineProcessor().startOperation(block, operation);
            }
            operation.addItem(inputItem);
            inventory.clear(slot);

            if(operation.isFinished()) {
                ItemStack itemStack = operation.getResult();
                SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                if(sfItem != null && InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), itemStack)) {
                    FinalTech.getLogService().addItem(sfItem.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
                    this.getMachineProcessor().endOperation(block);
                    operation = null;
                }
            }
        }

        if (operation == null) {
            int amount = this.baseAmountDifficulty;
            int type = this.baseTypeDifficulty;
            if(this.deviationDifficulty != 0) {
                int deviation = this.deviationDifficulty / Math.abs(this.deviationDifficulty) * FinalTech.getRandom().nextInt(Math.abs(this.deviationDifficulty) + 1);
                amount += this.multiAmountDifficulty * (this.deviationDifficulty - deviation);
                type += this.multiTypeDifficulty * deviation;
            }

            operation = new DustFactoryOperation(amount, type);
            this.getMachineProcessor().startOperation(block, operation);
        }

        if (!inventory.getViewers().isEmpty()) {
            this.updateInv(inventory, this.statusSlot, this,
                    String.valueOf(operation.getAmountCount()),
                    String.valueOf(operation.getTypeCount()),
                    String.valueOf(operation.getAmountDifficulty()),
                    String.valueOf(operation.getTypeDifficulty()),
                    String.valueOf(dynamicDifficulty));
        }

        if (this.locationList.size() > 1) {
            Location anotherLocation = this.locationList.get(FinalTech.getRandom().nextInt(this.locationList.size()));
            double distance = LocationUtil.getManhattanDistance(locationData.getLocation(), anotherLocation);
            if (distance > 0 && FinalTech.getRandom().nextDouble(distance * distance) <= this.locationList.size()) {
                dynamicDifficultyStr = StringNumberUtil.min(StringNumberUtil.add(String.valueOf(dynamicDifficulty)), String.valueOf(this.maxDynamicDifficulty));
                FinalTech.getLocationDataService().setLocationData(locationData, this.key, dynamicDifficultyStr);
            }
        }

        this.locationList.add(locationData.getLocation());
    }

    @Override
    protected void uniqueTick() {
        super.uniqueTick();
        this.locationList.clear();
    }

    @Override
    public void updateInv(@Nonnull Inventory inventory, int slot, @Nonnull SlimefunItem slimefunItem, @Nonnull String... text) {
        MenuUpdater.super.updateInv(inventory, slot, slimefunItem, text);
        if (text.length == 5) {
            int amountCount = Integer.parseInt(text[0]);
            int typeCount = Integer.parseInt(text[1]);
            int amountDifficulty = Integer.parseInt(text[2]);
            int typeDifficulty = Integer.parseInt(text[3]);

            ItemStack itemStack = inventory.getItem(slot);

            if (amountCount == 0 && typeCount == 0) {
                itemStack.setType(Material.RED_STAINED_GLASS_PANE);
            } else if (amountCount > amountDifficulty || typeCount > typeDifficulty) {
                itemStack.setType(Material.YELLOW_STAINED_GLASS_PANE);
            } else {
                itemStack.setType(Material.GREEN_STAINED_GLASS_PANE);
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.baseAmountDifficulty),
                String.valueOf(this.baseTypeDifficulty),
                String.valueOf(this.baseAmountDifficulty + this.deviationDifficulty * this.multiAmountDifficulty),
                String.valueOf(this.baseTypeDifficulty + this.deviationDifficulty * this.multiTypeDifficulty));
    }
}
