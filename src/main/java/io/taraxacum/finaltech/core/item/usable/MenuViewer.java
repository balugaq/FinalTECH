package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.RecipeUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * A tool to show the input-slot and output-slot of a machine.{@link BlockMenuPreset}
 * @author Final_ROOT
 */
public class MenuViewer extends UsableSlimefunItem implements RecipeItem {
    private final int INSERT_SLOT_VALUE = 1;
    private final int WITHDRAW_SLOT_VALUE = 2;
    private final int INSERT_AND_WITHDRAW_SLOT_VALUE = INSERT_SLOT_VALUE + WITHDRAW_SLOT_VALUE;

    private final ItemStack VOID_ICON = new CustomItemStack(Material.GRAY_STAINED_GLASS_PANE, FinalTech.getLanguageString("items", this.getId(), "void-icon", "name"));
    private final ItemStack INPUT_ICON = new CustomItemStack(Material.BLUE_STAINED_GLASS_PANE, FinalTech.getLanguageString("items", this.getId(), "input-icon", "name"));
    private final ItemStack OUTPUT_ICON = new CustomItemStack(Material.ORANGE_STAINED_GLASS_PANE, FinalTech.getLanguageString("items", this.getId(), "output-icon", "name"));
    private final ItemStack INPUT_AND_OUTPUT_ICON = new CustomItemStack(Material.PURPLE_STAINED_GLASS_PANE, FinalTech.getLanguageString("items", this.getId(), "in-and-out-icon", "name"));

    public MenuViewer(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        Player player = playerRightClickEvent.getPlayer();
        if (!playerRightClickEvent.getClickedBlock().isEmpty()) {
            Location location = playerRightClickEvent.getClickedBlock().get().getLocation();
            if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                LocationData locationData = slimefunLocationDataService.getLocationData(location);
                if(locationData instanceof SlimefunLocationData slimefunLocationData) {
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(slimefunLocationData);
                    if (blockMenu != null) {
                        BlockMenuPreset preset = blockMenu.getPreset();
                        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();
                        int[] slotsAccessedByItemTransportInsert = preset.getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.INSERT, itemInOffHand);
                        int[] slotsAccessedByItemTransportWithdraw = preset.getSlotsAccessedByItemTransport(blockMenu, ItemTransportFlow.WITHDRAW, itemInOffHand);
                        int[] viewSlot = new int[blockMenu.getContents().length];
                        for (int slot : slotsAccessedByItemTransportInsert) {
                            if (slot >= 0 && slot < viewSlot.length) {
                                viewSlot[slot] += INSERT_SLOT_VALUE;
                            }
                        }
                        for (int slot : slotsAccessedByItemTransportWithdraw) {
                            if (slot >= 0 && slot < viewSlot.length) {
                                viewSlot[slot] += WITHDRAW_SLOT_VALUE;
                            }
                        }
                        ChestMenu chestMenu = new ChestMenu(preset.getTitle());
                        for (int slot = 0; slot < viewSlot.length; slot++) {
                            if (viewSlot[slot] == INSERT_SLOT_VALUE) {
                                chestMenu.addItem(slot, INPUT_ICON);
                            } else if (viewSlot[slot] == WITHDRAW_SLOT_VALUE) {
                                chestMenu.addItem(slot, OUTPUT_ICON);
                            } else if (viewSlot[slot] == INSERT_AND_WITHDRAW_SLOT_VALUE) {
                                chestMenu.addItem(slot, INPUT_AND_OUTPUT_ICON);
                            } else {
                                chestMenu.addItem(slot, VOID_ICON);
                            }
                            chestMenu.addMenuClickHandler(slot, ChestMenuUtils.getEmptyClickHandler());
                        }
                        chestMenu.open(player);
                    }
                }
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        RecipeUtil.registerDescriptiveRecipe(FinalTech.getLanguageManager(), this);
    }
}
