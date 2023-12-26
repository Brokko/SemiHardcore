package com.github.brokko.semihardcore.events;

import com.github.brokko.semihardcore.BrokkosMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BrokkosMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)

public class ClientEvent {

}
