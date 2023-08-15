package io.taraxacum.finaltech.core.item.usable.machine;

import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.core.attributes.MachineProcessHolder;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.github.thebusybiscuit.slimefun4.core.machines.MachineProcessor;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.item.usable.UsableSlimefunItem;
import io.taraxacum.finaltech.util.BlockTickerUtil;
import io.taraxacum.finaltech.util.ConfigUtil;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.util.LocationDataUtil;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Final_ROOT
 */
public abstract class AbstractOperationAccelerateCard extends UsableSlimefunItem {
    protected final Set<String> notAllowedId = new HashSet<>(ConfigUtil.getItemStringList(this, "not-allowed-id"));
    public AbstractOperationAccelerateCard(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item) {
        super(itemGroup, item);
    }

    @Override
    protected void function(@Nonnull PlayerRightClickEvent playerRightClickEvent) {
        playerRightClickEvent.cancel();

        Optional<Block> clickedBlock = playerRightClickEvent.getClickedBlock();
        ItemStack itemStack = playerRightClickEvent.getPlayer().getInventory().getItemInMainHand();
        if(clickedBlock.isPresent() && ItemStackUtil.isItemSimilar(itemStack, this.getItem())) {
            Block block = clickedBlock.get();
            LocationData locationData = FinalTech.getLocationDataService().getLocationData(block.getLocation());

            if(locationData != null
                    && !this.notAllowedId.contains(LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData))
                    && LocationDataUtil.getSlimefunItem(FinalTech.getLocationDataService(), locationData) instanceof MachineProcessHolder<?> machineProcessHolder) {
                MachineProcessor<?> machineProcessor = machineProcessHolder.getMachineProcessor();
                MachineOperation machineOperation = machineProcessor.getOperation(locationData.getLocation());

                String id = LocationDataUtil.getId(FinalTech.getLocationDataService(), locationData);
                if(machineOperation != null && id != null) {
                    BlockTickerUtil.runTask(FinalTech.getLocationRunnableFactory(), FinalTech.isAsyncSlimefunItem(id), () -> this.addProgress(machineOperation), locationData.getLocation());
                    if(this.consumable()) {
                        itemStack.setAmount(itemStack.getAmount() - 1);
                    }

                    JavaPlugin javaPlugin = this.getAddon().getJavaPlugin();
                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
                }
            }
        }
    }

    abstract void addProgress(@Nonnull MachineOperation machineOperation);

    abstract boolean consumable();
}
