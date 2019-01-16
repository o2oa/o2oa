package jiguang.chat.utils.photochoose;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.activity.PersonalActivity;
import jiguang.chat.utils.HandleResponseCode;
import jiguang.chat.utils.SharePreferenceManager;
import jiguang.chat.utils.ToastUtil;

/**
 * Created by ${chenyn} on 2017/3/3.
 */

public class ChoosePhoto {
    public PhotoUtils photoUtils;
    private BottomMenuDialog mDialog;
    private Activity mContext;
    private boolean isFromPersonal;

    public void setInfo(PersonalActivity personalActivity, boolean isFromPersonal) {
        this.mContext = personalActivity;
        this.isFromPersonal = isFromPersonal;
    }

    public void showPhotoDialog(final Context context) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

        mDialog = new BottomMenuDialog(context);
        mDialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                photoUtils.takePicture((Activity) context);
            }
        });
        mDialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                photoUtils.selectPicture((Activity) context);
            }
        });
        mDialog.show();
    }


    public void setPortraitChangeListener(final Context context, final ImageView iv_photo, final int count) {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(final Uri uri) {
                Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                //图片设置给控件
                iv_photo.setImageBitmap(bitmap);
                if (count == 1) {
                    SharePreferenceManager.setRegisterAvatarPath(uri.getPath());
                } else {
                    SharePreferenceManager.setCachedAvatarPath(uri.getPath());
                }
                if (isFromPersonal) {
                    jiguang.chat.utils.dialog.LoadDialog.show(context);
                    JMessageClient.updateUserAvatar(new File(uri.getPath()), new BasicCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage) {
                            jiguang.chat.utils.dialog.LoadDialog.dismiss(context);
                            if (responseCode == 0) {
                                ToastUtil.shortToast(mContext, "更新成功");
                            } else {
                                ToastUtil.shortToast(mContext, "更新失败" + responseMessage);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPhotoCancel() {
            }
        });
    }

    //更新群组头像
    public void setGroupAvatarChangeListener(final Activity context, final long groupId) {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(final Uri uri) {
                jiguang.chat.utils.dialog.LoadDialog.show(context);
                JMessageClient.getGroupInfo(groupId, new GetGroupInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, GroupInfo groupInfo) {
                        if (i == 0) {
                            groupInfo.updateAvatar(new File(uri.getPath()), "", new BasicCallback() {
                                @Override
                                public void gotResult(int i, String s) {
                                    jiguang.chat.utils.dialog.LoadDialog.dismiss(context);
                                    if (i == 0) {
                                        Intent intent = new Intent();
                                        intent.putExtra("groupAvatarPath", uri.getPath());
                                        context.setResult(Activity.RESULT_OK, intent);
                                        ToastUtil.shortToast(context, "更新成功");
                                        context.finish();
                                    } else {
                                        ToastUtil.shortToast(context, "更新失败");
                                        context.finish();
                                    }
                                }
                            });
                        } else {
                            HandleResponseCode.onHandle(context, i, false);
                        }
                    }
                });

            }

            @Override
            public void onPhotoCancel() {
            }
        });
    }

}
