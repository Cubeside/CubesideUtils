package de.iani.cubesideutils;

import java.util.HashMap;
import net.kyori.adventure.text.serializer.legacy.CharacterAndFormat;

public class LegacyColorUtil {
    private LegacyColorUtil() {
        throw new UnsupportedOperationException();
    }

    public static final char COLOR_CHAR = '§';
    private final static HashMap<Character, CharacterAndFormat> byCharacter = new HashMap<>();
    static {
        for (CharacterAndFormat e : CharacterAndFormat.defaults()) {
            byCharacter.put(e.character(), e);
        }
    }

    public static CharacterAndFormat getCharacterAndFormatForCharacter(char c) {
        return byCharacter.get(c);
    }

}
