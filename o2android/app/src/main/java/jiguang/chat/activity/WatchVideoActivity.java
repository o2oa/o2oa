package jiguang.chat.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;


/**
 * 视频播放界面
 */
public class WatchVideoActivity extends Activity{

    private VideoView mVv_video;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_watch_video);

        mVv_video = (VideoView) findViewById(R.id.vv_video);

        String videoPath = getIntent().getStringExtra("video_path");

        mVv_video.setVideoPath(videoPath);
        mVv_video.start();
    }
}