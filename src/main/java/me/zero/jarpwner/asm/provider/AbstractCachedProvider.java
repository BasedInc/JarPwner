package me.zero.jarpwner.asm.provider;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brady
 * @since 4/2/2019
 */
public abstract class AbstractCachedProvider<T> implements ICachedProvider<T> {

    private Map<String, T> cache;

    public AbstractCachedProvider() {
        this.cache = new HashMap<>();
    }

    @Override
    public final T forName(String path) {
        return cache.computeIfAbsent(path, this::forName0);
    }

    @Override
    public final void clear() {
        this.cache.clear();
    }

    protected abstract T forName0(String path);
}
