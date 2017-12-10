package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;

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
    private MaterialEditText et_albumname;
    private NestedScrollView nsv_root;
    public ImageView iv_preview;
    private String filepath;
    public FrameLayout insert_container;
    private String albumname;
    private FloatingActionButton btn_commit;
    private TextView tv_picktime;
    public boolean isTookPic = false;
    private Toolbar toolbar;

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
//        myHandler = new MyHandler();
        insert_container = view.findViewById(R.id.insert_container);
        iv_preview = view.findViewById(R.id.iv_preview);
        et_albumname = view.findViewById(R.id.et_albumname);
        nsv_root = view.findViewById(R.id.nsv_root);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_picktime = view.findViewById(R.id.tv_picktime);
        toolbar = view.findViewById(R.id.toolbar);
        setToolbarColor();
        final LinearLayout ll_choosetime = view.findViewById(R.id.ll_choosetime);

        et_albumname.validate("\\d+", "Only Integer Valid!");
        et_albumname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkCommit();
            }
        });
        tv_picktime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                System.out.println("afterTextChanged");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkCommit();
                System.out.println("afterTextChanged");
            }
        });
        insert_container.setOnClickListener(this);
        view.findViewById(R.id.btn_picktime).setOnClickListener(this);
        ((Spinner)view.findViewById(R.id.spinner_cyc)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] cycs = getResources().getStringArray(R.array.cyc);
                str_cyc = cycs[i];
                if (str_cyc.equals("每小时")){
                    ll_choosetime.setVisibility(View.GONE);
                }
                else{
                    ll_choosetime.setVisibility(View.VISIBLE);
                }
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
                                tv_picktime.setText("hh");
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
                                tv_picktime.setText("hh");
                            }
                        });
                        break;
                    case "每年":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_year, null);
                        final NumberPickerView npday = numberpicker.findViewById(R.id.np_day);
                        final int[] monthday = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
                        npday.setMaxValue(monthday[t.month], true);
                        npday.setMinValue(1);
                        npday.setValue(t.monthDay);

                        ((NumberPickerView)numberpicker.findViewById(R.id.np_month)).setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 1; i <= monthday[newVal - 1]; i++){
                                    list.add(String.valueOf(i));
                                }
                                String[] strings = new String[list.size()];
                                list.toArray(strings);
                                npday.refreshByNewDisplayedValues(strings);
                            }
                        });
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每周！", Toast.LENGTH_SHORT).show();
                                tv_picktime.setText("hh");
                            }
                        });
                        break;
                    case "每天":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_day, null);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity(), "每天！", Toast.LENGTH_SHORT).show();
                                tv_picktime.setText("hh");
                            }
                        });
                        break;
                    case "每小时":

                        break;
                }
                if (numberpicker.findViewById(R.id.np_hour) != null){
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_hour)).setMaxValue(23, true);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_hour)).setMinValue(0);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_hour)).setValue(t.hour);
                }
                if (numberpicker.findViewById(R.id.np_week) != null){
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_week)).setDisplayedValues(new String[]{"星期一",
                            "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"});
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_week)).setValue(t.weekDay==0? 7: t.weekDay);
                }
                if (numberpicker.findViewById(R.id.np_day) != null && !str_cyc.equals("每年")){
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_day)).setMaxValue(31, true);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_day)).setMinValue(1);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_day)).setValue(t.monthDay);
                }
                if (numberpicker.findViewById(R.id.np_minute) != null){
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_minute)).setMaxValue(59, true);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_minute)).setMinValue(0);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_minute)).setValue(t.minute);
                }
                if (numberpicker.findViewById(R.id.np_month) != null){
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_month)).setMaxValue(12, true);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_month)).setMinValue(1);
                    ((NumberPickerView)numberpicker.findViewById(R.id.np_month)).setValue(t.month + 1);
                }

                builder.show();

                break;
        }
    }

    public interface onBtnClickListener{
        public void onClick(View v);
    }

    public void checkCommit(){
        if (et_albumname.getText().toString().equals("") || !isTookPic
                || tv_picktime.getText().toString().equals("")){
            System.out.println("dismiss");
            if (btn_commit.getVisibility() == View.VISIBLE){
                Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.fm_pop_exit);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        btn_commit.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                btn_commit.startAnimation(animation);
            }
        }
        else {
            System.out.println("show");
            if (btn_commit.getVisibility() == View.GONE){
                btn_commit.setVisibility(View.VISIBLE);
                btn_commit.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fm_camera_enter));
            }
        }

    }


    public void setToolbarColor(){
        int toolbarColor = ((MainActivity)getActivity()).tb_color;
        int toolbarTextColor = ((MainActivity)getActivity()).tb_title;
        int subTextColor = ((MainActivity)getActivity()).tb_sub;
        if (toolbarColor != -1 && toolbarTextColor != -1 && subTextColor != -1){
            toolbar.setBackgroundColor(toolbarColor);
            toolbar.setTitleTextColor(toolbarTextColor);
            toolbar.setSubtitleTextColor(subTextColor);
        }
    }

}
