package com.meirco.babyobservations.di;

import android.app.Application;

/**
 * Created by nitsa_000 on 19-Aug-15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Injector injector = Injector.getInstance();
        injector.initAppComponent(this);
        injector.getAppComponent().inject(this);
    }
}
