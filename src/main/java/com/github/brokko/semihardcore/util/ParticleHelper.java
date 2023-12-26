package com.github.brokko.semihardcore.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class ParticleHelper {

    public static void spawnCustomParticlesAroundBlock(Level world, BlockPos centerPos, int radius, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            double offsetX = world.random.nextFloat() * 2.0 * radius - radius;
            double offsetY = world.random.nextFloat() * 2.0 * radius - radius;
            double offsetZ = world.random.nextFloat() * 2.0 * radius - radius;

            double x = centerPos.getX() + 0.5 + offsetX;
            double y = centerPos.getY() + 1.0 + offsetY; // Über dem Block
            double z = centerPos.getZ() + 0.5 + offsetZ;

            world.addParticle(ParticleTypes.GLOW, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
