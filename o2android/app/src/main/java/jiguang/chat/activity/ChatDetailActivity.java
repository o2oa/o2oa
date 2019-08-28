package jiguang.chat.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.wugang.activityresult.library.ActivityResult;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactPickerResult;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactPickerResultItem;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.application.JGApplication;
import jiguang.chat.controller.ChatDetailController;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.view.ChatDetailView;

/**
 * Created by ${chenyn} on 2017/4/21.
 */

public class ChatDetailActivity extends BaseActivity {

    private static final String TAG = "ChatDetailActivity";

    private ChatDetailView mChatDetailView;
    private ChatDetailController mChatDetailController;
    private UIHandler mUIHandler = new UIHandler(this);
    public final static String START_FOR_WHICH = "which";
    private final static int GROUP_NAME_REQUEST_CODE = 1;
    private final static int MY_NAME_REQUEST_CODE = 2;

    public static final int GROUP_DESC = 70;
    public static final int FLAGS_GROUP_DESC = 71;
    public static final String GROUP_DESC_KEY = "group_desc_key";

    public static final int GROUP_NAME = 72;
    public static final int FLAGS_GROUP_NAME = 73;
    public static final String GROUP_NAME_KEY = "group_name_key";

    private long groupID;


    private Context mContext;
    private ProgressDialog mDialog;
    private String mGroupName;
    private String mGroupDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);
        mContext = this;
        mChatDetailView = (ChatDetailView) findViewById(R.id.chat_detail_view);
        mChatDetailView.initModule();
        mChatDetailController = new ChatDetailController(mChatDetailView, this, mAvatarSize, mWidth);
        mChatDetailView.setListeners(mChatDetailController);
        mChatDetailView.setOnChangeListener(mChatDetailController);
        mChatDetailView.setItemListener(mChatDetailController);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(JGApplication.CONV_TITLE, mChatDetailController.getName());
        intent.putExtra(JGApplication.MEMBERS_COUNT, mChatDetailController.getCurrentCount());
        intent.putExtra("deleteMsg", mChatDetailController.getDeleteFlag());
        setResult(JGApplication.RESULT_CODE_CHAT_DETAIL, intent);
        finish();
        super.onBackPressed();
    }

    private void dismissSoftInput() {
        //隐藏软键盘
        InputMethodManager imm = ((InputMethodManager) mContext
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (this.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (this.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void updateGroupNameDesc(long groupId, int nameOrDesc) {
        this.groupID = groupId;
        Intent intent = new Intent(ChatDetailActivity.this, NickSignActivity.class);
        if (nameOrDesc == 1) {
            intent.setFlags(FLAGS_GROUP_NAME);
            intent.putExtra("group_name", mGroupName);
        } else {
            intent.setFlags(FLAGS_GROUP_DESC);
            intent.putExtra("group_desc", mGroupDesc);
        }
        startActivityForResult(intent, GROUP_NAME);
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle extras = data.getExtras();
            mDialog = new ProgressDialog(mContext);
            mDialog.setMessage("正在修改");
            switch (resultCode) {
                case Activity.RESULT_CANCELED:

                    break;
                case GROUP_NAME://修改群组名
                    mDialog.show();
                    final String groupName = extras.getString(GROUP_NAME_KEY);
                    if (TextUtils.isEmpty(groupName)) {
                        mDialog.dismiss();
                        ToastUtil.shortToast(mContext, "输入不能是空");
                        break;
                    }
                    JMessageClient.updateGroupName(groupID, groupName, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            mDialog.dismiss();
                            if (responseCode == 0) {
                                mChatDetailView.updateGroupName(groupName);
                                mChatDetailController.refreshGroupName(groupName);
                            } else {
                                ToastUtil.shortToast(mContext, "输入不合法");
                            }
                        }
                    });
                    break;
                case GROUP_DESC://修改群组描述
                    mDialog.show();
                    final String groupDesc = extras.getString(GROUP_DESC_KEY);
                    JMessageClient.updateGroupDescription(groupID, groupDesc, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            mDialog.dismiss();
                            if (responseCode == 0) {
                                mChatDetailView.setGroupDesc(groupDesc);
                            } else {
                                ToastUtil.shortToast(mContext, "输入不合法");
                            }
                        }
                    });
                    break;
            }

            switch (requestCode) {
                case GROUP_NAME_REQUEST_CODE:
                    mChatDetailView.setGroupName(data.getStringExtra("resultName"));
                    break;
                case MY_NAME_REQUEST_CODE:
                    if (data.getBooleanExtra("returnChatActivity", false)) {
                        data.putExtra("deleteMsg", mChatDetailController.getDeleteFlag());
                        data.putExtra(JGApplication.NAME, mChatDetailController.getName());
                        setResult(JGApplication.RESULT_CODE_CHAT_DETAIL, data);
                        finish();
                    }
                    break;
                case JGApplication.REQUEST_CODE_ALL_MEMBER:
                    mChatDetailController.refreshMemberList();
                    break;
                case 4://修改群头像
                    String path = data.getStringExtra("groupAvatarPath");
                    if (path != null) {
                        mChatDetailView.setGroupAvatar(new File(path));
                    }
                    break;
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        mChatDetailController.initData();
        mChatDetailController.isShowMore();
        if (mChatDetailController.getAdapter() != null) {
            mChatDetailController.getAdapter().notifyDataSetChanged();
            mChatDetailController.getNoDisturb();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 从ContactsActivity中选择朋友加入到群组中
     */
    public void showContacts(Long group) {
        ArrayList<String> modes = new ArrayList<>();
        modes.add("personPicker");
        Bundle bundle1 = ContactPickerActivity.Companion.startPickerBundle(modes,
                new ArrayList<>(),
                "",
                0,
                true,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>());
        ActivityResult.of(this)
                .className(ContactPickerActivity.class)
                .params(bundle1)
                .greenChannel().forResult((resultCode, data) -> {
                    if (data != null) {
                        ContactPickerResult result = data.getParcelableExtra(ContactPickerActivity.CONTACT_PICKED_RESULT);
                        if (result != null) {
                            ArrayList<ContactPickerResultItem> users = result.getUsers();
                            if (users.size() != 0) {
                                ArrayList<String> list = new ArrayList<>();
                                for (int i = 0; i < users.size(); i++) {
                                    list.add(users.get(i).getDistinguishedName());
                                }
                                mChatDetailController.addMembersToGroup(list);
                            }
                        }
                    }
                });

    }


    public void startMainActivity() {
        Intent intent = new Intent(this, net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void startChatActivity(long groupID, String groupName, int member) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //设置跳转标志
        intent.putExtra("fromGroup", true);
        intent.putExtra(JGApplication.MEMBERS_COUNT, member + 1);
        intent.putExtra(JGApplication.GROUP_ID, groupID);
        intent.putExtra(JGApplication.CONV_TITLE, groupName);
        intent.setClass(this, ChatActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 接收群成员变化事件
     *
     * @param event 消息事件
     */
    public void onEvent(MessageEvent event) {
        final cn.jpush.im.android.api.model.Message msg = event.getMessage();
        if (msg.getContentType() == ContentType.eventNotification) {
            EventNotificationContent.EventNotificationType msgType = ((EventNotificationContent) msg
                    .getContent()).getEventNotificationType();
            switch (msgType) {
                //添加群成员事件特殊处理
                case group_member_added:
                    List<String> userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                    for (final String userName : userNames) {
                        JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                            @Override
                            public void gotResult(int status, String desc, UserInfo userInfo) {
                                if (status == 0) {
                                    mChatDetailController.getAdapter().notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    break;
                case group_member_removed:
                    break;
                case group_member_exit:
                    break;
            }
            //无论是否添加群成员，刷新界面
            android.os.Message handleMsg = mUIHandler.obtainMessage();
            handleMsg.what = JGApplication.ON_GROUP_EVENT;
            Bundle bundle = new Bundle();
            bundle.putLong(JGApplication.GROUP_ID, ((GroupInfo) msg.getTargetInfo()).getGroupID());
            handleMsg.setData(bundle);
            handleMsg.sendToTarget();
        }
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public void setGroupDesc(String groupDesc) {
        mGroupDesc = groupDesc;
    }

    private static class UIHandler extends Handler {

        private WeakReference<ChatDetailActivity> mActivity;

        public UIHandler(ChatDetailActivity activity) {
            mActivity = new WeakReference<ChatDetailActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatDetailActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case JGApplication.ON_GROUP_EVENT:
                        activity.mChatDetailController.refresh(msg.getData()
                                .getLong(JGApplication.GROUP_ID, 0));
                        break;
                }
            }
        }
    }


}
