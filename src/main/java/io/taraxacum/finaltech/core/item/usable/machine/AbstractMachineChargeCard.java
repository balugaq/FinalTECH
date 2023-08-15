package io.taraxacum.finaltech.core.item.usable.machine;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.networks.energy.EnergyNetComponentType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.protection.Interaction;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.usable.UsableSlimefunItem;
import io.taraxacum.finaltech.util.PermissionUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
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
public abstract class AbstractMachineChargeCard extends UsableSlimefunItem {
    public AbstractMachineChargeCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
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
        if(locationData == null) {
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

        if (!this.conditionMatch(player)) {
            player.sendRawMessage(FinalTech.getLanguageString("message", "no-condition", "player"));
            return;
        }

        SlimefunItem slimefunItem = SlimefunItem.getById(id);
        if (slimefunItem instanceof EnergyNetComponent energyNetComponent && energyNetComponent.getCapacity() > 0) {
            if (this.consume()) {
                if (playerRightClickEvent.getItem().getAmount() > 0) {
                    ItemStack item = playerRightClickEvent.getItem();
                    item.setAmount(item.getAmount() - 1);
                } else {
                    return;
                }
            }

            JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
            javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(this.getAddon().getJavaPlugin(), Particle.WAX_OFF, 0, block));

            int capacity = energyNetComponent.getCapacity();
            int chargeEnergy = (int) this.energy();
            if (!EnergyNetComponentType.CAPACITOR.equals(energyNetComponent.getEnergyComponentType()) && !EnergyNetComponentType.GENERATOR.equals(energyNetComponent.getEnergyComponentType())) {
                chargeEnergy += (int)((this.energy() - (int) this.energy()) * capacity);
            }
            if (!this.consume()) {
                chargeEnergy *= playerRightClickEvent.getItem().getAmount();
            }
            int storedEnergy = energyNetComponent.getCharge(location);
            chargeEnergy = chargeEnergy / 2 + storedEnergy / 2 > Integer.MAX_VALUE / 2 ? Integer.MAX_VALUE : chargeEnergy + storedEnergy;
            energyNetComponent.setCharge(location, Math.min(capacity, chargeEnergy));
        }
    }

    protected abstract double energy();

    /**
     * @return If using it will consume itself
     */
    protected abstract boolean consume();

    /**
     * If it can work.
     * May be designed to cost player's health or exp.
     */
    protected abstract boolean conditionMatch(@Nonnull Player player);
}
