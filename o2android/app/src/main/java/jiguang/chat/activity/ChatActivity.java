package jiguang.chat.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Toast;


import net.muliba.fancyfilepickerlibrary.PicturePicker;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.MessageReceiptStatusChangeEvent;
import cn.jpush.im.android.api.event.MessageRetractEvent;
import cn.jpush.im.android.api.event.OfflineMessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.ChattingListAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.location.activity.MapPickerActivity;
import jiguang.chat.model.Constants;
import jiguang.chat.pickerimage.model.PhotoInfo;
import jiguang.chat.pickerimage.utils.RequestCode;
import jiguang.chat.pickerimage.utils.SendImageHelper;
import jiguang.chat.pickerimage.utils.StorageType;
import jiguang.chat.pickerimage.utils.StorageUtil;
import jiguang.chat.utils.IdHelper;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.SimpleCommonUtils;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.event.ImageEvent;
import jiguang.chat.utils.imagepicker.bean.ImageItem;
import jiguang.chat.utils.keyboard.XhsEmoticonsKeyBoard;
import jiguang.chat.utils.keyboard.data.EmoticonEntity;
import jiguang.chat.utils.keyboard.interfaces.EmoticonClickListener;
import jiguang.chat.utils.keyboard.utils.EmoticonsKeyboardUtils;
import jiguang.chat.utils.keyboard.widget.EmoticonsEditText;
import jiguang.chat.utils.keyboard.widget.FuncLayout;
import jiguang.chat.view.ChatView;
import jiguang.chat.view.SimpleAppsGridView;
import jiguang.chat.view.TipItem;
import jiguang.chat.view.TipView;
import jiguang.chat.view.listview.DropDownListView;
import rx.functions.Action1;


/**
 * Created by ${chenyn} on 2017/3/26.
 */

public class ChatActivity extends BaseActivity implements FuncLayout.OnFuncKeyBoardListener, View.OnClickListener {
    DropDownListView lvChat;
    XhsEmoticonsKeyBoard ekBar;
    public static final String JPG = ".jpg";
    private static String MsgIDs = "msgIDs";

    private String mTitle;
    private boolean mLongClick = false;

    private static final String MEMBERS_COUNT = "membersCount";
    private static final String GROUP_NAME = "groupName";

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_APP_KEY = "targetAppKey";
    private static final String DRAFT = "draft";
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    public static final int REQUEST_CODE_SELECT = 100;
    private ChatView mChatView;
    private boolean mIsSingle = true;
    private Conversation mConv;
    private String mTargetId;
    private String mTargetAppKey;
    private Activity mContext;
    private ChattingListAdapter mChatAdapter;
    int maxImgCount = 9;
    private List<UserInfo> mAtList;
    private long mGroupId;
    private static final int REFRESH_LAST_PAGE = 0x1023;
    private static final int REFRESH_CHAT_TITLE = 0x1024;
    private static final int REFRESH_GROUP_NAME = 0x1025;
    private static final int REFRESH_GROUP_NUM = 0x1026;
    private Dialog mDialog;

    private GroupInfo mGroupInfo;
    private UserInfo mMyInfo;
    private static final String GROUP_ID = "groupId";
    private int mAtMsgId;
    private int mAtAllMsgId;
    private int mUnreadMsgCnt;
    private boolean mShowSoftInput = false;
    private List<UserInfo> forDel = new ArrayList<>();

