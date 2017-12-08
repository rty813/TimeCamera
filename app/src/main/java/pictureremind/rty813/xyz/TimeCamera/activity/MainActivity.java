package pictureremind.rty813.xyz.TimeCamera.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;

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
    public void onSucceed(final File file) {
        getSupportFragmentManager().popBackStack();
//        System.out.println(getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!(getSupportFragmentManager().getBackStackEntryCount() == 0)
                        && getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName().equals("newAlbumFragment")){
                    newAlbumFragment.setAlbumPic(file);
                    System.out.println("succeed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
            }
        }, 100);


    }
}
