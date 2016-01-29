package me.chunyu.spike.wcl_video_list_demo.items;

import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.volokh.danylo.video_player_manager.manager.VideoItem;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.volokh.danylo.visibility_utils.items.ListItem;

import me.chunyu.spike.wcl_video_list_demo.lists.VideoListAdapter;

/**
 * 基本视频项, 实现适配项和列表项
 * <p/>
 * Created by wangchenlong on 16/1/27.
 */
public class VideoListItem implements VideoItem, ListItem {

    private final Rect mCurrentViewRect = new Rect(); // 当前视图的方框
    private final VideoPlayerManager<MetaData> mVideoPlayerManager; // 视频播放管理器

    private final String mTitle; // 标题
    @DrawableRes private final int mImageResource; // 图片资源
    private final AssetFileDescriptor mAssetFileDescriptor; // 资源文件描述

    // 构造器, 输入视频播放管理器
    public VideoListItem(
            VideoPlayerManager<MetaData> videoPlayerManager,
            String title,
            @DrawableRes int imageResource,
            AssetFileDescriptor assetFileDescriptor
    ) {
        mVideoPlayerManager = videoPlayerManager;

        mTitle = title;
        mAssetFileDescriptor = assetFileDescriptor;
        mImageResource = imageResource;
    }

    // 视频项的标题
    public String getTitle() {
        return mTitle;
    }

    // 视频项的背景
    public int getImageResource() {
        return mImageResource;
    }

    // 显示可视的百分比程度
    @Override public int getVisibilityPercents(View view) {
        int percents = 100;

        view.getLocalVisibleRect(mCurrentViewRect);
        int height = view.getHeight();

        if (viewIsPartiallyHiddenTop()) {
            percents = (height - mCurrentViewRect.top) * 100 / height;
        } else if (viewIsPartiallyHiddenBottom(height)) {
            percents = mCurrentViewRect.bottom * 100 / height;
        }

        // 设置百分比
        setVisibilityPercentsText(view, percents);

        return percents;
    }

    @Override public void setActive(View newActiveView, int newActiveViewPosition) {
        VideoListAdapter.VideoViewHolder viewHolder =
                (VideoListAdapter.VideoViewHolder) newActiveView.getTag();
        playNewVideo(new CurrentItemMetaData(newActiveViewPosition, newActiveView),
                viewHolder.getVpvPlayer(), mVideoPlayerManager);
    }

    @Override public void deactivate(View currentView, int position) {
        stopPlayback(mVideoPlayerManager);
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player,
                             VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mAssetFileDescriptor);
    }

    @Override public void stopPlayback(VideoPlayerManager videoPlayerManager) {
        videoPlayerManager.stopAnyPlayback();
    }

    // 显示百分比
    private void setVisibilityPercentsText(View currentView, int percents) {
        VideoListAdapter.VideoViewHolder vh =
                (VideoListAdapter.VideoViewHolder) currentView.getTag();
        String percentsText = "可视百分比: " + String.valueOf(percents);
        vh.getTvPercents().setText(percentsText);
    }

    // 顶部出现
    private boolean viewIsPartiallyHiddenTop() {
        return mCurrentViewRect.top > 0;
    }

    // 底部出现
    private boolean viewIsPartiallyHiddenBottom(int height) {
        return mCurrentViewRect.bottom > 0 && mCurrentViewRect.bottom < height;
    }
}