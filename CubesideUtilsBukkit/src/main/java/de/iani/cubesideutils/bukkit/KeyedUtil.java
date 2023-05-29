package de.iani.cubesideutils.bukkit;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Art;
import org.bukkit.Fluid;
import org.bukkit.GameEvent;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Villager;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffectType;

public class KeyedUtil {
    private KeyedUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static final Map<NamespacedKey, Keyed> internalRegistry;
    public static final Map<NamespacedKey, Keyed> registry;

    static {
        internalRegistry = new HashMap<>();
        registry = Collections.unmodifiableMap(internalRegistry);

        // commented out = no values-method exists, might still want to include?
        addToRegistry(Art.values());
        addToRegistry(Attribute.values());
        // addToCache(BaseTag.values());
        addToRegistry(Biome.values());
        addToRegistry(Cat.Type.values());
        // addToCache(CookingRecipe.values());
        addToRegistry(Enchantment.values());
        addToRegistry(EntityType.values());
        addToRegistry(Fluid.values());
        addToRegistry(Frog.Variant.values());
        addToRegistry(GameEvent.values());
        addToRegistry(LootTables.values());
        addToRegistry(Material.values());
        addToRegistry(MemoryKey.values());
        addToRegistry(MusicInstrument.values());
        addToRegistry(PotionEffectType.values());
        // addToCache(PotionMix.values());
        addToRegistry(Sound.values());
        addToRegistry(Statistic.values());
        // addToCache(Structure.values());
        // addToCache(StructureType.values());
        addToRegistry(Villager.Profession.values());
        addToRegistry(Villager.Type.values());
    }

    private static <T extends Keyed> void addToRegistry(T[] values) {
        for (T t : values) {
            internalRegistry.put(t.getKey(), t);
        }
    }

    private static <T extends Keyed> void addToRegistry(Collection<T> values) {
        for (T t : values) {
            internalRegistry.put(t.getKey(), t);
        }
    }

    public static boolean registerElement(Keyed k) {
        return internalRegistry.putIfAbsent(k.getKey(), k) == null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Keyed> T getFromRegistry(NamespacedKey k) {
        return (T) internalRegistry.get(k);
    }
}
