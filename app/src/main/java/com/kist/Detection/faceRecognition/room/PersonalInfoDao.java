package com.kist.Detection.faceRecognition.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PersonalInfoDao {
    @Query("SELECT * FROM PersonalInfo")
    LiveData<List<com.kist.Detection.faceRecognition.room.PersonalInfo>> getAll();

    @Insert
    void insert(com.kist.Detection.faceRecognition.room.PersonalInfo personalInfo);

    @Update
    void update(com.kist.Detection.faceRecognition.room.PersonalInfo personalInfo);

    @Delete
    void delete(com.kist.Detection.faceRecognition.room.PersonalInfo personalInfo);

    @Query("DELETE FROM PersonalInfo")
    void deleteAll();

}
