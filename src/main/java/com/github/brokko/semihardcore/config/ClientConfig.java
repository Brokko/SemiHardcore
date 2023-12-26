package com.github.brokko.semihardcore.config;

import com.github.brokko.semihardcore.BrokkosMod;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;

class ClientConfig {
    final ForgeConfigSpec.BooleanValue clientBoolean;
    final ForgeConfigSpec.ConfigValue<ArrayList<String>> clientStringList;
    final ForgeConfigSpec.EnumValue<DyeColor> clientDyeColorEnum;

    protected ClientConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("general");
        clientBoolean = builder
                .comment("An example boolean in the client config")
                .translation(BrokkosMod.MODID + ".config.clientBoolean")
                .define("clientBoolean", true);
        clientStringList = builder
                .comment("An example list of Strings in the client config")
                .translation(BrokkosMod.MODID + ".config.clientStringList")
                .define("clientStringList", new ArrayList<>());
        clientDyeColorEnum = builder
                .comment("An example DyeColor enum in the client config")
                .translation(BrokkosMod.MODID + ".config.clientDyeColorEnum")
                .defineEnum("clientDyeColorEnum", DyeColor.WHITE);

        builder.pop();
    }
}