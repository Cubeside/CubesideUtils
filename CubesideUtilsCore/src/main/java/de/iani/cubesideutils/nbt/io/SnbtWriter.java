package de.iani.cubesideutils.nbt.io;

import de.iani.cubesideutils.nbt.BaseTag;
import de.iani.cubesideutils.nbt.ByteArrayTag;
import de.iani.cubesideutils.nbt.ByteTag;
import de.iani.cubesideutils.nbt.CompoundTag;
import de.iani.cubesideutils.nbt.DoubleTag;
import de.iani.cubesideutils.nbt.FloatTag;
import de.iani.cubesideutils.nbt.IntArrayTag;
import de.iani.cubesideutils.nbt.IntTag;
import de.iani.cubesideutils.nbt.ListTag;
import de.iani.cubesideutils.nbt.LongArrayTag;
import de.iani.cubesideutils.nbt.LongTag;
import de.iani.cubesideutils.nbt.ShortTag;
import de.iani.cubesideutils.nbt.StringTag;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class SnbtWriter extends FilterWriter {
    private static final Pattern NON_QUOTE_PATTERN = Pattern.compile("[a-zA-Z_.+\\-]+");

    public SnbtWriter(Writer out) {
        super(out);
    }

    public void writeNbt(CompoundTag nbt) throws IOException {
        writeInternal(nbt);
    }

    private void writeInternal(BaseTag<?> tag) throws IOException {
        switch (tag.getType()) {
            case BYTE -> append(Byte.toString(((ByteTag) tag).getData())).write('b');
            case SHORT -> append(Short.toString(((ShortTag) tag).getData())).write('s');
            case INT -> write(Integer.toString(((IntTag) tag).getData()));
            case LONG -> append(Long.toString(((LongTag) tag).getData())).write('L');
            case FLOAT -> append(Float.toString(((FloatTag) tag).getData())).write('f');
            case DOUBLE -> append(Double.toString(((DoubleTag) tag).getData())).write('d');
            case BYTE_ARRAY -> {
                byte[] data = ((ByteArrayTag) tag).getData();
                write("[B;");
                for (int i = 0; i < data.length; i++) {
                    if (i > 0) {
                        write(',');
                    }
                    write(Byte.toString(data[i]));
                    write('b');
                }
                write(']');
            }
            case STRING -> writeEscapedString(((StringTag) tag).getData());
            case LIST -> {
                ListTag<?> list = (ListTag<?>) tag;
                write('[');
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        write(',');
                    }
                    writeInternal(list.get(i));
                }
                write(']');
            }
            case COMPOUND -> {
                write('{');
                boolean first = true;
                CompoundTag compound = (CompoundTag) tag;
                for (Entry<String, BaseTag<?>> entry : compound.entrySet()) {
                    if (!first) {
                        append(',');
                    }
                    first = false;
                    writeEscapedString(entry.getKey());
                    write(':');
                    writeInternal(entry.getValue());
                    first = false;
                }
                write('}');
            }
            case INT_ARRAY -> {
                int[] data = ((IntArrayTag) tag).getData();
                write("[I;");
                for (int i = 0; i < data.length; i++) {
                    if (i > 0) {
                        write(',');
                    }
                    write(Integer.toString(data[i]));
                }
                write(']');

            }
            case LONG_ARRAY -> {
                long[] data = ((LongArrayTag) tag).getData();
                write("[L;");
                for (int i = 0; i < data.length; i++) {
                    if (i > 0) {
                        write(',');
                    }
                    write(Long.toString(data[i]));
                    write('l');
                }
                write(']');
            }
            default -> throw new IllegalArgumentException("unknown tag type: " + tag.getType());
        }
    }

    private void writeEscapedString(String string) throws IOException {
        if (NON_QUOTE_PATTERN.matcher(string).matches() && !string.equalsIgnoreCase("true") && !string.equalsIgnoreCase("false")) {
            write(string);
        } else {
            write('"');
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                if (c == '\\' || c == '"') {
                    write('\\');
                }
                write(c);
            }
            write('"');
        }
    }
}
