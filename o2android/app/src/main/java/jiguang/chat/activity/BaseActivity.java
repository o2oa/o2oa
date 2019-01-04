package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.FileHelper;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.swipeback.app.SwipeBackActivity;

public class BaseActivity extends SwipeBackActivity {

    protected int mWidth;
    protected int mHeight;
    protected float mDensity;
    protected int mDensityDpi;
    private TextView mJmui_title_tv;
    private ImageButton mReturn_btn;
    private TextView mJmui_title_left;
    public Button mJmui_commit_btn;
    protected int mAvatarSize;
    protected float mRatio;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //注册sdk的event用于接收各种event事件
        JMessageClient.registerEventReceiver(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mDensityDpi = dm.densityDpi;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;
        mRatio = Math.min((float) mWidth / 720, (float) mHeight / 1280);
        mAvatarSize = (int) (50 * mDensity);

    }

    //初始化各个activity的title
    public void initTitle(boolean returnBtn, boolean titleLeftDesc, String titleLeft, String title, boolean save, String desc) {
        mReturn_btn = (ImageButton) findViewById(R.id.return_btn);
        mJmui_title_left = (TextView) findViewById(R.id.jmui_title_left);
        mJmui_title_tv = (TextView) findViewById(R.id.jmui_title_tv);
        mJmui_commit_btn = (Button) findViewById(R.id.jmui_commit_btn);

        if (returnBtn) {
            mReturn_btn.setVisibility(View.VISIBLE);
            mReturn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (titleLeftDesc) {
            mJmui_title_left.setVisibility(View.VISIBLE);
            mJmui_title_left.setText(titleLeft);
        }
        mJmui_title_tv.setText(title);
        if (save) {
            mJmui_commit_btn.setVisibility(View.VISIBLE);
            mJmui_commit_btn.setText(desc);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public void goToActivity(Context context, Class toActivity) {
        Intent intent = new Intent(context, toActivity);
        startActivity(intent);
        finish();
    }

    public void onEventMainThread(LoginStateChangeEvent event) {
        final LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        if (myInfo != null) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }
            SharePreferenceManager.setCachedUsername(myInfo.getUserName());
            SharePreferenceManager.setCachedAvatarPath(path);
            JMessageClient.logout();
        }
        switch (reason) {
            case user_logout:
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.jmui_cancel_btn:
                                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.jmui_commit_btn:
                                JMessageClient.login(SharePreferenceManager.getCachedUsername(), SharePreferenceManager.getCachedPsw(), new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            Intent intent = new Intent(BaseActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                break;
                        }
                    }
                };
                dialog = DialogCreator.createLogoutStatusDialog(BaseActivity.this, "您的账号在其他设备上登陆", listener);
                dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case user_password_change:
                Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onDestroy() {
        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this);
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }


}
