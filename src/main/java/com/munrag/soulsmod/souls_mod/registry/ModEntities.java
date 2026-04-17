package com.munrag.soulsmod.souls_mod.registry;

import com.munrag.soulsmod.souls_mod.Souls_mod;
import com.munrag.soulsmod.souls_mod.entity.monster.shadow.ShadowZombie;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModEntities {
    // La oficina de registro de entidades
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, Souls_mod.MODID);

    // Registramos el Shadow Zombie
    // Usamos MobCategory.MONSTER para que tenga IA de combate, pero lo controlaremos.
    // .sized(0.6f, 1.95f) es el tamaño exacto de un zombi vanilla.
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowZombie>> SHADOW_ZOMBIE = ENTITIES.register("shadow_zombie",
            () -> EntityType.Builder.of(ShadowZombie::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .build("shadow_zombie"));
    // Registramos la Entidad Alma
    // Usamos MobCategory.MISC porque no es un monstruo que pelee, es un "objeto" interactivo del mundo
    public static final DeferredHolder<EntityType<?>, EntityType<com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity>> SOUL_ENTITY = ENTITIES.register("soul_entity",
            () -> EntityType.Builder.<com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity>of(com.munrag.soulsmod.souls_mod.entity.soul.SoulEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f) // Una caja de colisión pequeña (medio bloque)
                    .clientTrackingRange(10)
                    .build("soul_entity"));
}