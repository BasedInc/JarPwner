package me.zero.jarpwner.asm.provider;

import java.util.Map;

/**
 * A type of Provider that is capable of providing all of the objects that it has acquired.
 * There is a guarentee that all of the objects provided by this type of provider will always be
 * no more or no less.
 *
 * @author Brady
 * @since 4/2/2019
 */
public interface IAcquiredProvider<T> extends IProvider<T> {

    Map<String, T> getAll();

    @Override
    default T forName(String path) {
        return getAll().get(path);
    }
}
