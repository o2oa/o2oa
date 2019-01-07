package jiguang.chat.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.IntegerCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;

/**
 * Created by ${chenyn} on 2017/2/21.
 */

public class MeView extends LinearLayout implements SlipButton.OnChangedListener{
    private Context mContext;
    private TextView mSignatureTv;
    private TextView mNickNameTv;
    private SelectableRoundedImageView mTakePhotoBtn;
    private RelativeLayout mSet_pwd;
    public SlipButton mSet_noDisturb;
    private RelativeLayout mOpinion;
    private RelativeLayout mAbout;
    private RelativeLayout mExit;
    private int mWidth;
    private int mHeight;
    private RelativeLayout mRl_personal;

    public MeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

    }


    public void initModule(float density, int width) {
        mTakePhotoBtn = (SelectableRoundedImageView) findViewById(R.id.take_photo_iv);
        mNickNameTv = (TextView) findViewById(R.id.nickName);
        mSignatureTv = (TextView) findViewById(R.id.signature);
        mSet_pwd = (RelativeLayout) findViewById(R.id.setPassword);
        mSet_noDisturb = (SlipButton) findViewById(R.id.btn_noDisturb);
        mOpinion = (RelativeLayout) findViewById(R.id.opinion);
        mAbout = (RelativeLayout) findViewById(R.id.about);
        mExit = (RelativeLayout) findViewById(R.id.exit);
        mRl_personal = (RelativeLayout) findViewById(R.id.rl_personal);
        mSet_noDisturb.setOnChangedListener(R.id.btn_noDisturb, this);

        mWidth = width;
        mHeight = (int) (190 * density);


        final Dialog dialog = DialogCreator.createLoadingDialog(mContext, mContext.getString(R.string.jmui_loading));
        dialog.show();
        //初始化是否全局免打扰
        JMessageClient.getNoDisturbGlobal(new IntegerCallback() {
            @Override
            public void gotResult(int responseCode, String responseMessage, Integer value) {
                dialog.dismiss();
                if (responseCode == 0) {
                    mSet_noDisturb.setChecked(value == 1);
                } else {
                    ToastUtil.shortToast(mContext, responseMessage);
                }
            }
        });


    }

    public void setListener(OnClickListener onClickListener) {
        mSet_pwd.setOnClickListener(onClickListener);
        mOpinion.setOnClickListener(onClickListener);
        mAbout.setOnClickListener(onClickListener);
        mExit.setOnClickListener(onClickListener);
        mRl_personal.setOnClickListener(onClickListener);


    }

    public void showPhoto(Bitmap avatarBitmap) {
        if (avatarBitmap != null) {
            mTakePhotoBtn.setImageBitmap(avatarBitmap);
        }else {
            mTakePhotoBtn.setImageResource(R.drawable.rc_default_portrait);
        }

    }

    public void showNickName(UserInfo myInfo) {
        if (!TextUtils.isEmpty(myInfo.getNickname().trim())) {
            mNickNameTv.setText(myInfo.getNickname());
        } else {
            mNickNameTv.setText(myInfo.getUserName());
        }
        mSignatureTv.setText(myInfo.getSignature());
    }

    @Override
    public void onChanged(int id, final boolean checkState) {
        switch (id) {
            case R.id.btn_noDisturb:
                final Dialog loadingDialog = DialogCreator.createLoadingDialog(mContext,
                        mContext.getString(R.string.jmui_loading));
                loadingDialog.show();
                JMessageClient.setNoDisturbGlobal(checkState ? 1 : 0, new BasicCallback() {
                    @Override
                    public void gotResult(int status, String desc) {
                        loadingDialog.dismiss();
                        if (status == 0) {
                        } else {
                            mSet_noDisturb.setChecked(!checkState);
                            ToastUtil.shortToast(mContext, "设置失败");
                        }
                    }
                });
                break;
        }
    }

}
