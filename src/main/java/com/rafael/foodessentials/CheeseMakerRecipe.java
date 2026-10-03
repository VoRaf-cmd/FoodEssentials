package com.rafael.foodessentials;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CheeseMakerRecipe {

    private final Item ingredient;
    private final int cookTime;

    public CheeseMakerRecipe(Item ingredient, int cookTime) {
        this.ingredient = ingredient;
        this.cookTime = cookTime;
    }

    public Item getIngredient() {
        return ingredient;
    }

    public int getCookTime() {
        return cookTime;
    }

    public ItemStack getResult() {
        return new ItemStack(FoodEssentials.CHEESE_BLOCK);
    }


    // =========================================================
    // RECEITAS
    // =========================================================

    // Leite + Red Mushroom = Queijo
    public static final CheeseMakerRecipe RED_MUSHROOM =
            new CheeseMakerRecipe(
                    Items.RED_MUSHROOM,
                    500
            );


    // Leite + Brown Mushroom = Queijo
    public static final CheeseMakerRecipe BROWN_MUSHROOM =
            new CheeseMakerRecipe(
                    Items.BROWN_MUSHROOM,
                    500
            );


    // Leite + Nether Wart = Queijo
    // 200 ticks = 10 segundos
    public static final CheeseMakerRecipe NETHER_WART =
            new CheeseMakerRecipe(
                    Items.NETHER_WART,
                    200
            );


    public static final CheeseMakerRecipe[] RECIPES = {
            RED_MUSHROOM,
            BROWN_MUSHROOM,
            NETHER_WART
    };


    // =========================================================
    // ENCONTRA RECEITA
    // =========================================================

    public static CheeseMakerRecipe getRecipe(Item item) {

        for (CheeseMakerRecipe recipe : RECIPES) {

            if (recipe.ingredient == item) {
                return recipe;
            }
        }

        return null;
    }
}
