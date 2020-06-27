package com.project.opencvproject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

import static org.opencv.imgproc.Imgproc.rectangle;

public class CameraActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "AndroidOpenCv";
    private JavaCameraView mCameraView;
    private Mat mInputMat;
    private Mat mResultMat;
    private int mMode;

    public native void ConvertRGBtoGray(long matAddrInput, long matAddrResult);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 초기화같은 의미인가?
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mMode = intent.getIntExtra("mode", 0);

        // 화면을 FULLSCREEN으로 설정한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 화면이 켜진 상태를 유지한다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(!hasPermissions(PERMISSIONS)) {
                // PERMISSION 요청을 한다.
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // JavaCameraView
        mCameraView = findViewById(R.id.activity_Texture_view);
        mCameraView.setVisibility(TextureView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);

        // mGrayCameraButton이 눌렸을 때, setCameraIndex(카메라 전면, 후면 설정) 동작
        if(mMode == 2)
        {
            mCameraView.setCameraIndex(0); // 단말기 카메라를 후면으로 설정
        }
        else // 그 외 버튼이 눌렸을 땐 mMode를 따라가도록 함.
        {
            mCameraView.setCameraIndex(mMode); // 후면/rear = 0, 전면/front = 1
        }
    }

    // 다른 Activity가 활성화 되었을 때 호출되는 onPause()
    @Override
    protected void onPause() {
        super.onPause();
        if(mCameraView != null)
            mCameraView.disableView();
    }

    // 다른 Activity가 활성화 되었다가 다시 기존 Activity를 호출했을 때 실행되는 onResume()
    @Override
    protected void onResume() {
        super.onResume();
        // OpenCV 환경 구성이 되었는지 확인하는 if문
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume : OpenCV initialization error");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume : OpenCV initialization success");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    // 카메라 시작되어졌을 때 호출되는 메소드
   @Override
    public void onCameraViewStarted(int width, int height) {

    }

    // 카메라 미리보기가 어떤 이유로 인해 중단되었을 때 호출된다.
    @Override
    public void onCameraViewStopped() {

    }

    // 카메라 프레임 전달이 필요할 때 호출되는 메소드
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mInputMat = inputFrame.rgba();
//        if (mMode == 2) {
//            if (mResultMat == null) {
//                mResultMat = new Mat(mInputMat.rows(), mInputMat.cols(), mInputMat.type());
//            }
//            ConvertRGBtoGray(mInputMat.getNativeObjAddr(), mResultMat.getNativeObjAddr());
//            return mResultMat;
//        } else {
            Core.flip(mInputMat, mInputMat, 1);
            detectFace();

            return mInputMat;

    }

    private void detectFace() {
        // 얼굴 검출과 눈 검출을 위한 객체 정의
        CascadeClassifier face_cascade = new CascadeClassifier();
        CascadeClassifier eye_cascade = new CascadeClassifier();

        if (face_cascade.empty()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();

            // 객체에 얼굴, 눈 인식을 위한 xml 파일 Load.
            face_cascade.load(path + "/haarcascade_frontalface_default.xml");
            eye_cascade.load(path + "/haarcascade_eye.xml");
        }

        if (face_cascade.empty()) {
            return;
        }

        Mat gray = new Mat();
        Mat resizingGray = new Mat();
        Imgproc.cvtColor(mInputMat, gray, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.resize(gray, resizingGray, new Size(640, 360));

        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();
        face_cascade.detectMultiScale(resizingGray, faces, 1.3, 3, 0, new Size(40, 40));
        eye_cascade.detectMultiScale(resizingGray, eyes);
        for (int i = 0; i < faces.total(); i++) {
            Rect rc = faces.toList().get(i);
            rc.x *= 3;
            rc.y *= 3;
            rc.width *= 3;
            rc.height *= 3;

            // (그림을 그리고자 하는 원본 이미지, 사각형, 사각형의 색, 선의 두께)
            rectangle(mInputMat, rc, new Scalar(0, 255, 0), 2);
        }
        for(int i=0; i< eyes.total(); i++)
        {
            Rect rc = eyes.toList().get(i);
            rc.x *= 3;
            rc.y *= 3;
            rc.width *= 3;
            rc.height *= 3;
            rectangle(mInputMat, rc, new Scalar(255, 0, 0), 2);
        }
    }

    // permission
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};

    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
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