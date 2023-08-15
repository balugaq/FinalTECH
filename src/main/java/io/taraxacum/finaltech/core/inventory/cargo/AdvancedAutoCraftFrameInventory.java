package io.taraxacum.finaltech.core.inventory.cargo;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.inventory.AbstractMachineInventory;
import io.taraxacum.finaltech.core.option.Icon;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.finaltech.util.StringItemUtil;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.inventory.SimpleVirtualInventory;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import io.taraxacum.libs.slimefun.dto.RecipeTypeRegistry;
import io.taraxacum.libs.slimefun.util.SfItemUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class AdvancedAutoCraftFrameInventory extends AbstractMachineInventory implements LogicInventory {
    private final int[] border = new int[] {27, 28, 29, 36, 38, 47};
    private final int[] inputBorder = new int[] {18, 19, 20, 21, 22, 23, 24, 25, 26};
    private final int[] outputBorder = new int[] {33, 34, 35, 42, 44, 51, 52, 53};
    private final int[] machineSlot = new int[] {0, 1, 2, 3, 4, 5, 6 ,7 ,8 ,9 ,10, 11, 12, 13, 14, 15 ,16 ,17};
    private final int[] contentSlot = new int[0];

    private final int parseSlot = 45;
    public final int parseItemSlot = 46;
    private final ItemStack parseIcon = ItemStackUtil.newItemStack(Material.OBSERVER,
            FinalTech.getLanguageString("items", this.getId(), "parse-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-icon", "lore"));

    private final int wikiSlot = 37;
    private final ItemStack wikiIcon = ItemStackUtil.newItemStack(Material.KNOWLEDGE_BOOK,
            FinalTech.getLanguageString("items", this.getId(), "wiki-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "wiki-icon", "lore"));

    private final int[] itemInputSlot = new int[] {30, 31, 32, 39, 40, 41, 48, 49, 50};
    private final int itemOutputSlot = 43;

    private final ItemStack parseFailedIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-failed-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-failed-icon", "lore"));
    private final ItemStack parseSuccessIcon = ItemStackUtil.newItemStack(Material.GREEN_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-success-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-success-icon", "lore"));
    private final ItemStack parseExtendIcon = ItemStackUtil.newItemStack(Material.PURPLE_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-extend-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-extend-icon", "lore"));

    /**
     * RecipeType.getKey().getKey()
     * It will be used to analyse the recipe type in first step.
     */
    private final Set<String> recipeTypeIdList;

    /**
     * key: slimefun item id
     * value: the machine recipe list it handles
     */
    private final Map<String, List<AdvancedMachineRecipe>> recipeMap = new HashMap<>();
    private final Map<Location, AdvancedMachineRecipe> locationRecipeMap;


    public AdvancedAutoCraftFrameInventory(@Nonnull SlimefunItem slimefunItem, @Nonnull Map<Location, AdvancedMachineRecipe> locationRecipeMap) {
        super(slimefunItem);
        this.locationRecipeMap = locationRecipeMap;
        this.recipeTypeIdList = new HashSet<>(ConfigUtil.getItemStringList(slimefunItem.getId(), "recipe-type-id"));
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return this.inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return this.outputBorder;
    }

    @Override
    @Nullable
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return switch (slot) {
            case parseSlot -> inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                AdvancedAutoCraftFrameInventory.this.updateInventory(inventoryClickEvent.getInventory(), location);
            };
            case 50 -> inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                AdvancedMachineRecipe advancedMachineRecipe = this.locationRecipeMap.get(location);
                if (advancedMachineRecipe != null
                        && advancedMachineRecipe.getInput().length > 9
                        && inventoryClickEvent.getWhoClicked() instanceof Player player) {
                    SimpleVirtualInventory virtualInventory = new SimpleVirtualInventory(54, "");
                    for (int slot1 = 0; slot1 < 54 || slot1 < advancedMachineRecipe.getInput().length; slot1++) {
                        if (advancedMachineRecipe.getInput().length > slot1) {
                            int amount = advancedMachineRecipe.getInput()[slot1].getAmount();
                            ItemStack itemStack = MachineUtil.cloneAsDescriptiveItemWithLore(advancedMachineRecipe.getInput()[slot1].getItemStack(),
                                    FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", this.getId(), "parse-amount"),
                                            String.valueOf(amount)));
                            itemStack.setAmount(Math.min(amount, 64));
                            virtualInventory.drawBackground(slot1, itemStack);
                        } else {
                            virtualInventory.drawBackground(slot1, ItemStackUtil.AIR);
                        }
                    }

                    virtualInventory.open(player);
                }
            };
            default -> super.onClick(location, slot);
        };
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(parseSlot, parseIcon);
        this.defaultItemStack.put(wikiSlot, wikiIcon);

        for (int slot : itemInputSlot) {
            this.defaultItemStack.put(slot, Icon.INPUT_BORDER_ICON);
        }
        this.defaultItemStack.put(itemOutputSlot, Icon.BORDER_ICON);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return this.contentSlot;
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        ItemStack parseItem = inventory.getItem(this.parseItemSlot);
        if (ItemStackUtil.isItemNull(parseItem)) {
            parseItem = inventory.getItem(this.itemOutputSlot);
            if (ItemStackUtil.isItemNull(parseItem) || ItemStackUtil.isItemSimilar(parseItem, this.parseFailedIcon)) {
                this.setParseFailedMenu(inventory, location);
                return;
            }
        }

        SlimefunItem slimefunItem = SlimefunItem.getByItem(parseItem);
        if (slimefunItem == null) {
            this.setParseFailedMenu(inventory, location);
            return;
        }

        List<ItemAmountWrapper> inputList = null;
        if (this.recipeTypeIdList.contains(slimefunItem.getRecipeType().getKey().getKey())) {
            inputList = ItemStackUtil.calItemListWithAmount(slimefunItem.getRecipe());
        }
        if (inputList == null || inputList.size() == 0) {
            this.setParseFailedMenu(inventory, location);
            return;
        }

        for (int slot : this.machineSlot) {
            ItemStack machineItem = inventory.getItem(slot);
            SlimefunItem sfMachineItem = SlimefunItem.getByItem(machineItem);
            if (ItemStackUtil.isItemNull(machineItem) || sfMachineItem == null) {
                continue;
            }

            List<AdvancedMachineRecipe> advancedMachineRecipeList = this.recipeMap.get(sfMachineItem.getId());
            if (advancedMachineRecipeList != null) {
                inputList = MachineUtil.calParsed(inputList, advancedMachineRecipeList, machineItem.getAmount());
            } else if (FinalTechItems.COPY_CARD.verifyItem(machineItem)) {
                ItemStack stringItem = StringItemUtil.parseItemInCard(machineItem);
                if(!ItemStackUtil.isItemNull(stringItem)) {
                    String amount = StringItemUtil.parseAmountInCard(machineItem);
                    amount = StringNumberUtil.mul(amount, String.valueOf(machineItem.getAmount()));
                    if (!StringNumberUtil.ZERO.equals(amount)) {
                        Iterator<ItemAmountWrapper> iterator = inputList.iterator();
                        while (iterator.hasNext()) {
                            ItemAmountWrapper inputItem = iterator.next();
                            if (ItemStackUtil.isItemSimilar(inputItem, stringItem)) {
                                if (StringNumberUtil.compare(amount, String.valueOf(inputItem.getAmount())) >= 0) {
                                    iterator.remove();
                                } else {
                                    inputItem.setAmount(inputItem.getAmount() - Integer.parseInt(amount));
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput = new AdvancedMachineRecipe.AdvancedRandomOutput(new ItemAmountWrapper[] {(new ItemAmountWrapper(slimefunItem.getRecipeOutput()))}, 1);
        AdvancedMachineRecipe advancedMachineRecipe = new AdvancedMachineRecipe(inputList.toArray(new ItemAmountWrapper[0]), new AdvancedMachineRecipe.AdvancedRandomOutput[] {(advancedRandomOutput)});
        this.setParseSuccessMenu(inventory, location, advancedMachineRecipe);
    }


    private void setParseFailedMenu(@Nonnull Inventory inventory, @Nonnull Location location) {
        this.locationRecipeMap.remove(location);
        for (int slot : this.itemInputSlot) {
            inventory.setItem(slot, this.parseFailedIcon);
        }
        inventory.setItem(this.itemOutputSlot, Icon.BORDER_ICON);
    }

    private void setParseSuccessMenu(@Nonnull Inventory inventory, @Nonnull Location location, @Nonnull AdvancedMachineRecipe advancedMachineRecipe) {
        this.locationRecipeMap.put(location, advancedMachineRecipe);
        int i;
        for (i = 0; i < this.itemInputSlot.length - 1; i++) {
            if (i < advancedMachineRecipe.getInput().length) {
                int amount = advancedMachineRecipe.getInput()[i].getAmount();
                ItemStack itemStack = ItemStackUtil.cloneItem(advancedMachineRecipe.getInput()[i].getItemStack());
                SfItemUtil.removeSlimefunId(itemStack);
                itemStack.setAmount(Math.min(amount, 64));
                ItemStackUtil.addLoreToLast(itemStack,
                        FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", this.getId(), "parse-amount"),
                                String.valueOf(amount)));
                inventory.setItem(this.itemInputSlot[i], itemStack);
            } else {
                inventory.setItem(this.itemInputSlot[i], this.parseSuccessIcon);
            }
        }

        if (advancedMachineRecipe.getInput().length < this.itemInputSlot.length) {
            inventory.setItem(this.itemInputSlot[i], this.parseSuccessIcon);
        } else if (advancedMachineRecipe.getInput().length == this.itemInputSlot.length) {
            int amount = advancedMachineRecipe.getInput()[i].getAmount();
            ItemStack itemStack = ItemStackUtil.cloneItem(advancedMachineRecipe.getInput()[i].getItemStack());
            SfItemUtil.removeSlimefunId(itemStack);
            itemStack.setAmount(Math.min(amount, 64));
            ItemStackUtil.addLoreToLast(itemStack,
                    FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", this.getId(), "parse-amount"),
                            String.valueOf(amount)));
            inventory.setItem(this.itemInputSlot[i], itemStack);
        } else {
            inventory.setItem(this.itemInputSlot[i], this.parseExtendIcon);
        }
        ItemStack itemStack = ItemStackUtil.cloneItem(advancedMachineRecipe.getOutput()[0].getItemStack());
        ItemStackUtil.addLoreToLast(itemStack,
                FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", this.getId(), "parse-amount"),
                        String.valueOf(advancedMachineRecipe.getOutput()[0].getAmount())));
        inventory.setItem(this.itemOutputSlot, itemStack);
    }

    public void registerRecipe() {
        for (String string : this.recipeTypeIdList) {
            RecipeType recipeType = RecipeTypeRegistry.getInstance().getRecipeTypeById(string);
            SlimefunItem slimefunItem = null;
            if (recipeType != null) {
                slimefunItem = recipeType.getMachine();
                if (slimefunItem == null) {
                    slimefunItem = SlimefunItem.getByItem(recipeType.toItem());
                    if (slimefunItem == null) {
                        slimefunItem = SlimefunItem.getById(recipeType.getKey().getKey());
                    }
                }
            }
            if (slimefunItem != null) {
                List<SlimefunItem> slimefunItemList = RecipeTypeRegistry.getInstance().getByRecipeType(recipeType);
                List<AdvancedMachineRecipe> advancedMachineRecipeList = new ArrayList<>(slimefunItemList.size());
                for (SlimefunItem sfItem : slimefunItemList) {
                    advancedMachineRecipeList.add(new AdvancedMachineRecipe(ItemStackUtil.calItemArrayWithAmount(sfItem.getRecipe()), new AdvancedMachineRecipe.AdvancedRandomOutput[] {new AdvancedMachineRecipe.AdvancedRandomOutput(new ItemAmountWrapper[] {new ItemAmountWrapper(sfItem.getRecipeOutput())}, 1)}));
                }
                this.recipeMap.put(slimefunItem.getId(), advancedMachineRecipeList);
            }
        }

        this.recipeTypeIdList.add(RecipeType.ENHANCED_CRAFTING_TABLE.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.GRIND_STONE.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.ARMOR_FORGE.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.ORE_CRUSHER.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.COMPRESSOR.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.SMELTERY.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.PRESSURE_CHAMBER.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.MAGIC_WORKBENCH.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.ORE_WASHER.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.GOLD_PAN.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.JUICER.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.ANCIENT_ALTAR.getKey().getKey());
        this.recipeTypeIdList.add(RecipeType.HEATED_PRESSURE_CHAMBER.getKey().getKey());

        MachineRecipeFactory machineRecipeFactory = MachineRecipeFactory.getInstance();

        this.recipeMap.put(FinalTechItems.MANUAL_ENHANCED_CRAFTING_TABLE.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_ENHANCED_CRAFTING_TABLE.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_GRIND_STONE.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_GRIND_STONE.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_ARMOR_FORGE.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_ARMOR_FORGE.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_ORE_CRUSHER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_ORE_CRUSHER.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_COMPRESSOR.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_COMPRESSOR.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_SMELTERY.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_SMELTERY.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_PRESSURE_CHAMBER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_PRESSURE_CHAMBER.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_MAGIC_WORKBENCH.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_MAGIC_WORKBENCH.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_ORE_WASHER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.ADVANCED_DUST_WASHER.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_COMPOSTER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_COMPOSTER.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_GOLD_PAN.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_GOLD_PAN.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_CRUCIBLE.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_CRUCIBLE.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_JUICER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_JUICER.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_ANCIENT_ALTAR.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_ANCIENT_ALTAR.getId()));
        this.recipeMap.put(FinalTechItems.MANUAL_HEATED_PRESSURE_CHAMBER.getId(), machineRecipeFactory.getAdvancedRecipe(FinalTechItems.MANUAL_HEATED_PRESSURE_CHAMBER.getId()));
    }

    @Nonnull
    public Set<String> getRecipeTypeIdList() {
        return this.recipeTypeIdList;
    }

    @Nonnull
    public Map<String, List<AdvancedMachineRecipe>> getRecipeMap() {
        return this.recipeMap;
    }
}
