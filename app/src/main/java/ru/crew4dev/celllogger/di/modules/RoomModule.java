package ru.crew4dev.celllogger.di.modules;

import android.content.Context;
import android.util.Log;

import javax.inject.Singleton;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import dagger.Module;
import dagger.Provides;
import ru.crew4dev.celllogger.data.LocalDatabase;

@Module(includes = ContextModule.class)
public class RoomModule {
    private String dbName;
    public RoomModule(String dbName) {
        this.dbName = dbName;
    }

    @Provides
    @Singleton
    public LocalDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, LocalDatabase.class, dbName).addMigrations(MIGRATION_1_2).fallbackToDestructiveMigration().build();
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.d("LocalDatabase", "migrate 1-2");
            database.execSQL("ALTER TABLE place ADD COLUMN name TEXT");
        }
    };
}
