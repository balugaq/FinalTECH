package io.taraxacum.finaltech.util;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.ReflectionUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.common.pref.ComplexOptional;
import io.taraxacum.finaltech.core.exception.ParseErrorException;
import io.taraxacum.finaltech.core.group.MainItemGroup;
import io.taraxacum.finaltech.core.group.SubFlexItemGroup;
import io.taraxacum.finaltech.setup.FinalTechRecipeTypes;
import io.taraxacum.finaltech.setup.FinalTechRecipes;
import io.taraxacum.libs.plugin.file.ConfigFileManager;
import io.taraxacum.libs.plugin.file.LanguageManager;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class ConfigUtil {
    @Nonnull
    public static String getStatusMenuName(@Nonnull LanguageManager languageManager, @Nonnull SlimefunItem slimefunItem, String... strings) {
        return languageManager.replaceString(languageManager.getString("items", slimefunItem.getId(), "status", "name"), strings);
    }
    @Nonnull
    public static String getStatusMenuName(@Nonnull LanguageManager languageManager, @Nonnull String id, String... strings) {
        return languageManager.replaceString(languageManager.getString("items", id, "status", "name"), strings);
    }

    @Nonnull
    public static String[] getStatusMenuLore(@Nonnull LanguageManager languageManager, @Nonnull SlimefunItem slimefunItem, String... strings) {
        return languageManager.replaceStringArray(languageManager.getStringArray("items", slimefunItem.getId(), "status", "lore"), strings);
    }
    @Nonnull
    public static String[] getStatusMenuLore(@Nonnull LanguageManager languageManager, @Nonnull String id, String... strings) {
        return languageManager.replaceStringArray(languageManager.getStringArray("items", id, "status", "lore"), strings);
    }

    @Nonnull
    public static SlimefunItemStack getSlimefunItemStack(@Nonnull LanguageManager languageManager, @Nonnull String id, @Nonnull Material defaultMaterial, @Nonnull String defaultName) {
        Material material = defaultMaterial;
        if (languageManager.containPath("categories", id, "material")) {
            material = Material.getMaterial(languageManager.getString("categories", id, "material"));
            material = material == null ? defaultMaterial : material;
        }
        String name = languageManager.containPath("items", id, "name") ? languageManager.getString("items", id, "name") : defaultName;
        return new SlimefunItemStack(id, material, name, languageManager.getStringArray("items", id, "lore"));
    }

    @Nonnull
    public static SlimefunItemStack getSlimefunItemStack(@Nonnull LanguageManager languageManager, @Nonnull String id, @Nonnull String texture, @Nonnull String defaultName) {
        String name = languageManager.containPath("items", id, "name") ? languageManager.getString("items", id, "name") : defaultName;
        try {
            return new SlimefunItemStack(id, texture, name, languageManager.getStringArray("items", id, "lore"));
        } catch (Exception e) {
            e.printStackTrace();
            return new SlimefunItemStack(id, Material.STONE, name, languageManager.getStringArray("items", id, "lore"));
        }
    }

    public static MainItemGroup getMainItemGroup(@Nonnull LanguageManager languageManager, @Nonnull String key, @Nonnull Material defaultMaterial, @Nonnull String defaultName) {
        Material material = defaultMaterial;
        if (languageManager.containPath("categories", key, "material")) {
            material = Material.getMaterial(languageManager.getString("categories", key, "material"));
            material = material == null ? defaultMaterial : material;
        }
        String name = languageManager.containPath("categories", key, "name") ? languageManager.getString("categories", key, "name") : defaultName;
        return new MainItemGroup(new NamespacedKey(languageManager.getPlugin(), key), ItemStackUtil.newItemStack(material, name), 0);
    }

    public static SubFlexItemGroup getSubFlexItemGroup(@Nonnull LanguageManager languageManager, @Nonnull String key, @Nonnull Material defaultMaterial, @Nonnull String defaultName) {
        Material material = defaultMaterial;
        if (languageManager.containPath("categories", key, "material")) {
            material = Material.getMaterial(languageManager.getString("categories", key, "material"));
            material = material == null ? defaultMaterial : material;
        }
        String name = languageManager.containPath("categories", key, "name") ? languageManager.getString("categories", key, "name") : defaultName;
        return new SubFlexItemGroup(new NamespacedKey(languageManager.getPlugin(), key), ItemStackUtil.newItemStack(material, name), FinalTech.getSlimefunGuideHistoryService());
    }

    @Nonnull
    public static <T> T getOrDefaultItemSetting(@Nonnull T defaultValue, @Nonnull SlimefunItem slimefunItem, @Nonnull String... path) {
        return FinalTech.getItemManager().getOrDefault(defaultValue, JavaUtil.addToFirst(slimefunItem.getId(), path));
    }
    @Nonnull
    public static <T> T getOrDefaultItemSetting(@Nonnull T defaultValue, @Nonnull String id, @Nonnull String... path) {
        return FinalTech.getItemManager().getOrDefault(defaultValue, JavaUtil.addToFirst(id, path));
    }

    @Nonnull
    public static List<String> getItemStringList(@Nonnull SlimefunItem slimefunItem, @Nonnull String... path) {
        return FinalTech.getItemManager().getStringList(JavaUtil.addToFirst(slimefunItem.getId(), path));
    }

    @Nonnull
    public static List<String> getItemStringList(@Nonnull String id, @Nonnull String... path) {
        return FinalTech.getItemManager().getStringList(JavaUtil.addToFirst(id, path));
    }

    @Nonnull
    public static <T> T getOrDefaultConfigSetting(@Nonnull T defaultValue, @Nonnull String... path) {
        return FinalTech.getConfigManager().getOrDefault(defaultValue, path);
    }

    @Nonnull
    public static <T> T getOrDefaultValueSetting(@Nonnull T defaultValue, @Nonnull String... path) {
        return FinalTech.getValueManager().getOrDefault(defaultValue, path);
    }

    @Nonnull
    public static ItemStack[] getRecipe(@Nonnull String id) {
        try {
            ConfigFileManager recipeManager = FinalTech.getRecipeManager();
            if (!recipeManager.containPath(id)) {
                String prefix = "FINALTECH_";
                return ReflectionUtil.getStaticValue(FinalTechRecipes.class, id.substring(prefix.length()));
            }

            List<String> stringList = recipeManager.getStringList(id);
            stringList.sort(Comparator.naturalOrder());

            ItemStack[] itemStacks = new ItemStack[stringList.size()];
            for (int i = 0; i < itemStacks.length; i++) {
                String index = stringList.get(i);

                String itemId = recipeManager.getOrDefault("error", id, index, "id");

                if ("error".equals(itemId)) {
                    FinalTech.logger().severe("Can not generate recipe for " + id);
                    FinalTech.logger().severe("You need set a valid value for " + id + "." + index + ".id");
                    return new ItemStack[0];
                }

                int amount = recipeManager.getOrDefault(1, id, index, "amount");

                try {
                    if (amount > 64 || amount <= 0) {
                        throw new ParseErrorException("The amount should be in [0, 64]");
                    }
                } catch (Exception e) {
                    FinalTech.logger().severe("Can not generate recipe for " + id);
                    FinalTech.logger().severe("You need set a valid value for " + id + "." + index + ".amount");
                    e.printStackTrace();
                    return new ItemStack[0];
                }

                ComplexOptional<ItemStack> optionItemStack = MachineUtil.getItemStackById(itemId);

                itemStacks[i] = optionItemStack.ok() ? optionItemStack.getOrByArgs(() -> recipeManager.getStringList(id, index, "extra-args").toArray(new String[0])) : null;
            }

            return itemStacks;
        } catch (Exception e) {
            FinalTech.logger().severe("An error occurred while generating recipe for " + id);
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    @Nonnull
    public static RecipeType getRecipeType(@Nonnull String id) {
        try {
            ItemStack[] itemStacks;
            ConfigFileManager recipeManager = FinalTech.getRecipeManager();
            if (!recipeManager.containPath(id)) {
                String prefix = "FINALTECH_";
                itemStacks = ReflectionUtil.getStaticValue(FinalTechRecipes.class, id.substring(prefix.length()));
            } else {
                List<String> stringList = recipeManager.getStringList(id);
                stringList.sort(Comparator.naturalOrder());

                itemStacks = new ItemStack[stringList.size()];
            }

            if (itemStacks == null) {
                return RecipeType.NULL;
            } else if (itemStacks.length <= 9) {
                return FinalTechRecipeTypes.BEDROCK_CRAFT_TABLE;
            } else if (itemStacks.length <= 36) {
                return FinalTechRecipeTypes.MATRIX_CRAFTING_TABLE;
            } else {
                return RecipeType.NULL;
            }
        } catch (Exception e) {
            FinalTech.logger().severe("An error occurred while generating recipe for " + id);
            e.printStackTrace();
            return RecipeType.NULL;
        }
    }
}
