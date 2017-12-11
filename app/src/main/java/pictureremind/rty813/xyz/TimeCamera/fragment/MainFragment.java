package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomi.mistatistic.sdk.MiStatInterface;
import com.yanzhenjie.recyclerview.swipe.SwipeItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;
import pictureremind.rty813.xyz.TimeCamera.util.RecyclerviewAdapter;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

/**
 * Created by zhang on 2017/12/5.
 */

public class MainFragment extends Fragment implements View.OnClickListener, OnItemMoveListener {
    private onBtnClickListener mListener;
    private SwipeMenuRecyclerView recyclerView;
    private RecyclerviewAdapter adapter;
    private ArrayList<Map<String, String>> list;
    private onSwipeItemClickListener swipeItemClickListener;
    private Toolbar toolbar;
    public static int[] themeColor = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button).setOnClickListener(this);
        view.findViewById(R.id.btn_insert).setOnClickListener(this);
        toolbar = view.findViewById(R.id.toolbar);
        list = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            list.add(null);
        }
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLongPressDragEnabled(true);
        recyclerView.setSwipeItemClickListener(new SwipeItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                swipeItemClickListener.onItemClick(itemView, position);
            }
        });
        recyclerView.setOnItemMoveListener(new OnItemMoveListener() {
            @Override
            public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
                int fromPosition = srcHolder.getAdapterPosition();
                int toPosition = targetHolder.getAdapterPosition();
                if (fromPosition < toPosition)
                    for (int i = fromPosition; i < toPosition; i++)
                        Collections.swap(list, i, i + 1);
                else
                    for (int i = fromPosition; i > toPosition; i--)
                        Collections.swap(list, i, i - 1);

                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
                int position = srcHolder.getAdapterPosition();
                list.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        adapter = new RecyclerviewAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

    }

    public void setToolbarColor(){
        int toolbarColor = ((MainActivity)getActivity()).tb_color;
        int toolbarTextColor = ((MainActivity)getActivity()).tb_title;
        int subTextColor = ((MainActivity)getActivity()).tb_sub;
        if (toolbarColor != -1) {
            toolbar.setBackgroundColor(toolbarColor);
        }
        if (toolbarTextColor != -1){
            toolbar.setTitleTextColor(toolbarTextColor);
            if (subTextColor != -1){
                toolbar.setSubtitleTextColor(subTextColor);
            }
            else {
                toolbar.setSubtitleTextColor(toolbarTextColor);
            }
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                this.mListener.onClick(view);
                break;
            case R.id.btn_insert:
                this.mListener.onClick(view);
                break;
        }
    }

    @Override
    public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
        return false;
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

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

    public interface onSwipeItemClickListener{
        public void onItemClick(View viewItem, int position);
    }

    public void setSwipeItemClickListener(onSwipeItemClickListener listener){
        this.swipeItemClickListener = listener;
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
            }
        }).start();

    }
}
