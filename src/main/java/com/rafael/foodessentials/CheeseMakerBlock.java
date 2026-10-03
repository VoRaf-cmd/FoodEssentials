package com.rafael.foodessentials;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CheeseMakerBlock extends Block {

    public static final BooleanProperty POWERED =
            BlockStateProperties.POWERED;

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    public static final BooleanProperty LIT =
            BlockStateProperties.LIT;

    public static final BooleanProperty EJECT =
            BooleanProperty.create("eject");


    public CheeseMakerBlock(Properties properties) {

        // Picareta de madeira ou melhor
        super(properties
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(0)
        );

        this.setDefaultState(
                this.stateContainer.getBaseState()
                        .with(POWERED, false)
                        .with(FACING, Direction.NORTH)
                        .with(LIT, false)
                        .with(EJECT, false)
        );
    }


    @Override
    public boolean onBlockActivated(
            BlockState state,
            World worldIn,
            BlockPos pos,
            PlayerEntity player,
            Hand handIn,
            BlockRayTraceResult hit) {

        if (!worldIn.isRemote) {

            TileEntity tileEntity =
                    worldIn.getTileEntity(pos);

            if (tileEntity instanceof CheeseMakerTileEntity) {

                NetworkHooks.openGui(
                        (ServerPlayerEntity) player,
                        (INamedContainerProvider) tileEntity,
                        pos
                );
            }
        }

        return true;
    }


    @Override
    public void neighborChanged(
            BlockState state,
            World worldIn,
            BlockPos pos,
            Block blockIn,
            BlockPos fromPos,
            boolean isMoving) {

        if (!worldIn.isRemote) {

            boolean powered =
                    worldIn.isBlockPowered(pos);

            if (powered != state.get(POWERED)) {

                worldIn.setBlockState(
                        pos,
                        state.with(
                                POWERED,
                                powered
                        ),
                        3
                );
            }
        }
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(
            BlockItemUseContext context) {

        return this.getDefaultState()
                .with(
                        FACING,
                        context.getPlacementHorizontalFacing()
                                .getOpposite()
                )
                .with(
                        POWERED,
                        context.getWorld()
                                .isBlockPowered(context.getPos())
                )
                .with(LIT, false)
                .with(EJECT, false);
    }


    @Override
    protected void fillStateContainer(
            StateContainer.Builder<Block, BlockState> builder) {

        builder.add(
                POWERED,
                FACING,
                LIT,
                EJECT
        );
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(
            BlockState state,
            IBlockReader world) {

        return new CheeseMakerTileEntity();
    }
}