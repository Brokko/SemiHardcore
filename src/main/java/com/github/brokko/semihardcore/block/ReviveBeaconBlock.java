package com.github.brokko.semihardcore.block;

import com.github.brokko.semihardcore.capability.PlayerCapabilityProvider;
import com.github.brokko.semihardcore.register.ModBlockEntity;
import com.github.brokko.semihardcore.register.ModItems;
import com.github.brokko.semihardcore.util.InventoryHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ReviveBeaconBlock extends Block implements EntityBlock {
    private static final Block[][][] ID_MATRIX = new Block[3][3][3];

    static {
        ID_MATRIX[0][0][0] = Blocks.IRON_BLOCK;
        ID_MATRIX[0][0][1] = Blocks.GOLD_BLOCK;
        ID_MATRIX[0][0][2] = Blocks.IRON_BLOCK;

        ID_MATRIX[0][1][0] = Blocks.DIAMOND_BLOCK;
        ID_MATRIX[0][1][2] = Blocks.DIAMOND_BLOCK;

        ID_MATRIX[1][0][1] = Blocks.GOLD_BLOCK;
        ID_MATRIX[1][0][0] = Blocks.GOLD_BLOCK;
        ID_MATRIX[1][0][2] = Blocks.GOLD_BLOCK;

        ID_MATRIX[1][2][1] = Blocks.ENCHANTING_TABLE;

        ID_MATRIX[2][0][0] = Blocks.IRON_BLOCK;
        ID_MATRIX[2][0][1] = Blocks.GOLD_BLOCK;
        ID_MATRIX[2][0][2] = Blocks.IRON_BLOCK;

        ID_MATRIX[2][1][0] = Blocks.DIAMOND_BLOCK;
        ID_MATRIX[2][1][2] = Blocks.DIAMOND_BLOCK;
    }

    public ReviveBeaconBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        // Check if executed on server side
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Block current = level.getBlockState(pos.offset(xOffset, yOffset, zOffset)).getBlock();
                    if (ID_MATRIX[xOffset + 1][yOffset + 1][zOffset + 1] == null) {
                        // Check if anything other than air is placed
                        if (current != Blocks.AIR && current != this) {
                            return InteractionResult.FAIL;
                        }

                        continue;
                    }

                    // Check if defined blocks are place
                    if (current != ID_MATRIX[xOffset + 1][yOffset + 1][zOffset + 1]) {
                        return InteractionResult.FAIL;
                    }
                }
            }
        }

        ItemStack item = player.getItemInHand(hand);
        if (!item.is(ModItems.KNIFE_BLOODY.get())) {
            return InteractionResult.FAIL;
        }

        ReviveBeaconEntity entity = (ReviveBeaconEntity) level.getBlockEntity(pos);
        if (entity == null) {
            return InteractionResult.FAIL;
        }

        // If .getPlayer(entity.get()); == null player is not online and can't be revived
        // .getPlayerList(); is never null while called on server side
        Player toRevive = level.getServer().getPlayerList().getPlayer(entity.get());
        if (toRevive == null) {
            destroyBlocks(level, pos, true);
            return InteractionResult.SUCCESS;
        }

        toRevive.getCapability(PlayerCapabilityProvider.PLAYER_DATA).ifPresent(data -> {
            destroyBlocks(level, pos, !data.isDead());

            if (!data.isDead())
                return;

            // Replace knife_bloody with knife
            InventoryHelper.replace(player, item, new ItemStack(ModItems.KNIFE.get()));

            // Set placer position on top of beacon
            toRevive.teleportTo(pos.getX(), pos.getY() + 1, pos.getZ());

            // Allow player visible interactions
            toRevive.setInvisible(false);
            toRevive.setInvulnerable(false);
            toRevive.setSilent(false);

            // Set "isDead" flag to false, allowing block interaction
            data.setIsDead(false);
        });

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ReviveBeaconEntity(pos, state);
    }

    private void destroyBlocks(Level level, BlockPos pos, boolean dropItems) {
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    BlockPos position = pos.offset(xOffset, yOffset, zOffset);

                    // Don't destroy ReviveBeacon block
                    Block current = level.getBlockState(position).getBlock();
                    if (current == this)
                        continue;

                    // Destroy and drop all blocks on error or drop nothing on revive
                    level.destroyBlock(position, dropItems);
                }
            }
        }
    }

    public static class ReviveBeaconEntity extends BlockEntity {
        private static final String IDENTIFIER = "semihardcore";

        private CompoundTag compoundTag = new CompoundTag();

        public ReviveBeaconEntity(BlockPos blockPos, BlockState blockState) {
            super(ModBlockEntity.REVIVE_BEACON_ENTITY.get(), blockPos, blockState);
        }

        /*
         * Called when LevelChunk containing the Block is loaded
         * Super method has to be called
         * Node: The tag names `id`, `x`, `y`, `z`, `ForgeData` and `ForgeCaps` are reserved by the `super` methods.
         */
        @Override
        public void saveAdditional(CompoundTag tag) {
            tag.put(IDENTIFIER, compoundTag);
            super.saveAdditional(tag);
        }

        /*
         * Called when LevelChunk containing the Block is loaded
         * Super method has to be called
         */
        @Override
        public void load(@NotNull CompoundTag tag) {
            super.load(tag);
            compoundTag = tag.getCompound(IDENTIFIER);
        }

        /*
         * Creates CompoundTag to containing all custom data
         */
        @Override
        public @NotNull CompoundTag getUpdateTag() {
            CompoundTag tag = super.getUpdateTag();
            tag.put(IDENTIFIER, compoundTag);
            return tag;
        }

        /*
         * Called to sync client and server
         * Will get tag from #getUpdateTag
         */
        @Override
        public Packet<ClientGamePacketListener> getUpdatePacket() {
            return ClientboundBlockEntityDataPacket.create(this);
        }

        @Override
        public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
            CompoundTag tag = pkt.getTag();
            if (tag.contains(IDENTIFIER))
                compoundTag = tag.getCompound(IDENTIFIER);
        }

        public void set(UUID uuid) {
            compoundTag.putUUID("playerUUID", uuid);
        }

        public @NotNull UUID get() {
            return compoundTag.getUUID("playerUUID");
        }
    }
}
