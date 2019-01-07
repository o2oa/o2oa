package jiguang.chat.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.eventbus.EventBus;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.activity.ChatActivity;
import jiguang.chat.application.JGApplication;
import jiguang.chat.entity.Event;
import jiguang.chat.entity.EventType;
import jiguang.chat.utils.DialogCreator;
import jiguang.chat.utils.HandleResponseCode;

/**
 * Created by ${chenyn} on 2017/4/26.
 */

public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private String groupName;
    private Map<Long, String> mGroupName = new HashMap<>();
    private List<GroupInfo> mGroupInfo;
    private boolean mIsForward;
    private boolean mBusiness;
    private Dialog mLoadingDialog;
    private Dialog mDialog;
    private int mWidth;
    private String mUserName;
    private String mAppKey;
    private String mAvatarPath;

    public GroupListAdapter(Context context, List<GroupInfo> groupInfo, boolean isFromForward, int width,
                            boolean isBusinessCard, String userName, String appKey, String path) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mGroupInfo = groupInfo;
        this.mIsForward = isFromForward;
        this.mWidth = width;
        this.mBusiness = isBusinessCard;
        this.mUserName = userName;
        this.mAppKey = appKey;
        this.mAvatarPath = path;
    }

    @Override
    public int getCount() {
        return mGroupInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String getGroupName(List<UserInfo> groupMembers, StringBuilder builder) {
        for (UserInfo info : groupMembers) {
            String noteName = info.getDisplayName();
            builder.append(noteName);
            builder.append(",");
        }

        return builder.substring(0, builder.lastIndexOf(","));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_group_list, parent, false);
            holder.itemLl = (LinearLayout) convertView.findViewById(R.id.group_ll);
            holder.avatar = (ImageView) convertView.findViewById(R.id.group_iv);
            holder.groupName = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GroupInfo groupInfo = mGroupInfo.get(position);
        if (TextUtils.isEmpty(groupInfo.getGroupName())) {
            //Conversation groupConversation = JMessageClient.getGroupConversation(groupId);
            //群组名是null的话,手动拿出5个名字拼接
            List<UserInfo> groupMembers = groupInfo.getGroupMembers();

            StringBuilder builder = new StringBuilder();
            if (groupMembers.size() <= 5) {
                groupName = getGroupName(groupMembers, builder);
            } else {
                List<UserInfo> newGroupMember = groupMembers.subList(0, 5);
                groupName = getGroupName(newGroupMember, builder);
            }
        } else {
            groupName = groupInfo.getGroupName();
        }

        mGroupName.put(groupInfo.getGroupID(), groupName);
        holder.groupName.setText(groupName);

        groupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    holder.avatar.setImageBitmap(bitmap);
                } else {
                    holder.avatar.setImageResource(R.drawable.group);
                }
            }
        });

        if (mIsForward) {
            holder.itemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogCreator.createForwardMsg(mContext, mWidth, false, null, groupInfo, mGroupName.get(groupInfo.getGroupID()), null);
                }
            });
        } else if (mBusiness) {
            holder.itemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View.OnClickListener listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = v.getId();
                            if (i == R.id.btn_cancel) {
                                mDialog.dismiss();
                            } else if (i == R.id.btn_sure) {
                                mDialog.dismiss();
                                mLoadingDialog = DialogCreator.createLoadingDialog(mContext,
                                        mContext.getString(R.string.btn_send));
                                mLoadingDialog.show();
                                //把名片的userName和appKey通过extra发送给对方
                                TextContent content = new TextContent("推荐了一张名片");
                                content.setStringExtra("userName", mUserName);
                                content.setStringExtra("appKey", mAppKey);
                                content.setStringExtra("businessCard", "businessCard");

                                Conversation conversation = JMessageClient.getGroupConversation(groupInfo.getGroupID());
                                if (conversation == null) {
                                    conversation = Conversation.createGroupConversation(groupInfo.getGroupID());
                                    EventBus.getDefault().post(new Event.Builder()
                                            .setType(EventType.createConversation)
                                            .setConversation(conversation)
                                            .build());
                                }
                                Message textMessage = conversation.createSendMessage(content);
                                MessageSendingOptions options = new MessageSendingOptions();
                                options.setNeedReadReceipt(false);
                                JMessageClient.sendMessage(textMessage, options);
                                textMessage.setOnSendCompleteCallback(new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            HandleResponseCode.onHandle(mContext, i, false);
                                        }
                                    }
                                });
                                mLoadingDialog.dismiss();

                            }
                        }
                    };
                    mDialog = DialogCreator.createBusinessCardDialog(mContext, listener, groupInfo.getGroupName(), mUserName, mAvatarPath);
                    mDialog.getWindow().setLayout((int) (0.8 * mWidth), WindowManager.LayoutParams.WRAP_CONTENT);
                    mDialog.show();
                }
            });
        } else {
            holder.itemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Conversation groupConversation = JMessageClient.getGroupConversation(groupInfo.getGroupID());
                    if (groupConversation == null) {
                        groupConversation = Conversation.createGroupConversation(groupInfo.getGroupID());
                        EventBus.getDefault().post(new Event.Builder()
                                .setType(EventType.createConversation)
                                .setConversation(groupConversation)
                                .build());
                    }

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra(JGApplication.CONV_TITLE, mGroupName.get(groupInfo.getGroupID()));
                    intent.putExtra(JGApplication.GROUP_ID, groupInfo.getGroupID());
                    mContext.startActivity(intent);

                }
            });
        }
        return convertView;
    }


    private static class ViewHolder {
        LinearLayout itemLl;
        TextView groupName;
        ImageView avatar;
    }
}
