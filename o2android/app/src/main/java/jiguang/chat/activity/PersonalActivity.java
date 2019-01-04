package jiguang.chat.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ThreadUtil;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.citychoose.view.SelectAddressDialog;
import jiguang.chat.utils.citychoose.view.myinterface.SelectAddressInterface;
import jiguang.chat.utils.photochoose.ChoosePhoto;
import jiguang.chat.utils.photochoose.PhotoUtils;

/**
 * Created by ${chenyn} on 2017/2/23.
 */

public class PersonalActivity extends BaseActivity implements SelectAddressInterface, View.OnClickListener {

    public static final int SIGN = 1;
    public static final int FLAGS_SIGN = 2;
    public static final String SIGN_KEY = "sign_key";

    public static final int NICK_NAME = 4;
    public static final int FLAGS_NICK = 3;
    public static final String NICK_NAME_KEY = "nick_name_key";

    private RelativeLayout mRl_cityChoose;
    private TextView mTv_city;
    private SelectAddressDialog dialog;
    private RelativeLayout mRl_gender;
    private RelativeLayout mRl_birthday;

    private TextView mTv_birthday;
    private TextView mTv_gender;
    private RelativeLayout mSign;
    private TextView mTv_sign;
    private RelativeLayout mRl_nickName;


    Intent intent;
    private TextView mTv_nickName;
    private ImageView mIv_photo;
    private ChoosePhoto mChoosePhoto;
    private UserInfo mMyInfo;
    private TextView mTv_userName;
    private RelativeLayout mRl_zxing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

//        Window window = this.getWindow();
//        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        //设置状态栏颜色
//        window.setStatusBarColor(getResources().getColor(R.color.line_normal));

