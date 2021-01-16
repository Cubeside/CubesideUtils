package de.iani.cubesideutils;

import java.io.IOException;
import java.io.InputStream;
import net.md_5.bungee.api.chat.BaseComponent;

public class FontUtil {
    public static final int BOOK_LINE_WIDTH = 110;// mojang: 116

    private static byte[] charWidth = new byte[65536];

    static {
        try (InputStream is = FontUtil.class.getClassLoader().getResourceAsStream("char_sizes.bin")) {
            is.read(charWidth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean fitsSingleBookPage(BaseComponent... text) {
        if (text == null) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        for (BaseComponent c : text) {
            sb.append(c.toLegacyText());
        }
        return fitsSingleBookPage(sb.toString());
    }

    public static boolean fitsSingleBookPage(String string) {
        return getBookLines(string) <= 14;
    }

    public static int getBookLines(String string) {
        return splitStringForLineWidth(trimTrailingNewlines(string), BOOK_LINE_WIDTH).length;
    }

    private static String trimTrailingNewlines(String string) {
        while (string.endsWith("\n")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    public static String[] splitStringForLineWidth(String string, int availableWidth) {
        return wrapString(string, availableWidth).split("\n");
    }

    private static String wrapString(String string, int availableWidth) {
        int charactersInLine = getFittingCharacters(string, availableWidth);
        if (string.length() <= charactersInLine) {
            return string;
        } else {
            String thisLine = string.substring(0, charactersInLine);
            char firstRemainingCharacter = string.charAt(charactersInLine);
            boolean removeFirstRemainingChar = firstRemainingCharacter == ' ' || firstRemainingCharacter == '\n';
            String remaining = extractFormatingFromString(thisLine) + string.substring(charactersInLine + (removeFirstRemainingChar ? 1 : 0));
            return thisLine + "\n" + wrapString(remaining, availableWidth);
        }
    }

    private static int getFittingCharacters(String string, int availableWidth) {
        int stringLength = string.length();
        int totalWidth = 0;
        int pos = 0;
        int lastSpace = -1;
        boolean bold = false;
        for (; pos < stringLength; ++pos) {
            char c = string.charAt(pos);

            switch (c) {
                case '\n': // this will always terminate the line
                    lastSpace = pos;
                    break;
                case '§': // color codes do not take any space but might switch bold/not bold
                    if (pos < stringLength - 1) {
                        char colorChar = string.charAt(pos + 1);
                        if (isFormatCharacter(colorChar)) {
                            ++pos;
                            if (colorChar != 'l' && colorChar != 'L') {
                                if (colorChar == 'r' || colorChar == 'R' || isFormatColor(colorChar)) {
                                    bold = false;
                                }
                            } else {
                                bold = true;
                            }
                        }
                    }
                    break;
                case ' ': // possible position for a line break
                    lastSpace = pos;
                    //$FALL-THROUGH$
                default:
                    totalWidth += getCharWidth(c) + (bold ? 1 : 0);
            }

            if (c == '\n' || totalWidth > availableWidth) {
                break;
            }
        }
        return pos != stringLength && lastSpace != -1 && lastSpace < pos ? lastSpace : pos;
    }

    private static int getCharWidth(char c) {
        return charWidth[c];
    }

    private static boolean isFormatColor(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    private static boolean isFormatSpecialCharacter(char c) {
        return c >= 'k' && c <= 'o' || c >= 'K' && c <= 'O' || c == 'r' || c == 'R' || c == 'x' || c == 'X';
    }

    private static boolean isFormatCharacter(char c) {
        return isFormatColor(c) || isFormatSpecialCharacter(c);
    }

    private static String extractFormatingFromString(String string) {
        String formatingString = "";
        int formatingInitialCharacter = -1;
        int stringLength = string.length();

        while ((formatingInitialCharacter = string.indexOf('§', formatingInitialCharacter + 1)) != -1) {
            if (formatingInitialCharacter < stringLength - 1) {
                char colorCharacter = string.charAt(formatingInitialCharacter + 1);

                if (isFormatColor(colorCharacter)) {
                    formatingString = "§" + colorCharacter;
                } else if (isFormatSpecialCharacter(colorCharacter)) {
                    formatingString = formatingString + "§" + colorCharacter;
                }
            }
        }

        return formatingString;
    }
}
