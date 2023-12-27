package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import com.github.brokko.semihardcore.item.KnifeItem;
import com.github.brokko.semihardcore.item.ReviveBeaconBlockItem;
import com.github.brokko.semihardcore.item.SoulChainItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SemiHardcoreMod.MODID);

    public static final RegistryObject<Item> SOUL = ITEMS.register("soul",
            () -> new Item(new Item.Properties()
                    .fireResistant()
                    .stacksTo(16)));

    public static final RegistryObject<Item> SOUL_CHAIN = ITEMS.register("soul_chain",
            () -> new SoulChainItem(new Item.Properties()
                    .setNoRepair()
                    .stacksTo(1)));

    public static final RegistryObject<Item> KNIFE = ITEMS.register("knife",
            () -> new KnifeItem(new Item.Properties()
                    .setNoRepair()
                    .stacksTo(1)));

    public static final RegistryObject<Item> KNIFE_BLOODY = ITEMS.register("knife_bloody",
            () -> new Item(new Item.Properties()
                    .setNoRepair()
                    .stacksTo(1)));

    public static final RegistryObject<Item> REVIVE_BEACON_BLOCKITEM = ITEMS.register("revive_beacon",
            () -> new ReviveBeaconBlockItem(ModBlocks.REVIVE_BEACON.get(), new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.COMMON)));
}
