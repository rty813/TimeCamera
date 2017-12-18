package pictureremind.rty813.xyz.TimeCamera.util;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;

/**
 * Created by doufu on 2017/12/6.
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter {
    private ArrayList<Map<String, String>> list;
    private Context mContext;
    private onItemLongClickListener mLongClickListener;
    private onItemClickListener mClickListener;

    public RecyclerviewAdapter(Context context, ArrayList<Map<String, String>> list){
        super();
        this.list = list;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imageview, parent, false);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mLongClickListener != null){
                    mLongClickListener.onItemLongClick(view, (Integer) view.getTag());
                }
                return true;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null){
                    mClickListener.onItemClick(view, (Integer) view.getTag());
                }
            }
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.itemView.setTag(position);
        SimpleDraweeView imageView = (SimpleDraweeView) ((MyViewHolder) holder).getIv_item();
        FrameLayout ll_root = ((MyViewHolder) holder).getLl_root();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        String filepath = list.get(position).get("path");
        BitmapFactory.decodeFile(filepath, options);

        int width = viewHolder.getWidth();
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

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) ll_root.getLayoutParams();
        layoutParams.height = height;
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(filepath)))
                .setResizeOptions(new ResizeOptions(width, (int) (width * prop)))
                .build();

        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imageView.getController())
                .setImageRequest(request)
                .build();

        imageView.setController(controller);
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

    public void setItemLongClickListener(onItemLongClickListener longClickListener){
        this.mLongClickListener = longClickListener;
    }

    public void setItemClickListener(onItemClickListener clickListener){
        this.mClickListener = clickListener;
    }
    
    public interface onItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public interface onItemClickListener{
        void onItemClick(View view, int position);
    }

}

