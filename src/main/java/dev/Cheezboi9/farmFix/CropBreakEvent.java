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
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class CropBreakEvent implements Listener {
  private static final float RARE_EVENT_CHANCE = 0.05f; // Chance to immediately grow a crop after harvest
  private static final float EXTRA_SEED_CHANCE = 0.5f;

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
    // Early return for non-crops or non-farmlands. Surprisingly, BlockData: Ageable does not apply to copper blocks
    if (block == null || (!(block.getBlockData() instanceof Ageable) && block.getType() != Material.FARMLAND) ) {
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

    // Early returns so that the crop block is actually a crop, and we're using a hoe
    if (!(crop.getBlockData() instanceof Ageable ageable)) {
      return;
    }
    // Bone meal behaviour should not be affected
    if (heldItem.getType() == Material.BONE_MEAL) {
      return;
    }

    // Disable default harvest behaviour
    event.setCancelled(true);

    if (!isHoe(heldItem)) {
      return;
    }
    // Must have permission to harvest
    if (!player.hasPermission(FarmPerms.HARVEST)) {
      return;
    }
    // or if crop is not mature yet
    if (ageable.getAge() < ageable.getMaximumAge()) {
      return;
    }

    List<ItemStack> cropDrops = getCropDrops(heldItem, crop.getType());
    damageTool(player, heldItem);
    dropItems(cropDrops, crop);

    // Rare event that instantly grows a crop after harvest
    boolean rareEvent = triggerRareEvent(crop.getWorld(), crop.getLocation());

    // Set max age if rare event triggers, otherwise reset it
    int age = rareEvent ? ageable.getMaximumAge() : 0;
    ageable.setAge(age);
    crop.setBlockData(ageable);
  }

  private boolean triggerRareEvent(World world, Location location) {
    boolean rareEventTriggered = Math.random() < RARE_EVENT_CHANCE;
    if (rareEventTriggered) {
      world.spawnParticle(Particle.HAPPY_VILLAGER, location.add(0.5, 1, 0.5), 5);
      world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    return rareEventTriggered;
  }

  private void dropItems(List<ItemStack> drops, Block crop) {
    drops.forEach(drop -> crop.getWorld().dropItemNaturally(crop.getLocation(), drop));
    crop.getWorld().playSound(crop.getLocation(), Sound.ITEM_CROP_PLANT, 0.4f, 1.0f);
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
    return heldItem.getType().name().endsWith("_HOE");
  }

  /**
   * Applies damage to a tool and accounts for vanilla-like and unbreakable behaviour.
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

    if (meta instanceof Damageable damageable) { // The spigot-safe way
      // Handle Unbreaking the vanilla way
      int unbreakingLevel = tool.getEnchantmentLevel(Enchantment.UNBREAKING);
      float damageChance = (1f / (unbreakingLevel + 1)); // https://minecraft.fandom.com/wiki/Unbreaking
      boolean takeDamage = (Math.random() < damageChance);

      if (takeDamage) {
        int currentDamage = damageable.getDamage();
        damageable.setDamage(currentDamage + 1);
        tool.setItemMeta(meta);
        tryVanillaBreak(tool, damageable, player);
      }
    }

    // tool.damage(1, player); // This is Paper extended api... it would handle unbreaking too :(
  }

  // Breaks tool if max durability is reached
  private void tryVanillaBreak(ItemStack tool, Damageable damageable, Player player) {
    if (damageable.getDamage() >= tool.getType().getMaxDurability()) {
      player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
      player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
    }
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
    int extraSeed = Math.random() < EXTRA_SEED_CHANCE ? 1 : 0; // Used to add seeds, rather than compare so is converted to int

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
