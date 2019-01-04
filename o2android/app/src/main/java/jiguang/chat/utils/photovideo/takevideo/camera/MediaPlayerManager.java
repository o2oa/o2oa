package jiguang.chat.utils.photovideo.takevideo.camera;

import android.app.Application;
import android.media.MediaPlayer;
import android.view.Surface;

import jiguang.chat.utils.photovideo.takevideo.utils.LogUtils;


/**
 * 由于拍摄跟播放都关联TextureView,停止播放时要释放mediaplayer
 */

public class MediaPlayerManager {

    private Application app;

    private MediaPlayer mPlayer;

    private MediaPlayerManager(Application app) {
        this.app = app;
    }

    private static MediaPlayerManager INSTANCE;

    public static MediaPlayerManager getInstance(Application app) {
        if (INSTANCE == null) {
            synchronized (CameraManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MediaPlayerManager(app);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 播放Media
     */
    public void playMedia(Surface surface, String mediaPath) {
        try {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(mediaPath);
            } else {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.reset();
                mPlayer.setDataSource(mediaPath);
            }
            mPlayer.setSurface(surface);
            mPlayer.setLooping(true);
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (Exception e) {
            LogUtils.i(e);
        }
    }

    /**
     * 停止播放Media
     */
    public void stopMedia() {
        try {
            if (mPlayer != null) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                mPlayer.release();
                mPlayer = null;
            }
        } catch (Exception e) {
            LogUtils.i(e);
        }
    }

}
