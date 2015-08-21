package com.meirco.babyobservations.di;

import com.meirco.babyobservations.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by nitsa_000 on 19-Aug-15.
 */
@Singleton
@Component(modules = Mod.class)
public interface AppComponent {
    void inject(App app);
    void inject(MainActivity activity);
}
