package com.munrag.soulsmod.souls_mod.client.gui;

import com.munrag.soulsmod.souls_mod.world.soul.CapturedSoul;

import java.util.ArrayList;
import java.util.List;
// Si la palabra CapturedSoul sale en rojo, pon el cursor sobre ella y presiona Alt + Enter para importarla.


public class ClientSoulData {
    private static List<CapturedSoul> souls = new ArrayList<>();

    public static void set(List<CapturedSoul> newSouls) {
        souls = new ArrayList<>(newSouls);

        // --- DEBUG DE ALMAS ---
        System.out.println("========== DEBUG DE ALMAS ==========");
        System.out.println("Almas recibidas del servidor: " + souls.size());
        if (!souls.isEmpty()) {
            System.out.println("Datos de la primera alma: " + souls.get(0).toString());
        }
        System.out.println("====================================");
    }

    public static List<CapturedSoul> get() {
        return souls;
    }
}