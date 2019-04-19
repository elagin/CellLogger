package ru.crew4dev.celllogger.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import ru.crew4dev.celllogger.db.converters.ArrayConverter;

@Entity(tableName = "tower_group")
public class TowerGroup {
    @PrimaryKey
    public Long towerGroupId;
    public String name;
    @TypeConverters({ArrayConverter.class})
    public String towerList;
}
