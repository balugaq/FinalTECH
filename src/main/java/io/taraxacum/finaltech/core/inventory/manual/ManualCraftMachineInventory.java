package io.taraxacum.finaltech.core.inventory.manual;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.machine.manual.craft.AbstractManualCraftMachine;
import io.taraxacum.finaltech.util.MachineUtil;
import io.taraxacum.libs.plugin.dto.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.dto.ItemAmountWrapper;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.interfaces.LogicInventory;
import io.taraxacum.libs.plugin.interfaces.OpenFunctionInventory;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.AdvancedCraft;
import io.taraxacum.libs.slimefun.dto.MachineRecipeFactory;
import io.taraxacum.libs.slimefun.util.EnergyUtil;
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
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Final_ROOT
 */
public class ManualCraftMachineInventory extends AbstractManualMachineInventory implements LogicInventory, OpenFunctionInventory {
    private final int[] border = new int[] {48, 50};
    private final int[] inputBorder = new int[0];
    private final int[] outputBorder = new int[0];
    private final int[] inputSlot = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
    private final int[] outputSlot = new int[] {27, 28, 29, 30, 31, 32, 33, 34, 35, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};

    private final int nextSlot = 41;
    private final int previousSlot = 39;

    private final int statusSlot = 40;
    private final int[] statusLSlot = new int[] {38, 37, 36};
    private final int[] statusRSlot = new int[] {42, 43, 44};

    private final int craftSlot = 49;
    private final int[] craftLSlot = new int[] {47, 46, 45};
    private final int[] craftRSlot = new int[] {51, 52, 53};

    private final ItemStack craftIcon = ItemStackUtil.newItemStack(Material.YELLOW_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", "ManualCraftMachine", "craft-icon", "name"),
            FinalTech.getLanguageStringArray("items", "ManualCraftMachine", "craft-icon", "lore"));
    private final ItemStack statusIcon = ItemStackUtil.newItemStack(Material.RED_STAINED_GLASS_PANE,
            FinalTech.getLanguageString("items", "ManualCraftMachine", "status-icon", "name"),
            FinalTech.getLanguageStringArray("items", "ManualCraftMachine", "status-icon", "lore"));

    public final String key = "offset";
    public final String[] keyL = new String[] {"offset-l1", "offset-l2", "offset-l3"};
    public final String[] keyR = new String[] {"offset-r1", "offset-r2", "offset-r3"};
    private final String keyOrder = "order";
    private final String orderValueDesc = "desc";
    private final String orderValueAsc = "asc";

    private final AbstractManualCraftMachine manualCraftMachine;

