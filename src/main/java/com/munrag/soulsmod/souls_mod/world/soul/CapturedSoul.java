package com.munrag.soulsmod.souls_mod.world.soul;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CapturedSoul(String mobType, String customName) {

    // El nuevo estándar de Minecraft (Codec) para guardar y leer en el disco duro
    public static final Codec<CapturedSoul> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.STRING.fieldOf("MobType").forGetter(CapturedSoul::mobType),
            Codec.STRING.fieldOf("CustomName").forGetter(CapturedSoul::customName)
    ).apply(inst, CapturedSoul::new));

}