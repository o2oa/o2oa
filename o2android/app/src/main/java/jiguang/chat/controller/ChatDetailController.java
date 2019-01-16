package jiguang.chat.controller;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my.MyInfoActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.activity.ChatDetailActivity;
import jiguang.chat.activity.MembersInChatActivity;
import jiguang.chat.activity.historyfile.activity.HistoryFileActivity;
import jiguang.chat.adapter.GroupMemberGridAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.view.ChatDetailView;
import jiguang.chat.view.SlipButton;


public class ChatDetailController implements OnClickListener, OnItemClickListener,
        SlipButton.OnChangedListener {


    private ChatDetailView mChatDetailView;
    private ChatDetailActivity mContext;
    private GroupMemberGridAdapter mGridAdapter;
    //GridView的数据源
    private List<UserInfo> mMemberInfoList = new ArrayList<UserInfo>();
    // 当前GridView群成员项数
    private int mCurrentNum;
    // 空白项的项数
    // 除了群成员Item和添加、删除按钮，剩下的都看成是空白项，
    // 对应的mRestNum[mCurrent%4]的值即为空白项的数目
    private int[] mRestArray = new int[] {2, 1, 0, 3};
    private boolean mIsGroup = false;
    private boolean mIsCreator = false;
    private long mGroupId;
    private String mTargetId;
    private Dialog mLoadingDialog = null;
    private static final int ADD_MEMBERS_TO_GRIDVIEW = 2048;
    private static final int ADD_A_MEMBER_TO_GRIDVIEW = 2049;
    private static final int MAX_GRID_ITEM = 40;
    private String mGroupName;
    private String mGroupDesc;
    private final MyHandler myHandler = new MyHandler(this);
    private Dialog mDialog;
    private boolean mDeleteMsg;
    private int mAvatarSize;
    private String mMyUsername;
    private String mTargetAppKey;
    private int mWidth;
    private GroupInfo mGroupInfo;
    private UserInfo mUserInfo;
    private boolean mShowMore;
    private String mMyNickName;
    private String mNickName;
    private String mAvatarPath;
    private boolean mFriend;
    private Long mUid;

    public ChatDetailController(ChatDetailView chatDetailView, ChatDetailActivity context, int size,
                                int width) {
        this.mChatDetailView = chatDetailView;
        this.mContext = context;
        this.mAvatarSize = size;
        this.mWidth = width;
        initData();
    }

    /*
     * 获得群组信息，初始化群组界面，先从本地读取，如果没有再从服务器读取
     */

    public void initData() {
        Intent intent = mContext.getIntent();
        mGroupId = intent.getLongExtra(JGApplication.GROUP_ID, 0);
        mTargetId = intent.getStringExtra(JGApplication.TARGET_ID);
        mTargetAppKey = intent.getStringExtra(JGApplication.TARGET_APP_KEY);
        UserInfo myInfo = JMessageClient.getMyInfo();
        mMyNickName = myInfo.getNickname();
        mMyUsername = myInfo.getUserName();
        // 是群组
        if (mGroupId != 0) {
            mChatDetailView.setTitle("群组信息");
            mIsGroup = true;
            //获得群组基本信息：群主ID、群组名、群组人数
            Conversation conv = JMessageClient.getGroupConversation(mGroupId);
            mGroupInfo = (GroupInfo) conv.getTargetInfo();
            mChatDetailView.initNoDisturb(mGroupInfo.getNoDisturb());
            mMemberInfoList = mGroupInfo.getGroupMembers();
            String groupOwnerId = mGroupInfo.getGroupOwner();
            mGroupName = mGroupInfo.getGroupName();
            mGroupDesc = mGroupInfo.getGroupDescription();
            if (mGroupInfo.getAvatarFile() != null && mGroupInfo.getAvatarFile().exists()) {
                mChatDetailView.setGroupAvatar(mGroupInfo.getAvatarFile());
            }

            if (TextUtils.isEmpty(mGroupName)) {
                mChatDetailView.setGroupName(mContext.getString(R.string.unnamed));
            } else {
                mChatDetailView.setGroupName(mGroupName);
                mContext.setGroupName(mGroupName);
            }
            if (TextUtils.isEmpty(mGroupDesc)) {
                mChatDetailView.setGroupDesc(mContext.getString(R.string.undesc));
            } else {
                mChatDetailView.setGroupDesc(mGroupDesc);
                mContext.setGroupDesc(mGroupDesc);
            }

            // 判断是否为群主
            if (groupOwnerId != null && groupOwnerId.equals(mMyUsername)) {
                mIsCreator = true;
            }
            mChatDetailView.setMyName(mMyUsername);
            mChatDetailView.showBlockView(mGroupInfo.isGroupBlocked());
            initAdapter();
            if (mGridAdapter != null) {
                mGridAdapter.setCreator(mIsCreator);
            }

            //群聊才有点击显示更多
            if (mMemberInfoList.size() > 13) {
                mChatDetailView.isLoadMoreShow(true);
            } else {
                mChatDetailView.isLoadMoreShow(false);
            }

            // 是单聊
        } else {
            mChatDetailView.setTitle("聊天设置");
            Conversation conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            mUserInfo = (UserInfo) conv.getTargetInfo();
            mChatDetailView.initNoDisturb(mUserInfo.getNoDisturb());
            mCurrentNum = 1;
            mGridAdapter = new GroupMemberGridAdapter(mContext, mTargetId, mTargetAppKey);
            mChatDetailView.setAdapter(mGridAdapter);
            // 设置单聊界面
            mChatDetailView.setSingleView(mUserInfo.isFriend());
            mChatDetailView.dismissAllMembersBtn();
            mChatDetailView.isLoadMoreShow(false);

            JMessageClient.getUserInfo(mTargetId, new GetUserInfoCallback() {

                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        mFriend = userInfo.isFriend();
                        mNickName = userInfo.getNickname();
                        mUid = userInfo.getUserID();
                        if (TextUtils.isEmpty(mNickName)) {
                            mNickName = mTargetId;
                        }
                        File avatarFile = userInfo.getAvatarFile();
                        if (avatarFile != null) {
                            mAvatarPath = avatarFile.getAbsolutePath();
                        }
                    }
                }
            });
        }
    }


    private void initAdapter() {
        // 初始化头像
        mGridAdapter = new GroupMemberGridAdapter(mContext, mMemberInfoList, mIsCreator, mAvatarSize);
        if (mMemberInfoList.size() > MAX_GRID_ITEM) {
            mCurrentNum = MAX_GRID_ITEM - 1;
        } else {
            mCurrentNum = mMemberInfoList.size();
        }
        mChatDetailView.setAdapter(mGridAdapter);
        mChatDetailView.getGridView().setFocusable(false);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.return_btn:
                intent.putExtra("deleteMsg", mDeleteMsg);
                intent.putExtra(JGApplication.CONV_TITLE, getName());
                intent.putExtra(JGApplication.MEMBERS_COUNT, mMemberInfoList.size());
                mContext.setResult(JGApplication.RESULT_CODE_CHAT_DETAIL, intent);
                mContext.finish();
                break;

            // 设置群组名称
            case R.id.group_name_ll:
                mContext.updateGroupNameDesc(mGroupId, 1);
                break;
            case R.id.group_desc_ll:
                mContext.updateGroupNameDesc(mGroupId, 2);
                break;
            case R.id.rl_groupAvatar:
//                intent.setClass(mContext, GroupAvatarActivity.class);
//                intent.putExtra("groupID",mGroupId);
//                if(mGroupInfo.getBigAvatarFile() != null && mGroupInfo.getBigAvatarFile().exists()) {
//                    intent.putExtra("groupAvatar", mGroupInfo.getBigAvatarFile().getAbsolutePath());
//                }
//                mContext.startActivityForResult(intent,4);
                break;
            // 删除聊天记录
            case R.id.group_chat_del_ll:
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.cancel();
                                break;
                            case R.id.jmui_commit_btn:
                                Conversation conv;
                                if (mIsGroup) {
                                    conv = JMessageClient.getGroupConversation(mGroupId);
                                } else {
                                    conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
                                }
                                if (conv != null) {
                                    conv.deleteAllMessage();
                                    mDeleteMsg = true;
                                }
                                ToastUtil.shortToast(mContext, "清空成功");
                                mDialog.cancel();
                                break;
                        }
                    }
                };
                mDialog = DialogCreator.createDeleteMessageDialog(mContext, listener);
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            //删除好友或群组
            case R.id.chat_detail_del_group:
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.cancel();
                                break;
                            case R.id.jmui_commit_btn:
                                deleteAndExit();
                                mDialog.cancel();
                                break;
                        }
                    }
                };
                if (mIsGroup) {
                    mDialog = DialogCreator.createExitGroupDialog(mContext, listener);
                } else {
                    mDialog = DialogCreator.createBaseDialogWithTitle(mContext,
                            mContext.getString(R.string.delete_friend_dialog_title), listener);
                }
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            case R.id.tv_moreGroup:
//                intent.setClass(mContext, GroupGridViewActivity.class);
//                intent.putExtra(JGApplication.GROUP_ID, mGroupId);
//                intent.putExtra(JGApplication.DELETE_MODE, false);
//                mContext.startActivityForResult(intent, JGApplication.REQUEST_CODE_ALL_MEMBER);
                break;
            case R.id.chat_detail_add_friend:
