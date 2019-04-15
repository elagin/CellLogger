package ru.crew4dev.celllogger.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    private Context context;

    public ContextModule(Context appContext) {
        context = appContext;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return context;
    }
}