package jiguang.chat.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.NativeImageLoader;
import jiguang.chat.utils.SharePreferenceManager;

/**
 * Created by ${chenyn} on 2017/3/21.
 */

public class SearchFriendDetailActivity extends BaseActivity {

    private ImageView mIv_friendPhoto;
    private TextView mTv_nickName;
    private TextView mTv_additionalMsg;
    private TextView mTv_signature;
    private TextView mTv_gender;
    private TextView mTv_birthday;
    private TextView mTv_address;
    private Button mBtn_refusal;
    private Button mBtn_agree;
    private String mUsername;
    private String mAppKey;
    private UserInfo mToUserInfo;

    private String mAvatarPath;
    private String mDisplayName;
    private TextView mUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        initView();
    }

    private void initView() {
        initTitle(true, true, "详细资料", "", false, "");
        mIv_friendPhoto = (ImageView) findViewById(R.id.iv_friendPhoto);
        mTv_nickName = (TextView) findViewById(R.id.tv_nickName);
        mTv_additionalMsg = (TextView) findViewById(R.id.tv_additionalMsg);
        mTv_signature = (TextView) findViewById(R.id.tv_sign);
        mTv_gender = (TextView) findViewById(R.id.tv_gender);
        mTv_birthday = (TextView) findViewById(R.id.tv_birthday);
        mTv_address = (TextView) findViewById(R.id.tv_address);
        mBtn_refusal = (Button) findViewById(R.id.btn_refusal);
        mBtn_agree = (Button) findViewById(R.id.btn_agree);
        mUserName = (TextView) findViewById(R.id.tv_userName);

        initModel();

    }

    private void initModel() {
        final Dialog dialog = DialogCreator.createLoadingDialog(this, this.getString(R.string.jmui_loading));
        dialog.show();
        final Intent intent = getIntent();
        mUsername = intent.getStringExtra(JGApplication.TARGET_ID);
        mAppKey = intent.getStringExtra(JGApplication.TARGET_APP_KEY);
        JMessageClient.getUserInfo(mUsername, mAppKey, new GetUserInfoCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                dialog.dismiss();
                if (responseCode == 0) {
                    mToUserInfo = info;
                    Bitmap bitmap = NativeImageLoader.getInstance().getBitmapFromMemCache(mUsername);
                    if (bitmap != null) {
                        mIv_friendPhoto.setImageBitmap(bitmap);
                    } else if (!TextUtils.isEmpty(info.getAvatar())) {
                        mAvatarPath = info.getAvatarFile().getPath();
                        info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage, Bitmap avatarBitmap) {
                                if (responseCode == 0) {
                                    mIv_friendPhoto.setImageBitmap(avatarBitmap);
                                    NativeImageLoader.getInstance().updateBitmapFromCache(mUsername, avatarBitmap);
                                } else {
                                    mIv_friendPhoto.setImageResource(R.drawable.rc_default_portrait);
                                }
                            }
                        });
                    } else {
                        mIv_friendPhoto.setImageResource(R.drawable.rc_default_portrait);
                    }
                    mDisplayName = info.getNickname();
                    if (TextUtils.isEmpty(mDisplayName)) {
                        mDisplayName = info.getUserName();
                    }
                    mTv_nickName.setText(mDisplayName);
                    if (info.getGender() == UserInfo.Gender.male) {
                        mTv_gender.setText("男");
                    } else if (info.getGender() == UserInfo.Gender.female) {
                        mTv_gender.setText("女");
                    } else {
                        mTv_gender.setText("保密");
                    }
                    mTv_additionalMsg.setText("附加消息: " + intent.getStringExtra("reason"));
                    mTv_signature.setText(info.getSignature());
                    mUserName.setText(mUsername);
                    long birthday = info.getBirthday();
                    if (birthday == 0) {
                        mTv_birthday.setText("");
                    } else {
                        Date date = new Date(birthday);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        mTv_birthday.setText(dateFormat.format(date));
                    }
                    mTv_address.setText(info.getRegion());
                }
            }
        });

        final int position = intent.getIntExtra("position", -1);

        View.OnClickListener listener = new View.OnClickListener() {
            final Dialog dialog = DialogCreator.createLoadingDialog(SearchFriendDetailActivity.this, "正在加载");

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_refusal:
                        //拒绝添加
                        dialog.show();
                        ContactManager.declineInvitation(mToUserInfo.getUserName(), mToUserInfo.getAppKey(), "拒绝了您的好友请求", new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                dialog.dismiss();
                                if (responseCode == 0) {
                                    //拒绝时候要修改button数据库状态,并更新内存
                                    Intent btnIntent = new Intent();
                                    btnIntent.putExtra("position", position);
                                    btnIntent.putExtra("btn_state", 1);
                                    setResult(JGApplication.RESULT_BUTTON, btnIntent);
                                    finish();
                                }
                            }
                        });
                        break;
                    case R.id.btn_agree:
                        //同意添加
                        dialog.show();
                        ContactManager.acceptInvitation(mToUserInfo.getUserName(), mToUserInfo.getAppKey(), new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                dialog.dismiss();
                                if (responseCode == 0) {
                                    Intent btnIntent2 = new Intent();
                                    btnIntent2.putExtra("position", position);
                                    btnIntent2.putExtra("btn_state", 2);
                                    setResult(JGApplication.RESULT_BUTTON, btnIntent2);
                                    EventBus.getDefault().post(new Event.Builder().setType(EventType.addFriend)
                                            .setFriendId(SharePreferenceManager.getItem()).build());
                                    finish();
                                }
                            }
                        });
                        break;
                    default:
                        break;
                }

            }
        };
        mBtn_agree.setOnClickListener(listener);
        mBtn_refusal.setOnClickListener(listener);
    }


}
