package io.taraxacum.finaltech.core.item.machine.manual;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.inventory.manual.AbstractManualMachineInventory;
import io.taraxacum.finaltech.core.inventory.manual.EquivalentExchangeTableInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.slimefun.dto.ItemValueTableV2;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class EquivalentExchangeTable extends AbstractManualMachine implements RecipeItem {
    private final String keyReal = "r";
    private final String keyImaginary = "i";
    private int parseItemSlot;

    public EquivalentExchangeTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.parseItemSlot);
    }

    @Nonnull
    @Override
    protected AbstractManualMachineInventory newMachineInventory() {
        EquivalentExchangeTableInventory equivalentExchangeTableInventory = new EquivalentExchangeTableInventory(this, this.keyReal, this.keyImaginary);
        this.parseItemSlot = equivalentExchangeTableInventory.parseItemSlot;
        return equivalentExchangeTableInventory;
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
        if(inventory == null) {
            return;
        }

        Location location = block.getLocation();
        String realValue = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.keyReal), StringNumberUtil.ZERO);
        String imaginaryValue = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.keyImaginary), StringNumberUtil.ZERO);
        ItemValueTableV2.Value value = new ItemValueTableV2.Value(realValue, imaginaryValue);
        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (ItemStackUtil.isItemNull(itemStack)) {
                continue;
            }

            if (FinalTechItems.UNORDERED_DUST.verifyItem(itemStack)) {
                if (InventoryUtil.slotCount(inventory, this.getOutputSlot()) == this.getOutputSlot().length) {
                    continue;
                }
                int amount = itemStack.getAmount();
                value = this.doCraft(value, inventory, location, amount);
                itemStack.setAmount(0);
                FinalTech.getLogService().subItem(FinalTechItems.UNORDERED_DUST.getId(), amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                continue;
            }

            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem != null) {
                boolean valid = true;
                if (sfItem instanceof ValidItem validItem) {
                    if (!validItem.verifyItem(itemStack)) {
                        valid = false;
                    } else {
                        FinalTech.getLogService().subItem(sfItem.getId(), itemStack.getAmount(), this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                    }
                }
                if (valid) {
                    ItemValueTableV2.Value itemValue = ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack);
                    value = ItemValueTableV2.getInstance().addValue(value, itemValue);
                    itemStack.setAmount(0);
                }
            }
        }

        FinalTech.getLocationDataService().setLocationData(locationData, this.keyReal, value.getRealNumber());
        FinalTech.getLocationDataService().setLocationData(locationData, this.keyImaginary, value.getImaginaryNumber());

        if (!inventory.getViewers().isEmpty()) {
            this.getMachineInventory().updateInventory(inventory, block.getLocation());
        }
    }

    @Nonnull
    private ItemValueTableV2.Value doCraft(@Nonnull ItemValueTableV2.Value value, @Nonnull Inventory inventory, @Nonnull Location location, int amount) {
        if (StringNumberUtil.compare(value.getRealNumber(), StringNumberUtil.ZERO) <= 0) {
            return value;
        }

        List<String> availableOutputList = ItemValueTableV2.getInstance().getAvailableOutputList();
        int searchedTime = 0;
        SlimefunItem searchedSlimefunItem = null;
        ItemValueTableV2.Value searchedValue = null;

        for (int i = 0, retryTimes = value.getRealNumber().length(); i < retryTimes; i++) {
            String slimefunItemId = availableOutputList.get(FinalTech.getRandom().nextInt(availableOutputList.size()));
            SlimefunItem slimefunItem = SlimefunItem.getById(slimefunItemId);
            if (slimefunItem == null) {
                continue;
            }

            ItemValueTableV2.Value itemValue = ItemValueTableV2.getInstance().getOrCalItemOutputValue(slimefunItem);
            if (itemValue.getRealNumber().equals(StringNumberUtil.VALUE_INFINITY) && ++searchedTime <= amount) {
                i--;
                continue;
            }

            if (slimefunItem instanceof MultiBlockMachine || RecipeType.MULTIBLOCK.equals(slimefunItem.getRecipeType())) {
                continue;
            }

            if (ItemValueTableV2.getInstance().biggerOrEqual(value, itemValue)) {
                if(searchedValue == null || ItemValueTableV2.getInstance().isEmptyValue(itemValue) || ItemValueTableV2.getInstance().biggerOrEqual(itemValue, searchedValue)) {
                    searchedSlimefunItem = slimefunItem;
                    searchedValue = itemValue;
                }
            }
        }

        if(searchedSlimefunItem != null && searchedValue != null) {
            ItemStack itemStack = searchedSlimefunItem instanceof SimpleValidItem simpleValidItem ? simpleValidItem.getValidItem() : ItemStackUtil.cloneItem(searchedSlimefunItem.getItem(), 1);
            if (InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), itemStack)) {
                if(ItemValueTableV2.getInstance().isEmptyValue(searchedValue)) {
                    value = searchedValue;
                } else {
                    value = ItemValueTableV2.getInstance().subValue(value, searchedValue);
                }
                if(searchedSlimefunItem instanceof ValidItem) {
                    FinalTech.getLogService().addItem(searchedSlimefunItem.getId(), 1, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, location, this.getAddon().getJavaPlugin());
                }
            }
        }

        return value;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
