package com.github.brokko.semihardcore.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SoulChainItem extends Item {
    public SoulChainItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        // Check if executed on client side
        if(player.level().isClientSide) {
            return false;
        }

        if(entity instanceof Player target) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putUUID("playerUUID", target.getUUID());
        }

        return true;
    }
}
