package com.rafael.foodessentials;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class CheeseMakerTileEntity
        extends TileEntity
        implements ITickableTileEntity, INamedContainerProvider {

    // =========================================================
    // INVENTÁRIO
    // =========================================================

    // 0 = leite
    // 1 = fermento
    // 2 = queijo

    private final ItemStackHandler inventory =
            new ItemStackHandler(3) {

                @Override
                protected void onContentsChanged(int slot) {
                    markDirty();
                }

                @Override
                public boolean isItemValid(
                        int slot,
                        @Nonnull ItemStack stack) {

                    // Leite
                    if (slot == 0) {
                        return stack.getItem()
                                == Items.MILK_BUCKET;
                    }

                    // Fermento
                    if (slot == 1) {
                        return stack.getItem()
                                == Items.RED_MUSHROOM
                                || stack.getItem()
                                == Items.BROWN_MUSHROOM
                                || stack.getItem()
                                == Items.NETHER_WART;
                    }

                    // Slot de saída
                    return false;
                }
            };


    // =========================================================
    // FUNIS
    // =========================================================

    // Cima = leite
    private final LazyOptional<IItemHandler> handlerTopo =
            LazyOptional.of(
                    () -> new RangedWrapper(
                            inventory,
                            0,
                            1
                    )
            );

    // Laterais = fermento
    private final LazyOptional<IItemHandler> handlerLados =
            LazyOptional.of(
                    () -> new RangedWrapper(
                            inventory,
                            1,
                            2
                    )
            );

    // Baixo = queijo
    private final LazyOptional<IItemHandler> handlerBaixo =
            LazyOptional.of(
                    () -> new RangedWrapper(
                            inventory,
                            2,
                            3
                    )
            );


    // =========================================================
    // PROCESSAMENTO
    // =========================================================

    private int cookTime = 0;

    // 500 ticks = 25 segundos
    // 200 ticks = 10 segundos
    private int cookTimeTotal = 500;


    // =========================================================
    // CONTROLE DO SOM
    // =========================================================

    private boolean cheeseSoundPlaying = false;


    // =========================================================
    // PORTINHA
    // =========================================================

    // 15 ticks = 0,75 segundo
    private int ejectCooldown = 0;


    // =========================================================
    // GUI
    // =========================================================

    protected final IIntArray dataAccess =
            new IIntArray() {

                @Override
                public int get(int index) {

                    switch (index) {

                        case 0:
                            return cookTime;

                        case 1:
                            return cookTimeTotal;

                        default:
                            return 0;
                    }
                }

                @Override
                public void set(
                        int index,
                        int value) {

                    if (index == 0) {
                        cookTime = value;
                    }

                    if (index == 1) {
                        cookTimeTotal = value;
                    }
                }

                @Override
                public int size() {
                    return 2;
                }
            };


    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public CheeseMakerTileEntity() {
        super(FoodEssentials.CHEESE_MAKER_TILE_ENTITY);
    }


    // =========================================================
    // TICK
    // =========================================================

    @Override
    public void tick() {

        if (world == null) {
            return;
        }

        BlockState state =
                world.getBlockState(pos);


        // =====================================================
        // SERVIDOR
        // =====================================================

        if (!world.isRemote) {

            // =================================================
            // RECEITA
            // =================================================

            CheeseMakerRecipe receita =
                    getCurrentRecipe();


            // =================================================
            // TEMPO DA RECEITA
            // =================================================

            if (receita != null) {

                cookTimeTotal =
                        receita.getCookTime();

            } else {

                cookTimeTotal = 500;
            }


            // =================================================
            // REDSTONE
            // =================================================

            boolean powered =
                    state.get(
                            CheeseMakerBlock.POWERED
                    );


            // =================================================
            // PODE TRABALHAR
            // =================================================

            boolean trabalhando =
                    powered
                            && receita != null
                            && podeFabricar();


            // =================================================
            // LIT
            // =================================================

            boolean lit =
                    state.get(
                            CheeseMakerBlock.LIT
                    );


            if (lit != trabalhando) {

                world.setBlockState(
                        pos,
                        state.with(
                                CheeseMakerBlock.LIT,
                                trabalhando
                        ),
                        3
                );

                state =
                        world.getBlockState(pos);
            }


            // =================================================
            // PROCESSAMENTO
            // =================================================

            if (trabalhando) {

                cookTime++;

                if (cookTime >= cookTimeTotal) {

                    if (fabricarQueijo()) {
                        cookTime = 0;
                    } else {
                        cookTime = 0;
                    }
                }

                markDirty();

            } else {

                if (cookTime != 0) {

                    cookTime = 0;

                    markDirty();
                }
            }


            // =================================================
            // TIMER DA PORTINHA
            // =================================================

            if (ejectCooldown > 0) {

                ejectCooldown--;

                if (ejectCooldown == 0) {

                    BlockState currentState =
                            world.getBlockState(pos);

                    if (currentState.get(
                            CheeseMakerBlock.EJECT
                    )) {

                        world.setBlockState(
                                pos,
                                currentState.with(
                                        CheeseMakerBlock.EJECT,
                                        false
                                ),
                                3
                        );

                        world.playSound(
                                null,
                                pos,
                                SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,
                                SoundCategory.BLOCKS,
                                0.5F,
                                1.2F
                        );
                    }
                }
            }
        }


        // =====================================================
        // CLIENTE
        // =====================================================

        if (world.isRemote) {

            boolean powered =
                    state.get(
                            CheeseMakerBlock.POWERED
                    );

            CheeseMakerRecipe receita =
                    getCurrentRecipe();

            boolean trabalhando =
                    powered
                            && receita != null
                            && podeFabricar();


            // =================================================
            // FABRICAÇÃO
            // =================================================

            if (trabalhando) {

                Random rand =
                        world.rand;


                // =================================================
                // FUMAÇA
                // =================================================

                if (rand.nextFloat() < 0.20F) {

                    world.addParticle(
                            ParticleTypes.CLOUD,

                            pos.getX() + 0.5D,
                            pos.getY() + 1.0D,
                            pos.getZ() + 0.5D,

                            0.0D,
                            0.05D,
                            0.0D
                    );
                }


                // =================================================
                // SOM DA FABRICAÇÃO
                // =================================================

                if (!cheeseSoundPlaying) {

                    world.playSound(
                            pos.getX() + 0.5D,
                            pos.getY() + 0.5D,
                            pos.getZ() + 0.5D,

                            FoodEssentials.CHEESE_MAKER_SOUND,

                            SoundCategory.BLOCKS,

                            0.8F,
                            1.0F,

                            false
                    );

                    cheeseSoundPlaying = true;
                }

            } else {

                // =================================================
                // PARA O SOM
                // =================================================

                if (cheeseSoundPlaying) {

                    pararSomCheeseMaker();

                    cheeseSoundPlaying = false;
                }
            }
        }
    }


    // =========================================================
    // PARAR SOM
    // =========================================================

    private void pararSomCheeseMaker() {

        if (world == null || !world.isRemote) {
            return;
        }

        net.minecraft.client.Minecraft
                .getInstance()
                .getSoundHandler()
                .stop(
                        FoodEssentials.CHEESE_MAKER_SOUND.getRegistryName(),
                        SoundCategory.BLOCKS
                );
    }


    // =========================================================
    // RECEITA ATUAL
    // =========================================================

    @Nullable
    private CheeseMakerRecipe getCurrentRecipe() {

        ItemStack fermento =
                inventory.getStackInSlot(1);

        if (fermento.isEmpty()) {
            return null;
        }

        return CheeseMakerRecipe.getRecipe(
                fermento.getItem()
        );
    }


    // =========================================================
    // PODE FABRICAR
    // =========================================================

    private boolean podeFabricar() {

        ItemStack leite =
                inventory.getStackInSlot(0);

        ItemStack fermento =
                inventory.getStackInSlot(1);

        ItemStack saida =
                inventory.getStackInSlot(2);


        // =====================================================
        // LEITE
        // =====================================================

        if (leite.isEmpty()
                || leite.getItem()
                != Items.MILK_BUCKET) {

            return false;
        }


        // =====================================================
        // FERMENTO
        // =====================================================

        CheeseMakerRecipe receita =
                CheeseMakerRecipe.getRecipe(
                        fermento.getItem()
                );

        if (receita == null) {
            return false;
        }


        // =====================================================
        // RESULTADO
        // =====================================================

        ItemStack queijo =
                receita.getResult();


        if (saida.isEmpty()) {
            return true;
        }


        if (!saida.isItemEqual(queijo)) {
            return false;
        }


        return saida.getCount()
                + queijo.getCount()
                <= saida.getMaxStackSize();
    }


    // =========================================================
    // FABRICAR
    // =========================================================

    private boolean fabricarQueijo() {

        if (world == null) {
            return false;
        }

        if (world.isRemote) {
            return false;
        }

        if (!podeFabricar()) {
            return false;
        }


        // =====================================================
        // RECEITA
        // =====================================================

        CheeseMakerRecipe receita =
                getCurrentRecipe();

        if (receita == null) {
            return false;
        }


        // =====================================================
        // ITENS
        // =====================================================

        ItemStack leite =
                inventory.getStackInSlot(0);

        ItemStack fermento =
                inventory.getStackInSlot(1);

        ItemStack saida =
                inventory.getStackInSlot(2);

        ItemStack queijo =
                receita.getResult();


        // =====================================================
        // DIREÇÃO
        // =====================================================

        BlockState state =
                world.getBlockState(pos);

        Direction frente =
                state.get(
                        CheeseMakerBlock.FACING
                );

        Direction tras =
                frente.getOpposite();


        // =====================================================
        // POSIÇÃO DO BALDE
        // =====================================================

        double x =
                pos.getX()
                        + 0.5D
                        + tras.getXOffset()
                        * 0.7D;

        double y =
                pos.getY()
                        + 0.5D;

        double z =
                pos.getZ()
                        + 0.5D
                        + tras.getZOffset()
                        * 0.7D;


        // =====================================================
        // CRIA O BALDE
        // =====================================================

        ItemEntity baldeEntity =
                new ItemEntity(
                        world,
                        x,
                        y,
                        z,
                        new ItemStack(Items.BUCKET)
                );

        baldeEntity.setMotion(
                tras.getXOffset() * 0.20D,
                0.08D,
                tras.getZOffset() * 0.20D
        );


        // =====================================================
        // CONSUME LEITE
        // =====================================================

        leite.shrink(1);


        // =====================================================
        // CONSUME FERMENTO
        // =====================================================

        fermento.shrink(1);


        // =====================================================
        // COLOCA O QUEIJO
        // =====================================================

        if (saida.isEmpty()) {

            inventory.setStackInSlot(
                    2,
                    queijo.copy()
            );

        } else {

            saida.grow(
                    queijo.getCount()
            );

            inventory.setStackInSlot(
                    2,
                    saida
            );
        }


        // =====================================================
        // ABRE A PORTINHA
        // =====================================================

        BlockState currentState =
                world.getBlockState(pos);

        if (!currentState.get(
                CheeseMakerBlock.EJECT
        )) {

            world.setBlockState(
                    pos,
                    currentState.with(
                            CheeseMakerBlock.EJECT,
                            true
                    ),
                    3
            );

            world.playSound(
                    null,
                    pos,
                    SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,
                    SoundCategory.BLOCKS,
                    0.5F,
                    1.2F
            );
        }


        // =====================================================
        // SOLTA O BALDE
        // =====================================================

        world.addEntity(
                baldeEntity
        );


        // =====================================================
        // TIMER DA PORTINHA
        // =====================================================

        ejectCooldown = 15;


        // =====================================================
        // SALVA
        // =====================================================

        markDirty();

        return true;
    }


    // =========================================================
    // FUNIS
    // =========================================================

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(
            @Nonnull Capability<T> cap,
            @Nullable Direction side) {

        if (!this.removed
                && cap
                == CapabilityItemHandler
                .ITEM_HANDLER_CAPABILITY) {

            // =================================================
            // BAIXO = QUEIJO
            // =================================================

            if (side == Direction.DOWN) {
                return handlerBaixo.cast();
            }


            // =================================================
            // CIMA = LEITE
            // =================================================

            if (side == Direction.UP) {
                return handlerTopo.cast();
            }


            // =================================================
            // LADOS = FERMENTO
            // =================================================

            return handlerLados.cast();
        }

        return super.getCapability(
                cap,
                side
        );
    }


    // =========================================================
    // REMOVE
    // =========================================================

    @Override
    public void remove() {

        /*
         * O TileEntity pode ser removido enquanto o som
         * ainda estiver tocando.
         *
         * O som é parado somente no cliente.
         */
        if (world != null
                && world.isRemote
                && cheeseSoundPlaying) {

            pararSomCheeseMaker();

            cheeseSoundPlaying = false;
        }

        super.remove();

        handlerTopo.invalidate();
        handlerLados.invalidate();
        handlerBaixo.invalidate();
    }


    // =========================================================
    // CARREGAR
    // =========================================================

    @Override
    public void read(
            CompoundNBT compound) {

        super.read(compound);


        if (compound.contains(
                "ForgeInv"
        )) {

            inventory.deserializeNBT(
                    compound.getCompound(
                            "ForgeInv"
                    )
            );
        }


        cookTime =
                compound.getInt(
                        "CookTime"
                );


        cookTimeTotal =
                compound.getInt(
                        "CookTimeTotal"
                );


        ejectCooldown =
                compound.getInt(
                        "EjectCooldown"
                );


        if (cookTimeTotal <= 0) {
            cookTimeTotal = 500;
        }


        // O estado do som não precisa ser salvo.
        cheeseSoundPlaying = false;
    }


    // =========================================================
    // SALVAR
    // =========================================================

    @Override
    public CompoundNBT write(
            CompoundNBT compound) {

        super.write(compound);


        compound.put(
                "ForgeInv",
                inventory.serializeNBT()
        );


        compound.putInt(
                "CookTime",
                cookTime
        );


        compound.putInt(
                "CookTimeTotal",
                cookTimeTotal
        );


        compound.putInt(
                "EjectCooldown",
                ejectCooldown
        );


        return compound;
    }


    // =========================================================
    // NOME
    // =========================================================

    @Override
    public ITextComponent getDisplayName() {

        return new TranslationTextComponent(
                "block.foodessentials.cheese_maker"
        );
    }


    // =========================================================
    // MENU
    // =========================================================

    @Nullable
    @Override
    public Container createMenu(
            int id,
            PlayerInventory playerInv,
            PlayerEntity player) {

        return new CheeseMakerContainer(
                id,
                playerInv,
                inventory,
                dataAccess
        );
    }
}
