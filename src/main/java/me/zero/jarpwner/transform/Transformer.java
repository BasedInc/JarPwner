package me.zero.jarpwner.transform;

/**
 * Implementation of {@link ITransformer} that has a constructor which takes in a single
 * {@link ITransformerContext} argument, which may then be referenced through {@code protected}
 * access by implementors in order to access information which the context provides.
 *
 * @see ITransformerContext
 *
 * @author Brady
 * @since 4/4/2019
 */
public abstract class Transformer implements ITransformer {

    /**
     * The transformer context provided by the transform sequence
     */
    protected final ITransformerContext context;

    public Transformer(ITransformerContext context) {
        this.context = context;
    }
}
