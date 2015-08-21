package com.meirco.babyobservations.di;

/**
 * Created by nitsa_000 on 19-Aug-15.
 */
public class Injector {
    private static Injector sInstance;

    public static Injector getInstance() {
        if (null == sInstance) {
            sInstance = new Injector();
        }
        return sInstance;
    }

    private Injector() {
    }

    private AppComponent mAppComponent;

    void initAppComponent(App app) {
        mAppComponent = DaggerAppComponent.builder().mod(new Mod(app)).build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

}
