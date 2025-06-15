package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.RandomUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Keyed;
import org.bukkit.Registry;

public class RegistryUtil {
    private static final HashMap<Registry<?>, List<? extends Keyed>> REGISTRY_ENTRIES = new HashMap<>();

    private RegistryUtil() {
        throw new RuntimeException("No instances allowed");
    }

    public static <T extends Keyed> List<T> getRegistryValues(RegistryKey<T> registryKey) {
        return getRegistryValues(RegistryAccess.registryAccess().getRegistry(registryKey));
    }

    public static <T extends Keyed> List<T> getRegistryValues(Registry<T> registry) {
        @SuppressWarnings("unchecked")
        List<T> values = (List<T>) REGISTRY_ENTRIES.get(registry);
        if (values == null) {
            synchronized (REGISTRY_ENTRIES) {
                ArrayList<T> valuesFinal = new ArrayList<>();
                values = valuesFinal;
                registry.forEach(t -> valuesFinal.add(t));
                REGISTRY_ENTRIES.put(registry, Collections.unmodifiableList(values));
            }
        }
        return values;
    }

    public static <T extends Keyed> T getRandomRegistryEntry(RegistryKey<T> registryKey) {
        return getRandomRegistryEntry(RegistryAccess.registryAccess().getRegistry(registryKey));
    }

    public static <T extends Keyed> T getRandomRegistryEntry(Registry<T> registry) {
        List<T> valuesList = getRegistryValues(registry);
        return valuesList.isEmpty() ? null : RandomUtil.randomElement(valuesList);
    }
}