        initView();
        initListener();
        initData();
    }

    private void initData() {
        final Dialog dialog = DialogCreator.createLoadingDialog(PersonalActivity.this,
                PersonalActivity.this.getString(R.string.jmui_loading));
        dialog.show();
        mMyInfo = JMessageClient.getMyInfo();
        if (mMyInfo != null) {
            mTv_nickName.setText(mMyInfo.getNickname());
            SharePreferenceManager.setRegisterUsername(mMyInfo.getNickname());
            mTv_userName.setText("用户名:" + mMyInfo.getUserName());
            mTv_sign.setText(mMyInfo.getSignature());
            UserInfo.Gender gender = mMyInfo.getGender();
            if (gender != null) {
                if (gender.equals(UserInfo.Gender.male)) {
                    mTv_gender.setText("男");
                } else if (gender.equals(UserInfo.Gender.female)) {
                    mTv_gender.setText("女");
                } else {
                    mTv_gender.setText("保密");
                }
            }
            long birthday = mMyInfo.getBirthday();
            if (birthday == 0) {
                mTv_birthday.setText("");
            } else {
                Date date = new Date(mMyInfo.getBirthday());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                mTv_birthday.setText(format.format(date));
            }
            mTv_city.setText(mMyInfo.getAddress());
            mMyInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int responseCode, String responseMessage, Bitmap avatarBitmap) {
                    if (responseCode == 0) {
                        mIv_photo.setImageBitmap(avatarBitmap);
                    } else {
                        mIv_photo.setImageResource(R.drawable.rc_default_portrait);
                    }
                }
            });
            dialog.dismiss();
        }
    }

    private void initListener() {
        mRl_cityChoose.setOnClickListener(this);
        mRl_birthday.setOnClickListener(this);
        mRl_gender.setOnClickListener(this);
        mSign.setOnClickListener(this);
        mRl_nickName.setOnClickListener(this);
        mIv_photo.setOnClickListener(this);
        mRl_zxing.setOnClickListener(this);
    }

    private void initView() {
        initTitle(true, true, "个人信息", "", false, "");
        mRl_cityChoose = (RelativeLayout) findViewById(R.id.rl_cityChoose);
        mTv_city = (TextView) findViewById(R.id.tv_city);
        mRl_gender = (RelativeLayout) findViewById(R.id.rl_gender);
        mRl_birthday = (RelativeLayout) findViewById(R.id.rl_birthday);
        mTv_birthday = (TextView) findViewById(R.id.tv_birthday);
        mTv_gender = (TextView) findViewById(R.id.tv_gender);
        mSign = (RelativeLayout) findViewById(R.id.sign);
        mTv_sign = (TextView) findViewById(R.id.tv_sign);
        mRl_nickName = (RelativeLayout) findViewById(R.id.rl_nickName);
        mTv_nickName = (TextView) findViewById(R.id.tv_nickName);
        mIv_photo = (ImageView) findViewById(R.id.iv_photo);
        mTv_userName = (TextView) findViewById(R.id.tv_userName);
        mRl_zxing = (RelativeLayout) findViewById(R.id.rl_zxing);

        mChoosePhoto = new ChoosePhoto();
        mChoosePhoto.setPortraitChangeListener(PersonalActivity.this, mIv_photo, 2);

    }

    @Override
    public void setAreaString(String area) {
        mTv_city.setText(area);
    }

    @Override
    public void setTime(String time) {
    }

    @Override
    public void setGender(String gender) {
        mTv_gender.setText(gender);
    }

    @Override
    public void onClick(View v) {
        intent = new Intent(PersonalActivity.this, NickSignActivity.class);
        switch (v.getId()) {
            case R.id.iv_photo:
                //头像
                if ((ContextCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) ||
                        (ContextCompat.checkSelfPermission(PersonalActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(PersonalActivity.this, "请在应用管理中打开“读写存储”和“相机”访问权限！", Toast.LENGTH_SHORT).show();
                }
                mChoosePhoto.setInfo(PersonalActivity.this, true);
                mChoosePhoto.showPhotoDialog(PersonalActivity.this);
                break;
            case R.id.rl_nickName:
                //昵称
                intent.setFlags(FLAGS_NICK);
                intent.putExtra("old_nick", mMyInfo.getNickname());
                startActivityForResult(intent, NICK_NAME);
                break;
            case R.id.sign:
                //签名
                intent.setFlags(FLAGS_SIGN);
                intent.putExtra("old_sign", mMyInfo.getSignature());
                startActivityForResult(intent, SIGN);
                break;
            case R.id.rl_gender:
                //弹出性别选择器
                dialog = new SelectAddressDialog(PersonalActivity.this);
                dialog.showGenderDialog(PersonalActivity.this, mMyInfo);
                break;
            case R.id.rl_birthday:
                //弹出时间选择器选择生日
                TimePickerView timePickerView = new TimePickerView.Builder(PersonalActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(final Date date, View v) {
                        mMyInfo.setBirthday(date.getTime());
                        JMessageClient.updateMyInfo(UserInfo.Field.birthday, mMyInfo, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == 0) {
                                    mTv_birthday.setText(getDataTime(date));
                                    Toast.makeText(PersonalActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PersonalActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                        .setType(TimePickerView.Type.YEAR_MONTH_DAY)
                        .setCancelText("取消")
                        .setSubmitText("确定")
                        .setContentSize(20)//滚轮文字大小
                        .setTitleSize(20)//标题文字大小
                        .setOutSideCancelable(true)
                        .isCyclic(true)
                        .setTextColorCenter(Color.BLACK)//设置选中项的颜色
                        .setSubmitColor(Color.GRAY)//确定按钮文字颜色
                        .setCancelColor(Color.GRAY)//取消按钮文字颜色
                        .isCenterLabel(false)
                        .build();
                timePickerView.show();
//                dialog = new SelectAddressDialog(PersonalActivity.this);
//                dialog.showDateDialog(PersonalActivity.this, mMyInfo);
                break;
            case R.id.rl_cityChoose:
                //点击选择省市
                dialog = new SelectAddressDialog(PersonalActivity.this,
                        PersonalActivity.this, SelectAddressDialog.STYLE_THREE, null, mMyInfo);
                dialog.showDialog();
                break;
            case R.id.rl_zxing:
                //二维码
                Intent intent = new Intent(PersonalActivity.this, Person2CodeActivity.class);
                intent.putExtra("appkey", mMyInfo.getAppKey());
                intent.putExtra("username", mMyInfo.getUserName());
                if (mMyInfo.getAvatarFile() != null) {
                    intent.putExtra("avatar", mMyInfo.getAvatarFile().getAbsolutePath());
                }
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            switch (resultCode) {
                case SIGN:
                    final String sign = bundle.getString(SIGN_KEY);
                    ThreadUtil.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyInfo.setSignature(sign);
                            JMessageClient.updateMyInfo(UserInfo.Field.signature, mMyInfo, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        mTv_sign.setText(sign);
                                        ToastUtil.shortToast(PersonalActivity.this, "更新成功");
                                    } else {
                                        ToastUtil.shortToast(PersonalActivity.this, "更新失败");
                                    }
                                }
                            });
                        }
                    });
                    break;
                case NICK_NAME:
                    final String nick = bundle.getString(NICK_NAME_KEY);
                    ThreadUtil.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            mMyInfo.setNickname(nick);
                            JMessageClient.updateMyInfo(UserInfo.Field.nickname, mMyInfo, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        mTv_nickName.setText(nick);
                                        ToastUtil.shortToast(PersonalActivity.this, "更新成功");
                                    } else {
                                        ToastUtil.shortToast(PersonalActivity.this, "更新失败,请正确输入");
                                    }
                                }
                            });
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:
            case PhotoUtils.INTENT_TAKE:
            case PhotoUtils.INTENT_SELECT:
                mChoosePhoto.photoUtils.onActivityResult(PersonalActivity.this, requestCode, resultCode, data);
                break;
        }
    }

    public String getDataTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
