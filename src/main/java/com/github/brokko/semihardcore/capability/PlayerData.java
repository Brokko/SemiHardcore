package com.github.brokko.semihardcore.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerData {
    private boolean dead = false;
    private int deathCount = 0;

    public PlayerData() {

    }

    public boolean isDead() {
        return dead;
    }

    public void setIsDead(boolean isDead) {
        dead = isDead;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void increaseDeathCount() {
        deathCount++;
    }

    public void copyForm(PlayerData data) {
        this.deathCount = data.deathCount;
        this.dead = data.isDead();
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("deathCount", deathCount);
        tag.putBoolean("isDead", dead);
    }

    public void loadNBTData(CompoundTag tag) {
        this.dead = tag.getBoolean("isDead");
        this.deathCount = tag.getInt("deathCount");
    }
}
