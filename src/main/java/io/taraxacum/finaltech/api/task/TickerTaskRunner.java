package io.taraxacum.finaltech.api.task;

import io.taraxacum.common.util.JavaUtil;
import io.taraxacum.finaltech.api.factory.ServerRunnableLockFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public class TickerTaskRunner<T, C extends AbstractSingleTickerTask<T>> extends AbstractTask {
    private List<C> tickerTaskList = new ArrayList<>();
    private final T object;
    private final ServerRunnableLockFactory<T> serverRunnableLockFactory;
    private BukkitTask bukkitTask;
    protected final static Map<Object, TickerTaskRunner<Object, AbstractSingleTickerTask<Object>>> OBJECT_TASK_MAP = new HashMap<>();

    protected TickerTaskRunner(@Nonnull JavaPlugin javaPlugin, @Nonnull T t) {
        super(javaPlugin);
        this.object = t;
        this.serverRunnableLockFactory = ServerRunnableLockFactory.getInstance(javaPlugin, (Class<T>) t.getClass());
    }

    public T getObject() {
        return object;
    }

    public List<C> getTickerTaskList() {
        return tickerTaskList;
    }

    @Override
    public synchronized void run() {
        if (this.bukkitTask == null) {
            this.bukkitTask = this.getJavaPlugin().getServer().getScheduler().runTaskTimerAsynchronously(this.getJavaPlugin(), () -> {
                this.tickerTaskList.sort(Comparator.comparingInt(AbstractSingleTickerTask::getPriority));
                Iterator<C> iterator = this.tickerTaskList.iterator();
                while (iterator.hasNext()) {
                    C tickerTask = iterator.next();
                    if (tickerTask.getTime() == 0) {
                        if (tickerTask.isSync()) {
                            this.getJavaPlugin().getServer().getScheduler().runTask(this.getJavaPlugin(), () -> tickerTask.endTick(object));
                        } else {
                            this.serverRunnableLockFactory.waitThenRun(() -> tickerTask.endTick(object), object);
                        }
                        iterator.remove();
                    } else {
                        this.tickTask(tickerTask);
                    }
                    tickerTask.setTime(tickerTask.getTime() - 1);
                }
                if (this.tickerTaskList.size() == 0) {
                    this.bukkitTask.cancel();
                    OBJECT_TASK_MAP.remove(this.object);
                    this.bukkitTask = null;
                }
            }, 1, 1);
        }
    }

    private void tickTask(@Nonnull C tickerTask) {
        if (tickerTask.isSync()) {
            this.getJavaPlugin().getServer().getScheduler().runTask(this.getJavaPlugin(), () -> tickerTask.tick(object));
        } else {
            this.serverRunnableLockFactory.waitThenRun(() -> tickerTask.tick(object), object);
        }
    }

    @Nonnull
    public static <T> TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> applyOrAddTo(@Nonnull AbstractSingleTickerTask<T> tickerTask, @Nonnull T t, @Nonnull JavaPlugin javaPlugin) {
        TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> tickerTaskRunner;
        List<AbstractSingleTickerTask<Object>> tickerTaskList;
        if (OBJECT_TASK_MAP.containsKey(t)) {
            tickerTaskRunner = OBJECT_TASK_MAP.get(t);
            tickerTaskList = tickerTaskRunner.tickerTaskList;
            for (AbstractSingleTickerTask<Object> existedTickerTask : tickerTaskList) {
                if (existedTickerTask.getId().equals(tickerTask.getId())) {
                    existedTickerTask.setTime(existedTickerTask.getTime() + tickerTask.getTime());
                    if (tickerTask.isSync()) {
                        tickerTaskRunner.getJavaPlugin().getServer().getScheduler().runTask(tickerTaskRunner.getJavaPlugin(), () -> tickerTask.addTick(t));
                    } else {
                        tickerTaskRunner.serverRunnableLockFactory.waitThenRun(() -> tickerTask.addTick(t), t);
                    }
                    return tickerTaskRunner;
                }
            }
            tickerTaskList = new ArrayList<>(tickerTaskList);
        } else {
            tickerTaskRunner = new TickerTaskRunner<>(javaPlugin, t);
            tickerTaskList = new ArrayList<>();
            OBJECT_TASK_MAP.put(t, tickerTaskRunner);
        }
        tickerTaskList.add((AbstractSingleTickerTask<Object>) tickerTask);
        tickerTaskRunner.tickerTaskList = tickerTaskList;
        if (tickerTask.isSync()) {
            tickerTaskRunner.getJavaPlugin().getServer().getScheduler().runTask(tickerTaskRunner.getJavaPlugin(), () -> tickerTask.startTick(t));
        } else {
            tickerTaskRunner.serverRunnableLockFactory.waitThenRun(() -> tickerTask.startTick(t), t);
        }
        tickerTaskRunner.run();
        return tickerTaskRunner;
    }

    @Nonnull
    public static <T> TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> applyOrSetTo(@Nonnull AbstractSingleTickerTask<T> tickerTask, @Nonnull T t, @Nonnull JavaPlugin javaPlugin) {
        TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> tickerTaskRunner;
        List<AbstractSingleTickerTask<Object>> tickerTaskList;
        if (OBJECT_TASK_MAP.containsKey(t)) {
            tickerTaskRunner = OBJECT_TASK_MAP.get(t);
            tickerTaskList = tickerTaskRunner.tickerTaskList;
            for (AbstractSingleTickerTask<Object> existedTickerTask : tickerTaskList) {
                if (existedTickerTask.getId().equals(tickerTask.getId())) {
                    existedTickerTask.setTime(tickerTask.getTime());
                    if (tickerTask.isSync()) {
                        tickerTaskRunner.getJavaPlugin().getServer().getScheduler().runTask(tickerTaskRunner.getJavaPlugin(), () -> tickerTask.addTick(t));
                    } else {
                        tickerTaskRunner.serverRunnableLockFactory.waitThenRun(() -> tickerTask.addTick(t), t);
                    }
                    return tickerTaskRunner;
                }
            }
            tickerTaskList = new ArrayList<>(tickerTaskList);
        } else {
            tickerTaskRunner = new TickerTaskRunner<>(javaPlugin, t);
            tickerTaskList = new ArrayList<>();
            OBJECT_TASK_MAP.put(t, tickerTaskRunner);
        }
        tickerTaskList.add((AbstractSingleTickerTask<Object>) tickerTask);
        tickerTaskRunner.tickerTaskList = tickerTaskList;
        if (tickerTask.isSync()) {
            tickerTaskRunner.getJavaPlugin().getServer().getScheduler().runTask(tickerTaskRunner.getJavaPlugin(), () -> tickerTask.startTick(t));
        } else {
            tickerTaskRunner.serverRunnableLockFactory.waitThenRun(() -> tickerTask.startTick(t), t);
        }
        tickerTaskRunner.run();
        return tickerTaskRunner;
    }

    public static <T> void clear(@Nonnull T t, String... ids) {
        Set<String> stringSet = JavaUtil.toSet(ids);
        TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> tickerTaskRunner = OBJECT_TASK_MAP.get(t);
        for (AbstractSingleTickerTask<Object> tickerTask : tickerTaskRunner.getTickerTaskList()) {
            if (stringSet.contains(tickerTask.getId())) {
                tickerTask.setTime(0);
            }
        }
    }

    public static <T> TickerTaskRunner<Object, AbstractSingleTickerTask<Object>> getInstance(@Nonnull T t) {
        return OBJECT_TASK_MAP.get(t);
    }
}
