package jiguang.chat.controller;

import android.content.Intent;
import android.view.View;

import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.FriendInfoActivity;
import jiguang.chat.activity.FriendSettingActivity;
import jiguang.chat.view.FriendInfoView;

/**
 * Created by ${chenyn} on 2017/3/24.
 */

public class FriendInfoController implements View.OnClickListener {
    private FriendInfoActivity mContext;
    private UserInfo friendInfo;

    public FriendInfoController(FriendInfoView friendInfoView, FriendInfoActivity context) {
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goToChat:
                mContext.startChatActivity();
                break;
            case R.id.iv_friendPhoto:
                mContext.startBrowserAvatar();
                break;
            case R.id.jmui_commit_btn:
                Intent intent = new Intent(mContext, FriendSettingActivity.class);
                intent.putExtra("userName", friendInfo.getUserName());
                intent.putExtra("noteName", friendInfo.getNotename());
                mContext.startActivity(intent);
                break;
            case R.id.return_btn:
                mContext.finish();
                break;
            default:
                break;
        }
    }

    public void setFriendInfo(UserInfo info) {
        friendInfo = info;
    }

}
