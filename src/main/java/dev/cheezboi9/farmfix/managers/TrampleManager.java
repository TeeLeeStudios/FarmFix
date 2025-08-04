package dev.cheezboi9.farmfix.managers;

import dev.cheezboi9.farmfix.FarmFix;

import java.util.UUID;

public class TrampleManager {

  // Appends FarmFix to our logs which is easier to debug later

  private boolean mobTrample = false;


  public void toggleTrample(UUID uuid, boolean state) {
    if (state) {
      FarmFix.getDatabaseManager().upsertTrample(uuid);
    } else {
      FarmFix.getDatabaseManager().removeTrample(uuid);
    }
  }

  public void forceTrample(UUID uuid, boolean state) {
    if (state) {
      FarmFix.getDatabaseManager().upsertForced(uuid);
    } else {
      FarmFix.getDatabaseManager().removeForced(uuid);
    }
  }

  public boolean isForced(UUID uuid) {
    return FarmFix.getDatabaseManager().getForcedState(uuid);
  }

  public boolean canTrample(UUID uuid) {
    return FarmFix.getDatabaseManager().getTrampleState(uuid);
  }

  public void setMobTrample (boolean state) {
    this.mobTrample = state;
  }

  public boolean canMobTrample() {
    return this.mobTrample;
  }

}
