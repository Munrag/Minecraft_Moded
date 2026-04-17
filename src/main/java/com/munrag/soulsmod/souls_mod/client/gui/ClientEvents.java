package com.munrag.soulsmod.souls_mod.client.gui; // Ruta corregida

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers; // Import correcto para NeoForge 1.21.1

@EventBusSubscriber(modid = "souls_mod", value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.FOOD_LEVEL,
                ResourceLocation.fromNamespaceAndPath("souls_mod", "mana_overlay"),
                ManaHudOverlay.HUD_MANA
        );
    }
}