    public ManualCraftMachineInventory(@Nonnull AbstractManualCraftMachine abstractMachine) {
        super(abstractMachine);
        this.manualCraftMachine = abstractMachine;
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected void initSelf() {
        this.defaultItemStack.put(this.previousSlot, ItemStackUtil.newItemStack(new SlimefunItemStack("_UI_PREVIOUS_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8\u21E6 Previous Page"),
                FinalTech.getLanguageString("items", "ManualCraftMachine", "previous-icon", "name"),
                FinalTech.getLanguageStringArray("items", "ManualCraftMachine", "previous-icon", "lore")));
        this.defaultItemStack.put(this.nextSlot, ItemStackUtil.newItemStack(new SlimefunItemStack("_UI_NEXT_INACTIVE", Material.BLACK_STAINED_GLASS_PANE, "&8Next Page \u21E8"),
                FinalTech.getLanguageString("items", "ManualCraftMachine", "next-icon", "name"),
                FinalTech.getLanguageStringArray("items", "ManualCraftMachine", "next-icon", "lore")));

        this.defaultItemStack.put(this.statusSlot, this.statusIcon);
        for (int slot : this.statusLSlot) {
            this.defaultItemStack.put(slot, this.statusIcon);
        }
        for (int slot : this.statusRSlot) {
            this.defaultItemStack.put(slot, this.statusIcon);
        }

        this.defaultItemStack.put(this.craftSlot, this.craftIcon);
        for (int slot : this.craftLSlot) {
            this.defaultItemStack.put(slot, this.craftIcon);
        }
        for (int slot : this.craftRSlot) {
            this.defaultItemStack.put(slot, this.craftIcon);
        }

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

    @Nullable
    @Override
    public Consumer<InventoryClickEvent> onClick(@Nonnull Location location, int slot) {
        Consumer<InventoryClickEvent> eventConsumer = this.onClickStatusSlot(location, slot);
        if (eventConsumer != null) {
            return eventConsumer;
        }
        eventConsumer = this.onClickCraftSlot(location, slot);
        if (eventConsumer != null) {
            return eventConsumer;
        }

        return switch (slot) {
            case previousSlot -> this.onClickPrevious(location);
            case nextSlot -> this.onClickNext(location);
            case statusSlot -> this.onClickStatus(location);
            case craftSlot -> this.onClickCraft(location);
            default -> super.onClick(location, slot);
        };
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return new int[0];
    }

    @Nonnull
    @Override
    public int[] requestSlots(@Nonnull RequestType requestType) {
        return this.inputSlot;
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(locationData == null) {
            return;
        }

        String chargeStr = EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData);
        int charge = Integer.parseInt(chargeStr);

        AdvancedCraft craft = null;
        String order = FinalTech.getLocationDataService().getLocationData(locationData, this.keyOrder);
        List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(this.getId());
        String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, this.key);
        int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
        if (order == null || this.orderValueAsc.equals(order)) {
            craft = AdvancedCraft.craftAsc(inventory, this.inputSlot, advancedMachineRecipeList, this.inputSlot.length, offset);
        } else if (this.orderValueDesc.equals(order)) {
            craft = AdvancedCraft.craftDesc(inventory, this.inputSlot, advancedMachineRecipeList, this.inputSlot.length, offset);
        }

        if (craft != null) {
            offset = craft.getOffset();
            FinalTech.getLocationDataService().setLocationData(locationData, this.key, String.valueOf(craft.getOffset()));
            ItemStack itemStack = MachineUtil.cloneAsDescriptiveItemWithLore(craft.getOutputItemList()[0].getItemStack(),
                    FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", "ManualCraftMachine", "match-item", "lore"),
                            String.valueOf(Math.min(charge, craft.getMatchCount()))));
            inventory.setItem(this.statusSlot, itemStack);

            int offsetR = offset + 1;
            for (int i = 0; i < this.statusRSlot.length; i++) {
                craft = AdvancedCraft.craftAsc(inventory, this.inputSlot, advancedMachineRecipeList, this.inputSlot.length, offsetR);
                if (craft != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyR[i], String.valueOf(craft.getOffset()));
                    offsetR = craft.getOffset() + 1;
                    itemStack = MachineUtil.cloneAsDescriptiveItemWithLore(craft.getOutputItemList()[0].getItemStack(),
                            FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", "ManualCraftMachine", "match-item", "lore"),
                                    String.valueOf(Math.min(charge, craft.getMatchCount()))));
                    inventory.setItem(this.statusRSlot[i], itemStack);
                } else {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyR[i], null);
                    inventory.setItem(this.statusRSlot[i], this.statusIcon);
                }
            }

            int offsetL = offset - 1;
            for (int i = 0; i < this.statusLSlot.length; i++) {
                craft = AdvancedCraft.craftDesc(inventory, this.inputSlot, advancedMachineRecipeList, this.inputSlot.length, offsetL);
                if (craft != null) {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyL[i], String.valueOf(craft.getOffset()));
                    offsetL = craft.getOffset() - 1;
                    itemStack = MachineUtil.cloneAsDescriptiveItemWithLore(craft.getOutputItemList()[0].getItemStack(),
                            FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("items", "ManualCraftMachine", "match-item", "lore"),
                                    String.valueOf(Math.min(charge, craft.getMatchCount()))));
                    inventory.setItem(this.statusLSlot[i], itemStack);
                } else {
                    FinalTech.getLocationDataService().setLocationData(locationData, this.keyL[i], null);
                    inventory.setItem(this.statusLSlot[i], this.statusIcon);
                }
            }
        } else {
            FinalTech.getLocationDataService().setLocationData(locationData, this.key, "0");
            inventory.setItem(this.statusSlot, this.statusIcon);
            for (int slot : this.statusRSlot) {
                inventory.setItem(slot, this.statusIcon);
            }
            for (int slot : this.statusLSlot) {
                inventory.setItem(slot, this.statusIcon);
            }
        }

        FinalTech.getLocationDataService().setLocationData(locationData, this.keyOrder, null);

        for(int slot : this.craftLSlot) {
            ItemStack itemStack = inventory.getItem(slot);
            if(!ItemStackUtil.isItemNull(itemStack)) {
                ItemStackUtil.setLore(itemStack,
                        FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", "ManualCraftMachine", "craft-icon", "lore"),
                                chargeStr));
            }
        }

        for(int slot : this.craftRSlot) {
            ItemStack itemStack = inventory.getItem(slot);
            if(!ItemStackUtil.isItemNull(itemStack)) {
                ItemStackUtil.setLore(itemStack,
                        FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", "ManualCraftMachine", "craft-icon", "lore"),
                                chargeStr));
            }
        }

        ItemStack itemStack = inventory.getItem(this.craftSlot);
        if(!ItemStackUtil.isItemNull(itemStack)) {
            ItemStackUtil.setLore(itemStack,
                    FinalTech.getLanguageManager().replaceStringList(FinalTech.getLanguageStringList("items", "ManualCraftMachine", "craft-icon", "lore"),
                            chargeStr));
        }
    }

    public void doFunction(@Nonnull Inventory inventory, @Nonnull Location location, @Nonnull ClickType clickType, @Nonnull Player player, int offset) {
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if (locationData == null) {
            return;
        }

        int charge = Integer.parseInt(EnergyUtil.getCharge(FinalTech.getLocationDataService(), locationData));
        if(charge == 0) {
            return;
        }

        if (InventoryUtil.slotCount(inventory, this.outputSlot) == this.outputSlot.length) {
            return;
        }

        int quantity;
        if(!clickType.isRightClick()) {
            if(!clickType.isShiftClick()) {
                // left-click and default 1
                quantity = this.manualCraftMachine.getLeftClickAmount();
            } else {
                // left-shift-click and default 576
                quantity = this.manualCraftMachine.getLeftShiftClickAmount();
            }
        } else {
            if(!clickType.isShiftClick()) {
                // right-click and default 64
                quantity = this.manualCraftMachine.getRightClickAmount();
            } else {
                // right-shift-click and default 2304
                quantity = this.manualCraftMachine.getRightShiftClickAmount();
            }
        }

        quantity = Math.min(quantity, charge / this.manualCraftMachine.getConsume());

        List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(this.getId());
        List<AdvancedMachineRecipe> targetAdvancedRecipeList = List.of(advancedMachineRecipeList.get(offset % advancedMachineRecipeList.size()));

        AdvancedCraft craft = AdvancedCraft.craftAsc(inventory, this.inputSlot, targetAdvancedRecipeList, quantity, 0);

        if (craft == null) {
            return;
        }

        ItemAmountWrapper[] outputItems = craft.getOutputItemList();
        for (ItemAmountWrapper itemAmountWrapper : outputItems) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemAmountWrapper.getItemStack());
            if (slimefunItem != null && !slimefunItem.canUse(player, true)) {
                return;
            }
        }

        if (advancedMachineRecipeList.get(craft.getOffset()).isRandomOutput()) {
            int maxStackSize = inventory.getMaxStackSize();
            AdvancedMachineRecipe advancedMachineRecipe = advancedMachineRecipeList.get(craft.getOffset());
            for(AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputs()) {
                for(ItemAmountWrapper itemAmountWrapper : advancedRandomOutput.outputItem()) {
                    maxStackSize = Math.min(maxStackSize, itemAmountWrapper.getItemStack().getMaxStackSize());
                }
            }
            craft.setMatchCount(Math.min(craft.getMatchCount(), (this.outputSlot.length - InventoryUtil.slotCount(inventory, this.outputSlot)) * maxStackSize));
        }

        AdvancedMachineRecipe advancedMachineRecipe = advancedMachineRecipeList.get(craft.getOffset());
        if (advancedMachineRecipe.getOutputs().length > 1) {
            craft.setMatchCount(Math.min(inventory.getMaxStackSize(), craft.getMatchCount()));
        }

        craft.setMatchCount(Math.min(craft.getMatchCount(), InventoryUtil.calMaxMatch(inventory, this.outputSlot, craft.getOutputItemList())));
        if (craft.getMatchCount() == 0) {
            return;
        }

        int pushAmount = InventoryUtil.tryPushItem(inventory, this.outputSlot, craft.getMatchCount(), craft.getOutputItemList());
        if(pushAmount > 0) {
            craft.setMatchCount(pushAmount);
            craft.consumeItem(inventory);
        }

        EnergyUtil.setCharge(FinalTech.getLocationDataService(), locationData, String.valueOf(charge - craft.getMatchCount() * this.manualCraftMachine.getConsume()));

        this.updateInventory(inventory, location);
    }

    private boolean verifyCount(@Nonnull Location location) {
        Integer count = this.manualCraftMachine.getLocationCountMap().getOrDefault(location, 0);
        if(count < this.manualCraftMachine.getCountThreshold()) {
            this.manualCraftMachine.getLocationCountMap().put(location, ++count);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onOpen(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        if (!this.verifyCount(location)) {
            return;
        }

        ManualCraftMachineInventory.this.updateInventory(inventory, location);
    }
    
    @Nullable
    private Consumer<InventoryClickEvent> onClickStatusSlot(@Nonnull Location location, int slot) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();

        int[] statusSlots = JavaUtil.merge(this.statusLSlot, this.statusRSlot);
        String[] keys = JavaUtil.merge(this.keyL, this.keyR);
        for (int slotP = 0; slotP < statusSlots.length; slotP++) {
            if (slot == statusSlots[slotP]) {
                final String key = keys[slotP];

                return inventoryClickEvent -> {
                    inventoryClickEvent.setCancelled(true);

                    if (!this.verifyCount(location)) {
                        return;
                    }

                    LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                    if (locationData == null) {
                        return;
                    }

                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));

                    FinalTech.getLocationDataService().setLocationData(locationData, this.key, FinalTech.getLocationDataService().getLocationData(locationData, key));
                    this.updateInventory(inventoryClickEvent.getInventory(), location);
                };
            }
        }
        
        return null;
    }

    @Nullable
    private Consumer<InventoryClickEvent> onClickCraftSlot(@Nonnull Location location, int slot) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();

        int[] statusSlots = JavaUtil.merge(this.craftLSlot, this.craftRSlot);
        String[] keys = JavaUtil.merge(this.keyL, this.keyR);
        for (int slotP = 0; slotP < statusSlots.length; slotP++) {
            if (slot == statusSlots[slotP]) {
                final String key = keys[slotP];

                return inventoryClickEvent -> {
                    inventoryClickEvent.setCancelled(true);

                    if (!this.verifyCount(location)) {
                        return;
                    }

                    LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
                    if (locationData == null) {
                        return;
                    }

                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));
                    String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, key);
                    int offset = offsetStr != null ? Integer.parseInt(offsetStr) : 0;
                    this.doFunction(inventoryClickEvent.getInventory(), location, inventoryClickEvent.getClick(), (Player) inventoryClickEvent.getWhoClicked(), offset);
                };
            }
        }

        return null;
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickPrevious(@Nonnull Location location) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (!this.verifyCount(location)) {
                return;
            }

            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData == null) {
                return;
            }

            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));

            String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, key);
            int offset = offsetStr != null ? Integer.parseInt(offsetStr) : 0;
            int length = MachineRecipeFactory.getInstance().getRecipe(this.getId()).size();
            offset = (offset + length - 1) % length;
            FinalTech.getLocationDataService().setLocationData(locationData, key, String.valueOf(offset));
            FinalTech.getLocationDataService().setLocationData(locationData, keyOrder, orderValueDesc);
            this.updateInventory(inventoryClickEvent.getInventory(), location);
        };
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickNext(@Nonnull Location location) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (!this.verifyCount(location)) {
                return;
            }

            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData == null) {
                return;
            }

            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));

            String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, key);
            int offset = offsetStr != null ? Integer.parseInt(offsetStr) : 0;
            int length = MachineRecipeFactory.getInstance().getAdvancedRecipe(this.getId()).size();
            offset = (offset + 1) % length;
            FinalTech.getLocationDataService().setLocationData(locationData, key, String.valueOf(offset));
            FinalTech.getLocationDataService().setLocationData(locationData, keyOrder, orderValueAsc);
            ManualCraftMachineInventory.this.updateInventory(inventoryClickEvent.getInventory(), location);
        };
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickCraft(@Nonnull Location location) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (!this.verifyCount(location)) {
                return;
            }

            LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
            if (locationData == null) {
                return;
            }

            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));

            String offsetStr = FinalTech.getLocationDataService().getLocationData(locationData, key);
            int offset = offsetStr != null ? Integer.parseInt(offsetStr) : 0;
            ManualCraftMachineInventory.this.doFunction(inventoryClickEvent.getInventory(), location, inventoryClickEvent.getClick(), (Player) inventoryClickEvent.getWhoClicked(), offset);
        };
    }

    @Nonnull
    private Consumer<InventoryClickEvent> onClickStatus(@Nonnull Location location) {
        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
        return inventoryClickEvent -> {
            inventoryClickEvent.setCancelled(true);

            if (!this.verifyCount(location)) {
                return;
            }

            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, location.getBlock()));
            InventoryUtil.stockSlots(inventoryClickEvent.getInventory(), this.inputSlot);
        };
    }
}
