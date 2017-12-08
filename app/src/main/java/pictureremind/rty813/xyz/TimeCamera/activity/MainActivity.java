package pictureremind.rty813.xyz.TimeCamera.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

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

        if (null == mainFragment){
            mainFragment = new MainFragment();
            mainFragment.setSwipeItemClickListener(this);
        }

        if (null == savedInstanceState){
            getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).replace(R.id.container, mainFragment).commit();
        }
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
                mHandler = new MyHandler(newAlbumFragment);
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

    private static class MyHandler extends Handler{
        private final WeakReference<NewAlbumFragment> mFragment;

        public MyHandler(NewAlbumFragment fragment){
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    File file = (File) msg.obj;
                    NewAlbumFragment fragment = mFragment.get();
                    fragment.setAlbumPic(file);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }


    @Override
    public void onSucceed(File file) {
        getSupportFragmentManager().popBackStack();
        final Message msg = mHandler.obtainMessage();
        msg.what = 1;
        msg.obj = file;
//        System.out.println(file.hashCode());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if (!(getSupportFragmentManager().getBackStackEntryCount() == 0)
                            && getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("newAlbumFragment")) {
//                        newAlbumFragment.setAlbumPic(file);
                        mHandler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
