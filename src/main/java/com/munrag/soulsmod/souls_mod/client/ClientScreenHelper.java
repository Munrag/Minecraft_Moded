package com.munrag.soulsmod.souls_mod.client;

import com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity;
import net.minecraft.client.Minecraft;

public class ClientScreenHelper {
    public static void openSoulCaptureScreen(SoulEntity soul) {
        Minecraft.getInstance().setScreen(
                new com.munrag.soulsmod.souls_mod.client.gui.SoulCaptureScreen(soul)
        );
    }
}
