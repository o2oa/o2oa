package jiguang.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.model.InfoModel;

/**
 * Created by ${chenyn} on 2017/3/14.
 */

public class SearchFriendInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mIv_photo;
    private TextView mTv_nickName;
    private TextView mTv_userName;
    private TextView mTv_sign;
    private TextView mTv_gender;
    private TextView mTv_birthday;
    private TextView mTv_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend_info);

        initView();
        initData();
    }

    private void initView() {
        initTitle(true, true, "详细资料", "", false, "保存");
        mIv_photo = (ImageView) findViewById(R.id.iv_photo);
        mTv_nickName = (TextView) findViewById(R.id.tv_nickName);
        mTv_userName = (TextView) findViewById(R.id.tv_userName);
        mTv_sign = (TextView) findViewById(R.id.tv_sign);
        mTv_gender = (TextView) findViewById(R.id.tv_gender);
        mTv_birthday = (TextView) findViewById(R.id.tv_birthday);
        mTv_city = (TextView) findViewById(R.id.tv_city);

        findViewById(R.id.btn_addFriend).setOnClickListener(this);
    }

    private void initData() {
        InfoModel instance = InfoModel.getInstance();
        if (instance.getAvatar() == null) {
            mIv_photo.setImageResource(R.drawable.rc_default_portrait);
        } else {
            mIv_photo.setImageBitmap(instance.getAvatar());
        }
        if (TextUtils.isEmpty(instance.getNickName())) {
            mTv_nickName.setText("用户名: " + instance.getUserName());
        } else {
            mTv_nickName.setText("昵称:" + instance.getNickName());
        }
        mTv_userName.setText(instance.getUserName());
        mTv_sign.setText(instance.getSign());
        mTv_gender.setText(instance.getGender());
        mTv_birthday.setText(instance.getBirthday());
        mTv_city.setText(instance.getCity());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_addFriend:
                //添加好友界面
                intent = new Intent(SearchFriendInfoActivity.this, VerificationActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }
}
