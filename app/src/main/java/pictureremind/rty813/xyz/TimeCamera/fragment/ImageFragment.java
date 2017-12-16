package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

import pictureremind.rty813.xyz.TimeCamera.R;



public class ImageFragment extends Fragment {
    private static final String ARG_PARAM1 = "PATH";

    private String path;
    private SimpleDraweeView simpleDraweeView;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String path) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            path = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        simpleDraweeView = view.findViewById(R.id.draweeView);
        simpleDraweeView.post(new Runnable() {
            @Override
            public void run() {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int width = simpleDraweeView.getWidth();
                float ratio = (float)options.outHeight / options.outWidth;
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(path)))
                        .setResizeOptions(new ResizeOptions(width, (int) (width * ratio)))
                        .build();

                simpleDraweeView.setController(Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request).build());
            }
        });
    }
}
