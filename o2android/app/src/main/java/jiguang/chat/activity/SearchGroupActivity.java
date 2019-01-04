package jiguang.chat.activity;

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
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.application.JGApplication;
import jiguang.chat.model.SearchResult;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;
import jiguang.chat.utils.pinyin.CharacterParser;
import jiguang.chat.utils.query.TextSearcher;

/**
 * Created by ${chenyn} on 2017/6/22.
 */

public class SearchGroupActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mIvBack;
    private ImageView mIvClear;
    private EditText mEtSearch;
    private ListView mLv_searchGroup;
    private NetworkReceiver mReceiver;
    private TextView mNoConnect;
    private ArrayList<UserInfo> mFilterFriendList;
    private AsyncTask mAsyncTask;
    private ThreadPoolExecutor mExecutor;
    private CharacterParser mCharacterParser;
    private String mFilterString;
    private TextView mSearchNoResultsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group);
        initReceiver();
        initView();
        initData();
        initListener();
    }

    private void initReceiver() {
        mReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_iv_press_back:
                finish();
                break;
            case R.id.iv_clear:
                mEtSearch.setText("");
                break;
            default:
                break;
        }
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
                } else {
                    mNoConnect.setVisibility(View.GONE);
                }
            }
        }

    }

    private void initView() {
        mIvBack = (LinearLayout) findViewById(R.id.ac_iv_press_back);
        mIvClear = (ImageView) findViewById(R.id.iv_clear);
        mEtSearch = (EditText) findViewById(R.id.ac_et_search);
        mNoConnect = (TextView) findViewById(R.id.no_connect);
        mSearchNoResultsTextView = (TextView) findViewById(R.id.ac_tv_search_no_results);


        mIvBack.setOnClickListener(this);
        mIvClear.setOnClickListener(this);

        mLv_searchGroup = (ListView) findViewById(R.id.lv_searchGroup);
    }

    private void initData() {
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFilterFriendList = new ArrayList<>();
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

                            if (mFilterFriendList.size() == 0) {
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
                                mLv_searchGroup.setVisibility(View.VISIBLE);
                                FriendListAdapter friendListAdapter = new FriendListAdapter(mFilterFriendList);
                                mLv_searchGroup.setAdapter(friendListAdapter);
                            } else {
                                mLv_searchGroup.setVisibility(View.GONE);
                            }
                        }
                    }
                }.executeOnExecutor(mExecutor, s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mExecutor = new ThreadPoolExecutor(3, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mCharacterParser = CharacterParser.getInstance();
    }

    private SearchResult filterInfo(String filterStr) {
        SearchResult searchResult = new SearchResult();
        List<UserInfo> filterFriend = new ArrayList<>();

        if (filterStr.equals("")) {
            SearchResult result = new SearchResult();
            result.setFilterStr("");
            result.setFriendList(filterFriend);
            return result;
        }
        if (filterStr.equals("'")) {
            SearchResult result = new SearchResult();
            result.setFriendList(filterFriend);
            return result;
        }

        //所有好友名单
        for (UserInfo friendInfo : JGApplication.mSearchGroup) {
            //如果好友名 包含 搜索内容 就把这个人的userinfo添加
            if (TextSearcher.contains(false, friendInfo.getNickname(), filterStr) ||
                    TextSearcher.contains(false, friendInfo.getNotename(), filterStr) ||
                    TextSearcher.contains(false, friendInfo.getUserName(), filterStr)) {
                filterFriend.add(friendInfo);
            }
        }
        searchResult.setFilterStr(filterStr);
        searchResult.setFriendList(filterFriend);

        return searchResult;
    }

    private class FriendListAdapter extends BaseAdapter {
        private List<UserInfo> filterFriendList;

        public FriendListAdapter(List<UserInfo> filterFriendList) {
            this.filterFriendList = filterFriendList;
        }

        @Override
        public int getCount() {
            return filterFriendList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            final UserInfo friendInfo = (UserInfo) getItem(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(SearchGroupActivity.this, R.layout.item_filter_friend_list, null);
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
                if (TextSearcher.contains(false, friendInfo.getNotename(), mFilterString)) {
                    name = noteName;
                } else if (TextSearcher.contains(false, friendInfo.getNickname(), mFilterString)) {
                    name = nickName;
                } else if (TextSearcher.contains(false, friendInfo.getUserName(), mFilterString)) {
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

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void initListener() {
        final Intent intent = new Intent();
        mLv_searchGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAtPosition = parent.getItemAtPosition(position);
                if (itemAtPosition instanceof UserInfo) {
                    UserInfo info = ((UserInfo) itemAtPosition);
                    if (info.isFriend()) {
                        intent.setClass(SearchGroupActivity.this, FriendInfoActivity.class);
                        intent.putExtra("fromSearch", true);
                    } else {
                        intent.setClass(SearchGroupActivity.this, GroupNotFriendActivity.class);
                    }
                    intent.putExtra(JGApplication.TARGET_ID, info.getUserName());
                    intent.putExtra(JGApplication.TARGET_APP_KEY, info.getAppKey());
                    startActivity(intent);
                }
            }
        });
    }
}
