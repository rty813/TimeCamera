package pictureremind.rty813.xyz.TimeCamera.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.fragment.CameraFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.NewAlbumFragment;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnBtnClickListener,
        MainFragment.onBtnClickListener, NewAlbumFragment.onBtnClickListener,
        MainFragment.onSwipeItemClickListener, CameraFragment.onCaptureSucceed{

    MainFragment mainFragment;
    CameraFragment cameraFragment;
    NewAlbumFragment newAlbumFragment;
    public static int width;
    public static int height;
    private MyHandler mHandler = null;
    private OrientationEventListener orientationEventListener;
    private int rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowManager windowManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        System.out.println(width + " " + height);
        if (null == savedInstanceState){
            if (null == mainFragment){
                Log.e("Err","Null == mainFragment");
                mainFragment = new MainFragment();
                mainFragment.setSwipeItemClickListener(this);
            }
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    replace(R.id.container, mainFragment, "mainFragment").commit();
        }
        else{
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragment");
        }
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                rotate = ((i+45) / 90 * 90) % 360;
            }
        };
        if (orientationEventListener.canDetectOrientation()){
            orientationEventListener.enable();
        }
    }

    public int getRotate() {
        return rotate;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                cameraFragment = new CameraFragment();
                cameraFragment.setOnClickListener(this);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                        .hide(mainFragment)
                        .add(R.id.container, cameraFragment)
//                        .replace(R.id.container, cameraFragment)
                        .addToBackStack("cameraFragment")
                        .commit();
                break;
            case R.id.btn_return:
//                dismissCameraFragment();
                getSupportFragmentManager().popBackStack();
                break;
            case R.id.btn_insert:
                newAlbumFragment = NewAlbumFragment.newInstance(null, null);
                mHandler = new MyHandler(this, newAlbumFragment);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                        .hide(mainFragment)
                        .add(R.id.container, newAlbumFragment)
                        .addToBackStack("newAlbumFragment")
                        .commit();
                break;
            case R.id.insert_container:
//                Toast.makeText(this, "HH", Toast.LENGTH_SHORT).show();

                cameraFragment = new CameraFragment();
                cameraFragment.setOnClickListener(this);
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                        .hide(newAlbumFragment)
                        .add(R.id.container, cameraFragment)
//                        .replace(R.id.container, cameraFragment)
                        .addToBackStack("cameraFragment")
                        .commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(View viewItem, int position) {
        cameraFragment = new CameraFragment();
        cameraFragment.setOnClickListener(MainActivity.this);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                .hide(mainFragment)
                .add(R.id.container, cameraFragment)
                .addToBackStack("cameraFragment")
                .commit();
    }

    @Override
    protected void onDestroy() {
        orientationEventListener.disable();
        super.onDestroy();
    }

    private static class MyHandler extends Handler{
        private final WeakReference<NewAlbumFragment> mFragment;
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity, NewAlbumFragment fragment){
            mFragment = new WeakReference<>(fragment);
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String filepath = (String) msg.obj;
                    NewAlbumFragment fragment = mFragment.get();
                    MainActivity activity = mActivity.get();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filepath, options);
                    int width = fragment.insert_container.getWidth();
                    int height = fragment.insert_container.getHeight();
                    float prop = (float)options.outHeight / options.outWidth;
                    height = (int)(width * prop) > height ? (int)(width * prop) : height;
                    width  = (int)(height / prop) > width ? (int)(height / prop) : width;
                    int outWidth = options.outWidth;
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = (int)((float)outWidth / width);
                    Log.e("inSampleSize", String.valueOf(options.inSampleSize));
                    Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
                    fragment.iv_preview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    fragment.iv_preview.setImageBitmap(bitmap);

//                    Picasso.with(activity).load(new File(filepath)).resize(width, height)
//                            .centerInside().placeholder(R.drawable.add).into(fragment.iv_preview);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    @Override
    public void onSucceed(String filepath) {
        if ((getSupportFragmentManager().getBackStackEntryCount() > 1)
                && getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName().equals("newAlbumFragment")) {
            getSupportFragmentManager().popBackStack();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filepath, options);
            int width = newAlbumFragment.insert_container.getWidth();
            float prop = (float)options.outHeight / options.outWidth;
            height = (int)(width * prop);

//            调整容器高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newAlbumFragment.insert_container.getLayoutParams();
            params.height = height;
            newAlbumFragment.insert_container.setLayoutParams(params);

//            压缩图片
            int outWidth = options.outWidth;
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int)((float)outWidth / width);
            final Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
            newAlbumFragment.iv_preview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    newAlbumFragment.iv_preview.setImageBitmap(bitmap);
                }
            });
        }
        else{
            getSupportFragmentManager().popBackStack();
        }
//        if (mHandler == null){
//            return;
//        }
//        final Message msg = mHandler.obtainMessage();
//        msg.what = 1;
//        msg.obj = filepath;
////        System.out.println(file.hashCode());
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(100);
//                    if (!(getSupportFragmentManager().getBackStackEntryCount() == 0)
//                            && getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("newAlbumFragment")) {
////                        newAlbumFragment.setAlbumPic(file);
//                        mHandler.sendMessage(msg);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
