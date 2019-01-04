package jiguang.chat.activity.historyfile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import jiguang.chat.activity.fragment.BaseFragment;
import jiguang.chat.activity.historyfile.adapter.ImageFileAdapter;
import jiguang.chat.activity.historyfile.controller.HistoryFileController;
import jiguang.chat.activity.historyfile.view.YMComparator;
import jiguang.chat.entity.FileItem;
import jiguang.chat.utils.FileUtils;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.view.SendImageView;

/**
 * Created by ${chenyn} on 2017/8/23.
 */

public class ImageFileFragment extends BaseFragment {

    private HistoryFileController mController;
    private String mUserName;
    private long mGroupId;

    private Activity mContext;
    private View mRootView;
    private SendImageView mSIView;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private final MyHandler myHandler = new MyHandler(this);
    private ImageFileAdapter mAdapter;
    private List<FileItem> mImages = new ArrayList<>();
    private ArrayList<String> mPath = new ArrayList<>();
    private GridView mGridView;
    private Boolean mIsGroup;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();

    public void setController(HistoryFileController controller, String userName, long groupId, boolean isGroup) {
        mController = controller;
        mUserName = userName;
        mGroupId = groupId;
        mIsGroup = isGroup;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        SharePreferenceManager.setShowCheck(false);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.fragment_history_image,
                (ViewGroup) mContext.findViewById(R.id.send_doc_view), false);
        mSIView = (SendImageView) mRootView.findViewById(R.id.send_image_view);
        mSIView.initModule();
        //历史消息图片界面
        mGridView = mSIView.initFileViewModule();
        getImages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    private void getImages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conversation conversation;
                if (mIsGroup) {
                    conversation = JMessageClient.getGroupConversation(mGroupId);
                } else {
                    conversation = JMessageClient.getSingleConversation(mUserName);
                }
                List<cn.jpush.im.android.api.model.Message> allMessage = conversation.getAllMessage();
                for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                    MessageContent content = msg.getContent();
                    if (content.getContentType() == ContentType.image) {
                        ImageContent imageContent = (ImageContent) content;
                        //第一次登录,没有原图路径会比较奇怪,这里统一使用缩略图就不会有这种情况
                        String localPath = imageContent.getLocalThumbnailPath();
                        if (!TextUtils.isEmpty(localPath)) {
                            File imageFile = new File(localPath);
                            if (imageFile.exists()) {
                                long createTime = msg.getCreateTime();
                                long fileSize = imageFile.length();
                                Date date = new Date(createTime);
                                SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
                                String time = format.format(date);
                                String size = FileUtils.getFileSize(fileSize);
                                FileItem item = new FileItem(localPath, imageFile.getName(), size, time, msg.getId());
                                //根据时间设置listView的header,按月份分割
                                if (!sectionMap.containsKey(item.getDate())) {
                                    item.setSection(section);
                                    sectionMap.put(item.getDate(), section);
                                    section++;
                                }else {
                                    item.setSection(sectionMap.get(item.getDate()));
                                }
                                mPath.add(localPath);
                                mImages.add(item);
                            }
                        }
                        //当信息中有图片是以文件形式发送过来的如下处理
                    } else if (content.getContentType() == ContentType.file) {
                        FileContent fileContent = (FileContent) content;
                        String fileType = fileContent.getStringExtra("fileType");
                        if (fileType != null && (fileType.equals("jpeg") || fileType.equals("jpg") || fileType.equals("png") ||
                                fileType.equals("bmp") || fileType.equals("gif"))) {

                            String localPath = fileContent.getLocalPath();
                            if (!TextUtils.isEmpty(localPath)) {
                                File file = new File(localPath);
                                if (file.exists()) {
                                    long createTime = msg.getCreateTime();
                                    long fileSize = file.length();
                                    Date date = new Date(createTime);
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
                                    String time = format.format(date);
                                    String size = FileUtils.getFileSize(fileSize);
                                    FileItem item = new FileItem(localPath, file.getName(), size, time, msg.getId());
                                    if (!sectionMap.containsKey(item.getDate())) {
                                        item.setSection(section);
                                        sectionMap.put(item.getDate(), section);
                                        section++;
                                    }else {
                                        item.setSection(sectionMap.get(item.getDate()));
                                    }
                                    mPath.add(localPath);
                                    mImages.add(item);
                                }
                            }
                        }
                    }
                    myHandler.sendEmptyMessage(SCAN_OK);
                }
            }
        }).start();

    }


    private static class MyHandler extends Handler {
        private final WeakReference<ImageFileFragment> mFragment;

        public MyHandler(ImageFileFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ImageFileFragment fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case SCAN_OK:
                        //时间排序
                        Collections.sort(fragment.mImages, new YMComparator());
                        fragment.mAdapter = new ImageFileAdapter(fragment, fragment.mImages, fragment.mPath,
                                fragment.mGridView);
                        fragment.mSIView.setFileAdapter(fragment.mAdapter);
                        fragment.mAdapter.setUpdateListener(fragment.mController);
                        break;
                    case SCAN_ERROR:
                        Toast.makeText(fragment.getActivity(), fragment.getString(R.string.sdcard_not_prepare_toast), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    public void notifyGridView() {
        mImages.clear();
        mPath.clear();
        getImages();
        mAdapter.notifyDataSetChanged();
    }

    public void notifyImage() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
