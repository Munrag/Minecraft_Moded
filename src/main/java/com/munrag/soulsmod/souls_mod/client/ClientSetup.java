package com.munrag.soulsmod.souls_mod.client;

import com.munrag.soulsmod.souls_mod.registry.ModEntities;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "souls_mod", value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADOW_ZOMBIE.get(), (context) ->
                new ZombieRenderer(context) {

                    // 1. Declaramos la ruta exacta de tu nueva textura
                    private final net.minecraft.resources.ResourceLocation SHADOW_TEXTURE =
                            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("souls_mod", "textures/entity/shadow_zombie.png");

                    // 2. Le decimos al juego que use esta imagen en lugar de la del zombi normal
                    @Override
                    public net.minecraft.resources.ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Zombie entity) {
                        return SHADOW_TEXTURE;
                    }

                    // 3. Mantenemos el truco de la iluminación para que parezca una sombra
                    @Override
                    protected int getBlockLightLevel(net.minecraft.world.entity.monster.Zombie entity, net.minecraft.core.BlockPos pos) {
                        return 0; // 0 = Siempre oscuro. (Si prefieres que brille en la oscuridad, cámbialo a 15)
                    }
                }
                );
        // Reemplazamos NoopRenderer por nuestro nuevo SoulRenderer animado
        event.registerEntityRenderer(ModEntities.SOUL_ENTITY.get(), com.munrag.soulsmod.souls_mod.client.render.SoulRenderer::new);
    }
}
