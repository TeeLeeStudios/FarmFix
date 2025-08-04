package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;


public class LiquidBreakEventHandler implements Listener {

  @EventHandler (ignoreCancelled = true)
  public void handleLiquidEvent (BlockFromToEvent event) {
    Block toBlock = event.getToBlock();

    if (CropUtility.notCrop(toBlock)){
      return;
    }

    CropUtility.dropSeed(toBlock);
    toBlock.setType(Material.AIR);
    event.setCancelled(true);
  }

}
