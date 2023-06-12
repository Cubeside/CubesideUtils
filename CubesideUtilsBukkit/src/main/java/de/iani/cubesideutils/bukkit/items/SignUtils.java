package de.iani.cubesideutils.bukkit.items;

import de.iani.cubesideutils.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.HangingSign;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallHangingSign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;

public class SignUtils {
    private SignUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!");
        // really prevents instances
    }

    public static Side getFacingSignSide(Entity entity, Block sign) {
        BlockData data = sign.getBlockData();
        Material type = data.getMaterial();
        BlockFace signFace = null;
        double centerx = 0.5;
        double centerz = 0.5;
        double yRotationDegree = 0;
        if (type.data == Sign.class || type.data == HangingSign.class) {
            Rotatable rotatableData = (Rotatable) data;
            signFace = rotatableData.getRotation();
            if (signFace == BlockFace.SOUTH) {
                yRotationDegree = 360 * 0.0 / 16.0;
            } else if (signFace == BlockFace.SOUTH_SOUTH_WEST) {
                yRotationDegree = 360 * 1.0 / 16.0;
            } else if (signFace == BlockFace.SOUTH_WEST) {
                yRotationDegree = 360 * 2.0 / 16.0;
            } else if (signFace == BlockFace.WEST_SOUTH_WEST) {
                yRotationDegree = 360 * 3.0 / 16.0;
            } else if (signFace == BlockFace.WEST) {
                yRotationDegree = 360 * 4.0 / 16.0;
            } else if (signFace == BlockFace.WEST_NORTH_WEST) {
                yRotationDegree = 360 * 5.0 / 16.0;
            } else if (signFace == BlockFace.NORTH_WEST) {
                yRotationDegree = 360 * 6.0 / 16.0;
            } else if (signFace == BlockFace.NORTH_NORTH_WEST) {
                yRotationDegree = 360 * 7.0 / 16.0;
            } else if (signFace == BlockFace.NORTH) {
                yRotationDegree = 360 * 8.0 / 16.0;
            } else if (signFace == BlockFace.NORTH_NORTH_EAST) {
                yRotationDegree = 360 * 9.0 / 16.0;
            } else if (signFace == BlockFace.NORTH_EAST) {
                yRotationDegree = 360 * 10.0 / 16.0;
            } else if (signFace == BlockFace.EAST_NORTH_EAST) {
                yRotationDegree = 360 * 11.0 / 16.0;
            } else if (signFace == BlockFace.EAST) {
                yRotationDegree = 360 * 12.0 / 16.0;
            } else if (signFace == BlockFace.EAST_SOUTH_EAST) {
                yRotationDegree = 360 * 13.0 / 16.0;
            } else if (signFace == BlockFace.SOUTH_EAST) {
                yRotationDegree = 360 * 14.0 / 16.0;
            } else if (signFace == BlockFace.SOUTH_SOUTH_EAST) {
                yRotationDegree = 360 * 15.0 / 16.0;
            }
        } else if (type.data == WallSign.class || type.data == WallHangingSign.class) {
            Directional directionalData = (Directional) data;
            signFace = directionalData.getFacing();
            if (signFace == BlockFace.SOUTH) {
                yRotationDegree = 0;
            } else if (signFace == BlockFace.WEST) {
                yRotationDegree = 90;
            } else if (signFace == BlockFace.NORTH) {
                yRotationDegree = 180;
            } else if (signFace == BlockFace.EAST) {
                yRotationDegree = 270;
            }
            // wall signs are not centered on the block (but hanging wall signs are)
            if (type.data == WallSign.class) {
                if (signFace == BlockFace.NORTH) {
                    centerz = 15.0 / 16.0;
                } else if (signFace == BlockFace.SOUTH) {
                    centerz = 1.0 / 16.0;
                } else if (signFace == BlockFace.WEST) {
                    centerx = 15.0 / 16.0;
                } else if (signFace == BlockFace.EAST) {
                    centerx = 1.0 / 16.0;
                }
            }
        } else {
            throw new IllegalArgumentException("block is not a sign");
        }

        Location entityLoc = entity.getLocation();
        double relativeX = entityLoc.getX() - (sign.getX() + centerx);
        double relativeZ = entityLoc.getZ() - (sign.getZ() + centerz);
        double f = Math.atan2(relativeZ, relativeX) * 180.0 / Math.PI - 90.0;

        return Math.abs(MathUtil.warpDegrees(f - yRotationDegree)) <= 90.0 ? Side.FRONT : Side.BACK;
    }

}
