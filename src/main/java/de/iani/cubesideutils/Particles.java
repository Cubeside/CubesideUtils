package de.iani.cubesideutils;

import de.iani.cubesideutils.Locatable.LocationWrapper;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Particles {
    private Particles() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // prevents instances
    }

    private static int MAX_COLOR_VALUE = (1 << 24) - 1;

    public static void spawnParticles(Player player, Particle particle, double amount, double x, double y, double z, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        int intAmount = (int) Math.floor(amount) + (Math.random() < amount - Math.floor(amount) ? 1 : 0);

        boolean randomColor = particle == Particle.REDSTONE && data == null;
        for (int i = 0; i < intAmount; i++) {
            double newX = x + (2 * Math.random() * offsetX) - offsetX;
            double newY = y + (2 * Math.random() * offsetY) - offsetY;
            double newZ = z + (2 * Math.random() * offsetZ) - offsetZ;

            if (randomColor) {
                data = new DustOptions(Color.fromRGB(RandomUtil.SHARED_RANDOM.nextInt(MAX_COLOR_VALUE)), 1.0f);
            }

            player.spawnParticle(Particle.REDSTONE, newX, newY, newZ, 1, 0.0f, 0.0f, 0.0f, extra, data);
        }

    }

    public static void spawnParticles(Player player, Particle particle, double amount, Location location, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        if (location.getWorld() != player.getWorld()) {
            return;
        }
        spawnParticles(player, particle, amount, location.getX(), location.getY(), location.getZ(), offsetX, offsetY, offsetZ, extra, data);
    }

    // color null bedeuted bunt.
    public static void spawnColoredDust(Player player, double amount, double x, double y, double z, double offsetX, double offsetY, double offsetZ, Color color) {
        spawnParticles(player, Particle.REDSTONE, amount, x, y, z, offsetX, offsetY, offsetZ, 1.0f, color == null ? null : new DustOptions(color, 1.0f));
    }

    public static void spawnColoredDust(Player player, double amount, Location location, double offsetX, double offsetY, double offsetZ, Color color) {
        spawnParticles(player, Particle.REDSTONE, amount, location, offsetX, offsetY, offsetZ, 1.0f, color == null ? null : new DustOptions(color, 1.0f));
    }

    // numberOfTicks < 0 bedeuted unendlich.
    // returned: taskId (-1 wenn fehlgeschlagen oder numberOfTicks == 0)
    public static int spawnParticles(Plugin plugin, Player player, Particle particle, double amountPerTick, int numberOfTicks, Locatable location, double offsetX, double offsetY, double offsetZ, double extra, Object... datas) {
        if (numberOfTicks == 0) {
            return -1;
        }

        BukkitTask runnable = new BukkitRunnable() {

            private int count = 0;

            @Override
            public void run() {
                if (!player.isValid()) {
                    cancel();
                    return;
                }

                Location loc = location.getLocation();
                Object data = (datas == null || datas.length == 0) ? null : datas[RandomUtil.SHARED_RANDOM.nextInt(datas.length)];
                spawnParticles(player, particle, amountPerTick, loc, offsetX, offsetY, offsetZ, extra, data);

                if (this.count >= 0 && ++this.count >= numberOfTicks) {
                    cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 1);

        return runnable.getTaskId();
    }

    // numberOfTicks < 0 bedeuted unendlich.
    // returned: taskId (-1 wenn fehlgeschlagen oder numberOfTicks == 0)
    public static int spawnParticles(Plugin plugin, Player player, Particle particle, double amountPerTick, int numberOfTicks, double x, double y, double z, double offsetX, double offsetY, double offsetZ, double extra, Object data) {
        Location location = player.getLocation();
        location.set(x, y, z);
        return spawnParticles(plugin, player, particle, amountPerTick, numberOfTicks, new LocationWrapper(location), offsetX, offsetY, offsetZ, extra, data);
    }

    // color null bedeuted bunt, numberOfTicks < 0 bedeuted unendlich.
    // returned: taskId (-1 wenn fehlgeschlagen oder numberOfTicks == 0)
    public static int spawnColoredDust(Plugin plugin, Player player, double amountPerTick, int numberOfTicks, Locatable location, double offsetX, double offsetY, double offsetZ, Color... colors) {
        Object[] datas;
        if (colors == null || colors.length == 0) {
            datas = null;
        } else {
            datas = new Object[colors.length];
            for (int i = 0; i < datas.length; i++) {
                datas[i] = colors[i] == null ? null : new DustOptions(colors[i], 1.0f);
            }
        }
        return spawnParticles(plugin, player, Particle.REDSTONE, amountPerTick, numberOfTicks, location, offsetX, offsetY, offsetZ, 1.0f, datas);
    }

    // color null bedeuted bunt, numberOfTicks < 0 bedeuted unendlich.
    // returned: taskId (-1 wenn fehlgeschlagen oder numberOfTicks == 0)
    public static int spawnColoredDust(Plugin plugin, Player player, double amountPerTick, int numberOfTicks, double x, double y, double z, double offsetX, double offsetY, double offsetZ, Color... colors) {
        Location location = player.getLocation();
        location.set(x, y, z);
        return spawnColoredDust(plugin, player, amountPerTick, numberOfTicks, new LocationWrapper(location), offsetX, offsetY, offsetZ, colors);
    }
}
