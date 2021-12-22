package com.kist.temirobot;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.constants.Platform;
import com.robotemi.sdk.listeners.OnTelepresenceEventChangedListener;
import com.robotemi.sdk.model.CallEventModel;
import com.robotemi.sdk.permission.Permission;

public class moduleTemi {
    public Robot robot;
    
    
    public moduleTemi(){
        robot = Robot.Companion.getInstance();
    }
}
