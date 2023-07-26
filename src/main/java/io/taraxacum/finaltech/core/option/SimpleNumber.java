package io.taraxacum.finaltech.core.option;

import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.util.ConstantTableUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.LocationDataLoreOption;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;

/**
 * @author Final_ROOT
 */
public class SimpleNumber {
    public static final String KEY = "sn";

    public static final ItemStack SIMPLE_NUMBER_ICON = ItemStackUtil.newItemStack(Material.TARGET,
            FinalTech.getLanguageString("option", "SIMPLE_NUMBER", "icon", "name"),
            FinalTech.getLanguageStringArray("option", "SIMPLE_NUMBER", "icon", "lore"));
    public static final ItemStack SIMPLE_NUMBER_ADD_ICON = ItemStackUtil.newItemStack(Material.GREEN_CONCRETE,
            FinalTech.getLanguageString("option", "SIMPLE_NUMBER", "add-icon", "name"),
            FinalTech.getLanguageStringArray("option", "SIMPLE_NUMBER", "add-icon", "lore"));
    public static final ItemStack SIMPLE_NUMBER_SUB_ICON = ItemStackUtil.newItemStack(Material.RED_CONCRETE,
            FinalTech.getLanguageString("option", "SIMPLE_NUMBER", "sub-icon", "name"),
            FinalTech.getLanguageStringArray("option", "SIMPLE_NUMBER", "sub-icon", "lore"));

    public static final LocationDataLoreOption OPTION_256 = new LocationDataLoreOption("SIMPLE_NUMBER", KEY , 0, new LinkedHashMap<>() {{
        for (int i = 0; i <= 256; i++) {
            this.put(String.valueOf(i), FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("option", "SIMPLE_NUMBER", "icon", "lore"), String.valueOf(i)));
        }
    }}) {
        @Nonnull
        @Override
        public String defaultValue() {
            return String.valueOf(ConstantTableUtil.ITEM_MAX_STACK);
        }

        @Nonnull
        @Override
        public String clickNextValue(@Nullable String value, @Nonnull ClickType clickType) {
            if (!clickType.isShiftClick() && !clickType.isRightClick()) {
                return this.nextOrDefaultValue(value);
            } else if (clickType.isShiftClick() && !clickType.isRightClick()) {
                return this.offsetOrDefaultValue(value, ConstantTableUtil.ITEM_MAX_STACK);
            } else {
                return this.offsetOrDefaultValue(value, 8);
            }
        }

        @Nonnull
        @Override
        public String clickPreviousValue(@Nullable String value, @Nonnull ClickType clickType) {
            if (!clickType.isShiftClick() && !clickType.isRightClick()) {
                return this.previousOrDefaultValue(value);
            } else if (clickType.isShiftClick() && !clickType.isRightClick()) {
                return this.offsetOrDefaultValue(value, -ConstantTableUtil.ITEM_MAX_STACK);
            } else {
                return this.offsetOrDefaultValue(value, -8);
            }
        }
    };
}
