package com.munrag.soulsmod.souls_mod.client;

import com.munrag.soulsmod.souls_mod.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(modid = "souls_mod", value = Dist.CLIENT)
public class ClientInputEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        // Verificamos si se presionó nuestra tecla R
        if (KeyBindings.OPEN_ARMY_MENU.consumeClick()) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                // Escaneamos el inventario buscando el Monarch Book
                boolean hasBook = false;
                for (ItemStack itemStack : player.getInventory().items) {
                    if (itemStack.is(ModItems.MONARCH_BOOK.get())) {
                        hasBook = true;
                        break;
                    }
                }

                // También revisamos la mano secundaria (escudo) por si acaso
                if (player.getOffhandItem().is(ModItems.MONARCH_BOOK.get())) {
                    hasBook = true;
                }

                // La Acción
                if (hasBook) {
                    // ¡Abrimos la pantalla del Ejército!
                    Minecraft.getInstance().setScreen(new com.munrag.soulsmod.souls_mod.client.gui.MonarchArmyScreen());
                } else {
                    // No tiene el libro (mantenemos el mensaje de error)
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§cTus sombras no responden. Necesitas el Monarch Book."), true);
                }
            }
        }
    }
}