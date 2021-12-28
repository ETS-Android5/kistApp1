package com.kist.kistapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;


import com.kist.Detection.humanDetection.DetectorActivity;
import com.kist.listView.LocationAdapter;
import com.kist.listView.LocationData;
import com.robotemi.sdk.Robot;
import com.robotemi.sdk.UserInfo;
import com.robotemi.sdk.constants.ContentType;
import com.robotemi.sdk.face.ContactModel;
import com.robotemi.sdk.face.OnContinuousFaceRecognizedListener;
import com.robotemi.sdk.face.OnFaceRecognizedListener;
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener;
import com.robotemi.sdk.listeners.OnLocationsUpdatedListener;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;

public class MainActivity extends AppCompatActivity implements OnRobotReadyListener, OnFaceRecognizedListener, OnContinuousFaceRecognizedListener, OnLocationsUpdatedListener, OnGoToLocationStatusChangedListener {
    Robot robot;
    ImageView imageViewFace;
    ArrayList<LocationData> locationDataList;
    ModuleManager m_moduleManager = new ModuleManager(this);
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final int REQUEST_CAMERA = 1;

    // Human detection
    public static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;

    //필요한 퍼미션
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA
    };
    //퍼미션 확인
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    //퍼미션 승인 요구
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CAMERA
            );
        }
    }

    @Override
    protected void onStop() {
        robot.removeOnRobotReadyListener(this);
        robot.removeOnLocationsUpdateListener(this);
        robot.removeOnGoToLocationStatusChangedListener(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitTemi();

        // 퍼미션 확인
        verifyStoragePermissions(this);

        // internet state check
        if (isConnectedInternet()) {
            // 화면 ON
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            MediaPlayer.create(MainActivity.this, R.raw.needtocheckinternetstate).start();
            finish();
        }
    }

    protected boolean isConnectedInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 테미
        robot.addOnRobotReadyListener(this);
    }

    public void InitTemi(){
        robot = m_moduleManager.moduleTemi.robot;
        robot.addOnFaceRecognizedListener(this);
        robot.addOnContinuousFaceRecognizedListener(this);
        robot.addOnTelepresenceEventChangedListener(callEventModel -> Log.d("onTelepresenceEvent", callEventModel.toString()));
        robot.addOnLocationsUpdatedListener(this);
        robot.addOnGoToLocationStatusChangedListener(this);

        // 영상통화 걸기
        Button callSeungwonBtn = (Button)findViewById(R.id.callSeungwonBtn);
        callOwnerBtn(callSeungwonBtn,"유승원한테 전화 걸기.");

        // 따라가기
        Button followMeBtn = (Button)findViewById(R.id.followMeBtn);
        followMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.beWithMe();
            }
        });

        // 상단바 표시
        Button showTopBarBtn = (Button) findViewById(R.id.showTopBarBtn);
        showTopBarBtn.setOnClickListener(view -> robot.showTopBar());

        // 얼굴인식
        imageViewFace = (ImageView)findViewById(R.id.faceView);

        Button startFaceRecogBtn = (Button) findViewById(R.id.faceStartBtn);
        startFaceRecogBtn.setOnClickListener(view -> startFaceRecognition());

        Button stopFaceRecogBtn = (Button) findViewById(R.id.faceStopBtn);
        stopFaceRecogBtn.setOnClickListener(view -> stopFaceRecognition());

        Button humanDetectionStartBtn = (Button)findViewById(R.id.humanDtStartBtn);
        humanDetectionStartBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DetectorActivity.class)));

        // 순찰 기능
        Button patrolBtn = (Button)findViewById(R.id.patrolBtn);
        patrolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.playSequence(robot.getAllSequences().get(0).getId());
            }
        });

        // 키오스크 모드 시작
        Button kioskBtn = (Button)findViewById(R.id.kioskBtn);
        kioskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.requestToBeKioskApp();
                ArrayList<Permission> p = new ArrayList<>();
                p.add(Permission.SETTINGS);
                robot.requestPermissions(p,1);
                robot.toggleWakeup(true);
            }
        });

        // 앱 전환
        Button changeAppBtn = (Button)findViewById(R.id.changeAppBtn);
        changeAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent receiveIntent = new Intent("com.kist.kistapp2.action.APP_SIGNAL");
                receiveIntent.putExtra("kistAppIsDead",true);
                sendBroadcast(receiveIntent);
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.kist.kistapp2");
                startActivity(intent);
                finish();
            }
        });
    }

    // 화면 밖 터치하면 키보드 내려감
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View focusView = getCurrentFocus();
        if (focusView != null){
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) event.getX(), y = (int) event.getY();
            if (!rect.contains(x, y)){
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onRobotReady(boolean b) {
        ArrayList<Permission> p = new ArrayList<>();
        p.add(Permission.SETTINGS);
        robot.requestPermissions(p,1);
        // robot.requestToBeKioskApp();
        robot.toggleWakeup(true);
        InitializeLocationData();
        ListView listView = (ListView)findViewById(R.id.listView);
        final LocationAdapter locationAdapter = new LocationAdapter(this, locationDataList);
        listView.setAdapter(locationAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                robot.goTo(locationAdapter.getItem(position).getLocationName());
            }
        });
    }

    @Override
    public void onContinuousFaceRecognized(@NotNull List contactModelList) {
        if (contactModelList.isEmpty()) {
            imageViewFace.setImageResource(R.drawable.question_mark);
            imageViewFace.setVisibility(View.VISIBLE);
        } else {
            String imageKey = null;
            for ( int i = 0 ; i < contactModelList.size(); i++ ){
                ContactModel contactModel = (ContactModel) contactModelList.get(i);
                if (StringsKt.isBlank((CharSequence) contactModel.getImageKey())){
                    continue;
                }
                imageKey = contactModel.getImageKey();
                break;
            }
            if (imageKey != null) {
                showFaceRecognitionImage(imageKey);
            }else {
                imageViewFace.setImageResource(R.drawable.xxxsign);
                imageViewFace.setVisibility(View.VISIBLE);
            }

            for ( int i = 0 ; i < contactModelList.size(); i++ ){
                ContactModel contactModel = (ContactModel) contactModelList.get(i);
            }
        }
    }

    @Override
    public void onFaceRecognized(@NotNull List contactModelList) {
        if (contactModelList.isEmpty()) {
            imageViewFace.setImageResource(R.drawable.question_mark);
            imageViewFace.setVisibility(View.VISIBLE);
        } else {
            String imageKey = null;
            for (int i = 0; i < contactModelList.size(); i++) {
                ContactModel contactModel = (ContactModel) contactModelList.get(i);
                if (StringsKt.isBlank((CharSequence) contactModel.getImageKey())) {
                    continue;
                }
                imageKey = contactModel.getImageKey();
                break;
            }
            if (imageKey != null) {
                showFaceRecognitionImage(imageKey);
            } else {
                imageViewFace.setImageResource(R.drawable.xxxsign);
                imageViewFace.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < contactModelList.size(); i++) {
                ContactModel contactModel = (ContactModel) contactModelList.get(i);
            }
        }
    }

    private void showFaceRecognitionImage(@NonNull String mediaKey) {
        if (mediaKey.isEmpty()) {
            imageViewFace.setImageResource(R.drawable.xxxsign);
            imageViewFace.setVisibility(View.VISIBLE);
            return;
        }
        Thread thread = new Thread(() -> {
            InputStream inputStream =
                    robot.getInputStreamByMediaKey(ContentType.FACE_RECOGNITION_IMAGE, mediaKey);
            runOnUiThread(() -> {
                imageViewFace.setVisibility(View.VISIBLE);
                imageViewFace.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
        thread.start();
    }

    private boolean requestPermissionIfNeeded(Permission permission, int requestCode){
        if (robot.checkSelfPermission(permission) == Permission.GRANTED){
            return false;
        }
        robot.requestPermissions(CollectionsKt.listOf(permission),requestCode);
        return true;
    }

    private void startFaceRecognition(){
        if (requestPermissionIfNeeded(Permission.FACE_RECOGNITION, 1)){
            return;
        }
        robot.startFaceRecognition();
    }

    private void stopFaceRecognition(){
        robot.stopFaceRecognition();
    }

    private void goToBtn(Button button,String sentence, String place){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                robot.goTo(place);
            }
        });
    }

    private void callOwnerBtn(Button button, String sentence){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo admin = robot.getAdminInfo();
                if (admin == null) {
                    return;
                }
                robot.startTelepresence(admin.getName(), admin.getUserId());
            }
        });
    }

    private void InitializeLocationData(){
        locationDataList = new ArrayList<LocationData>();
        List<String> locations = robot.getLocations();
        for(int i=0; i<locations.size(); i++){
            locationDataList.add(new LocationData(locations.get(i)));
        }
    }

    @Override
    public void onLocationsUpdated(@NonNull List<String> list) {

    }

    @Override
    public void onGoToLocationStatusChanged(@NonNull String s, @NonNull String s1, int i, @NonNull String s2) {

    }
}