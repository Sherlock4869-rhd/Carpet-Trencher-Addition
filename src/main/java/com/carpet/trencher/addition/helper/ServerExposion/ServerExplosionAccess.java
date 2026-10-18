package com.carpet.trencher.addition.helper.ServerExposion;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

//? if >= 1.21.2 {

//? } else {
/*
import net.minecraft.world.level.Level;
*/
//? }


import java.util.Map;

public interface ServerExplosionAccess {

    float cta$getRadius();

    ExplosionDamageCalculator cta$getDamageCalculator();

    Entity cta$getSource();

    DamageSource cta$getDamageSource();

    Map<Player, Vec3> cta$getHitPlayers();

    //? if >= 1.21.2 {
    ServerLevel cta$getLevel();
    Vec3 cta$getCenter();
    //? } else {
    /*
    Level cta$getLevel();
    double cta$getCenterX();
    double cta$getCenterY();
    double cta$getCenterZ();
    */
    //? }

}