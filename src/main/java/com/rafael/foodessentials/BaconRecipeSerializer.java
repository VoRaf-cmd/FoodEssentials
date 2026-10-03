package com.rafael.foodessentials;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class BaconRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
        implements IRecipeSerializer<BaconRecipe> {

    @Override
    public BaconRecipe read(ResourceLocation recipeId, JsonObject json) {
        return new BaconRecipe(recipeId);
    }

    @Override
    public BaconRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return new BaconRecipe(recipeId);
    }

    @Override
    public void write(PacketBuffer buffer, BaconRecipe recipe) {
    }
}
