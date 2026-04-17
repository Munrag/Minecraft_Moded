package com.munrag.soulsmod.souls_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProcessSoulPayload(int soulEntityId, boolean success, String mobType, String customName) implements CustomPacketPayload {

    public static final Type<ProcessSoulPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("souls_mod", "process_soul"));

    public static final StreamCodec<FriendlyByteBuf, ProcessSoulPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.soulEntityId()); // El ID del alma flotante para poder borrarla
                buf.writeBoolean(payload.success());  // ¿Ganaste el RNG o fallaste?
                buf.writeUtf(payload.mobType());      // "minecraft:zombie"
                buf.writeUtf(payload.customName());   // "Runt"
            },
            buf -> new ProcessSoulPayload(buf.readInt(), buf.readBoolean(), buf.readUtf(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}