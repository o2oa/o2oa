package jiguang.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.dialog.LoadDialog;
import jiguang.chat.view.SlipButton;

/**
 * Created by ${chenyn} on 2017/9/26.
 */

public class NotFriendSettingActivity extends BaseActivity implements SlipButton.OnChangedListener{
    private UserInfo mUserInfo;
    private SlipButton mBtn_addBlackList;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_friend_setting);

        mBtn_addBlackList = (SlipButton) findViewById(R.id.btn_addBlackList);
        mUserName = getIntent().getStringExtra("notFriendUserName");
        mBtn_addBlackList.setOnChangedListener(R.id.btn_addBlackList, this);
        JMessageClient.getUserInfo(mUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    mUserInfo = userInfo;
                    mBtn_addBlackList.setChecked(userInfo.getBlacklist() == 1);
                }
            }
        });
    }

    public void returnBtn(View view) {
        finish();
    }

    public void sendBusinessCard(View view) {
        //发送此人的名片
        Intent businessIntent = new Intent(NotFriendSettingActivity.this, ForwardMsgActivity.class);
        businessIntent.setFlags(1);
        businessIntent.putExtra("userName", mUserInfo.getUserName());
        businessIntent.putExtra("appKey", mUserInfo.getAppKey());
        if (mUserInfo.getAvatarFile() != null) {
            businessIntent.putExtra("avatar", mUserInfo.getAvatarFile().getAbsolutePath());
        }
        startActivity(businessIntent);
    }

    @Override
    public void onChanged(int id, boolean checkState) {
        switch (id) {
            case R.id.btn_addBlackList:
                final LoadDialog dialog = new LoadDialog(NotFriendSettingActivity.this, false, "正在设置");
                dialog.show();
                List<String> name = new ArrayList<>();
                name.add(mUserName);
                if (checkState) {
                    JMessageClient.addUsersToBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "添加成功");
                            } else {
                                mBtn_addBlackList.setChecked(false);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "添加失败" + responseMessage);
                            }
                        }
                    });
                } else {
                    JMessageClient.delUsersFromBlacklist(name, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            dialog.dismiss();
                            if (responseCode == 0) {
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "移除成功");
                            } else {
                                mBtn_addBlackList.setChecked(true);
                                ToastUtil.shortToast(NotFriendSettingActivity.this, "移除失败" + responseMessage);
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
