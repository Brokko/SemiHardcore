package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import com.github.brokko.semihardcore.block.ReviveBeaconBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SemiHardcoreMod.MODID);

    public static final RegistryObject<BlockEntityType<ReviveBeaconBlock.ReviveBeaconEntity>> REVIVE_BEACON_ENTITY =
            BLOCK_ENTITIES.register("revive_beacon_entity", () -> BlockEntityType.Builder
                    .of(ReviveBeaconBlock.ReviveBeaconEntity::new, ModBlocks.REVIVE_BEACON.get())
                    .build(null));
}
