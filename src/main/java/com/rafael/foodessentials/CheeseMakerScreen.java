package com.rafael.foodessentials;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CheeseMakerScreen extends ContainerScreen<CheeseMakerContainer> {

    // =========================================================
    // TEXTURAS
    // =========================================================

    private static final ResourceLocation BACKGROUND_TEXTURE =
            new ResourceLocation(
                    FoodEssentials.MOD_ID,
                    "textures/gui/container/cheese_maker.png"
            );

    private static final ResourceLocation RECIPE_BOOK_TEXTURE =
            new ResourceLocation(
                    FoodEssentials.MOD_ID,
                    "textures/gui/container/recipe_book.png"
            );


    // =========================================================
    // CONFIGURAÇÕES DO LIVRO
    // =========================================================

    private static final int RECIPE_BOOK_WIDTH = 120;
    private static final int RECIPE_BOOK_HEIGHT = 166;

    private static final int RECIPE_BOOK_BUTTON_SIZE = 20;


    // =========================================================
    // POSIÇÃO DO BOTÃO
    // =========================================================

    /*
     * O botão fica dentro da GUI.
     *
     * X = distância da borda esquerda
     * Y = distância do topo
     */
    private static final int RECIPE_BUTTON_X = 23;
    private static final int RECIPE_BUTTON_Y = 32;


    // =========================================================
    // TAMANHO DA TEXTURA DO BOTÃO
    // =========================================================

    /*
     * Estamos considerando que recipe_book.png possui
     * 20x20 pixels.
     *
     * Se o PNG tiver outro tamanho, alteramos aqui.
     */
    private static final int RECIPE_TEXTURE_WIDTH = 20;
    private static final int RECIPE_TEXTURE_HEIGHT = 20;


    // =========================================================
    // ESTADO
    // =========================================================

    private boolean recipeBookOpen = false;

    private RecipeBookButton recipeBookButton;


    // =========================================================
    // CONSTRUTOR
    // =========================================================

    public CheeseMakerScreen(
            CheeseMakerContainer screenContainer,
            PlayerInventory inv,
            ITextComponent title) {

        super(
                screenContainer,
                inv,
                title
        );
    }


    // =========================================================
    // INIT
    // =========================================================

    @Override
    protected void init() {

        /*
         * Primeiro inicializamos a ContainerScreen.
         *
         * Isso calcula guiLeft e guiTop.
         */
        super.init();


        /*
         * Criamos o botão.
         *
         * Ele fica dentro da GUI, no lado esquerdo,
         * próximo aos slots.
         */
        recipeBookButton =
                this.addButton(
                        new RecipeBookButton(
                                this.guiLeft + RECIPE_BUTTON_X,
                                this.guiTop + RECIPE_BUTTON_Y
                        )
                );
    }


    // =========================================================
    // RENDER PRINCIPAL
    // =========================================================

    @Override
    public void render(
            int mouseX,
            int mouseY,
            float partialTicks) {

        /*
         * Fundo escuro padrão do Minecraft.
         */
        this.renderBackground();


        /*
         * O livro é desenhado antes da GUI principal.
         */
        if (recipeBookOpen) {

            this.renderRecipeBook(
                    mouseX,
                    mouseY
            );
        }


        /*
         * Renderiza a ContainerScreen.
         *
         * Isso também renderiza o botão.
         */
        super.render(
                mouseX,
                mouseY,
                partialTicks
        );


        /*
         * Tooltips ficam por cima.
         */
        this.renderHoveredToolTip(
                mouseX,
                mouseY
        );
    }


    // =========================================================
    // FUNDO DA GUI
    // =========================================================

    @Override
    protected void drawGuiContainerBackgroundLayer(
            float partialTicks,
            int mouseX,
            int mouseY) {

        GlStateManager.color4f(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );


        if (this.minecraft == null) {
            return;
        }


        /*
         * Carrega a textura da Cheese Maker.
         */
        this.minecraft
                .getTextureManager()
                .bindTexture(
                        BACKGROUND_TEXTURE
                );


        int x = this.guiLeft;
        int y = this.guiTop;


        // =====================================================
        // GUI
        // =====================================================

        this.blit(
                x,
                y,
                0,
                0,
                this.xSize,
                this.ySize
        );


        // =====================================================
        // BARRA DE PROGRESSO
        // =====================================================

        int progress =
                this.container
                        .getCookProgressionScaled();


        if (progress > 0) {

            this.blit(
                    x + 79,
                    y + 34,
                    176,
                    14,
                    progress,
                    16
            );
        }
    }


    // =========================================================
    // TEXTO DA GUI
    // =========================================================

    @Override
    protected void drawGuiContainerForegroundLayer(
            int mouseX,
            int mouseY) {

        // =====================================================
        // TÍTULO
        // =====================================================

        String title =
                this.title
                        .getFormattedText();


        this.font.drawString(
                title,
                (float) (
                        this.xSize / 2
                                - this.font.getStringWidth(title) / 2
                ),
                6.0F,
                4210752
        );


        // =====================================================
        // INVENTÁRIO DO JOGADOR
        // =====================================================

        this.font.drawString(
                this.playerInventory
                        .getDisplayName()
                        .getFormattedText(),
                8.0F,
                this.ySize - 96 + 2.0F,
                4210752
        );
    }


    // =========================================================
    // LIVRO DE RECEITAS
    // =========================================================

    private void renderRecipeBook(
            int mouseX,
            int mouseY) {

        /*
         * O painel fica à esquerda da Cheese Maker.
         */
        int x =
                this.guiLeft - RECIPE_BOOK_WIDTH;

        int y =
                this.guiTop;


        // =====================================================
        // CORES
        // =====================================================

        int backgroundColor =
                0xFFC6C6C6;

        int borderColor =
                0xFF555555;

        int textColor =
                0xFF3F3F3F;


        // =====================================================
        // FUNDO
        // =====================================================

        fill(
                x,
                y,
                x + RECIPE_BOOK_WIDTH,
                y + RECIPE_BOOK_HEIGHT,
                backgroundColor
        );


        // =====================================================
        // BORDA SUPERIOR
        // =====================================================

        fill(
                x,
                y,
                x + RECIPE_BOOK_WIDTH,
                y + 2,
                borderColor
        );


        // =====================================================
        // BORDA INFERIOR
        // =====================================================

        fill(
                x,
                y + RECIPE_BOOK_HEIGHT - 2,
                x + RECIPE_BOOK_WIDTH,
                y + RECIPE_BOOK_HEIGHT,
                borderColor
        );


        // =====================================================
        // BORDA ESQUERDA
        // =====================================================

        fill(
                x,
                y,
                x + 2,
                y + RECIPE_BOOK_HEIGHT,
                borderColor
        );


        // =====================================================
        // BORDA DIREITA
        // =====================================================

        fill(
                x + RECIPE_BOOK_WIDTH - 2,
                y,
                x + RECIPE_BOOK_WIDTH,
                y + RECIPE_BOOK_HEIGHT,
                borderColor
        );


        // =====================================================
        // TÍTULO
        // =====================================================

        this.font.drawString(
                "Receitas",
                x + 8,
                y + 8,
                textColor
        );


        // =====================================================
        // RECEITA: RED MUSHROOM
        // =====================================================

        renderRecipe(
                x + 8,
                y + 30,
                Items.RED_MUSHROOM,
                25
        );


        // =====================================================
        // RECEITA: BROWN MUSHROOM
        // =====================================================

        renderRecipe(
                x + 8,
                y + 76,
                Items.BROWN_MUSHROOM,
                25
        );


        // =====================================================
        // RECEITA: NETHER WART
        // =====================================================

        renderRecipe(
                x + 8,
                y + 122,
                Items.NETHER_WART,
                10
        );
    }


    // =========================================================
    // DESENHA UMA RECEITA
    // =========================================================

    private void renderRecipe(
            int x,
            int y,
            Item ingredient,
            int seconds) {

        RenderHelper.enableGUIStandardItemLighting();


        // =====================================================
        // LEITE
        // =====================================================

        renderItem(
                new ItemStack(
                        Items.MILK_BUCKET
                ),
                x,
                y
        );


        // =====================================================
        // INGREDIENTE
        // =====================================================

        renderItem(
                new ItemStack(
                        ingredient
                ),
                x + 22,
                y
        );


        // =====================================================
        // SETA
        // =====================================================

        this.font.drawString(
                "->",
                x + 42,
                y + 5,
                0xFF3F3F3F
        );


        // =====================================================
        // QUEIJO
        // =====================================================

        renderItem(
                new ItemStack(
                        FoodEssentials.CHEESE_BLOCK
                ),
                x + 65,
                y
        );


        // =====================================================
        // TEMPO
        // =====================================================

        this.font.drawString(
                seconds + " segundos",
                x,
                y + 20,
                0xFF4A4A4A
        );


        RenderHelper.disableStandardItemLighting();
    }


    // =========================================================
    // DESENHA ITEM
    // =========================================================

    private void renderItem(
            ItemStack stack,
            int x,
            int y) {

        this.itemRenderer
                .renderItemAndEffectIntoGUI(
                        stack,
                        x,
                        y
                );


        this.itemRenderer
                .renderItemOverlayIntoGUI(
                        this.font,
                        stack,
                        x,
                        y,
                        null
                );
    }


    // =========================================================
    // BOTÃO DO LIVRO
    // =========================================================

    private class RecipeBookButton extends Button {

        public RecipeBookButton(
                int x,
                int y) {

            super(
                    x,
                    y,
                    RECIPE_BOOK_BUTTON_SIZE,
                    RECIPE_BOOK_BUTTON_SIZE,
                    "",
                    button -> {

                        /*
                         * Abre ou fecha o livro.
                         */
                        recipeBookOpen =
                                !recipeBookOpen;
                    }
            );
        }


        // =====================================================
        // RENDER DO BOTÃO
        // =====================================================

        @Override
        public void renderButton(
                int mouseX,
                int mouseY,
                float partialTicks) {

            if (!this.visible) {
                return;
            }


            if (minecraft == null) {
                return;
            }


            // =================================================
            // FUNDO
            // =================================================

            /*
             * Fundo neutro do botão.
             */
            fill(
                    this.x,
                    this.y,
                    this.x + this.width,
                    this.y + this.height,
                    0xFFC6C6C6
            );


            // =================================================
            // TEXTURA
            // =================================================

            minecraft
                    .getTextureManager()
                    .bindTexture(
                            RECIPE_BOOK_TEXTURE
                    );


            /*
             * Minecraft 1.14.4:
             *
             * x, y
             * u, v
             * largura, altura
             * largura da textura, altura da textura
             *
             * Estamos considerando que o PNG é 20x20.
             */
            this.blit(
                    this.x,
                    this.y,
                    0,
                    0,
                    RECIPE_BOOK_BUTTON_SIZE,
                    RECIPE_BOOK_BUTTON_SIZE,
                    RECIPE_TEXTURE_WIDTH,
                    RECIPE_TEXTURE_HEIGHT
            );


            // =================================================
            // HOVER
            // =================================================

            boolean hovered =
                    mouseX >= this.x
                            && mouseY >= this.y
                            && mouseX < this.x + this.width
                            && mouseY < this.y + this.height;


            if (hovered) {

                fill(
                        this.x,
                        this.y,
                        this.x + this.width,
                        this.y + this.height,
                        0x33FFFFFF
                );
            }
        }
    }
}
