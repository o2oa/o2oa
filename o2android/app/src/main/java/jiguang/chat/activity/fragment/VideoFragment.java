package jiguang.chat.activity.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import jiguang.chat.adapter.VideoAdapter;
import jiguang.chat.controller.SendFileController;
import jiguang.chat.entity.FileItem;
import jiguang.chat.view.SendVideoView;


public class VideoFragment extends BaseFragment {

    private Activity mContext;
    private View mRootView;
    private SendVideoView mSVView;
    private VideoAdapter mAdapter;
    private List<FileItem> mVideos = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private SendFileController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_send_video,
                (ViewGroup) mContext.findViewById(R.id.send_doc_view), false);
        mSVView = (SendVideoView) mRootView.findViewById(R.id.send_video_view);
        mSVView.initModule();
        getVideos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    private void getVideos() {
        //显示进度条
        mProgressDialog = ProgressDialog.show(VideoFragment.this.getContext(), null,
                mContext.getString(R.string.jmui_loading));

        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.SIZE,
                        MediaStore.Video.VideoColumns.DATE_MODIFIED};
                try {
                    String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                            + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

                    //类型是在http://qd5.iteye.com/blog/1564040找的
                    String[] selectionArgs = new String[] {
                            "video/quicktime", "video/mp4", "application/vnd.rn-realmedia", "aapplication/vnd.rn-realmedia",
                            "video/x-ms-wmv", "video/x-msvideo", "video/3gpp", "video/x-matroska"};

                    Cursor cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                            selection, selectionArgs, MediaStore.Video.VideoColumns.DATE_MODIFIED + " desc");

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME));
                            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                            String size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE));
                            String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATE_MODIFIED));
                            if (scannerFile(filePath)) {
                                FileItem fileItem = new FileItem(filePath, fileName, size, date,0);
                                mVideos.add(fileItem);
                            }
                        }
                        cursor.close();
                        cursor = null;
                        myHandler.sendEmptyMessage(SCAN_OK);
                    } else {
                        myHandler.sendEmptyMessage(SCAN_ERROR);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private File file;

    private boolean scannerFile(String path) {
        file = new File(path);
        if (file.exists() && file.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setController(SendFileController controller) {
        this.mController = controller;
    }

    public int getTotalCount() {
        return mController.getPathListSize();
    }

    public long getTotalSize() {
        return mController.getTotalSize();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<VideoFragment> mFragment;

        public MyHandler(VideoFragment fragment) {
            mFragment = new WeakReference<VideoFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            VideoFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        //关闭进度条
                        fragment.mProgressDialog.dismiss();
                        fragment.mAdapter = new VideoAdapter(fragment, fragment.mVideos,
                                fragment.mDensity);
                        fragment.mSVView.setAdapter(fragment.mAdapter);
                        fragment.mAdapter.setUpdateListener(fragment.mController);
                        break;
                    case SCAN_ERROR:
                        fragment.mProgressDialog.dismiss();
                        Toast.makeText(fragment.getActivity(), fragment.getString(R.string.sdcard_not_prepare_toast),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mProgressDialog) {
            mProgressDialog.dismiss();
        }
    }
}
