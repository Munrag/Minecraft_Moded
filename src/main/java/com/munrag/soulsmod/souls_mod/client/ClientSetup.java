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
        // Reemplazamos NoopRenderer por nuestro nuevo SoulRenderer animado
        event.registerEntityRenderer(ModEntities.SOUL_ENTITY.get(), com.munrag.soulsmod.souls_mod.client.render.SoulRenderer::new);
    }
}
