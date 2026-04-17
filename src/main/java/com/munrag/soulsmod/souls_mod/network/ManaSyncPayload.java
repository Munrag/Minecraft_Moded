package com.munrag.soulsmod.souls_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Un "record" es perfecto para esto. Solo transporta un dato: el maná actual.
public record ManaSyncPayload(int mana) implements CustomPacketPayload {

    // 1. Le damos una etiqueta o "sello postal" a nuestro paquete
    public static final Type<ManaSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("souls_mod", "mana_sync"));

    // 2. El "Codec" es el traductor.
    // Convierte el número (int) en bytes para viajar por internet, y luego de bytes a número otra vez.
    public static final StreamCodec<FriendlyByteBuf, ManaSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeInt(payload.mana()), // Escribir en el sobre
            buf -> new ManaSyncPayload(buf.readInt())       // Leer del sobre
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}