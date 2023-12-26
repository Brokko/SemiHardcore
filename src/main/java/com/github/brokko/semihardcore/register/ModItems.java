package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.BrokkosMod;
import com.github.brokko.semihardcore.item.KnifeItem;
import com.github.brokko.semihardcore.item.ReviveBeaconBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BrokkosMod.MODID);

    public static final RegistryObject<Item> SOUL = ITEMS.register("soul",
            () -> new Item(new Item.Properties()
                    .fireResistant()
                    .stacksTo(16)));

    public static final RegistryObject<Item> KNIFE = ITEMS.register("knife",
            () -> new KnifeItem(new Item.Properties()
                    .setNoRepair()
                    .stacksTo(1)));

    public static final RegistryObject<Item> KNIFE_BLOODY = ITEMS.register("knife_bloody",
            () -> new KnifeItem(new Item.Properties()
                    .setNoRepair()
                    .stacksTo(1)));

    public static final RegistryObject<Item> REVIVE_BEACON = ITEMS.register("revive_beacon",
            () -> new ReviveBeaconBlockItem(ModBlocks.REVIVE_BEACON.get(), new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.COMMON)));
}
