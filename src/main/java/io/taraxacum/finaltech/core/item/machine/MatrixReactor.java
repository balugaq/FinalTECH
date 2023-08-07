package io.taraxacum.finaltech.core.item.machine;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.MatrixReactorInventory;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.ItemAmountWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class MatrixReactor extends AbstractMachine implements RecipeItem, MenuUpdater {
    private final String keyItem = "i";
    private final String keyCount = "c";
    private final int difficulty = ConfigUtil.getOrDefaultItemSetting(80, this, "difficulty");
    private final Set<String> notAllowedIdList = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private int[] itemPhonyInputSlot;
    private int[] itemInputSlot;
    private int statusSlot;
    private int[] orderedDustSlot;
    private int[] unorderedDustSlot;

    public MatrixReactor(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        MatrixReactorInventory matrixReactorInventory = new MatrixReactorInventory(this);
        this.itemPhonyInputSlot = matrixReactorInventory.itemPhonyInputSlot;
        this.itemInputSlot = matrixReactorInventory.itemInputSlot;
        this.statusSlot = matrixReactorInventory.statusSlot;
        this.orderedDustSlot = matrixReactorInventory.orderedDustInputSlot;
        this.unorderedDustSlot = matrixReactorInventory.unorderedDustInputSlot;
        return matrixReactorInventory;
    }

    @Nonnull
    @Override
    protected BlockPlaceHandler onBlockPlace() {
        return MachineUtil.BLOCK_PLACE_HANDLER_PLACER_DENY;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return MachineUtil.simpleBlockBreakerHandler(FinalTech.getLocationDataService(), this, this.itemPhonyInputSlot);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        ItemStack itemStack = inventory.getItem(this.itemInputSlot[0]);

        if (ItemStackUtil.isItemNull(itemStack)) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, "0");
            if (!inventory.getViewers().isEmpty()) {
                this.updateInv(inventory, this.statusSlot, this,
                        "0",
                        String.valueOf(this.difficulty));
            }
            return;
        } else if (!this.allowedItem(itemStack)) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, null);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, "0");
            if (!inventory.getViewers().isEmpty()) {
                this.updateInv(inventory, this.statusSlot, this,
                        "0",
                        String.valueOf(this.difficulty));
            }
            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            // TODO safe to do this?
            Location location = locationData.getLocation();
            javaPlugin.getServer().getScheduler().runTask(javaPlugin, () -> InventoryUtil.dropItems(inventory, location, this.itemInputSlot));
            return;
        }

        ItemStack stringItem = null;
        String keyItemStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyItem);
        if (keyItemStr != null) {
            stringItem = ItemStackUtil.stringToItemStack(keyItemStr);
        }

        int amount = itemStack.getAmount();
        int[] orderedDustItemSlots = new int[this.orderedDustSlot.length];
        int[] unorderedDustItemSlots = new int[this.unorderedDustSlot.length];
        int orderedDustItemSlotsP = 0;
        int orderedDustItemCount = 0;
        int unorderedDustItemSlotsP = 0;
        int unorderedDustItemCount = 0;

        for (int slot : this.orderedDustSlot) {
            ItemStack itemStack1 = inventory.getItem(slot);
            if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.ORDERED_DUST.verifyItem(itemStack1)) {
                orderedDustItemSlots[orderedDustItemSlotsP++] = slot;
                orderedDustItemCount += itemStack1.getAmount();
                if (orderedDustItemCount > amount) {
                    break;
                }
            }
        }

        if (orderedDustItemCount >= amount) {
            for (int slot : this.unorderedDustSlot) {
                ItemStack itemStack1 = inventory.getItem(slot);
                if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.UNORDERED_DUST.verifyItem(itemStack1)) {
                    unorderedDustItemSlots[unorderedDustItemSlotsP++] = slot;
                    unorderedDustItemCount += itemStack1.getAmount();
                    if (unorderedDustItemCount > amount) {
                        break;
                    }
                }
            }
        }

        boolean match = orderedDustItemCount >= amount && unorderedDustItemCount >= amount;

        if (!match) {
            String keyCountStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyCount);
            int count = keyCountStr != null ? Integer.parseInt(keyCountStr) : 0;
            count = count > 0 ? count - 1 : 0;
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, String.valueOf(count));
        } else  {
            orderedDustItemCount = amount;
            for (int slot : orderedDustItemSlots) {
                ItemStack dustItemStack = inventory.getItem(slot);
                int n = Math.min(dustItemStack.getAmount(), orderedDustItemCount);
                dustItemStack.setAmount(dustItemStack.getAmount() - n);
                orderedDustItemCount -= n;
                if (orderedDustItemCount == 0) {
                    break;
                }
            }

            unorderedDustItemCount = amount;
            for (int slot : unorderedDustItemSlots) {
                ItemStack dustItemStack = inventory.getItem(slot);
                int n = Math.min(dustItemStack.getAmount(), unorderedDustItemCount);
                dustItemStack.setAmount(dustItemStack.getAmount() - n);
                unorderedDustItemCount -= n;
                if (unorderedDustItemCount == 0) {
                    break;
                }
            }

            if (ItemStackUtil.isItemNull(stringItem) || !ItemStackUtil.isItemSimilar(itemStack, stringItem) || itemStack.getAmount() != stringItem.getAmount()) {
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, ItemStackUtil.itemStackToString(itemStack));

                int count;
                ItemStack itemStack1 = inventory.getItem(this.itemPhonyInputSlot[0]);
                if (!ItemStackUtil.isItemNull(itemStack1) && FinalTechItems.ITEM_PHONY.verifyItem(itemStack1)) {
                    ItemStack itemPhony = inventory.getItem(this.itemPhonyInputSlot[0]);
                    itemPhony.setAmount(itemPhony.getAmount() - 1);
                    count = 1;
                } else {
                    count = FinalTech.getRandom().nextBoolean() ? 1 : 0;
                }

                FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, String.valueOf(count));
            } else {
                String keyCountStr = FinalTech.getLocationDataService().getLocationData(locationData, this.keyCount);
                int count = keyCountStr != null ? Integer.parseInt(keyCountStr) : 0;
                ItemStack itemStack1 = inventory.getItem(this.itemPhonyInputSlot[0]);
                if (!ItemStackUtil.isItemNull(itemStack1)
                        && FinalTechItems.ITEM_PHONY.verifyItem(itemStack1)
                        && itemStack1.getAmount() >= amount + count) {
                    itemStack1.setAmount(itemStack1.getAmount() - count - amount);
                    // TODO item log
                    count++;
                } else {
                    count = FinalTech.getRandom().nextBoolean() ? count - 1 : count + 1;
                }

                if (count + itemStack.getAmount() >= this.difficulty) {
                    if(InventoryUtil.tryPushAllItem(inventory, this.getOutputSlot(), new ItemAmountWrapper(itemStack, 1))) {
                        FinalTech.getLocationDataService().setLocationData(locationData, this.keyItem, null);
                        FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, "0");
                        if (!inventory.getViewers().isEmpty()) {
                            this.updateInv(inventory, this.statusSlot, this,
                                    "0",
                                    String.valueOf(difficulty));
                        }
                        return;
                    }

                    count = count < this.difficulty ? count + 1 : this.difficulty;
                }

                count = Math.max(count, 0);
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount, String.valueOf(count));
            }
        }

        if (!inventory.getViewers().isEmpty()) {
            String countStr = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(locationData, this.keyCount), "0");
            int count = Integer.parseInt(countStr);
            this.updateInv(inventory, this.statusSlot, this,
                    String.valueOf(amount + count),
                    String.valueOf(this.difficulty));
        }
    }

    @Override
    protected boolean isSynchronized() {
        return false;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this,
                String.valueOf(this.difficulty),
                ConstantTableUtil.SLIMEFUN_TICK_INTERVAL);
    }

    private boolean allowedItem(@Nonnull ItemStack item) {
        if (Tag.SHULKER_BOXES.isTagged(item.getType()) || Material.BUNDLE.equals(item.getType())) {
            return false;
        }

        if (item.hasItemMeta()) {
            ItemMeta itemMeta = item.getItemMeta();
            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            if (persistentDataContainer.getKeys().size() > 0) {
                for (NamespacedKey namespacedKey : persistentDataContainer.getKeys()) {
                    if (!"slimefun".equals(namespacedKey.getNamespace())) {
                        return false;
                    }
                }
            }
        }

        SlimefunItem slimefunItem = SlimefunItem.getByItem(item);
        return slimefunItem == null || !this.notAllowedIdList.contains(slimefunItem.getId());
    }
}
