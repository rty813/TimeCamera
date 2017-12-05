package pictureremind.rty813.xyz.TimeCamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import pictureremind.rty813.xyz.TimeCamera.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (null == savedInstanceState){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
        }
    }
}
