package me.zero.jarpwner.util;

/**
 * @author Brady
 * @since 4/4/2019
 */
@FunctionalInterface
public interface SupplierWithThrowable<T, E extends Throwable> {

    T get() throws E;
}
