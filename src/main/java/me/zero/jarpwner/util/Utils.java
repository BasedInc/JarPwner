package me.zero.jarpwner.util;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Brady
 * @since 4/4/2019
 */
public final class Utils {

    private Utils() {}

    public static <T> Optional<T> tryOptional(SupplierWithThrowable<T, ?> supplier) {
        return Optional.ofNullable(tryOrElseNull(supplier));
    }

    public static <T> T tryOrElseNull(SupplierWithThrowable<T, ?> supplier) {
        return tryOrElse(supplier, null);
    }

    public static <T> T tryOrElse(SupplierWithThrowable<T, ?> supplier, T value) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return value;
        }
    }

    public static <T> T tryOrElseGet(SupplierWithThrowable<T, ?> supplier, Supplier<T> value) {
        try {
            return supplier.get();
        } catch (Throwable e) {
            return value.get();
        }
    }
}