//                if (mUserInfo.isFriend()) {
//                    ToastUtil.shortToast(mContext, "对方已经是你的好友");
//                } else {
//                    intent.setClass(mContext, VerificationActivity.class);
//                    //对方信息
//                    intent.putExtra("detail_add_nick_name", mNickName);
//                    intent.putExtra("detail_add_avatar_path", mAvatarPath);
//                    intent.putExtra("detail_add_friend", mTargetId);
//                    intent.putExtra("detail_add_uid", mUid);
//                    //自己的昵称.
//                    intent.putExtra("detail_add_friend_my_nickname", mMyNickName);
//                    intent.setFlags(1);
//                    mContext.startActivity(intent);
//                }
                break;
            case R.id.clear_rl:
                final Dialog clear = new Dialog(mContext, R.style.jmui_default_dialog_style);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View view = inflater.inflate(R.layout.dialog_clear, null);
                clear.setContentView(view);
                Window window = clear.getWindow();
                window.setWindowAnimations(R.style.mystyle); // 添加动画
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                clear.show();
                clear.setCanceledOnTouchOutside(true);
                Button delete = (Button) view.findViewById(R.id.btn_del);
                Button cancel = (Button) view.findViewById(R.id.btn_cancel);
                View.OnClickListener listen = new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.btn_del:
                                if (mIsGroup) {
                                    Conversation singleConversation = JMessageClient.getGroupConversation(mGroupId);
                                    List<cn.jpush.im.android.api.model.Message> allMessage = singleConversation.getAllMessage();
                                    for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                                        MessageContent content = msg.getContent();
                                        if (content.getContentType() == ContentType.image) {
                                            ImageContent imageContent = (ImageContent) content;
                                            String localPath = imageContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File imageFile = new File(localPath);
                                                if (imageFile.exists()) {
                                                    imageFile.delete();
                                                }
                                            }
                                        } else if (content.getContentType() == ContentType.file) {
                                            FileContent fileContent = (FileContent) content;
                                            String localPath = fileContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File file = new File(localPath);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.shortToast(mContext, "清理成功");
                                } else {
                                    Conversation singleConversation = JMessageClient.getSingleConversation(mTargetId);
                                    List<cn.jpush.im.android.api.model.Message> allMessage = singleConversation.getAllMessage();
                                    for (cn.jpush.im.android.api.model.Message msg : allMessage) {
                                        MessageContent content = msg.getContent();
                                        if (content.getContentType() == ContentType.image) {
                                            ImageContent imageContent = (ImageContent) content;
                                            String localPath = imageContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File imageFile = new File(localPath);
                                                if (imageFile.exists()) {
                                                    imageFile.delete();
                                                }
                                            }
                                        } else if (content.getContentType() == ContentType.file) {
                                            FileContent fileContent = (FileContent) content;
                                            String localPath = fileContent.getLocalPath();
                                            if (!TextUtils.isEmpty(localPath)) {
                                                File file = new File(localPath);
                                                if (file.exists()) {
                                                    boolean delete1 = file.delete();
                                                    File copyFile = new File(JGApplication.FILE_DIR + fileContent.getFileName());
                                                    boolean delete2 = copyFile.delete();
                                                }
                                            }
                                        }
                                    }
                                    ToastUtil.shortToast(mContext, "清理成功");
                                }
                                clear.dismiss();
                                break;
                            case R.id.btn_cancel:
                                clear.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                };
                delete.setOnClickListener(listen);
                cancel.setOnClickListener(listen);
                break;
            case R.id.chat_file:
                intent = new Intent(mContext, HistoryFileActivity.class);
                intent.putExtra("userName", mTargetId);
                intent.putExtra("groupId", mGroupId);
                intent.putExtra("isGroup", mIsGroup);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.trans_in, R.anim.trans_out);
                break;
        }
    }

    /**
     * 删除并退出
     */
    private void deleteAndExit() {
        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                mContext.getString(R.string.exiting_group_toast));
        mLoadingDialog.show();
        if (mIsGroup) {
            JMessageClient.exitGroup(mGroupId, new BasicCallback() {
                @Override
                public void gotResult(final int status, final String desc) {
                    if (mLoadingDialog != null)
                        mLoadingDialog.dismiss();
                    if (status == 0) {
                        Conversation conv = JMessageClient.getGroupConversation(mGroupId);
                        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                                .setConversation(conv)
                                .build());
                        JMessageClient.deleteGroupConversation(mGroupId);
                        mContext.startMainActivity();
                    } else {
                        ToastUtil.shortToast(mContext, "退出失败");
                    }
                }
            });
        } else {
            mUserInfo.removeFromFriendList(new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage) {
                    mLoadingDialog.dismiss();
                    if (responseCode == 0) {
                        //删除好友时候要从list中移除.准备接收下一次添加好友申请
                        JGApplication.forAddFriend.remove(mUserInfo.getUserName());
                        //将好友删除时候还原黑名单设置
                        List<String> name = new ArrayList<>();
                        name.add(mUserInfo.getUserName());
                        JMessageClient.delUsersFromBlacklist(name, null);


                        ToastUtil.shortToast(mContext, "移除好友");
                        delConvAndReturnMainActivity();
                    } else {
                        ToastUtil.shortToast(mContext, "移除失败");
                    }
                }
            });
        }
    }

    public void delConvAndReturnMainActivity() {
        Conversation conversation = JMessageClient.getSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                .setConversation(conversation)
                .build());
        JMessageClient.deleteSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
        mContext.startMainActivity();
    }

    // GridView点击事件
    @Override
    public void onItemClick(AdapterView<?> viewAdapter, View view, final int position, long id) {
        //群聊
        if (mIsGroup) {
            // 点击群成员项时
            if (position < mCurrentNum) {
                if (mMemberInfoList.get(position).getUserName().equals(mMyUsername)) {
                    Intent my = new Intent(mContext, MyInfoActivity.class);
                    mContext.startActivity(my);
                } else {
                    UserInfo userInfo = mMemberInfoList.get(position);
                    Intent person = new Intent(mContext, PersonActivity.class);
                    person.putExtra(PersonActivity.Companion.getPERSON_NAME_KEY(), userInfo.getUserName());
                    mContext.startActivity(person);
                }

                // 点击添加成员按钮
            } else if (position == mCurrentNum) {
                mContext.showContacts(mGroupId);
                // 是群主, 成员个数大于1并点击删除按钮
            } else if (position == mCurrentNum + 1 && mIsCreator && mCurrentNum > 1) {
                Intent intent = new Intent();
                intent.putExtra(JGApplication.DELETE_MODE, true);
                intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                intent.setClass(mContext, MembersInChatActivity.class);
                mContext.startActivityForResult(intent, JGApplication.REQUEST_CODE_ALL_MEMBER);
            }
            //单聊
        } else if (position < mCurrentNum) {
            //会话中点击右上角进入拉人进群界面,点击add按钮之前的user头像.
//            if (mFriend) {
//                intent.setClass(mContext, FriendInfoActivity.class);
//            } else {
//                intent.setClass(mContext, GroupNotFriendActivity.class);
//            }
//            intent.putExtra(JGApplication.TARGET_ID, mTargetId);
//            intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);
//            mContext.startActivityForResult(intent, JGApplication.REQUEST_CODE_FRIEND_INFO);
        } else if (position == mCurrentNum) {
//            mContext.showContacts(0l);
        }

    }

    public void addMembersToGroup(ArrayList<String> users) {
        ArrayList<String> list = new ArrayList<String>();
        for (String username : users) {
            if (checkIfNotContainUser(username)) {
                list.add(username);
            }
        }
        if (list.size() > 0) {
            mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                    mContext.getString(R.string.adding_hint));
            mLoadingDialog.show();
            android.os.Message msg = myHandler.obtainMessage();
            msg.what = ADD_MEMBERS_TO_GRIDVIEW;
            msg.obj = list;
            msg.sendToTarget();
        }
    }


    /**
     * 添加成员时检查是否存在该群成员
     *
     * @param targetID 要添加的用户
     * @return 返回是否存在该用户
     */
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

    /**
     * @param userInfo 要增加的成员的用户名，目前一次只能增加一个
     */
    private void addAMember(final UserInfo userInfo) {
        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                mContext.getString(R.string.adding_hint));
        mLoadingDialog.show();
        ArrayList<String> list = new ArrayList<String>();
        list.add(userInfo.getUserName());
        JMessageClient.addGroupMembers(mGroupId, list, new BasicCallback() {

            @Override
            public void gotResult(final int status, final String desc) {
                mLoadingDialog.dismiss();
                if (status == 0) {
                    refreshMemberList();
                } else {
                    ToastUtil.shortToast(mContext, "添加失败");
                }
            }
        });
    }

    private void addMembers(final ArrayList<String> users) {
        JMessageClient.addGroupMembers(mGroupId, users, new BasicCallback() {

            @Override
            public void gotResult(final int status, final String desc) {
                XLog.info("is in getGroupInfo...................."+status+", "+desc);
                mLoadingDialog.dismiss();
                if (status == 0) {
                    XLog.info("is in addGroupmembers....................");
//                    refreshMemberList();
                    mContext.refresh();
                } else {
                    ToastUtil.shortToast(mContext, "添加失败");
                }
            }
        });
    }

    //添加或者删除成员后重新获得MemberInfoList
    public void refreshMemberList() {
        mCurrentNum = mMemberInfoList.size() > MAX_GRID_ITEM ? MAX_GRID_ITEM - 1 : mMemberInfoList.size();
        XLog.info("is in addGroupmembers...................."+mMemberInfoList.size());
        mGridAdapter.refreshMemberList();
    }

    public void refreshGroupName(String newName) {
        mGroupName = newName;
    }

    @Override
    public void onChanged(int id, final boolean checked) {
        switch (id) {
            case R.id.no_disturb_slip_btn:
                final Dialog dialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.processing));
                dialog.show();
                //设置免打扰,1为将当前用户或群聊设为免打扰,0为移除免打扰
                if (mIsGroup) {
                    mGroupInfo.setNoDisturb(checked ? 1 : 0, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            dialog.dismiss();
                            if (status == 0) {
                                if (checked) {
                                    Toast.makeText(mContext, mContext.getString(R.string.set_do_not_disturb_success_hint),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.remove_from_no_disturb_list_hint),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //设置失败,恢复为原来的状态
                            } else {
                                if (checked) {
                                    mChatDetailView.setNoDisturbChecked(false);
                                } else {
                                    mChatDetailView.setNoDisturbChecked(true);
                                }
                            }
                        }
                    });
                } else {
                    mUserInfo.setNoDisturb(checked ? 1 : 0, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            dialog.dismiss();
                            if (status == 0) {
                                if (checked) {
                                    Toast.makeText(mContext, mContext.getString(R.string.set_do_not_disturb_success_hint),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.remove_from_no_disturb_list_hint),
                                            Toast.LENGTH_SHORT).show();
                                }
                                //设置失败,恢复为原来的状态
                            } else {
                                if (checked) {
                                    mChatDetailView.setNoDisturbChecked(false);
                                } else {
                                    mChatDetailView.setNoDisturbChecked(true);
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.block_slip_btn:
                mDialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.processing));
                mDialog.show();
                mGroupInfo.setBlockGroupMessage(checked ? 1 : 0, new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        mDialog.dismiss();
                        if (status == 0) {
                            if (checked) {
                                Toast.makeText(mContext, mContext.getString(R.string
                                        .set_block_succeed_hint), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string
                                        .remove_block_succeed_hint), Toast.LENGTH_SHORT).show();
                            }
                        } else {
//                            if (checked) {
//                                mChatDetailView.setBlockChecked(false);
//                            } else {
//                                mChatDetailView.setBlockChecked(true);
//                            }
                        }
                    }
                });
                break;
        }
    }

    public void getNoDisturb() {
        if (mUserInfo != null) {
            ChatDetailView.mNoDisturbBtn.setChecked(mUserInfo.getNoDisturb() == 1);
        }

    }

    public void isShowMore() {
        JMessageClient.getGroupInfo(mGroupId, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, GroupInfo groupInfo) {
                if (responseCode == 0) {
                    List<UserInfo> groupMembers = groupInfo.getGroupMembers();
                    if (mIsCreator) {
                        if (groupMembers.size() > 13) {
                            mChatDetailView.isLoadMoreShow(true);
                        } else {
                            mChatDetailView.isLoadMoreShow(false);
                        }
                    } else {
                        if (groupMembers.size() > 14) {
                            mChatDetailView.isLoadMoreShow(true);
                        } else {
                            mChatDetailView.isLoadMoreShow(false);
                        }
                    }
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<ChatDetailController> mController;

        public MyHandler(ChatDetailController controller) {
            mController = new WeakReference<ChatDetailController>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ChatDetailController controller = mController.get();
            if (controller != null) {
                switch (msg.what) {
                    //无好友模式一次添加一个
                    case ADD_A_MEMBER_TO_GRIDVIEW:
                        if (controller.mLoadingDialog != null) {
                            controller.mLoadingDialog.dismiss();
                        }
                        final UserInfo userInfo = (UserInfo) msg.obj;
                        if (controller.mIsGroup) {
                            controller.addAMember(userInfo);
                            //在单聊中点击加人按钮并且用户信息返回正确,如果为第三方则创建群聊
                        } else {
                            if (userInfo.getUserName().equals(controller.mMyUsername)
                                    || userInfo.getUserName().equals(controller.mTargetId)) {
                                return;
                            } else {
                                controller.addMemberAndCreateGroup(userInfo.getUserName());
                            }
                        }
                        break;
                    //好友模式从通讯录中添加好友
                    case ADD_MEMBERS_TO_GRIDVIEW:
                        ArrayList<String> users = (ArrayList<String>) msg.obj;
                        if (controller.mIsGroup) {
                            controller.addMembers(users);
                            //在单聊中点击加人按钮并且用户信息返回正确,如果为第三方则创建群聊
                        } else {
                            if (controller.mLoadingDialog != null) {
                                controller.mLoadingDialog.dismiss();
                            }
                            controller.addMembersAndCreateGroup(users);
                        }
                        break;
                }
            }
        }
    }

    private void addMemberAndCreateGroup(final String newMember) {
        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                mContext.getString(R.string.creating_hint));
        mLoadingDialog.show();
        JMessageClient.createGroup("", "", new CreateGroupCallback() {
            @Override
            public void gotResult(int status, final String desc, final long groupId) {
                if (status == 0) {
                    final ArrayList<String> list = new ArrayList<String>();
                    list.add(mTargetId);
                    list.add(newMember);
                    JMessageClient.addGroupMembers(groupId, list, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            if (status == 0) {
                                Conversation conv = Conversation.createGroupConversation(groupId);
                                EventBus.getDefault().post(new Event.Builder().setType(EventType.createConversation)
                                        .setConversation(conv).build());
                                mContext.startChatActivity(groupId, conv.getTitle(), list.size());
                            } else {
                                ToastUtil.shortToast(mContext, "创建群组时添加成员失败");
                            }
                        }
                    });
                } else {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtil.shortToast(mContext, "创建群组失败");
                }
            }
        });
    }

    private void addMembersAndCreateGroup(final ArrayList<String> list) {
        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                mContext.getString(R.string.creating_hint));
        mLoadingDialog.show();
        JMessageClient.createGroup("", "", new CreateGroupCallback() {
            @Override
            public void gotResult(int status, final String desc, final long groupId) {
                if (status == 0) {
                    JMessageClient.addGroupMembers(groupId, list, new BasicCallback() {
                        @Override
                        public void gotResult(int status, String desc) {
                            if (mLoadingDialog != null) {
                                mLoadingDialog.dismiss();
                            }
                            if (status == 0) {
                                Conversation conv = Conversation.createGroupConversation(groupId);
                                EventBus.getDefault().post(new Event.Builder().setType(EventType.createConversation)
                                        .setConversation(conv).build());

                                mContext.startChatActivity(groupId, conv.getTitle(), list.size());
                            } else {
                                ToastUtil.shortToast(mContext, "创建群组时添加成员失败");
                            }
                        }
                    });
                } else {
                    if (mLoadingDialog != null) {
                        mLoadingDialog.dismiss();
                    }
                    ToastUtil.shortToast(mContext, "创建群组失败");
                }
            }
        });
    }

    public String getName() {
        if (mIsGroup) {
            if (TextUtils.isEmpty(mGroupName)) {
                Conversation groupConversation = JMessageClient.getGroupConversation(mGroupId);
                mGroupName = groupConversation.getTitle();
            }
            return mGroupName;
        } else {
            Conversation conv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            return conv.getTitle();
        }
    }

    public int getCurrentCount() {
        return mMemberInfoList.size();
    }

    public boolean getDeleteFlag() {
        return mDeleteMsg;
    }

    public GroupMemberGridAdapter getAdapter() {
        return mGridAdapter;
    }

    /**
     * 当收到群成员变化的Event后，刷新成员列表
     *
     * @param groupId 群组Id
     */
    public void refresh(long groupId) {
        //当前群聊
        if (mGroupId == groupId) {
            refreshMemberList();
        }
    }

}
