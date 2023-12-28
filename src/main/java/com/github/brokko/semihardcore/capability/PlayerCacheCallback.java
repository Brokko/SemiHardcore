package com.github.brokko.semihardcore.capability;

import net.minecraft.world.entity.player.Player;

public interface PlayerCacheCallback {
    void valuesUpdated(Player player, PlayerData playerData);
}
