package io.taraxacum.finaltech.core.option;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.LocationDataLoreOption;
import io.taraxacum.libs.slimefun.dto.LocationDataOption;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public final class MachineRecipeLock {
    public static final String KEY = "rl";

    public static final String VALUE_UNLOCK = "-1";
    public static final String VALUE_LOCK_OFF = "-2";

    public static final ItemStack ICON = new CustomItemStack(Material.TRIPWIRE_HOOK, FinalTech.getLanguageString("option", "MACHINE_RECIPE_LOCK", "icon", "name"), FinalTech.getLanguageStringArray("option", "MACHINE_RECIPE_LOCK", "icon", "lore"));

    public static final LocationDataLoreOption OPTION = new LocationDataLoreOption(LocationDataOption.CARGO_ID, KEY, new LinkedHashMap<>(2) {{
        this.put("-2", FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "unlock", "lore"));
        this.put("-1", FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "lock-off", "lore"));
    }}) {
        @Nonnull
        @Override
        public String nextOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Nonnull
        @Override
        public String previousOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Override
        public void updateLore(@Nonnull ItemStack itemStack, @Nullable String value, @Nonnull SlimefunItem slimefunItem) {
            if (this.validValue(value)) {
                super.updateLore(itemStack, value);
            } else {
                int recipeLock = value == null ? Integer.parseInt(this.defaultValue()) :  Integer.parseInt(value);
                List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(slimefunItem.getId());
                if (recipeLock >= 0) {
                    recipeLock = recipeLock % advancedMachineRecipeList.size();
                    AdvancedMachineRecipe advancedMachineRecipe = advancedMachineRecipeList.get(recipeLock);
                    List<String> loreList;
                    if (advancedMachineRecipe.getOutputs().length == 1) {
                        loreList = new ArrayList<>(advancedMachineRecipe.getInput().length + advancedMachineRecipe.getOutputs()[0].getOutputItem().length + 3);
                        loreList.addAll(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "input", "title"));
                        for (ItemAmountWrapper inputItem : advancedMachineRecipe.getInput()) {
                            loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "input", "items"),
                                    ItemStackUtil.getItemName(inputItem.getItemStack()),
                                    String.valueOf(inputItem.getAmount())));
                        }
                        loreList.addAll(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "output", "title"));
                        for (ItemAmountWrapper outputItem : advancedMachineRecipe.getOutputs()[0].getOutputItem()) {
                            loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "output", "items"),
                                    ItemStackUtil.getItemName(outputItem.getItemStack()),
                                    String.valueOf(outputItem.getAmount())));
                        }
                    } else {
                        int outputLength = 0;
                        for (AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputs()) {
                            outputLength += advancedRandomOutput.getOutputItem().length + 1;
                        }
                        loreList = new ArrayList<>(advancedMachineRecipe.getInput().length + outputLength + 3);
                        loreList.addAll(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "input", "title"));
                        for (ItemAmountWrapper inputItem : advancedMachineRecipe.getInput()) {
                            loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "input", "items"),
                                    ItemStackUtil.getItemName(inputItem.getItemStack()),
                                    String.valueOf(inputItem.getAmount())));
                        }
                        loreList.addAll(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "output", "title"));
                        for (AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputs()) {
                            String random = String.valueOf(((double) advancedRandomOutput.getWeight()) / advancedMachineRecipe.getWeightSum() * 100.0);
                            if (random.contains(".")) {
                                random = random.substring(0, Math.min(random.indexOf(".") + 3, random.length()));
                            }
                            loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "output-random", "title"),
                                    random));
                            for (ItemAmountWrapper outputItem : advancedRandomOutput.getOutputItem()) {
                                loreList.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "MACHINE_RECIPE_LOCK", "input", "items"),
                                        ItemStackUtil.getItemName(outputItem.getItemStack()),
                                        String.valueOf(outputItem.getAmount())));
                            }
                        }
                    }
                    ItemStackUtil.setLore(itemStack, loreList);
                }
            }
        }

        @Nonnull
        @Override
        public Consumer<InventoryClickEvent> getHandler(@Nonnull LocationDataService locationDataService, @Nonnull Location location, @Nonnull SlimefunItem slimefunItem) {
            return inventoryClickEvent -> {
                inventoryClickEvent.setCancelled(true);
                Inventory inventory = inventoryClickEvent.getClickedInventory();
                if(inventory != null) {
                    ItemStack itemStack = inventory.getItem(inventoryClickEvent.getSlot());
                    if (!ItemStackUtil.isItemNull(itemStack)) {
                        this.checkOrSetDefault(locationDataService, location);
                        String value = inventoryClickEvent.getClick().isRightClick() ? VALUE_LOCK_OFF : VALUE_UNLOCK;
                        this.updateLore(itemStack, value);
                        this.setOrClearValue(locationDataService, location, value);
                    }
                }
            };
        }
    };
}
