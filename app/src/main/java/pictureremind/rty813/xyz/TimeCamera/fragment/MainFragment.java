package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.Inflater;

import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;
import pictureremind.rty813.xyz.TimeCamera.util.GetFilesUtils;
import pictureremind.rty813.xyz.TimeCamera.util.RecyclerviewAdapter;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.util.SQLiteDBHelper;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

import static android.content.Context.MODE_PRIVATE;
import static pictureremind.rty813.xyz.TimeCamera.activity.MainActivity.ROOTPATH;
import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.CHANGE;
import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.CHANGEALL;
import static pictureremind.rty813.xyz.TimeCamera.fragment.BrowseFragment.DELETE;

/**
 * Created by zhang on 2017/12/5.
 */

public class MainFragment extends Fragment implements View.OnClickListener{
    private onBtnClickListener mListener;
    private RecyclerView recyclerView;
    private RecyclerviewAdapter adapter;
    private ArrayList<Map<String, String>> list;
    private onItemClickListener mItemClickListener;
    private Toolbar toolbar;
    public static int[] themeColor = null;
    private int pos = -1;
    private FloatingActionButton fab_insert;
    public static final String DEFAULT_TIME = "1900-01-01-01-01-01";
    private boolean hasLoaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab_insert = view.findViewById(R.id.btn_insert);
        fab_insert.setOnClickListener(this);
        toolbar = view.findViewById(R.id.toolbar);
        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerviewAdapter(getActivity(), list);
        adapter.setItemLongClickListener(new RecyclerviewAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                getActivity().invalidateOptionsMenu();
                pos = position;
                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                Menu menu = popupMenu.getMenu();
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.menu_popup, menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.menu_delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("警告")
                                        .setMessage("真的要删除本相册吗？\n数据删除不可恢复！")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                SQLiteDatabase database = ((MainActivity)getActivity()).getDbHelper().getWritableDatabase();
                                                database.delete(SQLiteDBHelper.TABLE_NAME, "NAME=?", new String[]{list.get(pos).get("name")});
                                                database.close();
                                                BrowseFragment.deleteDirectory(list.get(pos).get("dirpath"));
                                                notifyChange(DELETE);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .show();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        adapter.setItemClickListener(new RecyclerviewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pos = position;
                mItemClickListener.onItemClick(view, position);
            }
        });
        if(ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            hasLoaded = true;
            loadAlbum();
        }
        recyclerView.setAdapter(adapter);
    }

    private void loadAlbum(){
        File file = new File(ROOTPATH);
        if (!file.exists()){
            file.mkdirs();
        }
        List<Map<String, Object>> albumlist = GetFilesUtils.getInstance().getSonNode(ROOTPATH);
        ArrayList<String> nameList = new ArrayList<>();
        SQLiteDatabase database = ((MainActivity) getActivity()).getDbHelper().getReadableDatabase();
        Cursor cursor;
        for (Map<String, Object> album: albumlist){
            String albumname = (String) album.get(GetFilesUtils.FILE_INFO_NAME);
            List<Map<String, Object>> imageslist = GetFilesUtils.getInstance().getSonNode(ROOTPATH + albumname);
            if (imageslist.size() > 0){
                String path = Collections.max(imageslist, (map1, map2) -> map1.get(GetFilesUtils.FILE_INFO_PATH).toString().compareTo(
                        map2.get(GetFilesUtils.FILE_INFO_PATH).toString()
                )).get(GetFilesUtils.FILE_INFO_PATH).toString();

                nameList.add(albumname);
                Map<String, String> map = new HashMap<>();
                map.put("path", path);
                map.put("dirpath", ROOTPATH + albumname);
                map.put("name", albumname);
                cursor = database.query(SQLiteDBHelper.TABLE_NAME, null, "NAME=?", new String[]{albumname}, null, null, null);
                if (cursor.getCount() > 0){
                    cursor.moveToFirst();
                    String createTime = cursor.getString(cursor.getColumnIndex("CREATE_TIME"));
                    map.put("createTime", createTime);
                }
                else {
//                    数据库中无数据，而本地有相册，则提醒用户重新设置
                    map.put("createTime", DEFAULT_TIME);
                }
                if (!list.contains(map)){
                    list.add(new HashMap<>(map));
                }
            }
        }
//        数据库中有数据，而本地无相片（包括无文件夹，或有文件夹无文件），则删除数据库中的数据
        cursor = database.query(SQLiteDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                if (!nameList.contains(name)){
                    database.delete(SQLiteDBHelper.TABLE_NAME, "NAME=?", new String[]{name});
                }
            }while (cursor.moveToNext());
        }
        database.close();
        if (list.size() > 0){
            Collections.sort(list, new Comparator<Map<String, String>>() {
                @Override
                public int compare(Map<String, String> map1, Map<String, String> map2) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
                    try {
                        Date date1 = dateFormat.parse(map1.get("createTime"));
                        Date date2 = dateFormat.parse(map2.get("createTime"));
                        return date2.compareTo(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
        }
    }

    public void notifyInsert(){
        loadAlbum();
        adapter.notifyItemInserted(0);
        if (list.size() > 1){
            adapter.notifyItemRangeChanged(1, list.size() - 1);
        }
        recyclerView.smoothScrollToPosition(0);
    }

    public void notifyChange(int type){
        list.removeAll(list);
        loadAlbum();
        switch (type){
            case CHANGE:
                adapter.notifyItemChanged(pos);
                break;
            case DELETE:
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(pos, list.size());
                break;
            case CHANGEALL:
                adapter.notifyDataSetChanged();
                break;

        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_insert:
                this.mListener.onClick(view);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onBtnClickListener) {
            mListener = (onBtnClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface onItemClickListener {
        public void onItemClick(View viewItem, int position);
    }

    public void setItemClickListener(onItemClickListener listener){
        this.mItemClickListener = listener;
    }


    public interface onBtnClickListener{
        public void onClick(View v);
    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    @Override
    public void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(getActivity(), "MainFragment");
        final AutoBackground autoBackground = new AutoBackground(getActivity(), toolbar).setDefaultBgEnable(true);
        autoBackground.setOnChangeBackgroundFinishedListener(new AutoBackground.onChangeBackgroundFinishdListener() {
            @Override
            public void onFinished() {
                themeColor = autoBackground.getColor();
                fab_insert.setBackgroundTintList(ColorStateList.valueOf(themeColor[0]));
            }
        }).start();
        if(!hasLoaded && ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
            hasLoaded = true;
            loadAlbum();
            adapter.notifyDataSetChanged();
        }
    }

    public ArrayList<Map<String, String>> getList() {
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_mainfragment, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}
