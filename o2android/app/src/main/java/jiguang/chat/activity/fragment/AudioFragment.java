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

import jiguang.chat.adapter.AudioAdapter;
import jiguang.chat.controller.SendFileController;
import jiguang.chat.entity.FileItem;
import jiguang.chat.view.SendAudioView;


public class AudioFragment extends BaseFragment {

    private Activity mContext;
    private View mRootView;
    private SendAudioView mSAView;
    private AudioAdapter mAdapter;
    private List<FileItem> mAudios = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private SendFileController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_send_audio,
                (ViewGroup) mContext.findViewById(R.id.send_doc_view), false);
        mSAView = (SendAudioView) mRootView.findViewById(R.id.send_audio_view);
        mSAView.initModule();
        getAudios();
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

    private void getAudios() {
        //显示进度条
        mProgressDialog = ProgressDialog.show(AudioFragment.this.getContext(), null,
                mContext.getString(R.string.jmui_loading));

        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DATE_MODIFIED, MediaStore.Audio.AudioColumns.MIME_TYPE};

                String selection = MediaStore.Audio.AudioColumns.MIME_TYPE + "= ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Audio.AudioColumns.MIME_TYPE + " = ? ";

                String[] selectionArgs = new String[] {
                        "audio/mpeg", "audio/x-ms-wma", "audio/x-wav", "audio/midi"};

                Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection, selection, selectionArgs, MediaStore.Audio.AudioColumns.DATE_MODIFIED + " desc");

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME));
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.SIZE));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_MODIFIED));
                        if (scannerFile(filePath)) {
                            FileItem fileItem = new FileItem(filePath, fileName, size, date, 0);
                            mAudios.add(fileItem);
                        }

                    }
                    cursor.close();
                    cursor = null;
                    //通知Handler扫描图片完成
                    myHandler.sendEmptyMessage(SCAN_OK);
                } else {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
                }
            }
        }).start();

    }

    private File file;

    private boolean scannerFile(String path) {
        file = new File(path);
        if (file.exists()) {
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
        private final WeakReference<AudioFragment> mFragment;

        public MyHandler(AudioFragment fragment) {
            mFragment = new WeakReference<AudioFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AudioFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        fragment.mProgressDialog.dismiss();
                        fragment.mAdapter = new AudioAdapter(fragment, fragment.mAudios);
                        fragment.mSAView.setAdapter(fragment.mAdapter);
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
