package de.iani.cubesideutils.bukkit.blocks;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.SideChaining.ChainPart;
import org.bukkit.block.data.type.Shelf;
import org.bukkit.util.Vector;

public class BlockUtils {
    private BlockUtils() {
        throw new UnsupportedOperationException("No instance for you, Sir!"); // really prevents instances
    }

    /**
     * Gets the list of shelf blocks that form a shelf block chain.
     *
     * @param start
     *            the clicked block. must be a powered shelf.
     * @return a list of up to 3 blocks that form the chain
     */
    public static List<Block> getShelfChainBlocks(Block start) {
        BlockData startBlockData = start.getBlockData();
        if (!Tag.WOODEN_SHELVES.isTagged(startBlockData.getMaterial()) || !(startBlockData instanceof Shelf blockData) || !blockData.isPowered()) {
            throw new IllegalArgumentException("start block is not a powered shelf!");
        }

        // find neighbours - up to 3 blocks total, priorize right side (up to 2) then left side
        // stop when not shelf, not powered or not connected to the current (to the right must have "sidechain.right" and vv
        // stop after the block if not center or limit reached
        BlockFace side = rotate90degreeClockwise(blockData.getFacing()).getOppositeFace();
        // to the right
        ArrayList<Block> blockChain = new ArrayList<>();
        blockChain.add(start);
        for (int i = 1; i < 3; i++) {
            Block neighbour = start.getRelative(side, i);
            if (!(neighbour.getBlockData() instanceof Shelf neighbourData) || !neighbourData.isPowered() || neighbourData.getFacing() != blockData.getFacing()) {
                break;
            }
            ChainPart sideChain = neighbourData.getSideChain();
            if (sideChain != ChainPart.CENTER && sideChain != ChainPart.RIGHT) {
                break;
            }
            blockChain.add(neighbour);
            if (sideChain != ChainPart.CENTER) {
                break;
            }
        }
        // to the left
        side = side.getOppositeFace();
        for (int i = 1; i < 3 && blockChain.size() < 3; i++) {
            Block neighbour = start.getRelative(side, i);
            if (!(neighbour.getBlockData() instanceof Shelf neighbourData) || !neighbourData.isPowered() || neighbourData.getFacing() != blockData.getFacing()) {
                break;
            }
            ChainPart sideChain = neighbourData.getSideChain();
            if (sideChain != ChainPart.CENTER && sideChain != ChainPart.LEFT) {
                break;
            }
            blockChain.add(0, neighbour);
            if (sideChain != ChainPart.CENTER) {
                break;
            }
        }
        return blockChain;
    }

    /**
     * rotates a blockface when looking down to it. the direction must be a cardinal direction
     *
     * @param direction
     *            the initial direction
     * @return the new direction
     */
    public static BlockFace rotate90degreeClockwise(BlockFace direction) {
        return switch (direction) {
            case NORTH -> BlockFace.EAST;
            case SOUTH -> BlockFace.WEST;
            case WEST -> BlockFace.NORTH;
            case EAST -> BlockFace.SOUTH;
            default -> throw new IllegalArgumentException("direction must be cardinal");
        };
    }

    /**
     * Gets the clicked slot in a container like chiseled bookshelfes or shelfes. this logic is replicated from the internal minecraft logic
     *
     * @param face
     *            the clicked face on the block (must also be the open face)
     * @param clickPosition
     *            the clicked position in this block (0..1)
     * @param rows
     *            the rows of the container
     * @param columns
     *            the columns of the container
     * @return
     */
    public static int getSelectableSlotContainerHitSlot(BlockFace face, Vector clickPosition, int rows, int columns) {
        double clickX = switch (face) {
            case NORTH -> 1 - clickPosition.getX();
            case SOUTH -> clickPosition.getX();
            case EAST -> 1 - clickPosition.getZ();
            case WEST -> clickPosition.getZ();
            default -> throw new IllegalArgumentException("Unexpected facing for SelectableSlotContainer: " + face);
        };
        double clickY = clickPosition.getY();
        int sectionY = getSelectableSlotContainerSection(1.0F - clickY, rows);
        int sectionX = getSelectableSlotContainerSection(clickX, columns);
        return sectionX + sectionY * columns;
    }

    private static int getSelectableSlotContainerSection(double x, int slots) {
        return Math.clamp((int) Math.floor(x * slots), 0, slots - 1);
    }
}
