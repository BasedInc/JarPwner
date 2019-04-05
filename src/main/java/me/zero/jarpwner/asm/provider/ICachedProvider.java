package me.zero.jarpwner.asm.provider;

/**
 * A type of provider that contains a cache of the paths to whatever the key type is.
 * The cache may be cleared with {@link #clear()}
 *
 * @author Brady
 * @since 4/2/2019
 */
public interface ICachedProvider<T> extends IProvider<T> {

    void clear();
}
