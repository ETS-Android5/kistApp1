package com.kist.kistapp1;

//import android.util.Log;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

import com.kist.temirobot.moduleTemi;

import static android.content.Context.AUDIO_SERVICE;

public class ModuleManager {
    Context context;

    ModuleManager m_moduleManager;

    boolean m_bContinue = true;

    Thread m_hGetCommandThread;
    String m_strLastCommand = "";

    AppCompatActivity m_mainAppActivity;

    moduleTemi moduleTemi;

    public ModuleManager(AppCompatActivity mainApp) {

        m_moduleManager = this;
        m_mainAppActivity = mainApp;
        moduleTemi = new moduleTemi();
    }
}
