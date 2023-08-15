package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.MatrixItemDismantleTableInventory;
import io.taraxacum.finaltech.core.item.unusable.ReplaceableCard;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class MatrixItemDismantleTable extends AbstractMachine implements RecipeItem {
    private final Set<String> notAllowedRecipeType = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-recipe-type"));
    private final Set<String> allowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "allowed-id"));
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));

    public MatrixItemDismantleTable(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        return new MatrixItemDismantleTableInventory(this);
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
        if (inventory != null && InventoryUtil.isEmpty(inventory, this.getOutputSlot())) {
            ItemStack itemStack = inventory.getItem(this.getInputSlot()[0]);
            SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
            if (sfItem != null && this.calAllowed(sfItem) && itemStack.getAmount() >= sfItem.getRecipeOutput().getAmount()) {
                boolean verify;
                if(sfItem instanceof ValidItem validItem) {
                    verify = validItem.verifyItem(itemStack);
                } else {
                    verify = ItemStackUtil.isItemSimilar(itemStack, sfItem.getRecipeOutput()) && ItemStackUtil.isEnchantmentSame(itemStack, sfItem.getRecipeOutput());
                }
                if(verify) {
                    int amount = itemStack.getAmount() / sfItem.getRecipeOutput().getAmount();
                    for (ItemStack outputItem : sfItem.getRecipe()) {
                        if (!ItemStackUtil.isItemNull(outputItem)) {
                            amount = Math.min(amount, outputItem.getMaxStackSize() / outputItem.getAmount());
                        }
                    }
                    itemStack.setAmount(itemStack.getAmount() - sfItem.getRecipeOutput().getAmount() * amount);
                    if(sfItem instanceof ValidItem) {
                        FinalTech.getLogService().subItem(sfItem.getId(), sfItem.getRecipeOutput().getAmount() * amount, this.getId(), LogSourceType.SLIMEFUN_MACHINE, null, block.getLocation(), this.getAddon().getJavaPlugin());
                    }
                    for (int i = 0; i < this.getOutputSlot().length && i < sfItem.getRecipe().length; i++) {
                        if (!ItemStackUtil.isItemNull(sfItem.getRecipe()[i])) {
                            ItemStack outputItem;
                            ReplaceableCard replaceableCard = RecipeUtil.getReplaceableCard(sfItem.getRecipe()[i]);
                            if (replaceableCard != null && replaceableCard.getExtraSourceMaterial() != null) {
                                outputItem = replaceableCard.getItem();
                            } else {
                                outputItem = sfItem.getRecipe()[i];
                            }
                            inventory.setItem(this.getOutputSlot()[i], outputItem);
                            outputItem = inventory.getItem(this.getOutputSlot()[i]);
                            outputItem.setAmount(outputItem.getAmount() * amount);
                        }
                    }
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
        RecipeTypeRegistry.getInstance().reload();

        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for (RecipeType recipeType : RecipeTypeRegistry.getInstance().getRecipeTypeSet()) {
            if (!this.notAllowedRecipeType.contains(recipeType.getKey().getKey()) && !ItemStackUtil.isItemNull(recipeType.toItem())) {
                this.registerDescriptiveRecipe(recipeType.toItem());
            }
        }
    }

    private boolean calAllowed(@Nonnull SlimefunItem slimefunItem) {
        if (this.allowedId.contains(slimefunItem.getId())) {
            return true;
        } else if (this.notAllowedId.contains(slimefunItem.getId())) {
            return false;
        } else {
            String slimefunItemId = slimefunItem.getId();
            synchronized (this) {
                if (this.allowedId.contains(slimefunItemId)) {
                    return true;
                } else if (this.notAllowedId.contains(slimefunItemId)) {
                    return false;
                }

                if (this.notAllowedRecipeType.contains(slimefunItem.getRecipeType().getKey().getKey())) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                if (slimefunItem.getRecipe().length > this.getOutputSlot().length) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                boolean hasRecipe = false;
                for (ItemStack itemStack : slimefunItem.getRecipe()) {
                    if (ItemStackUtil.isItemNull(itemStack)) {
                        continue;
                    }
                    hasRecipe = true;
                    SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                    if (sfItem == null && !ItemStackUtil.isItemSimilar(itemStack, new ItemStack(itemStack.getType()))) {
                        this.notAllowedId.add(slimefunItemId);
                        return false;
                    } else if(sfItem instanceof ValidItem) {
                        this.notAllowedId.add(slimefunItemId);
                        return false;
                    }
                }
                if (!hasRecipe) {
                    this.notAllowedId.add(slimefunItemId);
                    return false;
                }

                this.allowedId.add(slimefunItemId);
                return true;
            }
        }
    }
}
