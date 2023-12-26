package com.github.brokko.semihardcore;

import com.github.brokko.semihardcore.config.ConfigHolder;
import com.github.brokko.semihardcore.events.ClientEvent;
import com.github.brokko.semihardcore.events.ModEvents;
import com.github.brokko.semihardcore.register.*;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BrokkosMod.MODID)
public class BrokkosMod {
    public static final String MODID = "semihardcore";
    private static final Logger LOGGER = LogUtils.getLogger();

    public BrokkosMod() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Deferred Registers
        ModItems.ITEMS.register(modEventBus);
        ModRecipe.RECIPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntity.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);

        // TODO später
        // Drops on explosion
        // Knife damages player
        // Partikel bei revive
        // config: start leben und messer schaden
        // Leben in tab liste oder buch anzeigen

        // Register Configs // TODO Nutzen im schaden von messer zu regulieren
        modLoadingContext.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        modLoadingContext.registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);

        // Register events annotated with @SubscribeEvent
        MinecraftForge.EVENT_BUS.register(ClientEvent.class);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);
    }
}
