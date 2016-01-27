package me.chunyu.spike.wcl_video_list_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunyu.spike.wcl_video_list_demo.lists.VideoListFragment;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.main_t_toolbar) Toolbar mTToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mTToolbar.setTitle("列表");
        setSupportActionBar(mTToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_fl_container, new VideoListFragment())
                    .commit();
        }
    }
}
