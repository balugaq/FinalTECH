package io.taraxacum.finaltech.util;

import io.github.thebusybiscuit.slimefun4.api.items.ItemState;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.player.PlayerProfile;
import io.github.thebusybiscuit.slimefun4.api.researches.Research;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.common.util.ReflectionUtil;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.common.pref.ComplexOptional;
import io.taraxacum.finaltech.core.exception.ParseErrorException;
import io.taraxacum.finaltech.core.interfaces.ExtraParameterItem;
import io.taraxacum.finaltech.core.dto.SpecialResearch;
import io.taraxacum.finaltech.setup.FinalTechItemStacks;
import io.taraxacum.libs.plugin.recipe.AdvancedMachineRecipe;
import io.taraxacum.libs.plugin.is.ItemAmountWrapper;
import io.taraxacum.finaltech.core.item.machine.AbstractMachine;
import io.taraxacum.libs.plugin.ld.LocationDataService;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Final_ROOT
 */
public final class MachineUtil {
    private static Map<String, ItemStack> itemStackCache = new HashMap<>();

    public static final BlockPlaceHandler BLOCK_PLACE_HANDLER_PLACER_ALLOW = new BlockPlaceHandler(true) {
        @Override
        public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {

        }
    };
    public static final BlockPlaceHandler BLOCK_PLACE_HANDLER_PLACER_DENY = new BlockPlaceHandler(false) {
        @Override
        public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {

        }
    };
    public static final BlockPlaceHandler BLOCK_PLACE_HANDLER_DENY = new BlockPlaceHandler(false) {
        @Override
        public void onPlayerPlace(@Nonnull BlockPlaceEvent blockPlaceEvent) {
            blockPlaceEvent.setCancelled(true);
        }
    };

