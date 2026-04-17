package com.munrag.soulsmod.souls_mod.world.soul;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;

public class PlayerSouls {
    private final List<CapturedSoul> souls = new ArrayList<>();

    // Creamos un Codec para la lista completa usando xmap (una conversión bidireccional)
    public static final Codec<PlayerSouls> CODEC = CapturedSoul.CODEC.listOf().xmap(
            listaGuardada -> {
                PlayerSouls ps = new PlayerSouls();
                ps.souls.addAll(listaGuardada);
                return ps;
            },
            PlayerSouls::getSouls
    );

    public void addSoul(CapturedSoul soul) {
        this.souls.add(soul);
    }

    public List<CapturedSoul> getSouls() {
        return this.souls;
    }
}