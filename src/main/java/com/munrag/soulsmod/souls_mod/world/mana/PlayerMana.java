package com.munrag.soulsmod.souls_mod.world.mana;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerMana implements INBTSerializable<CompoundTag> {

    private int currentMana = 20;

    // Métodos para leer y modificar el maná
    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int mana) {
        this.currentMana = mana;
    }
    public int getMaxMana(Player player) {
        int level = player.experienceLevel;
        float mana = 20; // Nivel 0

        if (level <= 15) {
            // Niveles 1 al 15: +2 por nivel
            mana += (level * 2);
        } else if (level <= 30) {
            // Niveles 16 al 30: 50 base (del tier anterior) + 1 por nivel
            mana = 50 + (level - 15);
        } else {
            // Niveles 31+: 65 base + 0.5 por nivel
            mana = 65 + (level - 30) * 0.5f;
        }

        return (int) mana;
    }

    // --- CÓDIGO DE GUARDADO Y CARGA (NBT) ---
    // Esto es lo que escribe el dato en el archivo de tu mundo cuando sales
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("currentMana", currentMana);
        return tag;
    }

    // Esto es lo que lee el dato cuando vuelves a entrar a tu mundo
    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        currentMana = nbt.getInt("currentMana");
    }
}