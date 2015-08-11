package com.meirco.babyobservations.utils;

import android.content.Context;

/**
 * Created by nitsa_000 on 10-Aug-15.
 */
public class PreferencesUtils {

    private final Context mContext;
    private static PreferencesUtils sInstance;

    public static PreferencesUtils getInstance(Context context) {
        if (null == sInstance) {
            sInstance = new PreferencesUtils(context);
        }
        return sInstance;
    }

    private PreferencesUtils(Context context) {
        mContext = context;
    }

}
