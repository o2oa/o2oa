package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.database.FriendRecommendEntry;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.dialog.LoadDialog;
import jiguang.chat.view.SlipButton;

/**
 * Created by ${chenyn} on 2017/5/7.
 */

public class FriendSettingActivity extends BaseActivity implements SlipButton.OnChangedListener, View.OnClickListener {

    private RelativeLayout mSetNoteName;
    private SlipButton mBtn_addBlackList;
    private Button mBtn_deleteFriend;
    private TextView mTv_noteName;
    private Dialog mDialog;
    private UserInfo mFriendInfo;
    private RelativeLayout mRl_business;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_setting);
        initView();
        initData();

    }

    private void initData() {
        //设置黑名单
        mBtn_addBlackList.setOnChangedListener(R.id.btn_addBlackList, this);
        mBtn_deleteFriend.setOnClickListener(this);
        mSetNoteName.setOnClickListener(this);
        mRl_business.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setNoteName:
                Intent intent = new Intent(FriendSettingActivity.this, SetNoteNameActivity.class);
                intent.putExtra("user", getIntent().getStringExtra("userName"));
                intent.putExtra("note", getIntent().getStringExtra("noteName"));
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_deleteFriend:
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.jmui_cancel_btn:
                                mDialog.dismiss();
                                break;
                            case R.id.jmui_commit_btn:
                                final Dialog dialog = DialogCreator.createLoadingDialog(FriendSettingActivity.this, getString(R.string.processing));
                                dialog.show();
                                mFriendInfo.removeFromFriendList(new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        dialog.dismiss();
                                        if (responseCode == 0) {
                                            //将好友删除时候还原黑名单设置
                                            List<String> name = new ArrayList<>();
                                            name.add(mFriendInfo.getUserName());
                                            JMessageClient.delUsersFromBlacklist(name, null);

                                            FriendEntry friend = FriendEntry.getFriend(JGApplication.getUserEntry(),
                                                    mFriendInfo.getUserName(), mFriendInfo.getAppKey());
                                            if (friend != null) {
                                                friend.delete();
                                            }
                                            FriendRecommendEntry entry = FriendRecommendEntry
                                                    .getEntry(JGApplication.getUserEntry(),
                                                            mFriendInfo.getUserName(), mFriendInfo.getAppKey());
                                            if (entry != null) {
                                                entry.delete();
                                            }
                                            ToastUtil.shortToast(FriendSettingActivity.this, "移除好友");
                                            mDialog.dismiss();
                                            delConvAndReturnMainActivity();
                                        } else {
                                            mDialog.dismiss();
                                            ToastUtil.shortToast(FriendSettingActivity.this, "移除失败");
                                        }
                                    }
                                });
                                break;
                        }
                    }
                };
                mDialog = DialogCreator.createBaseDialogWithTitle(this,
                        getString(R.string.delete_friend_dialog_title), listener);
                mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                mDialog.show();
                break;
            //发送好友名片
            case R.id.rl_business:
                Intent businessIntent = new Intent(FriendSettingActivity.this, ForwardMsgActivity.class);
                businessIntent.setFlags(1);
                businessIntent.putExtra("userName", mFriendInfo.getUserName());
                businessIntent.putExtra("appKey", mFriendInfo.getAppKey());
                if (mFriendInfo.getAvatarFile() != null) {
                    businessIntent.putExtra("avatar", mFriendInfo.getAvatarFile().getAbsolutePath());
                }
                startActivity(businessIntent);
                break;
            default:
                break;
        }
    }

    public void delConvAndReturnMainActivity() {
        Conversation conversation = JMessageClient.getSingleConversation(mFriendInfo.getUserName(), mFriendInfo.getAppKey());
        EventBus.getDefault().post(new Event.Builder().setType(EventType.deleteConversation)
                .setConversation(conversation)
                .build());
        JMessageClient.deleteSingleConversation(mFriendInfo.getUserName(), mFriendInfo.getAppKey());
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        initTitle(true, true, "设置", "", false, "");
        mSetNoteName = (RelativeLayout) findViewById(R.id.setNoteName);
        mBtn_addBlackList = (SlipButton) findViewById(R.id.btn_addBlackList);
        mBtn_deleteFriend = (Button) findViewById(R.id.btn_deleteFriend);
        mTv_noteName = (TextView) findViewById(R.id.tv_noteName);
        mRl_business = (RelativeLayout) findViewById(R.id.rl_business);
        final Dialog dialog = DialogCreator.createLoadingDialog(FriendSettingActivity.this,
                FriendSettingActivity.this.getString(R.string.jmui_loading));
        dialog.show();
        if (!TextUtils.isEmpty(getIntent().getStringExtra("noteName"))) {
            mTv_noteName.setText(getIntent().getStringExtra("noteName"));
        }
        JMessageClient.getUserInfo(getIntent().getStringExtra("userName"), new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                dialog.dismiss();
                if (responseCode == 0) {
                    mFriendInfo = info;
                    mBtn_addBlackList.setChecked(info.getBlacklist() == 1);
                    if (info.isFriend()) {
                        mBtn_deleteFriend.setVisibility(View.VISIBLE);
                        mSetNoteName.setVisibility(View.VISIBLE);
                    }else {
                        mBtn_deleteFriend.setVisibility(View.GONE);
                        mSetNoteName.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            mTv_noteName.setText(data.getStringExtra("updateName"));
        }
    }

    @Override
    public void onChanged(int id, boolean checkState) {
        switch (id) {
            case R.id.btn_addBlackList:
                final LoadDialog dialog = new LoadDialog(FriendSettingActivity.this, false, "正在设置");
                dialog.show();
                String userName = getIntent().getStringExtra("userName");
                List<String> name = new ArrayList<>();
                name.add(userName);
                if (checkState) {
                    JMessageClient.addUsersToBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "添加成功");
                            } else {
                                mBtn_addBlackList.setChecked(false);
                                ToastUtil.shortToast(FriendSettingActivity.this, "添加失败" + responseMessage);
                            }
                        }
                    });
                } else {
                    JMessageClient.delUsersFromBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(FriendSettingActivity.this, "移除成功");
                            } else {
                                mBtn_addBlackList.setChecked(true);
                                ToastUtil.shortToast(FriendSettingActivity.this, "移除失败" + responseMessage);
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
