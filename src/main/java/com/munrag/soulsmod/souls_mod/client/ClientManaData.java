package com.munrag.soulsmod.souls_mod.client;

public class ClientManaData {
    private static int playerMana;

    public static void set(int mana) {
        playerMana = mana;
    }

    public static int getPlayerMana() {
        return playerMana;
    }
}