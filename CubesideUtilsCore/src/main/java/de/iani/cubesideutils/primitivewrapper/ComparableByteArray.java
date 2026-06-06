package de.iani.cubesideutils.primitivewrapper;

import java.util.Arrays;

public record ComparableByteArray(byte[] content) {
    @Override
    public final boolean equals(Object other) {
        if (other instanceof ComparableByteArray otherArray) {
            return Arrays.equals(content, otherArray.content);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(content);
    }
}
