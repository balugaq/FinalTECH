package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.interfaces.LogicInventory;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.dto.ItemValueTableV2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 */
public class EquivalentExchangeTableInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {30, 31, 32, 39, 41, 48, 49, 50};
    private final int[] inputBorder = new int[] {2, 11, 20, 29, 38, 47};
    private final int[] outputBorder = new int[] {6, 15, 24, 33, 42, 51};
    private final int[] inputSlot = new int[] {0, 1, 9, 10, 18, 19, 27, 28, 36, 37, 45, 46};
    private final int[] outputSlot = new int[] {7, 8, 16, 17, 25, 26, 34, 35, 43, 44, 52, 53};

    private final int[] parseBorder = new int[] {3, 4, 5, 12, 14, 21, 23};
    public final int parseItemSlot = 13;
    private final int parseStatusSlot = 22;

    public final int statusSlot = 40;

    private final ItemStack parseBorderIcon = ItemStackUtil.newItemStack(Material.PURPLE_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-border-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-border-icon", "lore"));
    private final ItemStack parseStatusIcon = ItemStackUtil.newItemStack(Material.YELLOW_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-result-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-result-icon", "lore"));
    private final ItemStack craftIcon = ItemStackUtil.newItemStack(Material.GREEN_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "status-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "status-icon", "lore"));

    private final String keyRealValue;
    private final String keyImaginaryValue;

    public EquivalentExchangeTableInventory(@Nonnull AbstractMachine machine, @Nonnull String keyRealValue, @Nonnull String keyImaginaryValue) {
        super(machine);

        this.keyRealValue = keyRealValue;
        this.keyImaginaryValue = keyImaginaryValue;
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return border;
    }

    @Nonnull
    @Override
    protected int[] getInputBorder() {
        return inputBorder;
    }

    @Nonnull
    @Override
    protected int[] getOutputBorder() {
        return outputBorder;
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        for (int slot : parseBorder) {
            this.defaultItemStack.put(slot, this.parseBorderIcon);
        }
        this.defaultItemStack.put(this.parseStatusSlot, this.parseStatusIcon);
        this.defaultItemStack.put(this.statusSlot, this.craftIcon);
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return switch (requestType) {
            case INPUT -> this.inputSlot;
            case OUTPUT -> this.outputSlot;
            default -> new int[0];
        };
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        ItemStack itemStack = inventory.getItem(this.parseItemSlot);
        List<String> lore = new ArrayList<>();
        if (!ItemStackUtil.isItemNull(itemStack)) {
            lore.add("§f" + ItemStackUtil.getItemName(itemStack));
        }

        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem == null) {
            lore.addAll(FinalTech.getLanguageStringList("items", this.getId(), "no-value", "lore"));
        } else {
            lore.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", this.getId(), "input-value", "lore"),
                    ItemValueTableV2.getInstance().getOrCalItemInputValue(itemStack).toString()));
            lore.addAll(FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", this.getId(), "output-value", "lore"),
                    ItemValueTableV2.getInstance().getOrCalItemOutputValue(itemStack).toString()));
        }

        ItemStack iconItem = inventory.getItem(this.parseStatusSlot);
        if (ItemStackUtil.isItemNull(iconItem)) {
            return;
        }
        ItemStackUtil.setLore(iconItem, lore);

        iconItem = inventory.getItem(this.statusSlot);
        if (ItemStackUtil.isItemNull(iconItem)) {
            return;
        }
        String realValue = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(location, this.keyRealValue), StringNumberUtil.ZERO);
        String imaginaryValue = JavaUtil.getFirstNotNull(FinalTech.getLocationDataService().getLocationData(location, this.keyImaginaryValue), StringNumberUtil.ZERO);
        String storedValue = new ItemValueTableV2.Value(realValue, imaginaryValue).toString();
        ItemStackUtil.setLore(iconItem,
                FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", this.getId(), "stored-value", "lore"),
                        storedValue));
    }
}
