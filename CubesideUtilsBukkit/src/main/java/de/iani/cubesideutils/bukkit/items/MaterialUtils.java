package de.iani.cubesideutils.bukkit.items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Material;

public class MaterialUtils {
    private static final Map<Material, Set<Material>> PLACED_AS_BLOCK = new HashMap<>();

    private MaterialUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!"); // really prevents instances
    }

    static {
        for (Material m : Material.values()) {
            if (!m.isLegacy() && m.isBlock()) {
                Material placement = m.createBlockData().getPlacementMaterial();
                if (placement != null) {
                    PLACED_AS_BLOCK.computeIfAbsent(placement, placement2 -> new HashSet<>()).add(m);
                }
            }
        }
        // add missing items
        PLACED_AS_BLOCK.computeIfAbsent(Material.BAMBOO, placement2 -> new HashSet<>()).add(Material.BAMBOO_SAPLING);

        for (Entry<Material, Set<Material>> e : PLACED_AS_BLOCK.entrySet()) {
            e.setValue(Set.of(e.getValue().toArray(Material[]::new)));
        }
    }

    public static Set<Material> getPlacedMaterials(Material item) {
        return PLACED_AS_BLOCK.get(item);
    }
}
