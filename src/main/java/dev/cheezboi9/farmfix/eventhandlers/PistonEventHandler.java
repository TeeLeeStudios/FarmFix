package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;

public class PistonEventHandler implements Listener {
  @EventHandler(ignoreCancelled = true)
  public void onPistonExtend(BlockPistonExtendEvent pistonExtendEvent) {
    handlePistonEvent(pistonExtendEvent.getBlocks());
  }

  @EventHandler(ignoreCancelled = true)
  public void onPistonRetract(BlockPistonRetractEvent pistonRetractEvent) {
    handlePistonEvent(pistonRetractEvent.getBlocks());
  }

  /**
   * Fixes drops from pistons to only drop seeds
   *
   * @param blocksAffected The list of blocks the piston would affect
   */
  private void handlePistonEvent(List<Block> blocksAffected) {
    for (Block block : blocksAffected) {
      // Check to see if farmland is being moved
      if (block.getType() == Material.FARMLAND) {
        Block blockAbove = block.getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
        // Drop seed if a crop is above
        if (CropUtility.dropSeed(blockAbove)) {
          blockAbove.setType(Material.AIR);
          continue;
        }
        continue; // No crop is affected
      }

      // Drop only the seed if the block is a crop
      if (CropUtility.dropSeed(block)) {
        block.setType(Material.AIR);
      }
    }
  }

}
