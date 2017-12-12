package pictureremind.rty813.xyz.TimeCamera.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
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
import android.widget.Toolbar;

import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.CameraFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.NewAlbumFragment;

public class MainActivity extends AppCompatActivity implements CameraFragment.OnBtnClickListener,
        MainFragment.onBtnClickListener, NewAlbumFragment.onBtnClickListener,
        MainFragment.onSwipeItemClickListener, CameraFragment.onCaptureSucceed{

    MainFragment mainFragment;
    CameraFragment cameraFragment;
    NewAlbumFragment newAlbumFragment;
    BrowseFragment browseFragment;
    public static int width;
    public static int height;
    private OrientationEventListener orientationEventListener;
    private int rotate;
    //    private MyHandler mHandler = null;
    public int tb_color = -1;
    public int tb_title = -1;
    public int tb_sub = -1;
    private static final String MY_APPID = "2882303761517679467";
    private static final String MY_APP_KEY = "5611767931467";
    private static final String CHANNEL = "SELF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MiStatInterface.initialize(this, MY_APPID, MY_APP_KEY, CHANNEL);
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
        MiStatInterface.enableExceptionCatcher(true);
        URLStatsRecorder.enableAutoRecord();

        WindowManager windowManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;

        System.out.println(width + " " + height);
        if (null == savedInstanceState) {
            if (null == mainFragment) {
                Log.e("Err", "Null == mainFragment");
                mainFragment = new MainFragment();
                mainFragment.setSwipeItemClickListener(this);
            }
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).
                    replace(R.id.container, mainFragment, "mainFragment").commit();
        } else {
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("mainFragment");
        }
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int i) {
                rotate = ((i + 45) / 90 * 90) % 360;
            }
        };
        if (orientationEventListener.canDetectOrientation()) {
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
            case R.id.btn_commit:
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().popBackStack();
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
        browseFragment = BrowseFragment.newInstance(null, null);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                .hide(mainFragment)
                .add(R.id.container, browseFragment)
                .addToBackStack("browseFragment")
                .commit();
    }

    @Override
    protected void onDestroy() {
        orientationEventListener.disable();
        super.onDestroy();
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
            newAlbumFragment.filepath = filepath;
            newAlbumFragment.checkCommit();
        }
        else{
            getSupportFragmentManager().popBackStack();
        }
    }
}
