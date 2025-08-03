package de.iani.cubesideutils.bukkit;

import de.iani.cubesideutils.RandomUtil;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Keyed;
import org.bukkit.Registry;

public class RegistryUtil {
    private static final ConcurrentHashMap<Registry<?>, List<? extends Keyed>> REGISTRY_ENTRIES = new ConcurrentHashMap<>();

    private RegistryUtil() {
        throw new RuntimeException("No instances allowed");
    }

    public static <T extends Keyed> List<T> getRegistryValues(RegistryKey<T> registryKey) {
        return getRegistryValues(RegistryAccess.registryAccess().getRegistry(registryKey));
    }

    private static <T extends Keyed> List<T> generateSortedRegistryEntryList(Registry<T> registry) {
        ArrayList<T> values = new ArrayList<>();
        registry.forEach(t -> values.add(t));
        values.sort((v1, v2) -> v1.getKey().compareTo(v2.getKey()));
        return values;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Keyed> List<T> getRegistryValues(Registry<T> registry) {
        return (List<T>) REGISTRY_ENTRIES.computeIfAbsent(registry, RegistryUtil::generateSortedRegistryEntryList);
    }

    public static <T extends Keyed> T getRandomRegistryEntry(RegistryKey<T> registryKey) {
        return getRandomRegistryEntry(RegistryAccess.registryAccess().getRegistry(registryKey));
    }

    public static <T extends Keyed> T getRandomRegistryEntry(Registry<T> registry) {
        List<T> valuesList = getRegistryValues(registry);
        return valuesList.isEmpty() ? null : RandomUtil.randomElement(valuesList);
    }

    public static <T extends Keyed> T getNextRegistryEntry(RegistryKey<T> registryKey, T previous) {
        return getNextRegistryEntry(RegistryAccess.registryAccess().getRegistry(registryKey), previous);
    }

    public static <T extends Keyed> T getNextRegistryEntry(Registry<T> registry, T previous) {
        List<T> valuesList = getRegistryValues(registry);
        int size = valuesList.size();
        for (int i = 0; i < size; i++) {
            if (valuesList.get(i).equals(previous)) {
                return valuesList.get((i + 1) % size);
            }
        }
        return valuesList.get(0);
    }
}
