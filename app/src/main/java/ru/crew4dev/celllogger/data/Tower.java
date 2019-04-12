package ru.crew4dev.celllogger.data;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import ru.mkb.dsamobile.db.converters.DateConverter;

@Entity(tableName = "tower")
public class Tower {
    @PrimaryKey
    public Long towerId;
    @TypeConverters({DateConverter.class})
    public Date date;
    public Long placeId;

    public int cellId;
    public int lac;
    public int dbm;
    public int mcc;
    public int mnc;

    public Tower() {
    }

    @Ignore
    public Tower(int cellId, int lac, int dbm, int mcc, int mnc) {
        this.date = new Date();
        this.cellId = cellId;
        this.lac = lac;
        this.dbm = dbm;
        this.mcc = mcc;
        this.mnc = mnc;
    }

    public Long getTowerId() {
        return towerId;
    }

    public Date getDate() {
        return date;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public int getCellId() {
        return cellId;
    }

    public int getLac() {
        return lac;
    }

    public int getDbm() {
        return dbm;
    }

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    @Override
    public String toString() {
        return "Tower{" +
                "towerId=" + towerId +
                ", startDate=" + date +
                ", placeId=" + placeId +
                ", cellId=" + cellId +
                ", lac=" + lac +
                ", dbm=" + dbm +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                '}';
    }
}
