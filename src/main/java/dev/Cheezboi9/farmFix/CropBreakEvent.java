package dev.Cheezboi9.farmFix;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CropBreakEvent implements Listener {

  // a crop matrix that reads [crop type, primary drop, seed drop]
  private enum CropInfo {
    WHEAT(Material.WHEAT, Material.WHEAT, Material.WHEAT_SEEDS),
    BEETROOTS(Material.BEETROOTS, Material.BEETROOT, Material.BEETROOT_SEEDS),
    COCOA(Material.COCOA, Material.COCOA_BEANS, Material.COCOA_BEANS),
    // Seedless crops
    CARROTS(Material.CARROTS, Material.CARROT, null),
    POTATOES(Material.POTATOES, Material.POTATO, null),
    NETHER_WART(Material.NETHER_WART, Material.NETHER_WART, null);

    private final Material cropType;
    private final Material cropDrop;
    private final Material seedDrop;

    // Constructor
    CropInfo(Material cropType, Material cropDrop, Material seedDrop) {
      this.cropType = cropType;
      this.cropDrop = cropDrop;
      this.seedDrop = seedDrop;
    }

    // Accessor Methods
    Material getCropDrop() {
      return cropDrop;
    }

    Material getSeedDrop() {
      return seedDrop;
    }

    // Search func returns null if crop type is not in CropInfo.
    static CropInfo fromCropBlock(Material type) {
      for (CropInfo info : CropInfo.values()) {
        if (info.cropType == type) {
          return info;
        }
      }
      return null; // The crop block is not found
    }
  }

  /**
   * Handles crop breaking (left click) behavior.
   * Only allows players with permission to be able to break crops.
   *
   * @param breakEvent event data from breaking any block
   */
  @EventHandler
  public void onCropBreak(BlockBreakEvent breakEvent) {
    Player player = breakEvent.getPlayer();
    Block block = breakEvent.getBlock();
    BlockData data = block.getBlockData();
    // Prevent unauthorized block breaking behavior
    if (!player.hasPermission(FarmPerms.BREAK) && (data instanceof Ageable || block.getType() == Material.FARMLAND)) {
      breakEvent.setCancelled(true);
    }

  }

  /**
   * Handles harvest behaviour
   */
  @EventHandler
  public void onCropHarvest(PlayerInteractEvent interactEvent) {
    Block block = interactEvent.getClickedBlock();
    // Early return for non-crops. Surprisingly, BlockData: Ageable does not apply to copper blocks
    if (block == null || !(block.getBlockData() instanceof Ageable)) {
      return;
    }
    // Determine if it's left/right click or a trample and handle event
    switch (interactEvent.getAction()) {
      case Action.PHYSICAL -> handleTrample(interactEvent, block); // Always involves a block
      case Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK -> useHoe(interactEvent, block);
      // Falls through so we can handle other events should we choose to
    }

  }

  private void useHoe(PlayerInteractEvent event, Block crop) {

    Player player = event.getPlayer();
    ItemStack heldItem = player.getInventory().getItemInMainHand();

    // Only allow players with perms to break crops without a hoe, otherwise return if they have no hoe in hand
    if (!isHoe(heldItem)) {
      if (!player.hasPermission(FarmPerms.BREAK)) {
        event.setCancelled(true);
      }
      return;
    }

    // Disable default harvest behaviour
    event.setCancelled(true);

    // Must have permission to harvest
    if (!player.hasPermission(FarmPerms.HARVEST)) {
      return;
    }

    List<ItemStack> cropDrops = getCropDrops(heldItem, crop.getType());
    damageTool(player, heldItem);
    dropItems(cropDrops, crop);

    // Rare event that instantly grows a crop after harvest
    float rareEventChance = 0.05f;
    boolean rareEvent = Math.random() < rareEventChance;

    // Make the rare event noticeable with sound and particles
    if (rareEvent) {
      World world = crop.getWorld();
      world.spawnParticle(Particle.HAPPY_VILLAGER, crop.getLocation().add(0.5, 1, 0.5), 5);
      world.playSound(crop.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    BlockData cropData = crop.getBlockData();

    // Set max age if rare event triggers, otherwise reset it
    if (cropData instanceof Ageable ageable) {
      int age = rareEvent ? ageable.getMaximumAge() : 0;
      ageable.setAge(age);
      crop.setBlockData(ageable);
    }
  }

  private void dropItems(List<ItemStack> drops, Block crop) {
    drops.forEach(drop -> crop.getWorld().dropItemNaturally(crop.getLocation(), drop));
  }

  private void handleTrample(PlayerInteractEvent event, Block crop) {
    Player player = event.getPlayer();
    if (crop.getType() == Material.FARMLAND) {
      if (!TrampleManager.canTrample(player.getUniqueId())) {
        event.setCancelled(true);
      }
    }
  }

  private boolean isHoe(ItemStack heldItem) {
    return heldItem != null && heldItem.getType().name().endsWith("_HOE");
  }

  /**
   * Applies damage to a tool and accounts for vanilla-ly and unbreakable behaviour.
   *
   * @param player Current player
   * @param tool   Held tool
   */
  private void damageTool(Player player, ItemStack tool) {
    // Early returns for creative mode, unbreakable tool, or no meta to take damage
    ItemMeta meta = tool.getItemMeta();
    if (player.getGameMode() == GameMode.CREATIVE || meta != null && meta.isUnbreakable()) {
      return;
    }
    if (meta == null) {
      return;
    }
    tool.damage(1, player); // Should handle unbreakable enchants vanilla-ly
  }


  // Handles calculations for how many crops to drop based on crop type
  private List<ItemStack> getCropDrops(ItemStack hoe, Material cropType) {

    // Early return for unsupported crops/blocks
    CropInfo cropDropInfo = CropInfo.fromCropBlock(cropType);
    if (cropDropInfo == null) {
      return List.of();
    }

    // Default behavior without fortune
    Material hoeType = hoe.getType();
    int baseDropAmount = switch (hoeType) {
      case WOODEN_HOE, STONE_HOE -> 1;
      case IRON_HOE -> 2;
      case DIAMOND_HOE, NETHERITE_HOE -> 3;
      default -> 0;
    };

    // Fortune behavior in this plugin is [amount to drop] + [fortune level]
    int fortuneLevel = hoe.getEnchantmentLevel(Enchantment.FORTUNE);

    float extraSeedChance = 0.5f; // simple solution means ez to change later
    int extraSeed = Math.random() < extraSeedChance ? 1 : 0; // Used to add seeds, rather than compare so is converted to int

    Material seed = cropDropInfo.getSeedDrop();
    Material crop = cropDropInfo.getCropDrop();

    List<ItemStack> toDrop = new ArrayList<>();
    toDrop.add(new ItemStack(crop, baseDropAmount + fortuneLevel));
    if (seed != null) {
      toDrop.add(new ItemStack(seed, 1 + extraSeed));
    }

    return toDrop;
  }

}
