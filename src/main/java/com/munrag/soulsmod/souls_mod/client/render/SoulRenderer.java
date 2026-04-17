package com.munrag.soulsmod.souls_mod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class SoulRenderer extends EntityRenderer<SoulEntity> {

    // La ruta de tu spritesheet
    private static final ResourceLocation SOUL_TEXTURE = ResourceLocation.fromNamespaceAndPath("souls_mod", "textures/entity/soul.png");

    public SoulRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulEntity entity) {
        return SOUL_TEXTURE;
    }

    @Override
    public void render(SoulEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        // 1. Levitar un poco sobre el suelo
        poseStack.translate(0.0D, 0.5D, 0.0D);

        // 2. BILLBOARD: Hacemos que el plano mire siempre a la cámara del jugador
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        // 3. Rotar 180 grados en Y para que la textura no se vea en espejo
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));

        // 4. Tamaño del Alma (0.5 = medio bloque)
        float scale = 0.5F;
        poseStack.scale(scale, scale, scale);

        // --- SISTEMA DE ANIMACIÓN UV (AJUSTADO PARA TIRA HORIZONTAL) ---
        // 1. Número total de frames en tu spritesheet horizontal
        int totalFrames = 3;

        // 2. Velocidad de animación (Menor = más rápido. 6 ticks = 0.3s por frame)
        int ticksPerFrame = 6;

        // Calculamos qué frame (0, 1 o 2) toca dibujar basado en la edad de la entidad
        int currentFrame = (entity.tickCount / ticksPerFrame) % totalFrames;

        // 3. MATEMÁTICA UV PARA TIRA HORIZONTAL
        // La V (vertical) permanece fija (asumimos tira única que va de arriba 0 a abajo 1)
        float v0 = 0.0F; // Parte superior de la textura sheet
        float v1 = 1.0F; // Parte inferior de la textura sheet

        // La U (horizontal) varía por frame.
        // Desplazamos la "ventana" de dibujado horizontalmente.
        float u0 = (float) currentFrame / totalFrames; // Inicio del frame actual (ej. frame 1 empieza en U=0.33)
        float u1 = (float) (currentFrame + 1) / totalFrames; // Fin del frame actual (ej. frame 1 termina en U=0.66)

        // --- DIBUJAR CUADRADO (LOS VÉRTICES Y SUS UV LLAMAN A ESTAS VARIABLES) ---
        // El resto del código de dibujado de vértices que ya tenías abajo utiliza estas
        // variables u0, u1, v0, v1, así que ya no necesitas cambiar nada más.

        // --- DIBUJADO DEL QUAD ---
        Matrix4f pose = poseStack.last().pose();
        PoseStack.Pose normalPose = poseStack.last();

        // Usamos entityTranslucent para que soporte transparencias suaves (Alpha)
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));

        // Hacemos que brille en la oscuridad ignorando la luz del mundo (FULL_BRIGHT)
        int light = LightTexture.FULL_BRIGHT;

        // Dibujamos los 4 vértices del cuadrado
        // Vértice 1 (Abajo Izquierda)
        vertexConsumer.addVertex(pose, -0.5F, -0.5F, 0.0F).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normalPose, 0.0F, 1.0F, 0.0F);
        // Vértice 2 (Abajo Derecha)
        vertexConsumer.addVertex(pose, 0.5F, -0.5F, 0.0F).setColor(255, 255, 255, 255).setUv(u0, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normalPose, 0.0F, 1.0F, 0.0F);
        // Vértice 3 (Arriba Derecha)
        vertexConsumer.addVertex(pose, 0.5F, 0.5F, 0.0F).setColor(255, 255, 255, 255).setUv(u0, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normalPose, 0.0F, 1.0F, 0.0F);
        // Vértice 4 (Arriba Izquierda)
        vertexConsumer.addVertex(pose, -0.5F, 0.5F, 0.0F).setColor(255, 255, 255, 255).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(normalPose, 0.0F, 1.0F, 0.0F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }
}
