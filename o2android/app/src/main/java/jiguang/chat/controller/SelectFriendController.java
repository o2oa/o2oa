package jiguang.chat.controller;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.GridView;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.SelectFriendActivity;
import jiguang.chat.adapter.CreateGroupAdapter;
import jiguang.chat.adapter.StickyListAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.UserEntry;
import jiguang.chat.utils.pinyin.PinyinComparator;
import jiguang.chat.utils.sidebar.SideBar;
import jiguang.chat.view.SelectFriendView;

public class SelectFriendController implements View.OnClickListener,
        SideBar.OnTouchingLetterChangedListener, TextWatcher {

    private SelectFriendView mSelectFriendView;
    private SelectFriendActivity mContext;
    private StickyListAdapter mAdapter;
    private List<FriendEntry> mData;
    private Long mGroupID;
    HorizontalScrollView scrollViewSelected;
    CreateGroupAdapter groupAdapter;
    GridView imageSelectedGridView;
    private FriendEntry mFriendEntry;

    public SelectFriendController(SelectFriendView view, SelectFriendActivity context,
                                  long groupID, HorizontalScrollView scrollViewSelected,
                                  CreateGroupAdapter groupAdapter, GridView imageSelectedGridView) {
        this.mSelectFriendView = view;
        this.mContext = context;
        this.mGroupID = groupID;
        this.scrollViewSelected = scrollViewSelected;
        this.groupAdapter = groupAdapter;
        this.imageSelectedGridView = imageSelectedGridView;
        initData();
    }

    private void initData() {
        UserEntry userEntry = JGApplication.getUserEntry();
        mData = userEntry.getFriends();
        Collections.sort(mData, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mData, true, scrollViewSelected, imageSelectedGridView, groupAdapter);
        mAdapter.setGroupID(mGroupID);
        mSelectFriendView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jmui_cancel_btn:
                mContext.finish();
                break;
            case R.id.finish_btn:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("SelectedUser", mAdapter.getSelectedUser());
                mContext.setResult(JGApplication.RESULT_CODE_SELECT_FRIEND, intent);
                mContext.finish();
                break;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        int position = mAdapter.getSectionForLetter(s);
        if (position != -1 && position < mAdapter.getCount()) {
            mSelectFriendView.setSelection(position - 1);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    List<FriendEntry> forDelete = new ArrayList<>();
    List<FriendEntry> filterDateList;

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(final String filterStr) {
        filterDateList = new ArrayList<>();
        if (!TextUtils.isEmpty(filterStr)) {
            filterDateList.clear();
            for (FriendEntry entry : mData) {
                String appKey = entry.appKey;

                String userName = entry.username;
                String noteName = entry.noteName;
                String nickName = entry.nickName;
                if (!userName.equals(filterStr) && userName.contains(filterStr) ||
                        !userName.equals(filterStr) && noteName.contains(filterStr) ||
                        !userName.equals(filterStr) && nickName.contains(filterStr) &&
                                appKey.equals(JMessageClient.getMyInfo().getAppKey())) {
                    filterDateList.add(entry);
                }
            }
        } else {
            if (mFriendEntry != null) {
                mFriendEntry.delete();
            }
            filterDateList = mData;
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, new PinyinComparator());
        mAdapter.updateListView(filterDateList, true, filterStr);

        //当搜索的人不是好友时全局搜索
        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        final List<FriendEntry> finalFilterDateList = filterDateList;
        JMessageClient.getUserInfo(filterStr, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (responseCode == 0) {
                    mFriendEntry = new FriendEntry(info.getUserID(), info.getUserName(), info.getNotename(), info.getNickname(), info.getAppKey(), info.getAvatar(),
                            info.getUserName(), filterStr.substring(0, 1).toUpperCase(), user);
                    mFriendEntry.save();
                    forDelete.add(mFriendEntry);
                    finalFilterDateList.add(mFriendEntry);
                    Collections.sort(finalFilterDateList, new PinyinComparator());
                    mAdapter.updateListView(finalFilterDateList, true, filterStr);
                }
            }
        });
    }

    public void delNotFriend() {
        if (forDelete != null && forDelete.size() > 0) {
            for (FriendEntry e : forDelete) {
                e.delete();
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
