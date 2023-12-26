package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.BrokkosMod;
import com.github.brokko.semihardcore.block.ReviveBeaconBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BrokkosMod.MODID);

    public static final RegistryObject<Block> REVIVE_BEACON = BLOCKS.register("revive_beacon",
            () -> new ReviveBeaconBlock(BlockBehaviour.Properties.of()
                    .lightLevel(value -> 5)
                    .strength(0.3f)
                    .explosionResistance(0f)
                    .mapColor(MapColor.STONE)));
}
