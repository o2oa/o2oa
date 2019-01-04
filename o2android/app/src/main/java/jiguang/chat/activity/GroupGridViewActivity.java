package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.GroupGridViewAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.utils.DialogCreator;

/**
 * Created by ${chenyn} on 2017/5/8.
 */

public class GroupGridViewActivity extends BaseActivity {
    private static final String TAG = "ChatDetailActivity";
    private static final int ADD_MEMBERS_TO_GRIDVIEW = 2048;

    private GridView mGroup_gridView;
    private boolean mIsCreator = false;
    private long mGroupId;
    private List<UserInfo> mMemberInfoList = new ArrayList<UserInfo>();
    private int mCurrentNum;
    private static final int ADD_FRIEND_REQUEST_CODE = 3;
    private Dialog mLoadingDialog = null;
    private final MyHandler myHandler = new MyHandler(this);
    private GroupGridViewAdapter mGridViewAdapter;
    private LinearLayout mSearch_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_gridview);
        initTitle(true, true, "群成员", "", false, "");

        mGroup_gridView = (GridView) findViewById(R.id.group_gridView);
        mSearch_title = (LinearLayout) findViewById(R.id.search_title);
        initData();
    }

    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }

    private void initData() {
        mGroupId = getIntent().getLongExtra(JGApplication.GROUP_ID, 0);

        final Conversation conv = JMessageClient.getGroupConversation(mGroupId);
        GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
        mMemberInfoList = groupInfo.getGroupMembers();

        mCurrentNum = mMemberInfoList.size();
        String groupOwner = groupInfo.getGroupOwner();
        final String userName = JMessageClient.getMyInfo().getUserName();
        if (groupOwner.equals(userName)) {
            mIsCreator = true;
        }
        mGridViewAdapter = new GroupGridViewAdapter(this, mMemberInfoList, mIsCreator, mAvatarSize);
        mGroup_gridView.setAdapter(mGridViewAdapter);

        mGroup_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (position < mCurrentNum) {
                    if (mMemberInfoList.get(position).getUserName().equals(userName)) {
                        intent.setClass(GroupGridViewActivity.this, PersonalActivity.class);
                    } else {
                        UserInfo userInfo = mMemberInfoList.get(position);
                        if (userInfo.isFriend()) {
                            intent.setClass(GroupGridViewActivity.this, FriendInfoActivity.class);
                            intent.putExtra("group_grid", true);
                        } else {
                            intent.setClass(GroupGridViewActivity.this, GroupNotFriendActivity.class);
                        }
                        intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
                        intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
                        intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                    }
                    startActivity(intent);
                    // 点击添加成员按钮
                } else if (position == mCurrentNum) {
                    showContacts();

                    // 是群主, 成员个数大于1并点击删除按钮
                } else if (position == mCurrentNum + 1 && mIsCreator && mCurrentNum > 1) {
                    intent.putExtra(JGApplication.DELETE_MODE, true);
                    intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                    intent.setClass(GroupGridViewActivity.this, MembersInChatActivity.class);
                    startActivityForResult(intent, JGApplication.REQUEST_CODE_ALL_MEMBER);
                }
            }
        });

        mSearch_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupGridViewActivity.this, SearchGroupActivity.class);
                JGApplication.mSearchGroup = mMemberInfoList;
                startActivity(intent);
            }
        });

    }

    public void showContacts() {
        Intent intent = new Intent();
        intent.putExtra(TAG, 1);
        //作用是已经在群组中的人默认勾选checkbox
        intent.putExtra("add_friend_group_id", mGroupId);
        intent.setClass(this, SelectFriendActivity.class);
        startActivityForResult(intent, ADD_FRIEND_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_FRIEND_REQUEST_CODE && data != null) {
            ArrayList<String> list = data.getStringArrayListExtra("SelectedUser");
            if (null != list && list.size() != 0) {
                addMembersToGroup(list);
            }
        }
    }

    public void addMembersToGroup(ArrayList<String> users) {
        ArrayList<String> list = new ArrayList<>();
        for (String username : users) {
            if (checkIfNotContainUser(username)) {
                list.add(username);
            }
        }
        if (list.size() > 0) {
            mLoadingDialog = DialogCreator.createLoadingDialog(GroupGridViewActivity.this,
                    getString(R.string.adding_hint));
            mLoadingDialog.show();
            android.os.Message msg = myHandler.obtainMessage();
            msg.what = ADD_MEMBERS_TO_GRIDVIEW;
            msg.obj = list;
            msg.sendToTarget();
        }
    }

    private boolean checkIfNotContainUser(String targetID) {
        if (mMemberInfoList != null) {
            for (UserInfo userInfo : mMemberInfoList) {
                if (userInfo.getUserName().equals(targetID))
                    return false;
            }
            return true;
        }
        return true;
    }

    private static class MyHandler extends Handler {
        private final WeakReference<GroupGridViewActivity> mActivity;

        public MyHandler(GroupGridViewActivity controller) {
            mActivity = new WeakReference<>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            GroupGridViewActivity controller = mActivity.get();
            if (controller != null) {
                switch (msg.what) {
                    //好友模式从通讯录中添加好友
                    case ADD_MEMBERS_TO_GRIDVIEW:
                        Log.i(TAG, "Adding Group Members");
                        ArrayList<String> users = (ArrayList<String>) msg.obj;
                        controller.addMembers(users);
                        break;
                }
            }
        }
    }

    private void addMembers(ArrayList<String> users) {
        JMessageClient.addGroupMembers(mGroupId, users, new BasicCallback() {
            @Override
            public void gotResult(final int status, final String desc) {
                mLoadingDialog.dismiss();
                if (status == 0) {
                    initData();
                    mGridViewAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GroupGridViewActivity.this, "添加失败" + desc, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
