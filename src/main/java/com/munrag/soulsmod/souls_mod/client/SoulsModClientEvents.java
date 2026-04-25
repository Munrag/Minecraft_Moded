package com.munrag.soulsmod.souls_mod.client;

import com.mojang.logging.LogUtils;
import com.munrag.soulsmod.souls_mod.Souls_mod;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

@EventBusSubscriber(modid = Souls_mod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class SoulsModClientEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        LOGGER.info("HELLO FROM CLIENT SETUP");
        LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
