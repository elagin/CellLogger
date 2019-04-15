package ru.crew4dev.celllogger;

import android.app.Application;

import androidx.room.Room;
import ru.crew4dev.celllogger.data.LocalDatabase;

public class App extends Application {
    private static App instance;
    private static LocalDatabase mDb;

    public static LocalDatabase db() {
        return instance.mDb;
    }

    public static App getApplication() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mDb = Room.databaseBuilder(this, LocalDatabase.class, "local_db")
                .allowMainThreadQueries()
                .build();
    }
}