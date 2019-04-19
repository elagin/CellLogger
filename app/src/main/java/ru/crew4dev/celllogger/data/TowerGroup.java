package ru.crew4dev.celllogger.data;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Set;

import ru.crew4dev.celllogger.db.converters.ArrayConverter;

@Entity(tableName = "tower_group")
public class TowerGroup {

    private static final String TAG = "TowerGroup";

    @PrimaryKey
    public Long towerGroupId;
    public String name;
    @TypeConverters({ArrayConverter.class})
    public String towerList;

    public void addTower(Set<String> newList) {
        Set<String> oldList = ArrayConverter.fromString(towerList);
        Log.d(TAG, "towerList: " + towerList);
        Log.d(TAG, "List size: " + oldList.size());
        for (String item : newList) {
            oldList.add(item);
        }
        this.towerList = ArrayConverter.fromArrayList(oldList);
        Log.d(TAG, "towerList: " + towerList);
        Log.d(TAG, "List size: " + oldList.size());
    }
}
