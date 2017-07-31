package org.wangchenlong.wcl_video_list_demo.items;

import android.support.annotation.DrawableRes;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * 播放在线视频
 * <p/>
 * Created by wangchenlong on 16/1/30.
 */
public class OnlineVideoListItem extends VideoListItem {

    private final String mOnlineUrl; // 资源文件描述

    public OnlineVideoListItem(
            VideoPlayerManager<MetaData> videoPlayerManager,
            String title,
            @DrawableRes int imageResource,
            String onlineUrl
    ) {
        super(videoPlayerManager, title, imageResource);

        mOnlineUrl = onlineUrl;
    }

    @Override
    public void playNewVideo(MetaData currentItemMetaData, VideoPlayerView player, VideoPlayerManager<MetaData> videoPlayerManager) {
        videoPlayerManager.playNewVideo(currentItemMetaData, player, mOnlineUrl);
    }
}
