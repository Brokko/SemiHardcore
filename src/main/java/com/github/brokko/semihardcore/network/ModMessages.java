package com.github.brokko.semihardcore.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

/**
 * Only used to send particle information to clients. Other communications like {@link com.github.brokko.semihardcore.capability.PlayerCapabilityProvider}
 * or {@link com.github.brokko.semihardcore.block.ReviveBeaconBlock.ReviveBeaconEntity} are handled by Minecraft itself
 * via method overrides
 */
public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }


}
