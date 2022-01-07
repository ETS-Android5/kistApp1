package com.kist.Detection.faceRecognition.room;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;

public class Converters {
    @TypeConverter
    public static String fromFloatList(Object obj){
        float[][] f = (float[][])obj;
        return new Gson().toJson(f);
    }

    @TypeConverter
    public static Object toFloatList(String value){
        Type listType = new TypeToken<float[][]>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @TypeConverter
    public static Bitmap toBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
