package org.wangchenlong.wcl_video_list_demo.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import org.wangchenlong.wcl_video_list_demo.R;
import org.wangchenlong.wcl_video_list_demo.items.VideoListItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频列表的适配器
 * <p/>
 * Created by wangchenlong on 16/1/27.
 */
public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {
    private final List<VideoListItem> mList; // 视频项列表

    public VideoListAdapter(List<VideoListItem> list) {
        mList = list; // 数据源
    }

    @Override
    public VideoListAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        VideoListAdapter.VideoViewHolder holder = new VideoListAdapter.VideoViewHolder(view);
        view.setTag(holder); // 设置Tag，在视频播放时使用，优化加载速度
        return holder;
    }

    @Override
    public void onBindViewHolder(final VideoListAdapter.VideoViewHolder holder, int position) {
        VideoListItem videoItem = mList.get(position);
        holder.bindTo(videoItem);
    }

    @Override public int getItemCount() {
        return mList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_video_vpv_player) VideoPlayerView mVpvPlayer; // 播放控件
        @Bind(R.id.item_video_iv_cover) ImageView mIvCover; // 覆盖层
        @Bind(R.id.item_video_tv_title) TextView mTvTitle; // 标题
        @Bind(R.id.item_video_tv_percents) TextView mTvPercents; // 百分比

        private Context mContext;
        private MediaPlayerWrapper.MainThreadMediaPlayerListener mPlayerListener;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mContext = itemView.getContext().getApplicationContext();
            mPlayerListener = new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
                @Override
                public void onVideoSizeChangedMainThread(int width, int height) {
                }

                @Override
                public void onVideoPreparedMainThread() {
                    // 视频播放隐藏前图
                    mIvCover.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                }

                @Override
                public void onErrorMainThread(int what, int extra) {
                }

                @Override
                public void onBufferingUpdateMainThread(int percent) {
                }

                @Override
                public void onVideoStoppedMainThread() {
                    // 视频暂停显示前图
                    mIvCover.setVisibility(View.VISIBLE);
                }
            };

            mVpvPlayer.addMediaPlayerListener(mPlayerListener);
        }

        public void bindTo(VideoListItem vli) {
            mTvTitle.setText(vli.getTitle());
            mIvCover.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(vli.getImageResource()).into(mIvCover);
        }

        // 返回播放器
        public VideoPlayerView getVpvPlayer() {
            return mVpvPlayer;
        }

        // 返回百分比
        public TextView getTvPercents() {
            return mTvPercents;
        }
    }
}
