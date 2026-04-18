package com.munrag.soulsmod.souls_mod.registry;

import com.munrag.soulsmod.souls_mod.world.mana.PlayerMana;
import com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    // Creamos el "registro" específico para Attachments usando tu Mod ID
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "souls_mod");

    // Registramos la mochila de maná.
    // Le indicamos a NeoForge que es "serializable" para que sepa que debe guardarla.
    public static final Supplier<AttachmentType<PlayerMana>> PLAYER_MANA = ATTACHMENT_TYPES.register(
            "player_mana",
            () -> AttachmentType.serializable(PlayerMana::new).build()
    );
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls>> PLAYER_SOULS =
            ATTACHMENT_TYPES.register("player_souls", () -> AttachmentType.builder(() -> new com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls())
                    .serialize(com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls.CODEC).build());

    public static final Supplier<AttachmentType<java.util.UUID>> OWNER_UUID = ATTACHMENT_TYPES.register(
            "owner_uuid",
            () -> AttachmentType.builder(() -> new java.util.UUID(0, 0))
                    .serialize(net.minecraft.core.UUIDUtil.CODEC).build()
    );
}