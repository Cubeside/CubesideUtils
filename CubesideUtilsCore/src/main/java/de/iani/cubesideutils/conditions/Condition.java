package de.iani.cubesideutils.conditions;

import de.iani.cubesideutils.serialization.StringSerializable;
import java.util.function.Predicate;

public interface Condition<T> extends Predicate<T>, StringSerializable {

    public static String escape(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            switch (curr) {
                case '(':
                    builder.append("()");
                    continue;
                case ')':
                    builder.append(")(");
                    continue;
                default:
                    builder.append(curr);
            }
        }
        return builder.toString();
    }

    public static String unescape(String text) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char curr = text.charAt(i);
            if (curr == '(' && text.charAt(i + 1) == ')') {
                i++;
            } else if (curr == ')' && text.charAt(i + 1) == '(') {
                i++;
            }
            builder.append(curr);
        }
        return builder.toString();
    }

}
