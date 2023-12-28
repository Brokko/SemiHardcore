package com.github.brokko.semihardcore.capability;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Aims to cache values which are called in looping events like TickEvent.PlayerTickEvent
 * to achieve a better performance. Keeps the cached values up to date through callbacks
 * from {@link PlayerData} and {@link com.github.brokko.semihardcore.events.ModEvents}
 */
public class PlayerCapabilityCache implements PlayerCacheCallback{
    public static final List<Player> DEAD_PLAYERS = new LinkedList<>();
    public static final HashMap<Player, UUID> DEAD_BOUND = new HashMap<>();

    public PlayerCapabilityCache() {

    }

    // DON'T MODIFY PLAYERDATA HERE IN ANY WAY
    @Override
    public void valuesUpdated(Player player, PlayerData playerData) {
        if (playerData.isDead()) {
            if (!DEAD_PLAYERS.contains(player)) {
                DEAD_PLAYERS.add(player);
            }

            UUID bound = playerData.getBoundTo();
            if (bound != null)
                DEAD_BOUND.put(player, bound);
        } else {
            DEAD_PLAYERS.remove(player);
            DEAD_BOUND.remove(player);
        }
    }
}
