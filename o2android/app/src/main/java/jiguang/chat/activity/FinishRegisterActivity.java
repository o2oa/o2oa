package jiguang.chat.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.UserEntry;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ThreadUtil;
import jiguang.chat.utils.photochoose.ChoosePhoto;
import jiguang.chat.utils.photochoose.PhotoUtils;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;

public class FinishRegisterActivity extends BaseActivity {
    private EditText mNickNameEt;
    private SelectableRoundedImageView mAvatarIv;
    private Button mFinishBtn;
    private Context mContext;
    private Dialog mSetAvatarDialog;
    private String mPath;
    private Uri mUri;
    private static int OUTPUT_X = 720;
    private static int OUTPUT_Y = 720;
    private static final int MAX_COUNT = 30;
    private ProgressDialog mDialog;
    private TextView mTv_nickCount;
    private ChoosePhoto mChoosePhoto;
    private ImageView mIv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finish_register);
        mContext = this;

        initView();
    }

    private void initView() {
        mNickNameEt = (EditText) findViewById(R.id.nick_name_et);
        mAvatarIv = (SelectableRoundedImageView) findViewById(R.id.mine_header);
        mFinishBtn = (Button) findViewById(R.id.finish_btn);
        mTv_nickCount = (TextView) findViewById(R.id.tv_nickCount);
        mIv_back = (ImageView) findViewById(R.id.iv_back);

        mNickNameEt.addTextChangedListener(new TextChange());
        mAvatarIv.setOnClickListener(listener);
        mFinishBtn.setOnClickListener(listener);
        mIv_back.setOnClickListener(listener);
        SharePreferenceManager.setCachedFixProfileFlag(true);
        mNickNameEt.requestFocus();

        SharePreferenceManager.setRegisterAvatarPath(null);
        mChoosePhoto = new ChoosePhoto();
        mChoosePhoto.setPortraitChangeListener(FinishRegisterActivity.this, mAvatarIv, 1);
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mine_header:
                    if ((ContextCompat.checkSelfPermission(FinishRegisterActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                            (ContextCompat.checkSelfPermission(FinishRegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(FinishRegisterActivity.this, "请在应用管理中打开“读写存储”和“相机”访问权限！", Toast.LENGTH_SHORT).show();
                    } else {
                        mChoosePhoto.showPhotoDialog(FinishRegisterActivity.this);
                    }
                    break;
                case R.id.iv_back:
                    finish();
                    break;
                case R.id.finish_btn:
                    mDialog = new ProgressDialog(FinishRegisterActivity.this);
                    mDialog.setCancelable(false);
                    mDialog.show();

                    final String userId = SharePreferenceManager.getRegistrName();
                    final String password = SharePreferenceManager.getRegistrPass();
                    SharePreferenceManager.setRegisterUsername(userId);
                    JMessageClient.login(userId, password, new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            if (responseCode == 0) {
                                JGApplication.registerOrLogin = 1;
                                String username = JMessageClient.getMyInfo().getUserName();
                                String appKey = JMessageClient.getMyInfo().getAppKey();
                                UserEntry user = UserEntry.getUser(username, appKey);
                                if (null == user) {
                                    user = new UserEntry(username, appKey);
                                    user.save();
                                }

                                String nickName = mNickNameEt.getText().toString();

                                UserInfo myUserInfo = JMessageClient.getMyInfo();
                                if (myUserInfo != null) {
                                    myUserInfo.setNickname(nickName);
                                }
                                //注册时候更新昵称
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname, myUserInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(final int status, String desc) {
                                        //更新跳转标志
                                        SharePreferenceManager.setCachedFixProfileFlag(false);
                                        mDialog.dismiss();
                                        if (status == 0) {
                                            goToActivity(FinishRegisterActivity.this, MainActivity.class);
                                        }
                                    }
                                });
                                //注册时更新头像
                                final String avatarPath = SharePreferenceManager.getRegisterAvatarPath();
                                if (avatarPath != null) {
                                    ThreadUtil.runInThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JMessageClient.updateUserAvatar(new File(avatarPath), new BasicCallback() {
                                                @Override
                                                public void gotResult(int responseCode, String responseMessage) {
                                                    if (responseCode == 0) {
                                                        SharePreferenceManager.setCachedAvatarPath(avatarPath);
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    SharePreferenceManager.setCachedAvatarPath(null);
                                }
                            }
                        }
                    });
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                mChoosePhoto.photoUtils.onActivityResult(FinishRegisterActivity.this, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class TextChange implements TextWatcher {

        @Override
        public void afterTextChanged(Editable arg0) {
            int num = MAX_COUNT - arg0.length();
            mTv_nickCount.setText(num + "");
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int start, int before,
                                  int count) {
//            if (!TextUtils.isEmpty(mNickNameEt.getText().toString())) {
//                mFinishBtn.setEnabled(true);
//            } else {
//                mFinishBtn.setEnabled(false);
//            }
        }
    }
}
