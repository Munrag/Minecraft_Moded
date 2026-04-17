package com.munrag.soulsmod.souls_mod.world.mana;

import com.munrag.soulsmod.souls_mod.network.ManaSyncPayload;
import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "souls_mod")
public class ManaServerEvents {

    // Se ejecuta cada vez que el jugador entra al mundo
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMana(player);
        }
    }

    // Se ejecuta cada tick (20 veces por segundo)
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Solo regeneramos cada 2 segundos (40 ticks) para no saturar la red
            if (player.tickCount % 40 == 0) {
                PlayerMana manaData = player.getData(ModAttachments.PLAYER_MANA);
                int max = manaData.getMaxMana(player);

                if (manaData.getCurrentMana() < max) {
                    manaData.setCurrentMana(manaData.getCurrentMana() + 1);
                    syncMana(player);
                }
            }
        }
    }

    // Función auxiliar para enviar el paquete de red
    public static void syncMana(ServerPlayer player) {
        PlayerMana data = player.getData(ModAttachments.PLAYER_MANA);
        // Enviamos el número actual de maná al cliente específico
        PacketDistributor.sendToPlayer(player, new ManaSyncPayload(data.getCurrentMana()));
    }
}