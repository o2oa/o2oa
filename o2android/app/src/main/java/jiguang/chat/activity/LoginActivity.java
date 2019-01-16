package jiguang.chat.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.lang.reflect.Method;

import cn.jpush.im.android.api.JMessageClient;
import jiguang.chat.controller.LoginController;
import jiguang.chat.utils.BitmapLoader;
import jiguang.chat.utils.ClearWriteEditText;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.SoftKeyBoardStateHelper;

public class LoginActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    public ClearWriteEditText mLogin_userName;
    public ClearWriteEditText mLogin_passWord;
    public Button mBtn_login;
    public TextView mLogin_register;
    private LoginController mLoginController;
    private ImageView mDe_login_logo;
    private RelativeLayout mTitleBar;
    private RelativeLayout mBackground;
    private LinearLayout mLl_name_psw;
    private boolean mLogoShow = true;
    public TextView mNewUser;
    public TextView mLogin_desc;
    private ImageView mLogin_userLogo;
    private ImageView mLogin_pswLogo;
    private View mView;
    private View mUserLine;
    private View mPswLine;
    //内部测试环境使用,发布时会置为false;此处对开发者来说即使打开也是没有效果的.
    private boolean isTestVisibility = true;
    private RadioGroup mRadioGroup;
    private RadioButton mRelease;
    private RadioButton mTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juim_login);
        initView();
        initData();

        mLoginController = new LoginController(this);

        mBtn_login.setOnClickListener(mLoginController);
        mLogin_register.setOnClickListener(mLoginController);
        mNewUser.setOnClickListener(mLoginController);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.background:
                if (!getLogoShow()) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    setLogoShow(false);
                }
                break;
            case R.id.login_userName:
            case R.id.login_passWord:
                if (getLogoShow()) {
                    mTitleBar.setVisibility(View.VISIBLE);
                    mTitleBar.startAnimation(moveToView(0.0f, 0.0f, -1.0f, 0.0f));
                    mDe_login_logo.setVisibility(View.GONE);
                    mLl_name_psw.startAnimation(moveToView(0.0f, 0.0f, 0.32f, 0.0f));

                    mView.setVisibility(View.VISIBLE);
                    setLogoShow(false);
                }
                break;
            default:
                break;
        }

    }

    public void setLogoShow(boolean isLogoShow) {
        mLogoShow = isLogoShow;
    }

    public boolean getLogoShow() {
        return mLogoShow;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.login_userName:
                if (hasFocus) {
                    mLogin_userLogo.setImageResource(R.drawable.login_user_press);
                    mUserLine.setBackgroundColor(getResources().getColor(R.color.line_press));
                } else {
                    mLogin_userLogo.setImageResource(R.drawable.login_user_normal);
                    mUserLine.setBackgroundColor(getResources().getColor(R.color.line_normal));
                }
                if (hasFocus && getLogoShow()) {
                    mTitleBar.setVisibility(View.VISIBLE);
                    mTitleBar.startAnimation(moveToView(0.0f, 0.0f, -1.0f, 0.0f));
                    mDe_login_logo.setVisibility(View.GONE);
                    mLl_name_psw.startAnimation(moveToView(0.0f, 0.0f, 0.32f, 0.0f));
                    mView.setVisibility(View.VISIBLE);
                    setLogoShow(false);
                }
                break;
            case R.id.login_passWord:
                if (hasFocus) {
                    mLogin_pswLogo.setImageResource(R.drawable.login_psw_press);
                    mPswLine.setBackgroundColor(getResources().getColor(R.color.line_press));
                } else {
                    mLogin_pswLogo.setImageResource(R.drawable.login_psw_normal);
                    mPswLine.setBackgroundColor(getResources().getColor(R.color.line_normal));
                }
                if (hasFocus && getLogoShow()) {
                    mTitleBar.setVisibility(View.VISIBLE);
                    mTitleBar.startAnimation(moveToView(0.0f, 0.0f, -1.0f, 0.0f));
                    mDe_login_logo.setVisibility(View.GONE);
                    mLl_name_psw.startAnimation(moveToView(0.0f, 0.0f, 0.32f, 0.0f));
                    setLogoShow(false);
                }
                break;
        }
    }

    private void initData() {
        mLogin_userName.setOnFocusChangeListener(this);
        mLogin_passWord.setOnFocusChangeListener(this);
        mLogin_userName.setOnClickListener(this);
        mLogin_passWord.setOnClickListener(this);
        mBackground.setOnClickListener(this);
        SoftKeyBoardStateHelper helper = new SoftKeyBoardStateHelper(findViewById(R.id.background));
        helper.addSoftKeyboardStateListener(new SoftKeyBoardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //软键盘弹起
            }

            @Override
            public void onSoftKeyboardClosed() {
                //软键盘关闭
                if (!getLogoShow()) {
                    mTitleBar.setVisibility(View.GONE);
                    mTitleBar.startAnimation(moveToView(0.0f, 0.0f, 0.0f, -1.0f));
                    mDe_login_logo.setVisibility(View.VISIBLE);
                    AlphaAnimation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(250);
                    mDe_login_logo.startAnimation(animation);
                    mLl_name_psw.startAnimation(moveToView(0.0f, 0.0f, -0.09f, 0.003f));
                    setLogoShow(true);
                }
            }
        });

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_release:
                        swapEnvironment(LoginActivity.this.getApplicationContext(), false);
                        break;
                    case R.id.rb_test:
                        swapEnvironment(LoginActivity.this.getApplicationContext(), true);
                        break;
                    default:
                        break;
                }
            }
        });
    }


    public TranslateAnimation moveToView(float a, float b, float c, float d) {
        TranslateAnimation mHiddenAction = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, a,
                Animation.RELATIVE_TO_SELF, b,
                Animation.RELATIVE_TO_SELF, c,
                Animation.RELATIVE_TO_SELF, d);
        mHiddenAction.setDuration(250);
        return mHiddenAction;
    }


    private void initView() {
        mLogin_userName = (ClearWriteEditText) findViewById(R.id.login_userName);
        mLogin_passWord = (ClearWriteEditText) findViewById(R.id.login_passWord);
        mBtn_login = (Button) findViewById(R.id.btn_login);
        mDe_login_logo = (ImageView) findViewById(R.id.de_login_logo);
        mLogin_register = (TextView) findViewById(R.id.login_register);
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mBackground = (RelativeLayout) findViewById(R.id.background);
        mLl_name_psw = (LinearLayout) findViewById(R.id.ll_name_psw);
        mLogin_userLogo = (ImageView) findViewById(R.id.login_userLogo);
        mLogin_pswLogo = (ImageView) findViewById(R.id.login_pswLogo);
        mView = findViewById(R.id.view);
        mUserLine = findViewById(R.id.user_line);
        mPswLine = findViewById(R.id.psw_line);

        mNewUser = (TextView) findViewById(R.id.new_user);
        mLogin_desc = (TextView) findViewById(R.id.login_desc);

        mRadioGroup = (RadioGroup) findViewById(R.id.rg_group);
        mRelease = (RadioButton) findViewById(R.id.rb_release);
        mTest = (RadioButton) findViewById(R.id.rb_test);

        if (!isTestVisibility) {
            mRadioGroup.setVisibility(View.GONE);
        } else {
            //供jmessage sdk测试使用，开发者无需关心。
            Boolean isTestEvn = invokeIsTestEvn();
            if (isTestEvn) {
                mTest.setChecked(true);
            } else {
                mRelease.setChecked(true);
            }
        }

        if (mLogin_userName.getText().length() == 0 || mLogin_passWord.getText().length() == 0) {
            mBtn_login.setEnabled(false);
        }

        //退出登录重现上次的账号以及头像
        String userName = SharePreferenceManager.getCachedUsername();
        String userAvatar = SharePreferenceManager.getCachedAvatarPath();
        Bitmap bitmap = BitmapLoader.getBitmapFromFile(userAvatar, mAvatarSize, mAvatarSize);
        if (bitmap != null) {
            mDe_login_logo.setImageBitmap(bitmap);
        } else {
            mDe_login_logo.setImageResource(R.drawable.no_avatar);
        }
        mLogin_userName.setText(userName);
        if (userName != null)
            mLogin_userName.setSelection(userName.length());//设置光标位置

        //当把用户名删除后头像要换成默认的
        mLogin_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDe_login_logo.setImageResource(R.drawable.no_avatar);
                if (mLogin_userName.getText().length() == 0 || mLogin_passWord.getText().length() == 0) {
                    mBtn_login.setEnabled(false);
                } else {
                    mBtn_login.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLogin_passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mLogin_userName.getText().length() == 0 || mLogin_passWord.getText().length() == 0) {
                    mBtn_login.setEnabled(false);
                } else {
                    mBtn_login.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public String getUserId() {
        return mLogin_userName.getText().toString().trim();
    }

    public String getPassword() {
        return mLogin_passWord.getText().toString().trim();
    }

    public static Boolean invokeIsTestEvn() {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("isTestEnvironment");
            Object result = method.invoke(null);
            return (Boolean) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void swapEnvironment(Context context, boolean isTest) {
        try {
            Method method = JMessageClient.class.getDeclaredMethod("swapEnvironment", Context.class, Boolean.class);
            method.invoke(null, context, isTest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
