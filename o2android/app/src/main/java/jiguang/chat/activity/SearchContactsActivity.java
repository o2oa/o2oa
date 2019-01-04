package jiguang.chat.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.application.JGApplication;
import jiguang.chat.controller.ActivityController;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.model.SearchResult;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;
import jiguang.chat.utils.pinyin.CharacterParser;
import jiguang.chat.utils.query.TextSearcher;

/**
 * Created by ${chenyn} on 2017/4/1.
 */

public class SearchContactsActivity extends BaseActivity {

    private EditText mSearchEditText;
    private LinearLayout mFriendListLinearLayout;
    private ListView mFriendListView;
    private LinearLayout mMoreFriendLinearLayout;
    private LinearLayout mGroupListLinearLayout;
    private ListView mGroupsListView;
    private LinearLayout mMoreGroupsLinearLayout;
    private TextView mSearchNoResultsTextView;
    private LinearLayout mPressBackImageView;

    private CharacterParser mCharacterParser;
    private NetworkReceiver mReceiver;

    private String mFilterString;
    private AsyncTask mAsyncTask;
    private ThreadPoolExecutor mExecutor;
    private ArrayList<UserInfo> mFilterFriendList;
    private ArrayList<GroupInfo> mFilterGroupList;
    private ScrollView mSearchView;
    private TextView mNoConnect;
    private Map<String, String> user = new HashMap<>();
    private Map<Long, String> group = new HashMap<>();
    private List<UserInfo> allUser = new ArrayList<>();
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        ActivityController.addActivity(this);

        initView();
        initData();

