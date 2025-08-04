package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.List;
//TODO: Allow crops to drop just their seed
public class PistonEventHandler implements Listener {
  @EventHandler (ignoreCancelled = true)
  public void onPistonExtend(BlockPistonExtendEvent pistonExtendEvent) {
    handlePistonEvent(pistonExtendEvent, pistonExtendEvent.getBlocks());
  }

  @EventHandler (ignoreCancelled = true)
  public void onPistonRetract(BlockPistonRetractEvent pistonRetractEvent) {
    handlePistonEvent(pistonRetractEvent, pistonRetractEvent.getBlocks());
  }

  /**
   * Fixes drops from pistons to only drop seeds
   *
   * @param pistonEvent    The Piston event that's triggered
   * @param blocksAffected The list of blocks the piston would affect
   */
  private void handlePistonEvent(BlockPistonEvent pistonEvent, List<Block> blocksAffected) {
    for (Block block : blocksAffected) {
      // Check to see if farmland is being moved
      if (block.getType() == Material.FARMLAND) {
        Block blockAbove = block.getWorld().getBlockAt(block.getLocation().add(0,1,0));
        // Drop seed if a crop is above
        if (blockAbove instanceof Ageable && CropUtility.dropSeed(blockAbove)) {
          blockAbove.setType(Material.AIR);
          return;
        }
        return; // No crop is affected, allow vanilla behaviour
      }
      // If the crop is directly being broken
      if (block.getBlockData() instanceof Ageable ageable) {

        return;
      }

    }
  }

}
