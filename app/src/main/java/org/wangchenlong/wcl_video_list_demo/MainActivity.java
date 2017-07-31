package org.wangchenlong.wcl_video_list_demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.wangchenlong.wcl_video_list_demo.lists.VideoListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final int LOCAL = 0; // 本地
    public static final int ONLINE = 1; // 在线

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
                    .replace(R.id.main_fl_container, VideoListFragment.newInstance(LOCAL))
                    .commit();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.enable_local_video:
                if (!item.isChecked()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fl_container, VideoListFragment.newInstance(LOCAL))
                            .commit();
                }
                break;
            case R.id.enable_online_video:
                if (!item.isChecked()) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_fl_container, VideoListFragment.newInstance(ONLINE))
                            .commit();
                }
                break;
        }

        item.setChecked(!item.isChecked()); // 改变视频选中状态

        return true;
    }
}
