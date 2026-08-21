package com.carpet.trencher.addition.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Collection;
import java.util.Iterator;

public class ComponentUtils {
    public static MutableComponent formatComponentList(String prefix, Collection<Component> components) {

        MutableComponent message = Component.literal(prefix);

        if (components == null || components.isEmpty()) {
            return message.append(Component.literal("(empty)"));
        }

        Iterator<Component> iterator = components.iterator();

        while (iterator.hasNext()) {
            message.append(iterator.next());
            if (iterator.hasNext()) {
                message.append(Component.literal(", "));
            }
        }

        return message;

    }
}
