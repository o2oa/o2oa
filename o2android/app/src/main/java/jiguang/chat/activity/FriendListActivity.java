package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.FriendListAdapter;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.UserEntry;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.pinyin.PinyinComparator;
import jiguang.chat.utils.sidebar.SideBar;
import jiguang.chat.view.listview.StickyListHeadersListView;

/**
 * Created by ${chenyn} on 2017/9/21.
 */

public class FriendListActivity extends BaseActivity {

    private TextView mTv_cancel;
    private StickyListHeadersListView mFriend_listView;
    private LinearLayout mSearchTitle;
    private SideBar mSidebar;
    private Context mContext;
    private List<FriendEntry> mList;
    private FriendListAdapter mAdapter;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_friend_list);
        initView();
        initData();
    }

    private void initView() {
//        View inflate = LayoutInflater.from(this).inflate(R.layout.conversation_head_view, null);
        mFriend_listView = (StickyListHeadersListView) findViewById(R.id.friend_listView);
        mTv_cancel = (TextView) findViewById(R.id.tv_cancel);
//        mSearchTitle = (LinearLayout) inflate.findViewById(R.id.search_title);
        mSidebar = (SideBar) findViewById(R.id.sidebar);

//        mFriend_listView.addHeaderView(mSearchTitle);
    }

    private void initData() {

        final UserEntry user = UserEntry.getUser(JMessageClient.getMyInfo().getUserName(),
                JMessageClient.getMyInfo().getAppKey());
        mList = user.getFriends();
        Collections.sort(mList, new PinyinComparator());
        mAdapter = new FriendListAdapter(mContext, mList);
        mFriend_listView.setAdapter(mAdapter);

        mTv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFriend_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                FriendEntry friendEntry = (FriendEntry) itemAtPosition;
                if (getIntent().getBooleanExtra("isSingle", false)) {
                    setBusinessCard(friendEntry, JMessageClient.getSingleConversation(getIntent().getStringExtra("userId")));
                } else {
                    setBusinessCard(friendEntry, JMessageClient.getGroupConversation(getIntent().getLongExtra("groupId", 0)));
                }
            }
        });

//        mSearchTitle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FriendListActivity.this, SearchFriendBusinessActivity.class);
//                intent.putExtra("isSingle", getIntent().getBooleanExtra("isSingle", false));
//                intent.putExtra("userId", getIntent().getStringExtra("userId"));
//                intent.putExtra("groupId", getIntent().getLongExtra("groupId", 0));
//                startActivity(intent);
//            }
//        });
    }

    private void setBusinessCard(final FriendEntry entry, final Conversation conversation) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cancel:
                        mDialog.dismiss();
                        break;
                    case R.id.btn_sure:
                        mDialog.dismiss();
                        //把名片的userName和appKey通过extra发送给对方
                        TextContent content = new TextContent("推荐了一张名片");
                        content.setStringExtra("userName", entry.username);
                        content.setStringExtra("appKey", entry.appKey);
                        content.setStringExtra("businessCard", "businessCard");

                        Message textMessage = conversation.createSendMessage(content);
                        MessageSendingOptions options = new MessageSendingOptions();
                        options.setNeedReadReceipt(false);
                        JMessageClient.sendMessage(textMessage, options);
                        textMessage.setOnSendCompleteCallback(new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    SharePreferenceManager.setIsOpen(true);
                                    Toast.makeText(FriendListActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    HandleResponseCode.onHandle(FriendListActivity.this, i, false);
                                }
                            }
                        });
                        break;
                }
            }
        };
        mDialog = DialogCreator.createBusinessCardDialog(FriendListActivity.this, listener, conversation.getTitle(),
                entry.username, entry.avatar);
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }
}
