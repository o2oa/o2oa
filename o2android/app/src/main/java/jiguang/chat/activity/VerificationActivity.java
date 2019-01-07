package jiguang.chat.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.database.FriendRecommendEntry;
import jiguang.chat.database.UserEntry;
import jiguang.chat.entity.FriendInvitation;
import jiguang.chat.model.InfoModel;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;

/**
 * Created by ${chenyn} on 2017/3/14.
 */

public class VerificationActivity extends BaseActivity {

    private EditText mEt_reason;
    private UserInfo mMyInfo;
    private String mTargetAppKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        initView();
        initData();
    }

    private void initData() {
        mEt_reason.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendAddReason();
                }
                return false;
            }
        });

        mJmui_commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddReason();
            }
        });
    }

    private void sendAddReason() {
        final String userName;
        String displayName;
        String targetAvatar;
        Long targetUid;
        if (getIntent().getFlags() == 1) {
            //添加好友申请时对方信息
            userName = getIntent().getStringExtra("detail_add_friend");
            displayName = getIntent().getStringExtra("detail_add_nick_name");
            targetAvatar = getIntent().getStringExtra("detail_add_avatar_path");
            targetUid = getIntent().getLongExtra("detail_add_uid", 0);
            if (TextUtils.isEmpty(displayName)) {
                displayName = userName;
            }
            //搜索方式添加好友
        } else {
            targetAvatar = InfoModel.getInstance().getAvatarPath();
            displayName = InfoModel.getInstance().getNickName();
            targetUid = InfoModel.getInstance().getUid();
            if (TextUtils.isEmpty(displayName)) {
                displayName = InfoModel.getInstance().getUserName();
            }
            userName = InfoModel.getInstance().getUserName();
        }
        final String reason = mEt_reason.getText().toString();
        final String finalTargetAvatar = targetAvatar;
        final String finalDisplayName = displayName;
        final Long finalUid = targetUid;
        final Dialog dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading));
        dialog.show();
        ContactManager.sendInvitationRequest(userName, null, reason, new BasicCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage) {
                dialog.dismiss();
                if (responseCode == 0) {
                    UserEntry userEntry = UserEntry.getUser(mMyInfo.getUserName(), mMyInfo.getAppKey());
                    FriendRecommendEntry entry = FriendRecommendEntry.getEntry(userEntry,
                            userName, mTargetAppKey);
                    if (null == entry) {
                        entry = new FriendRecommendEntry(finalUid, userName, "", finalDisplayName, mTargetAppKey,
                                finalTargetAvatar, finalDisplayName, reason, FriendInvitation.INVITING.getValue(), userEntry, 100);
                    } else {
                        entry.state = FriendInvitation.INVITING.getValue();
                        entry.reason = reason;
                    }
                    entry.save();
                    ToastUtil.shortToast(VerificationActivity.this, "申请成功");
                    finish();
                } else if (responseCode == 871317) {
                    ToastUtil.shortToast(VerificationActivity.this, "不能添加自己为好友");
                } else {
                    ToastUtil.shortToast(VerificationActivity.this, "申请失败");
                }
            }
        });
    }

    private void initView() {
        initTitle(true, true, "验证信息", "", true, "发送");
        mEt_reason = (EditText) findViewById(R.id.et_reason);
        mMyInfo = JMessageClient.getMyInfo();
        mTargetAppKey = mMyInfo.getAppKey();
        String name;
        //群组详细信息点击非好友头像,跳转到此添加界面
        if (getIntent().getFlags() == 1) {
            name = getIntent().getStringExtra("detail_add_friend_my_nickname");
            if (TextUtils.isEmpty(name)) {
                mEt_reason.setText("我是");
            } else {
                mEt_reason.setText("我是" + name);
            }
            //搜索用户发送添加申请
        } else {
            name = mMyInfo.getNickname();
            if (TextUtils.isEmpty(name)) {
                mEt_reason.setText("我是");
            } else {
                mEt_reason.setText("我是" + name);
            }
        }

    }
}
