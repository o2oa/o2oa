package jiguang.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.application.JGApplication;
import jiguang.chat.model.InfoModel;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.dialog.LoadDialog;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;

/**
 * Created by ${chenyn} on 2017/3/13.
 */

public class SearchForAddFriendActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEt_searchUser;
    private Button mBtn_search;
    private LinearLayout mSearch_result;
    private SelectableRoundedImageView mSearch_header;
    private TextView mSearch_name;
    private Button mSearch_addBtn;
    private ImageView mIv_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_add_friend);

        initView();
        initListener();
    }

    private void initListener() {
        mEt_searchUser.addTextChangedListener(new TextChange());
        mBtn_search.setOnClickListener(this);
        mSearch_result.setOnClickListener(this);
        mSearch_addBtn.setOnClickListener(this);
        mIv_clear.setOnClickListener(this);
    }

    private void initView() {
        mEt_searchUser = (EditText) findViewById(R.id.et_searchUser);
        mBtn_search = (Button) findViewById(R.id.btn_search);
        mSearch_result = (LinearLayout) findViewById(R.id.search_result);
        mSearch_header = (SelectableRoundedImageView) findViewById(R.id.search_header);
        mSearch_name = (TextView) findViewById(R.id.search_name);
        mSearch_addBtn = (Button) findViewById(R.id.search_addBtn);
        mIv_clear = (ImageView) findViewById(R.id.iv_clear);
        mBtn_search.setEnabled(false);
        Intent intent = getIntent();
        if (intent.getFlags() == 2) {
            initTitle(true, true, "发起单聊", "", false, "");
            mSearch_addBtn.setVisibility(View.GONE);
        } else {
            initTitle(true, true, "添加好友", "", false, "");
        }
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_search:
                hintKbTwo();
                String searchUserName = mEt_searchUser.getText().toString();
                if (!TextUtils.isEmpty(searchUserName)) {
                    LoadDialog.show(this);
                    JMessageClient.getUserInfo(searchUserName, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            LoadDialog.dismiss(SearchForAddFriendActivity.this);
                            if (responseCode == 0) {
                                InfoModel.getInstance().friendInfo = info;
                                mSearch_result.setVisibility(View.VISIBLE);
                                //已经是好友则不显示"加好友"按钮
                                if (info.isFriend()) {
                                    mSearch_addBtn.setVisibility(View.GONE);
                                    //如果是发起单聊.那么不能显示加好友按钮
                                } else if (!info.isFriend() && getIntent().getFlags() != 2) {
                                    mSearch_addBtn.setVisibility(View.VISIBLE);
                                }
                                //这个接口会在本地寻找头像文件,不存在就异步拉取
                                File avatarFile = info.getAvatarFile();
                                if (avatarFile != null) {
                                    mSearch_header.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
                                    InfoModel.getInstance().setBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
                                } else {
                                    mSearch_header.setImageResource(R.drawable.rc_default_portrait);
                                    InfoModel.getInstance().setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rc_default_portrait));
                                }
                                mSearch_name.setText(TextUtils.isEmpty(info.getNickname()) ? info.getUserName() : info.getNickname());
                            } else {
                                ToastUtil.shortToast(SearchForAddFriendActivity.this, "该用户不存在");
                                mSearch_result.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                break;
            case R.id.search_result:
                //详细资料
                if (InfoModel.getInstance().isFriend()) {
                    //已经是好友
                    intent.setClass(SearchForAddFriendActivity.this, FriendInfoActivity.class);
                    intent.putExtra("addFriend", true);
                    intent.putExtra("targetId", InfoModel.getInstance().friendInfo.getUserName());
                    //直接发起单聊
                } else if (getIntent().getFlags() == 2){
                    intent.setClass(SearchForAddFriendActivity.this, GroupNotFriendActivity.class);
                    intent.putExtra(JGApplication.TARGET_ID, InfoModel.getInstance().friendInfo.getUserName());
                    intent.putExtra(JGApplication.TARGET_APP_KEY, InfoModel.getInstance().friendInfo.getAppKey());
                }else {
                    //添加好友
                    intent.setClass(SearchForAddFriendActivity.this, SearchFriendInfoActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.search_addBtn:
                //添加申请
                intent.setClass(SearchForAddFriendActivity.this, VerificationActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_clear:
                mEt_searchUser.setText("");
                break;
        }
    }


    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
            boolean feedback = mEt_searchUser.getText().length() > 0;
            if (feedback) {
                mIv_clear.setVisibility(View.VISIBLE);
                mBtn_search.setEnabled(true);
            } else {
                mIv_clear.setVisibility(View.GONE);
                mBtn_search.setEnabled(false);
            }
        }
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
