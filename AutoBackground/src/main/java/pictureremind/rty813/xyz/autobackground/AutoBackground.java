package pictureremind.rty813.xyz.autobackground;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by doufu on 2017/12/11.
 */

public class AutoBackground {
    private Context mContext;
    private Toolbar mToolbar;
    private boolean mGradient = true;
    private boolean mDefaultBgEnable = false;
    private boolean mToobarEnable = true;
    private boolean mStatusBarEnable = true;
    private boolean mOnlyBar = false;
    private int mUpdateTime = EVERY_LAUNCH;
    private URL mUrl = null;
    private String mStrUrl = "http://139.199.37.92:9999";
    private Bitmap mDefaultBg = null;
    private onChangeBackgroundFinishdListener mChangeFinishedListener = null;
    private onBackgroundDownloadFinishedListener mDownloadFinishedListener = null;
    private int[] mBarColor;

    public static final int EVERY_DAY = 0;
    public static final int EVERY_LAUNCH = 1;


    public AutoBackground(Context context, @Nullable Toolbar toolbar){
        mContext = context;
        mToolbar = toolbar;
    }

    public void start() {
        if (mOnlyBar){
            if (mToolbar == null){
                throw new AutoBackgroundException("NullPointerException! You should set toolbar first!");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((Activity)mContext).getWindow().setStatusBarColor(mBarColor[0]);
            }
            mToolbar.setTitleTextColor(mBarColor[2]);
            mToolbar.setSubtitleTextColor(mBarColor[3]);
            if (mGradient && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                if (!mStatusBarEnable){
                    throw new AutoBackgroundException("StatusBarEnable must be true if setGradient");
                }
                GradientDrawable drawable =new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{mBarColor[0], mBarColor[1]});
                mToolbar.setBackground(drawable);
            }
            else{
                mToolbar.setBackgroundColor(mBarColor[1]);
            }
            return;
        }

        //        设置背景图片
        Bitmap bitmap = null;
        File file = new File(mContext.getExternalFilesDir(null), "background.jpg");
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(mContext.getExternalFilesDir(null) + "/background.jpg");
        } else if (mDefaultBgEnable) {
            InputStream resourceAsStream = mContext.getClass().getClassLoader().getResourceAsStream("assets/" + "default_bg.jpg");
            bitmap = BitmapFactory.decodeStream(resourceAsStream);
        } else {
            bitmap = mDefaultBg;
        }
        final Activity activity = (Activity) mContext;
        activity.getWindow().setBackgroundDrawable(new BitmapDrawable(bitmap));
        if (bitmap != null && (mStatusBarEnable || mToobarEnable)){
            Palette.from(bitmap)
                    .generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@NonNull Palette palette) {
                            int tb_color = -1;
                            int tb_sub = -1;
                            int tb_title = -1;
                            int sb_color;
                            Palette.Swatch swatch = null;
                            if (palette.getVibrantSwatch() != null) {
                                swatch = palette.getLightMutedSwatch() == null? palette.getVibrantSwatch() : palette.getLightMutedSwatch();
                                sb_color = palette.getVibrantSwatch().getRgb();
                            }
                            else if (palette.getMutedSwatch() != null){
                                swatch = palette.getLightMutedSwatch() == null? palette.getLightMutedSwatch(): palette.getMutedSwatch();
                                sb_color = palette.getMutedSwatch().getRgb();
                            }
                            else{
                                return;
                            }
                            if (mStatusBarEnable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                activity.getWindow().setStatusBarColor(sb_color);
                            }
                            if (mToobarEnable && swatch != null){
                                tb_color = swatch.getRgb();
                                tb_sub = swatch.getBodyTextColor();
                                tb_title = swatch.getTitleTextColor();
                                mToolbar.setTitleTextColor(tb_title);
                                mToolbar.setSubtitleTextColor(tb_sub);
                                if (mGradient && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                                    if (!mStatusBarEnable){
                                        throw new AutoBackgroundException("StatusBarEnable must be true if setGradient");
                                    }

                                    GradientDrawable drawable =new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{sb_color, tb_color});
                                    mToolbar.setBackground(drawable);
                                }
                                else{
                                    mToolbar.setBackgroundColor(tb_color);
                                }
                            }
                            mBarColor = new int[]{sb_color, tb_color, tb_title, tb_sub};
                            if (mChangeFinishedListener != null){
                                mChangeFinishedListener.onFinished();
                            }
                        }
                    });
        }

