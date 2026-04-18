package com.munrag.soulsmod.souls_mod.events;

import com.munrag.soulsmod.souls_mod.registry.ModAttachments;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = "souls_mod")
public class MobServerEvents {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Mob mob) {
            if (mob.hasData(ModAttachments.OWNER_UUID)) {
                // Clear the target selector to make the mob completely neutral
                mob.targetSelector.removeAllGoals((goal) -> true);

                // Also clear standard goals just to be safe they don't do weird vanilla AI stuff,
                // but the prompt only explicitly asked for targetSelector.
                // Let's stick to targetSelector to ensure they just don't attack the player (or anyone).
            }
        }
    }
}
