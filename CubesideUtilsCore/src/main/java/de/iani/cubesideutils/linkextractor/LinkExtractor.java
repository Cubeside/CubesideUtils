package de.iani.cubesideutils.linkextractor;

import java.util.ArrayList;
import java.util.List;

public class LinkExtractor {
    public static List<Segment> extractLinks(String s) {
        List<Segment> result = new ArrayList<>();
        int pos = 0;
        int addedTo = 0;
        while (true) {
            int dot = s.indexOf('.', pos);
            if (dot < 0) {
                break;
            }
            int start = searchBackwards(s, dot, pos);
            if (start >= 0) {
                int end = searchForward(s, dot);
                if (end >= 0) {
                    // found a link
                    if (start > pos) {
                        result.add(new PlainSegment(s.substring(addedTo, start)));
                        // System.out.println(s + " -> " + " plain '" + s.substring(addedTo, start) + "'");
                    }
                    result.add(new LinkSegment(s.substring(start, end + 1)));
                    // System.out.println(s + " -> " + " link '" + s.substring(start, end + 1) + "'");
                    pos = end + 1;
                    addedTo = pos;
                    continue;
                }
            }
            pos = dot + 1;
        }
        if (addedTo < s.length()) {
            result.add(new PlainSegment(s.substring(addedTo, s.length())));
            // System.out.println(s + " -> " + " plain '" + s.substring(addedTo, s.length()) + "'");
        }
        return result;
    }

    private static int searchForward(String s, int dot) {
        int len = s.length();
        // tld must be at least 2 characters long, so "+ 2"
        if (dot + 2 >= len || !isAlphaNumAscii(s.charAt(dot + 1))) {
            return -1; // need at least one alphanumeric
        }
        int lastSafePos = dot + 1;
        boolean lastWasDot = false;
        for (int pos = dot + 2; pos < len; pos++) {
            char c = s.charAt(pos);
            if (isAlphaNumAscii(c)) {
                lastSafePos = pos;
                lastWasDot = false;
            } else if (lastWasDot) {
                break; // after a dot we need alphanumeric
            } else if (c == '.') {
                if (lastSafePos < pos - 1) {
                    break; // before a dot may be no dash
                }
                lastWasDot = true;
            } else if (isAlphaNumDashAscii(c)) {
                lastWasDot = false;
            } else {
                break;
            }
        }
        // if we have a / or ? at the end until next space or end
        if (lastSafePos + 1 < len) {
            char c = s.charAt(lastSafePos + 1);
            if (c == '/' || c == '?') {
                int nextSpace = s.indexOf(' ', lastSafePos + 1);
                if (nextSpace == -1) {
                    return len - 1;
                } else {
                    return nextSpace - 1;
                }
            }
            // System.out.println(lastSafePos + " " + len + " " + c);
        }
        return lastSafePos;
    }

    private static int searchBackwards(String s, int dot, int minPos) {
        // alphanum to the left until reached pos (if empty no valid link)
        if (minPos >= dot || !isAlphaNumAscii(s.charAt(dot - 1))) {
            return -1; // need at least one alphanumeric
        }
        int lastSafePos = dot - 1;
        for (int pos = dot - 2; pos >= minPos; pos--) {
            char c = s.charAt(pos);
            if (isAlphaNumAscii(c)) {
                lastSafePos = pos;
            } else if (!isAlphaNumDashAscii(c)) {
                break;
            }
        }
        // look for a protocol, accept only http:// and https://
        if (lastSafePos - 7 >= minPos && s.regionMatches(true, lastSafePos - 7, "http://", 0, 7)) {
            return lastSafePos - 7;
        }
        if (lastSafePos - 8 >= minPos && s.regionMatches(true, lastSafePos - 8, "https://", 0, 8)) {
            return lastSafePos - 8;
        }
        return lastSafePos;
    }

    private static boolean isAlphabeticAscii(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    private static boolean isDigitAscii(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAlphaNumAscii(char c) {
        return isAlphabeticAscii(c) || isDigitAscii(c);
    }

    private static boolean isAlphaNumDashAscii(char c) {
        return isAlphabeticAscii(c) || isDigitAscii(c) || c == '-';
    }

    public static void main(String[] args) {
        System.out.println(extractLinks("foo blabla. https://www.keks.de"));
        System.out.println();
        System.out.println(extractLinks("foo blabla https://www.keks.de foo blabla http://kuchen.com/abc.de "));
        System.out.println();
        System.out.println(extractLinks("foo blabla https://www.keks.dehttp://kuchen.com/abc.de "));
        System.out.println();
        System.out.println(extractLinks("https://www.a/b "));
        System.out.println();
        System.out.println(extractLinks("www.foo"));
        System.out.println();
        System.out.println(extractLinks("ttps://www.foo"));
    }
}
