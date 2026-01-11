package de.iani.cubesideutils.linkextractor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {
    private final static Pattern URL_PATTERN = Pattern.compile("(?<url>(?<protocol>https?\\:\\/\\/)?(?<domain>(?:[a-z0-9](?:[a-z0-9\\-]*[a-z0-9])?\\.)+[a-z]{2,63})(?<path>[\\/\\#\\?](?:[^\\s\"'<>)]*[^\\s\"'<>),.])?)?)(?=[\\s\"'<>),.]|$)", Pattern.CASE_INSENSITIVE);

    public static List<Segment> extractLinks(String s) {
        List<Segment> result = new ArrayList<>();

        Matcher matcher = URL_PATTERN.matcher(s);
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.add(new PlainSegment(s.substring(lastEnd, matcher.start())));
            }
            result.add(new LinkSegment(matcher.group("url"), matcher.group("domain")));
            lastEnd = matcher.end();
        }
        if (lastEnd < s.length()) {
            result.add(new PlainSegment(s.substring(lastEnd)));
        }

        return result;
    }
}
