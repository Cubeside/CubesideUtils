package de.iani.cubesideutils.bukkit.serialization;

import de.cubeside.connection.util.GlobalLocation;
import de.iani.cubesideutils.serialization.StringSerializable;
import java.util.regex.Pattern;

public class GlobalLocationWrapper implements StringSerializable {

    // registered in StringSerialization
    public static final String SERIALIZATION_TYPE = "GlobalLocation";

    public static GlobalLocationWrapper deserialize(String serialized) {
        if (serialized == null) {
            return new GlobalLocationWrapper(null);
        }

        String[] parts = serialized.split(Pattern.quote(";"));
        String server = parts[0];
        String world = parts[1];
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double z = Double.parseDouble(parts[4]);
        float yaw = 0.0f, pitch = 0.0f;
        if (parts.length > 5) {
            yaw = Float.parseFloat(parts[5]);
            pitch = Float.parseFloat(parts[6]);
        }

        return new GlobalLocationWrapper(new GlobalLocation(server, world, x, y, z, yaw, pitch));
    }

    public final GlobalLocation original;

    public GlobalLocationWrapper(GlobalLocation original) {
        this.original = original;
    }

    @Override
    public String getSerializationType() {
        return SERIALIZATION_TYPE;
    }

    @Override
    public String serializeToString() {
        if (this.original == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(this.original.getServer());
        builder.append(';').append(this.original.getWorld());
        builder.append(';').append(this.original.getX());
        builder.append(';').append(this.original.getY());
        builder.append(';').append(this.original.getZ());
        if (this.original.getYaw() != 0 || this.original.getPitch() != 0) {
            builder.append(';').append(this.original.getYaw());
            builder.append(';').append(this.original.getPitch());
        }
        return builder.toString();
    }

}
