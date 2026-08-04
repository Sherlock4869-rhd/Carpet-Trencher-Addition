package com.carpet.trencher.addition.utils;

import carpet.api.settings.Rule;
import static carpet.api.settings.RuleCategory.*;

public class CarpetTrencherAdditionSettings {

    public static final String CTA = "CTA";

    @Rule(
            categories = {CTA, FEATURE}
    )
    public static boolean preventExtremeTntMomentum = false;

    @Rule(
            categories = {CTA, FEATURE}
    )
    public static double tntInitialXVelocity = -1;

    @Rule(
            categories = {CTA, FEATURE}
    )
    public static boolean disableAmethystWaterGrowth = false;

    @Rule(
            categories = {CTA, FEATURE}
    )
    public static boolean waterWallLavaProtection = false;

    @Rule(
            categories = {CTA, FEATURE}
    )
    public static double explosionRayInit = -1;
}