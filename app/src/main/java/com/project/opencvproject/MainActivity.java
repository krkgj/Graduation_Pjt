package com.project.opencvproject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.project.opencvproject.CameraActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AndroidOpenCV";
   // private Button mFaceRearCameraButton;
    private Button mFaceFrontCameraButton;
   // private Button mGrayCameraButton;
   // private Button mlmageButton;
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA",
    "android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 시스템이 Activity를 생성할 때 실행됨, 필수적으로 구현!
        // 전체 수명 주기 동안 한 번만 발생해야 하는 기본 APP 시작 로직을 수행
        // 일부 클래스 범위 변수를 인스턴스화 할 수 있다.
        // Activity의 이전 저장 상태가 포함된 Bundle 객체인 saveInstanceState 수신
        // 처음 생성된 Activity라면 Bundle의 값은 null.
        super.onCreate(savedInstanceState);

        // Activity와 뷰모델인 activity_main과 연결
       setContentView(R.layout.activity_main);

       // 단말기 버전이 Marshmallow보다 높으면 실행되는 if문.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // DENIED일 때
            if(!hasPermissions(PERMISSIONS)) {
                // PERMISSION 요청을 한다.
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
        mFaceFrontCameraButton = findViewById(R.id.front_camera);
       // mFaceRearCameraButton = findViewById(R.id.rear_camera);
       // mGrayCameraButton = findViewById(R.id.gray_camera);
       // mlmageButton = findViewById(R.id.edge_detection_btn);

      /*  mFaceRearCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View V)
            {
                // Intent : 앱 컴포넌트 간에 메시지를 주고 받고, 데이터를 주고 받기 위한 객체
                // 키, 값으로 값을 CameraActivity에서 사용할 수 있다.
                // Manifest.xml의 <application> 안에 있는 Activity에서 적용됨.
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);

                // mode라는 키로 0이라는 값을 사용할 수 있게 함.
                intent.putExtra("mode", 0);

                // Intent에 의해 지정된 DisplayMessageActivity의 인스턴스를 시작.
                startActivity(intent);
            }
        });*/

        mFaceFrontCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // Intent : 앱 컴포넌트 간에 메시지를 주고 받고, 데이터를 주고 받기 위한 객체
                // 키, 값으로 값을 CameraActivity에서 사용할 수 있다.
                // Manifest.xml의 <application> 안에 있는 Activity에서 적용됨.
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);

                // mode라는 키로 1이라는 값을 사용할 수 있게 함.
                intent.putExtra("mode", 1);

                // Intent에 의해 지정된 DisplayMessageActivity의 인스턴스를 시작.
                startActivity(intent);
            }
        });

       /* mGrayCameraButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                // Intent : 앱 컴포넌트 간에 메시지를 주고 받고, 데이터를 주고 받기 위한 객체
                // 키, 값으로 값을 CameraActivity에서 사용할 수 있다.
                // Manifest.xml의 <application> 안에 있는 Activity에서 적용됨.
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);

                // mode라는 키로 2라는 값을 사용할 수 있게 함.
                intent.putExtra("mode", 2);

                // Intent에 의해 지정된 DisplayMessageActivity의 인스턴스를 시작.
                startActivity(intent);
            }
        });*/

      /*  mlmageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                // Intent : 앱 컴포넌트 간에 메시지를 주고 받고, 데이터를 주고 받기 위한 객체
                // 키, 값으로 값을 CameraActivity에서 사용할 수 있다.
                // Manifest.xml의 <application> 안에 있는 Activity에서 적용됨.
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);

                // Intent에 의해 지정된 DisplayMessageActivity의 인스턴스를 시작.
                startActivity(intent);
            }
        });*/

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    // Permission을 자가체크하여 권한에 따라 bool 값을 리턴하는 Method
    private boolean hasPermissions(String[] permissions)
    {
        int result;
        for(String perms : permissions)
        {
            // 권한 보유 여부를 Android 스스로 확인
            // 권한이 있는 경우에는 PackageManager.PERMISSION_GRANTED
            // 권한이 없는 경우에는 PackageManager.PERMISIION_DENIED
            result = ContextCompat.checkSelfPermission(this, perms);
            if(result == PackageManager.PERMISSION_DENIED) // 권한이 없는 경우
            {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M) // API 버전이 Marshmallow일 때 실행되는 Method
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

}
