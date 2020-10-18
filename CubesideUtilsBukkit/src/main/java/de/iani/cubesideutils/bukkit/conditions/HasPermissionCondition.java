package de.iani.cubesideutils.bukkit.conditions;

import de.iani.cubesideutils.conditions.Condition;
import java.util.Objects;
import org.bukkit.permissions.Permissible;

public class HasPermissionCondition implements Condition<Permissible> {

    public static final String SERIALIZATION_TYPE = "HasPermissionCondition";

    public static HasPermissionCondition deserialize(String serialized) {
        return new HasPermissionCondition(Condition.unescape(serialized));
    }

    private String permission;

    public HasPermissionCondition(String permission) {
        this.permission = Objects.requireNonNull(permission);
    }

    @Override
    public boolean test(Permissible t) {
        return t.hasPermission(this.permission);
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        return Condition.escape(permission);
    }

}
