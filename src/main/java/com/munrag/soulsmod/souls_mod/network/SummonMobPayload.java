package com.munrag.soulsmod.souls_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SummonMobPayload(String entityId, int manaCost) implements CustomPacketPayload {

    // La etiqueta de nuestro paquete (viaja del Cliente -> al Servidor)
    public static final Type<SummonMobPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("souls_mod", "summon_mob"));

    // El traductor que convierte el texto (String) y el número (int) a bytes para que viaje por internet
    public static final StreamCodec<FriendlyByteBuf, SummonMobPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.entityId()); // Escribe el nombre del mob
                buf.writeInt(payload.manaCost()); // Escribe el costo
            },
            buf -> new SummonMobPayload(buf.readUtf(), buf.readInt()) // Lee ambos datos al llegar
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}