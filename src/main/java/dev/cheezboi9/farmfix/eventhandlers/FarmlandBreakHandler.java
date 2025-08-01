package dev.cheezboi9.farmfix.eventhandlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

// Handles breaking farmland with crops
public class FarmlandBreakHandler implements Listener {

  @EventHandler (ignoreCancelled = true)
  public void onDropEvent (BlockDropItemEvent dropItemEvent) {

    Block block = dropItemEvent.getBlock();
    List<Item> items = dropItemEvent.getItems();
    List<Item> dropList = new ArrayList<>();
    boolean hasDirt = false;
    CropUtility.CropInfo matchedCrop = null;

    // Search for a valid crop while adding non-crops to a dropList
    for (Item item : items) {
      ItemStack itemStack = item.getItemStack();
      CropUtility.CropInfo cropInfo = CropUtility.CropInfo.fromDrop(itemStack.getType());

      if (itemStack.getType() == Material.DIRT) {
        hasDirt = true;
      }
      if (cropInfo != null) {
        matchedCrop = cropInfo;
        continue;
      }
      dropList.add(item);
    }

    // We do not have any crops in our list, default to vanilla drop behaviour
    if (matchedCrop == null) {
      return;
    }

    // If player is not breaking dirt, default to vanilla drop behaviour
    if (!hasDirt) {
      return;
    }

    // Crop and dirt exists, only drop relevant items
    dropItemEvent.getItems().clear();
    // Select the seed or crop, if seed does not exist for the crop
    Material toDrop = matchedCrop.getSeedDrop() != null ? matchedCrop.getSeedDrop() : matchedCrop.getCropDrop();

    // Drop seed, then remaining items
    block.getWorld().dropItemNaturally(block.getLocation(), ItemStack.of(toDrop));
    for (Item drop : dropList) {
      block.getWorld().dropItemNaturally(block.getLocation(), drop.getItemStack());
    }

  }
}
