package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pictureremind.rty813.xyz.TimeCamera.R;

/**
 * Created by zhang on 2017/12/5.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .hide(this)
                        .add(R.id.container, new CameraFragment())
                        .commit();
                break;
        }
    }
}
