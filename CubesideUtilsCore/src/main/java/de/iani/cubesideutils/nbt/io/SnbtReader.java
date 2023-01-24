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
import de.iani.cubesideutils.nbt.TagType;
import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class SnbtReader extends FilterReader {
    private int lookAhead = Integer.MIN_VALUE;
    private int lookAhead2 = Integer.MIN_VALUE;
    private boolean lastStringEscaped = false;

    public SnbtReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (lookAhead == Integer.MIN_VALUE) {
            return super.read();
        }
        int c = lookAhead;
        lookAhead = lookAhead2;
        lookAhead2 = Integer.MIN_VALUE;
        return c;
    }

    private void pushLookAhead(char c) {
        if (lookAhead == Integer.MIN_VALUE) {
            lookAhead = c;
        } else if (lookAhead2 == Integer.MIN_VALUE) {
            lookAhead2 = lookAhead;
            lookAhead = c;
        } else {
            throw new IllegalStateException("lookahead overflow");
        }
    }

    public int readSkipSpace() throws IOException {
        int c = read();
        while (c == ' ') {
            c = read();
        }
        return c;
    }

    public CompoundTag readNbt() throws IOException {
        char c = (char) readSkipSpace();
        if (c != '{') {
            throw new IOException("nbt must start with a compound");
        }
        CompoundTag tag = new CompoundTag();
        readInternal(tag);
        return tag;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void readInternal(BaseTag<?> tag) throws IOException {
        switch (tag.getType()) {
            case BYTE_ARRAY -> {
                char c = (char) readSkipSpace();
                if (c == ']') {
                    return;
                }
                pushLookAhead(c);
                ArrayList<Byte> tempList = new ArrayList<>();
                while (true) {
                    BaseTag<?> subtag = readAnyValue();
                    if (subtag.getType() == TagType.BYTE) {
                        tempList.add(((ByteTag) subtag).getData());
                    } else {
                        throw new IOException("unexpected " + subtag.getType() + "; expected byte");
                    }
                    c = (char) readSkipSpace();
                    if (c == ']') {
                        byte[] temp = new byte[tempList.size()];
                        for (int i = 0; i < temp.length; i++) {
                            temp[i] = tempList.get(i);
                        }
                        ((ByteArrayTag) tag).setData(temp);
                        return;
                    }
                    if (c != ',') {
                        throw new IOException(", expected");
                    }
                }
            }
            case LIST -> {
                ListTag<?> list = (ListTag<?>) tag;
                list.clear();
                char c = (char) readSkipSpace();
                if (c == ']') {
                    return;
                }
                pushLookAhead(c);
                while (true) {
                    BaseTag<?> subtag = readAnyValue();
                    ((ListTag) list).add(subtag);

                    c = (char) readSkipSpace();
                    if (c == ']') {
                        return;
                    }
                    if (c != ',') {
                        throw new IOException(", expected");
                    }
                }
            }
            case COMPOUND -> {
                CompoundTag compound = (CompoundTag) tag;
                compound.clear();
                char c = (char) readSkipSpace();
                if (c == '}') {
                    return;
                }
                pushLookAhead(c);
                while (true) {
                    String name = readMaybeEscapedString();
                    c = (char) readSkipSpace();
                    if (c != ':') {
                        throw new IOException(": expected");
                    }
                    BaseTag<?> subtag = readAnyValue();
                    compound.put(name, subtag);
                    c = (char) readSkipSpace();
                    if (c == '}') {
                        return;
                    }
                    if (c != ',') {
                        throw new IOException(", expected");
                    }
                }
            }
            case INT_ARRAY -> {
                char c = (char) readSkipSpace();
                if (c == ']') {
                    return;
                }
                pushLookAhead(c);
                ArrayList<Integer> tempList = new ArrayList<>();
                while (true) {
                    BaseTag<?> subtag = readAnyValue();
                    if (subtag.getType() == TagType.INT) {
                        tempList.add(((IntTag) subtag).getData());
                    } else if (subtag.getType() == TagType.SHORT) {
                        tempList.add((int) ((ShortTag) subtag).getData());
                    } else if (subtag.getType() == TagType.BYTE) {
                        tempList.add((int) ((ByteTag) subtag).getData());
                    } else {
                        throw new IOException("unexpected " + subtag.getType() + "; expected int");
                    }
                    c = (char) readSkipSpace();
                    if (c == ']') {
                        int[] temp = new int[tempList.size()];
                        for (int i = 0; i < temp.length; i++) {
                            temp[i] = tempList.get(i);
                        }
                        ((IntArrayTag) tag).setData(temp);
                        return;
                    }
                    if (c != ',') {
                        throw new IOException(", expected");
                    }
                }
            }
            case LONG_ARRAY -> {
                char c = (char) readSkipSpace();
                if (c == ']') {
                    return;
                }
                pushLookAhead(c);
                ArrayList<Long> tempList = new ArrayList<>();
                while (true) {
                    BaseTag<?> subtag = readAnyValue();
                    if (subtag.getType() == TagType.LONG) {
                        tempList.add(((LongTag) subtag).getData());
                    } else if (subtag.getType() == TagType.INT) {
                        tempList.add((long) ((IntTag) subtag).getData());
                    } else if (subtag.getType() == TagType.SHORT) {
                        tempList.add((long) ((ShortTag) subtag).getData());
                    } else if (subtag.getType() == TagType.BYTE) {
                        tempList.add((long) ((ByteTag) subtag).getData());
                    } else {
                        throw new IOException("unexpected " + subtag.getType() + "; expected long");
                    }
                    c = (char) readSkipSpace();
                    if (c == ']') {
                        long[] temp = new long[tempList.size()];
                        for (int i = 0; i < temp.length; i++) {
                            temp[i] = tempList.get(i);
                        }
                        ((LongArrayTag) tag).setData(temp);
                        return;
                    }
                    if (c != ',') {
                        throw new IOException(", expected");
                    }
                }
            }
            default -> throw new IllegalArgumentException("unknown tag type: " + tag.getType());
        }
    }

    private BaseTag<?> readAnyValue() throws IOException {
        char c = (char) readSkipSpace();
        if (c == '{') {
            CompoundTag compound = new CompoundTag();
            readInternal(compound);
            return compound;
        } else if (c == '[') {
            c = (char) read();
            if (c == 'B' || c == 'I' || c == 'L') {
                char c2 = (char) read();
                if (c2 == ';') {
                    if (c == 'B') {
                        ByteArrayTag array = new ByteArrayTag();
                        readInternal(array);
                        return array;
                    } else if (c == 'I') {
                        IntArrayTag array = new IntArrayTag();
                        readInternal(array);
                        return array;
                    } else if (c == 'L') {
                        LongArrayTag array = new LongArrayTag();
                        readInternal(array);
                        return array;
                    }
                } else {
                    pushLookAhead(c2);
                    pushLookAhead(c);
                }
            } else {
                pushLookAhead(c);
            }
            ListTag<?> list = new ListTag<>();
            readInternal(list);
            return list;
        } else {
            pushLookAhead(c);
            String s = readMaybeEscapedString();
            if (!lastStringEscaped && !s.isEmpty()) {
                // check what it is... this may be something else like a number or boolean
                if (s.equalsIgnoreCase("true")) {
                    return new ByteTag((byte) 1);
                } else if (s.equalsIgnoreCase("false")) {
                    return new ByteTag((byte) 0);
                }
                char lastChar = s.charAt(s.length() - 1);
                if (lastChar == 'b' || lastChar == 'B') {
                    try {
                        byte b = Byte.parseByte(s.substring(0, s.length() - 1));
                        return new ByteTag(b);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (lastChar == 's' || lastChar == 'S') {
                    try {
                        short sh = Short.parseShort(s.substring(0, s.length() - 1));
                        return new ShortTag(sh);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (lastChar == 'l' || lastChar == 'L') {
                    try {
                        long l = Long.parseLong(s.substring(0, s.length() - 1));
                        return new LongTag(l);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (lastChar == 'f' || lastChar == 'F') {
                    try {
                        float f = Float.parseFloat(s.substring(0, s.length() - 1));
                        return new FloatTag(f);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (lastChar == 'd' || lastChar == 'D') {
                    try {
                        double d = Double.parseDouble(s.substring(0, s.length() - 1));
                        return new DoubleTag(d);
                    } catch (NumberFormatException ignored) {
                    }
                } else {
                    if (s.indexOf('.') >= 0) {
                        try {
                            double d = Double.parseDouble(s);
                            return new DoubleTag(d);
                        } catch (NumberFormatException ignored) {
                        }
                    } else {
                        try {
                            int i = Integer.parseInt(s);
                            return new IntTag(i);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
            return new StringTag(s);
        }
    }

    private String readMaybeEscapedString() throws IOException {
        lastStringEscaped = false;
        char c = (char) readSkipSpace();
        if (c == '"' || c == '\'') {
            lastStringEscaped = true;
            char endChar = c;
            StringBuilder sb = new StringBuilder();
            boolean escapeNext = false;
            while (true) {
                c = (char) read();
                if (escapeNext) {
                    sb.append(c);
                    escapeNext = false;
                } else if (c == '\\') {
                    escapeNext = true;
                } else if (c != endChar) {
                    sb.append(c);
                } else {
                    return sb.toString();
                }
            }
        }
        // not escaped, first char is read
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (c != '}' && c != ']' && c != ':' && c != ';' && c != ',' && c != ' ') {
                sb.append(c);
            } else {
                pushLookAhead(c);
                return sb.toString();
            }
            c = (char) read();
        }
    }
}
