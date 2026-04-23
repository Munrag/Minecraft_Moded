package com.munrag.soulsmod.souls_mod.events;

import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;

import com.munrag.soulsmod.souls_mod.entity.ai.FollowMonarchGoal;
import com.munrag.soulsmod.souls_mod.entity.ai.MonarchHurtByTargetGoal;
import com.munrag.soulsmod.souls_mod.entity.ai.MonarchHurtTargetGoal;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.UUID;

@EventBusSubscriber(modid = "souls_mod")
public class MobServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Mob mob) {
            if (mob.hasData(ModAttachments.OWNER_UUID)) {
                // Clear the target selector to make the mob completely neutral
                mob.targetSelector.removeAllGoals((goal) -> true);

                mob.targetSelector.addGoal(1, new MonarchHurtByTargetGoal(mob));
                mob.targetSelector.addGoal(2, new MonarchHurtTargetGoal(mob));

                // Also clear standard goals just to be safe they don't do weird vanilla AI stuff,
                // but the prompt only explicitly asked for targetSelector.
                // Let's stick to targetSelector to ensure they just don't attack the player (or anyone).

                if (mob instanceof PathfinderMob pathfinderMob) {
                    pathfinderMob.goalSelector.addGoal(3, new FollowMonarchGoal(pathfinderMob, 1.25));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob && mob.hasData(ModAttachments.OWNER_UUID)) {
            UUID ownerUuid = mob.getData(ModAttachments.OWNER_UUID);
            if (event.getOriginalAboutToBeSetTarget() instanceof net.minecraft.world.entity.player.Player) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getSource().getEntity() instanceof Mob attacker && attacker.hasData(ModAttachments.OWNER_UUID)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingConversionPre(LivingConversionEvent.Pre event) {
        if (event.getEntity() instanceof Mob mob && mob.hasData(ModAttachments.OWNER_UUID)) {
            // Cancel the conversion event so the summoned zombie dies instead of becoming a drowned and losing its owner
            event.setCanceled(true);
            event.setConversionTimer(0);
        }
    }
}
