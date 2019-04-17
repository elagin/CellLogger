package ru.crew4dev.celllogger.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ru.crew4dev.celllogger.db.interfaces.CollectDao;

@Database(entities = {Tower.class, Place.class}, version = 3)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract CollectDao collectDao();
}
