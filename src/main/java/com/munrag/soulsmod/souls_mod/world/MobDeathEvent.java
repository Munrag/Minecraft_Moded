package com.munrag.soulsmod.souls_mod.world;

import com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity;
import com.munrag.soulsmod.souls_mod.registry.ModEntities;
import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = "souls_mod")
public class MobDeathEvent {

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        // Nos aseguramos de que esto corra en el Servidor y que el muerto sea un Monstruo (no queremos almas de vacas ni de jugadores por ahora)
        if (!event.getEntity().level().isClientSide() && event.getEntity() instanceof Monster monster) {

            if (monster.hasData(ModAttachments.OWNER_UUID)) {
                return;
            }

            // Probabilidad de soltar el alma (1.0f = 100% para pruebas)
            if (monster.level().random.nextFloat() <= 1.0f) {

                // Creamos el alma
                SoulEntity soul = ModEntities.SOUL_ENTITY.get().create(monster.level());

                if (soul != null) {
                    // Obtenemos el ID original del monstruo que acaba de morir (ej. "minecraft:skeleton")
                    String mobId = BuiltInRegistries.ENTITY_TYPE.getKey(monster.getType()).toString();
                    soul.setSourceMob(mobId);

                    // La posicionamos un poco arriba del cadáver
                    soul.setPos(monster.getX(), monster.getY() + 0.5D, monster.getZ());

                    // ¡La agregamos al mundo!
                    monster.level().addFreshEntity(soul);
                }
            }
        }
    }
}