    Window mWindow;
    InputMethodManager mImm;
    private final UIHandler mUIHandler = new UIHandler(this);
    private boolean mAtAll = false;
    private Uri cameraImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_chat);
        // 沉浸式状态栏
        ImmersedStatusBarUtils.INSTANCE.setImmersedStatusBar(this);
        mChatView = (ChatView) findViewById(R.id.chat_view);
        mChatView.initModule(mDensity, mDensityDpi);

        this.mWindow = getWindow();
        this.mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mChatView.setListeners(this);

        lvChat = (DropDownListView)findViewById(R.id.lv_chat);
        ekBar = (XhsEmoticonsKeyBoard)findViewById(R.id.ek_bar);

        initData();
        initView();

    }

    private void initData() {
        SimpleCommonUtils.initEmoticonsEditText(ekBar.getEtChat());
        Intent intent = getIntent();
        mTargetId = intent.getStringExtra(TARGET_ID);
        mTargetAppKey = intent.getStringExtra(TARGET_APP_KEY);
        mTitle = intent.getStringExtra(JGApplication.CONV_TITLE);
        mMyInfo = JMessageClient.getMyInfo();
        if (!TextUtils.isEmpty(mTargetId)) {
            //单聊
            mIsSingle = true;
            mChatView.setChatTitle(mTitle);
            mConv = JMessageClient.getSingleConversation(mTargetId, mTargetAppKey);
            if (mConv == null) {
                mConv = Conversation.createSingleConversation(mTargetId, mTargetAppKey);
            }
            mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);
        } else {
            //群聊
            mIsSingle = false;
            mGroupId = intent.getLongExtra(GROUP_ID, 0);
            final boolean fromGroup = intent.getBooleanExtra("fromGroup", false);
            if (fromGroup) {
                mChatView.setChatTitle(mTitle, intent.getIntExtra(MEMBERS_COUNT, 0));
                mConv = JMessageClient.getGroupConversation(mGroupId);
                mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);//长按聊天内容监听
            } else {
                mAtMsgId = intent.getIntExtra("atMsgId", -1);
                mAtAllMsgId = intent.getIntExtra("atAllMsgId", -1);
                mConv = JMessageClient.getGroupConversation(mGroupId);
                if (mConv != null) {
                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                    UserInfo userInfo = groupInfo.getGroupMemberInfo(mMyInfo.getUserName(), mMyInfo.getAppKey());
                    //如果自己在群聊中，聊天标题显示群人数
                    if (userInfo != null) {
                        if (!TextUtils.isEmpty(groupInfo.getGroupName())) {
                            mChatView.setChatTitle(mTitle, groupInfo.getGroupMembers().size());
                        } else {
                            mChatView.setChatTitle(mTitle, groupInfo.getGroupMembers().size());
                        }
                        mChatView.showRightBtn();
                    } else {
                        if (!TextUtils.isEmpty(mTitle)) {
                            mChatView.setChatTitle(mTitle);
                        } else {
                            mChatView.setChatTitle(R.string.group);
                        }
                        mChatView.dismissRightBtn();
                    }
                } else {
                    mConv = Conversation.createGroupConversation(mGroupId);
                }
                //更新群名
                JMessageClient.getGroupInfo(mGroupId, new GetGroupInfoCallback(false) {
                    @Override
                    public void gotResult(int status, String desc, GroupInfo groupInfo) {
                        if (status == 0) {
                            mGroupInfo = groupInfo;
                            mUIHandler.sendEmptyMessage(REFRESH_CHAT_TITLE);
                        }
                    }
                });
                if (mAtMsgId != -1) {
                    mUnreadMsgCnt = mConv.getUnReadMsgCnt();
                    // 如果 @我 的消息位于屏幕显示的消息之上，显示 有人@我 的按钮
                    if (mAtMsgId + 8 <= mConv.getLatestMessage().getId()) {
                        mChatView.showAtMeButton();
                    }
                    mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener, mAtMsgId);
                } else {
                    mChatAdapter = new ChattingListAdapter(mContext, mConv, longClickListener);
                }

            }
            //聊天信息标志改变
            mChatView.setGroupIcon();
        }

        String draft = intent.getStringExtra(DRAFT);
        if (draft != null && !TextUtils.isEmpty(draft)) {
            ekBar.getEtChat().setText(draft);
        }

        mChatView.setChatListAdapter(mChatAdapter);
