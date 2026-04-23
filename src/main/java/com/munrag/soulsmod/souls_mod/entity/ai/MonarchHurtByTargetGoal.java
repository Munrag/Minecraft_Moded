package com.munrag.soulsmod.souls_mod.entity.ai;

import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class MonarchHurtByTargetGoal extends TargetGoal {
    private LivingEntity attacker;

    public MonarchHurtByTargetGoal(Mob mob) {
        super(mob, false);
    }

    @Override
    public boolean canUse() {
        if (!this.mob.hasData(ModAttachments.OWNER_UUID)) {
            return false;
        }

        UUID ownerUuid = this.mob.getData(ModAttachments.OWNER_UUID);
        Player owner = this.mob.level().getPlayerByUUID(ownerUuid);

        if (owner == null) {
            return false;
        }

        this.attacker = owner.getLastHurtByMob();

        if (this.attacker == null) {
            return false;
        }

        if (this.attacker instanceof Player) {
            return false;
        }

        return this.canAttack(this.attacker, net.minecraft.world.entity.ai.targeting.TargetingConditions.DEFAULT);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.attacker);
        super.start();
    }
}