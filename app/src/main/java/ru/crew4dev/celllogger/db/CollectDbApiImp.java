package ru.crew4dev.celllogger.db;

import javax.inject.Inject;

import ru.crew4dev.celllogger.data.LocalDatabase;
import ru.crew4dev.celllogger.db.interfaces.CollectDbApiContract;

public class CollectDbApiImp implements CollectDbApiContract {
    private LocalDatabase collectDatabase;

    @Inject
    public CollectDbApiImp(LocalDatabase collectDatabase) {
        this.collectDatabase = collectDatabase;
    }
}
