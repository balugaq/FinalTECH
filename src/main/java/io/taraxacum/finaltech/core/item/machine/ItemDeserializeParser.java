package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.ItemDeserializeParserInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is a slimefun machine
 * it will be used in gameplay
 * It's not a function class!
 * @author Final_ROOT
 */
@Deprecated
public class ItemDeserializeParser extends AbstractMachine implements RecipeItem {
    public ItemDeserializeParser(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new ItemDeserializeParserInventory(this);
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
        if(inventory == null || InventoryUtil.slotCount(inventory, this.getOutputSlot()) == this.getOutputSlot().length) {
            return;
        }

        for (int slot : this.getInputSlot()) {
            ItemStack itemStack = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack) && FinalTechItems.COPY_CARD.verifyItem(itemStack)) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                ItemStack stringItem = StringItemUtil.parseItemInCard(itemMeta);
                if (!ItemStackUtil.isItemNull(stringItem)) {
                    int amount = itemStack.getAmount();
                    String amountInCardStr = StringItemUtil.parseAmountInCard(itemMeta);
                    if(StringNumberUtil.compare(amountInCardStr, "3456") >= 0) {
                        amount = 3456;
                    } else {
                        amount *= Integer.parseInt(amountInCardStr);
                    }
                    if(amount <= 0) {
                        return;
                    }

                    InventoryUtil.tryPushItem(inventory, this.getOutputSlot(), amount, new ItemAmountWrapper(stringItem, 1));
                }
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                ConstantTableUtil.SLIMEFUN_TICK_INTERVAL);
    }
}
