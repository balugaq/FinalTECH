package io.taraxacum.finaltech.core.item.machine.operation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.inventory.simple.AdvancedCraftStorageInventory;
import io.taraxacum.finaltech.core.operation.CraftStorageOperation;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
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
public class AdvancedCraftStorage extends CraftStorage {
    protected final String KEY_PARSE_ID = "pi_";
    protected final String KEY_PARSE_AMOUNT = "pa_";
    private final Map<String, String> parseIdMap;

    private final Map<String, String> recipeReplaceMap;

    private int[] parseSlot;

    public AdvancedCraftStorage(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
        this.parseIdMap = new HashMap<>();
        this.recipeReplaceMap = new HashMap<>();

        List<String> machineIdList = ConfigUtil.getItemStringList(this, "parse-machine");
        for(String machineId : machineIdList) {
            if(FinalTech.getItemManager().containPath(this.getId(), "parse-machine", machineId)) {
                String shortId = FinalTech.getItemManager().getString(this.getId(), "parse-machine", machineId);
                this.parseIdMap.put(shortId, machineId);
            }
        }

        List<String> replacedIdList = ConfigUtil.getItemStringList(this, "recipe-replace");
        for(String replacedId : replacedIdList) {
            if(FinalTech.getItemManager().containPath(this.getId(), "recipe-replace", replacedId)) {
                String targetId = FinalTech.getItemManager().getString(this.getId(), "recipe-replace", replacedId);
                this.recipeReplaceMap.put(replacedId, targetId);
            }
        }
    }

    @Nullable
    @Override
    protected AbstractMachineInventory setMachineInventory() {
        AdvancedCraftStorageInventory advancedCraftStorageInventory = new AdvancedCraftStorageInventory(this);
        this.parseSlot = advancedCraftStorageInventory.parseSlot;
        this.statusSlot = advancedCraftStorageInventory.statusSlot;
        return advancedCraftStorageInventory;
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
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(AdvancedCraftStorage.this.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, AdvancedCraftStorage.this.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, AdvancedCraftStorage.this.getOutputSlot());
                        InventoryUtil.dropItems(inventory, location, AdvancedCraftStorage.this.parseSlot);
                    }
                }

                AdvancedCraftStorage.this.getMachineProcessor().endOperation(location);
            }
        };
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

            List<ItemAmountWrapper> recipeList = ItemStackUtil.calItemListWithAmount(slimefunItem.getRecipe());
            Inventory inventory = FinalTech.getLocationDataService().getRawInventory(locationData);
            for(i = 0; i < this.parseSlot.length; i++) {
                int slot = this.parseSlot[i];
                ItemStack itemStack = inventory.getItem(slot);
                SlimefunItem sfItem = SlimefunItem.getByItem(itemStack);
                if(sfItem != null) {
                    boolean match = false;
                    for(Map.Entry<String, String> entry : this.parseIdMap.entrySet()) {
                        if(sfItem.getId().equals(entry.getValue())) {
                            FinalTech.getLocationDataService().setLocationData(locationData, this.KEY_PARSE_ID + i, entry.getKey());
                            FinalTech.getLocationDataService().setLocationData(locationData, this.KEY_PARSE_AMOUNT + i, String.valueOf(itemStack.getAmount()));

                            String machineId = this.recipeReplaceMap.getOrDefault(sfItem.getId(), sfItem.getId());
                            List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(machineId);

                            recipeList = MachineUtil.calParsed(recipeList, advancedMachineRecipeList, itemStack.getAmount());

                            match = true;
                            break;
                        }
                    }
                    if(!match) {
                        FinalTech.getLocationDataService().setLocationData(locationData, this.KEY_PARSE_ID + i, null);
                        FinalTech.getLocationDataService().setLocationData(locationData, this.KEY_PARSE_AMOUNT + i, null);
                    }
                }
            }

            CraftStorageOperation craftStorageOperation = new CraftStorageOperation(slimefunItem, recipeList.toArray(new ItemAmountWrapper[0]));
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
                for(String key : FinalTech.getLocationDataService().getKeys(locationData)) {
                    if(key.startsWith(this.keyCount)) {
                        amountMap.put(Integer.parseInt(key.substring(this.keyCount.length())), new BigInteger(FinalTech.getLocationDataService().getLocationData(locationData, key)));
                    }
                }

                List<ItemAmountWrapper> recipeList = ItemStackUtil.calItemListWithAmount(slimefunItem.getRecipe());
                for(int i = 0; i < this.parseSlot.length; i++) {
                    String machineShortId = FinalTech.getLocationDataService().getLocationData(locationData, this.KEY_PARSE_ID + i);
                    String amountStr = FinalTech.getLocationDataService().getLocationData(locationData, this.KEY_PARSE_AMOUNT + i);
                    if(machineShortId != null && amountStr != null) {
                        int amount = Integer.parseInt(amountStr);
                        String machineId = this.parseIdMap.get(machineShortId);
                        machineId = this.recipeReplaceMap.getOrDefault(machineId, machineId);
                        List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(machineId);
                        recipeList = MachineUtil.calParsed(recipeList, advancedMachineRecipeList, amount);
                    }
                }

                if(amountMap.size() == recipeList.size()) {
                    CraftStorageOperation craftStorageOperation = new CraftStorageOperation(slimefunItem, recipeList.toArray(new ItemAmountWrapper[0]));
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

    @Override
    public void registerDefaultRecipes() {
        super.registerDefaultRecipes();

        this.registerBorder();

        for(String id : this.parseIdMap.values()) {
            SlimefunItem slimefunItem = SlimefunItem.getById(id);
            if(slimefunItem != null) {
                this.registerDescriptiveRecipe(slimefunItem.getItem());
            }
        }
    }
}
