package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.block.Block;
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
   * Cancels piston extension/retraction if an illegal block is detected
   *
   * @param pistonEvent    The Piston event to cancel if illegal block is affected
   * @param blocksAffected The list of blocks the piston would affect
   */
  private void handlePistonEvent(BlockPistonEvent pistonEvent, List<Block> blocksAffected) {
//    for (Block block : blocksAffected) {
//      if (block.getType() == Material.FARMLAND) {
//        Block blockAbove = block.getWorld().getBlockAt(block.getLocation().add(0,1,0));
//        if (blockAbove instanceof Ageable ageable) {
//
//        }
//        return;
//      }
//      if (block.getBlockData() instanceof Ageable ageable) {
//
//        return;
//      }
//
//    }
  }

}
