package io.taraxacum.libs.slimefun.service.impl;

import com.xzavier0722.mc.plugin.slimefun4.storage.controller.BlockDataController;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.exceptions.IncompatibleItemHandlerException;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetProvider;
import io.taraxacum.libs.plugin.ld.LocationData;
import io.taraxacum.libs.slimefun.dto.LocationDatabaseData;
import io.taraxacum.libs.slimefun.service.BlockTickerService;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.Objects.handlers.BlockTicker;
import org.bukkit.Location;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Final_ROOT
 */
public class DatabaseTickerService extends BlockTickerService {

    private final BlockDataController blockDataController;

    private final MethodHandle methodHandleTick;

    private final MethodHandle methodHandleGetGeneratedOut;

    private final MethodHandle methodHandleWillExplode;

    private DatabaseTickerService(@Nonnull BlockDataController blockDataController, @Nonnull MethodHandle methodHandleTick, @Nonnull MethodHandle methodHandleGetGeneratedOut, @Nonnull MethodHandle methodHandleWillExplode) {
        this.blockDataController = blockDataController;
        this.methodHandleTick = methodHandleTick;
        this.methodHandleGetGeneratedOut = methodHandleGetGeneratedOut;
        this.methodHandleWillExplode = methodHandleWillExplode;
    }

    @Override
    public void run(@Nonnull BlockTicker blockTicker, @Nonnull LocationData locationData) throws Throwable {
        if(locationData instanceof LocationDatabaseData locationDatabaseData) {
            SlimefunItem slimefunItem = SlimefunItem.getById(locationDatabaseData.getSlimefunBlockData().getSfId());
            if(slimefunItem != null) {
                this.methodHandleTick.invokeExact(blockTicker, locationDatabaseData.getLocation().getBlock(), slimefunItem, locationDatabaseData.getSlimefunBlockData());
            }
        }
    }

    @Override
    public final BlockTicker warp(@Nonnull BlockTicker blockTicker, @Nonnull BiConsumer<Runnable, LocationData> ticker, @Nonnull Function<LocationData, Boolean>[] beforeTicks, @Nonnull Consumer<LocationData>[] afterTicks, @Nonnull Runnable[] extraUniqueTicks) {
        return new BlockTicker() {
            @Override
            public boolean isSynchronized() {
                return blockTicker.isSynchronized();
            }

            @Override
            public void tick(Block b, SlimefunItem item, Config data) {

            }

            public void tick(Block block, SlimefunItem slimefunItem, SlimefunBlockData data) {
                LocationData locationData = new LocationDatabaseData(block.getLocation(), data);
                ticker.accept(() -> {
                    try {
                        for (Function<LocationData, Boolean> function : beforeTicks) {
                            if (!function.apply(locationData)) {
                                return;
                            }
                        }
                        DatabaseTickerService.this.methodHandleTick.invokeExact(blockTicker, block, slimefunItem, data);
                        for (Consumer<LocationData> consumer : afterTicks) {
                            consumer.accept(locationData);
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }, locationData);
            }

            @Override
            public void update() {
                blockTicker.update();
            }

            @Override
            public Optional<IncompatibleItemHandlerException> validate(SlimefunItem item) {
                return blockTicker.validate(item);
            }

            @Override
            public void uniqueTick() {
                for(Runnable runnable : extraUniqueTicks) {
                    runnable.run();
                }
                blockTicker.uniqueTick();
            }

            @Override
            public Class<? extends ItemHandler> getIdentifier() {
                return blockTicker.getIdentifier();
            }

            @Override
            public void startNewTick() {
                blockTicker.startNewTick();
            }
        };
    }

    @Override
    public int getGeneratedOutput(@Nonnull EnergyNetProvider energyNetProvider, @Nonnull Location location) throws Throwable {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return (int) this.methodHandleGetGeneratedOut.invokeExact(energyNetProvider, location, slimefunBlockData);
    }

    @Override
    public boolean willExplode(@Nonnull EnergyNetProvider energyNetProvider, @Nonnull Location location) throws Throwable {
        SlimefunBlockData slimefunBlockData = this.blockDataController.getBlockDataFromCache(location);
        return (boolean) this.methodHandleWillExplode.invokeExact(energyNetProvider, location, slimefunBlockData);
    }

    @Nullable
    public static DatabaseTickerService newInstance(@Nonnull BlockDataController blockDataController) {
        try {
            Method tickerClassMethod = BlockTicker.class.getMethod("tick", Block.class, SlimefunItem.class, SlimefunBlockData.class);
            MethodHandle methodHandleTick = MethodHandles.lookup().unreflect(tickerClassMethod);
            Method getGeneratedOutputClassMethod = EnergyNetProvider.class.getMethod("getGeneratedOutput", Location.class, SlimefunBlockData.class);
            MethodHandle methodHandleGetGeneratedOutput = MethodHandles.lookup().unreflect(getGeneratedOutputClassMethod);
            Method ClassMethod3 = EnergyNetProvider.class.getMethod("willExplode", Location.class, SlimefunBlockData.class);
            MethodHandle methodHandle3 = MethodHandles.lookup().unreflect(ClassMethod3);
            return new DatabaseTickerService(blockDataController, methodHandleTick, methodHandleGetGeneratedOutput, methodHandle3);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