//        mChatAdapter.initMediaPlayer();
        mChatView.getListView().setOnDropDownListener(new DropDownListView.OnDropDownListener() {
            @Override
            public void onDropDown() {
                mUIHandler.sendEmptyMessageDelayed(REFRESH_LAST_PAGE, 1000);
            }
        });
        mChatView.setToBottom();
        mChatView.setConversation(mConv);

    }

    private void initView() {
        initEmoticonsKeyBoardBar();
        initListView();

        ekBar.getEtChat().addTextChangedListener(new TextWatcher() {
            private CharSequence temp = "";

            @Override
            public void afterTextChanged(Editable arg0) {
                if (temp.length() > 0) {
                    mLongClick = false;
                }

                if (mAtList != null && mAtList.size() > 0) {
                    for (UserInfo info : mAtList) {
                        String name = info.getDisplayName();

                        if (!arg0.toString().contains("@" + name + " ")) {
                            forDel.add(info);
                        }
                    }
                    mAtList.removeAll(forDel);
                }

                if (!arg0.toString().contains("@所有成员 ")) {
                    mAtAll = false;
                }

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
                if (s.length() > 0 && after >= 1 && s.subSequence(start, start + 1).charAt(0) == '@' && !mLongClick) {
                    if (null != mConv && mConv.getType() == ConversationType.group) {
                        ChooseAtMemberActivity.show(ChatActivity.this, ekBar.getEtChat(), mConv.getTargetId());
                    }
                }
            }
        });
    }

    private void initEmoticonsKeyBoardBar() {
        ekBar.setAdapter(SimpleCommonUtils.getCommonAdapter(this, emoticonClickListener));
        ekBar.addOnFuncKeyBoardListener(this);
        SimpleAppsGridView gridView = new SimpleAppsGridView(this);
        ekBar.addFuncView(gridView);

        ekBar.getEtChat().setOnSizeChangedListener(new EmoticonsEditText.OnSizeChangedListener() {
            @Override
            public void onSizeChanged(int w, int h, int oldw, int oldh) {
                scrollToBottom();
            }
        });
        //发送按钮
        ekBar.getBtnSend().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcgContent = ekBar.getEtChat().getText().toString();
                scrollToBottom();
                if (mcgContent.equals("")) {
                    return;
                }
                Message msg;
                TextContent content = new TextContent(mcgContent);
                if (mAtAll) {
                    msg = mConv.createSendMessageAtAllMember(content, null);
                    mAtAll = false;
                } else if (null != mAtList) {
                    msg = mConv.createSendMessage(content, mAtList, null);
                } else {
                    msg = mConv.createSendMessage(content);
                }
                //设置需要已读回执
                MessageSendingOptions options = new MessageSendingOptions();
                options.setNeedReadReceipt(false);
                JMessageClient.sendMessage(msg, options);
                mChatAdapter.addMsgFromReceiptToList(msg);
                ekBar.getEtChat().setText("");
                if (mAtList != null) {
                    mAtList.clear();
                }
                if (forDel != null) {
                    forDel.clear();
                }
            }
        });
        //切换语音输入
        ekBar.getVoiceOrText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_voice_or_text) {
                    ekBar.setVideoText();
                    ekBar.getBtnVoice().initConv(mConv, mChatAdapter, mChatView);
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.jmui_return_btn) {
            returnBtn();
        } else if (i == R.id.jmui_right_btn) {
            startChatDetailActivity(mTargetId, mTargetAppKey, mGroupId);

        } else if (i == R.id.jmui_at_me_btn) {
            if (mUnreadMsgCnt < ChattingListAdapter.PAGE_MESSAGE_COUNT) {
                int position = ChattingListAdapter.PAGE_MESSAGE_COUNT + mAtMsgId - mConv.getLatestMessage().getId();
                mChatView.setToPosition(position);
            } else {
                mChatView.setToPosition(mAtMsgId + mUnreadMsgCnt - mConv.getLatestMessage().getId());
            }

        } else {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnBtn();
    }

    private void returnBtn() {
        mConv.resetUnreadCount();
        dismissSoftInput();
        JMessageClient.exitConversation();
        //发送保存为草稿事件到会话列表
        EventBus.getDefault().post(new Event.Builder().setType(EventType.draft)
                .setConversation(mConv)
                .setDraft(ekBar.getEtChat().getText().toString())
                .build());
        finish();
    }

    private void dismissSoftInput() {
        if (mShowSoftInput) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(ekBar.getEtChat().getWindowToken(), 0);
                mShowSoftInput = false;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startChatDetailActivity(String targetId, String appKey, long groupId) {
        Log.i("ChatActivity", "stargetId:" + targetId + ", appkey:" + appKey + ", groupid:" + groupId);
        if (groupId!=0) {
            Intent intent = new Intent();
            intent.putExtra(TARGET_ID, targetId);
            intent.putExtra(TARGET_APP_KEY, appKey);
            intent.putExtra(GROUP_ID, groupId);
            intent.setClass(this, ChatDetailActivity.class);
            startActivityForResult(intent, JGApplication.REQUEST_CODE_CHAT_DETAIL);
        }else {//个人
            Intent intent = new Intent(this, PersonActivity.class);
            intent.putExtra(PersonActivity.Companion.getPERSON_NAME_KEY(), targetId);
            startActivity(intent);
        }
    }

    EmoticonClickListener emoticonClickListener = new EmoticonClickListener() {
        @Override
        public void onEmoticonClick(Object o, int actionType, boolean isDelBtn) {

            if (isDelBtn) {
                SimpleCommonUtils.delClick(ekBar.getEtChat());
            } else {
                if (o == null) {
                    return;
                }
                if (actionType == Constants.EMOTICON_CLICK_BIGIMAGE) {
                    if (o instanceof EmoticonEntity) {
                        OnSendImage(((EmoticonEntity) o).getIconUri());
                    }
                } else {
                    String content = null;
//                    Emoji
//                    EmojiDisplay.
//                    if (o instanceof EmojiBean) {
//                        content = ((EmojiBean) o).emoji;
//                    } else
                        if (o instanceof EmoticonEntity) {
                        content = ((EmoticonEntity) o).getContent();
                    }

                    if (TextUtils.isEmpty(content)) {
                        return;
                    }
                    int index = ekBar.getEtChat().getSelectionStart();
                    Editable editable = ekBar.getEtChat().getText();
                    editable.insert(index, content);
                }
            }
        }
    };


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (EmoticonsKeyboardUtils.isFullScreen(this)) {
            boolean isConsum = ekBar.dispatchKeyEventInFullScreen(event);
            return isConsum ? isConsum : super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void initListView() {
        lvChat.setAdapter(mChatAdapter);
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_FLING:
                        break;
                    case SCROLL_STATE_IDLE:
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        ekBar.reset();
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    private void scrollToBottom() {
        lvChat.requestLayout();
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                lvChat.setSelection(lvChat.getBottom());
            }
        });
    }

    @Override
    public void OnFuncPop(int height) {
        scrollToBottom();
    }

    @Override
    public void OnFuncClose() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        JMessageClient.exitConversation();
        ekBar.reset();
    }

    @Override
    protected void onResume() {
        String targetId = getIntent().getStringExtra(TARGET_ID);
        if (!mIsSingle) {
            long groupId = getIntent().getLongExtra(GROUP_ID, 0);
            if (groupId != 0) {
                JGApplication.isAtMe.put(groupId, false);
                JGApplication.isAtall.put(groupId, false);
                JMessageClient.enterGroupConversation(groupId);
            }
        } else if (null != targetId) {
            String appKey = getIntent().getStringExtra(TARGET_APP_KEY);
            JMessageClient.enterSingleConversation(targetId, appKey);
        }

        //历史消息中删除后返回到聊天界面刷新界面
        if (JGApplication.ids != null && JGApplication.ids.size() > 0) {
            for (Message msg : JGApplication.ids) {
                mChatAdapter.removeMessage(msg);
            }
        }
        mChatAdapter.notifyDataSetChanged();
        //发送名片返回聊天界面刷新信息
        if (SharePreferenceManager.getIsOpen()) {
            initData();
            SharePreferenceManager.setIsOpen(false);
        }
        super.onResume();

    }

    public void onEvent(MessageEvent event) {
        final Message message = event.getMessage();

        //若为群聊相关事件，如添加、删除群成员
        if (message.getContentType() == ContentType.eventNotification) {
            GroupInfo groupInfo = (GroupInfo) message.getTargetInfo();
            long groupId = groupInfo.getGroupID();
            EventNotificationContent.EventNotificationType type = ((EventNotificationContent) message
                    .getContent()).getEventNotificationType();
            if (groupId == mGroupId) {
                switch (type) {
                    case group_member_added:
                        //添加群成员事件
                        List<String> userNames = ((EventNotificationContent) message.getContent()).getUserNames();
                        //群主把当前用户添加到群聊，则显示聊天详情按钮
                        refreshGroupNum();
                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mChatView.showRightBtn();
                                }
                            });
                        }

                        break;
                    case group_member_removed:
                        //删除群成员事件
                        userNames = ((EventNotificationContent) message.getContent()).getUserNames();
                        //群主删除了当前用户，则隐藏聊天详情按钮
                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mChatView.dismissRightBtn();
                                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                                    if (TextUtils.isEmpty(groupInfo.getGroupName())) {
                                        mChatView.setChatTitle(R.string.group);
                                    } else {
                                        mChatView.setChatTitle(groupInfo.getGroupName());
                                    }
                                    mChatView.dismissGroupNum();
                                }
                            });
                        } else {
                            refreshGroupNum();
                        }

                        break;
                    case group_member_exit:
                        EventNotificationContent content = (EventNotificationContent) message.getContent();
                        if (content.getUserNames().contains(JMessageClient.getMyInfo().getUserName())) {
                            mChatAdapter.notifyDataSetChanged();
                        } else {
                            refreshGroupNum();
                        }
                        break;
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.getTargetType() == ConversationType.single) {
                    UserInfo userInfo = (UserInfo) message.getTargetInfo();
                    String targetId = userInfo.getUserName();
                    String appKey = userInfo.getAppKey();
                    if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                        Message lastMsg = mChatAdapter.getLastMsg();
                        if (lastMsg == null || message.getId() != lastMsg.getId()) {
                            mChatAdapter.addMsgToList(message);
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    long groupId = ((GroupInfo) message.getTargetInfo()).getGroupID();
                    if (groupId == mGroupId) {
                        Message lastMsg = mChatAdapter.getLastMsg();
                        if (lastMsg == null || message.getId() != lastMsg.getId()) {
                            mChatAdapter.addMsgToList(message);
                        } else {
                            mChatAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    public void onEventMainThread(MessageRetractEvent event) {
        Message retractedMessage = event.getRetractedMessage();
        mChatAdapter.delMsgRetract(retractedMessage);
    }

    /**
     * 当在聊天界面断网再次连接时收离线事件刷新
     */
    public void onEvent(OfflineMessageEvent event) {
        Conversation conv = event.getConversation();
        if (conv.getType().equals(ConversationType.single)) {
            UserInfo userInfo = (UserInfo) conv.getTargetInfo();
            String targetId = userInfo.getUserName();
            String appKey = userInfo.getAppKey();
            if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
                List<Message> singleOfflineMsgList = event.getOfflineMessageList();
                if (singleOfflineMsgList != null && singleOfflineMsgList.size() > 0) {
                    mChatView.setToBottom();
                    mChatAdapter.addMsgListToList(singleOfflineMsgList);
                }
            }
        } else {
            long groupId = ((GroupInfo) conv.getTargetInfo()).getGroupID();
            if (groupId == mGroupId) {
                List<Message> offlineMessageList = event.getOfflineMessageList();
                if (offlineMessageList != null && offlineMessageList.size() > 0) {
                    mChatView.setToBottom();
                    mChatAdapter.addMsgListToList(offlineMessageList);
                }
            }
        }
    }

    private void refreshGroupNum() {
        Conversation conv = JMessageClient.getGroupConversation(mGroupId);
        GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
        if (!TextUtils.isEmpty(groupInfo.getGroupName())) {
            android.os.Message handleMessage = mUIHandler.obtainMessage();
            handleMessage.what = REFRESH_GROUP_NAME;
            Bundle bundle = new Bundle();
            bundle.putString(GROUP_NAME, groupInfo.getGroupName());
            bundle.putInt(MEMBERS_COUNT, groupInfo.getGroupMembers().size());
            handleMessage.setData(bundle);
            handleMessage.sendToTarget();
        } else {
            android.os.Message handleMessage = mUIHandler.obtainMessage();
            handleMessage.what = REFRESH_GROUP_NUM;
            Bundle bundle = new Bundle();
            bundle.putInt(MEMBERS_COUNT, groupInfo.getGroupMembers().size());
            handleMessage.setData(bundle);
            handleMessage.sendToTarget();
        }
    }

    private ChattingListAdapter.ContentLongClickListener longClickListener = new ChattingListAdapter.ContentLongClickListener() {

        @Override
        public void onContentLongClick(final int position, View view) {
            final Message msg = mChatAdapter.getMessage(position);

            if (msg == null) {
                return;
            }
            //如果是文本消息
            if ((msg.getContentType() == ContentType.text) && ((TextContent) msg.getContent()).getStringExtra("businessCard") == null) {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            final String content = ((TextContent) msg.getContent()).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                ClipboardManager clipboard = (ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
                                        JGApplication.forwardMsg.clear();
                                        JGApplication.forwardMsg.add(msg);
                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("复制"))
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 0) {
                                        if (msg.getContentType() == ContentType.text) {
                                            final String content = ((TextContent) msg.getContent()).getText();
                                            if (Build.VERSION.SDK_INT > 11) {
                                                ClipboardManager clipboard = (ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                ClipData clip = ClipData.newPlainText("Simple text", content);
                                                clipboard.setPrimaryClip(clip);
                                            } else {
                                                android.text.ClipboardManager clip = (android.text.ClipboardManager) mContext
                                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                                if (clip.hasText()) {
                                                    clip.getText();
                                                }
                                            }
                                            Toast.makeText(ChatActivity.this, "已复制", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持复制文字", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 1) {
                                        //转发
                                        if (msg.getContentType() == ContentType.text || msg.getContentType() == ContentType.image ||
                                                (msg.getContentType() == ContentType.file && (msg.getContent()).getStringExtra("video") != null)) {
                                            Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
                                            JGApplication.forwardMsg.clear();
                                            JGApplication.forwardMsg.add(msg);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(ChatActivity.this, "只支持转发文本,图片,小视频", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (position == 2) {
                                        //撤回
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mChatAdapter.delMsgRetract(msg);
                                                }
                                            }
                                        });
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                }
                //除了文本消息类型之外的消息类型
            } else {
                //接收方
                if (msg.getDirect() == MessageDirect.receive) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    } else {
                                        Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
                                        JGApplication.forwardMsg.clear();
                                        JGApplication.forwardMsg.add(msg);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                    //发送方
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    float OldListY = (float) location[1];
                    float OldListX = (float) location[0];
                    new TipView.Builder(ChatActivity.this, mChatView, (int) OldListX + view.getWidth() / 2, (int) OldListY + view.getHeight())
                            .addItem(new TipItem("转发"))
                            .addItem(new TipItem("撤回"))
                            .addItem(new TipItem("删除"))
                            .setOnItemClickListener(new TipView.OnItemClickListener() {
                                @Override
                                public void onItemClick(String str, final int position) {
                                    if (position == 1) {
                                        //撤回
                                        mConv.retractMessage(msg, new BasicCallback() {
                                            @Override
                                            public void gotResult(int i, String s) {
                                                if (i == 855001) {
                                                    Toast.makeText(ChatActivity.this, "发送时间过长，不能撤回", Toast.LENGTH_SHORT).show();
                                                } else if (i == 0) {
                                                    mChatAdapter.delMsgRetract(msg);
                                                }
                                            }
                                        });
                                    } else if (position == 0) {
                                        Intent intent = new Intent(ChatActivity.this, ForwardMsgActivity.class);
                                        JGApplication.forwardMsg.clear();
                                        JGApplication.forwardMsg.add(msg);
                                        startActivity(intent);
                                    } else {
                                        //删除
                                        mConv.deleteMessage(msg.getId());
                                        mChatAdapter.removeMessage(msg);
                                    }
                                }

                                @Override
                                public void dismiss() {

                                }
                            })
                            .create();
                }
            }
        }
    };

    /**
     * 消息已读事件
     */
    public void onEventMainThread(MessageReceiptStatusChangeEvent event) {
        List<MessageReceiptStatusChangeEvent.MessageReceiptMeta> messageReceiptMetas = event.getMessageReceiptMetas();
        for (MessageReceiptStatusChangeEvent.MessageReceiptMeta meta : messageReceiptMetas) {
            long serverMsgId = meta.getServerMsgId();
            int unReceiptCnt = meta.getUnReceiptCnt();
            mChatAdapter.setUpdateReceiptCount(serverMsgId, unReceiptCnt);
        }
    }

    private Uri getUriFromFile(File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String applicationId = getApplicationContext().getPackageName();
            return FileProvider.getUriForFile(this, applicationId+".fileProvider", file);
        }else  {
            return Uri.fromFile(file);
        }
    }
    public void onEventMainThread(ImageEvent event) {
        switch (event.getFlag()) {
            case JGApplication.IMAGE_MESSAGE:
                final int requestCode = RequestCode.PICK_IMAGE;
                new PermissionRequester(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<PermissionResult>() {
                            @Override
                            public void call(PermissionResult permissionResult) {
                                if(permissionResult.getGranted()){
                                    new PicturePicker().withActivity(ChatActivity.this)
                                            .requestCode(requestCode)
                                            .chooseType(PicturePicker.CHOOSE_TYPE_MULTIPLE())
                                            .start();
                                }else {
                                    Toast.makeText(ChatActivity.this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(ChatActivity.this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                            }
                        });
                break;
            case JGApplication.TAKE_PHOTO_MESSAGE:
                new PermissionRequester(this).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
                        .subscribe(new Action1<PermissionResult>() {
                            @Override
                            public void call(PermissionResult permissionResult) {
                                if (permissionResult.getGranted()) {
                                    Log.i("ChatActivity", "take photo ........................");
                                    Intent intent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    //return-data false 不是直接返回拍照后的照片Bitmap 因为照片太大会传输失败
                                    intent.putExtra("return-data", false);
                                    //改用Uri 传递
                                    if (cameraImageUri == null) {
                                        String filePath = StorageUtil.getWritePath(ChatActivity.this, "camera_cache.jpg", StorageType.TYPE_TEMP);
                                        Log.i("ChatActivity", "take photo filePath."+filePath);
                                        cameraImageUri = getUriFromFile(new File(filePath));
                                    }
                                    Log.i("ChatActivity", "take photo ....111111111..."+cameraImageUri.getPath());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                                    intent.putExtra("noFaceDetection", true);
                                    startActivityForResult(intent, RequestCode.TAKE_PHOTO);
                                } else {
                                    Toast.makeText(ChatActivity.this, "请在应用管理中打开“相机,读写存储,录音”访问权限！", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(ChatActivity.this, "请在应用管理中打开“相机,读写存储,录音”访问权限！", Toast.LENGTH_LONG).show();
                            }
                        });

                break;
            case JGApplication.TAKE_LOCATION:

                new PermissionRequester(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe(new Action1<PermissionResult>() {
                            @Override
                            public void call(PermissionResult permissionResult) {
                                if (permissionResult.getGranted()){
                                    Intent intent = new Intent(mContext, MapPickerActivity.class);
                                    intent.putExtra(JGApplication.TARGET_ID, mTargetId);
                                    intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);
                                    intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                                    intent.putExtra("sendLocation", true);
                                    startActivityForResult(intent, JGApplication.REQUEST_CODE_SEND_LOCATION);
                                }else {
                                    Toast.makeText(ChatActivity.this, "请在应用管理中打开“位置”访问权限！", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(ChatActivity.this, "请在应用管理中打开“位置”访问权限！", Toast.LENGTH_LONG).show();
                            }
                        });


                break;
            case JGApplication.FILE_MESSAGE:
                new PermissionRequester(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Action1<PermissionResult>() {
                            @Override
                            public void call(PermissionResult permissionResult) {
                                if (permissionResult.getGranted()){
                                    Intent intent = new Intent(mContext, SendFileActivity.class);
                                    intent.putExtra(JGApplication.TARGET_ID, mTargetId);
                                    intent.putExtra(JGApplication.TARGET_APP_KEY, mTargetAppKey);
                                    intent.putExtra(JGApplication.GROUP_ID, mGroupId);
                                    startActivityForResult(intent, JGApplication.REQUEST_CODE_SEND_FILE);
                                }else {
                                    Toast.makeText(ChatActivity.this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(ChatActivity.this, "请在应用管理中打开“读写存储”访问权限！", Toast.LENGTH_LONG).show();
                            }
                        });

                break;
            case JGApplication.BUSINESS_CARD:
                Intent intent = new Intent(mContext, FriendListActivity.class);
                intent.putExtra("isSingle", mIsSingle);
                intent.putExtra("userId", mTargetId);
                intent.putExtra("groupId", mGroupId);
                startActivity(intent);
                break;
            case JGApplication.TACK_VIDEO:
            case JGApplication.TACK_VOICE:
                ToastUtil.shortToast(mContext, "该功能正在添加中");
                break;
            default:
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode) {
            case JGApplication.RESULT_CODE_AT_MEMBER:
                if (!mIsSingle) {
                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                    String username = data.getStringExtra(JGApplication.TARGET_ID);
                    String appKey = data.getStringExtra(JGApplication.TARGET_APP_KEY);
                    UserInfo userInfo = groupInfo.getGroupMemberInfo(username, appKey);
                    if (null == mAtList) {
                        mAtList = new ArrayList<UserInfo>();
                    }
                    mAtList.add(userInfo);
                    mLongClick = true;
                    ekBar.getEtChat().appendMention(data.getStringExtra(JGApplication.NAME));
                    ekBar.getEtChat().setSelection(ekBar.getEtChat().getText().length());
                }
                break;
            case JGApplication.RESULT_CODE_AT_ALL:
                mAtAll = data.getBooleanExtra(JGApplication.ATALL, false);
                mLongClick = true;
                if (mAtAll) {
                    ekBar.getEtChat().setText(ekBar.getEtChat().getText().toString() + "所有成员 ");
                    ekBar.getEtChat().setSelection(ekBar.getEtChat().getText().length());
                }
                break;
            case JGApplication.RESULT_CODE_SEND_LOCATION:
                //之前是在地图选择那边做的发送逻辑,这里是通过msgID拿到的message放到ui上.但是发现问题,message的status始终是send_going状态
                //因为那边发送的是自己创建的对象,这里通过id取出来的不是同一个对象.尽管内容都是一样的.所以为了保证发送的对象个ui上拿出来的
                //对象是同一个,就地图那边传过来数据,在这边进行创建message
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                int mapview = data.getIntExtra("mapview", 0);
                String street = data.getStringExtra("street");
                String path = data.getStringExtra("path");
                LocationContent locationContent = new LocationContent(latitude,
                        longitude, mapview, street);
                locationContent.setStringExtra("path", path);
                Message message = mConv.createSendMessage(locationContent);
                MessageSendingOptions options = new MessageSendingOptions();
                options.setNeedReadReceipt(true);
                JMessageClient.sendMessage(message, options);
                mChatAdapter.addMsgFromReceiptToList(message);

                int customMsgId = data.getIntExtra("customMsg", -1);
                if (-1 != customMsgId) {
                    Message customMsg = mConv.getMessage(customMsgId);
                    mChatAdapter.addMsgToList(customMsg);
                }
                mChatView.setToBottom();
                break;
            case JGApplication.RESULT_CODE_SEND_FILE:
                int[] intArrayExtra = data.getIntArrayExtra(MsgIDs);
                for (int msgId : intArrayExtra) {
                    handleSendMsg(msgId);
                }
                break;
            case JGApplication.RESULT_CODE_CHAT_DETAIL:
                String title = data.getStringExtra(JGApplication.CONV_TITLE);
                if (!mIsSingle) {
                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
                    UserInfo userInfo = groupInfo.getGroupMemberInfo(mMyInfo.getUserName(), mMyInfo.getAppKey());
                    //如果自己在群聊中，同时显示群人数
                    if (userInfo != null) {
                        if (TextUtils.isEmpty(title)) {
                            mChatView.setChatTitle(IdHelper.getString(mContext, "group"),
                                    data.getIntExtra(MEMBERS_COUNT, 0));
                        } else {
                            mChatView.setChatTitle(title, data.getIntExtra(MEMBERS_COUNT, 0));
                        }
                    } else {
                        if (TextUtils.isEmpty(title)) {
                            mChatView.setChatTitle(IdHelper.getString(mContext, "group"));
                        } else {
                            mChatView.setChatTitle(title);
                        }
                        mChatView.dismissGroupNum();
                    }

                } else mChatView.setChatTitle(title);
                if (data.getBooleanExtra("deleteMsg", false)) {
                    mChatAdapter.clearMsgList();
                }
                break;
        }


        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RequestCode.PICK_IMAGE://4
                onPickImageActivityResult(data);
                break;
            case RequestCode.TAKE_PHOTO:
                takePhotoResultBack();
                break;
            case RequestCode.TAKE_VIDEO:
                if (data != null) {
                    String path = data.getStringExtra("video");
                    try {
                        FileContent fileContent = new FileContent(new File(path));
                        fileContent.setStringExtra("video", "mp4");
                        Message msg = mConv.createSendMessage(fileContent);
                        handleSendMsg(msg.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }

    /**
     * 拍照成功后发送消息
     */
    private void takePhotoResultBack() {
        try {
            String filePath = StorageUtil.getWritePath(this, "camera_cache.jpg", StorageType.TYPE_TEMP);
            Log.i("ChatActivity", "take photo path:"+filePath);
            List<PhotoInfo> photoInfos = new ArrayList<>();
            PhotoInfo info = new PhotoInfo();
            info.setAbsolutePath(filePath);
            photoInfos.add(info);
            sendImageAfterSelfImagePicker(photoInfos);
//            InputStream in = getContentResolver().openInputStream(cameraImageUri);
//            Bitmap bitmap = BitmapFactory.decodeStream(in);
//            ImageContent.createImageContentAsync(bitmap, new ImageContent.CreateImageContentCallback() {
//                @Override
//                public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
//                    if (responseCode == 0) {
//                        Message msg = mConv.createSendMessage(imageContent);
//                        handleSendMsg(msg.getId());
//                    }
//                }
//            });
            Log.i("ChatActivity", "camera take photo success");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "获取照片异常", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 图片选取回调
     */
    private void onPickImageActivityResult( Intent data) {
        if (data == null) {
            return;
        }
//        boolean local = data.getBooleanExtra(Extras.EXTRA_FROM_LOCAL, false);
//        if (local) {
//            // 本地相册
//            sendImageAfterSelfImagePicker(data);
//        }
        List<String> results= data.getStringArrayListExtra(PicturePicker.FANCY_PICTURE_PICKER_ARRAY_LIST_RESULT_KEY());
        List<PhotoInfo> photoInfos = new ArrayList<>();
        if (results!=null && !results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                Log.i("ChatActivity", "choose photo ..."+results.get(i));
                PhotoInfo info = new PhotoInfo();
                info.setAbsolutePath(results.get(i));
                photoInfos.add(info);
            }
        }
        sendImageAfterSelfImagePicker(photoInfos);
    }

    /**
     * 发送图片
     */

    private void sendImageAfterSelfImagePicker(List<PhotoInfo> photoInfos) {
        SendImageHelper.sendImageAfterSelfFancyImagePicker(this, photoInfos, false, new SendImageHelper.Callback() {
            @Override
            public void sendImage(final File file, boolean isOrig) {
                Log.i("ChatActivity", "start sendImage...file:"+file.getAbsolutePath()+ ", isOrig:"+isOrig);
                //所有图片都在这里拿到
                ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                        if (responseCode == 0) {
                            Message msg = mConv.createSendMessage(imageContent);
                            handleSendMsg(msg.getId());
                        }
                    }
                });

            }
        });
    }

    //发送极光熊
    private void OnSendImage(String iconUri) {
        String substring = iconUri.substring(7);
        File file = new File(substring);
        ImageContent.createImageContentAsync(file, new ImageContent.CreateImageContentCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, ImageContent imageContent) {
                if (responseCode == 0) {
                    imageContent.setStringExtra("jiguang", "xiong");
                    Message msg = mConv.createSendMessage(imageContent);
                    handleSendMsg(msg.getId());
                } else {
                    ToastUtil.shortToast(mContext, responseMessage);
                }
            }
        });
    }

    /**
     * 处理发送图片，刷新界面
     *
     * @param data intent
     */
    private void handleSendMsg(int data) {
        mChatAdapter.setSendMsgs(data);
        mChatView.setToBottom();
    }

    private static class UIHandler extends Handler {
        private final WeakReference<ChatActivity> mActivity;

        public UIHandler(ChatActivity activity) {
            mActivity = new WeakReference<ChatActivity>(activity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            ChatActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case REFRESH_LAST_PAGE:
                        activity.mChatAdapter.dropDownToRefresh();
                        activity.mChatView.getListView().onDropDownComplete();
                        if (activity.mChatAdapter.isHasLastPage()) {
                            if (Build.VERSION.SDK_INT >= 21) {
                                activity.mChatView.getListView()
                                        .setSelectionFromTop(activity.mChatAdapter.getOffset(),
                                                activity.mChatView.getListView().getHeaderHeight());
                            } else {
                                activity.mChatView.getListView().setSelection(activity.mChatAdapter
                                        .getOffset());
                            }
                            activity.mChatAdapter.refreshStartPosition();
                        } else {
                            activity.mChatView.getListView().setSelection(0);
                        }
                        //显示上一页的消息数18条
                        activity.mChatView.getListView()
                                .setOffset(activity.mChatAdapter.getOffset());
                        break;
                    case REFRESH_GROUP_NAME:
                        if (activity.mConv != null) {
                            int num = msg.getData().getInt(MEMBERS_COUNT);
                            String groupName = msg.getData().getString(GROUP_NAME);
                            activity.mChatView.setChatTitle(groupName, num);
                        }
                        break;
                    case REFRESH_GROUP_NUM:
                        int num = msg.getData().getInt(MEMBERS_COUNT);
                        activity.mChatView.setChatTitle(R.string.group, num);
                        break;
                    case REFRESH_CHAT_TITLE:
                        if (activity.mGroupInfo != null) {
                            //检查自己是否在群组中
                            UserInfo info = activity.mGroupInfo.getGroupMemberInfo(activity.mMyInfo.getUserName(),
                                    activity.mMyInfo.getAppKey());
                            if (!TextUtils.isEmpty(activity.mGroupInfo.getGroupName())) {
                                if (info != null) {
                                    activity.mChatView.setChatTitle(activity.mTitle,
                                            activity.mGroupInfo.getGroupMembers().size());
                                    activity.mChatView.showRightBtn();
                                } else {
                                    activity.mChatView.setChatTitle(activity.mTitle);
                                    activity.mChatView.dismissRightBtn();
                                }
                            }
                        }
                        break;
                }
            }
        }
    }

}
