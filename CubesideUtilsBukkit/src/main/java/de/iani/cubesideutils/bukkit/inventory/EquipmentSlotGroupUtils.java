package de.iani.cubesideutils.bukkit.inventory;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.inventory.EquipmentSlotGroup;

public class EquipmentSlotGroupUtils {
    private final static List<EquipmentSlotGroup> GROUPS;

    static {
        ArrayList<EquipmentSlotGroup> groups = new ArrayList<>();
        for (Field f : EquipmentSlotGroup.class.getDeclaredFields()) {
            if (f.getType() == EquipmentSlotGroup.class && f.accessFlags().contains(AccessFlag.PUBLIC) && f.accessFlags().contains(AccessFlag.STATIC)) {
                try {
                    EquipmentSlotGroup group = (EquipmentSlotGroup) f.get(null);
                    groups.add(group);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        GROUPS = Collections.unmodifiableList(groups);
    }

    public static List<EquipmentSlotGroup> getAllGroups() {
        return GROUPS;
    }
}
