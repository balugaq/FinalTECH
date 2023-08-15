package io.taraxacum.finaltech.core.inventory.clicker;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.core.interfaces.DigitalItem;
import io.taraxacum.finaltech.core.item.machine.clicker.AbstractClickerMachine;
import io.taraxacum.finaltech.util.LocationUtil;
import io.taraxacum.libs.plugin.inventory.template.LogicInventory;
import io.taraxacum.libs.plugin.util.InventoryUtil;
import io.taraxacum.libs.plugin.util.ItemStackUtil;
import io.taraxacum.libs.plugin.util.ParticleUtil;
import io.taraxacum.libs.slimefun.service.SlimefunLocationDataService;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 */
public class ControllableRemoteAccessorInventory extends AbstractClickerInventory implements LogicInventory {
    private final int[] border = new int[] {0, 1, 2, 3, 5, 6, 7, 8};
    private final int[] contentSlot = new int[] {4};

    private final int range;

    public ControllableRemoteAccessorInventory(@Nonnull AbstractClickerMachine abstractClickerMachine, int range) {
        super(abstractClickerMachine);
        this.range = range;
    }

    @Nonnull
    @Override
    protected int[] getBorder() {
        return this.border;
    }

    @Override
    protected void initSelf() {

    }

    @Override
    protected void openFunction(@Nonnull Player player, @Nonnull Location location, @Nonnull Inventory inventory) {
        Block block = location.getBlock();
        ItemStack itemStack = inventory.getItem(this.contentSlot[0]);
        if(!ItemStackUtil.isItemNull(itemStack)) {
            SlimefunItem slimefunItem = SlimefunItem.getByItem(itemStack);
            if (slimefunItem instanceof DigitalItem digitalItem) {
                InventoryUtil.closeInv(inventory);

                int digit = digitalItem.getDigit();

                BlockData blockData = block.getState().getBlockData();
                if (blockData instanceof Directional) {
                    BlockFace blockFace = ((Directional) blockData).getFacing();
                    Block targetBlock = block;

                    if(digit > 0) {
                        targetBlock = targetBlock.getRelative(blockFace, digit);
                        if(!targetBlock.getChunk().isLoaded()) {
                            return;
                        }
                        if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                            BlockMenu targetBlockMenu = slimefunLocationDataService.getBlockMenu(targetBlock.getLocation());
                            if(targetBlockMenu != null && targetBlockMenu.canOpen(targetBlock, player)) {
                                JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
                                Block finalTargetBlock = targetBlock;
                                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, finalTargetBlock));
                                javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.WAX_OFF, 0, 0.25, LocationUtil.getCenterLocation(block), LocationUtil.getCenterLocation(finalTargetBlock)));
                                targetBlockMenu.open(player);
                            }
                        }
                    } else if(digit == 0) {
                        for (int i = 0; i < this.range; i++) {
                            targetBlock = targetBlock.getRelative(blockFace);
                            if(!targetBlock.getChunk().isLoaded()) {
                                return;
                            }
                            if(FinalTech.getLocationDataService() instanceof SlimefunLocationDataService slimefunLocationDataService) {
                                BlockMenu targetBlockMenu = slimefunLocationDataService.getBlockMenu(targetBlock.getLocation());
                                if (targetBlockMenu != null && targetBlockMenu.canOpen(targetBlock, player)) {
                                    JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
                                    Block finalTargetBlock = targetBlock;
                                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, finalTargetBlock));
                                    javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawLineByDistance(javaPlugin, Particle.WAX_OFF, 0, 0.25, LocationUtil.getCenterLocation(block), LocationUtil.getCenterLocation(finalTargetBlock)));
                                    targetBlockMenu.open(player);
                                    break;
                                }
                            }
                        }
                    }
                }
                return;
            }
        }

        JavaPlugin javaPlugin = this.slimefunItem.getAddon().getJavaPlugin();
        javaPlugin.getServer().getScheduler().runTaskAsynchronously(javaPlugin, () -> ParticleUtil.drawCubeByBlock(javaPlugin, Particle.WAX_OFF, 0, block));
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Nonnull
    @Override
    public int[] requestSlots() {
        return this.contentSlot;
    }

    @Override
    public void updateInventory(@Nonnull Inventory inventory, @Nonnull Location location) {

    }
}
