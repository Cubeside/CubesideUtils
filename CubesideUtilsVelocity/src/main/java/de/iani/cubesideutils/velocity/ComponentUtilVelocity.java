package de.iani.cubesideutils.velocity;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ComponentUtilVelocity {

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER;
    static {
        LegacyComponentSerializer ser = null;
        try {
            ser = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).extractUrls()
                    .useUnusualXRepeatedCharacterHexFormat().build();
        } catch (Throwable t) {
            ser = null;
        }
        LEGACY_COMPONENT_SERIALIZER = ser;
    }

    private ComponentUtilVelocity() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static LegacyComponentSerializer getLegacyComponentSerializer() {
        return LEGACY_COMPONENT_SERIALIZER;
    }
}
