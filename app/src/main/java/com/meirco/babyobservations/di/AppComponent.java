package com.meirco.babyobservations.di;

import com.meirco.babyobservations.MainActivity;
import com.meirco.babyobservations.ui.SessionFragment;
import com.meirco.babyobservations.ui.SessionsFragment;

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
    void inject(SessionsFragment sessionsFragment);
    void inject(SessionFragment sessionFragment);
}
