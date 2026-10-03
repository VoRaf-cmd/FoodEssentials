    package com.rafael.foodessentials;
    
    import com.google.common.collect.ImmutableSet;
    
    import net.minecraft.block.Block;
    import net.minecraft.block.SoundType;
    import net.minecraft.block.material.Material;
    import net.minecraft.entity.merchant.villager.VillagerProfession;
    import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
    import net.minecraft.inventory.container.ContainerType;
    import net.minecraft.item.BlockItem;
    import net.minecraft.item.Food;
    import net.minecraft.item.Item;
    import net.minecraft.item.ItemGroup;
    import net.minecraft.item.ItemStack;
    import net.minecraft.item.Items;
    import net.minecraft.item.MerchantOffer;
    import net.minecraft.item.SwordItem;
    import net.minecraft.item.ItemTier;
    import net.minecraft.item.crafting.IRecipeSerializer;
    import net.minecraft.tileentity.TileEntityType;
    import net.minecraft.util.ResourceLocation;
    import net.minecraft.util.SoundEvent;
    import net.minecraft.util.SoundEvents;
    import net.minecraft.village.PointOfInterestType;
    
    import net.minecraftforge.common.MinecraftForge;
    import net.minecraftforge.common.extensions.IForgeContainerType;
    import net.minecraftforge.event.RegistryEvent;
    import net.minecraftforge.event.village.VillagerTradesEvent;
    import net.minecraftforge.eventbus.api.SubscribeEvent;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
    import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
    import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
    
    import java.lang.reflect.Constructor;
    import java.lang.reflect.Method;
    import java.util.List;
    import java.util.Set;
    
    @Mod(FoodEssentials.MOD_ID)
    public class FoodEssentials {
    
        public static final String MOD_ID = "foodessentials";
    
    
        // =========================================================
        // ITENS
        // =========================================================
    
        public static final Item KNIFE = new SwordItem(
                ItemTier.IRON,
                1,
                -1.8F,
                new Item.Properties().group(ItemGroup.COMBAT)
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "knife")
        );
    
    
        public static final Item HAMBURGER = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(10)
                                .saturation(1.0F)
                                .meat()
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "hamburger")
        );
    
    
        public static final Item CHEESEBURGER = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(12)
                                .saturation(1.2F)
                                .meat()
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "cheeseburger")
        );
    
    
        public static final Item BACONBURGER = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(14)
                                .saturation(1.3F)
                                .meat()
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "baconburger")
        );
    
    
        public static final Item CHEESE_SLICE = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(2)
                                .saturation(0.3F)
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "cheese_slice")
        );
    
    
        public static final Item COOKED_BACON = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(6)
                                .saturation(0.8F)
                                .meat()
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "cooked_bacon")
        );
    
    
        public static final Item BACON = new Item(
                new Item.Properties()
                        .group(ItemGroup.FOOD)
                        .food(new Food.Builder()
                                .hunger(2)
                                .saturation(0.2F)
                                .meat()
                                .build())
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "bacon")
        );
    
    
        // =========================================================
        // BLOCOS
        // =========================================================
    
        public static final Block CHEESE_BLOCK = new Block(
                Block.Properties.create(Material.CAKE)
                        .hardnessAndResistance(0.5F)
                        .sound(SoundType.CLOTH)
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "cheese_block")
        );
    
    
        public static final Block CHEESE_MAKER = new CheeseMakerBlock(
                Block.Properties.create(Material.WOOD)
                        .hardnessAndResistance(2.0F)
                        .sound(SoundType.WOOD)
        ).setRegistryName(
                new ResourceLocation(MOD_ID, "cheese_maker")
        );
    
    
        // =========================================================
        // SOM DO CHEESE MAKER
        // =========================================================
    
        public static final SoundEvent CHEESE_MAKER_SOUND =
                new SoundEvent(
                        new ResourceLocation(
                                MOD_ID,
                                "cheese_maker"
                        )
                ).setRegistryName(
                        new ResourceLocation(
                                MOD_ID,
                                "cheese_maker"
                        )
                );
    
    
        // =========================================================
        // REGISTROS
        // =========================================================
    
        public static TileEntityType<CheeseMakerTileEntity> CHEESE_MAKER_TILE_ENTITY;
    
        public static ContainerType<CheeseMakerContainer> CHEESE_MAKER_CONTAINER;
    
        public static BaconRecipeSerializer BACON_RECIPE_SERIALIZER;
    
    
        // =========================================================
        // VILLAGER / POI
        // =========================================================
    
        public static PointOfInterestType CHEESE_MAKER_POI;
    
        public static VillagerProfession CHEESE_MAKER_PROFESSION;
    
    
        // =========================================================
        // CONSTRUTOR
        // =========================================================
    
        public FoodEssentials() {
    
            FMLJavaModLoadingContext.get()
                    .getModEventBus()
                    .addListener(this::setup);
    
            FMLJavaModLoadingContext.get()
                    .getModEventBus()
                    .addListener(this::clientSetup);
    
            MinecraftForge.EVENT_BUS.register(this);
        }
    
    
        // =========================================================
        // COMMON SETUP
        // =========================================================
    
        private void setup(final FMLCommonSetupEvent event) {
    
            registerPoiStates();
        }
    
    
        // =========================================================
        // CLIENT SETUP
        // =========================================================
    
        private void clientSetup(final FMLClientSetupEvent event) {
    
            net.minecraft.client.gui.ScreenManager.registerFactory(
                    CHEESE_MAKER_CONTAINER,
                    CheeseMakerScreen::new
            );
        }
    
    
        // =========================================================
        // REGISTRO DOS ESTADOS DO POI
        // =========================================================
    
        private static void registerPoiStates() {
    
            try {
    
                Method method =
                        PointOfInterestType.class.getDeclaredMethod(
                                "func_221052_a",
                                PointOfInterestType.class
                        );
    
                method.setAccessible(true);
    
                method.invoke(
                        null,
                        CHEESE_MAKER_POI
                );
    
            } catch (Exception e) {
    
                System.err.println(
                        "Erro ao registrar os estados do POI:"
                );
    
                e.printStackTrace();
            }
        }
    
    
        // =========================================================
        // REGISTROS
        // =========================================================
    
        @Mod.EventBusSubscriber(
                bus = Mod.EventBusSubscriber.Bus.MOD
        )
        public static class RegistryEvents {
    
    
            // -----------------------------------------------------
            // BLOCOS
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onBlocksRegistry(
                    final RegistryEvent.Register<Block> event) {
    
                event.getRegistry().register(
                        CHEESE_BLOCK
                );
    
                event.getRegistry().register(
                        CHEESE_MAKER
                );
            }
    
    
            // -----------------------------------------------------
            // ITENS
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onItemsRegistry(
                    final RegistryEvent.Register<Item> event) {
    
                event.getRegistry().register(KNIFE);
                event.getRegistry().register(HAMBURGER);
                event.getRegistry().register(CHEESEBURGER);
                event.getRegistry().register(BACONBURGER);
                event.getRegistry().register(CHEESE_SLICE);
                event.getRegistry().register(COOKED_BACON);
                event.getRegistry().register(BACON);
    
    
                event.getRegistry().register(
                        new BlockItem(
                                CHEESE_BLOCK,
                                new Item.Properties()
                                        .group(ItemGroup.BUILDING_BLOCKS)
                        ).setRegistryName(
                                CHEESE_BLOCK.getRegistryName()
                        )
                );
    
    
                event.getRegistry().register(
                        new BlockItem(
                                CHEESE_MAKER,
                                new Item.Properties()
                                        .group(ItemGroup.DECORATIONS)
                        ).setRegistryName(
                                CHEESE_MAKER.getRegistryName()
                        )
                );
            }
    
    
            // -----------------------------------------------------
            // SOM
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onSoundRegistry(
                    final RegistryEvent.Register<SoundEvent> event) {
    
                event.getRegistry().register(
                        CHEESE_MAKER_SOUND
                );
            }
    
    
            // -----------------------------------------------------
            // TILE ENTITY
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onTileEntitiesRegistry(
                    final RegistryEvent.Register<TileEntityType<?>> event) {
    
                CHEESE_MAKER_TILE_ENTITY =
                        TileEntityType.Builder
                                .create(
                                        CheeseMakerTileEntity::new,
                                        CHEESE_MAKER
                                )
                                .build(null);
    
                CHEESE_MAKER_TILE_ENTITY.setRegistryName(
                        new ResourceLocation(
                                MOD_ID,
                                "cheese_maker"
                        )
                );
    
                event.getRegistry().register(
                        CHEESE_MAKER_TILE_ENTITY
                );
            }
    
    
            // -----------------------------------------------------
            // CONTAINER
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onContainerRegistry(
                    final RegistryEvent.Register<ContainerType<?>> event) {
    
                CHEESE_MAKER_CONTAINER =
                        IForgeContainerType.create(
                                (windowId, inv, data) ->
                                        new CheeseMakerContainer(
                                                windowId,
                                                inv
                                        )
                        );
    
                CHEESE_MAKER_CONTAINER.setRegistryName(
                        new ResourceLocation(
                                MOD_ID,
                                "cheese_maker"
                        )
                );
    
                event.getRegistry().register(
                        CHEESE_MAKER_CONTAINER
                );
            }
    
    
            // -----------------------------------------------------
            // RECIPE SERIALIZER
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onRecipeSerializerRegistry(
                    final RegistryEvent.Register<IRecipeSerializer<?>> event) {
    
                BACON_RECIPE_SERIALIZER =
                        new BaconRecipeSerializer();
    
                BACON_RECIPE_SERIALIZER.setRegistryName(
                        new ResourceLocation(
                                MOD_ID,
                                "bacon"
                        )
                );
    
                event.getRegistry().register(
                        BACON_RECIPE_SERIALIZER
                );
            }
    
    
            // -----------------------------------------------------
            // POINT OF INTEREST
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onPoiRegistry(
                    final RegistryEvent.Register<PointOfInterestType> event) {
    
                try {
    
                    Set<?> states =
                            ImmutableSet.copyOf(
                                    CHEESE_MAKER
                                            .getStateContainer()
                                            .getValidStates()
                            );
    
                    Constructor<PointOfInterestType> constructor =
                            PointOfInterestType.class
                                    .getDeclaredConstructor(
                                            String.class,
                                            Set.class,
                                            int.class,
                                            net.minecraft.util.SoundEvent.class,
                                            int.class
                                    );
    
                    constructor.setAccessible(true);
    
                    CHEESE_MAKER_POI =
                            constructor.newInstance(
                                    "cheese_maker",
                                    states,
                                    1,
                                    SoundEvents.ENTITY_VILLAGER_WORK_FARMER,
                                    1
                            );
    
                    CHEESE_MAKER_POI.setRegistryName(
                            new ResourceLocation(
                                    MOD_ID,
                                    "cheese_maker"
                            )
                    );
    
                    event.getRegistry().register(
                            CHEESE_MAKER_POI
                    );
    
                } catch (Exception e) {
    
                    throw new RuntimeException(
                            "Erro ao criar o PointOfInterestType do Queijeiro.",
                            e
                    );
                }
            }
    
    
            // -----------------------------------------------------
            // PROFISSÃO
            // -----------------------------------------------------
    
            @SubscribeEvent
            public static void onVillagerProfessionRegistry(
                    final RegistryEvent.Register<VillagerProfession> event) {
    
                CHEESE_MAKER_PROFESSION =
                        new VillagerProfession(
                                "queijeiro",
                                CHEESE_MAKER_POI,
                                ImmutableSet.of(),
                                ImmutableSet.of()
                        );
    
                CHEESE_MAKER_PROFESSION.setRegistryName(
                        new ResourceLocation(
                                MOD_ID,
                                "queijeiro"
                        )
                );
    
                event.getRegistry().register(
                        CHEESE_MAKER_PROFESSION
                );
            }
        }
    
    
        // =========================================================
        // TRADES
        // =========================================================
    
        @Mod.EventBusSubscriber(modid = MOD_ID)
        public static class VillagerEvents {
    
            @SubscribeEvent
            public static void onVillagerTrades(
                    VillagerTradesEvent event) {
    
                if (event.getType() != CHEESE_MAKER_PROFESSION) {
                    return;
                }
    
                List<ITrade> level1 =
                        event.getTrades().get(1);
    
                List<ITrade> level2 =
                        event.getTrades().get(2);
    
    
                // =================================================
                // NÍVEL 1
                // =================================================
    
                level1.add(
                        (entity, random) ->
                                new MerchantOffer(
                                        new ItemStack(
                                                Items.MILK_BUCKET,
                                                1
                                        ),
                                        new ItemStack(
                                                Items.EMERALD,
                                                2
                                        ),
                                        12,
                                        2,
                                        0.05F
                                )
                );
    
    
                level1.add(
                        (entity, random) ->
                                new MerchantOffer(
                                        new ItemStack(
                                                Items.EMERALD,
                                                1
                                        ),
                                        new ItemStack(
                                                CHEESE_SLICE,
                                                4
                                        ),
                                        16,
                                        1,
                                        0.05F
                                )
                );
    
    
                // =================================================
                // NÍVEL 2
                // =================================================
    
                level2.add(
                        (entity, random) ->
                                new MerchantOffer(
                                        new ItemStack(
                                                Items.EMERALD,
                                                3
                                        ),
                                        new ItemStack(
                                                CHEESEBURGER,
                                                1
                                        ),
                                        12,
                                        5,
                                        0.05F
                                )
                );
            }
        }
    }