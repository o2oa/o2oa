package jiguang.chat.activity.historyfile.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import jiguang.chat.activity.fragment.BaseFragment;
import jiguang.chat.activity.historyfile.adapter.VideoFileAdapter;
import jiguang.chat.activity.historyfile.controller.HistoryFileController;
import jiguang.chat.entity.FileItem;
import jiguang.chat.view.listview.StickyListHeadersListView;

/**
 * Created by ${chenyn} on 2017/8/23.
 */

public class VideoFileFragment extends BaseFragment {
    private HistoryFileController mController;
    private String mUserName;
    private long mGroupId;
    private Activity mContext;
    private View mRootView;
    private final static int SCAN_OK = 1;
    private final static int SCAN_ERROR = 0;
    private VideoFileAdapter mAdapter;
    private List<FileItem> mDocuments = new ArrayList<>();
    private StickyListHeadersListView mDocumentList;
    private Boolean mIsGroup;
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();

    public void setController(HistoryFileController controller, String userName, long groupId, boolean isGroup, Activity activity) {
        mController = controller;
        mUserName = userName;
        mGroupId = groupId;
        mIsGroup = isGroup;
        mContext = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.document_file,
                (ViewGroup) mContext.findViewById(R.id.main_view), false);
        mDocumentList = (StickyListHeadersListView) mRootView.findViewById(R.id.document_list);
        initData();
    }

    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Conversation conversation;
                if (mIsGroup) {
                    conversation = JMessageClient.getGroupConversation(mGroupId);
                } else {
                    conversation = JMessageClient.getSingleConversation(mUserName);
                }
                List<Message> allMessage = conversation.getAllMessage();
                for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                    MessageContent content = msg.getContent();

                    if (content.getContentType() == ContentType.file) {
                        String fileType = content.getStringExtra("fileType");
                        if (fileType != null && (fileType.equals("mp4") || fileType.equals("mov") || fileType.equals("rm") ||
                                fileType.equals("rmvb") || fileType.equals("wmv") || fileType.equals("avi") ||
                                fileType.equals("3gp") || fileType.equals("mkv"))) {

                            FileContent fileContent = (FileContent) content;
                            String localPath = fileContent.getLocalPath();

                            long createTime = msg.getCreateTime();
                            long fileSize = fileContent.getFileSize();
                            Date date = new Date(createTime);
                            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
                            String time = format.format(date);
                            FileItem item = new FileItem(localPath, fileContent.getFileName(), fileSize + "", time, msg.getId(), msg.getFromName(), msg);
                            if (!sectionMap.containsKey(item.getDate())) {
                                item.setSection(section);
                                sectionMap.put(item.getDate(), section);
                                section++;
                            } else {
                                item.setSection(sectionMap.get(item.getDate()));
                            }
                            mDocuments.add(item);
                        }

                    }
                    mHandler.sendEmptyMessage(SCAN_OK);
                }
            }
        }).start();
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SCAN_OK:
                    if (mDocumentList != null) {
                        mAdapter = new VideoFileAdapter(mContext, mDocuments);
                        mDocumentList.setAdapter(mAdapter);
                        mAdapter.setUpdateListener(mController);
                    }
                    break;
                case SCAN_ERROR:
                    Toast.makeText(mContext, getString(R.string.sdcard_not_prepare_toast),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup p = (ViewGroup) mRootView.getParent();
        if (p != null) {
            p.removeAllViewsInLayout();
        }
        return mRootView;
    }

    public void notifyVideo() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void notifyListVideo() {
        mDocuments.clear();
        initData();
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }


}
