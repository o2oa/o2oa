package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.adapter.SearchGroupListAdapter;
import jiguang.chat.application.JGApplication;
import jiguang.chat.controller.ActivityController;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.model.SearchResult;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.query.TextSearcher;

/**
 * Created by ${chenyn} on 2017/5/2.
 */

public class SearchMoreGroupActivity extends BaseActivity {

    private EditText mSearchEditText;
    private ListView mGroupsListView;
    private TextView mSearchNoResultsTextView;
    private LinearLayout mPressBackImageView;
    private LinearLayout mGroupListResultsLinearLayout;

    private String mFilterString;

    private AsyncTask mAsyncTask;
    private ThreadPoolExecutor mExecutor;
    private boolean isForwardMsg;
    private boolean isBusinessCard;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_groups_info);
        ActivityController.addActivity(this);

        Intent intent = getIntent();
        mFilterString = intent.getStringExtra("filterString");
        isForwardMsg = intent.getBooleanExtra("forwardMsg", false);
        isBusinessCard = intent.getBooleanExtra("businessCard", false);

        initView();
        initData();
    }

    public void initView() {
        mSearchEditText = (EditText) findViewById(R.id.ac_et_search);
        mGroupsListView = (ListView) findViewById(R.id.ac_lv_group_list_detail_info);
        mSearchNoResultsTextView = (TextView) findViewById(R.id.ac_tv_search_no_results);
        mPressBackImageView = (LinearLayout) findViewById(R.id.ac_iv_press_back);
        mGroupListResultsLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_group_list_result);
    }

    public void initData() {
        mExecutor = new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilterString = s.toString();
                mAsyncTask = new AsyncTask<String, Void, SearchResult>() {

                    @Override
                    protected SearchResult doInBackground(String... params) {
                        return filterInfo(mFilterString);
                    }

                    @Override
                    protected void onPostExecute(SearchResult searchResult) {

                        if (searchResult.getFilterStr().equals(mFilterString)) {

                            List<GroupInfo> filterGroupId = searchResult.getGroupList();
                            if (filterGroupId.size() > 0) {
                                mGroupListResultsLinearLayout.setVisibility(View.VISIBLE);
                                mGroupsListView.setVisibility(View.VISIBLE);
                                SearchGroupListAdapter groupListAdapter = new SearchGroupListAdapter(SearchMoreGroupActivity.this, filterGroupId, mFilterString);
                                mGroupsListView.setAdapter(groupListAdapter);
                            } else {
                                mGroupListResultsLinearLayout.setVisibility(View.GONE);
                                mGroupsListView.setVisibility(View.GONE);
                            }
                            if (mFilterString.equals("")) {
                                mSearchNoResultsTextView.setVisibility(View.GONE);
                            }
                            if (filterGroupId.size() == 0) {
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
                        }
                    }
                }.executeOnExecutor(mExecutor, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSearchEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mSearchEditText.getRight() - 2 * mSearchEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
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
                SearchMoreGroupActivity.this.finish();
            }
        });

        mGroupsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selectObject = parent.getItemAtPosition(position);
                if (selectObject instanceof GroupInfo) {
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
                    if (isForwardMsg) {
                        DialogCreator.createForwardMsg(SearchMoreGroupActivity.this, mWidth, false, null, groupInfo, conversation.getTitle(), null);
                    }else if (isBusinessCard) {
                        setSearchContactsBusiness(getIntent(), groupInfo, null);
                    } else {
                        final Intent intent = new Intent(SearchMoreGroupActivity.this, ChatActivity.class);
                        intent.putExtra(JGApplication.GROUP_ID, groupID);
                        intent.putExtra(JGApplication.MEMBERS_COUNT, groupInfo.getGroupMembers().size());
                        intent.putExtra(JGApplication.CONV_TITLE, conversation.getTitle());
                        startActivity(intent);
                    }
                }
            }
        });

        mSearchEditText.setText(mFilterString);
    }

    public void setSearchContactsBusiness(final Intent intent, final GroupInfo groupInfo, final UserInfo userInfo) {
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
                                    Toast.makeText(SearchMoreGroupActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    HandleResponseCode.onHandle(SearchMoreGroupActivity.this, i, false);
                                }
                            }
                        });
                        break;
                }
            }
        };
        String name;
        if (userInfo == null) {
            name = groupInfo.getGroupName();
        } else {
            name = userInfo.getDisplayName();
        }
        mDialog = DialogCreator.createBusinessCardDialog(SearchMoreGroupActivity.this, listener, name,
                intent.getStringExtra("userName"), intent.getStringExtra("avatar"));
        mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.show();
    }


    private synchronized SearchResult filterInfo(String filterStr) {
        SearchResult searchResult = new SearchResult();
        List<GroupInfo> groupInfoList = new ArrayList<>();

        if (filterStr.equals("")) {
            SearchResult result = new SearchResult();
            result.setFilterStr("");
            result.setGroupList(groupInfoList);
            return result;
        }
        if (filterStr.contains("'")) {
            SearchResult result = new SearchResult();
            result.setFilterStr(filterStr);
            result.setGroupList(groupInfoList);
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

            if (TextSearcher.contains(false, groupName, mFilterString)) {
                //如果群组名中包含输入的字符,就把这个群组的groupinfo加入list
                groupInfoList.add(groupInfo);
            }
        }
        searchResult.setGroupList(groupInfoList);
        searchResult.setFilterStr(filterStr);
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
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        super.onDestroy();
    }
}
