package com.munrag.soulsmod.souls_mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ManaHudOverlay {

    private static final ResourceLocation FLAMA_LLENA = ResourceLocation.fromNamespaceAndPath("souls_mod", "textures/gui/mana.png");
    private static final ResourceLocation FLAMA_VACIA = ResourceLocation.fromNamespaceAndPath("souls_mod", "textures/gui/mana_empty.png");
    private static final ResourceLocation FLAMA_MEDIA = ResourceLocation.fromNamespaceAndPath("souls_mod", "textures/gui/mana_half.png");

    public static final LayeredDraw.Layer HUD_MANA = (GuiGraphics guiGraphics, DeltaTracker deltaTracker) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (mc.options.hideGui || mc.gameMode == null || player == null || !mc.gameMode.canHurtPlayer()) {
            return;
        }

        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int centerX = width / 2;
        int leftPos = centerX + 10;

        // Ajuste dinámico por burbujas de aire
        int offset = 49;
        if (player.getAirSupply() < player.getMaxAirSupply()) {
            offset += 10;
        }
        int topPos = height - offset;

// --- NUEVA LÓGICA DE COMPACTACIÓN CON DATOS REALES ---

        // 1. Calculamos el maná máximo basado en el nivel actual del jugador
        int level = player.experienceLevel;
        if (level < 1) level = 1;
        int manaMaximo = 20 + ((level - 1) * 10);

        // 2. Leemos el maná actual de nuestra "caja fuerte" que se actualiza por internet
        int manaActual = com.munrag.soulsmod.souls_mod.client.ClientManaData.getPlayerMana();

        // Evitamos que por algún error visual el maná dibuje de más
        if (manaActual > manaMaximo) {
            manaActual = manaMaximo;
        }

        int totalTiers = (manaMaximo + 19) / 20;
        int manaEnBarraActual = (manaActual > 0) ? ((manaActual - 1) % 20) + 1 : 0;

        // Dibujamos siempre una única línea de 10 flamas
        for (int i = 0; i < 10; i++) {
            int x = leftPos + (i * 8);
            int y = topPos;

            // Fondo vacío
            guiGraphics.blit(FLAMA_VACIA, x, y, 0, 0, 9, 9, 9, 9);

            // Lógica para flamas llenas y medias flamas
            if (manaEnBarraActual > (i * 2) + 1) {
                guiGraphics.blit(FLAMA_LLENA, x, y, 0, 0, 9, 9, 9, 9);
            } else if (manaEnBarraActual == (i * 2) + 1) {
                guiGraphics.blit(FLAMA_MEDIA, x, y, 0, 0, 9, 9, 9, 9);
            }
        }

        // --- EL MULTIPLICADOR ---
        if (totalTiers > 1) {
            String tierText = "x" + totalTiers;
            // Lo posicionamos 2 píxeles después de la última flama
            int textX = leftPos + 82;
            int textY = topPos + 1; // Ajuste leve para centrarlo verticalmente

            // Dibujamos el texto (blanco, con sombra para que se lea bien en cualquier bioma)
            guiGraphics.drawString(mc.font, tierText, textX, textY, 0xFFFFFF, true);
        }
    };
}