package com.kist.Detection.faceRecognition.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = com.kist.Detection.faceRecognition.room.PersonalInfo.class, version = 1)
@TypeConverters(com.kist.Detection.faceRecognition.room.Converters.class)
public abstract class PersonalInfoDatabase extends RoomDatabase {
    private static PersonalInfoDatabase INSTANCE;

    public abstract com.kist.Detection.faceRecognition.room.PersonalInfoDao personalInfoDao();

    public static PersonalInfoDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            /*INSTANCE = Room.databaseBuilder(
                    context, PersonalInfoDatabase.class, "personalInfo-db").setJournalMode(JournalMode.TRUNCATE).build();*/
            INSTANCE = Room.databaseBuilder(
                    context, PersonalInfoDatabase.class, "personalInfo-db").build();
        }
        return INSTANCE;
    }



    public static void destroyInstance(){
        INSTANCE = null;
    }
}
