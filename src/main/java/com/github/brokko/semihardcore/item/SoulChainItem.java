package com.github.brokko.semihardcore.item;

import com.github.brokko.semihardcore.capability.PlayerCapabilityProvider;
import com.github.brokko.semihardcore.events.ModEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SoulChainItem extends Item {
    public SoulChainItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity livingEntity, @NotNull InteractionHand hand) {
        Level level = player.level();

        // Check if executed on client side
        if (level.isClientSide)
            return InteractionResult.PASS;

        if (livingEntity instanceof Player target) {
            CompoundTag tag = stack.getOrCreateTag();
            if(tag.hasUUID("boundUUI")) {
                bindUUID(level, tag.getUUID("boundUUID"), UUID.randomUUID());
            }

            tag.putUUID("boundUUID", target.getUUID());
            player.setItemSlot(EquipmentSlot.MAINHAND, stack);

            target.getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                    .ifPresent(data -> data.setBoundTo(player.getUUID()));

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        Level level = player.level();

        // Check if executed on client side
        if (level.isClientSide)
            return false;

        CompoundTag tag = item.getOrCreateTag();
        if(tag.hasUUID("boundUUID")) {
            bindUUID(level, tag.getUUID("boundUUID"), UUID.randomUUID());
        }

        return super.onDroppedByPlayer(item, player);
    }

    private void bindUUID(Level level, UUID deadPlayer, UUID owningPlayer) {
        // Player object from bound player cant be obtained when player is offline, so we
        // create a Runnable containing the operation and try to execute while PlayerLoggedInEvent
        Player boundPlayer = level.getPlayerByUUID(deadPlayer);
        if (boundPlayer != null) {
            boundPlayer.getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                    .ifPresent(data -> data.setBoundTo(owningPlayer));
        } else {
            ModEvents.PLAYER_ACTION_BOUND.put(deadPlayer, () -> {
                Player dead = level.getPlayerByUUID(deadPlayer);
                dead.getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                        .ifPresent(data -> data.setBoundTo(owningPlayer));
            });
        }
    }
}
