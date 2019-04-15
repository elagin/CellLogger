package ru.crew4dev.celllogger.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import ru.mkb.dsamobile.db.converters.DateConverter;

@Entity(tableName = "place")
public class Place {
    @PrimaryKey
    public Long placeId;
    @TypeConverters({DateConverter.class})
    public Date startDate;
    @TypeConverters({DateConverter.class})
    public Date endDate;
    public String name;

    public void setTowerList(List<Tower> towerList) {
        this.towerList = towerList;
    }

    @Ignore
    public List<Tower> towerList = new ArrayList<>();

    public Place() {
        this.startDate = new Date();
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeId=" + placeId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
