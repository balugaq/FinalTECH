package io.taraxacum.common.pref;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Final_ROOT
 * @param <T>
 */
public class ComplexOptional<T> {
    private T object;

    private Function<String[], T> generateFunction;

    private State state;

    public ComplexOptional(@Nonnull T object) {
        this.object = object;
        this.state = State.SUCCESS;
    }

    public ComplexOptional(@Nonnull Function<String[], T> generateFunction) {
        this.generateFunction = generateFunction;
        this.state = State.NEED_EXTRA_ARGS;
    }

    public ComplexOptional() {
        this.state = State.NULL;
    }

    @Nonnull
    public State getState() {
        return this.state;
    }

    @Nullable
    public T get() {
        if (this.state == State.SUCCESS) {
            return this.object;
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    @Nullable
    public T getByArgs(@Nonnull String... args) {
        if (this.state == State.NEED_EXTRA_ARGS) {
            this.object = this.generateFunction.apply(args);
            this.state = State.SUCCESS;
            return this.object;
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    public boolean ok() {
        return this.state == State.SUCCESS || this.state == State.NEED_EXTRA_ARGS;
    }

    @Nullable
    public T getOrByArgs(@Nonnull Supplier<String[]> supplier) {
        if (this.state == State.SUCCESS) {
            return this.object;
        } else if (this.state == State.NEED_EXTRA_ARGS) {
            return this.getByArgs(supplier.get());
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    public enum State {
        // It means the return value is not existed.
        NULL,
        // It may return null.
        SUCCESS,
        // We need extra args to get the return value.
        NEED_EXTRA_ARGS
    }
}
