package com.munrag.soulsmod.souls_mod.network;

import com.munrag.soulsmod.souls_mod.client.ClientManaData;
import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import com.munrag.soulsmod.souls_mod.world.mana.ManaServerEvents;
import com.munrag.soulsmod.souls_mod.world.mana.PlayerMana;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "souls_mod")
public class ModNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("souls_mod");

        // 1. El que ya teníamos (Servidor -> Cliente) para actualizar la barra azul
        registrar.playToClient(
                ManaSyncPayload.TYPE,
                ManaSyncPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClientManaData.set(payload.mana());
                    });
                }
        );

        registrar.playToClient(
                SoulSyncPayload.TYPE,
                SoulSyncPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        com.munrag.soulsmod.souls_mod.client.gui.ClientSoulData.set(payload.souls());
                    });
                }
        );

        // --- 2. NUEVO: Cliente -> Servidor (Para invocar) ---
        registrar.playToServer(
                SummonMobPayload.TYPE,
                SummonMobPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        // Obtenemos al jugador que presionó el botón (en el servidor)
                        if (context.player() instanceof ServerPlayer player) {

                            // Revisamos su mochila de maná
                            PlayerMana manaData = player.getData(ModAttachments.PLAYER_MANA);

                            // ¿Tiene suficiente maná?
                            if (manaData.getCurrentMana() >= payload.manaCost()) {

                                // 1. Le restamos el maná y le avisamos a su pantalla que se actualice
                                manaData.setCurrentMana(manaData.getCurrentMana() - payload.manaCost());
                                ManaServerEvents.syncMana(player);

                                // --- NUEVO SISTEMA DE INVOCACIÓN DE SOMBRAS LEALES ---
                                EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(payload.entityId()));
                                if (entityType != null) {
                                    Entity entity = entityType.create(player.serverLevel());
                                    if (entity != null) {
                                        entity.setPos(player.getX(), player.getY(), player.getZ());
                                        // Asignamos el UUID del jugador usando el nuevo attachment
                                        entity.setData(ModAttachments.OWNER_UUID, player.getUUID());
                                        player.level().addFreshEntity(entity);
                                    }
                                }
                            }
                        }
                    });
                }
        );
        // --- FASE 3: Procesar apuesta del Alma ---
        registrar.playToServer(
                ProcessSoulPayload.TYPE,
                ProcessSoulPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {

                            // 1. Buscamos el alma flotante en el mundo por su ID y la eliminamos
                            net.minecraft.world.entity.Entity entity = player.level().getEntity(payload.soulEntityId());
                            if (entity instanceof com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity) {
                                entity.discard();
                            }

                            // 2. Si el jugador tuvo éxito en los dados, preparamos la Fase 4
                            if (payload.success()) {
                                // Un efecto de sonido épico para el servidor
                                player.level().playSound(null, player.blockPosition(),
                                        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                                        net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                                com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls soulData = player.getData(com.munrag.soulsmod.souls_mod.registry.ModAttachments.PLAYER_SOULS);
                                soulData.addSoul(new com.munrag.soulsmod.souls_mod.world.soul.CapturedSoul(payload.mobType(), payload.customName()));

                                context.reply(new SoulSyncPayload(soulData.getSouls()));

                                player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
                            }
                        }
                    });
                }
        );

        // --- FASE 5: Eliminar Alma ---
        registrar.playToServer(
                RemoveSoulPayload.TYPE,
                RemoveSoulPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                            com.munrag.soulsmod.souls_mod.world.soul.PlayerSouls soulData = player.getData(com.munrag.soulsmod.souls_mod.registry.ModAttachments.PLAYER_SOULS);
                            soulData.removeSoul(payload.customName());
                            context.reply(new SoulSyncPayload(soulData.getSouls()));
                        }
                    });
                }
        );
    }

}