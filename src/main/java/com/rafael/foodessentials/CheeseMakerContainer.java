package com.rafael.foodessentials;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CheeseMakerContainer extends Container {

    private final IItemHandler cheeseMakerInventory;
    private final IIntArray data;


    // =========================================================
    // CONSTRUTOR CLIENTE
    // =========================================================

    public CheeseMakerContainer(
            int id,
            PlayerInventory playerInventory) {

        this(
                id,
                playerInventory,
                new ItemStackHandler(3),
                new IntArray(2)
        );
    }


    // =========================================================
    // CONSTRUTOR PRINCIPAL
    // =========================================================

    public CheeseMakerContainer(
            int id,
            PlayerInventory playerInventory,
            IItemHandler slots,
            IIntArray data) {

        super(
                FoodEssentials.CHEESE_MAKER_CONTAINER,
                id
        );

        this.cheeseMakerInventory = slots;
        this.data = data;


        // =====================================================
        // CHEESE MAKER
        // =====================================================

        // Slot 0 = Leite
        this.addSlot(
                new SlotItemHandler(
                        slots,
                        0,
                        56,
                        17
                )
        );


        // Slot 1 = Fermento
        this.addSlot(
                new SlotItemHandler(
                        slots,
                        1,
                        56,
                        53
                )
        );


        // Slot 2 = Queijo
        this.addSlot(
                new SlotItemHandler(
                        slots,
                        2,
                        116,
                        35
                )
        );


        // =====================================================
        // INVENTÁRIO DO JOGADOR
        // =====================================================

        for (int i = 0; i < 3; ++i) {

            for (int j = 0; j < 9; ++j) {

                this.addSlot(
                        new Slot(
                                playerInventory,
                                j + i * 9 + 9,
                                8 + j * 18,
                                84 + i * 18
                        )
                );
            }
        }


        // Hotbar
        for (int k = 0; k < 9; ++k) {

            this.addSlot(
                    new Slot(
                            playerInventory,
                            k,
                            8 + k * 18,
                            142
                    )
            );
        }


        this.trackIntArray(data);
    }


    // =========================================================
    // PROGRESSO
    // =========================================================

    public int getCookProgressionScaled() {

        int cookTime =
                this.data.get(0);

        int cookTimeTotal =
                this.data.get(1);


        if (cookTimeTotal <= 0) {
            return 0;
        }


        return cookTime * 24 / cookTimeTotal;
    }


    // =========================================================
    // TEMPO
    // =========================================================

    public int getCookTime() {
        return this.data.get(0);
    }


    public int getCookTimeTotal() {
        return this.data.get(1);
    }


    // =========================================================
    // INTERAÇÃO
    // =========================================================

    @Override
    public boolean canInteractWith(
            PlayerEntity playerIn) {

        return true;
    }


    // =========================================================
    // SHIFT CLICK
    // =========================================================

    @Override
    public ItemStack transferStackInSlot(
            PlayerEntity playerIn,
            int index) {

        ItemStack itemstack =
                ItemStack.EMPTY;


        Slot slot =
                this.inventorySlots.get(index);


        if (slot == null
                || !slot.getHasStack()) {

            return ItemStack.EMPTY;
        }


        ItemStack itemstack1 =
                slot.getStack();


        itemstack =
                itemstack1.copy();


        // =====================================================
        // SAÍDA
        // =====================================================

        if (index == 2) {

            if (!this.mergeItemStack(
                    itemstack1,
                    3,
                    39,
                    true)) {

                return ItemStack.EMPTY;
            }

            slot.onSlotChange(
                    itemstack1,
                    itemstack
            );
        }


        // =====================================================
        // ENTRADAS
        // =====================================================

        else if (index == 0
                || index == 1) {

            if (!this.mergeItemStack(
                    itemstack1,
                    3,
                    39,
                    false)) {

                return ItemStack.EMPTY;
            }
        }


        // =====================================================
        // INVENTÁRIO DO JOGADOR
        // =====================================================

        else {

            // Leite
            if (itemstack1.getItem()
                    == Items.MILK_BUCKET) {

                if (!this.mergeItemStack(
                        itemstack1,
                        0,
                        1,
                        false)) {

                    return ItemStack.EMPTY;
                }
            }


            // Ingredientes
            else if (
                    itemstack1.getItem()
                            == Items.RED_MUSHROOM
                            || itemstack1.getItem()
                            == Items.BROWN_MUSHROOM
                            || itemstack1.getItem()
                            == Items.NETHER_WART) {

                if (!this.mergeItemStack(
                        itemstack1,
                        1,
                        2,
                        false)) {

                    return ItemStack.EMPTY;
                }
            }


            // Item não aceito
            else {

                return ItemStack.EMPTY;
            }
        }


        // =====================================================
        // ATUALIZA SLOT
        // =====================================================

        if (itemstack1.isEmpty()) {

            slot.putStack(
                    ItemStack.EMPTY
            );

        } else {

            slot.onSlotChanged();
        }


        if (itemstack1.getCount()
                == itemstack.getCount()) {

            return ItemStack.EMPTY;
        }


        slot.onTake(
                playerIn,
                itemstack1
        );


        return itemstack;
    }
}
