package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.util.GetFilesUtils;
import pictureremind.rty813.xyz.TimeCamera.util.RecyclerviewAdapter;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

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
        list = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new RecyclerviewAdapter(getActivity(), list);
        adapter.setItemLongClickListener(new RecyclerviewAdapter.onItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setItemClickListener(new RecyclerviewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pos = position;
                mItemClickListener.onItemClick(view, position);
            }
        });
        loadAlbum();
        recyclerView.setAdapter(adapter);

    }

    private void loadAlbum(){
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/TimeCamera/";
        File file = new File(dir);
        if (!file.exists()){
            file.mkdirs();
        }
        List<Map<String, Object>> albumlist = GetFilesUtils.getInstance().getSonNode(dir);

        for (Map<String, Object> album: albumlist){
            String albumname = (String) album.get(GetFilesUtils.FILE_INFO_NAME);
            List<Map<String, Object>> imageslist = GetFilesUtils.getInstance().getSonNode(dir + albumname);
            if (imageslist.size() > 0){
                Map<String, String> map = new HashMap<>();
                map.put("path", imageslist.get(imageslist.size() - 1).get(GetFilesUtils.FILE_INFO_PATH).toString());
                map.put("dirpath", dir + albumname);
                map.put("name", albumname);
                if (!list.contains(map)){
                    list.add(0, new HashMap<>(map));
                }
            }
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
            case BrowseFragment.CHANGE:
                adapter.notifyItemChanged(pos);
                break;
            case BrowseFragment.DELETE:
                adapter.notifyItemRemoved(pos);
                adapter.notifyItemRangeChanged(pos, list.size());
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
    }

    public ArrayList<Map<String, String>> getList() {
        return list;
    }
}
