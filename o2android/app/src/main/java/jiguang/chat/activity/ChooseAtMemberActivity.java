package jiguang.chat.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.adapter.AtMemberAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.model.ParentLinkedHolder;
import jiguang.chat.utils.keyboard.widget.EmoticonsEditText;
import jiguang.chat.utils.pinyin.UserComparator;
import jiguang.chat.utils.sidebar.SideBar;
import jiguang.chat.view.listview.StickyListHeadersListView;


/**
 * @ 功能, 选择群组中成员
 */

public class ChooseAtMemberActivity extends BaseActivity {

    private List<UserInfo> mList;
    private SideBar mSideBar;
    private TextView mLetterHintTv;
    private AtMemberAdapter mAdapter;
    private StickyListHeadersListView mListView;
    private LinearLayout mLl_groupAll;
    private LinearLayout mSearch_title;
    private static ParentLinkedHolder<EmoticonsEditText> textParentLinkedHolder;
    private List<UserInfo> forDel = new ArrayList<>();

    public static void show(ChatActivity textWatcher, EmoticonsEditText editText, String targetId) {
        synchronized (ChooseAtMemberActivity.class) {
            ParentLinkedHolder<EmoticonsEditText> holder = new ParentLinkedHolder<>(editText);
            textParentLinkedHolder = holder.addParent(textParentLinkedHolder);
        }

        Intent intent = new Intent(textWatcher, ChooseAtMemberActivity.class);
        intent.putExtra(JGApplication.GROUP_ID, Long.parseLong(targetId));
        textWatcher.startActivityForResult(intent, JGApplication
                .REQUEST_CODE_AT_MEMBER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_at_member);
        mListView = (StickyListHeadersListView) findViewById(R.id.at_member_list_view);
        mLl_groupAll = (LinearLayout) findViewById(R.id.ll_groupAll);
        mSearch_title = (LinearLayout) findViewById(R.id.search_title);
        mSideBar = (SideBar) findViewById(R.id.sidebar);
        mLetterHintTv = (TextView) findViewById(R.id.letter_hint_tv);
        mSideBar.setTextView(mLetterHintTv);

        initTitle(true, true, "选择成员", "", false, "");

        long groupId = getIntent().getLongExtra(JGApplication.GROUP_ID, 0);
        if (0 != groupId) {
            Conversation conv = JMessageClient.getGroupConversation(groupId);
            GroupInfo groupInfo = (GroupInfo) conv.getTargetInfo();
            mList = groupInfo.getGroupMembers();
            for (UserInfo info : mList) {
                if (info.getUserName().equals(JMessageClient.getMyInfo().getUserName())) {
                    forDel.clear();
                    forDel.add(info);
                }
            }
            mList.removeAll(forDel);
            Collections.sort(mList, new UserComparator());

            mAdapter = new AtMemberAdapter(this, mList);
            mListView.setAdapter(mAdapter);
        }

        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getSectionForLetter(s);
                if (position != -1 && position < mAdapter.getCount()) {
                    mListView.setSelection(position - 1);
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                UserInfo userInfo = mList.get(position);
                Intent intent = new Intent();
                String atName = userInfo.getDisplayName();

                synchronized (ChooseAtMemberActivity.class) {
                    if (textParentLinkedHolder != null) {
                        EmoticonsEditText editText = textParentLinkedHolder.item;
                        if (editText != null)
                            intent.putExtra(JGApplication.NAME, atName);
                    }
                }

                intent.putExtra(JGApplication.TARGET_ID, userInfo.getUserName());
                intent.putExtra(JGApplication.TARGET_APP_KEY, userInfo.getAppKey());
                setResult(JGApplication.RESULT_CODE_AT_MEMBER, intent);
                finish();
            }
        });

        mLl_groupAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //@all
                Intent intent = new Intent();
                intent.putExtra(JGApplication.ATALL, true);
                setResult(JGApplication.RESULT_CODE_AT_ALL, intent);
                finish();
            }
        });

        mSearch_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseAtMemberActivity.this, SearchAtMemberActivity.class);
                JGApplication.mSearchAtMember = mList;
                startActivityForResult(intent, JGApplication.SEARCH_AT_MEMBER_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33 && data != null) {
            Intent intent = new Intent();
            intent.putExtra(JGApplication.NAME, data.getStringExtra(JGApplication.SEARCH_AT_MEMBER_NAME));
            intent.putExtra(JGApplication.TARGET_ID, data.getStringExtra(JGApplication.SEARCH_AT_MEMBER_USERNAME));
            intent.putExtra(JGApplication.TARGET_APP_KEY, data.getStringExtra(JGApplication.SEARCH_AT_APPKEY));
            setResult(JGApplication.RESULT_CODE_AT_MEMBER, intent);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        synchronized (ChooseAtMemberActivity.class) {
            if (textParentLinkedHolder != null) {
                textParentLinkedHolder = textParentLinkedHolder.putParent();
            }
        }
    }


}
