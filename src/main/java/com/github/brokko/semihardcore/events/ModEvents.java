package com.github.brokko.semihardcore.events;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import com.github.brokko.semihardcore.capability.PlayerCapabilityProvider;
import com.github.brokko.semihardcore.capability.PlayerData;
import com.github.brokko.semihardcore.register.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SemiHardcoreMod.MODID, value = Dist.DEDICATED_SERVER, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    public static final List<Player> DEAD_PLAYERS = new LinkedList<>();

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        // If Player has no PlayerData capability add one
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

                    if (newStore.isDead()) {
                        DEAD_PLAYERS.remove(event.getOriginal());
                        DEAD_PLAYERS.add(event.getEntity());
                    }
                });
            });

            event.getOriginal().invalidateCaps();
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

            // Increase death count
            player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> {
                data.increaseDeathCount();
                data.setIsDead(data.getDeathCount() >= SemiHardcoreMod.LIVES);

                // Forbids visible effects other Player could see (resets on game mode change)
                if (data.isDead()) {
                    DEAD_PLAYERS.add(player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        if (!DEAD_PLAYERS.contains(event.player))
            return;

        Player player = event.player;
        player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> {
            if (data.isDead()) {
                player.setInvisible(data.isDead());
                player.setInvulnerable(true);
                player.setSilent(true);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerChangeGamemode(PlayerEvent.PlayerChangeGameModeEvent event) {
        // Set "isDead" flag to false
        event.getEntity().getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> data.setIsDead(false));
    }

    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> {
            if (data.isDead()) {
                DEAD_PLAYERS.add(player);
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        DEAD_PLAYERS.remove(event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityItemPickUpEvent(EntityItemPickupEvent event) {
        if (!DEAD_PLAYERS.contains(event.getEntity()))
            return;

        // Disables item picking, when player is dead
        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onPlayerInteractionEvent(PlayerInteractEvent event) {
        if (!DEAD_PLAYERS.contains(event.getEntity()))
            return;

        // Disables every interaction when a player is dead
        event.setCanceled(true);
        event.setResult(Event.Result.DENY);
    }
}
