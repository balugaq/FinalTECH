package io.taraxacum.finaltech.core.item.machine.range.point.face;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.LocationMachine;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.cargo.AdvancedAutoCraftInventory;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.core.option.SlotSearchOrder;
import io.taraxacum.finaltech.core.option.SlotSearchSize;
import io.taraxacum.finaltech.util.*;
import io.taraxacum.libs.plugin.dto.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.dto.InvWithSlots;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.AdvancedCraft;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class AdvancedAutoCraft extends AbstractFaceMachine implements RecipeItem, LocationMachine {
    public final Map<Location, AdvancedMachineRecipe> locationRecipeMap = new HashMap<>();
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private AdvancedAutoCraftInventory advancedAutoCraftInventory;

    public AdvancedAutoCraft(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        this.advancedAutoCraftInventory = new AdvancedAutoCraftInventory(this, this.locationRecipeMap);
        return this.advancedAutoCraftInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.advancedAutoCraftInventory.parseItemSlot, this.advancedAutoCraftInventory.moduleSlot);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }
        boolean hasViewer = !inventory.getViewers().isEmpty();

        Location location = block.getLocation();
        AdvancedMachineRecipe machineRecipe = this.locationRecipeMap.get(location);
        if (machineRecipe == null) {
            return;
        }

        Block containerBlock = block.getRelative(BlockFace.DOWN);
        LocationData containerLocationData = FinalTech.getLocationDataService().getLocationData(containerBlock.getLocation());
        if (containerLocationData == null) {
            return;
        }
        Inventory containerInventory = FinalTech.getLocationDataService().getInventory(containerLocationData);
        if(containerInventory == null) {
            return;
        }

        String containerId = LocationDataUtil.getId(FinalTech.getLocationDataService(), containerLocationData);
        if (containerId != null && !this.notAllowedId.contains(containerId)) {
            Runnable runnable = () -> {
                LocationData tempLocationData = FinalTech.getLocationDataService().getLocationData(containerLocationData.getLocation());
                if(tempLocationData == null || !containerId.equals(LocationDataUtil.getId(FinalTech.getLocationDataService(), tempLocationData))) {
                    return;
                }

                InvWithSlots inputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), containerBlock, SlotSearchSize.INPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), SlotSearchOrder.VALUE_ASCENT);
                InvWithSlots outputMap = CargoUtil.getInvWithSlots(FinalTech.getLocationDataService(), containerBlock, SlotSearchSize.OUTPUT_OPTION.getOrDefaultValue(FinalTech.getLocationDataService(), locationData), SlotSearchOrder.VALUE_ASCENT);
                if (inputMap == null || outputMap == null || inputMap.getSlots().length == 0 || outputMap.getSlots().length == 0) {
                    return;
                }

                int[] inputSlots = inputMap.getSlots();
                int[] outputSlots = outputMap.getSlots();

                int quantity = Icon.updateQuantityModule(inventory, hasViewer, this.advancedAutoCraftInventory.moduleSlot, this.advancedAutoCraftInventory.statusSlot);

                AdvancedCraft craft = AdvancedCraft.craftAsc(containerInventory, inputSlots, List.of(machineRecipe), quantity, 0);
                if (craft != null) {
                    int matchCount = InventoryUtil.tryPushItem(containerInventory, outputSlots, craft.getMatchCount(), craft.getOutputItemList());
                    if (matchCount > 0) {
                        craft.setMatchCount(matchCount);
                        craft.consumeItem(containerInventory);
                    }
                }
            };

            BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(containerId), runnable, location, containerBlock.getLocation());
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public int getRegisterRecipeDelay() {
        return 2;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        // TODO
        this.advancedAutoCraftInventory.registerRecipe();
        for (String id : this.advancedAutoCraftInventory.getRecipeMap().keySet()) {
            SlimefunItem slimefunItem = SlimefunItem.getById(id);
            if (slimefunItem != null) {
                ItemStack itemStack = slimefunItem.getItem();
                if (!ItemStackUtil.isItemNull(itemStack)) {
                    this.registerDescriptiveRecipe(itemStack);
                }
            }

            this.registerBorder();

            for (String recipeTypeId : this.advancedAutoCraftInventory.getRecipeTypeIdList()) {
                RecipeType recipeType = RecipeTypeRegistry.getInstance().getRecipeTypeById(recipeTypeId);
                if (recipeType != null) {
                    ItemStack itemStack = JavaUtil.getFirstNotNull(recipeType.toItem(), recipeType.getMachine().getItem());
                    if (!ItemStackUtil.isItemNull(itemStack)) {
                        this.registerDescriptiveRecipe(itemStack);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    protected BlockFace getBlockFace() {
        return BlockFace.DOWN;
    }

    @Override
    public Location[] getLocations(@Nonnull Location sourceLocation) {
        return new Location[] {new Location(sourceLocation.getWorld(), sourceLocation.getX(), sourceLocation.getY() - 1, sourceLocation.getZ())};
    }
}
