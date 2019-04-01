package me.zero.jarpwner.plugin;

import me.zero.jarpwner.transform.ITransformerProvider;

import java.util.List;

/**
 * @author Brady
 * @since 4/1/2019
 */
public interface IPlugin {

    String getName();

    String getId();

    String getDescription();

    List<ITransformerProvider> getTransformers();
}
