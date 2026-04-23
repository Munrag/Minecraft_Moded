package com.munrag.soulsmod.souls_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemoveSoulPayload(String customName) implements CustomPacketPayload {

    public static final Type<RemoveSoulPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("souls_mod", "remove_soul"));

    public static final StreamCodec<FriendlyByteBuf, RemoveSoulPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.customName());
            },
            buf -> {
                return new RemoveSoulPayload(buf.readUtf());
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
