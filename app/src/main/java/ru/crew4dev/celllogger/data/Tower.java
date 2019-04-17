package ru.crew4dev.celllogger.data;

import java.util.Date;
import java.util.Objects;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import ru.crew4dev.celllogger.db.converters.DateConverter;

@Entity(tableName = "tower")
public class Tower {
    @PrimaryKey
    public Long towerId;
    @TypeConverters({DateConverter.class})
    public Date date;
    @TypeConverters({DateConverter.class})
    public Date endDate;
    public Long placeId;

    public int cellId;
    public int lac;
    public int dbm;
    public int mcc;
    public int mnc;

    @Ignore
    private boolean isSelected = false;

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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tower tower = (Tower) o;
        return cellId == tower.cellId &&
                lac == tower.lac &&
                mcc == tower.mcc &&
                mnc == tower.mnc;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "Tower{" +
                "towerId=" + towerId +
                ", date=" + date +
                ", endDate=" + endDate +
                ", placeId=" + placeId +
                ", cellId=" + cellId +
                ", lac=" + lac +
                ", dbm=" + dbm +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                '}';
    }
}
