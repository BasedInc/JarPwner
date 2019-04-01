package me.zero.jarpwner.transform;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author Brady
 * @since 4/1/2019
 */
public interface ITransformerProvider<T extends ITransformer> {

    T provide();

    TransformerMeta getMeta();

    Class<? extends T> getTransformerClass();

    static <T extends ITransformer> ITransformerProvider<T> create(Class<T> transformerClass, Supplier<T> supplier) {
        TransformerMeta meta = transformerClass.getAnnotation(TransformerMeta.class);
        Objects.requireNonNull(meta, "Transformer must have an @TransformerMeta annotation");

        return new ITransformerProvider<>() {

            @Override
            public T provide() {
                return supplier.get();
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
