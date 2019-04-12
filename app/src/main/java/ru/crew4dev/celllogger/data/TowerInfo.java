package ru.crew4dev.celllogger.data;

import java.util.Date;

public class TowerInfo {
    Date date;
    int cellId;
    int tac;
    int dbm;
    int mcc;
    int mnc;

    public TowerInfo(int cellId, int tac, int dbm, int mcc, int mnc) {
        this.date = new Date();
        this.cellId = cellId;
        this.tac = tac;
        this.dbm = dbm;
        this.mcc = mcc;
        this.mnc = mnc;
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

    public int getMcc() {
        return mcc;
    }

    public int getMnc() {
        return mnc;
    }

    @Override
    public String toString() {
        return "TowerInfo{" +
                "date=" + date +
                ", cellId=" + cellId +
                ", tac=" + tac +
                ", dbm=" + dbm +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                '}';
    }
}
