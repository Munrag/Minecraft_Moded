package com.munrag.soulsmod.souls_mod.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    // El "Registrador" principal de objetos de tu mod
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("souls_mod");

    // Registramos el libro. 'stacksTo(1)' significa que no se pueden apilar 64, ocupan un slot cada uno.
    public static final DeferredItem<Item> MONARCH_BOOK = ITEMS.register("monarch_book",
            () -> new Item(new Item.Properties().stacksTo(1))
    );
}