package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import jiguang.chat.adapter.CreateGroupAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.keyboard.utils.EmoticonsKeyboardUtils;
import jiguang.chat.utils.sidebar.SideBar;
import jiguang.chat.view.listview.StickyListHeadersListView;


/**
 * Created by ${chenyn} on 2017/5/3.
 */

public class CreateGroupActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ImageButton mCancelBtn;
    private EditText mSearchEt;
    private StickyListHeadersListView mListView;
    private SideBar mSideBar;
    private TextView mLetterHintTv;
    private LinearLayout mFinishBtn;
//    private StickyVOListAdapter mAdapter;
//    private List<ImFriendEntryVO> mData = new ArrayList<>();
    private HorizontalScrollView scrollViewSelected;
    private GridView imageSelectedGridView;
    private CreateGroupAdapter mGroupAdapter;
    private Context mContext;
    private Dialog mLoadingDialog;
    private TextView mTv_noFriend;
    private TextView mTv_noFilter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_create_group);
        initView();
        initData();
    }

    private void initData() {




        mGroupAdapter = new CreateGroupAdapter(CreateGroupActivity.this);
        imageSelectedGridView.setAdapter(mGroupAdapter);
//        mAdapter = new StickyVOListAdapter(CreateGroupActivity.this, mData, true, scrollViewSelected, imageSelectedGridView, mGroupAdapter);
//        mListView.setAdapter(mAdapter);

    }

    private void initView() {
        mTv_noFriend = (TextView) findViewById(R.id.tv_noFriend);
        mTv_noFilter = (TextView) findViewById(R.id.tv_noFilter);
        mCancelBtn = (ImageButton) findViewById(R.id.jmui_cancel_btn);
        mFinishBtn = (LinearLayout) findViewById(R.id.finish_btn);
        mSearchEt = (EditText) findViewById(R.id.search_et);
        mListView = (StickyListHeadersListView) findViewById(R.id.sticky_list_view);
        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mLetterHintTv = (TextView) findViewById(R.id.letter_hint_tv);
        mSideBar.setTextView(mLetterHintTv);
        scrollViewSelected = (HorizontalScrollView) findViewById(R.id.contact_select_area);
        imageSelectedGridView = (GridView) findViewById(R.id.contact_select_area_grid);

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
//                int position = mAdapter.getSectionForLetter(s);
//                if (position != -1 && position < mAdapter.getCount()) {
//                    mListView.setSelection(position - 1);
//                }
            }
        });

        mSearchEt.addTextChangedListener(this);

        mListView.setDrawingListUnderStickyHeader(true);
        mListView.setAreHeadersSticky(true);
        mListView.setStickyHeaderTopOffset(0);
        mFinishBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (forDelete != null && forDelete.size() > 0) {
//            for (ImFriendEntryVO e : forDelete) {
////                realmDataService.deleteFriend(e).subscribeOn(Schedulers.io()).subscribe();
//            }
//            mAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jmui_cancel_btn:
                EmoticonsKeyboardUtils.closeSoftKeyboard(this);
                finish();
                break;
            case R.id.finish_btn:
                //拿到所选择的userName
//                final ArrayList<String> selectedUser = mAdapter.getSelectedUser();
                mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                        mContext.getString(R.string.creating_hint));
                mLoadingDialog.show();
//                JMessageClient.createGroup("", "", new CreateGroupCallback() {
//                    @Override
//                    public void gotResult(int responseCode, String responseMsg, final long groupId) {
//                        if (responseCode == 0) {
//                            if (selectedUser.size() > 0) {
//                                JMessageClient.addGroupMembers(groupId, selectedUser, new BasicCallback() {
//                                    @Override
//                                    public void gotResult(int responseCode, String responseMessage) {
//                                        mLoadingDialog.dismiss();
//                                        if (responseCode == 0) {
//                                            //如果创建群组时添加了人,那么就在size基础上加上自己
//                                            createGroup(groupId, selectedUser.size() + 1);
//                                        } else if (responseCode == 810007) {
//                                            ToastUtil.shortToast(mContext, "不能添加自己");
//                                        } else {
//                                            ToastUtil.shortToast(mContext, "添加失败");
//                                        }
//                                    }
//                                });
//                            } else {
//                                mLoadingDialog.dismiss();
//                                //如果创建群组时候没有选择人,那么size就是1
//                                createGroup(groupId, 1);
//                            }
//                        } else {
//                            mLoadingDialog.dismiss();
//                            ToastUtil.shortToast(mContext, responseMsg);
//                        }
//                    }
//                });
                break;
        }
    }

    private void createGroup(long groupId, int groupMembersSize) {
        Conversation groupConversation = JMessageClient.getGroupConversation(groupId);
        if (groupConversation == null) {
            groupConversation = Conversation.createGroupConversation(groupId);
            EventBus.getDefault().post(new Event.Builder()
                    .setType(EventType.createConversation)
                    .setConversation(groupConversation)
                    .build());
        }

        Intent intent = new Intent();
        //设置跳转标志
        intent.putExtra("fromGroup", true);
        intent.putExtra(JGApplication.CONV_TITLE, groupConversation.getTitle());
        intent.putExtra(JGApplication.MEMBERS_COUNT, groupMembersSize);
        intent.putExtra(JGApplication.GROUP_ID, groupId);
        intent.setClass(mContext, ChatActivity.class);
        mContext.startActivity(intent);
        finish();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

//    List<ImFriendEntryVO> filterDateList;

    private void filterData(final String filterStr) {
//        filterDateList = new ArrayList<>();
        if (!TextUtils.isEmpty(filterStr)) {
//            filterDateList.clear();
            //遍历好友集合进行匹配
//            for (ImFriendEntryVO entry : mData) {
//                String appKey = entry.getAppKey();
//
//                String userName = entry.getUsername();
//                String noteName = entry.getNoteName();
//                String nickName = entry.getNickName();
//                if (!userName.equals(filterStr) && userName.contains(filterStr) ||
//                        !userName.equals(filterStr) && noteName.contains(filterStr) ||
//                        !userName.equals(filterStr) && nickName.contains(filterStr) &&
//                                appKey.equals(JMessageClient.getMyInfo().getAppKey())) {
//                    filterDateList.add(entry);
//                }
//            }
        } else {
//            if (mFriendEntry != null) {
//                realmDataService.deleteFriend(mFriendEntry).subscribeOn(Schedulers.io()).subscribe();
//            }
//            filterDateList = mData;
        }
//        if (filterDateList.size() > 0) {
//            mTv_noFilter.setVisibility(View.GONE);
//        }
        // 根据a-z进行排序
//        Collections.sort(filterDateList, new PinyinVOComparator());
//        mAdapter.updateListView(filterDateList, true, filterStr);

        //当搜索的人不是好友时全局搜索
        //这个不能放在for中的else中,否则for循环了多少次就会添加多少次搜出来的user
//        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
//                JMessageClient.getMyInfo().getAppKey());
//        final List<ImFriendEntryVO> finalFilterDateList = filterDateList;
        JMessageClient.getUserInfo(filterStr, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (responseCode == 0) {
                    final UserInfo fInfo = info;
                     ////////////////////todo

                } else {
//                    if (filterDateList.size() > 0) {
//                        mTv_noFilter.setVisibility(View.GONE);
//                    } else {
//                        mTv_noFilter.setVisibility(View.VISIBLE);
//                    }
                }
            }
        });

    }

//    List<ImFriendEntryVO> forDelete = new ArrayList<>();

    @Override
    public void afterTextChanged(Editable s) {

    }
}
