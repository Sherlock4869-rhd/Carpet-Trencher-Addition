package com.carpet.trencher.addition.rules.optimizedTnt;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class TntPushCache {

    private final Map<Vec3, Vec3> cache = new HashMap<>();

    public boolean contains(Vec3 position) {
        return this.cache.containsKey(position);
    }

    public Vec3 get(Vec3 position) {
        return this.cache.get(position);
    }

    public void put(Vec3 position, Vec3 push) {
        this.cache.put(position, push);
    }

    public void clear() {
        this.cache.clear();
    }

}
