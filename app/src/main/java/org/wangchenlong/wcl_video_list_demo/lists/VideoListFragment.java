package org.wangchenlong.wcl_video_list_demo.lists;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import org.wangchenlong.wcl_video_list_demo.MainActivity;
import org.wangchenlong.wcl_video_list_demo.R;
import org.wangchenlong.wcl_video_list_demo.items.LocalVideoListItem;
import org.wangchenlong.wcl_video_list_demo.items.OnlineVideoListItem;
import org.wangchenlong.wcl_video_list_demo.items.VideoListItem;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频列表视图, 可以使用URL和本地文件.
 * <p/>
 * Created by wangchenlong on 16/1/27.
 */
public class VideoListFragment extends Fragment {

    public static final String VIDEO_TYPE_ARG = "me.chunyu.spike.video_list_fragment.video_type_arg";

    // 网络视频地址
    private static final String URL =
            "http://dn-chunyu.qbox.me/fwb/static/images/home/video/video_aboutCY_A.mp4";

    // 本地资源文件名
    private static final String[] LOCAL_NAMES = new String[]{
            "chunyu-local-1.mp4",
            "chunyu-local-2.mp4",
            "chunyu-local-3.mp4",
            "chunyu-local-4.mp4"
    };

    // 在线资源名
    private static final String ONLINE_NAME = "chunyu-online";

    @Bind(R.id.video_list_rv_list) RecyclerView mRvList; // 列表视图

    private final ArrayList<VideoListItem> mList; // 视频项的列表
    private final ListItemsVisibilityCalculator mVisibilityCalculator; // 可视估计器
    private final VideoPlayerManager<MetaData> mVideoPlayerManager;

    private LinearLayoutManager mLayoutManager; // 布局管理器
    private ItemsPositionGetter mItemsPositionGetter; // 位置提取器
    private int mScrollState; // 滑动状态

    // 创建实例, 添加类型
    public static VideoListFragment newInstance(int type) {
        VideoListFragment simpleFragment = new VideoListFragment();
        Bundle args = new Bundle();
        args.putInt(VIDEO_TYPE_ARG, type);
        simpleFragment.setArguments(args);
        return simpleFragment;
    }

    // 构造器
    public VideoListFragment() {
        mList = new ArrayList<>(); // 视频的列表
        mVisibilityCalculator = new SingleListViewItemActiveCalculator(
                new DefaultSingleItemCalculatorCallback(), mList);
        mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
            @Override
            public void onPlayerItemChanged(MetaData metaData) {
            }
        });

        mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE; // 暂停滚动状态
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_video_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLocalVideoList();
        Bundle args = getArguments();
        if (args != null && args.getInt(VIDEO_TYPE_ARG) == MainActivity.ONLINE) {
            initOnlineVideoList();
        }

        mRvList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvList.setLayoutManager(mLayoutManager);
        VideoListAdapter adapter = new VideoListAdapter(mList);
        mRvList.setAdapter(adapter);

        // 获取Item的位置
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRvList);
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if (scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()) {
                    mVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!mList.isEmpty()) {
                    mVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() -
                                    mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override public void onResume() {
        super.onResume();
        if (!mList.isEmpty()) {
            mRvList.post(new Runnable() {
                @Override
                public void run() {
                    // 判断一些滚动状态
                    mVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mVideoPlayerManager.resetMediaPlayer(); // 页面不显示时, 释放播放器
    }

    // 初始化本地视频
    private void initLocalVideoList() {
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[0], R.drawable.cover, getFile(LOCAL_NAMES[0])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[1], R.drawable.cover, getFile(LOCAL_NAMES[1])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[2], R.drawable.cover, getFile(LOCAL_NAMES[2])));
        mList.add(new LocalVideoListItem(mVideoPlayerManager, LOCAL_NAMES[3], R.drawable.cover, getFile(LOCAL_NAMES[3])));
    }

    // 初始化在线视频, 需要缓冲
    private void initOnlineVideoList() {
        final int count = 10;
        for (int i = 0; i < count; ++i) {
            mList.add(new OnlineVideoListItem(mVideoPlayerManager, ONLINE_NAME, R.drawable.cover, URL));
        }
    }

    // 获取资源文件
    private AssetFileDescriptor getFile(String name) {
        try {
            return getActivity().getAssets().openFd(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
