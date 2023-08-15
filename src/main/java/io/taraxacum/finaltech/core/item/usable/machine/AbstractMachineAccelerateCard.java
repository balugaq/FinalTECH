package io.taraxacum.finaltech.core.item.usable.machine;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.usable.UsableSlimefunItem;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.dto.SlimefunLocationData;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public abstract class AbstractMachineAccelerateCard extends UsableSlimefunItem {
    public AbstractMachineAccelerateCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        playerRightClickEvent.cancel();

        Block block = playerRightClickEvent.getInteractEvent().getClickedBlock();
        if (block == null) {
            return;
        }

        Player player = playerRightClickEvent.getPlayer();
        if (player.isDead()) {
            return;
        }

        Location location = block.getLocation();
        LocationData locationData = FinalTech.getLocationDataService().getLocationData(location);
        if(!(locationData instanceof SlimefunLocationData slimefunLocationData)) {
            return;
        }

        String id = LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData);
        if (id == null) {
            return;
        }

        if (!PermissionUtil.checkPermission(player, location, Interaction.INTERACT_BLOCK, Interaction.BREAK_BLOCK, Interaction.PLACE_BLOCK)) {
            player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
            return;
        }

        if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
            BlockMenu blockMenu = slimefunLocationDataService.getBlockMenu(slimefunLocationData);
            if (blockMenu != null && !blockMenu.canOpen(block, player)) {
                player.sendRawMessage(FinalTech.getLanguageString("message", "no-permission", "location"));
                return;
            }
        }

        if (!this.conditionMatch(player)) {
            player.sendRawMessage(FinalTech.getLanguageString("message", "no-condition", "player"));
            return;
        }

        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem == null || FinalTech.isAntiAccelerateSlimefunItem(slimefunItem.getId())) {
            return;
        }

        BlockTicker blockTicker = slimefunItem.getBlockTicker();
        if (blockTicker == null) {
            return;
        }

        int time;
        if (this.consume()) {
            if (playerRightClickEvent.getItem().getAmount() > 0) {
                ItemStack item = playerRightClickEvent.getItem();
                item.setAmount(item.getAmount() - 1);
                time = this.times();
            } else {
                return;
            }
        } else {
            time = this.times() * playerRightClickEvent.getItem().getAmount();
        }

        JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
        javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));

        Runnable runnable = () -> {
            try {
                for (int i = 0; i < time; i++) {
                    FinalTech.getBlockTickerService().run(blockTicker, locationData);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        };

        if (blockTicker.isSynchronized() || !FinalTech.isAsyncSlimefunItem(slimefunItem.getId())) {
            javaPlugin.getServer().getScheduler().runTask(javaPlugin, runnable);
        } else {
            FinalTech.getLocationRunnableFactory().waitThenRun(runnable, location);
        }
    }

    protected abstract int times();

    /**
     * @return If using it will consume itself
     */
    protected abstract boolean consume();

    /**
     * If it can work.
     * May cost player's health or exp;
     * @param player
     * @return
     */
    protected abstract boolean conditionMatch(@Nonnull Player player);
}
