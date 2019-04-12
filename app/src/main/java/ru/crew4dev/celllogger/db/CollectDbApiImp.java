package ru.mkb.dsamobile.db;

import javax.inject.Inject;

import ru.crew4dev.celllogger.data.LocalDatabase;
import ru.mkb.dsamobile.db.interfaces.CollectDbApiContract;

public class CollectDbApiImp implements CollectDbApiContract {
    private LocalDatabase collectDatabase;

    @Inject
    public CollectDbApiImp(LocalDatabase collectDatabase) {
        this.collectDatabase = collectDatabase;
    }
}
