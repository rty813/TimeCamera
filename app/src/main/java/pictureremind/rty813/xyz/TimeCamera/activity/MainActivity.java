package pictureremind.rty813.xyz.TimeCamera.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;

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
    private OrientationEventListener orientationEventListener;
    private int rotate;
    private MyHandler mHandler = null;
    public int tb_color = -1;
    public int tb_title = -1;
    public int tb_sub = -1;

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

//        获取背景图片
        mHandler = new MyHandler(this, mainFragment, newAlbumFragment);
        SharedPreferences sharedPreferences = getSharedPreferences("background", Context.MODE_PRIVATE);
        String date = sharedPreferences.getString("date", null);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String now = format.format(new Date(System.currentTimeMillis()));
        System.out.println(now + "//////////////" + date);
        if (date == null || !date.equals(now)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        URL url = new URL("http://imgapi.wallpaperscraft.com/preview/118/118548/118548_360x640.jpg");
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setRequestMethod("GET");
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
                        if(conn.getResponseCode() == 200) {
                            InputStream inputStream = conn.getInputStream();
                            bitmap = BitmapFactory.decodeStream(inputStream);
                        }
                        File file = new File(getExternalFilesDir(null), "background.jpg");
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Message msg = mHandler.obtainMessage();
                        msg.what = 2;
                        msg.obj = bitmap;
                        mHandler.sendMessage(msg);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else {
            mHandler.sendEmptyMessage(1);
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
        private final WeakReference<NewAlbumFragment> newAlbumFragment;
        private final WeakReference<MainFragment> mainFragment;
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity, MainFragment mainFragment, NewAlbumFragment newAlbumFragment){
            this.mainFragment = new WeakReference<>(mainFragment);
            this.newAlbumFragment = new WeakReference<>(newAlbumFragment);
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity activity = mActivity.get();
            final NewAlbumFragment albumFragment = newAlbumFragment.get();
            final MainFragment mFragment = mainFragment.get();
            Bitmap bitmap = null;
            switch (msg.what){
                case 1:
                    bitmap = BitmapFactory.decodeFile(activity.getExternalFilesDir(null) + "/background.jpg");
                    break;
                case 2:
                    bitmap = (Bitmap) msg.obj;
                    SharedPreferences sharedPreferences = activity.getSharedPreferences("background", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String date = format.format(new Date(System.currentTimeMillis()));
                    editor.putString("date",date);
                    editor.apply();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
            activity.getWindow().setBackgroundDrawable(new BitmapDrawable(bitmap));
            Palette.from(bitmap)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@NonNull Palette palette) {
                            if (palette.getVibrantSwatch() != null && palette.getLightVibrantSwatch() != null){
                                activity.getWindow().setStatusBarColor(palette.getVibrantSwatch().getRgb());
                                Palette.Swatch swatch = palette.getLightVibrantSwatch();
                                activity.tb_color = swatch.getRgb();
                                activity.tb_sub = swatch.getBodyTextColor();
                                activity.tb_title = swatch.getTitleTextColor();
                                if (mFragment != null){
                                    mFragment.setToolbarColor();
                                }
                            }
                        }
                    });
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
            newAlbumFragment.isTookPic = true;
            newAlbumFragment.checkCommit();
        }
        else{
            getSupportFragmentManager().popBackStack();
        }

    }
}
