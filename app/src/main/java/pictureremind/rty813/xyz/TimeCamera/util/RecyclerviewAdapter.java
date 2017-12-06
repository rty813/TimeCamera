package pictureremind.rty813.xyz.TimeCamera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import net.bither.util.NativeUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;

/**
 * Created by doufu on 2017/12/6.
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter {
    private ArrayList<Map<String, String>> list;
    private Context mContext;

    public RecyclerviewAdapter(Context context, ArrayList<Map<String, String>> list){
        super();
        this.list = list;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageview, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.itemView.setTag(position);
        ImageView imageView = ((MyViewHolder) holder).getIv_item();
        FrameLayout ll_root = ((MyViewHolder) holder).getLl_root();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int pic;
        if (position % 2 == 1){
            pic = R.drawable.test2;
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test2, options);
        }
        else{
            pic = R.drawable.test;
            BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test, options); // 此时返回的bitmap为null
        }
        int width = viewHolder.getWidth();
        int height = (int)(width * ((float)options.outHeight / options.outWidth));

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) ll_root.getLayoutParams();
        layoutParams.height = height;
        Picasso.with(mContext).load(pic)
                .centerInside().resize(width, height)
                .into(imageView);
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.test);
//        float prop = (float)bitmap.getHeight() / bitmap.getWidth();
//        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) ll_root.getLayoutParams();
//        layoutParams.height = (int)(layoutParams.width * prop);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0: list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_item;
        private FrameLayout ll_root;
        private int width;
        public MyViewHolder(View itemView) {
            super(itemView);
            iv_item = itemView.findViewById(R.id.iv_item);
            ll_root = itemView.findViewById(R.id.ll_root);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) ll_root.getLayoutParams();
            layoutParams.width = MainActivity.width / 2 - 40;
            this.width = layoutParams.width;
        }

        public int getWidth() {
            return width;
        }

        public ImageView getIv_item() {
            return iv_item;
        }

        public FrameLayout getLl_root() {
            return ll_root;
        }
    }
}

