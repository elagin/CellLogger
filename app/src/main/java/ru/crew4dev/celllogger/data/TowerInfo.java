package ru.crew4dev.celllogger.data;

import java.util.Date;

public class TowerInfo {
    int cellId;
    int tac;
    int dbm;
    Date date;

    public TowerInfo(int cellId, int tac, int dbm) {
        this.date = new Date();
        this.cellId = cellId;
        this.tac = tac;
        this.dbm = dbm;
    }

    public Date getDate() {
        return date;
    }

    public int getCellId() {
        return cellId;
    }

    public int getTac() {
        return tac;
    }

    public int getDbm() {
        return dbm;
    }

    @Override
    public String toString() {
        return "TowerInfo{" +
                "date=" + date +
                ", cellId=" + cellId +
                ", tac=" + tac +
                ", dbm=" + dbm +
                '}';
    }
}
