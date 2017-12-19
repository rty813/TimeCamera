package pictureremind.rty813.xyz.TimeCamera.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.coolerfall.daemon.Daemon;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.xiaomi.mistatistic.sdk.URLStatsRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pictureremind.rty813.xyz.TimeCamera.NotifyService;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.CameraFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment;
import pictureremind.rty813.xyz.TimeCamera.fragment.NewAlbumFragment;
import pictureremind.rty813.xyz.TimeCamera.util.SQLiteDBHelper;

import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.CHANGE;
import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.CHANGEALL;
import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.DELETE;

public class MainActivity extends AppCompatActivity implements MainFragment.onBtnClickListener,
        NewAlbumFragment.onBtnClickListener, MainFragment.onItemClickListener, CameraFragment.onCaptureSucceed{

    MainFragment mainFragment;
    CameraFragment cameraFragment;
    NewAlbumFragment newAlbumFragment;
    BrowseFragment browseFragment;
    public static int width;
    public static int height;
    private OrientationEventListener orientationEventListener;
    private int rotate;
    //    private MyHandler mHandler = null;
    private static final String MY_APPID = "2882303761517679467";
    private static final String MY_APP_KEY = "5611767931467";
    private static final String CHANNEL = "SELF";

    private SQLiteDBHelper dbHelper;
    public static String ROOTPATH;
    private String createtime = null;
    public static float alpha = 0.3f;
    private ImageRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MiStatInterface.initialize(this, MY_APPID, MY_APP_KEY, CHANNEL);
        MiStatInterface.setUploadPolicy(MiStatInterface.UPLOAD_POLICY_REALTIME, 0);
        MiStatInterface.enableExceptionCatcher(true);
        URLStatsRecorder.enableAutoRecord();
        dbHelper = new SQLiteDBHelper(this);
        ROOTPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/TimeCamera/";
        Fresco.initialize(this);
        WindowManager windowManager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        SharedPreferences sharedPreferences = getSharedPreferences("TimeCamera", MODE_PRIVATE);
        alpha = sharedPreferences.getFloat("alpha", 0.3f);

        checkPermission();
        System.out.println(width + " " + height);
        if (null == savedInstanceState) {
            if (null == mainFragment) {
                Log.e("Err", "Null == mainFragment");
                mainFragment = new MainFragment();
                mainFragment.setItemClickListener(this);
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
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,
                new Intent(this, NotifyService.class), 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (null != manager){
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
        }
    }

    private void checkPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.areAllPermissionsGranted()){
                    Toast.makeText(MainActivity.this, "您必须同意这些权限！", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    public int getRotate() {
        return rotate;
    }

    public SQLiteDBHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

                cameraFragment = CameraFragment.newInstance(null);
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
                if ((createtime != null && !createtime.equals(MainFragment.DEFAULT_TIME))
                        || (createtime == null)){
                    mainFragment.notifyInsert();
                }
                else if (createtime != null){
                    mainFragment.notifyChange(CHANGEALL);
                }
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
        final String filepath = mainFragment.getList().get(position).get("dirpath");
        final String albumname = mainFragment.getList().get(position).get("name");
        createtime = mainFragment.getList().get(position).get("createTime");
        if (createtime.equals(MainFragment.DEFAULT_TIME)){
            new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("尚未设置该相册信息！\n请选择操作")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            newAlbumFragment = NewAlbumFragment.newInstance(albumname, null);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                                    .hide(mainFragment)
                                    .add(R.id.container, newAlbumFragment)
                                    .addToBackStack("newAlbumFragment")
                                    .commit();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setNeutralButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BrowseFragment.deleteDirectory(filepath);
                            mainFragment.notifyChange(DELETE);
                        }
                    })
                    .show();
        }
        else{
            browseFragment = BrowseFragment.newInstance(filepath, albumname);
            browseFragment.setmOnChangedListener(type -> mainFragment.notifyChange(type));
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fm_camera_enter, R.anim.fm_camera_exit, R.anim.fm_pop_enter, R.anim.fm_pop_exit)
                    .hide(mainFragment)
                    .add(R.id.container, browseFragment)
                    .addToBackStack("browseFragment")
                    .commit();
        }
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
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            boolean needRotate = orientation == ExifInterface.ORIENTATION_UNDEFINED? (prop < 1):
                    orientation == ExifInterface.ORIENTATION_ROTATE_180;
            float ratio = prop < 1? 1 / prop : prop;
            int height = (int) (needRotate? width / ratio: width * ratio);
//            调整容器高度
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newAlbumFragment.insert_container.getLayoutParams();
            params.height = height;
            newAlbumFragment.insert_container.setLayoutParams(params);

            Uri uri = Uri.fromFile(new File(filepath));
            Fresco.getImagePipeline().evictFromMemoryCache(uri);
            Fresco.getImagePipelineFactory().getMainBufferedDiskCache().remove(new SimpleCacheKey(uri.toString()));
            Fresco.getImagePipelineFactory().getSmallImageFileCache().remove(new SimpleCacheKey(uri.toString()));
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width * 2, (int) (width * prop)))
                    .build();

            newAlbumFragment.iv_preview.post(new Runnable() {
                @Override
                public void run() {
                    long hashcode = newAlbumFragment.iv_preview.getController() == null ?
                            0: newAlbumFragment.iv_preview.getController().hashCode();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(newAlbumFragment.iv_preview.getController())
                            .build();
                    System.out.println(controller.hashCode() == hashcode);
                    newAlbumFragment.iv_preview.setController(controller);
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

    public BrowseFragment getBrowseFragment() {
        return browseFragment;
    }
}
