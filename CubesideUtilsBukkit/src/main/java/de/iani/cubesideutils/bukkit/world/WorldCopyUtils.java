package de.iani.cubesideutils.bukkit.world;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldCopyUtils {
    public static void backupWorld(JavaPlugin plugin, Key worldIdentifier, Path backupFolder) throws IOException {
        Files.createDirectories(backupFolder);
        if (Files.isRegularFile(backupFolder.resolve("level.dat"))) {
            // cleanup legacy
            Files.deleteIfExists(backupFolder.resolve("level.dat"));
            deleteDirectoryIfExists(backupFolder.resolve("region"));
            deleteDirectoryIfExists(backupFolder.resolve("poi"));
            deleteDirectoryIfExists(backupFolder.resolve("entities"));
        }
        Path backupTolevelPath = backupFolder.resolve("level");
        if (Files.isDirectory(backupTolevelPath)) {
            // cleanup modern
            deleteDirectoryIfExists(backupTolevelPath);
        }
        // backup modern
        Files.createDirectories(backupTolevelPath);

        Path backupFromBaseLevelPath = plugin.getServer().getLevelDirectory().resolve("dimensions", worldIdentifier.namespace(), worldIdentifier.value());
        if (Files.isDirectory(backupFromBaseLevelPath)) {
            copyDirectoryContents(backupFromBaseLevelPath, backupTolevelPath);
        }

        // need to remove paper metadata to avoid loading issues
        Path metaDataFile = backupTolevelPath.resolve("data", "paper", "metadata.dat");
        Files.deleteIfExists(metaDataFile);
    }

    public static void restoreWorld(JavaPlugin plugin, Path backupFolder, Key worldIdentifier) throws IOException {
        // delete legacy
        Path legacyRestoreToBaseLevelPath = plugin.getServer().getLevelDirectory().getParent().resolve(worldIdentifier.value());
        deleteDirectoryIfExists(legacyRestoreToBaseLevelPath);
        // delete modern
        Path restoreToBaseLevelPath = plugin.getServer().getLevelDirectory().resolve("dimensions", worldIdentifier.namespace(), worldIdentifier.value());
        deleteDirectoryIfExists(restoreToBaseLevelPath);

        if (Files.isRegularFile(backupFolder.resolve("level.dat"))) {
            // restore legacy
            Files.createDirectories(legacyRestoreToBaseLevelPath);
            Files.copy(backupFolder.resolve("level.dat"), legacyRestoreToBaseLevelPath.resolve("level.dat"));
            Files.createDirectories(legacyRestoreToBaseLevelPath.resolve("region"));
            if (Files.isDirectory(backupFolder.resolve("region"))) {
                copyDirectoryContents(backupFolder.resolve("region"), legacyRestoreToBaseLevelPath.resolve("region"));
            }
            Files.createDirectories(legacyRestoreToBaseLevelPath.resolve("poi"));
            if (Files.isDirectory(backupFolder.resolve("poi"))) {
                copyDirectoryContents(backupFolder.resolve("poi"), legacyRestoreToBaseLevelPath.resolve("poi"));
            }
            Files.createDirectories(legacyRestoreToBaseLevelPath.resolve("entities"));
            if (Files.isDirectory(backupFolder.resolve("entities"))) {
                copyDirectoryContents(backupFolder.resolve("entities"), legacyRestoreToBaseLevelPath.resolve("entities"));
            }
            return;
        }
        Path restoreFromLevelPath = backupFolder.resolve("level");
        if (Files.isDirectory(restoreFromLevelPath)) {
            // restore modern
            deleteDirectoryIfExists(restoreToBaseLevelPath);
            Files.createDirectories(restoreToBaseLevelPath);
            copyDirectoryContents(restoreFromLevelPath, restoreToBaseLevelPath);

            // need to remove paper metadata to avoid loading issues
            Path metaDataFile = restoreToBaseLevelPath.resolve("data", "paper", "metadata.dat");
            Files.deleteIfExists(metaDataFile);
        }
    }

    public static void moveEveryoneOutOfWorld(World fromWorld, Location toLocation, JavaPlugin plugin) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.getWorld() == fromWorld) {
                p.eject();
                if (p.getVehicle() != null) {
                    p.getVehicle().eject();
                }

                toLocation.getChunk();
                p.teleport(toLocation);
            }
        }
    }

    private static void deleteDirectoryIfExists(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void copyDirectoryContents(Path sourceDir, Path targetDir) throws IOException {
        if (!Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("sourceDir is not a directory: " + sourceDir);
        }

        Files.createDirectories(targetDir);

        Files.walkFileTree(sourceDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(dir);
                Path targetPath = targetDir.resolve(relative);
                Files.createDirectories(targetPath);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = sourceDir.relativize(file);
                Path targetPath = targetDir.resolve(relative);
                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
