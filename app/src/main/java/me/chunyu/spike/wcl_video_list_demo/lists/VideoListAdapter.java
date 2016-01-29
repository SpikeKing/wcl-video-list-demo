package me.chunyu.spike.wcl_video_list_demo.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.chunyu.spike.wcl_video_list_demo.R;
import me.chunyu.spike.wcl_video_list_demo.items.VideoListItem;

/**
 * 视频列表的适配器
 * <p/>
 * Created by wangchenlong on 16/1/27.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager; // 视频播放器管理器
    private final List<VideoListItem> mList; // 视频项列表
    private Context mContext;

    // 构造器
    public VideoListAdapter(VideoPlayerManager videoPlayerManager, Context context, List<VideoListItem> list) {
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public VideoListAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        VideoListItem videoItem = mList.get(viewType);
        View view = videoItem.createView(parent,
                mContext.getResources().getDisplayMetrics().widthPixels);
        return new VideoViewHolder(view);
    }

    @Override public void onBindViewHolder(VideoListAdapter.VideoViewHolder holder, int position) {
        VideoListItem videoItem = mList.get(position);
        videoItem.update(position, holder, mVideoPlayerManager);
    }

    @Override public int getItemCount() {
        return mList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_video_vpv_player) VideoPlayerView mVpvPlayer; // 播放控件
        @Bind(R.id.item_video_iv_cover) ImageView mIvCover; // 覆盖层
        @Bind(R.id.item_video_tv_title) TextView mTvTitle; // 标题
        @Bind(R.id.item_video_tv_percents) TextView mTvPercents; // 百分比

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        // 返回播放器
        public VideoPlayerView getVpvPlayer() {
            return mVpvPlayer;
        }

        // 返回百分比
        public TextView getTvPercents() {
            return mTvPercents;
        }

        // 返回覆盖图片
        public ImageView getIvCover() {
            return mIvCover;
        }

        public TextView getTvTitle() {
            return mTvTitle;
        }
    }
}
