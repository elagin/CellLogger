package ru.crew4dev.celllogger.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import ru.mkb.dsamobile.db.interfaces.CollectDao;

@Database(entities = {Tower.class, Place.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract CollectDao collectDao();
}
