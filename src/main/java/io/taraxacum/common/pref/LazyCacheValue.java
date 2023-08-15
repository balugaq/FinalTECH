package io.taraxacum.common.pref;

import java.util.function.Supplier;

/**
 * @author Final_ROOT
 * @param <T>
 */
public class LazyCacheValue<T> {

    private T result;

    private Supplier<T> supplier;

    private boolean done = false;

    public LazyCacheValue(Supplier<T> supplier) {
        this.supplier = supplier;
        this.result = null;
    }

    public T getResult() {
        if (!this.done) {
            this.result = this.supplier.get();
            this.done = true;
        }
        return this.result;
    }

    public void resetResult() {
        this.done = false;
    }

    public T resetAndGetResult() {
        this.result = this.supplier.get();
        return this.result;
    }

    public void reset(Supplier<T> supplier) {
        this.done = false;
        this.supplier = supplier;
        this.result = null;
    }

    public static <T> LazyCacheValue<T> newFrom(LazyCacheValue<T> lazyCacheValue) {
        return new LazyCacheValue<>(lazyCacheValue.supplier);
    }
}
