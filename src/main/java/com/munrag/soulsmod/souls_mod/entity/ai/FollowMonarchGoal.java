package com.munrag.soulsmod.souls_mod.entity.ai;

import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;
import java.util.UUID;

public class FollowMonarchGoal extends Goal {
    private final PathfinderMob mob;
    private final double speedModifier;
    private Player owner;
    private int timeToRecalcPath;

    public FollowMonarchGoal(PathfinderMob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.mob.hasData(ModAttachments.OWNER_UUID)) {
            return false;
        }

        UUID ownerUuid = this.mob.getData(ModAttachments.OWNER_UUID);
        if (ownerUuid == null) {
            return false;
        }

        Player player = this.mob.level().getPlayerByUUID(ownerUuid);
        if (player == null || !player.isAlive()) {
            return false;
        }

        if (this.mob.distanceToSqr(player) < 100.0D) { // 10 blocks = 100 sqr
            return false;
        }

        this.owner = player;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.owner == null || !this.owner.isAlive()) {
            return false;
        }
        if (this.mob.distanceToSqr(this.owner) <= 9.0D) { // 3 blocks = 9 sqr
            return false;
        }
        return true;
    }

    @Override
    public void start() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void stop() {
        this.owner = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (this.owner != null) {
            this.mob.getLookControl().setLookAt(this.owner, 10.0F, (float)this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.mob.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }
}