//        更新本地背景图片
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mUrl == null){
                        mUrl = new URL(mStrUrl);
                    }
                    final SharedPreferences sharedPreferences = mContext.getSharedPreferences("background", Context.MODE_PRIVATE);
                    String url_local = sharedPreferences.getString("url", null);
                    String url_remote;

                    HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                    conn.setConnectTimeout(5000);
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    url_remote = br.readLine();
                    conn.disconnect();
                    br.close();

                    if (url_local == null || (url_remote != null && !url_local.equals(url_remote))) {
                        conn = (HttpURLConnection) new URL(url_remote).openConnection();
                        conn.setConnectTimeout(5000);
                        InputStream inputStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        File file = new File(mContext.getExternalFilesDir(null), "background.jpg");
                        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("url", url_remote);
                        editor.apply();
                    }
                    if (mDownloadFinishedListener != null){
                        mDownloadFinishedListener.onFinished();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mDownloadFinishedListener != null){
                        mDownloadFinishedListener.onFailed(e);
                    }
                }
            }
        }).start();
    }

    public int[] getColor() {
        return mBarColor;
    }

    public AutoBackground setColor(int[] mColor) {
        this.mBarColor = mColor;
        this.mOnlyBar = true;
        return this;
    }

    public AutoBackground setGradient(boolean gradient) {
        this.mGradient = gradient;
        return this;
    }

    public AutoBackground setUpdateTime(int mUpdateTime) {
        if (mUpdateTime != EVERY_DAY && mUpdateTime != EVERY_LAUNCH){
            throw new AutoBackgroundException("UpdateTime Error");
        }
        this.mUpdateTime = mUpdateTime;
        return this;
    }

    public AutoBackground setUrl(URL mUrl) {
        this.mUrl = mUrl;
        return this;
    }

    public AutoBackground setUrl(String mStrUrl){
        this.mStrUrl = mStrUrl;
        if (this.mUrl != null){
            throw new AutoBackgroundException("Already Set the URL");
        }
        return this;
    }

    public AutoBackground setmDefaultBg(Bitmap mDefaultBg) {
        this.mDefaultBg = mDefaultBg;
        this.mDefaultBgEnable = false;
        return this;
    }

    public AutoBackground setDefaultBgEnable(boolean mDefault_bg_enable) {
        if (mDefaultBg == null){
            this.mDefaultBgEnable = mDefault_bg_enable;
        }
        return this;
    }

    public AutoBackground setToobarEnable(boolean mToobarEnable) {
        this.mToobarEnable = mToobarEnable;
        return this;
    }

    public AutoBackground setStatusBarEnable(boolean mStatusBarEnable) {
        this.mStatusBarEnable = mStatusBarEnable;
        return this;
    }

    public AutoBackground setOnChangeBackgroundFinishedListener(onChangeBackgroundFinishdListener onChangeBackgroundFinishdListener) {
        this.mChangeFinishedListener = onChangeBackgroundFinishdListener;
        return this;
    }

    public AutoBackground setOnDownloadFinishedListener(onBackgroundDownloadFinishedListener mDownloadFinishedListener) {
        this.mDownloadFinishedListener = mDownloadFinishedListener;
        return this;
    }

    public interface onChangeBackgroundFinishdListener {
        public void onFinished();
    }

    public interface onBackgroundDownloadFinishedListener{
        public void onFinished();
        public void onFailed(Exception e);
    }
}

