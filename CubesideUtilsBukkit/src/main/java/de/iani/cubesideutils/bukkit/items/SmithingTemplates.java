package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.plugin.CubesideUtils;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class SmithingTemplates {

    private SmithingTemplates() {
        throw new UnsupportedOperationException("No instances for you, Sir!");
        // prevent instances
    }

    private static final Map<TrimPattern, Material> PATTERN_TEMPLATES;

    static {
        Map<TrimPattern, Material> patternTemplates = new LinkedHashMap<>();

        Class<TrimPattern> patternClass = TrimPattern.class;
        for (Material mat : Material.values()) {
            if (!mat.name().endsWith("_SMITHING_TEMPLATE")) {
                continue;
            }
            if (mat == Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
                continue;
            }
            try {
                String patternName = mat.name().substring(0, mat.name().length() - "_ARMOR_TRIM_SMITHING_TEMPLATE".length());
                Field constantField = patternClass.getField(patternName);
                patternTemplates.put((TrimPattern) constantField.get(null), mat);
            } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
                CubesideUtils.getInstance().getLogger().log(Level.SEVERE, "Exception trying to map TrimPatterns to Materials, for Material " + mat + ".", e);
            }
        }

        PATTERN_TEMPLATES = Collections.unmodifiableMap(patternTemplates);
    }

    public static Material getTemplateMaterial(TrimPattern pattern) {
        return PATTERN_TEMPLATES.get(pattern);
    }

}
