package com.munrag.soulsmod.souls_mod.client.gui;

import com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoulCaptureScreen extends Screen {

    private final SoulEntity soul;
    private final int imageWidth = 200;
    private final int imageHeight = 140;
    private int leftPos;
    private int topPos;

    // Recibimos la entidad alma para saber qué estadísticas mostrar
    public SoulCaptureScreen(SoulEntity soul) {
        super(Component.literal("Juicio de Alma"));
        this.soul = soul;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        int btnWidth = 130;
        int btnX = this.leftPos + (this.imageWidth - btnWidth) / 2;
        int btnY = this.topPos + this.imageHeight - 30;

        // El botón de Fase 3
        this.addRenderableWidget(Button.builder(Component.literal("§5INTENTAR CAPTURAR"), button -> {

            // ¡TIRAMOS LOS DADOS! (0.45 significa 45% de probabilidad de éxito)
            boolean success = Math.random() < 0.45;

            if (success) {
                // ¡GANAMOS! Abrimos la pantalla de Bautizo sin avisarle al servidor todavía
                this.minecraft.setScreen(new SoulNamingScreen(this.soul));
            } else {
                // PERDIMOS: El alma se rompe
                if (this.minecraft.player != null) {
                    // Sonido local de cristal roto para dar dolor emocional
                    this.minecraft.player.playSound(net.minecraft.sounds.SoundEvents.GLASS_BREAK, 1.0F, 1.0F);
                }
                // Avisamos al servidor que borre el alma
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                        new com.munrag.soulsmod.souls_mod.network.ProcessSoulPayload(this.soul.getId(), false, "", "")
                );
                this.minecraft.setScreen(null); // Cerramos todo
            }

        }).bounds(btnX, btnY, btnWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Fondo oscuro con blur (Como arreglamos en el otro menú)
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // 2. Estructura de la ventana
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF1E1E1E);
        guiGraphics.renderOutline(leftPos, topPos, imageWidth, imageHeight, 0xFF5A5A5A);

        guiGraphics.drawCenteredString(this.font, "§5§lSOUL JUDGEMENT", leftPos + imageWidth / 2, topPos + 10, 0xFFFFFF);
        guiGraphics.fill(leftPos + 10, topPos + 25, leftPos + imageWidth - 10, topPos + 26, 0xFF444444);

        // 3. Formateamos el nombre del mob (ej. "minecraft:zombie" -> "ZOMBIE")
        String rawMob = soul.getSourceMob();
        String displayMob = rawMob.contains(":") ? rawMob.split(":")[1].toUpperCase() : rawMob.toUpperCase();

        // 4. Mostramos las estadísticas de captura (Variables placeholder para la Fase 3)
        guiGraphics.drawString(this.font, "§7TARGET: §f" + displayMob, leftPos + 20, topPos + 40, 0xFFFFFF);

        guiGraphics.drawString(this.font, "§7HEALTH: §f20", leftPos + 20, topPos + 55, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§7DAMAGE: §f3", leftPos + 110, topPos + 55, 0xFFFFFF);

        guiGraphics.drawString(this.font, "§7MANA COST: §b14", leftPos + 20, topPos + 70, 0xFFFFFF);

        // ¡El valor crucial del RNG!
        guiGraphics.drawString(this.font, "§7CAPTURE CHANCE: §e45%", leftPos + 20, topPos + 90, 0xFFFFFF);

        // 5. Renderizamos el botón
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Que el mundo siga vivo mientras decides (da más tensión)
    }
}