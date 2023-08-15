package io.taraxacum.libs.slimefun.dto;

import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.multiblocks.MultiBlockMachine;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.libs.plugin.file.ConfigFileManager;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Final_ROOT
 */
public class ItemValueTableV2 {
    private boolean init = false;

    // key: slimefun item id, value: output value
    private final Map<String, Value> itemInputValueMap = new HashMap<>(Slimefun.getRegistry().getEnabledSlimefunItems().size());
    // key: slimefun item id, value: input value
    private final Map<String, Value> itemOutputValueMap = new HashMap<>(Slimefun.getRegistry().getEnabledSlimefunItems().size());
    // string: slimefun item id
    private final List<String> availableOutputList = new ArrayList<>();

    private final String baseRealInputValue = FinalTech.getValueManager().getOrDefault("1","itemValueTable", "base", "real", "input-value");
    private final String baseImaginaryInputValue = FinalTech.getValueManager().getOrDefault("1","itemValueTable", "base", "imaginary", "input-value");
    private final String baseRealOutputValue = FinalTech.getValueManager().getOrDefault("64","itemValueTable", "base", "real", "output-value");
    private final String baseImaginaryOutputValue = FinalTech.getValueManager().getOrDefault("1","itemValueTable", "base", "imaginary", "output-value");

    private final Value emptyInputValue = new Value(StringNumberUtil.ZERO);
    private final Value infinityOutputValue = new Value(StringNumberUtil.VALUE_INFINITY, StringNumberUtil.VALUE_INFINITY);

    private final Value baseInputValue = new Value(this.baseRealInputValue, StringNumberUtil.ZERO);
    private final Value baseSimpleOutputValue = new Value(this.baseRealOutputValue, StringNumberUtil.ZERO);
    private final Value baseSpecialOutputValue = new Value(StringNumberUtil.ZERO, this.baseImaginaryOutputValue);

    private Set<String> notAllowedRecipeType = new HashSet<>();

    private static volatile ItemValueTableV2 instance;

    private ItemValueTableV2() {

    }

