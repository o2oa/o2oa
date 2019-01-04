package jiguang.chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.FriendInfoActivity;
import jiguang.chat.controller.FriendInfoController;

/**
 * Created by ${chenyn} on 2017/3/24.
 */

public class FriendInfoView extends LinearLayout {


    private FriendInfoController mListeners;
    private FriendInfoController mOnChangeListener;
    private ImageView mIv_friendPhoto;
    private TextView mTv_noteName;
    private TextView mTv_signature;
    private TextView mTv_userName;
    private TextView mTv_gender;
    private TextView mTv_birthday;
    private TextView mTv_address;
    private Button mBtn_goToChat;
    private Context mContext;
    private ImageView mSetting;
    private ImageButton mReturnBtn;
    private RelativeLayout mRl_NickName;
    private TextView mTv_NickName;


    public FriendInfoView(Context context) {
        super(context);
    }

    public FriendInfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FriendInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initModel(FriendInfoActivity context) {
        this.mContext = context;
        mIv_friendPhoto = (ImageView) findViewById(R.id.iv_friendPhoto);
        mTv_noteName = (TextView) findViewById(R.id.tv_nickName);
        mTv_signature = (TextView) findViewById(R.id.tv_signature);
        mTv_userName = (TextView) findViewById(R.id.tv_userName);
        mTv_gender = (TextView) findViewById(R.id.tv_gender);
        mTv_birthday = (TextView) findViewById(R.id.tv_birthday);
        mTv_address = (TextView) findViewById(R.id.tv_address);
        mBtn_goToChat = (Button) findViewById(R.id.btn_goToChat);
        mSetting = (ImageView) findViewById(R.id.jmui_commit_btn);
        mReturnBtn = (ImageButton) findViewById(R.id.return_btn);
        mRl_NickName = (RelativeLayout) findViewById(R.id.rl_nickName);
        mTv_NickName = (TextView) findViewById(R.id.tv_nick);

    }

    public void setListeners(OnClickListener listeners) {
        mBtn_goToChat.setOnClickListener(listeners);
        mIv_friendPhoto.setOnClickListener(listeners);
        mSetting.setOnClickListener(listeners);
        mReturnBtn.setOnClickListener(listeners);

    }

    public void setOnChangeListener(FriendInfoController onChangeListener) {
        mOnChangeListener = onChangeListener;
    }


    public void initInfo(UserInfo userInfo) {
        if (userInfo != null) {
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int status, String desc, Bitmap bitmap) {
                        if (status == 0) {
                            mIv_friendPhoto.setImageBitmap(bitmap);
                        }else {
                            mIv_friendPhoto.setImageResource(R.drawable.rc_default_portrait);
                        }
                    }
                });
            } else {
                mIv_friendPhoto.setImageResource(R.drawable.rc_default_portrait);
            }
            String noteName = userInfo.getNotename();
            String nickName = userInfo.getNickname();
            String userName = userInfo.getUserName();
            //有备注 有昵称
            mTv_userName.setText(userName);
            if (!TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(nickName)) {
                mRl_NickName.setVisibility(View.VISIBLE);
                mTv_NickName.setText(nickName);
                mTv_noteName.setText("备注名: " + noteName);
            }
            //没有备注 有昵称
            else if (TextUtils.isEmpty(noteName) && !TextUtils.isEmpty(nickName)) {
                mRl_NickName.setVisibility(View.GONE);
                mTv_noteName.setText("昵称: " + nickName);
            }
            //有备注 没有昵称
            else if (!TextUtils.isEmpty(noteName) && TextUtils.isEmpty(nickName)) {
                mRl_NickName.setVisibility(View.VISIBLE);
                mTv_NickName.setText(userInfo.getNickname());
                mTv_noteName.setText("备注名: " + noteName);
            }
            //没有备注名 没有昵称
            else {
                mRl_NickName.setVisibility(View.GONE);
                mTv_noteName.setText("用户名: " + userName);
            }
            if (userInfo.getGender() == UserInfo.Gender.male) {
                mTv_gender.setText(mContext.getString(R.string.man));
            } else if (userInfo.getGender() == UserInfo.Gender.female) {
                mTv_gender.setText(mContext.getString(R.string.woman));
            } else {
                mTv_gender.setText(mContext.getString(R.string.unknown));
            }
            mTv_address.setText(userInfo.getRegion());
            mTv_signature.setText(userInfo.getSignature());
            mTv_birthday.setText(getBirthday(userInfo));
        }
    }

    public String getBirthday(UserInfo info) {
        long birthday = info.getBirthday();
        if (birthday == 0) {
            return "";
        }else {
            Date date = new Date(birthday);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dateFormat.format(date);
        }
    }
}
