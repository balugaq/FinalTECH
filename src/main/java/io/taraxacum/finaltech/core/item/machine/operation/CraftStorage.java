package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.MenuUpdater;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.CraftStorageInventory;
import io.taraxacum.finaltech.core.operation.CraftStorageOperation;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.dto.ItemAmountWrapper;
import io.taraxacum.libs.plugin.dto.ItemWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
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
import java.math.BigInteger;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class CraftStorage extends AbstractOperationMachine implements MenuUpdater {
    protected final String keyCraft = "d";
    protected final String keyCount = "c";
    private final Set<String> allowedRecipeTypeId = new HashSet<>(ConfigUtil.getItemStringList(this, "allowed-recipe-type"));
    private final Set<String> allowedId = new HashSet<>();
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    private final Map<String, ItemAmountWrapper[]> slimefunItemRecipeMap = new HashMap<>();
    protected int statusSlot;

    public CraftStorage(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        CraftStorageInventory craftStorageInventory = new CraftStorageInventory(this);
        this.statusSlot = craftStorageInventory.statusSlot;
        return craftStorageInventory;
    }

    @Nonnull
    @Override
    protected BlockBreakHandler onBlockBreak() {
        return new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> drops) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(CraftStorage.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, CraftStorage.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, CraftStorage.this.getOutputSlot());
                    }
                }

                CraftStorage.this.getMachineProcessor().endOperation(location);
            }
        };
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull LocationData locationData) {
        Inventory inventory = FinalTech.getLocationDataService().getInventory(locationData);
        if(inventory == null) {
            return;
        }

        Location location = block.getLocation();
        MachineProcessor<MachineOperation> machineProcessor = this.getMachineProcessor();
        MachineOperation operation = machineProcessor.getOperation(location);

        if (operation == null) {
            this.setupCraftByConfig(locationData);
        }

        if(operation instanceof CraftStorageOperation craftStorageOperation) {
            int input = this.doInput(inventory, craftStorageOperation);
            int output = this.doOutput(inventory, craftStorageOperation);

            if(input != 0 || output != 0) {
                this.updateLocationData(locationData, craftStorageOperation);
            }
        }

        if(!inventory.getViewers().isEmpty()) {
            this.updateInv(inventory, location, this.statusSlot, this);
        }
    }

    @Override
    public void updateInv(@Nonnull Inventory inventory, @Nonnull Location location, int slot, @Nonnull SlimefunItem slimefunItem, @Nonnull String... text) {
        ItemStack itemStack = inventory.getItem(slot);
        MachineOperation machineOperation = this.getMachineProcessor().getOperation(location);
        if (!ItemStackUtil.isItemNull(itemStack) && machineOperation instanceof CraftStorageOperation craftStorageOperation) {
            ItemAmountWrapper[] recipe = craftStorageOperation.getItemAmountWrappers();
            BigInteger[] amount = craftStorageOperation.getAmount();

            List<String> loreList = new ArrayList<>(FinalTech.getLanguageStringList("items", this.getId(), "valid", "input", "title"));

            for(int i = 0; i < amount.length; i++) {
                ItemAmountWrapper itemAmountWrapper = recipe[i];
                loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", this.getId(), "valid", "input", "items"),
                        ItemStackUtil.getItemName(itemAmountWrapper.getItemStack()),
                        String.valueOf(itemAmountWrapper.getAmount()),
                        amount[i].toString()));
            }

            loreList.addAll(FinalTech.getLanguageStringList("items", this.getId(), "valid", "output", "title"));

            loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", this.getId(), "valid", "output", "items"),
                    craftStorageOperation.getSlimefunItem().getItemName(),
                    String.valueOf(craftStorageOperation.getSlimefunItem().getRecipeOutput().getAmount()),
                    craftStorageOperation.calMaxMatch().toString()));

            ItemStackUtil.setItemName(itemStack, FinalTech.getLanguageString("items", this.getId(), "valid", "name"));
            ItemStackUtil.setLore(itemStack, loreList);
            itemStack.setType(Material.GREEN_STAINED_GLASS_PANE);
        } else if (!ItemStackUtil.isItemNull(itemStack) && machineOperation == null) {
            List<String> loreList = new ArrayList<>(FinalTech.getLanguageStringList("items", this.getId(), "invalid", "lore"));

            ItemStackUtil.setItemName(itemStack, FinalTech.getLanguageString("items", this.getId(), "invalid", "name"));
            ItemStackUtil.setLore(itemStack, loreList);
            itemStack.setType(Material.RED_STAINED_GLASS_PANE);
        }
    }

    public int doInput(@Nonnull Inventory inventory, @Nonnull CraftStorageOperation craftStorageOperation) {
        int result = 0;
        Map<Integer, ItemWrapper> slotItemWrapperMap = InventoryUtil.getSlotItemWrapperMap(inventory, this.getInputSlot());
        Set<Integer> skipSlot = new HashSet<>();
        ItemAmountWrapper itemAmountWrapper;
        Iterator<Map.Entry<Integer, ItemWrapper>> iterator;
        int amount;
        for(int i = 0; i < craftStorageOperation.getItemAmountWrappers().length; i++) {
            itemAmountWrapper = craftStorageOperation.getItemAmountWrappers()[i];
            iterator = slotItemWrapperMap.entrySet().iterator();
            amount = 0;
            while (iterator.hasNext()) {
                Map.Entry<Integer, ItemWrapper> entry = iterator.next();
                if(!skipSlot.contains(entry.getKey()) && ItemStackUtil.isItemSimilar(itemAmountWrapper, entry.getValue())) {
                    amount += entry.getValue().getItemStack().getAmount();
                    inventory.clear(entry.getKey());
                    skipSlot.add(entry.getKey());
                    iterator.remove();
                }
            }
            if(amount != 0) {
                craftStorageOperation.getAmount()[i] = craftStorageOperation.getAmount()[i].add(new BigInteger(String.valueOf(amount)));
                result++;
            }
        }
        return result;
    }

    public int doOutput(@Nonnull Inventory inventory, @Nonnull CraftStorageOperation craftStorageOperation) {
        BigInteger[] amount = craftStorageOperation.getAmount();
        ItemAmountWrapper[] itemAmountWrappers = craftStorageOperation.getItemAmountWrappers();
        ItemStack recipeOutput = craftStorageOperation.getSlimefunItem().getRecipeOutput();

        int matchAmount = this.getOutputSlot().length * inventory.getMaxStackSize();

        for(int i = 0; i < amount.length; i++) {
            if(matchAmount == 0) {
                break;
            }
            matchAmount = Math.min(amount[i].min(new BigInteger(String.valueOf(matchAmount * itemAmountWrappers[i].getAmount()))).intValue() / itemAmountWrappers[i].getAmount(), matchAmount);
        }

        if(matchAmount > 0) {
            matchAmount = InventoryUtil.tryPushItem(inventory, this.getOutputSlot(), matchAmount, recipeOutput);
            if(matchAmount > 0) {
                for(int i = 0; i < amount.length; i++) {
                    amount[i] = amount[i].subtract(new BigInteger(String.valueOf(matchAmount * itemAmountWrappers[i].getAmount())));
                }
            }
        }

        return matchAmount;
    }

    public void updateLocationData(@Nonnull LocationData locationData, @Nonnull CraftStorageOperation craftStorageOperation) {
        for(int i = 0; i < craftStorageOperation.getAmount().length; i++) {
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount + i, craftStorageOperation.getAmount()[i].toString());
        }
    }

    /**
     * Use {@link #calAllowed(SlimefunItem)} before this
     */
    @Nullable
    public MachineOperation setupCraft(@Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
        if(this.getMachineProcessor().getOperation(location) != null) {
            return null;
        }

        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(locationData != null) {
            int i = 0;
            while (FinalTech.getLocationDataService().getLocationData(locationData, this.keyCount + i) != null) {
                FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount + i, null);
            }

            ItemAmountWrapper[] recipe = this.getItemRecipe(slimefunItem);
            CraftStorageOperation craftStorageOperation = new CraftStorageOperation(slimefunItem, recipe);
            FinalTech.getLocationDataService().setLocationData(locationData, this.keyCraft, slimefunItem.getId());
            this.getMachineProcessor().startOperation(location, craftStorageOperation);
            return craftStorageOperation;
        }

        return null;
    }

    @Nullable
    public MachineOperation setupCraftByConfig(@Nonnull LocationData locationData) {
        MachineOperation machineOperation = null;
        String id = FinalTech.getLocationDataService().getLocationData(locationData, this.keyCraft);
        if(id != null) {
            SlimefunItem slimefunItem = SlimefunItem.getById(id);
            if(slimefunItem != null && this.calAllowed(slimefunItem)) {
                Map<Integer, BigInteger> amountMap = new HashMap<>();
                for (String key : FinalTech.getLocationDataService().getKeys(locationData)) {
                    if (key.startsWith(this.keyCount)) {
                        amountMap.put(Integer.parseInt(key.substring(this.keyCount.length())), new BigInteger(FinalTech.getLocationDataService().getLocationData(locationData, key)));
                    }
                }
                ItemAmountWrapper[] recipe = this.getItemRecipe(slimefunItem);
                if(amountMap.size() == recipe.length) {
                    CraftStorageOperation craftStorageOperation = new CraftStorageOperation(slimefunItem, recipe);

                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyCraft, slimefunItem.getId());
                    for(Map.Entry<Integer, BigInteger> entry : amountMap.entrySet()) {
                        craftStorageOperation.getAmount()[entry.getKey()] = entry.getValue();
                    }
                    this.getMachineProcessor().startOperation(locationData.getLocation(), craftStorageOperation);
                    machineOperation = craftStorageOperation;
                } else {
                    for(int i : amountMap.keySet()) {
                        FinalTech.getLocationDataService().setLocationData(locationData, this.keyCount + i, null);
                    }
                }
            }
        }

        return machineOperation;
    }

    @Nonnull
    public ItemAmountWrapper[] getItemRecipe(@Nonnull SlimefunItem slimefunItem) {
        return this.slimefunItemRecipeMap.computeIfAbsent(slimefunItem.getId(), s -> {
            ItemStack[] itemStacks = slimefunItem.getRecipe();
            return ItemStackUtil.calItemArrayWithAmount(itemStacks);
        });
    }

    public boolean calAllowed(@Nonnull SlimefunItem slimefunItem) {
        if(this.allowedId.contains(slimefunItem.getId())) {
            return true;
        } else if(this.notAllowedId.contains(slimefunItem.getId())) {
            return false;
        }

        if(!this.allowedRecipeTypeId.contains(slimefunItem.getRecipeType().getKey().getKey())) {
            this.notAllowedId.add(slimefunItem.getId());
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
                this.notAllowedId.add(slimefunItem.getId());
                return false;
            } else if(sfItem instanceof ValidItem) {
                this.notAllowedId.add(slimefunItem.getId());
                return false;
            }
        }
        if (!hasRecipe) {
            this.notAllowedId.add(slimefunItem.getId());
            return false;
        }

        this.allowedId.add(slimefunItem.getId());
        return true;
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipeWithBorder(FinalTech.getLanguageManager(), this);

        for(String id : this.allowedRecipeTypeId) {
            RecipeType recipeType = RecipeTypeRegistry.getInstance().getRecipeTypeById(id);
            if(recipeType != null) {
                this.registerDescriptiveRecipe(recipeType.toItem());
            }
        }
    }
}
