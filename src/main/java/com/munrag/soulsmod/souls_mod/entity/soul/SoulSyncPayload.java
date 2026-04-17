package com.munrag.soulsmod.souls_mod.network;

import com.munrag.soulsmod.souls_mod.world.soul.CapturedSoul;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public record SoulSyncPayload(List<CapturedSoul> souls) implements CustomPacketPayload {
    public static final Type<SoulSyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("souls_mod", "soul_sync"));

    public static final StreamCodec<FriendlyByteBuf, SoulSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.souls().size());
                for (CapturedSoul soul : payload.souls()) {
                    buf.writeUtf(soul.mobType());
                    buf.writeUtf(soul.customName());
                }
            },
            buf -> {
                int size = buf.readInt();
                List<CapturedSoul> list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    list.add(new CapturedSoul(buf.readUtf(), buf.readUtf()));
                }
                return new SoulSyncPayload(list);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}