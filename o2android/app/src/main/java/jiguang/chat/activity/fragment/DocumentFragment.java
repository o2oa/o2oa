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

import jiguang.chat.adapter.DocumentAdapter;
import jiguang.chat.controller.SendFileController;
import jiguang.chat.entity.FileItem;
import jiguang.chat.view.SendDocumentView;


public class DocumentFragment extends BaseFragment {

    private Activity mContext;
    private View mRootView;
    private SendDocumentView mView;
    private DocumentAdapter mAdapter;
    private List<FileItem> mDocuments = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private SendFileController mController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_send_doc,
                (ViewGroup) mContext.findViewById(R.id.send_doc_view), false);
        mView = (SendDocumentView) mRootView.findViewById(R.id.send_doc_view);
        mView.initModule();
        getDocuments();
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

    public void setController(SendFileController controller) {
        this.mController = controller;
    }

    public int getTotalCount() {
        return mController.getPathListSize();
    }

    public long getTotalSize() {
        return mController.getTotalSize();
    }


    private void getDocuments() {
        //显示进度条
        mProgressDialog = ProgressDialog.show(DocumentFragment.this.getContext(), null,
                mContext.getString(R.string.jmui_loading));

        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentResolver contentResolver = mContext.getContentResolver();
                String[] projection = new String[] {MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.TITLE, MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.DATE_MODIFIED};

                //分别对应 txt doc pdf ppt xls wps docx pptx xlsx 类型的文档
                String selection = MediaStore.Files.FileColumns.MIME_TYPE + "= ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? "
                        + " or " + MediaStore.Files.FileColumns.MIME_TYPE + " = ? ";

                String[] selectionArgs = new String[] {"text/plain", "application/msword", "application/pdf",
                        "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "application/vnd.ms-works",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};

                Cursor cursor = contentResolver.query(MediaStore.Files.getContentUri("external"), projection,
                        selection, selectionArgs, MediaStore.Files.FileColumns.DATE_MODIFIED + " desc");

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                        String size = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                        String date = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED));
                        if (scannerFile(filePath)) {
                            FileItem fileItem = new FileItem(filePath, null, size, date,0);
                            mDocuments.add(fileItem);
                        }

                    }
                    cursor.close();
                    cursor = null;
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
        if (file.exists() && file.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<DocumentFragment> mFragment;

        public MyHandler(DocumentFragment fragment) {
            mFragment = new WeakReference<DocumentFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DocumentFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        //关闭进度条
                        fragment.mProgressDialog.dismiss();
                        fragment.mAdapter = new DocumentAdapter(fragment, fragment.mDocuments);
                        fragment.mView.setAdapter(fragment.mAdapter);
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