    public static BlockBreakHandler simpleBlockBreakerHandler() {
        return new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> list) {

            }
        };
    }

    public static BlockBreakHandler simpleBlockBreakerHandler(@Nonnull LocationDataService locationDataService, @Nonnull AbstractMachine abstractMachine) {
        return new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> list) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(locationDataService instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(abstractMachine.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, abstractMachine.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, abstractMachine.getOutputSlot());
                    }
                }
            }
        };
    }

    public static BlockBreakHandler simpleBlockBreakerHandler(@Nonnull LocationDataService locationDataService, @Nonnull String id, @Nonnull int... slots) {
        return new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> list) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(locationDataService instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(id)) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, slots);
                    }
                }
            }
        };
    }

    public static BlockBreakHandler simpleBlockBreakerHandler(@Nonnull LocationDataService locationDataService, @Nonnull AbstractMachine abstractMachine, int... slots) {
        return new BlockBreakHandler(false, true) {
            @Override
            public void onPlayerBreak(@Nonnull BlockBreakEvent blockBreakEvent, @Nonnull ItemStack itemStack, @Nonnull List<ItemStack> list) {
                Location location = blockBreakEvent.getBlock().getLocation();
                if(locationDataService instanceof SlimefunLocationDataService slimefunLocationDataService) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(location);
                    if(blockMenu != null && blockMenu.getPreset().getID().equals(abstractMachine.getId())) {
                        Inventory inventory = blockMenu.toInventory();
                        InventoryUtil.dropItems(inventory, location, abstractMachine.getInputSlot());
                        InventoryUtil.dropItems(inventory, location, abstractMachine.getOutputSlot());
                        InventoryUtil.dropItems(inventory, location, slots);
                    }
                }
            }
        };
    }

    /**
     * @return Number of slots the machine have and can be interacted.
     */
    public static int calMachineSlotSize(@Nonnull AbstractMachine abstractMachine) {
        Set<Integer> slots = new HashSet<>();
        for (int slot : abstractMachine.getInputSlot()) {
            slots.add(slot);
        }
        for (int slot : abstractMachine.getOutputSlot()) {
            slots.add(slot);
        }
        return slots.size();
    }

    /**
     * @param sourceList May be altered! Clone it if necessary!
     */
    @Nonnull
    public static List<ItemAmountWrapper> calParsed(@Nonnull List<ItemAmountWrapper> sourceList, @Nonnull List<AdvancedMachineRecipe> recipeList, int amount) {
        for(int i = 0; i < amount; i++) {
            boolean work = false;
            List<ItemAmountWrapper> tempList = new ArrayList<>();
            for(ItemAmountWrapper oldItem : sourceList) {
                if(oldItem.getAmount() < Integer.MAX_VALUE / ConstantTableUtil.ITEM_MAX_STACK) {
                    for (AdvancedMachineRecipe advancedMachineRecipe : recipeList) {
                        for (AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputs()) {
                            ItemAmountWrapper outputItem = advancedRandomOutput.getOutputItem()[0];
                            if (advancedRandomOutput.getOutputItem().length == 1 && oldItem.getAmount() >= outputItem.getAmount() && ItemStackUtil.isItemSimilar(oldItem, outputItem)) {
                                int count = oldItem.getAmount() / outputItem.getAmount();
                                for (ItemAmountWrapper inputItem : advancedMachineRecipe.getInput()) {
                                    ItemAmountWrapper.addToList(tempList, inputItem, count * advancedMachineRecipe.getWeightSum() / advancedRandomOutput.weight());
                                }
                                oldItem.setAmount(oldItem.getAmount() - count * outputItem.getAmount());
                                work = true;
                            }
                        }
                    }
                }
                if (oldItem.getAmount() > 0) {
                    ItemAmountWrapper.addToList(tempList, oldItem);
                    oldItem.setAmount(0);
                }
            }
            sourceList = tempList;
            if (!work) {
                break;
            }
        }
        return sourceList;
    }

    @Nonnull
    public static ItemStack cloneAsDescriptiveItemWithLore(@Nonnull ItemStack itemStack, @Nonnull String... loreToLast) {
        ItemMeta itemMeta;
        if(!itemStack.hasItemMeta()) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        } else {
            itemMeta = itemStack.getItemMeta();
        }

        ItemStack result = new ItemStack(itemStack);
        ItemStackUtil.clearNBT(itemMeta);
        ItemStackUtil.addLoresToLast(itemMeta, loreToLast);
        result.setItemMeta(itemMeta);

        return result;
    }

    @Nonnull
    public static ItemStack cloneAsDescriptiveItem(@Nullable ItemStack itemStack) {
        if (ItemStackUtil.isItemNull(itemStack)) {
            return ItemStackUtil.AIR;
        }
        SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
        if (slimefunItem != null && ItemState.ENABLED.equals(slimefunItem.getState())) {
            ItemStack result = MachineUtil.cloneAsDescriptiveItem(slimefunItem);
            result.setAmount(itemStack.getAmount());
            return result;
        } else if(ItemStackUtil.isRawMaterial(itemStack)) {
            ItemStack result = ItemStackUtil.cloneItem(itemStack);
            ItemMeta itemMeta;
            if(!itemStack.hasItemMeta()) {
                itemMeta = Bukkit.getItemFactory().getItemMeta(result.getType());
            } else {
                itemMeta = result.getItemMeta();
            }
            if(itemMeta == null) {
                return result;
            }

            ItemStackUtil.clearNBT(itemMeta);

            if(FinalTech.getLanguageManager().containPath("option", "ICON", "descriptive-item", "minecraft")) {
                ItemStackUtil.addLoresToLast(itemMeta, FinalTech.getLanguageManager().getString("option", "ICON", "descriptive-item", "minecraft"));
            } else {
                String lore = FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("option", "ICON", "descriptive-item", "default"),
                        "minecraft");
                ItemStackUtil.addLoresToLast(itemMeta, lore);
            }
            result.setItemMeta(itemMeta);
            return result;
        } else {
            return ItemStackUtil.cloneWithoutNBT(itemStack);
        }
    }

    @Nonnull
    public static ItemStack cloneAsDescriptiveItem(@Nonnull SlimefunItem slimefunItem) {
        ItemStack itemStack = ItemStackUtil.cloneItem(slimefunItem.getItem());
        if(!itemStack.hasItemMeta()) {
            return itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        ItemStackUtil.clearNBT(itemMeta);

        if (ItemState.ENABLED.equals(slimefunItem.getState())) {
            if (FinalTech.getLanguageManager().containPath("option", "ICON", "descriptive-item", slimefunItem.getAddon().getName())) {
                ItemStackUtil.addLoresToLast(itemMeta, FinalTech.getLanguageManager().getString("option", "ICON", "descriptive-item", slimefunItem.getAddon().getName()));
            } else {
                String lore = FinalTech.getLanguageManager().replaceString(FinalTech.getLanguageString("option", "ICON", "descriptive-item", "default"),
                        slimefunItem.getAddon().getName());
                ItemStackUtil.addLoresToLast(itemMeta, lore);
            }
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    @Nonnull
    public static ItemStack getLockedItem(@Nonnull Player player, @Nonnull Research research) {
        ItemStack itemStack = ItemStackUtil.cloneItem(ChestMenuUtils.getNotResearchedItem());
        List<String> stringList = new ArrayList<>();
        if (research instanceof SpecialResearch specialResearch) {
            stringList = List.of(specialResearch.getShowText(player));
        } else {
            Optional<PlayerProfile> optionalPlayerProfile = PlayerProfile.find(player);
            if (optionalPlayerProfile.isPresent() && !optionalPlayerProfile.get().hasUnlocked(research)) {
                // todo translate
                stringList.add("§7" + research.getName(player));
                stringList.add("§4§l" + Slimefun.getLocalization().getMessage(player, "guide.locked"));
                stringList.add("§a> Click to unlock");
                stringList.add("");
                stringList.add("§7Cost: §b" + research.getCost() + " Level(s)");
            }
        }

        ItemStackUtil.setItemName(itemStack, research.getName(player));
        ItemStackUtil.setLore(itemStack, stringList);
        return itemStack;
    }

    @Nonnull
    public static ComplexOptional<ItemStack> getItemStackById(@Nonnull String itemId) {
        // TODO clear item stack cache
        if (itemStackCache.containsKey(itemId)) {
            return new ComplexOptional<>(itemStackCache.get(itemId));
        }

        if ("error".equals(itemId)) {
            return new ComplexOptional<>();
        }

        SlimefunItem slimefunItem = SlimefunItem.getById(itemId);
        if (slimefunItem instanceof ExtraParameterItem extraParameterItem) {
            return new ComplexOptional<>(strings -> {
                try {
                    return extraParameterItem.getByExtraParameter(strings);
                } catch (ParseErrorException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        } else if (slimefunItem != null) {
            itemStackCache.put(itemId, slimefunItem.getItem());
            return new ComplexOptional<>(slimefunItem.getItem());
        } else {
            Material material = Material.getMaterial(itemId);
            if (material != null) {
                ItemStack itemStack = new ItemStack(material);
                itemStackCache.put(itemId, itemStack);
                return new ComplexOptional<>(itemStack);
            } else if (itemId.startsWith("FINALTECH_")) {
                String prefix = "FINALTECH_";
                ItemStack itemStack = ReflectionUtil.getStaticValue(FinalTechItemStacks.class, itemId.substring(prefix.length()));
                if (itemStack != null) {
                    itemStackCache.put(itemId, itemStack);
                    return new ComplexOptional<>(itemStack);
                }
            }
        }

        itemStackCache.put(itemId, null);
        return new ComplexOptional<>();
    }
}
