package jiguang.chat.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.utils.CommonUtils;
import jiguang.chat.utils.ToastUtil;

public class ResetPasswordActivity extends BaseActivity {

    private EditText mOld_password;
    private EditText mNew_password;
    private EditText mRe_newPassword;
    private Button mBtn_sure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initView();
        initData();

    }

    private void initView() {
        initTitle(true, true, "修改密码", "", false, "保存");
        mOld_password = (EditText) findViewById(R.id.old_password);
        mNew_password = (EditText) findViewById(R.id.new_password);
        mRe_newPassword = (EditText) findViewById(R.id.re_newPassword);
        mBtn_sure = (Button) findViewById(R.id.btn_sure);
    }

    private void initData() {
        mBtn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPsw = mOld_password.getText().toString().trim();
                String newPsw = mNew_password.getText().toString().trim();
                String reNewPsw = mRe_newPassword.getText().toString().trim();

                boolean passwordValid = JMessageClient.isCurrentUserPasswordValid(oldPsw);
                if (passwordValid) {
                    if (newPsw.equals(reNewPsw)) {
                        final ProgressDialog dialog = new ProgressDialog(ResetPasswordActivity.this);
                        dialog.setMessage(getString(R.string.modifying_hint));
                        dialog.show();
                        JMessageClient.updateUserPassword(oldPsw, newPsw, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                dialog.dismiss();
                                if (responseCode == 0) {
                                    ToastUtil.shortToast(ResetPasswordActivity.this, "修改成功");
                                }else {
                                    ToastUtil.shortToast(ResetPasswordActivity.this, "修改失败, 新密码要在4-128字节之间");
                                }
                            }
                        });
                    } else {
                        ToastUtil.shortToast(ResetPasswordActivity.this, "两次输入不相同");
                    }
                } else {
                    ToastUtil.shortToast(ResetPasswordActivity.this, "原密码不正确");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CommonUtils.hideKeyboard(this);
    }
}
