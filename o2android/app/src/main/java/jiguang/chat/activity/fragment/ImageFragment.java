package jiguang.chat.activity.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
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

import jiguang.chat.adapter.ImageAdapter;
import jiguang.chat.controller.SendFileController;
import jiguang.chat.entity.FileItem;
import jiguang.chat.view.SendImageView;


public class ImageFragment extends BaseFragment {

    private Activity mContext;
    private View mRootView;
    private SendImageView mSIView;
    private ImageAdapter mAdapter;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private List<FileItem> mImages = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private SendFileController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_send_image,
                (ViewGroup) mContext.findViewById(R.id.send_doc_view), false);
        mSIView = (SendImageView) mRootView.findViewById(R.id.send_image_view);
        mSIView.initModule();
        getImages();
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

    private void getImages() {
        //显示进度条
        mProgressDialog = ProgressDialog.show(ImageFragment.this.getContext(), null,
                mContext.getString(R.string.jmui_loading));

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Images.ImageColumns.DATA,
                        MediaStore.Images.ImageColumns.DISPLAY_NAME,
                        MediaStore.Images.ImageColumns.SIZE};
                Cursor cursor = contentResolver.query(imageUri, projection, null, null,
                        MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor == null || cursor.getCount() == 0) {
                    myHandler.sendEmptyMessage(SCAN_ERROR);
                } else {
                    while (cursor.moveToNext()) {
                        //获取图片的路径
                        String path = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DATA));
                        String fileName = cursor.getString(cursor
                                .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        if (scannerFile(path)) {
                            FileItem item = new FileItem(path, fileName, size, null, 0);
                            mImages.add(item);
                        }
                    }
                    cursor.close();
                    //通知Handler扫描图片完成
                    myHandler.sendEmptyMessage(SCAN_OK);
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
        private final WeakReference<ImageFragment> mFragment;

        public MyHandler(ImageFragment fragment) {
            mFragment = new WeakReference<ImageFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        //关闭进度条
                        fragment.mProgressDialog.dismiss();
                        fragment.mAdapter = new ImageAdapter(fragment, fragment.mImages);
                        fragment.mSIView.setAdapter(fragment.mAdapter);
                        fragment.mAdapter.setUpdateListener(fragment.mController);
                        break;
                    case SCAN_ERROR:
                        fragment.mProgressDialog.dismiss();
                        Toast.makeText(fragment.getActivity(), fragment.getString(R.string.sdcard_not_prepare_toast), Toast.LENGTH_SHORT).show();
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
