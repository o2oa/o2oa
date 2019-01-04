package jiguang.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class WelcomeActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mContext = this;
        initData();
    }

    private void initData() {
        //检测账号是否登陆
        UserInfo myInfo = JMessageClient.getMyInfo();
        if (myInfo == null) {
            goToRegisterAndLoginActivity();
        }else {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }

    private void goToRegisterAndLoginActivity() {
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }
}
