package com.meirco.babyobservations.di;

import android.content.Context;
import android.location.LocationManager;

import com.meirco.babyobservations.db.DbHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by nitsa_000 on 19-Aug-15.
 */
@Module
public class Mod {
    private final App mApp;

    public Mod(App app) {
        mApp = app;
    }

    @Provides
    public App provideApp() {
        return mApp;
    }

    @Provides
    public Context provideAppContext() {
        return mApp;
    }

    @Provides
    public LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    public DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }
}
