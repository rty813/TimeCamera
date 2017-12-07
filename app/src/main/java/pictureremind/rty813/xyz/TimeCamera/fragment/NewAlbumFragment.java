package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import pictureremind.rty813.xyz.TimeCamera.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewAlbumFragment.onBtnClickListener} interface
 * to handle interaction events.
 * Use the {@link NewAlbumFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewAlbumFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String str_cyc = "每周";

    private onBtnClickListener mListener;

    public NewAlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewAlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewAlbumFragment newInstance(String param1, String param2) {
        NewAlbumFragment fragment = new NewAlbumFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.insert_container).setOnClickListener(this);
        view.findViewById(R.id.btn_picktime).setOnClickListener(this);
        ((Spinner)view.findViewById(R.id.spinner_cyc)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] cycs = getResources().getStringArray(R.array.cyc);
                str_cyc = cycs[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.insert_container:
                mListener.onClick(view);
                break;
            case R.id.btn_picktime:
                Toast.makeText(getActivity(), str_cyc, Toast.LENGTH_SHORT).show();
                View numberpicker = null;
                Time t=new Time();
                t.setToNow();
                DialogInterface.OnClickListener listener = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("提醒时间")
                        .setNegativeButton("取消", null)
                        .setCancelable(true);
                switch (str_cyc){
                    case "每周":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_week, null);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每周！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "每月":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_month, null);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每周！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "每年":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_year, null);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每周！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "每天":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_day, null);
                        builder.setView(R.layout.numberpicker_day);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每天！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "每小时":

                        break;
                }
                if (numberpicker.findViewById(R.id.np_hour) != null){
                    ((NumberPicker)numberpicker.findViewById(R.id.np_hour)).setMaxValue(23);
                    ((NumberPicker)numberpicker.findViewById(R.id.np_hour)).setMinValue(0);
                }
                if (numberpicker.findViewById(R.id.np_week) != null){
                    ((NumberPicker)numberpicker.findViewById(R.id.np_week)).setMaxValue(7);
                    ((NumberPicker)numberpicker.findViewById(R.id.np_week)).setMinValue(1);
                }
                if (numberpicker.findViewById(R.id.np_day) != null){
                    ((NumberPicker)numberpicker.findViewById(R.id.np_day)).setMaxValue(31);
                    ((NumberPicker)numberpicker.findViewById(R.id.np_day)).setMinValue(1);
                }
                if (numberpicker.findViewById(R.id.np_minute) != null){
                    ((NumberPicker)numberpicker.findViewById(R.id.np_minute)).setMaxValue(59);
                    ((NumberPicker)numberpicker.findViewById(R.id.np_minute)).setMinValue(0);
                }
                if (numberpicker.findViewById(R.id.np_month) != null){
                    ((NumberPicker)numberpicker.findViewById(R.id.np_month)).setMaxValue(12);
                    ((NumberPicker)numberpicker.findViewById(R.id.np_month)).setMinValue(1);
                }

                builder.show();

                break;
        }
    }

    public interface onBtnClickListener{
        public void onClick(View v);
    }

}
