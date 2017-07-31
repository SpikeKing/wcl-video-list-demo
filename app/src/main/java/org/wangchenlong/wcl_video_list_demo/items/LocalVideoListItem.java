package org.wangchenlong.wcl_video_list_demo.items;

import android.content.res.AssetFileDescriptor;
import android.support.annotation.DrawableRes;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * 本地视频项
 * <p/>
 * Created by wangchenlong on 16/1/30.
 */
public class LocalVideoListItem extends VideoListItem {

    private final AssetFileDescriptor mAssetFileDescriptor; // 资源文件描述

    public LocalVideoListItem(
            VideoPlayerManager<MetaData> videoPlayerManager,
            String title,
            @DrawableRes int imageResource,
            AssetFileDescriptor assetFileDescriptor
    ) {
        super(videoPlayerManager, title, imageResource);
        mAssetFileDescriptor = assetFileDescriptor;
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player,
                             VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mAssetFileDescriptor);
    }
}
