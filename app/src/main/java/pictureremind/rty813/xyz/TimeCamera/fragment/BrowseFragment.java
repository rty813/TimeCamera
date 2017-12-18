package pictureremind.rty813.xyz.TimeCamera.fragment;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;
import pictureremind.rty813.xyz.TimeCamera.util.GetFilesUtils;
import pictureremind.rty813.xyz.TimeCamera.util.SQLiteDBHelper;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

import static android.content.Context.MODE_PRIVATE;
import static pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment.themeColor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Toolbar toolbar;
    // TODO: Rename and change types of parameters
    private String path;
    private String name;
    private ViewPager viewpager;
    private FragmentManager fragmentManager;
    private ArrayList<String> list;
    private MyAdapter adapter;
    private onChanged mOnChangedListener;
    public static final int DELETE = 1;
    public static final int CHANGE = 0;
    public static final int CHANGEALL = 2;
    private FloatingActionButton fab_add;
    private FloatingActionButton fab_remove;
    private FloatingActionButton fab_replace;
    private FloatingActionMenu fab_menu;
    private boolean isCancel;
    private TextView tv_imageNumHint;
    private CameraFragment cameraFragment;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(ARG_PARAM1);
            name = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setSubtitle(name);
        fab_add = view.findViewById(R.id.fab_add);
        fab_remove = view.findViewById(R.id.fab_remove);
        fab_replace = view.findViewById(R.id.fab_replace);
        fab_menu = view.findViewById(R.id.fab_menu);
        fab_menu.setClosedOnTouchOutside(true);
        fab_menu.hideMenuButton(true);
        fab_menu.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_scale_up));
        fab_menu.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fab_scale_down));
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        fragmentManager = getChildFragmentManager();
        list = new ArrayList<>();

        List<Map<String, Object>> imageslist = GetFilesUtils.getInstance().getSonNode(path);
        for (Map<String, Object> image : imageslist) {
            list.add(0, image.get(GetFilesUtils.FILE_INFO_PATH).toString());
        }

        tv_imageNumHint = view.findViewById(R.id.tv_imageNumHint);
        tv_imageNumHint.setText(String.format(Locale.getDefault(), "%d/%d", 1, list.size()));
        viewpager = view.findViewById(R.id.viewpager);
        adapter = new MyAdapter(fragmentManager);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(1);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    fab_menu.hideMenuButton(true);
                    tv_imageNumHint.setVisibility(View.GONE);
                }
                else{
                    fab_menu.showMenuButton(true);
                    tv_imageNumHint.setVisibility(View.VISIBLE);
                }
                tv_imageNumHint.setText(String.format(Locale.getDefault(), "%d/%d", position, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_menu.showMenuButton(true);
            }
        }, 200);
        fab_replace.setOnClickListener(this);
        fab_remove.setOnClickListener(this);
        fab_add.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (themeColor != null) {
            new AutoBackground(getActivity(), toolbar).setColor(themeColor).start();
            fab_menu.setMenuButtonColorNormal(themeColor[0]);
            fab_menu.setMenuButtonColorPressed(themeColor[1]);
            fab_add.setColorNormal(themeColor[0]);
            fab_add.setColorPressed(themeColor[1]);
            fab_remove.setColorNormal(themeColor[0]);
            fab_remove.setColorPressed(themeColor[1]);
            fab_replace.setColorNormal(themeColor[0]);
            fab_replace.setColorPressed(themeColor[1]);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add:
                viewpager.setCurrentItem(0);
                break;
            case R.id.fab_replace:
                break;
            case R.id.fab_remove:
                if (list.size() == 1){
                    Snackbar.make(fab_menu, "无法删除最后一张照片", Snackbar.LENGTH_SHORT).show();
                    fab_menu.close(true);
                    return;
                }
                final int pos = viewpager.getCurrentItem() - 1;
                final String filepath = list.get(pos);
                list.remove(pos);
                adapter.notifyDataSetChanged();
                tv_imageNumHint.setText(String.format(Locale.getDefault(), "%d/%d", pos + 1, list.size()));

                isCancel = false;
                Snackbar.make(fab_menu, "删除记录", Snackbar.LENGTH_SHORT)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                isCancel = true;
                                list.add(pos, filepath);
                                adapter.notifyDataSetChanged();
                                tv_imageNumHint.setText(String.format(Locale.getDefault(), "%d/%d", pos + 1, list.size()));
                            }
                        })
                        .addCallback(new MyCallBack(filepath))
                        .show();
                break;
        }
        fab_menu.close(true);
    }

    private class MyCallBack extends Snackbar.Callback{
        private String filepath;
        public MyCallBack(String filepath){
            super();
            this.filepath = filepath;
        }
        @Override
        public void onDismissed(Snackbar transientBottomBar, int event) {
            if (!isCancel) {
                deleteFile(filepath);
                mOnChangedListener.onChanged(CHANGE);
                super.onDismissed(transientBottomBar, event);
            }
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_browsefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("警告")
                        .setMessage("真的要删除本相册吗？\n数据删除不可恢复！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SQLiteDatabase database = ((MainActivity)getActivity()).getDbHelper().getWritableDatabase();
                                database.delete(SQLiteDBHelper.TABLE_NAME, "NAME=?", new String[]{name});
                                database.close();
                                deleteDirectory(path);
                                mOnChangedListener.onChanged(DELETE);
                                getActivity().onBackPressed();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.menu_alpha:
                View popupView = View.inflate(getActivity(), R.layout.popupwindow_alpha, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, false);
                SeekBar seekBar = popupView.findViewById(R.id.seekbar);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setAnimationStyle(R.style.dismiss_anim);
                popupWindow.update();
                seekBar.setProgress((int) (MainActivity.alpha * 100));
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        cameraFragment.setAlpha((float)i / 100);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        MainActivity.alpha = (float)seekBar.getProgress() / 100;
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TimeCamera", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("alpha", MainActivity.alpha);
                        editor.apply();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(viewpager, Gravity.CENTER, 0, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                String str = list.size() == 0? null : list.get(0);
                cameraFragment = CameraFragment.newInstance(str);
                cameraFragment.setmSucceed(new CameraFragment.onCaptureSucceed() {
                    @Override
                    public void onSucceed(String filepath) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
                        String date = dateFormat.format(System.currentTimeMillis());
                        String dir = path + "/" + date + ".jpg";
                        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            Snackbar.make(viewpager, "保存失败！", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int bytesum = 0;
                            int byteread = 0;
                            File oldfile = new File(filepath);
                            if (oldfile.exists()){
                                InputStream inputStream = new FileInputStream(oldfile);
                                new File(path).mkdirs();
                                new File(dir).createNewFile();
                                FileOutputStream outputStream = new FileOutputStream(dir);
                                byte[] buffer = new byte[1444];
                                while((byteread = inputStream.read(buffer)) != -1){
                                    bytesum += byteread;
                                    System.out.println(bytesum);
                                    outputStream.write(buffer, 0, byteread);
                                }
                                inputStream.close();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            Snackbar.make(viewpager, "复制文件出错！", Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        SQLiteDatabase database = ((MainActivity)getActivity()).getDbHelper().getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("LAST_TIME", date);
                        database.update(SQLiteDBHelper.TABLE_NAME, values, "NAME=?", new String[]{name});
                        database.close();
                        list.add(0, dir);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                viewpager.setCurrentItem(1);
                            }
                        });
                        if (mOnChangedListener != null){
                            mOnChangedListener.onChanged(CHANGE);
                        }
                    }
                });
                return cameraFragment;
            } else {
                return ImageFragment.newInstance(list.get(position - 1));
            }
        }

        @Override
        public int getCount() {
            return list.size() + 1;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }


    public interface onChanged{
        public void onChanged(int type);
    }

    public void setmOnChangedListener(onChanged mOnChangedListener) {
        this.mOnChangedListener = mOnChangedListener;
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }
}
