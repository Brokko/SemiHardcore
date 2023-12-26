package com.github.brokko.semihardcore.item;

import com.github.brokko.semihardcore.block.ReviveBeaconBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ReviveBeaconBlockItem extends BlockItem {
    public ReviveBeaconBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack itemStack, Level level, @NotNull Player player) {
        if (level.isClientSide)
            return;

        CompoundTag nbt = itemStack.getOrCreateTag();
        nbt.putUUID("playerUUID", player.getUUID());
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        // Check if executed on server side
        if (context.getLevel().isClientSide()) {
            return InteractionResult.PASS;
        }

        super.useOn(context);

        // Calculates the position of the place Block
        BlockPos pos = context.getClickedPos().offset(context.getClickedFace().getNormal());
        Level world = context.getLevel();

        if (world.getBlockEntity(pos) instanceof ReviveBeaconBlock.ReviveBeaconEntity entity)
            entity.set(context.getPlayer().getUUID());

        return InteractionResult.SUCCESS;
    }
}
