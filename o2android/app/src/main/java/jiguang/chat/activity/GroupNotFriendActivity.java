package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;

/**
 * Created by ${chenyn} on 2017/5/10.
 */

public class GroupNotFriendActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIv_friendPhoto;
    private TextView mTv_noteName;
    private TextView mTv_sign;
    private TextView mTv_userName;
    private TextView mTv_gender;
    private TextView mTv_birthday;
    private TextView mTv_address;
    private Button mBtn_add_friend;
    private Button mBtn_send_message;
    private String mUserName;
    private UserInfo mUserInfo;
    private String mMyName;
    private RelativeLayout mRl_NickName;
    private TextView mTv_NickName;
    private String mNickName;
    private String mAvatarPath;
    private TextView mTv_additionalMsg;
    private LinearLayout mLl_additional;
    private ImageButton mReturnBtn;
    private ImageView mIvMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_not_friend);

        initView();
        initData();
    }

    private void initData() {
        final Dialog dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading));
        dialog.show();
        mUserName = getIntent().getStringExtra(JGApplication.TARGET_ID);
        String reason = getIntent().getStringExtra("reason");
        if (reason == null) {
            mLl_additional.setVisibility(View.GONE);
        } else {
            mLl_additional.setVisibility(View.VISIBLE);
            mTv_additionalMsg.setText("附加消息: " + reason);
        }
        JMessageClient.getUserInfo(mUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                if (responseCode == 0) {
                    mUserInfo = info;
                    File avatar = info.getAvatarFile();
                    if (avatar != null) {
                        mAvatarPath = avatar.getAbsolutePath();
                        mIv_friendPhoto.setImageBitmap(BitmapFactory.decodeFile(mAvatarPath));
                    } else {
                        mIv_friendPhoto.setImageResource(R.drawable.rc_default_portrait);
                    }
                    String noteName = info.getNotename();
                    mNickName = info.getNickname();
                    //有备注 有昵称
                    mTv_userName.setText(mUserName);
                    if (!TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(mNickName)) {
                        mRl_NickName.setVisibility(View.VISIBLE);
                        mTv_NickName.setText(mNickName);
                        mTv_noteName.setText("备注名: " + noteName);
                    }
                    //没有备注 有昵称
                    else if (TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(mNickName)) {
                        mRl_NickName.setVisibility(View.GONE);
                        mTv_noteName.setText("昵称: " + mNickName);
                    }
                    //有备注 没有昵称
                    else if (!TextUtils.isEmpty(noteName) && TextUtils.isEmpty(mNickName)) {
                        mRl_NickName.setVisibility(View.VISIBLE);
                        mTv_NickName.setText(info.getNickname());
                        mTv_noteName.setText("备注名: " + noteName);
                    }
                    //没有备注名 没有昵称
                    else {
                        mRl_NickName.setVisibility(View.GONE);
                        mTv_noteName.setText("用户名: " + mUserName);
                    }
                    mTv_sign.setText(info.getSignature());
                    if (info.getGender() == UserInfo.Gender.male) {
                        mTv_gender.setText("男");
                    } else if (info.getGender() == UserInfo.Gender.female) {
                        mTv_gender.setText("女");
                    } else {
                        mTv_gender.setText("未知");
                    }
                    mTv_birthday.setText(getBirthday(info));
                    mTv_address.setText(info.getRegion());
                }
                dialog.dismiss();
            }
        });

        UserInfo myInfo = JMessageClient.getMyInfo();
        mMyName = myInfo.getNickname();
        if (TextUtils.isEmpty(mMyName)) {
            mMyName = myInfo.getUserName();
        }
    }

    private void initView() {
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mIvMore = (ImageView) findViewById(R.id.iv_more);
        mIv_friendPhoto = (ImageView) findViewById(R.id.iv_friendPhoto);
        mTv_noteName = (TextView) findViewById(R.id.tv_nickName);
        mTv_sign = (TextView) findViewById(R.id.tv_sign);
        mTv_userName = (TextView) findViewById(R.id.tv_userName);
        mTv_gender = (TextView) findViewById(R.id.tv_gender);
        mTv_birthday = (TextView) findViewById(R.id.tv_birthday);
        mTv_address = (TextView) findViewById(R.id.tv_address);
        mBtn_add_friend = (Button) findViewById(R.id.btn_add_friend);
        mBtn_send_message = (Button) findViewById(R.id.btn_send_message);
        mRl_NickName = (RelativeLayout) findViewById(R.id.rl_nickName);
        mTv_NickName = (TextView) findViewById(R.id.tv_nick);

        mTv_additionalMsg = (TextView) findViewById(R.id.tv_additionalMsg);
        mLl_additional = (LinearLayout) findViewById(R.id.ll_additional);

        mBtn_add_friend.setOnClickListener(this);
        mBtn_send_message.setOnClickListener(this);
        mReturnBtn.setOnClickListener(this);
        mIvMore.setOnClickListener(this);
    }

    public String getBirthday(UserInfo info) {
        long birthday = info.getBirthday();
        Date date = new Date(birthday);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return dateFormat.format(date);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_add_friend:
                if (mUserInfo.isFriend()) {
                    ToastUtil.shortToast(GroupNotFriendActivity.this, "对方已经是你的好友");
                } else {
                    intent.setClass(GroupNotFriendActivity.this, VerificationActivity.class);
                    //对方信息
                    intent.putExtra("detail_add_friend", mUserName);
                    intent.putExtra("detail_add_nick_name", mNickName);
                    intent.putExtra("detail_add_avatar_path", mAvatarPath);
                    //自己的昵称或者是用户名
                    intent.putExtra("detail_add_friend_my_nickname", mMyName);
                    intent.setFlags(1);
                    startActivity(intent);
                }
                break;
            case R.id.btn_send_message:
                intent.setClass(GroupNotFriendActivity.this, ChatActivity.class);
                //创建会话
                intent.putExtra(JGApplication.TARGET_ID, mUserInfo.getUserName());
                intent.putExtra(JGApplication.TARGET_APP_KEY, mUserInfo.getAppKey());
                String notename = mUserInfo.getNotename();
                if (TextUtils.isEmpty(notename)) {
                    notename = mUserInfo.getNickname();
                    if (TextUtils.isEmpty(notename)) {
                        notename = mUserInfo.getUserName();
                    }
                }
                intent.putExtra(JGApplication.CONV_TITLE, notename);
                Conversation conv = JMessageClient.getSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
                //如果会话为空，使用EventBus通知会话列表添加新会话
                if (conv == null) {
                    conv = Conversation.createSingleConversation(mUserInfo.getUserName(), mUserInfo.getAppKey());
                    EventBus.getDefault().post(new Event.Builder()
                            .setType(EventType.createConversation)
                            .setConversation(conv)
                            .build());
                }
                startActivity(intent);
                break;
            case R.id.return_btn:
                finish();
                break;
            case R.id.iv_more:
                intent.setClass(GroupNotFriendActivity.this, NotFriendSettingActivity.class);
                intent.putExtra("notFriendUserName", mUserName);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
