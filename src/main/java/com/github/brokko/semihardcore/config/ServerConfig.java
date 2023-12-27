package com.github.brokko.semihardcore.config;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

class ServerConfig {
    final ForgeConfigSpec.BooleanValue serverBoolean;
    final ForgeConfigSpec.ConfigValue<ArrayList<String>> serverStringList;
    final ForgeConfigSpec.ConfigValue<DyeColor> serverEnumDyeColor;

    protected ServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        serverBoolean = builder
                .comment("An example boolean in the server config")
                .translation(SemiHardcoreMod.MODID + ".config.serverBoolean")
                .define("serverBoolean", true);
        serverStringList = builder
                .comment("An example list of Strings in the server config")
                .translation(SemiHardcoreMod.MODID + ".config.serverStringList")
                .define("serverStringList", new ArrayList<>());
        serverEnumDyeColor = builder
                .comment("An example enum DyeColor in the server config")
                .translation(SemiHardcoreMod.MODID + ".config.serverEnumDyeColor")
                .defineEnum("serverEnumDyeColor", DyeColor.WHITE);

        builder.pop();
    }
}