        initReceiver();
    }


    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);
    }

    //监听网络状态的广播
    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (null == activeInfo) {
                    mNoConnect.setVisibility(View.VISIBLE);
                    mSearchView.setVisibility(View.GONE);
                } else {
                    mNoConnect.setVisibility(View.GONE);
                    mSearchView.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    private void initView() {
        mSearchEditText = (EditText) findViewById(R.id.ac_et_search);
        mFriendListLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_filtered_friend_list);
        mFriendListView = (ListView) findViewById(R.id.ac_lv_filtered_friends_list);
        mMoreFriendLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_more_friends);
        mGroupListLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_filtered_group_list);
        mGroupsListView = (ListView) findViewById(R.id.ac_lv_filtered_groups_list);
        mMoreGroupsLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_more_groups);
        mSearchNoResultsTextView = (TextView) findViewById(R.id.ac_tv_search_no_results);
        mPressBackImageView = (LinearLayout) findViewById(R.id.ac_iv_press_back);
        mSearchView = (ScrollView) findViewById(R.id.search_view);
        mNoConnect = (TextView) findViewById(R.id.no_connect);

        initListener();

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilterFriendList = new ArrayList<>();
                mFilterGroupList = new ArrayList<>();
                mFilterString = s.toString();
                mAsyncTask = new AsyncTask<String, Void, SearchResult>() {
                    @Override
                    protected void onPreExecute() {
                    }

                    @Override
                    protected SearchResult doInBackground(String... params) {
                        return filterInfo(mFilterString);
                    }

                    @Override
                    protected void onPostExecute(SearchResult searchResult) {
                        if (searchResult.getFilterStr().equals(mFilterString)) {
                            List<UserInfo> friendList = searchResult.getFriendList();
                            for (UserInfo friend : friendList) {
                                mFilterFriendList.add(friend);
                            }

                            List<GroupInfo> groupList = searchResult.getGroupList();
                            for (GroupInfo group : groupList) {
                                mFilterGroupList.add(group);
                            }

                            if (mFilterFriendList.size() == 0 && mFilterGroupList.size() == 0) {
                                if (mFilterString.equals("")) {
                                    mSearchNoResultsTextView.setVisibility(View.GONE);
                                } else {
                                    mSearchNoResultsTextView.setVisibility(View.VISIBLE);
                                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                    spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_pre));
                                    SpannableStringBuilder colorFilterStr = new SpannableStringBuilder(mFilterString);
                                    colorFilterStr.setSpan(new ForegroundColorSpan(Color.parseColor("#2DD0CF")), 0, mFilterString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                                    spannableStringBuilder.append(colorFilterStr);
                                    spannableStringBuilder.append(getResources().getString(R.string.ac_search_no_result_suffix));
                                    mSearchNoResultsTextView.setText(spannableStringBuilder);
                                }
                            } else {
                                mSearchNoResultsTextView.setVisibility(View.GONE);
                            }
                            if (mFilterFriendList.size() > 0) {
                                mFriendListLinearLayout.setVisibility(View.VISIBLE);
                                FriendListAdapter friendListAdapter = new FriendListAdapter(mFilterFriendList, mFilterString);
                                mFriendListView.setAdapter(friendListAdapter);
                                if (mFilterFriendList.size() > 3) {
                                    mMoreFriendLinearLayout.setVisibility(View.VISIBLE);
                                } else {
                                    mMoreFriendLinearLayout.setVisibility(View.GONE);
                                }
                            } else {
                                mFriendListLinearLayout.setVisibility(View.GONE);
                            }

                            if (mFilterGroupList.size() > 0) {
                                mGroupListLinearLayout.setVisibility(View.VISIBLE);
                                SearchGroupListAdapter groupListAdapter = new SearchGroupListAdapter(mFilterGroupList);
                                mGroupsListView.setAdapter(groupListAdapter);
                                if (mFilterGroupList.size() > 3) {
                                    mMoreGroupsLinearLayout.setVisibility(View.VISIBLE);
                                } else {
                                    mMoreGroupsLinearLayout.setVisibility(View.GONE);
                                }
                            } else {
                                mGroupListLinearLayout.setVisibility(View.GONE);
                            }
                        }

                    }
                }.executeOnExecutor(mExecutor, s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
                    filterInfo(String.valueOf(mSearchEditText.getText()));
                    return true;
                }
                return false;
            }
        });

        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mSearchEditText.getRight() - 2 * mSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        filterInfo("");
                        mSearchEditText.setText("");
                        mSearchEditText.clearFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        mPressBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initListener() {
        //转发消息
        if (getIntent().getFlags() == 1) {
            mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof UserInfo) {
                        UserInfo userInfo = (UserInfo) selectObject;
                        DialogCreator.createForwardMsg(SearchContactsActivity.this, mWidth, true, null, null, user.get(userInfo.getUserName()), userInfo);
                    }
                }
            });
            mGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof GroupInfo) {
                        GroupInfo groupInfo = (GroupInfo) selectObject;
                        DialogCreator.createForwardMsg(SearchContactsActivity.this, mWidth, false, null, groupInfo, group.get(groupInfo.getGroupID()), null);
                    }
                }
            });
            //发送名片
        } else if (getIntent().getFlags() == 2) {
            mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof UserInfo) {
                        final UserInfo userInfo = (UserInfo) selectObject;
                        Intent intent = getIntent();
                        setSearchContactsBusiness(SearchContactsActivity.this, mWidth, intent, null, userInfo);
                    }
                }
            });
            mGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof GroupInfo) {
                        final GroupInfo groupInfo = (GroupInfo) selectObject;
                        Intent intent = getIntent();
                        setSearchContactsBusiness(SearchContactsActivity.this, mWidth, intent, groupInfo, null);
                    }
                }
            });
        } else {
            mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof UserInfo) {
//                        Intent intent = new Intent(SearchContactsActivity.this, FriendInfoActivity.class);
                        UserInfo friend = (UserInfo) selectObject;
//                        intent.putExtra(JGApplication.TARGET_ID, friend.getUserName());
//                        intent.putExtra(JGApplication.TARGET_APP_KEY, friend.getAppKey());
//                        intent.putExtra("fromSearch", true);
//                        startActivity(intent);

                        //
                        Intent intent = new Intent();
                        intent.setAction("o2_person");
                        intent.setClassName("net.zoneland.x.bpm.mobile.v1.zoneXBPM", "net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity");
                        intent.putExtra("name", friend.getUserName());
                        startActivity(intent);

                        finish();
                    }
                }
            });
            mGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object selectObject = parent.getItemAtPosition(position);
                    if (selectObject instanceof GroupInfo) {
                        Intent intent = new Intent(SearchContactsActivity.this, ChatActivity.class);
                        GroupInfo groupInfo = (GroupInfo) selectObject;
                        long groupID = groupInfo.getGroupID();

                        Conversation conversation = JMessageClient.getGroupConversation(groupID);
                        if (conversation == null) {
                            conversation = Conversation.createGroupConversation(groupID);
                            EventBus.getDefault().post(new Event.Builder()
                                    .setType(EventType.createConversation)
                                    .setConversation(conversation)
                                    .build());
                        }
                        intent.putExtra(JGApplication.GROUP_ID, groupID);
                        intent.putExtra(JGApplication.CONV_TITLE, conversation.getTitle());
                        startActivity(intent);
                    }
                }
            });
        }

        //搜索出的好友数量超过三条 点击加载更多
        mMoreFriendLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchContactsActivity.this, SearchMoreFriendsActivity.class);
                intent.putExtra("filterString", mFilterString);
                if (getIntent().getFlags() == 1) {
                    intent.putExtra("forwardMsg", true);
                } else if (getIntent().getFlags() == 2) {
                    intent.putExtra("businessCard", true);
                    intent.putExtra("userName", getIntent().getStringExtra("userName"));
                    intent.putExtra("appKey", getIntent().getStringExtra("appKey"));
                    intent.putExtra("avatar", getIntent().getStringExtra("avatar"));
                }
                startActivity(intent);
            }
        });
        //搜索出的群组数量超过三条,点击加载更多
        mMoreGroupsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchContactsActivity.this, SearchMoreGroupActivity.class);
                intent.putExtra("filterString", mFilterString);
                if (getIntent().getFlags() == 1) {
                    intent.putExtra("forwardMsg", true);
                } else if (getIntent().getFlags() == 2) {
                    intent.putExtra("businessCard", true);
                    intent.putExtra("userName", getIntent().getStringExtra("userName"));
                    intent.putExtra("appKey", getIntent().getStringExtra("appKey"));
                    intent.putExtra("avatar", getIntent().getStringExtra("avatar"));
                }
                startActivity(intent);
            }
        });
    }

    public void setSearchContactsBusiness(final SearchContactsActivity context, int width,
                                          final Intent intent, final GroupInfo groupInfo, final UserInfo userInfo) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.btn_cancel) {
                    mDialog.dismiss();

                } else if (i == R.id.btn_sure) {
                    mDialog.dismiss();
                    //把名片的userName和appKey通过extra发送给对方
                    TextContent content = new TextContent("推荐了一张名片");
                    content.setStringExtra("userName", intent.getStringExtra("userName"));
                    content.setStringExtra("appKey", intent.getStringExtra("appKey"));
                    content.setStringExtra("businessCard", "businessCard");
                    Conversation conversation;
                    if (userInfo == null) {
                        conversation = JMessageClient.getGroupConversation(groupInfo.getGroupID());
                        if (conversation == null) {
                            conversation = Conversation.createGroupConversation(groupInfo.getGroupID());
                            EventBus.getDefault().post(new Event.Builder()
                                    .setType(EventType.createConversation)
                                    .setConversation(conversation)
                                    .build());
                        }
                    } else {
                        conversation = JMessageClient.getSingleConversation(userInfo.getUserName(), userInfo.getAppKey());
                        if (conversation == null) {
                            conversation = Conversation.createSingleConversation(userInfo.getUserName(), userInfo.getAppKey());
                            EventBus.getDefault().post(new Event.Builder()
                                    .setType(EventType.createConversation)
                                    .setConversation(conversation)
                                    .build());
                        }
                    }

                    Message textMessage = conversation.createSendMessage(content);
                    MessageSendingOptions options = new MessageSendingOptions();
                    options.setNeedReadReceipt(false);
                    JMessageClient.sendMessage(textMessage, options);
                    textMessage.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                            } else {
                                HandleResponseCode.onHandle(context, i, false);
                            }
                        }
                    });

                }
            }
        };
        String name;
        if (userInfo == null) {
            name = groupInfo.getGroupName();
        } else {
            name = userInfo.getDisplayName();
        }
        mDialog = DialogCreator.createBusinessCardDialog(context, listener, name,
                intent.getStringExtra("userName"), intent.getStringExtra("avatar"));
        mDialog.getWindow().setLayout((int) (0.8 * width), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }

    private class FriendListAdapter extends BaseAdapter {
        private List<UserInfo> filterFriendList;
        private String mFilterString;

        public FriendListAdapter(List<UserInfo> filterFriendList, String filterString) {
            this.filterFriendList = filterFriendList;
            this.mFilterString = filterString;
        }

        @Override
        public int getCount() {
            if (filterFriendList != null) {
                return filterFriendList.size() > 3 ? 3 : filterFriendList.size();
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            final UserInfo friendInfo = (UserInfo) getItem(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(SearchContactsActivity.this, R.layout.item_filter_friend_list, null);
                viewHolder.portraitImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.item_aiv_friend_image);//头像
                viewHolder.nameSingleTextView = (TextView) convertView.findViewById(R.id.item_tv_friend_name_single);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (friendInfo != null) {
                viewHolder.nameSingleTextView.setVisibility(View.VISIBLE);
                String noteName = friendInfo.getNotename();
                String nickName = friendInfo.getNickname();
                String userName = friendInfo.getUserName();
                String name = "";
                if (TextSearcher.contains(false, noteName, mFilterString)) {
                    name = noteName;
                } else if (TextSearcher.contains(false, nickName, mFilterString)) {
                    name = nickName;
                } else if (TextSearcher.contains(false, userName, mFilterString)) {
                    name = userName;
                }
                friendInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseMessage, Bitmap avatarBitmap) {
                        if (responseCode == 0) {
                            viewHolder.portraitImageView.setImageBitmap(avatarBitmap);
                        } else {
                            //没有头像给个默认的
                            viewHolder.portraitImageView.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
                user.put(userName, name);
                viewHolder.nameSingleTextView.setText(mCharacterParser.getColoredName(mFilterString, name));
            }

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            if (filterFriendList == null)
                return null;

            if (position >= filterFriendList.size())
                return null;

            return filterFriendList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    class ViewHolder {
        SelectableRoundedImageView portraitImageView;
        TextView nameSingleTextView;
    }

    public class SearchGroupListAdapter extends BaseAdapter {
        private List<GroupInfo> filterGroupInfoList;

        public SearchGroupListAdapter(ArrayList<GroupInfo> filterGroupList) {
            this.filterGroupInfoList = filterGroupList;
        }

        @Override
        public int getCount() {
            if (filterGroupInfoList != null) {
                return filterGroupInfoList.size() > 3 ? 3 : filterGroupInfoList.size();
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupViewHolder viewHolder;
            GroupInfo groupInfo = (GroupInfo) getItem(position);
            String groupName;
            if (convertView == null) {
                viewHolder = new GroupViewHolder();
                convertView = View.inflate(SearchContactsActivity.this, R.layout.item_filter_group_list, null);
                viewHolder.portraitImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.item_iv_group_image);
                viewHolder.nameSingleTextView = (TextView) convertView.findViewById(R.id.item_tv_group_name_single);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GroupViewHolder) convertView.getTag();
            }
            if (groupInfo != null) {
                viewHolder.nameSingleTextView.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(groupInfo.getGroupName())) {
                    //Conversation groupConversation = JMessageClient.getGroupConversation(groupId);
                    //群组名是null的话,手动拿出5个名字拼接
                    List<UserInfo> groupMembers = groupInfo.getGroupMembers();
                    StringBuilder builder = new StringBuilder();
                    if (groupMembers.size() <= 5) {
                        groupName = getGroupName(groupMembers, builder);
                    } else {
                        List<UserInfo> newGroupMember = groupMembers.subList(0, 5);
                        groupName = getGroupName(newGroupMember, builder);
                    }
                } else {
                    groupName = groupInfo.getGroupName();
                }
                group.put(groupInfo.getGroupID(), groupName);
                viewHolder.nameSingleTextView.setText(mCharacterParser.getColoredGroupName(mFilterString, groupName));

            } else {
                viewHolder.nameSingleTextView.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            if (filterGroupInfoList == null)
                return null;

            if (position >= filterGroupInfoList.size())
                return null;

            return filterGroupInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private String getGroupName(List<UserInfo> groupMembers, StringBuilder builder) {
            for (UserInfo info : groupMembers) {
                String noteName = info.getDisplayName();
                builder.append(noteName);
                builder.append(",");
            }

            return builder.substring(0, builder.lastIndexOf(","));
        }
    }

    class GroupViewHolder {
        SelectableRoundedImageView portraitImageView;
        TextView nameSingleTextView;
    }


    private void initData() {
        mExecutor = new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCharacterParser = CharacterParser.getInstance();
    }

    private SearchResult filterInfo(String filterStr) {
        allUser.clear();
        SearchResult searchResult = new SearchResult();
        List<GroupInfo> filterGroup = new ArrayList<>();
        List<UserInfo> filterFriend = new ArrayList<>();

        if (filterStr.equals("")) {
            SearchResult result = new SearchResult();
            result.setFilterStr("");
            result.setFriendList(filterFriend);
            result.setGroupList(filterGroup);
            return result;
        }
        if (filterStr.equals("'")) {
            SearchResult result = new SearchResult();
            result.setFriendList(filterFriend);
            result.setGroupList(filterGroup);
            return result;
        }

        //所有的群组
        List<GroupInfo> mGroupInfoList = JGApplication.mGroupInfoList;
        String groupName;
        for (GroupInfo groupInfo : mGroupInfoList) {
            if (TextUtils.isEmpty(groupInfo.getGroupName())) {
                //Conversation groupConversation = JMessageClient.getGroupConversation(groupId);
                //群组名是null的话,手动拿出5个名字拼接
                List<UserInfo> groupMembers = groupInfo.getGroupMembers();
                StringBuilder builder = new StringBuilder();
                if (groupMembers.size() <= 5) {
                    groupName = getGroupName(groupMembers, builder);
                } else {
                    List<UserInfo> newGroupMember = groupMembers.subList(0, 5);
                    groupName = getGroupName(newGroupMember, builder);
                }
            } else {
                groupName = groupInfo.getGroupName();
            }

            if (TextSearcher.contains(false, groupName, filterStr)) {
                //如果群组名中包含输入的字符,就把这个群组的groupinfo加入list
                filterGroup.add(groupInfo);
            }
        }

        //所有的单聊会话非好友名单
        List<Conversation> conversationList = JMessageClient.getConversationList();
        for (Conversation conv : conversationList) {
            if (conv.getType() == ConversationType.single) {
                UserInfo convUserInfo = (UserInfo) conv.getTargetInfo();
                if (!convUserInfo.isFriend()) {
                    allUser.add(convUserInfo);
                }
            }
        }

        //所有好友名单
        allUser.addAll(JGApplication.mFriendInfoList);

        for (UserInfo friendInfo : allUser) {
            String userName = friendInfo.getUserName();
            String noteName = friendInfo.getNotename();
            String nickName = friendInfo.getNickname();
            //如果好友名 包含 搜索内容 就把这个人的userinfo添加
            if (TextSearcher.contains(false, noteName, filterStr) ||
                    TextSearcher.contains(false, nickName, filterStr) ||
                    TextSearcher.contains(false, userName, filterStr)) {
                filterFriend.add(friendInfo);
            }
        }

        searchResult.setFilterStr(filterStr);
        searchResult.setGroupList(filterGroup);
        searchResult.setFriendList(filterFriend);

        return searchResult;
    }

    private String getGroupName(List<UserInfo> groupMembers, StringBuilder builder) {
        for (UserInfo info : groupMembers) {
            String noteName = info.getDisplayName();
            builder.append(noteName);
            builder.append(",");
        }

        return builder.substring(0, builder.lastIndexOf(","));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        mSearchEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mSearchEditText, 0);
        super.onResume();
    }

    @Override
    protected void onPause() {
        allUser.clear();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
