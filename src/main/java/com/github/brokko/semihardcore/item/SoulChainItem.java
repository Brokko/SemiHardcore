package com.github.brokko.semihardcore.item;

import com.github.brokko.semihardcore.capability.PlayerCapabilityProvider;
import com.github.brokko.semihardcore.events.ModEvents;
import com.github.brokko.semihardcore.util.InventoryHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity livingEntity, @NotNull InteractionHand hand) {
        Level level = player.level();

        // Check if executed on client side
        if (level.isClientSide)
            return InteractionResult.PASS;

        if (livingEntity instanceof Player target) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.hasUUID("boundUUI")) {
                bindUUID(level, tag.getUUID("boundUUID"), UUID.randomUUID());
            }

            tag.putUUID("boundUUID", target.getUUID());
            player.setItemSlot(EquipmentSlot.MAINHAND, stack);

            target.getCapability(PlayerCapabilityProvider.PLAYER_DATA)
                    .ifPresent(data -> data.setBoundTo(player.getUUID()));

            // Rename item to contain targets name
            InventoryHelper.updateName(stack, target);

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

        // Remove the 'bound' flag of the owning player from the dataset of the tagged player
        CompoundTag tag = item.getOrCreateTag();
        if (tag.hasUUID("boundUUID")) {
            bindUUID(level, tag.getUUID("boundUUID"), UUID.randomUUID());
        }

        return super.onDroppedByPlayer(item, player);
    }

    /**
     * The method onEntityItemPickUpEvent() is not called when the player dies, even
     * though the item is dropped. Therefore, we need to construct the
     * event ourselves using the EntityItemPickupEvent of the event bus.
     * <p>
     * This method is always called from server side!
     * <p>
     * (Should be encapsulated in an interface in the future)
     */
    public void onDroppedByDead(ItemStack stack, Player player) {
        onDroppedByPlayer(stack, player);
    }

    /**
     * The Item class does not contain a method that is called when the player dies and
     * the ItemStack is dropped from their inventory. Therefore, we need to construct the
     * event ourselves using the EntityItemPickupEvent of the event bus.
     * <p>
     * This method is always called from server side!
     * <p>
     * (Should be encapsulated in an interface in the future)
     */
    public void onPickUpByPlayer(ItemStack stack, Player player) {
        // Set the 'bound' flag of the tagged player to the player who picked up the item
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.hasUUID("boundUUID")) {
            bindUUID(player.level(), tag.getUUID("boundUUID"), player.getUUID());
        }

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
