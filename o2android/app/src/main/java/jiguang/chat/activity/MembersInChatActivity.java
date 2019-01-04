package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.AllMembersAdapter;
import jiguang.chat.adapter.CreateGroupAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.pinyin.HanyuPinyin;
import jiguang.chat.utils.sidebar.SideBar;

public class MembersInChatActivity extends BaseActivity {

    private ListView mListView;
    private Dialog mDialog;
    private Context mContext;
    private ImageButton mReturnBtn;
    private TextView mRightBtn;
    private EditText mSearchEt;
    private List<UserInfo> mMemberInfoList = new ArrayList<UserInfo>();
    private List<ItemModel> mShowUserList = new ArrayList<ItemModel>();
    private List<String> mPinyinList = new ArrayList<String>();
    private UIHandler mUIHandler = new UIHandler(this);
    private BackgroundHandler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private static final int PROCESS_USER_INFO_TO_BEANS = 0x1000;
    private static final int SEARCH_MEMBER = 0x1001;
    private static final int SEARCH_MEMBER_SUCCESS = 0x1002;
    private static final int INIT_ADAPTER = 0x1003;
    private static final int ADD_ALL_MEMBER = 0x1004;
    private AllMembersAdapter mAdapter;
    private Dialog mLoadingDialog;
    private long mGroupId;
    private boolean mIsDeleteMode;
    private boolean mIsCreator;
    private String mSearchText;
    private SideBar mSideBar;
    private TextView mLetterHintTv;
    private HorizontalScrollView mHorizontalView;
    private CreateGroupAdapter mGroupAdapter;
    private GridView imageSelectedGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_all_members);
        mListView = (ListView) findViewById(R.id.members_list_view);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mRightBtn = (TextView) findViewById(R.id.right_btn);
        mSearchEt = (EditText) findViewById(R.id.search_et);
        mHorizontalView = (HorizontalScrollView) findViewById(R.id.contact_select_area);
        mLetterHintTv = (TextView) findViewById(R.id.letter_hint_tv);
        imageSelectedGridView = (GridView) findViewById(R.id.contact_select_area_grid);
