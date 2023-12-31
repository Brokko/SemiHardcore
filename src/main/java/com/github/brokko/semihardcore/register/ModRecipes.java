package com.github.brokko.semihardcore.register;

import com.github.brokko.semihardcore.SemiHardcoreMod;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, SemiHardcoreMod.MODID);

    public static final RegistryObject<RecipeSerializer<?>> REVIVE_BEACON = RECIPES.register("custom_recipe", ShapedRecipe.Serializer::new);
}
