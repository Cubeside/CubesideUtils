package de.iani.cubesideutils.nbt.io;

import de.iani.cubesideutils.nbt.CompoundTag;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NbtIo {
    public static CompoundTag readCompressed(byte[] buffer) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer)) {
            return readCompressed(bais);
        }
    }

    public static CompoundTag readCompressed(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            return readCompressed(fis);
        }
    }

    public static CompoundTag readCompressed(InputStream in) throws IOException {
        return new NbtInputStream(new BufferedInputStream(new GZIPInputStream(in))).readNbt();
    }

    public static byte[] writeCompressed(CompoundTag tag) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            writeCompressed(tag, baos);
            return baos.toByteArray();
        }
    }

    public static void safeWriteCompressed(CompoundTag tag, File file) throws IOException {
        File fileTemp = new File(file.getAbsolutePath() + "_tmp");
        if (fileTemp.exists()) {
            fileTemp.delete();
        }
        writeCompressed(tag, fileTemp);
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete " + file);
            }
        }
        fileTemp.renameTo(file);
    }

    public static void writeCompressed(CompoundTag tag, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            writeCompressed(tag, fos);
        }
    }

    public static void writeCompressed(CompoundTag tag, OutputStream out) throws IOException {
        NbtOutputStream dos = new NbtOutputStream(new BufferedOutputStream(new GZIPOutputStream(out)));
        try {
            dos.writeNbt(tag);
        } finally {
            dos.flush();
        }
    }
}