//        mSideBar = (SideBar) findViewById(R.id.sidebar);
//        mSideBar.setTextView(mLetterHintTv);
        mGroupAdapter = new CreateGroupAdapter(MembersInChatActivity.this);
        imageSelectedGridView.setAdapter(mGroupAdapter);


        mBackgroundThread = new HandlerThread("Work on MembersInChatActivity");
        mBackgroundThread.start();
        mBackgroundHandler = new BackgroundHandler(mBackgroundThread.getLooper());
        mGroupId = getIntent().getLongExtra(JGApplication.GROUP_ID, 0);
        mIsDeleteMode = getIntent().getBooleanExtra(JGApplication.DELETE_MODE, false);
        final Conversation conv = JMessageClient.getGroupConversation(mGroupId);
        GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
        mMemberInfoList = groupInfo.getGroupMembers();
        String groupOwnerId = groupInfo.getGroupOwner();
        final UserInfo myInfo = JMessageClient.getMyInfo();
        mIsCreator = groupOwnerId != null && groupOwnerId.equals(myInfo.getUserName());

        mBackgroundHandler.sendEmptyMessage(PROCESS_USER_INFO_TO_BEANS);

        if (mIsDeleteMode) {
            mRightBtn.setText(this.getString(R.string.jmui_delete));
        } else {
            mRightBtn.setText(this.getString(R.string.add));
        }

        mReturnBtn.setOnClickListener(listener);
        mRightBtn.setOnClickListener(listener);
        mSearchEt.addTextChangedListener(watcher);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    Intent intent = new Intent();
                    setResult(JGApplication.RESULT_CODE_ALL_MEMBER, intent);
                    finish();
                    break;
                case R.id.right_btn:
                    if (mIsDeleteMode) {
                        List<String> deleteList = mAdapter.getSelectedList();
                        if (deleteList.size() > 0) {
                            showDeleteMemberDialog(deleteList);
                        }else {
                            ToastUtil.shortToast(MembersInChatActivity.this, "请至少选择一个成员");
                        }
                    } else {
                        addMemberToGroup();
                    }
                    break;
            }
        }
    };

    TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mSearchText = s.toString().trim();
            mBackgroundHandler.removeMessages(SEARCH_MEMBER);
            mBackgroundHandler.sendMessageDelayed(mBackgroundHandler.obtainMessage(SEARCH_MEMBER), 200);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void showDeleteMemberDialog(final List<String> list) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.jmui_cancel_btn:
                        mDialog.dismiss();
                        break;
                    case R.id.jmui_commit_btn:
                        mDialog.dismiss();
                        mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                                mContext.getString(R.string.deleting_hint));
                        mLoadingDialog.show();
                        JMessageClient.removeGroupMembers(mGroupId, list, new BasicCallback() {
                            @Override
                            public void gotResult(int status, String desc) {
                                mLoadingDialog.dismiss();
                                if (status == 0) {
                                    Intent intent = new Intent();
                                    setResult(JGApplication.RESULT_CODE_ALL_MEMBER, intent);
                                    finish();
                                } else {
                                    Toast.makeText(MembersInChatActivity.this, "删除失败" + desc, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;

                }
            }
        };
        mDialog = DialogCreator.createDeleteMemberDialog(mContext, listener, false);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    //点击添加按钮触发事件
    private void addMemberToGroup() {
        final Dialog dialog = new Dialog(this, R.style.jmui_default_dialog_style);
        final View view = LayoutInflater.from(mContext)
                .inflate(R.layout.dialog_add_friend_to_conv_list, null);
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
        TextView title = (TextView) view.findViewById(R.id.dialog_name);
        title.setText(mContext.getString(R.string.add_friend_to_group_title));
        final EditText userNameEt = (EditText) view.findViewById(R.id.user_name_et);
        final Button cancel = (Button) view.findViewById(R.id.jmui_cancel_btn);
        final Button commit = (Button) view.findViewById(R.id.jmui_commit_btn);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.jmui_cancel_btn:
                        dialog.cancel();
                        break;
                    case R.id.jmui_commit_btn:
                        final String targetId = userNameEt.getText().toString().trim();
                        if (TextUtils.isEmpty(targetId)) {
                            HandleResponseCode.onHandle(mContext, 801001, true);
                            break;
                            //检查群组中是否包含该用户
                        } else if (checkIfNotContainUser(targetId)) {
                            mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                                    mContext.getString(R.string.searching_user));
                            mLoadingDialog.show();
                            getUserInfo(targetId, dialog);
                        } else {
                            HandleResponseCode.onHandle(mContext, 1002, true);
                        }
                        break;
                }
            }
        };
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
    }

    /**
     * 添加成员时检查是否存在该群成员
     *
     * @param targetId 要添加的用户
     * @return 返回是否存在该用户
     */
    private boolean checkIfNotContainUser(String targetId) {
        if (mMemberInfoList != null) {
            for (UserInfo userInfo : mMemberInfoList) {
                if (userInfo.getUserName().equals(targetId))
                    return false;
            }
            return true;
        }
        return true;
    }

    private void getUserInfo(String targetId, final Dialog dialog) {
        JMessageClient.getUserInfo(targetId, new GetUserInfoCallback() {
            @Override
            public void gotResult(int status, String desc, UserInfo userInfo) {
                if (mLoadingDialog != null) {
                    mLoadingDialog.dismiss();
                }
                if (status == 0) {
                    addAMember(userInfo);
                    dialog.cancel();
                }
            }
        });
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
                    // 添加群成员
                    refreshMemberList();
                    Toast.makeText(mContext, mContext.getString(R.string.added), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MembersInChatActivity.this, "添加失败" + desc, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //添加或者删除成员后重新获得MemberInfoList
    public void refreshMemberList() {
        mSearchText = "";
        mSearchEt.setText(mSearchText);
        Conversation conv = JMessageClient.getGroupConversation(mGroupId);
        GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
        mMemberInfoList = groupInfo.getGroupMembers();
//        addAll(true);
        mBackgroundHandler.sendEmptyMessage(ADD_ALL_MEMBER);
    }

    /**
     * 根据输入框输入的字符过滤群成员
     */
    private void filterData() {
        if (TextUtils.isEmpty(mSearchText)) {
            addAll();
        } else {
            String nickname, pinyin;
            int sort;
            SpannableString result;
            ItemModel model;
            UserInfo userInfo;
            for (int i = 0; i < mPinyinList.size(); i++) {
                sort = 0;
                userInfo = mMemberInfoList.get(i);
                nickname = userInfo.getNickname();
                if (TextUtils.isEmpty(nickname)) {
                    nickname = userInfo.getUserName();
                }
                result = new SpannableString(nickname);
                //先进行拼音匹配
                pinyin = mPinyinList.get(i).toLowerCase();
                int offset = pinyin.indexOf(mSearchText.toLowerCase());
                if (offset != -1) {
                    model = new ItemModel();
                    sort += mSearchText.length();
                    result.setSpan(new ForegroundColorSpan(Color.RED), offset,
                            offset + mSearchText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    //进行直接匹配
                    int index = nickname.indexOf(mSearchText);
                    if (index != -1) {
                        sort += mSearchText.length();
                        result.setSpan(new ForegroundColorSpan(Color.RED), index,
                                index + mSearchText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        model.data = userInfo;
                        model.highlight = result;
                        model.sortIndex = sort;
                        mShowUserList.add(model);
                        continue;
                    }
                    model.data = userInfo;
                    model.highlight = result;
                    model.sortIndex = sort;
                    mShowUserList.add(model);
                    //进行直接匹配
                } else {
                    int index = nickname.indexOf(mSearchText);
                    if (index != -1) {
                        sort += mSearchText.length();
                        result.setSpan(new ForegroundColorSpan(Color.RED), index,
                                index + mSearchText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        model = new ItemModel();
                        model.data = userInfo;
                        model.highlight = result;
                        model.sortIndex = sort;
                        mShowUserList.add(model);
                    }
                }
            }
            Collections.sort(mShowUserList, searchComparator);

        }

        mUIHandler.sendEmptyMessage(SEARCH_MEMBER_SUCCESS);
    }

    static class UIHandler extends Handler {

        private final WeakReference<MembersInChatActivity> mActivity;

        public UIHandler(MembersInChatActivity activity) {
            mActivity = new WeakReference<MembersInChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MembersInChatActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case INIT_ADAPTER:
                        activity.mAdapter = new AllMembersAdapter(activity, activity.mShowUserList,
                                activity.mIsDeleteMode, activity.mIsCreator, activity.mGroupId, activity.mWidth
                        ,activity.mHorizontalView, activity.imageSelectedGridView, activity.mGroupAdapter);
                        activity.mListView.setAdapter(activity.mAdapter);
                        activity.mListView.requestFocus();
                        //单击ListView item，跳转到个人详情界面
                        activity.mListView.setOnItemClickListener(activity.mAdapter);
                        break;
                    case SEARCH_MEMBER_SUCCESS:
                        if (activity.mAdapter != null) {
                            activity.mAdapter.updateListView(activity.mShowUserList);
                        }
                        break;
                }
            }
        }
    }

    private class BackgroundHandler extends Handler {
        public BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_MEMBER:
                    if (mShowUserList != null) {
                        mShowUserList.clear();
                    }
                    filterData();
                    break;
                case PROCESS_USER_INFO_TO_BEANS:
                    addAll();
                    mUIHandler.sendEmptyMessage(INIT_ADAPTER);
                    break;
                case ADD_ALL_MEMBER:
                    addAll();
                    break;
            }
        }
    }

    private void addAll() {
        String nickname, pinyin;
        ItemModel itemModel;
        mPinyinList.clear();
        mShowUserList.clear();
        for (UserInfo userInfo: mMemberInfoList) {
            itemModel = new ItemModel();
            itemModel.data = userInfo;
            nickname = userInfo.getNickname();
            if (TextUtils.isEmpty(nickname)) {
                nickname = userInfo.getUserName();
            }
            pinyin = HanyuPinyin.getInstance().getStringPinYin(nickname);
            mPinyinList.add(pinyin);
            itemModel.highlight = new SpannableString(nickname);
            mShowUserList.add(itemModel);
        }
        mUIHandler.sendEmptyMessage(SEARCH_MEMBER_SUCCESS);
    }

    public class ItemModel {
        public UserInfo data;
        public SpannableString highlight;
        public int sortIndex;
    }

    Comparator<ItemModel> searchComparator = new Comparator<ItemModel>() {
        @Override
        public int compare(ItemModel m1, ItemModel m2) {
            return m2.sortIndex - m1.sortIndex;
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(JGApplication.RESULT_CODE_ALL_MEMBER, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUIHandler.removeCallbacksAndMessages(null);
        mBackgroundHandler.removeCallbacksAndMessages(null);
        mBackgroundThread.getLooper().quit();
    }
}
