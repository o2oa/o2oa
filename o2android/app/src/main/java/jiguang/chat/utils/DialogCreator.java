package jiguang.chat.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.FileContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.application.JGApplication;
import jiguang.chat.controller.ActivityController;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;


public class DialogCreator {
    public static Dialog mLoadingDialog;

    public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(IdHelper.getLayout(context, "jmui_loading_view"), null);
        RelativeLayout layout = (RelativeLayout) v.findViewById(IdHelper.getViewID(context, "jmui_dialog_view"));
        ImageView mLoadImg = (ImageView) v.findViewById(IdHelper.getViewID(context, "jmui_loading_img"));
        TextView mLoadText = (TextView) v.findViewById(IdHelper.getViewID(context, "jmui_loading_txt"));
        AnimationDrawable mDrawable = (AnimationDrawable) mLoadImg.getDrawable();
        mDrawable.start();
        mLoadText.setText(msg);
        final Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return loadingDialog;
    }

    public static Dialog createBaseCustomDialog(Context context, String title, String text,
                                                View.OnClickListener onClickListener) {
        Dialog baseDialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_base"), null);
        baseDialog.setContentView(v);
        TextView titleTv = (TextView) v.findViewById(IdHelper.getViewID(context, "jmui_dialog_base_title_tv"));
        TextView textTv = (TextView) v.findViewById(IdHelper.getViewID(context, "jmui_dialog_base_text_tv"));
        Button confirmBtn = (Button) v.findViewById(IdHelper.getViewID(context, "jmui_dialog_base_confirm_btn"));
        titleTv.setText(title);
        textTv.setText(text);
        confirmBtn.setOnClickListener(onClickListener);
        baseDialog.setCancelable(false);
        return baseDialog;
    }

    public static Dialog createBaseDialogWithTitle(Context context, String title, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView titleTv = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        titleTv.setText(title);
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createDelConversationDialog(Context context,
                                                     View.OnClickListener listener, boolean isTop) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View v = LayoutInflater.from(context).inflate(
                IdHelper.getLayout(context, "jmui_dialog_delete_conv"), null);
        dialog.setContentView(v);
        final LinearLayout deleteLl = (LinearLayout) v.findViewById(IdHelper
                .getViewID(context, "jmui_delete_conv_ll"));
        final LinearLayout top = (LinearLayout) v.findViewById(IdHelper
                .getViewID(context, "jmui_top_conv_ll"));
        TextView tv_top = (TextView) v.findViewById(IdHelper.getViewID(context, "tv_conv_top"));
        if (isTop) {
            tv_top.setText("会话置顶");
        } else {
            tv_top.setText("取消置顶");
        }

        deleteLl.setOnClickListener(listener);
        top.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createSavePictureDialog(Context context,
                                                 View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View v = LayoutInflater.from(context).inflate(
                IdHelper.getLayout(context, "jmui_dialog_delete_conv"), null);
        dialog.setContentView(v);
        final LinearLayout deleteLl = (LinearLayout) v.findViewById(IdHelper
                .getViewID(context, "jmui_delete_conv_ll"));
        final LinearLayout top = (LinearLayout) v.findViewById(IdHelper
                .getViewID(context, "jmui_top_conv_ll"));
        TextView text = (TextView) v.findViewById(IdHelper.getViewID(context, "tv_conv_top"));
        text.setText("转发");

        TextView textView = (TextView) v.findViewById(IdHelper.getViewID(context, "tv_dialogText"));
        textView.setText("保存到手机");

        top.setOnClickListener(listener);
        deleteLl.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createDelRecommendDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View v = LayoutInflater.from(context).inflate(
                IdHelper.getLayout(context, "jmui_dialog_del_recommend"), null);
        dialog.setContentView(v);
        final LinearLayout deleteLl = (LinearLayout) v.findViewById(IdHelper
                .getViewID(context, "jmui_del_recommend_ll"));
        deleteLl.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createLongPressMessageDialog(Context context, String title, boolean hide,
                                                      View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context, "jmui_dialog_msg_alert"), null);
        dialog.setContentView(view);
        Button copyBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_copy_msg_btn"));
        Button deleteBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_delete_msg_btn"));
        if (hide) {
            copyBtn.setVisibility(View.GONE);
        }
        copyBtn.setOnClickListener(listener);
        deleteBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createResendDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(
                IdHelper.getLayout(context, "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        Button cancelBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        Button resendBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancelBtn.setOnClickListener(listener);
        resendBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createDeleteMessageDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_base_with_button"), null);
        dialog.setContentView(v);
        TextView title = (TextView) v.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "jmui_clear_history_confirm_title"));
        final Button cancel = (Button) v.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) v.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createExitGroupDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_base_with_button"), null);
        dialog.setContentView(v);
        TextView title = (TextView) v.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "jmui_delete_group_confirm_title"));
        final Button cancel = (Button) v.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) v.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createSetAvatarDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_set_avatar"), null);
        dialog.setContentView(view);
        Button takePhotoBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_take_photo_btn"));
        Button pickPictureBtn = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_pick_picture_btn"));
        takePhotoBtn.setOnClickListener(listener);
        pickPictureBtn.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createLogoutDialog(Context context, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        title.setText(IdHelper.getString(context, "jmui_logout_confirm"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createLogoutStatusDialog(Context context, String title, View.OnClickListener listener) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        View view = LayoutInflater.from(context).inflate(IdHelper.getLayout(context,
                "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView titleTv = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        titleTv.setText(title);
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        cancel.setText("退出");
        commit.setText("重新登录");
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createResetPwdDialog(final Context context) {
        final Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_reset_password"), null);
        dialog.setContentView(view);
        final EditText pwdEt = (EditText) view.findViewById(IdHelper.getViewID(context, "jmui_password_et"));
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == IdHelper.getViewID(context, "jmui_cancel_btn")) {
                    dialog.cancel();
                } else {
                    String input = pwdEt.getText().toString().trim();
                    if (JMessageClient.isCurrentUserPasswordValid(input)) {
                        Intent intent = new Intent();
                        intent.putExtra("oldPassword", input);
//                        intent.setClass(context, ResetPasswordActivity.class);
                        context.startActivity(intent);
                        dialog.cancel();
                    } else {
                        Toast toast = Toast.makeText(context, IdHelper.getString(context,
                                "jmui_input_password_error_toast"), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        };
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createDeleteMemberDialog(Context context, View.OnClickListener listener,
                                                  boolean isSingle) {
        Dialog dialog = new Dialog(context, IdHelper.getStyle(context, "jmui_default_dialog_style"));
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(IdHelper.getLayout(context, "jmui_dialog_base_with_button"), null);
        dialog.setContentView(view);
        TextView title = (TextView) view.findViewById(IdHelper.getViewID(context, "jmui_title"));
        if (isSingle) {
            title.setText(IdHelper.getString(context, "jmui_delete_member_confirm_hint"));
        } else {
            title.setText(IdHelper.getString(context, "jmui_delete_confirm_hint"));
        }
        final Button cancel = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_cancel_btn"));
        final Button commit = (Button) view.findViewById(IdHelper.getViewID(context, "jmui_commit_btn"));
        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);
        commit.setText(IdHelper.getString(context, "jmui_confirm"));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static Dialog createBusinessCardDialog(Context context, View.OnClickListener listener,
                                                  String nameTo, String name, String avatarPath) {
        Dialog dialog = new Dialog(context, R.style.jmui_default_dialog_style);
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.jmui_dialog_business_card, null);
        dialog.setContentView(view);
        TextView cardTo = (TextView) view.findViewById(R.id.tv_businessCardTo);
        TextView cardName = (TextView) view.findViewById(R.id.tv_businessCard);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_businessHead);

        cardTo.setText(nameTo);
        cardName.setText(name);
        if (avatarPath != null) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
        }

        final Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        final Button commit = (Button) view.findViewById(R.id.btn_sure);

        cancel.setOnClickListener(listener);
        commit.setOnClickListener(listener);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public static void createForwardMsg(final Context context, int mWidth, final boolean isSingle, final Conversation conv,
                                        final GroupInfo groupInfo, String groupName, final UserInfo userInfo) {
        final Dialog dialog = new Dialog(context, R.style.jmui_default_dialog_style);
        View forwardView = LayoutInflater.from(context).inflate(R.layout.jmui_dialog_forward_text_button, null);
        dialog.setContentView(forwardView);
        TextView name = (TextView) forwardView.findViewById(R.id.tv_forward_name);
        TextView content = (TextView) forwardView.findViewById(R.id.tv_forward_text);
        ImageView imageContent = (ImageView) forwardView.findViewById(R.id.iv_forward_image);
        ImageView videoContent = (ImageView) forwardView.findViewById(R.id.iv_forward_video);
        FrameLayout videoLayout = (FrameLayout) forwardView.findViewById(R.id.fl_forward_video);
        final Button cancel = (Button) forwardView.findViewById(R.id.btn_cancel);
        final Button commit = (Button) forwardView.findViewById(R.id.btn_send);

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();

        if (conv != null) {
            if (conv.getType() == ConversationType.single) {
                name.setText(((UserInfo) conv.getTargetInfo()).getDisplayName());
            } else {
                name.setText(conv.getTitle());
            }
        }
        if (groupName != null) {
            name.setText(groupName);
        }
        final Message message = JGApplication.forwardMsg.get(0);

        switch (message.getContentType()) {
            case text:
                content.setVisibility(View.VISIBLE);
                TextContent text = (TextContent) message.getContent();
                if (text.getStringExtra("businessCard") != null) {
                    content.setText("[名片]");
                } else {
                    content.setText(text.getText());
                }
                break;
            case voice:
                content.setVisibility(View.VISIBLE);
                content.setText("[语音消息]");
                break;
            case image:
                imageContent.setVisibility(View.VISIBLE);
                ImageContent image = (ImageContent) message.getContent();
                String imagePath = image.getLocalThumbnailPath();
                imageContent.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                break;
            case file:
                FileContent fileVideo = (FileContent) message.getContent();
                String videoExtra = fileVideo.getStringExtra("video");
                content.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(videoExtra)) {
                    content.setText("[小视频]");
                } else {
                    content.setText("[文件]" + fileVideo.getFileName());
                }
                break;
            case location:
                LocationContent locationContent = (LocationContent) message.getContent();
                content.setVisibility(View.VISIBLE);
                content.setText("[位置]" + locationContent.getAddress());
                break;
            default:
                break;
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingDialog = DialogCreator.createLoadingDialog(context, "发送中");
                mLoadingDialog.show();
                String userName = null;
                String appKey = null;

                if (userInfo != null) {
                    userName = userInfo.getUserName();
                    appKey = userInfo.getAppKey();
                }
                Conversation conversation = null;
                if (userInfo == null && groupInfo == null) {
                    conversation = conv;
                } else {
                    if (isSingle) {
                        conversation = JMessageClient.getSingleConversation(userName, appKey);
                        if (conversation == null) {
                            conversation = Conversation.createSingleConversation(userName, appKey);
                            EventBus.getDefault().post(new Event.Builder()
                                    .setType(EventType.createConversation)
                                    .setConversation(conversation)
                                    .build());
                        }
                    } else {
                        conversation = JMessageClient.getGroupConversation(groupInfo.getGroupID());
                        if (conversation == null) {
                            conversation = Conversation.createGroupConversation(groupInfo.getGroupID());
                            EventBus.getDefault().post(new Event.Builder()
                                    .setType(EventType.createConversation)
                                    .setConversation(conversation)
                                    .build());
                        }
                    }
                }
                MessageSendingOptions options = new MessageSendingOptions();
                options.setNeedReadReceipt(false);
                JMessageClient.forwardMessage(message, conversation, options, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        mLoadingDialog.dismiss();
                        dialog.dismiss();
                        if (i == 0) {
                            Toast.makeText(context, "已发送", Toast.LENGTH_SHORT).show();
                            SharePreferenceManager.setIsOpen(true);
                            ActivityController.finishAll();
                        } else {
                            HandleResponseCode.onHandle(context, i, false);
                        }
                    }
                });
            }
        });
    }

}
