package de.iani.cubesideutils;

public class CharacterUtil {
    private CharacterUtil() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    public static boolean isAngloGermanLetter(char c) {
        c = Character.toLowerCase(c);
        return ('a' <= c && c <= 'z') || (c == 'ä' || c == 'ö' || c == 'ü' || c == 'ß');
    }

    public static boolean isVowl(char c) {
        c = Character.toLowerCase(c);
        if (!isAngloGermanLetter(c)) {
            return false;
        }
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'ä' || c == 'ö' || c == 'ü';
    }

    public static boolean isConsonant(char c) {
        if (!isAngloGermanLetter(c)) {
            return false;
        }
        return !isVowl(c);
    }

}
