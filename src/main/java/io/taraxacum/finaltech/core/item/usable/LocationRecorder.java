package io.taraxacum.finaltech.core.item.usable;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.RecipeItem;
import io.taraxacum.finaltech.util.*;
import io.taraxacum.libs.plugin.dto.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public class LocationRecorder extends UsableSlimefunItem implements RecipeItem {
    private final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));

    public LocationRecorder(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        PlayerInteractEvent interactEvent = playerRightClickEvent.getInteractEvent();
        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();

        if (playerRightClickEvent.getPlayer().isSneaking()) {
            Block block = interactEvent.getClickedBlock();
            if (block != null) {
                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
                ItemStack item = playerRightClickEvent.getItem();
                LocationUtil.saveLocationToItem(item, block.getLocation());
                LocationUtil.updateLocationItem(item);
                PlayerUtil.updateIdInItem(item, playerRightClickEvent.getPlayer(), true);
            }
        } else {
            Location location = LocationUtil.parseLocationInItem(playerRightClickEvent.getItem());
            if (location == null || !location.getChunk().isLoaded()) {
                return;
            }

            Block block = location.getBlock();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));

            Player player = playerRightClickEvent.getPlayer();
            if (!PermissionUtil.checkPermission(playerRightClickEvent.getPlayer(), location, Interaction.INTERACT_BLOCK, Interaction.BREAK_BLOCK, Interaction.PLACE_BLOCK)) {
                player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
                return;
            }

            if (FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                LocationData locationData = slimefunLocationDataService.getLocationData(location);
                if (locationData instanceof SlimefunLocationData slimefunLocationData) {
                    if (this.notAllowedId.contains(slimefunLocationData.getId())) {
                        player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
                        return;
                    }
                    BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(slimefunLocationData);
                    if(blockMenu != null) {
                        if (blockMenu.canOpen(block, player)) {
                            blockMenu.open(player);
                        } else {
                            player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
                        }
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
