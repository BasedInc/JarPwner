package me.zero.jarpwner.transform;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Brady
 * @since 4/1/2019
 */
public interface ITransformerProvider<T extends ITransformer> {

    /**
     * Provides an instance of the transformer expressed by this provider
     *
     * @param context The transformer context
     * @return A new transformer instance
     */
    T provide(ITransformerContext context);

    /**
     * @return The metadata of the transformer
     */
    TransformerMeta getMeta();

    /**
     * @return The type of transformer that is provided
     */
    Class<? extends T> getTransformerClass();

    static <T extends ITransformer> ITransformerProvider<T> create(Class<T> transformerClass, Function<ITransformerContext, T> supplier) {
        TransformerMeta meta = transformerClass.getAnnotation(TransformerMeta.class);
        Objects.requireNonNull(meta, "Transformer must have an @TransformerMeta annotation");

        return new ITransformerProvider<>() {

            @Override
            public T provide(ITransformerContext context) {
                return supplier.apply(context);
            }

            @Override
            public TransformerMeta getMeta() {
                return meta;
            }

            @Override
            public Class<? extends T> getTransformerClass() {
                return transformerClass;
            }
        };
    }
}
