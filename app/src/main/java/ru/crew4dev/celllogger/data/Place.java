package ru.crew4dev.celllogger.data;

import java.util.Date;

import androidx.room.Entity;
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

    public Place() {
        this.startDate = new Date();
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
