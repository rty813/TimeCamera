package pictureremind.rty813.xyz.TimeCamera.fragment;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import pictureremind.rty813.xyz.TimeCamera.R;
import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;
import pictureremind.rty813.xyz.TimeCamera.util.SQLiteDBHelper;
import pictureremind.rty813.xyz.autobackground.AutoBackground;

import static pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment.themeColor;

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
    private String albumname;
    private String mParam2;
    private String str_cyc = "每周";

    private onBtnClickListener mListener;
    private MaterialEditText et_albumname;
    private NestedScrollView nsv_root;
    public SimpleDraweeView iv_preview;
    public FrameLayout insert_container;
    private FloatingActionButton btn_commit;
    private TextView tv_picktime;
    public boolean isTookPic = false;
    private Toolbar toolbar;
    public String filepath = null;

    public NewAlbumFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param albumname Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewAlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewAlbumFragment newInstance(String albumname, String param2) {
        NewAlbumFragment fragment = new NewAlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, albumname);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumname = getArguments().getString(ARG_PARAM1);
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
        handler = new MyHandler();
        insert_container = view.findViewById(R.id.insert_container);
        iv_preview = view.findViewById(R.id.iv_preview);
        et_albumname = view.findViewById(R.id.et_albumname);
        nsv_root = view.findViewById(R.id.nsv_root);
        btn_commit = view.findViewById(R.id.btn_commit);
        tv_picktime = view.findViewById(R.id.tv_picktime);
        toolbar = view.findViewById(R.id.toolbar);
        final LinearLayout ll_choosetime = view.findViewById(R.id.ll_choosetime);

        if (albumname != null){
            insert_container.setVisibility(View.GONE);
            et_albumname.setText(albumname);
            et_albumname.setEnabled(false);
        }

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
        view.findViewById(R.id.btn_commit).setOnClickListener(this);
        ((Spinner)view.findViewById(R.id.spinner_cyc)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] cycs = getResources().getStringArray(R.array.cyc);
                str_cyc = cycs[i];
                tv_picktime.setText("");
                if (str_cyc.equals("不设置")){
                    ll_choosetime.setVisibility(View.GONE);
                }
                else{
                    ll_choosetime.setVisibility(View.VISIBLE);
                }
                checkCommit();
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
                View numberpicker = null;
                Time t=new Time();
                t.setToNow();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("设置提醒时间")
                        .setNegativeButton("取消", null)
                        .setCancelable(true);
                switch (str_cyc){
                    case "每周":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_week, null);
                        final NumberPickerView np_week_week = numberpicker.findViewById(R.id.np_week);
                        final NumberPickerView np_hour_week = numberpicker.findViewById(R.id.np_hour);
                        final NumberPickerView np_minute_week = numberpicker.findViewById(R.id.np_minute);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String string = np_week_week.getContentByCurrValue() + "-"
                                        + np_hour_week.getContentByCurrValue() + "-" + np_minute_week.getContentByCurrValue();
                                tv_picktime.setText(string);
                            }
                        });
                        break;
                    case "每月":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_month, null);
                        final NumberPickerView np_hour_month = numberpicker.findViewById(R.id.np_hour);
                        final NumberPickerView np_day_month = numberpicker.findViewById(R.id.np_day);
                        final NumberPickerView np_minute_month = numberpicker.findViewById(R.id.np_minute);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String string = np_day_month.getContentByCurrValue() + "-" +
                                        np_hour_month.getContentByCurrValue() + "-" + np_minute_month.getContentByCurrValue();
                                tv_picktime.setText(string);
                            }
                        });
                        break;
                    case "每年":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_year, null);
                        final NumberPickerView np_month_year = numberpicker.findViewById(R.id.np_month);
                        final NumberPickerView np_hour_year = numberpicker.findViewById(R.id.np_hour);
                        final NumberPickerView np_minute_year = numberpicker.findViewById(R.id.np_minute);
                        final NumberPickerView np_day_year = numberpicker.findViewById(R.id.np_day);

                        final int[] monthday = new int[]{31,28,31,30,31,30,31,31,30,31,30,31};
                        np_day_year.setMaxValue(monthday[t.month], true);
                        np_day_year.setMinValue(1);
                        np_day_year.setValue(t.monthDay);

                        ((NumberPickerView)numberpicker.findViewById(R.id.np_month)).setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 1; i <= monthday[newVal - 1]; i++){
                                    list.add(String.valueOf(i));
                                }
                                String[] strings = new String[list.size()];
                                list.toArray(strings);
                                np_day_year.refreshByNewDisplayedValues(strings);
                            }
                        });
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String string = np_month_year.getContentByCurrValue() + "-" + np_day_year.getContentByCurrValue()
                                        + "-" + np_hour_year.getContentByCurrValue() + "-" + np_minute_year.getContentByCurrValue();
                                tv_picktime.setText(string);
                            }
                        });
                        break;
                    case "每天":
                        numberpicker = LayoutInflater.from(getActivity()).inflate(R.layout.numberpicker_day, null);
                        final NumberPickerView np_hour_day = numberpicker.findViewById(R.id.np_hour);
                        final NumberPickerView np_minute_day = numberpicker.findViewById(R.id.np_minute);
                        builder.setView(numberpicker);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String string = np_hour_day.getContentByCurrValue() + "-" + np_minute_day.getContentByCurrValue();
                                tv_picktime.setText(string);
                            }
                        });
                        break;
                    default:
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
            case R.id.btn_commit:
                String albumname = et_albumname.getText().toString();
                SQLiteDatabase database = ((MainActivity)getActivity()).getDbHelper().getReadableDatabase();
                Cursor cursor = database.query(SQLiteDBHelper.TABLE_NAME, null, "NAME=?", new String[]{albumname}, null, null, null);
                if (cursor.getCount() != 0){
                    Snackbar.make(btn_commit, "该相册名称已存在，请重试", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                database.close();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
                if (this.albumname == null){
                    String date = dateFormat.format(System.currentTimeMillis());
                    String dir = MainActivity.ROOTPATH + albumname + "/" + date + ".jpg";
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        Snackbar.make(btn_commit, "保存失败！", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        int bytesum = 0;
                        int byteread = 0;
                        File oldfile = new File(filepath);
                        if (oldfile.exists()){
                            InputStream inputStream = new FileInputStream(oldfile);
                            (new File(MainActivity.ROOTPATH + albumname)).mkdirs();
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
                        Snackbar.make(btn_commit, "复制文件出错！", Snackbar.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                ContentValues values = new ContentValues();
                values.put("NAME", albumname);
                values.put("CYC", str_cyc);
                values.put("REMIND_TIME", tv_picktime.getText().toString());
                values.put("CREATE_TIME", dateFormat.format(new Date(System.currentTimeMillis())));
                values.put("LAST_TIME", dateFormat.format(new Date(System.currentTimeMillis())));
                database = ((MainActivity)getActivity()).getDbHelper().getWritableDatabase();
                database.insert(SQLiteDBHelper.TABLE_NAME, null, values);
                database.close();
                mListener.onClick(view);
                break;
        }
    }

    public interface onBtnClickListener{
        public void onClick(View v);
    }

    public void checkCommit(){
        if ((albumname == null
                && (et_albumname.getText().toString().equals("") || !isTookPic || (tv_picktime.getText().toString().equals("") && !str_cyc.equals("不设置"))))
        || ((albumname != null)
                && (tv_picktime.getText().toString().equals("")))){
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
            if (btn_commit.getVisibility() == View.GONE){
                Message msg = handler.obtainMessage();
                msg.what = 1;
                msg.obj = btn_commit;
                handler.handleMessage(msg);
                btn_commit.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.fm_camera_enter));
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        MiStatInterface.recordPageEnd();
    }

    @Override
    public void onResume() {
        super.onResume();
        MiStatInterface.recordPageStart(getActivity(), "NewAlbumFragment");
        if (themeColor != null){
            new AutoBackground(getActivity(), toolbar).setColor(themeColor).start();
            btn_commit.setBackgroundTintList(ColorStateList.valueOf(themeColor[0]));

        }
    }


    private static class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ((FloatingActionButton) msg.obj).setVisibility(View.VISIBLE);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private MyHandler handler;
}