    public void init(@Nonnull ConfigFileManager valueFile) {
        if (this.init) {
            return;
        }

        this.init = true;

        this.notAllowedRecipeType.addAll(valueFile.getStringList("not-allowed-recipe-type"));

        this.itemInputValueMap.put(SlimefunItems.IRON_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.GOLD_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.COPPER_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.TIN_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.LEAD_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.SILVER_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.ALUMINUM_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.ZINC_DUST.getItemId(), this.emptyInputValue);
        this.itemInputValueMap.put(SlimefunItems.MAGNESIUM_DUST.getItemId(), this.emptyInputValue);

        this.itemOutputValueMap.put(SlimefunItems.IRON_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.GOLD_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.COPPER_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.TIN_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.LEAD_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.SILVER_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.ALUMINUM_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.ZINC_DUST.getItemId(), this.baseSimpleOutputValue);
        this.itemOutputValueMap.put(SlimefunItems.MAGNESIUM_DUST.getItemId(), this.baseSimpleOutputValue);

        if (valueFile.containPath("input", "base")) {
            for (String key : valueFile.getStringList("input", "base")) {
                String realNumber = valueFile.getOrDefault("0", "input", "base", key, "real");
                String imaginaryNumber = valueFile.getString("0", "input", "base", key, "imaginary");
                this.itemInputValueMap.put(key, new Value(realNumber, imaginaryNumber));
            }
        }

        if (valueFile.containPath("output", "base")) {
            for (String key : valueFile.getStringList("output", "base")) {
                String realNumber = valueFile.getOrDefault(StringNumberUtil.VALUE_INFINITY, "output", "base", key, "real");
                String imaginaryNumber = valueFile.getString(StringNumberUtil.VALUE_INFINITY, "output", "base", key, "imaginary");
                this.itemOutputValueMap.put(key, new Value(realNumber, imaginaryNumber));
            }
        }

        this.itemInputValueMap.put(FinalTechItems.COPY_CARD.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemInputValueMap.put(FinalTechItems.SINGULARITY.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemInputValueMap.put(FinalTechItems.SPIROCHETE.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemInputValueMap.put(FinalTechItems.ITEM_PHONY.getId(), new Value(StringNumberUtil.ZERO, this.baseImaginaryInputValue));
        this.itemInputValueMap.put(FinalTechItems.BUG.getId(), this.emptyInputValue);

        this.itemOutputValueMap.put(FinalTechItems.COPY_CARD.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemOutputValueMap.put(FinalTechItems.SINGULARITY.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemOutputValueMap.put(FinalTechItems.SPIROCHETE.getId(), new Value(this.baseRealInputValue, StringNumberUtil.ZERO));
        this.itemOutputValueMap.put(FinalTechItems.ITEM_PHONY.getId(), new Value(StringNumberUtil.ZERO, this.baseImaginaryInputValue));
        this.itemOutputValueMap.put(FinalTechItems.BUG.getId(), this.emptyInputValue);

        this.availableOutputList.add(FinalTechItems.BUG.getId());

        List<SlimefunItem> allSlimefunItems = Slimefun.getRegistry().getEnabledSlimefunItems();
        for (SlimefunItem slimefunItem : allSlimefunItems) {
            if (slimefunItem.getState() == ItemState.ENABLED) {
                try {
                    this.getOrCalItemInputValue(slimefunItem);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.itemInputValueMap.put(slimefunItem.getId(), this.emptyInputValue);
                }
                try {
                    this.getOrCalItemOutputValue(slimefunItem);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.itemOutputValueMap.put(slimefunItem.getId(), this.infinityOutputValue);
                }
            }
        }

        this.itemInputValueMap.put(SlimefunItems.IRON_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.GOLD_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.COPPER_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.TIN_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.LEAD_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.SILVER_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.ALUMINUM_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.ZINC_DUST.getItemId(), this.baseInputValue);
        this.itemInputValueMap.put(SlimefunItems.MAGNESIUM_DUST.getItemId(), this.baseInputValue);

        this.itemOutputValueMap.put(SlimefunItems.IRON_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.GOLD_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.COPPER_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.TIN_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.LEAD_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.SILVER_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.ALUMINUM_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.ZINC_DUST.getItemId(), this.baseInputValue);
        this.itemOutputValueMap.put(SlimefunItems.MAGNESIUM_DUST.getItemId(), this.baseInputValue);

        if (valueFile.containPath("input", "result")) {
            for (String key : valueFile.getStringList("input", "result")) {
                String realNumber = valueFile.getOrDefault("0", "input", "result", key, "real");
                String imaginaryNumber = valueFile.getString("0", "input", "result", key, "imaginary");
                this.itemInputValueMap.put(key, new Value(realNumber, imaginaryNumber));
            }
        }

        if (valueFile.containPath("output", "result")) {
            for (String key : valueFile.getStringList("output", "result")) {
                String realNumber = valueFile.getOrDefault(StringNumberUtil.VALUE_INFINITY, "output", "result", key, "real");
                String imaginaryNumber = valueFile.getString(StringNumberUtil.VALUE_INFINITY, "output", "result", key, "imaginary");
                this.itemOutputValueMap.put(key, new Value(realNumber, imaginaryNumber));
                if (!StringNumberUtil.VALUE_INFINITY.equals(realNumber) && !StringNumberUtil.VALUE_INFINITY.equals(imaginaryNumber)) {
                    SlimefunItem slimefunItem = SlimefunItem.getById(key);
                    if (slimefunItem != null
                            && slimefunItem.getState() == ItemState.ENABLED
                            && !this.notAllowedRecipeType.contains(slimefunItem.getRecipeType().getKey().getKey())) {
                        this.availableOutputList.add(key);
                    }
                }
            }
        }

        if (valueFile.containPath("output", "remove")) {
            for (String id : valueFile.getStringList("output", "remove")) {
                this.availableOutputList.remove(id);
            }
        }
    }

    @Nonnull
    public Value getOrCalItemInputValue(@Nullable ItemStack itemStack) {
        if (ItemStackUtil.isItemNull(itemStack)) {
            return this.emptyInputValue;
        }
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem == null) {
            Value value = this.itemInputValueMap.getOrDefault(itemStack.getType().name(), this.baseInputValue);
            return itemStack.getAmount() == 1 ? value : this.mulValue(value, itemStack.getAmount());
        }
        return this.mulValue(this.getOrCalItemInputValue(slimefunItem), itemStack.getAmount());
    }

    @Nonnull
    public Value getOrCalItemInputValue(@Nonnull SlimefunItem slimefunItem) {
        String id = slimefunItem.getId();
        if (this.itemInputValueMap.containsKey(id)) {
            return this.itemInputValueMap.get(id);
        } else if (slimefunItem.getState() != ItemState.ENABLED) {
            this.itemInputValueMap.put(id, this.emptyInputValue);
            return this.emptyInputValue;
        }
        this.itemInputValueMap.put(id, this.emptyInputValue);
        Value value = this.emptyInputValue;

        List<ItemAmountWrapper> recipeList = ItemStackUtil.calItemListWithAmount(slimefunItem.getRecipe());
        for (ItemAmountWrapper recipeItem : recipeList) {
            ItemStack itemStack = recipeItem.getItemStack();
            itemStack.setAmount(1);
            value = this.addValue(value, this.getOrCalItemInputValue(itemStack));
        }

        value = this.addRealValue(value, String.valueOf(recipeList.size()));
        value = this.addRealValue(value, this.baseRealInputValue);
        this.itemInputValueMap.put(id, value);
        return value;
    }

    @Nonnull
    public Value getOrCalItemOutputValue(@Nullable ItemStack itemStack) {
        if (ItemStackUtil.isItemNull(itemStack)) {
            return this.infinityOutputValue;
        }
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem == null) {
            Value value;
            if (ItemStackUtil.isItemSimilar(itemStack, new ItemStack(itemStack.getType()))) {
                value = this.itemOutputValueMap.getOrDefault(itemStack.getType().name(), this.baseSimpleOutputValue);
            } else {
                value = this.baseSpecialOutputValue;
            }
            return itemStack.getAmount() == 1 ? value : this.mulValue(value, itemStack.getAmount());
        }
        return this.mulValue(this.getOrCalItemOutputValue(slimefunItem), itemStack.getAmount());
    }

    @Nonnull
    public Value getOrCalItemOutputValue(@Nonnull SlimefunItem slimefunItem) {
        String id = slimefunItem.getId();
        if (this.itemOutputValueMap.containsKey(id)) {
            return this.itemOutputValueMap.get(id);
        } else if (slimefunItem.getState() != ItemState.ENABLED || this.notAllowedRecipeType.contains(slimefunItem.getRecipeType().getKey().getKey())) {
            this.itemOutputValueMap.put(id, this.infinityOutputValue);
            return this.infinityOutputValue;
        }
        this.itemOutputValueMap.put(id, this.infinityOutputValue);
        Value value = this.baseSimpleOutputValue;

        List<ItemAmountWrapper> recipeList = ItemStackUtil.calItemListWithAmount(slimefunItem.getRecipe());
        for (ItemAmountWrapper recipeItem : recipeList) {
            int amount = recipeItem.getAmount();
            SlimefunItem recipeSlimefunItem = SlimefunItem.getByItem(recipeItem.getItemStack());
            if (recipeSlimefunItem != null) {
                amount /= recipeSlimefunItem.getRecipeOutput().getAmount();
                if (amount == 0) {
                    amount = 1;
                }
            }
            ItemStack itemStack = recipeItem.getItemStack();
            itemStack.setAmount(1);
            value = this.addValue(value, this.mulValue(this.getOrCalItemOutputValue(itemStack), amount));
        }
        value = this.mulValue(value, recipeList.size());

        RecipeType recipeType = slimefunItem.getRecipeType();
        if (RecipeType.NULL.equals(recipeType) || recipeList.isEmpty()) {
            value = this.addValue(value, this.baseSpecialOutputValue);
        } else if (slimefunItem instanceof MultiBlockMachine || RecipeType.MULTIBLOCK.equals(recipeType) || recipeType.getMachine() instanceof MultiBlockMachine) {
            value = this.addValue(value, this.baseSimpleOutputValue);
        } else if (slimefunItem.equals(recipeType.getMachine())) {
            value = this.addValue(value, value);
        } else if (recipeType.getMachine() != null) {
            value = this.addValue(value, this.getOrCalItemOutputValue(recipeType.getMachine()));
        } else {
            value = this.addValue(value, this.baseSimpleOutputValue);
        }

        if (!StringNumberUtil.VALUE_INFINITY.equals(value.realNumber)
                && !StringNumberUtil.VALUE_INFINITY.equals(value.imaginaryNumber)
                && !(slimefunItem instanceof MultiBlockMachine)
                && !RecipeType.MULTIBLOCK.equals(recipeType)) {
            this.availableOutputList.add(id);
        }
        this.itemOutputValueMap.put(id, value);
        return value;
    }

    @Nonnull
    public List<String> getAvailableOutputList() {
        return this.availableOutputList;
    }

    @Nonnull
    public Value addRealValue(@Nonnull Value value, @Nonnull String realNumber) {
        return new Value(StringNumberUtil.add(value.realNumber, realNumber), value.imaginaryNumber);
    }

    @Nonnull
    public Value addImaginaryValue(@Nonnull Value value, @Nonnull String imaginaryNumber) {
        return new Value(value.realNumber, StringNumberUtil.add(value.imaginaryNumber, imaginaryNumber));
    }

    @Nonnull
    public Value addValue(@Nonnull Value value1, @Nonnull Value value2) {
        return new Value(StringNumberUtil.add(value1.realNumber, value2.realNumber), StringNumberUtil.add(value1.imaginaryNumber, value2.imaginaryNumber));
    }

    @Nonnull
    public Value subValue(@Nonnull Value value1, @Nonnull Value value2) {
        return new Value(StringNumberUtil.sub(value1.realNumber, value2.realNumber), StringNumberUtil.sub(value1.imaginaryNumber, value2.imaginaryNumber));
    }

    @Nonnull
    public Value mulValue(@Nonnull Value value, int mul) {
        return new Value(StringNumberUtil.mul(value.realNumber, String.valueOf(mul)), StringNumberUtil.mul(value.imaginaryNumber, String.valueOf(mul)));
    }

    public boolean biggerOrEqual(@Nonnull Value value1, @Nonnull Value value2) {
        return StringNumberUtil.compare(value1.realNumber, value2.realNumber) >= 0 && StringNumberUtil.compare(value1.imaginaryNumber, value2.imaginaryNumber) >= 0;
    }

    public boolean isEmptyValue(@Nonnull Value value) {
        return value.realNumber.equals(StringNumberUtil.ZERO) && value.imaginaryNumber.equals(StringNumberUtil.ZERO);
    }

    @Nonnull
    public static ItemValueTableV2 getInstance() {
        if (instance == null) {
            synchronized (ItemValueTableV2.class) {
                if (instance == null) {
                    instance = new ItemValueTableV2();
                }
            }
        }
        return instance;
    }

    public static class Value {
        String realNumber;

        String imaginaryNumber;

        public Value(@Nonnull String realNumber, @Nonnull String imaginaryNumber) {
            this.realNumber = realNumber;
            this.imaginaryNumber = imaginaryNumber;
        }

        public Value(@Nonnull String realNumber) {
            this.realNumber = realNumber;
            this.imaginaryNumber = StringNumberUtil.ZERO;
        }

        @Nonnull
        public String getRealNumber() {
            return realNumber;
        }

        @Nonnull
        public String getImaginaryNumber() {
            return imaginaryNumber;
        }

        @Override
        public int hashCode() {
            return this.realNumber.hashCode() / 2 + this.imaginaryNumber.hashCode() / 2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Value value) {
                return StringNumberUtil.compare(this.realNumber, value.realNumber) == 0 && StringNumberUtil.compare(this.imaginaryNumber, value.imaginaryNumber) == 0;
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringNumberUtil.ZERO.equals(this.realNumber) && StringNumberUtil.ZERO.equals(this.imaginaryNumber)) {
                return StringNumberUtil.ZERO;
            }

            if (StringNumberUtil.ZERO.equals(this.realNumber)) {
                return stringBuilder.append(this.imaginaryNumber).append("i").toString();
            }

            if (StringNumberUtil.ZERO.equals(this.imaginaryNumber)) {
                return stringBuilder.append(this.realNumber).toString();
            }

            stringBuilder.append(this.realNumber);
            stringBuilder.append(" + ");
            return stringBuilder.append(this.imaginaryNumber).append("i").toString();
        }
    }
}
