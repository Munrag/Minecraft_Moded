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

            if (this.mob.distanceToSqr(this.owner) > 144.0D) {
                this.teleportToOwner();
            } else if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.mob.getNavigation().moveTo(this.owner, this.speedModifier);
            }
        }
    }

    private void teleportToOwner() {
        net.minecraft.core.BlockPos ownerPos = this.owner.blockPosition();
        net.minecraft.world.level.Level level = this.mob.level();

        for (int i = 0; i < 10; i++) {
            int dx = this.mob.getRandom().nextInt(7) - 3;
            int dy = this.mob.getRandom().nextInt(5) - 2;
            int dz = this.mob.getRandom().nextInt(7) - 3;

            net.minecraft.core.BlockPos targetPos = ownerPos.offset(dx, dy, dz);

            boolean hasBlockBelow = !level.getBlockState(targetPos.below()).getCollisionShape(level, targetPos.below()).isEmpty();
            boolean isSpaceEmpty = level.getBlockState(targetPos).getCollisionShape(level, targetPos).isEmpty()
                                && level.getBlockState(targetPos.above()).getCollisionShape(level, targetPos.above()).isEmpty();

            if (hasBlockBelow && isSpaceEmpty) {
                this.mob.randomTeleport(targetPos.getX() + 0.5D, targetPos.getY(), targetPos.getZ() + 0.5D, false);
                this.mob.getNavigation().stop();
                return;
            }
        }
    }
}