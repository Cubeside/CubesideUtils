package de.iani.cubesideutils.nbt.io;

import de.iani.cubesideutils.nbt.CompoundTag;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;

public class SnbtIo {
    public static CompoundTag read(String snbt) throws IOException {
        try (SnbtReader r = new SnbtReader(new StringReader(snbt))) {
            return r.readNbt();
        }
    }

    public static String write(CompoundTag tag) throws IOException {
        try (CharArrayWriter w = new CharArrayWriter(); SnbtWriter snbtw = new SnbtWriter(w)) {
            snbtw.writeNbt(tag);
            return w.toString();
        }
    }
}
