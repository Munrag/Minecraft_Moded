package com.munrag.soulsmod.souls_mod.client.gui;

import com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity;
import com.munrag.soulsmod.souls_mod.network.ProcessSoulPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class SoulNamingScreen extends Screen {

    private final SoulEntity soul;
    private EditBox nameInput; // ¡Caja de texto!
    private final int imageWidth = 200;
    private final int imageHeight = 100;
    private int leftPos;
    private int topPos;

    public SoulNamingScreen(SoulEntity soul) {
        super(Component.literal("Bautizo de Alma"));
        this.soul = soul;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        // 1. Configuramos la caja de texto
        this.nameInput = new EditBox(this.font, this.leftPos + 25, this.topPos + 40, 150, 20, Component.literal("Nombre"));
        this.nameInput.setMaxLength(16); // Límite de nombre (como las etiquetas de Minecraft)
        this.addRenderableWidget(this.nameInput);
        this.setInitialFocus(this.nameInput); // Que el cursor aparezca parpadeando, listo para escribir

        // 2. El botón CONFIRMAR
        int btnWidth = 100;
        this.addRenderableWidget(Button.builder(Component.literal("§aCONFIRMAR"), button -> {

            // Leemos lo que escribiste
            String customName = this.nameInput.getValue().trim();
            if (customName.isEmpty()) customName = "Sombra"; // Nombre de emergencia si lo dejas en blanco

            // Enviamos el paquete de ÉXITO al Servidor
            PacketDistributor.sendToServer(new ProcessSoulPayload(this.soul.getId(), true, this.soul.getSourceMob(), customName));

            this.minecraft.setScreen(null); // Cerramos el menú
        }).bounds(this.leftPos + (this.imageWidth - btnWidth) / 2, this.topPos + 70, btnWidth, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF1E1E1E);
        guiGraphics.renderOutline(leftPos, topPos, imageWidth, imageHeight, 0xFF5A5A5A);

        guiGraphics.drawCenteredString(this.font, "§6OTORGA UN NOMBRE AL ALMA", leftPos + imageWidth / 2, topPos + 15, 0xFFFFFF);

        // Esto dibuja la caja de texto y el botón
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() { return false; }
}