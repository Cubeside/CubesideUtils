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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map.Entry;

public class NbtOutputStream extends DataOutputStream {
    public NbtOutputStream(OutputStream out) {
        super(out);
    }

    public void writeNbt(CompoundTag nbt) throws IOException {
        writeByte(nbt.getType().ordinal());
        writeUTF(""); // empty name
        writeInternal(nbt);
    }

    private void writeInternal(BaseTag<?> tag) throws IOException {
        switch (tag.getType()) {
            case BYTE -> writeByte(((ByteTag) tag).getData());
            case SHORT -> writeShort(((ShortTag) tag).getData());
            case INT -> writeInt(((IntTag) tag).getData());
            case LONG -> writeLong(((LongTag) tag).getData());
            case FLOAT -> writeFloat(((FloatTag) tag).getData());
            case DOUBLE -> writeDouble(((DoubleTag) tag).getData());
            case BYTE_ARRAY -> {
                byte[] data = ((ByteArrayTag) tag).getData();
                writeInt(data.length);
                write(data);
            }
            case STRING -> writeUTF(((StringTag) tag).getData());
            case LIST -> {
                ListTag<?> list = (ListTag<?>) tag;
                writeByte(list.getElementType().ordinal());
                int size = list.size();
                writeInt(size);
                for (int i = 0; i < size; i++) {
                    writeInternal(list.get(i));
                }
            }
            case COMPOUND -> {
                CompoundTag compound = (CompoundTag) tag;
                for (Entry<String, BaseTag<?>> entry : compound.entrySet()) {
                    writeByte(entry.getValue().getType().ordinal());
                    writeUTF(entry.getKey());
                    writeInternal(entry.getValue());
                }
                writeByte(TagType.END.ordinal());
            }
            case INT_ARRAY -> {
                int[] data = ((IntArrayTag) tag).getData();
                int length = data.length;
                writeInt(length);
                for (int i = 0; i < length; i++) {
                    writeInt(data[i]);
                }
            }
            case LONG_ARRAY -> {
                long[] data = ((LongArrayTag) tag).getData();
                int length = data.length;
                writeInt(length);
                for (int i = 0; i < length; i++) {
                    writeLong(data[i]);
                }
            }
            default -> throw new IllegalArgumentException("unknown tag type: " + tag.getType());
        }
    }
}
