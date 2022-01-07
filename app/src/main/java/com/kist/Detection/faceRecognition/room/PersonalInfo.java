package com.kist.Detection.faceRecognition.room;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kist.Detection.faceRecognition.tflite.SimilarityClassifier;

@Entity(tableName = "PersonalInfo")
public class PersonalInfo {
    @PrimaryKey(autoGenerate = true)
    private int main_id;

    private String name;

    @Embedded private final SimilarityClassifier.Recognition recognition;
    // private Bitmap crop;

    public PersonalInfo(String name, SimilarityClassifier.Recognition recognition){
        this.name = name;
        this.recognition = recognition;
    }

    public SimilarityClassifier.Recognition getRecognition() {
        return recognition;
    }

    public int getMain_id() {
        return main_id;
    }

    public void setMain_id(int main_id) {
        this.main_id = main_id;
    }

    public String getName() {
        return name;
    }
}
