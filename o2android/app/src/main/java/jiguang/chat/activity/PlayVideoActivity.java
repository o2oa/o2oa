package jiguang.chat.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.util.ArrayList;


public class PlayVideoActivity extends BaseActivity {

    private VideoView mPlayVideoVV;
    private MediaController mController;
    private int mCurrentPos;
    private ArrayList<String> mPathList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        mPlayVideoVV = (VideoView) findViewById(R.id.play_video_vv);
        mController = new MediaController(this);
        String path = getIntent().getStringExtra("videoPath");
        mPathList = getIntent().getStringArrayListExtra("videoPathList");
        mCurrentPos = mPathList.indexOf(path);
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            mPlayVideoVV.setVideoPath(path);
            mPlayVideoVV.setMediaController(mController);
            mController.setMediaPlayer(mPlayVideoVV);
            mPlayVideoVV.start();
            mController.setPrevNextListeners(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int nextPos;
                    if (mCurrentPos + 1 == mPathList.size()) {
                        nextPos = 0;
                    } else {
                        nextPos = mCurrentPos + 1;
                    }
                    mPlayVideoVV.setVideoPath(mPathList.get(nextPos));
                    mPlayVideoVV.start();
                    mCurrentPos = nextPos;
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int prevPos;
                    if (mCurrentPos != 0) {
                        prevPos = mCurrentPos - 1;
                    } else {
                        prevPos = mPathList.size() - 1;
                    }
                    mPlayVideoVV.setVideoPath(mPathList.get(prevPos));
                    mPlayVideoVV.start();
                    mCurrentPos = prevPos;
                }
            });
        }
    }
}
