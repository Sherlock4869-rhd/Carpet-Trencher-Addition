package com.carpet.trencher.addition.network;

import com.carpet.trencher.addition.network.track.TrackListPayload;
import com.carpet.trencher.addition.network.track.TrackUpdatePayload;
import com.carpet.trencher.addition.utils.NetworkUtils;

public final class CTANetworking {

    private CTANetworking() {}

    public static void init() {
        NetworkUtils.registerS2C(TrackUpdatePayload.TYPE, TrackUpdatePayload.CODEC);
        NetworkUtils.registerS2C(TrackListPayload.TYPE, TrackListPayload.CODEC);
    }

}
