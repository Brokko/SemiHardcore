package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SemiHardcoreMod.MODID);

    static {
        // Defines a new creative Tab
        CREATIVE_MODE_TABS.register("revive_tab", () -> CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> ModItems.REVIVE_BEACON_BLOCKITEM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    // Adds Items to the creative tab
                    output.accept(ModItems.SOUL.get());
                    output.accept(ModItems.SOUL_CHAIN.get());
                    output.accept(ModItems.KNIFE.get());
                    output.accept(ModItems.REVIVE_BEACON_BLOCKITEM.get());
                }).build());
    }
}
