package com.munrag.soulsmod.souls_mod.entity.monster.shadow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class ShadowZombie extends Zombie {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(ShadowZombie.class, EntityDataSerializers.OPTIONAL_UUID);

    public ShadowZombie(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(false);
    }

    // --- SISTEMA DE LEALTAD ---
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (getOwnerUUID() != null) { tag.putUUID("OwnerUUID", getOwnerUUID()); }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) { setOwnerUUID(tag.getUUID("OwnerUUID")); }
    }

    // --- ESTADÍSTICAS ---
    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 26.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    // --- INTELIGENCIA ARTIFICIAL ---
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        // ¡EL ARREGLO ESTÁ AQUÍ! Usamos nuestra meta personalizada "FollowMonarchGoal"
        // (Entidad, Velocidad al seguirte, Distancia para empezar a caminar, Distancia para detenerse)
        this.goalSelector.addGoal(5, new FollowMonarchGoal(this, 1.2D, 8.0F, 2.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 5, true, false,
                (entity) -> entity instanceof net.minecraft.world.entity.monster.Enemy && !(entity instanceof ShadowZombie)));
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if (getOwnerUUID() != null && target.getUUID().equals(getOwnerUUID())) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for(int i = 0; i < 2; ++i) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    // --- NUESTRA PROPIA META DE SEGUIMIENTO ---
    class FollowMonarchGoal extends Goal {
        private final ShadowZombie zombie;
        private LivingEntity owner;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;

        public FollowMonarchGoal(ShadowZombie zombie, double speed, float startDist, float stopDist) {
            this.zombie = zombie;
            this.speedModifier = speed;
            this.startDistance = startDist;
            this.stopDistance = stopDist;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            // Revisa si la sombra tiene un Monarca asignado
            UUID ownerId = this.zombie.getOwnerUUID();
            if (ownerId == null) return false;

            // Busca al Monarca en el mundo
            Player player = this.zombie.level().getPlayerByUUID(ownerId);
            if (player == null || player.isSpectator()) return false;

            // Si estás muy cerca (menos de 8 bloques), no hace nada
            if (this.zombie.distanceToSqr(player) < (this.startDistance * this.startDistance)) {
                return false;
            }

            this.owner = player;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.owner == null || !this.owner.isAlive()) return false;
            if (this.zombie.getNavigation().isDone()) return false;
            // Continúa caminando hasta que esté a menos de 2 bloques de ti
            return this.zombie.distanceToSqr(this.owner) > (this.stopDistance * this.stopDistance);
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.zombie.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.zombie.getLookControl().setLookAt(this.owner, 10.0F, (float)this.zombie.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10);
                this.zombie.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}