package com.kist.kistapp1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.listeners.OnDetectionStateChangedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;

import java.util.Arrays;
import java.util.List;

public class TemiPatrolActivity extends AppCompatActivity implements OnRobotReadyListener, OnDetectionStateChangedListener, OnGoToLocationStatusChangedListener {
    Robot robot;
    List<String> locations;
    Integer index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.black_screen);
        // add listeners
        robot = Robot.getInstance();
        robot.addOnRobotReadyListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);
        robot.addOnDetectionStateChangedListener(this);
    }

    private void InitializeLocationData(){
        // locations = robot.getLocations();
        locations = Arrays.asList("티비", "내자리");
    }

    @Override
    public void onDetectionStateChanged(int i) {
        // human body is detected
        if (i == 2){
            robot.stopMovement();
            Intent intent = new Intent(TemiPatrolActivity.this, MainActivity.class);
            intent.putExtra("start faceRecognition",true);
            startActivity(intent);
        }
    }

    @Override
    public void onGoToLocationStatusChanged(@NonNull String s, @NonNull String s1, int i, @NonNull String s2) {
        if (index < locations.size() && s1.equals("complete")){
            index += 1;
            if (index >= locations.size()){
                index = 0;
            }
            robot.goTo(locations.get(index));
        }
    }

    @Override
    public void onRobotReady(boolean b) {
        InitializeLocationData();
        robot.goTo(locations.get(index++));
        robot.toggleNavigationBillboard(true);
        robot.setDetectionModeOn(true, (float)2.0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        robot.removeOnGoToLocationStatusChangedListener(this);
        robot.removeOnRobotReadyListener(this);
        robot.removeOnDetectionStateChangedListener(this);
    }
}
