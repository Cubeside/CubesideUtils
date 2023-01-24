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
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NbtInputStream extends DataInputStream {
    public NbtInputStream(InputStream in) {
        super(in);
    }

    public CompoundTag readNbt() throws IOException {
        TagType type = TagType.valueOf(readByte());
        if (type != TagType.COMPOUND) {
            new IOException("Root tag must be a named compound tag");
        }
        CompoundTag root = new CompoundTag();
        readUTF();// unused name
        load(root);
        return root;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void load(BaseTag<?> tag) throws IOException {
        switch (tag.getType()) {
            case BYTE -> ((ByteTag) tag).setData(readByte());
            case SHORT -> ((ShortTag) tag).setData(readShort());
            case INT -> ((IntTag) tag).setData(readInt());
            case LONG -> ((LongTag) tag).setData(readLong());
            case FLOAT -> ((FloatTag) tag).setData(readFloat());
            case DOUBLE -> ((DoubleTag) tag).setData(readDouble());
            case BYTE_ARRAY -> {
                int length = readInt();
                byte[] data = new byte[length];
                readFully(data);
                ((ByteArrayTag) tag).setData(data);
            }
            case STRING -> ((StringTag) tag).setData(readUTF());
            case LIST -> {
                ListTag<?> list = (ListTag<?>) tag;
                list.clear();
                TagType type = TagType.valueOf(readByte());
                list.setElementType(type);
                int size = readInt();
                for (int i = 0; i < size; i++) {
                    BaseTag<?> subtag = type.create();
                    load(subtag);
                    ((ListTag) list).add(subtag);
                }
            }
            case COMPOUND -> {
                CompoundTag compound = (CompoundTag) tag;
                compound.clear();
                while (true) {
                    TagType type = TagType.valueOf(readByte());
                    if (type == TagType.END) {
                        break;
                    }
                    String name = readUTF();
                    BaseTag<?> subtag = type.create();
                    load(subtag);
                    compound.put(name, subtag);
                }
            }
            case INT_ARRAY -> {
                int length = readInt();
                int[] data = new int[length];
                for (int i = 0; i < length; i++) {
                    data[i] = readInt();
                }
                ((IntArrayTag) tag).setData(data);
            }
            case LONG_ARRAY -> {
                int length = readInt();
                long[] data = new long[length];
                for (int i = 0; i < length; i++) {
                    data[i] = readLong();
                }
                ((LongArrayTag) tag).setData(data);
            }
            default -> throw new IllegalArgumentException("unknown tag type: " + tag.getType());
        }
    }
}
