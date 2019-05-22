package me.zero.jarpwner.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @author Brady
 * @since 4/1/2019
 */
public final class PluginDiscovery {

    private PluginDiscovery() {}

    private static List<IPlugin> plugins;

    public static synchronized List<IPlugin> getPlugins() {
        if (plugins == null) {
            plugins = new ArrayList<>();
            var loader = ServiceLoader.load(IPlugin.class);
            loader.forEach(plugins::add);
        }

        return plugins;
    }

    public static Optional<IPlugin> getPlugin(String id) {
        return getPlugins().stream().filter(plugin -> plugin.getId().equals(id)).findFirst();
    }
}
