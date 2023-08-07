package io.taraxacum.finaltech.core.item.machine.template.broken;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitInjectableItem;
import io.taraxacum.finaltech.core.interfaces.LogicInjectableItem;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.limit.lock.BasicMachineInventory;
import io.taraxacum.finaltech.core.inventory.limit.lock.BrokenMachineInventory;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.finaltech.core.option.MachineMaxStack;
import io.taraxacum.finaltech.core.option.MachineRecipeLock;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.dto.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.slimefun.dto.AdvancedCraft;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Final_ROOT
 */
public abstract class AbstractBrokenMachine extends AbstractMachine implements RecipeItem, DigitInjectableItem, LogicInjectableItem {
    private final String offsetKey = "offset";
    private int recipeLockSlot;
    private BiConsumer<Inventory, LocationData> logicInjectInventoryUpdater;
    private BiConsumer<Inventory, LocationData> digitInjectInventoryUpdater;

    @ParametersAreNonnullByDefault
    protected AbstractBrokenMachine(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        BrokenMachineInventory brokenMachineInventory = new BrokenMachineInventory(this);
        this.recipeLockSlot = brokenMachineInventory.recipeLockSlot;
        this.logicInjectInventoryUpdater = brokenMachineInventory::updateMachineRecipeLock;
        this.digitInjectInventoryUpdater = brokenMachineInventory::updateMachineMaxStack;
        return brokenMachineInventory;
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
    protected final void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }
        boolean hasViewer = !inventory.getViewers().isEmpty();

        String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, this.offsetKey);
        int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
        String recipeLockStr = FinalTech.getLocationDataService().getLocationData(locationData, MachineRecipeLock.KEY);
        int recipeLock = recipeLockStr == null ? -2 : Integer.parseInt(recipeLockStr);
        int quantityModule = 1;
        List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(this.getId());
        if (recipeLock >= 0) {
            List<AdvancedMachineRecipe> finalAdvancedMachineRecipeList = advancedMachineRecipeList;
            advancedMachineRecipeList = new ArrayList<>(1);
            recipeLock = recipeLock % finalAdvancedMachineRecipeList.size();
            advancedMachineRecipeList.add(finalAdvancedMachineRecipeList.get(recipeLock));
        }

        InventoryUtil.stockSlots(inventory, this.getInputSlot());

        AdvancedCraft craft = AdvancedCraft.craftAsc(inventory, this.getInputSlot(), advancedMachineRecipeList, quantityModule, offset);
        if (craft != null) {
            int matchAmount = InventoryUtil.tryPushItem(inventory, this.getOutputSlot(), craft.getMatchCount(), craft.getOutputItemList());
            if (matchAmount > 0) {
                craft.setMatchCount(matchAmount);
                craft.consumeItem(inventory);
                if (recipeLock == Integer.parseInt(MachineRecipeLock.VALUE_UNLOCK)) {
                    ItemStack itemStack = inventory.getItem(this.recipeLockSlot);
                    if(hasViewer) {
                        MachineRecipeLock.OPTION.updateLore(itemStack, String.valueOf(craft.getOffset()), this);
                    }
                    FinalTech.getLocationDataService().setLocationData(locationData, MachineRecipeLock.KEY, String.valueOf(craft.getOffset()));
                } else if (recipeLock == Integer.parseInt(MachineRecipeLock.VALUE_LOCK_OFF)) {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.offsetKey, String.valueOf(craft.getOffset()));
                }
                InventoryUtil.stockSlots(inventory, this.getOutputSlot());
            }
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void injectDigit(@Nonnull LocationData locationData, int digit) {
        MachineMaxStack.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, String.valueOf(digit));

        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory != null) {
            this.digitInjectInventoryUpdater.accept(inventory, locationData);
        }
    }

    @Override
    public void injectLogic(@Nonnull LocationData locationData, boolean logic) {
        if (logic) {
            MachineRecipeLock.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, MachineRecipeLock.VALUE_UNLOCK);
        } else {
            MachineRecipeLock.OPTION.setOrClearValue(FinalTech.getLocationDataService(), locationData, MachineRecipeLock.VALUE_LOCK_OFF);
        }

        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if (inventory != null) {
            this.logicInjectInventoryUpdater.accept(inventory, locationData);
        }
    }
}
