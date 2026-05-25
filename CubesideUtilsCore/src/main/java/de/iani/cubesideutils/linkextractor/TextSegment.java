package de.iani.cubesideutils.linkextractor;

public interface TextSegment {
    public record PlainSegment(String value) implements TextSegment {}

    public record LinkSegment(String value, String domain) implements TextSegment {}
}
