package com.munrag.soulsmod.souls_mod.entity.soul;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SoulEntity extends Entity {

    // Guardaremos el ID del monstruo que murió (ej. "minecraft:zombie") para saber qué estadísticas mostrar luego
    private static final EntityDataAccessor<String> SOURCE_MOB_ID = SynchedEntityData.defineId(SoulEntity.class, EntityDataSerializers.STRING);

    // Temporizador de vida: 20 ticks por segundo * 120 segundos = 2400 ticks (2 minutos)
    private int lifespan = 2400;

    public SoulEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = true; // ¡Incorpórea! Ignora gravedad, paredes y empujones
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SOURCE_MOB_ID, "minecraft:zombie"); // Valor por defecto
    }

    public void setSourceMob(String mobId) {
        this.entityData.set(SOURCE_MOB_ID, mobId);
    }

    public String getSourceMob() {
        return this.entityData.get(SOURCE_MOB_ID);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.lifespan--;
            if (this.lifespan <= 0) {
                this.discard(); // El alma se desvanece si no la capturas a tiempo
            }
        } else {
            // Un pequeño movimiento visual: flota suavemente hacia arriba muy despacio
            this.setDeltaMovement(0, 0.01, 0);
            this.setPos(this.getX(), this.getY() + this.getDeltaMovement().y, this.getZ());
        }
    }

    // --- LA INTERACCIÓN (CLIC DERECHO) ---
    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {

            // Las interfaces gráficas SOLO existen del lado del Cliente
            if (this.level().isClientSide()) {
                // Abrimos la pantalla y le pasamos ESTA alma exacta
                net.minecraft.client.Minecraft.getInstance().setScreen(
                        new com.munrag.soulsmod.souls_mod.client.gui.SoulCaptureScreen(this)
                );
            }

            // Le decimos al juego que la interacción fue un éxito para que haga la animación de la mano
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return InteractionResult.PASS;
    }

    // --- REGLAS DE LA ENTIDAD INCORPÓREA ---
    @Override
    public boolean isAttackable() {
        return false; // Las espadas la atraviesan
    }

    @Override
    public boolean isPushable() {
        return false; // Los jugadores no pueden empujarla caminando
    }

    @Override
    public boolean isPickable() {
        return true; // ¡VITAL! Permite que la mira del jugador la apunte para el clic derecho
    }

    // --- GUARDADO DE DATOS ---
    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("SourceMob")) setSourceMob(tag.getString("SourceMob"));
        if (tag.contains("Lifespan")) this.lifespan = tag.getInt("Lifespan");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("SourceMob", getSourceMob());
        tag.putInt("Lifespan", this.lifespan);
    }
}