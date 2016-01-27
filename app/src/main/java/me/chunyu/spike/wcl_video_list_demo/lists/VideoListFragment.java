package me.chunyu.spike.wcl_video_list_demo.lists;

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

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunyu.spike.wcl_video_list_demo.R;
import me.chunyu.spike.wcl_video_list_demo.items.VideoListItem;

/**
 * 视频列表视图
 * <p/>
 * Created by wangchenlong on 16/1/27.
 */
public class VideoListFragment extends Fragment {

    private static final String[] NAMES = new String[]{
            "video_sample_1.mp4",
            "video_sample_2.mp4",
            "video_sample_3.mp4",
            "video_sample_4.mp4"
    };

    @Bind(R.id.video_list_rv_list) RecyclerView mRvList; // 列表视图

    private final ArrayList<VideoListItem> mList; // 视频项的列表
    private final ListItemsVisibilityCalculator mVisibilityCalculator; // 可视估计器

    private LinearLayoutManager mLayoutManager; // 布局管理器
    private ItemsPositionGetter mItemsPositionGetter; // 位置提取器

    private final VideoPlayerManager<MetaData> mVideoPlayerManager; // 视频管理器
    private int mScrollState; // 滑动状态

    // 初始基本参数
    public VideoListFragment() {
        mList = new ArrayList<>();

        mVisibilityCalculator = new SingleListViewItemActiveCalculator(
                new DefaultSingleItemCalculatorCallback(), mList);

        mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
            @Override
            public void onPlayerItemChanged(MetaData metaData) {

            }
        });

        mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_video_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initVideoList();

        mRvList.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvList.setLayoutManager(mLayoutManager);

        VideoListAdapter adapter =
                new VideoListAdapter(mVideoPlayerManager, getActivity(), mList);
        mRvList.setAdapter(adapter);

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
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRvList);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mList.isEmpty()) {
            mRvList.post(new Runnable() {
                @Override
                public void run() {
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
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

    private void initVideoList() {
        mList.add(new VideoListItem(mVideoPlayerManager, NAMES[0], R.drawable.video_sample_1_pic, getFile(NAMES[0]), Picasso.with(getActivity())));
        mList.add(new VideoListItem(mVideoPlayerManager, NAMES[1], R.drawable.video_sample_2_pic, getFile(NAMES[1]), Picasso.with(getActivity())));
        mList.add(new VideoListItem(mVideoPlayerManager, NAMES[2], R.drawable.video_sample_1_pic, getFile(NAMES[2]), Picasso.with(getActivity())));
        mList.add(new VideoListItem(mVideoPlayerManager, NAMES[3], R.drawable.video_sample_1_pic, getFile(NAMES[3]), Picasso.with(getActivity())));
    }

    private AssetFileDescriptor getFile(String name) {
        try {
            return getActivity().getAssets().openFd(name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
