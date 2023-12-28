package com.github.brokko.semihardcore.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerData {
    private final PlayerCacheCallback callback;

    private Player player = null;
    private boolean dead = false;
    private int deathCount = 0;
    private UUID boundTo = UUID.randomUUID();

    public PlayerData(PlayerCacheCallback callback) {
        this.callback = callback;
    }

    public void setPlayer(Player player) {
        this.player = player;

        // Notify update handler
        callback.valuesUpdated(player, this);
    }

    public boolean isDead() {
        return dead;
    }

    public void setIsDead(boolean isDead) {
        dead = isDead;

        // Notify update handler
        callback.valuesUpdated(player, this);
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void increaseDeathCount() {
        deathCount++;

        // Notify update handler
        callback.valuesUpdated(player, this);
    }

    public UUID getBoundTo() {
        return boundTo;
    }

    public void setBoundTo(@NotNull UUID bound) {
        boundTo = bound;

        // Notify update handler
        callback.valuesUpdated(player, this);
    }

    public void copyForm(PlayerData data) {
        this.deathCount = data.deathCount;
        this.dead = data.isDead();
    }

    public void saveNBTData(CompoundTag tag) {
        tag.putInt("deathCount", deathCount);
        tag.putBoolean("isDead", dead);
        tag.putUUID("boundPlayer", boundTo);
    }

    public void loadNBTData(CompoundTag tag) {
        this.dead = tag.getBoolean("isDead");
        this.deathCount = tag.getInt("deathCount");
        this.boundTo = tag.getUUID("boundPlayer");
    }
}
