package ru.crew4dev.celllogger.db.converters;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public Long fromDate(Date value) {
        if (value == null) {
            return null;
        } else {
            return value.getTime() ;
        }
    }

    @TypeConverter
    public Date toDate(Long value) {
        if (value == null) {
            return null;
        } else {
            return new Date(value);
        }
    }
}