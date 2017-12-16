package pictureremind.rty813.xyz.TimeCamera.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.util.GetFilesUtils;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

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
    private FloatingActionButton fab_takepic;
    private MyAdapter adapter;


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
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        fragmentManager = getChildFragmentManager();
        list = new ArrayList<>();

        List<Map<String, Object>> imageslist = GetFilesUtils.getInstance().getSonNode(path);
        for (Map<String, Object> image : imageslist) {
            list.add(0, image.get(GetFilesUtils.FILE_INFO_PATH).toString());
        }

        final TextView tv_imageNumHint = view.findViewById(R.id.tv_imageNumHint);
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
                int visible = position == 0 ? View.GONE : View.VISIBLE;
                fab_takepic.setVisibility(visible);
                tv_imageNumHint.setVisibility(visible);
                tv_imageNumHint.setText(String.format(Locale.getDefault(), "%d/%d", position, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        fab_takepic = view.findViewById(R.id.fab_takepic);
        fab_takepic.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainFragment.themeColor != null) {
            new AutoBackground(getActivity(), toolbar).setColor(MainFragment.themeColor).start();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_takepic:
                viewpager.setCurrentItem(0);
                break;
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
                        .setMessage("真的要删除本相册吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                System.out.println("asdfasdf");
                            }
                        })
                        .setPositiveButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onMenuItemClick(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_delete:
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("警告")
//                        .setMessage("真的要删除本相册吗？")
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                            }
//                        })
//                        .setPositiveButton("取消", null)
//                        .create()
//                        .show();
//        }
//        return true;
//    }

    private class MyAdapter extends FragmentStatePagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                CameraFragment cameraFragment = CameraFragment.newInstance(list.get(0));
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

                        list.add(0, dir);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                viewpager.setCurrentItem(1);
                            }
                        });
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

}
