package com.github.brokko.semihardcore.item;

import com.github.brokko.semihardcore.register.ModItems;
import com.github.brokko.semihardcore.util.InventoryHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class KnifeItem extends Item {

    public KnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        // Check if player is looking at a block to prevent interaction blocking
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
        if (hitResult.getType() == HitResult.Type.BLOCK)
            return InteractionResultHolder.fail(stack);

        // Replace knife_bloody with knife
        InventoryHelper.replace(player, stack, new ItemStack(ModItems.KNIFE_BLOODY.get()));

        // Damage player to 0.5f health
        player.hurt(player.damageSources().cramming(), player.getHealth() - 0.5f);

        return InteractionResultHolder.success(stack);
    }
}
