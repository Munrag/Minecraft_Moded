package com.munrag.soulsmod.souls_mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MonarchArmyScreen extends Screen {

    private final int imageWidth = 280;
    private final int imageHeight = 190;
    private int leftPos;
    private int topPos;

    private LivingEntity previewEntity;
    private record MobEntry(String species, String customName, String description, int hp, int armor, int damage, int mana, EntityType<? extends LivingEntity> type) {}
    private final List<MobEntry> mobList = new ArrayList<>();
    private int selectedIndex = 0;

    public MonarchArmyScreen() {
        super(Component.literal("MONARCH MOD MENU"));

        // Maniquí temporal con estadísticas falsas (20 hp, 2 armadura, 3 daño, 10 maná)
        mobList.add(new MobEntry("Zombie", "Sin alma", "Captura un alma para verla aquí...", 20, 2, 3, 10, net.minecraft.world.entity.EntityType.ZOMBIE));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        updatePreviewEntity();

        // Botón INVOCAR
        int invX = this.leftPos + this.imageWidth - 85;
        int invY = this.topPos + this.imageHeight - 25;
        this.addRenderableWidget(Button.builder(Component.literal("INVOCAR"), b -> {

            MobEntry selected = mobList.get(selectedIndex);

            // Obtenemos el ID oficial de Minecraft para este mob (ej. "minecraft:zombie")
            String entityId = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(selected.type).toString();

            // ¡Enviamos la carta al Servidor!
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.munrag.soulsmod.souls_mod.network.SummonMobPayload(entityId, selected.mana));

            // Cerramos el menú automáticamente para que puedas ver a tu sombra aparecer y seguir peleando
            this.minecraft.setScreen(null);

        }).bounds(invX, invY, 75, 18).build());
    }

    // --- NUEVO: Nuestro propio detector de clics para la lista ---
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Revisamos si el jugador hizo clic sobre alguno de nuestros botones dibujados a mano
        for (int i = 0; i < mobList.size(); i++) {
            int btnX = leftPos + 7;
            int btnY = topPos + 25 + (i * 32);

            // Si el mouse está dentro del recuadro del botón
            if (mouseX >= btnX && mouseX <= btnX + 85 && mouseY >= btnY && mouseY <= btnY + 30) {
                this.selectedIndex = i;
                updatePreviewEntity();
                return true; // Le decimos al juego que ya procesamos el clic
            }
        }
        // Si no hizo clic en la lista, dejamos que Minecraft procese el clic (para el botón INVOCAR)
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void updatePreviewEntity() {
        if (this.minecraft != null && this.minecraft.level != null) {
            this.previewEntity = mobList.get(selectedIndex).type.create(this.minecraft.level);
            if (this.previewEntity instanceof net.minecraft.world.entity.Mob mob) {
                mob.setNoAi(true);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Dibujamos el fondo oscuro vanilla con blur (una sola vez al principio)
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // --- 2. FONDO Y ESTRUCTURA DE NUESTRO MENÚ ---
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xFF1E1E1E);
        guiGraphics.renderOutline(leftPos, topPos, imageWidth, imageHeight, 0xFF5A5A5A);

        guiGraphics.drawCenteredString(this.font, "§6§lMONARCH §fMOD MENU", leftPos + imageWidth / 2, topPos + 8, 0xFFFFFF);

        guiGraphics.fill(leftPos + 95, topPos + 22, leftPos + 96, topPos + imageHeight - 8, 0xFF444444);
        guiGraphics.fill(leftPos + 96, topPos + 120, leftPos + imageWidth - 8, topPos + 121, 0xFF444444);

        // --- 3. LISTA DE RECLUTAS ---
        guiGraphics.drawString(this.font, "§6MOB RECRUITS", leftPos + 12, topPos + 12, 0xFFFFFF, false);

        for (int i = 0; i < mobList.size(); i++) {
            MobEntry entry = mobList.get(i);
            int y = topPos + 25 + (i * 32);

            int color = (i == selectedIndex) ? 0xFF444444 : 0xFF2D2D2D;
            guiGraphics.fill(leftPos + 7, y, leftPos + 92, y + 30, color);
            guiGraphics.renderOutline(leftPos + 7, y, 85, 30, (i == selectedIndex) ? 0xFFFFFFFF : 0xFF000000);

            guiGraphics.fill(leftPos + 10, y + 3, leftPos + 34, y + 27, 0xFF111111);

            guiGraphics.drawString(this.font, entry.species, leftPos + 38, y + 5, 0xFFFFFF, false);
            guiGraphics.drawString(this.font, "§8" + entry.customName, leftPos + 38, y + 16, 0xFFFFFF, false);
        }

        // --- 4. ESTADÍSTICAS ---
        MobEntry sel = mobList.get(selectedIndex);
        int tx = leftPos + 195;
        int ty = topPos + 25;

        guiGraphics.drawString(this.font, "§7NAME: §f" + sel.customName, tx, ty, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§7HP: §f" + sel.hp, tx, ty + 12, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§7ARMOR: §f" + sel.armor, tx, ty + 24, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§7DAMAGE: §f" + sel.damage, tx, ty + 36, 0xFFFFFF);
        guiGraphics.drawString(this.font, "§7MANA COST: §b" + sel.mana, tx, ty + 48, 0xFFFFFF);

        guiGraphics.drawWordWrap(this.font, Component.literal("§8" + sel.description), tx, ty + 62, 75, 0xFFFFFF);

        // --- 5. INVENTARIO ---
        guiGraphics.drawString(this.font, "§7INVENTORY:", leftPos + 105, topPos + 130, 0xFFFFFF);
        for(int row = 0; row < 2; row++) {
            for(int col = 0; col < 5; col++) {
                int slotX = leftPos + 105 + (col * 18);
                int slotY = topPos + 142 + (row * 18);
                guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0x55000000);
                guiGraphics.renderOutline(slotX, slotY, 16, 16, 0xFF444444);
            }
        }

        // --- 6. DIBUJAR WIDGETS (El gran arreglo) ---
        // En lugar de usar super.render, le decimos a la pantalla que dibuje
        // solo el botón INVOCAR, sin aplicarnos el fondo borroso otra vez.
        for (net.minecraft.client.gui.components.Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        // --- 7. MODELO 3D ---
        if (this.previewEntity != null) {
            float rotation = (System.currentTimeMillis() % 3600) / 10.0f;
            Quaternionf pose = new Quaternionf().rotationZ((float) Math.toRadians(180)).rotateY((float) Math.toRadians(180 + rotation));
            InventoryScreen.renderEntityInInventory(guiGraphics, leftPos + 145, topPos + 110, 45, new Vector3f(0,0,0), pose, null, this.previewEntity);
        }
    }
}