package me.zero.jarpwner.transform;

import me.zero.jarpwner.util.jar.IJarFileProvider;

/**
 * @author Brady
 * @since 4/4/2019
 */
public interface ITransformerContext {

    /**
     * @return A {@link IJarFileProvider} representing the input jar file
     */
    IJarFileProvider getSource();
}
