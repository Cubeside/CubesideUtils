package de.iani.cubesideutils;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Locatable {

    public static class LocationWrapper implements Locatable {
        private Location location;

        public LocationWrapper(Location location) {
            this.location = Objects.requireNonNull(location);
        }

        @Override
        public Location getLocation() {
            return location;
        }

    }

    public static class EntityWrapper implements Locatable {
        private Entity entity;

        private double offsetX;
        private double offsetY;
        private double offsetZ;

        public EntityWrapper(Entity entity) {
            this(entity, 0.0, 0.0, 0.0);
        }

        public EntityWrapper(Entity entity, double offsetX, double offsetY, double offsetZ) {
            this.entity = Objects.requireNonNull(entity);
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }

        @Override
        public Location getLocation() {
            return entity.getLocation().add(offsetX, offsetY, offsetZ);
        }

    }

    public Location getLocation();

}
