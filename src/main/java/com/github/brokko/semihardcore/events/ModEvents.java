package com.github.brokko.semihardcore.events;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import com.github.brokko.semihardcore.capability.PlayerCapabilityCache;
import com.github.brokko.semihardcore.capability.PlayerCapabilityProvider;
import com.github.brokko.semihardcore.capability.PlayerData;
import com.github.brokko.semihardcore.item.SoulChainItem;
import com.github.brokko.semihardcore.register.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemiHardcoreMod.MODID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    public final static HashMap<UUID, Runnable> PLAYER_ACTION_BOUND = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        // If Player object has no PlayerData capability add one
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).isPresent()) {
                event.addCapability(new ResourceLocation(SemiHardcoreMod.MODID, "properties"), new PlayerCapabilityProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        // If a Player dies he is cloned. So this copies the capabilities to make it persistent
        if (event.isWasDeath()) {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(newStore -> {
                    newStore.copyForm(oldStore);
                    newStore.setPlayer(event.getEntity());
                });
            });

            event.getOriginal().invalidateCaps();
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient())
            return;

        if (!PlayerCapabilityCache.DEAD_PLAYERS.contains(event.player))
            return;

        /* -- Set dead player restrictions - */
        // Flags are reset every player tick somewhere in the code base,
        // therefore we have to set these flags ever tick
        Player player = event.player;
        player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(playerData -> {
            player.setInvisible(playerData.isDead());
            player.setInvulnerable(playerData.isDead());
            player.setSilent(playerData.isDead());
        });


        /* -- Calculate if dead player moves too far away -- */
        Level level = player.level();

        // If dead is bound through a soul chain, teleport him to bound player
        UUID deadPlayer = PlayerCapabilityCache.DEAD_BOUND.get(player);
        if (deadPlayer != null) { // UUID is never null, but player might be alive already
            Player bound = level.getPlayerByUUID(deadPlayer);
            if (bound != null) {
                Vec3 pos = bound.position();
                if (pos.distanceTo(player.position()) > SemiHardcoreMod.DEAD_WORLDBORDER) {
                    player.teleportTo(pos.x, pos.y, pos.z);
                    return;
                }
            }
        }

        // If dead has no bound player, teleport him to the nearest player
        Vec3 playerPos = player.position();
        Vec3 nearestPos = null;
        double nearestDistance = Double.MAX_VALUE;

        List<? extends Player> players = level.players();
        for (Player check : players) {
            if (check == player)
                continue;

            if (PlayerCapabilityCache.DEAD_PLAYERS.contains(check))
                continue;

            double distance = playerPos.distanceTo(check.position());
            if (distance > SemiHardcoreMod.DEAD_WORLDBORDER) {
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestPos = check.position();
                }
            } else {
                return; // Dead player is within radius of a player
            }
        }

        if (nearestPos != null) {
            player.teleportTo(nearestPos.x, nearestPos.y, nearestPos.z);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeGamemode(PlayerEvent.PlayerChangeGameModeEvent event) { // Gamemode muss zweimal gewechselt wetrden??
        event.getEntity().getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                .ifPresent(data -> data.setIsDead(false));
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                .ifPresent(data -> data.setPlayer(player));

        Runnable runnable = PLAYER_ACTION_BOUND.get(player.getUUID());
        if (runnable != null) {
            runnable.run();
            PLAYER_ACTION_BOUND.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractionEvent(PlayerInteractEvent event) {
        if (!PlayerCapabilityCache.DEAD_PLAYERS.contains(event.getEntity()))
            return;

        // Not every interaction is cancelable
        if (!event.isCancelable())
            return;

        // Disables every interaction when a player is dead
        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onEntityItemPickUpEvent(EntityItemPickupEvent event) {
        // Disables item picking, when player is dead
        if (PlayerCapabilityCache.DEAD_PLAYERS.contains(event.getEntity())) {
            event.setCanceled(true);
            event.setResult(Event.Result.DENY);

            return;
        }

        // Item has no method getting called when a Player
        // picks up the item, so we create or own
        ItemStack stack = event.getItem().getItem();
        if (stack.getItem() instanceof SoulChainItem soulChain) {
            soulChain.onPickUpByPlayer(stack, event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Spawn soul item on player death
            Level level = player.level();
            ItemStack itemStack = new ItemStack(ModItems.SOUL.get());
            BlockPos pos = event.getEntity().blockPosition();
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);

            level.addFreshEntity(itemEntity);

            // Update dead flag and count in player dataset
            player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> {
                data.increaseDeathCount();
                data.setIsDead(data.getDeathCount() >= SemiHardcoreMod.LIVES);
            });

            // The method Item.onDroppedByPlayer() is not called on player death, even though the item will be dropped.
            // So we issue a call to onDroppedByPlayer(), to unbind the players
            NonNullList<ItemStack> i = player.getInventory().items;
            for (ItemStack stack : i) {
                Item current = stack.getItem();
                if (current instanceof SoulChainItem soulChain) {
                    soulChain.onDroppedByDead(stack, player);
                }
            }
        }
    }
}
