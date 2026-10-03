package com.rafael.foodessentials;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BaconRecipe implements ICraftingRecipe {

    private final ResourceLocation id;

    public BaconRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        boolean hasPorkchop = false;
        boolean hasKnife = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.isEmpty()) {
                continue;
            }

            if (stack.getItem() == Items.PORKCHOP && !hasPorkchop) {
                hasPorkchop = true;
            }
            else if (stack.getItem() == FoodEssentials.KNIFE && !hasKnife) {
                hasKnife = true;
            }
            else {
                return false;
            }
        }

        return hasPorkchop && hasKnife;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        return new ItemStack(FoodEssentials.BACON, 4);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(FoodEssentials.BACON, 4);
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return FoodEssentials.BACON_RECIPE_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.CRAFTING;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInventory inv) {
        NonNullList<ItemStack> remaining =
                NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack.getItem() == FoodEssentials.KNIFE) {

                ItemStack knife = stack.copy();

                if (knife.getDamage() + 1 < knife.getMaxDamage()) {
                    knife.setDamage(knife.getDamage() + 1);
                    remaining.set(i, knife);
                }
            }
        }

        return remaining;
    }
}
