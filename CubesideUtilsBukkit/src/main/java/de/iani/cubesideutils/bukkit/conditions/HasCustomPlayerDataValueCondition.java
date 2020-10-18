package de.iani.cubesideutils.bukkit.conditions;

import de.iani.cubesideutils.bukkit.plugin.api.UtilsApiBukkit;
import de.iani.cubesideutils.conditions.Condition;
import de.iani.cubesideutils.plugin.api.PlayerData;
import java.util.Objects;
import org.bukkit.OfflinePlayer;

public class HasCustomPlayerDataValueCondition implements Condition<OfflinePlayer> {

    public static String SERIALIZATION_TYPE = "HasCustomPlayerDataValueCondition";
    private static final char SEPERATION_CHAR = '#';

    public static HasCustomPlayerDataValueCondition deserialize(String serialized) {
        int parenthesis = 0;
        int i = 0;

        loop: for (; i < serialized.length(); i++) {
            switch (serialized.charAt(i)) {
                case '(':
                    parenthesis++;
                    break;
                case ')':
                    parenthesis--;
                    break;
                case SEPERATION_CHAR:
                    if (parenthesis == 0) {
                        break loop;
                    }
            }
        }

        if (i >= serialized.length()) {
            throw new IllegalArgumentException("invalid syntax");
        }

        String key = Condition.unescape(serialized.substring(1, i - 1));
        String value = Condition.unescape(serialized.substring(i + 1));
        return new HasCustomPlayerDataValueCondition(key, value);
    }

    private String key;
    private String value;

    public HasCustomPlayerDataValueCondition(String key, String value) {
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean test(OfflinePlayer t) {
        PlayerData data = UtilsApiBukkit.getInstance().getPlayerData(t);
        if (data == null) {
            return false;
        }
        return Objects.equals(data.getCustomData(this.key), this.value);
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        return '(' + Condition.escape(this.key) + ')' + SEPERATION_CHAR + Condition.escape(this.value);
    }

}
