package de.iani.cubesideutils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

/**
 * A data input stream that contains some additional methods to read data in minecraft format.
 */
public class MinecraftDataInputStream extends DataInputStream {
    public MinecraftDataInputStream(InputStream in) {
        super(in);
    }

    public String readString() throws IOException {
        int bytes = readVarInt();
        byte[] stringBytes = new byte[bytes];
        readFully(stringBytes);
        return new String(stringBytes, StandardCharsets.UTF_8);
    }

    public int readVarInt() throws IOException {
        int i = 0;
        int byteId = 0;
        while (true) {
            byte b = readByte();
            i |= (b & 0x7F) << byteId++ * 7;
            if (byteId > 5) {
                throw new RuntimeException("VarInt encoding error");
            }
            if ((b & 0x80) == 0) {
                break;
            }
        }
        return i;
    }

    public long readVarLong() throws IOException {
        long i = 0;
        int byteId = 0;
        while (true) {
            byte b = readByte();
            i |= ((long) (b & 0x7F)) << byteId++ * 7;
            if (byteId > 10) {
                throw new RuntimeException("VarLong encoding error");
            }
            if ((b & 0x80) == 0) {
                break;
            }
        }
        return i;
    }

    public UUID readUuid() throws IOException {
        long msb = readLong();
        long lsb = readLong();
        return new UUID(msb, lsb);
    }

    public Component readText() throws IOException {
        return JSONComponentSerializer.json().deserialize(readString());
    }
}
