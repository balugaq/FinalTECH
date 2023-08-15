package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.enums.LogSourceType;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.BasicCraft;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import io.taraxacum.libs.slimefun.interfaces.SimpleValidItem;
import io.taraxacum.libs.slimefun.interfaces.ValidItem;
import io.taraxacum.libs.slimefun.util.SfItemUtil;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class MatrixCraftingTableInventory extends AbstractManualMachineInventory implements LogicInventory {
    private final int[] border = new int[] {6, 7, 8, 15, 17, 24, 25, 26};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[] {33, 34, 35, 42, 44, 51, 52, 53};
    private final int[] inputSlot = new int[] {0, 1, 2, 3, 4, 5, 9, 10, 11, 12, 13, 14, 18, 19, 20, 21, 22, 23, 27, 28, 29, 30, 31, 32, 36, 37, 38, 39, 40, 41, 45, 46, 47, 48, 49, 50};
    private final int[] outputSlot = new int[] {43};

    private final int parseSlot = 16;
    private final ItemStack parseIcon = ItemStackUtil.newItemStack(Material.YELLOW_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", this.getId(), "parse-icon", "name"),
            FinalTech.getLanguageStringArray("items", this.getId(), "parse-icon", "lore"));

    public MatrixCraftingTableInventory(@Nonnull AbstractMachine machine) {
        super(machine);
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

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        return slot == this.parseSlot ? inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));

            ItemStack[] itemStacks;
            SlimefunItem slimefunItem;
            List<MachineRecipe> machineRecipeList = MachineRecipeFactory.getInstance().getRecipe(this.getId());
            List<SlimefunItem> slimefunItemList = new ArrayList<>(machineRecipeList.size());
            for (MachineRecipe machineRecipe : machineRecipeList) {
                itemStacks = machineRecipe.getOutput();
                if (itemStacks.length == 1) {
                    slimefunItem = SlimefunItem.getByItem(itemStacks[0]);
                    if (slimefunItem != null) {
                        slimefunItemList.add(slimefunItem);
                    }
                }
            }

            Inventory inventory = inventoryClickEvent.getInventory();
            BasicCraft basicCraft = BasicCraft.doCraftBySlimefunItem(slimefunItemList, inventory, this.requestSlots(RequestType.INPUT));
            if (basicCraft != null) {
                slimefunItem = basicCraft.getMatchItem();
                boolean validOutputItem;
                ItemStack outputItemStack;
                if (slimefunItem instanceof SimpleValidItem simpleValidItem) {
                    validOutputItem = true;
                    outputItemStack = simpleValidItem.getValidItem();
                } else {
                    validOutputItem = false;
                    outputItemStack = basicCraft.getMatchItem().getRecipeOutput();
                }
                ClickType clickType = inventoryClickEvent.getClick();
                basicCraft.setMatchAmount(clickType.isRightClick() || clickType.isShiftClick() ? basicCraft.getMatchAmount() : 1);
                basicCraft.setMatchAmount(Math.min(basicCraft.getMatchAmount(), outputItemStack.getMaxStackSize() / outputItemStack.getAmount()));
                if (InventoryUtil.tryPushAllItem(inventory, this.requestSlots(RequestType.OUTPUT), basicCraft.getMatchAmount(), outputItemStack)) {
                    if (validOutputItem) {
                        FinalTech.getLogService().addItem(slimefunItem.getId(), outputItemStack.getAmount() * basicCraft.getMatchAmount(), this.getId(), LogSourceType.SLIMEFUN_MACHINE, (Player) inventoryClickEvent.getWhoClicked(), location, this.slimefunItem.getAddon().getJavaPlugin());
                    }

                    ItemStack existedItem;
                    itemStacks = slimefunItem.getRecipe();
                    for (int i = 0; i < itemStacks.length; i++) {
                        existedItem = inventory.getItem(this.requestSlots(RequestType.INPUT)[i]);
                        if (!ItemStackUtil.isItemNull(existedItem)) {
                            existedItem.setAmount(existedItem.getAmount() - itemStacks[i].getAmount() * basicCraft.getMatchAmount());
                            slimefunItem = SlimefunItem.getByItem(existedItem);
                            if (slimefunItem instanceof ValidItem) {
                                FinalTech.getLogService().subItem(slimefunItem.getId(), itemStacks[i].getAmount() * basicCraft.getMatchAmount(), this.getId(), LogSourceType.SLIMEFUN_MACHINE, (Player) inventoryClickEvent.getWhoClicked(), location, this.slimefunItem.getAddon().getJavaPlugin());
                            }
                        }
                    }
                }
            }
        } : super.onClick(location, slot);
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.parseSlot, this.parseIcon);
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
        SlimefunItem slimefunItem;
        List<MachineRecipe> machineRecipeList = MachineRecipeFactory.getInstance().getRecipe(this.getId());
        List<SlimefunItem> slimefunItemList = new ArrayList<>(machineRecipeList.size());
        for (MachineRecipe machineRecipe : machineRecipeList) {
            ItemStack[] output = machineRecipe.getOutput();
            if (output.length == 1) {
                slimefunItem = SlimefunItem.getByItem(output[0]);
                if (slimefunItem != null) {
                    slimefunItemList.add(slimefunItem);
                }
            }
        }

        BasicCraft basicCraft = BasicCraft.doCraftBySlimefunItem(slimefunItemList, inventory, this.requestSlots(RequestType.INPUT));

        if (basicCraft != null) {
            slimefunItem = basicCraft.getMatchItem();
            ItemStack matchItem = slimefunItem.getRecipeOutput();
            SfItemUtil.removeSlimefunId(matchItem);
            ItemStackUtil.addLoresToLast(matchItem,
                    FinalTech.getLanguageManager().replaceStringArray(FinalTech.getLanguageStringArray("items", this.getId(), "show-icon", "lore"),
                            String.valueOf(basicCraft.getMatchAmount())));
            inventory.setItem(this.parseSlot, matchItem);
        } else {
            inventory.setItem(this.parseSlot, this.parseIcon);
        }
    }
}
