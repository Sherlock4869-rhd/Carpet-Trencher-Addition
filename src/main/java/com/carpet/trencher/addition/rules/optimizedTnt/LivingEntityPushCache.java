package com.carpet.trencher.addition.rules.optimizedTnt;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class LivingEntityPushCache {

    private final Map<CacheKey, CacheValue> cache = new HashMap<>();

    public CacheValue get(LivingEntity entity) {
        return cache.get(CacheKey.of(entity));
    }

    public void put(LivingEntity entity, CacheValue value) {
        cache.put(CacheKey.of(entity), value);
    }

    public record CacheKey(
            EntityType<?> entityType,
            Vec3 position,
            boolean baby
    ) {
        public static CacheKey of(LivingEntity entity) {
            return new CacheKey(entity.getType(), entity.position(), entity.isBaby());
        }
    }

    public record CacheValue(
            Vec3 push,
            float damage
    ) {
    }
}