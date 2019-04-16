package ru.crew4dev.celllogger.db.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import ru.crew4dev.celllogger.data.Place;
import ru.crew4dev.celllogger.data.Tower;

import java.util.List;

@Dao
public abstract class CollectDao {

    @Query("select * from place")
    public abstract List<Place> getPlaces();

    @Query("select * from place where placeId = :placeId")
    public abstract Place getPlace(Long placeId);

    @Query("DELETE from place where placeId = :placeId")
    public abstract void deletePlace(Long placeId);

    /*
    @Query("select * from sessions where sessionId = :sessionId")
    public abstract Session getSessionBySession(Long sessionId);

    @Query("select * from sessions where createDate < :startDate")
    public abstract List<Session> getSessionByDate(Long startDate);
*/
    @Insert
    public abstract Long insert(Place item);

    @Update
    public abstract void update(Place item);


    @Query("select * from tower")
    public abstract List<Tower> getTowers();

    @Query("select * from tower where placeId = :placeId")
    public abstract List<Tower> getTowers(Long placeId);

    @Insert
    public abstract Long insert(Tower item);


    @Query("DELETE from tower where placeId = :placeId")
    public abstract void deleteTowers(Long placeId);
/*
    @Insert
    public abstract Long insert(NfcStepDb item);

    @Query("select * from nfc_step where sessionId = :sessionId")
    public abstract NfcStepDb getNfcBySession(Long sessionId);

    @Query("delete from nfc_step where sessionId = :sessionId")
    public abstract void deleteNfc(Long sessionId);

    @Insert
    public abstract Long insert(PhotoStepDb item);

    @Query("select * from photo_step where sessionId = :sessionId")
    public abstract List<PhotoStepDb> getPhotoBySession(Long sessionId);

    @Query("select * from photo_step where sessionId = :sessionId AND stepID = :stepID")
    public abstract PhotoStepDb getPhotoBySession(Long sessionId, Integer stepID);

    @Query("delete from photo_step where sessionId = :sessionId")
    public abstract void deletePhoto(Long sessionId);

    @Query("delete from photo_step where sessionId = :sessionId AND stepID = :stepID")
    public abstract void deletePhoto(Long sessionId, Integer stepID);

    @Insert
    public abstract Long insert(InfoStepDb item);
*/
//    @Query("select * from info_step where sessionId = :sessionId/* AND stepID = :stepID*/")
//    public abstract InfoStepDb getInfoBySession(Long sessionId/*, Integer stepID*/);

//    @Query("delete from info_step where sessionId = :sessionId/* AND stepID = :stepID*/")
//    public abstract void deleteInfo(Long sessionId/*, Integer stepID*/);
}
