package pictureremind.rty813.xyz.TimeCamera.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.Toast;

import java.util.Arrays;

import pictureremind.rty813.xyz.TimeCamera.R;

public class TestActivity extends AppCompatActivity {
    private TextureView textureView;
    private String TAG = "测试";

    private HandlerThread mThreadHandler;
    private Handler mHandler;

    private CaptureRequest.Builder mPreviewBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mThreadHandler = new HandlerThread("CAMERA2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());

        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(textureListener);
        findViewById(R.id.imageView).setAlpha(0.5f);

    }

    private TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG, "可用");

            //CameraManaer 摄像头管理器，用于检测摄像头，打开系统摄像头
            CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
            try {
                String[] CameraIdList = cameraManager.getCameraIdList();//获取可用相机列表
                Log.e(TAG, "可用相机的个数是:" + CameraIdList.length);
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(CameraIdList[0]);//获取某个相机(摄像头特性)
                cameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);//检查支持

                if (ActivityCompat.checkSelfPermission(TestActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(TestActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(TestActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    return;
                }
                cameraManager.openCamera(CameraIdList[0], mCameraDeviceStateCallback, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
            Log.e(TAG,"改变");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            Log.e(TAG,"释放");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            Log.e(TAG,"更新");
        }
    };

    //CameraDeviceandroid.hardware.Camera也就是Camera1的Camera
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            try {
                startPreview(camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };


    /**
     * @param camera
     * @throws CameraAccessException
     * 开始预览
     */
    private void startPreview(CameraDevice camera) throws CameraAccessException {
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(textureView.getWidth(), textureView.getHeight());
        Surface surface = new Surface(texture);
        try {
            //CameraRequest表示一次捕获请求，用来对z照片的各种参数设置，比如对焦模式、曝光模式等。CameraRequest.Builder用来生成CameraRequest对象
            mPreviewBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);
        camera.createCaptureSession(Arrays.asList(surface), mSessionStateCallback, mHandler);
    }

    //CameraCaptureSession 这个对象控制摄像头的预览或者拍照
    //setRepeatingRequest()开启预览，capture()拍照
    //StateCallback监听CameraCaptureSession的创建
    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            Log.e(TAG,"相机创建成功！");
            try {
                session.capture(mPreviewBuilder.build(), mSessionCaptureCallback, mHandler);//拍照
                session.setRepeatingRequest(mPreviewBuilder.build(), mSessionCaptureCallback, mHandler);//返回结果
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e(TAG,"这里异常");
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.e(TAG,"相机创建失败！");
        }
    };


    //CameraCaptureSession.CaptureCallback监听拍照过程
    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Log.e(TAG,"这里接受到数据"+result.toString());
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult){

        }};

}
