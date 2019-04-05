package me.zero.jarpwner.asm.provider;

import java.util.function.Function;

/**
 * A generic type that is a {@code String => T} function.
 *
 * @author Brady
 * @since 4/2/2019
 */
public interface IProvider<T> {

    /**
     * Provides a lookup for Object of type {@link T} for the given path.
     *
     * @param path The object path
     * @return The found object, {@code null} if none is found.
     */
    T forName(String path);

    default IProvider<T> withFallback(IProvider<T> other) {
        return path -> {
            var node = this.forName(path);
            return node == null ? other.forName(path) : node;
        };
    }

    /**
     * Returns the function representation of this {@link IProvider<T>}. It is
     * expressed as a method reference for {@link #forName(String)}.
     *
     * @return The function representation of this provider
     */
    default Function<String, T> function() {
        return this::forName;
    }
}
