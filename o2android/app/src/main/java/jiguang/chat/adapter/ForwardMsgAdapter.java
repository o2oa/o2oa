package jiguang.chat.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.List;

import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by ${chenyn} on 2017/7/16.
 */

public class ForwardMsgAdapter extends BaseAdapter {
    private Context mContext;
    private List<Conversation> mConvList;

    public ForwardMsgAdapter(Context context, List<Conversation> conversationList) {
        this.mContext = context;
        this.mConvList = conversationList;
    }

    @Override
    public int getCount() {
        return mConvList.size();
    }

    @Override
    public Object getItem(int position) {
        return mConvList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.avatar = (ImageView) convertView.findViewById(R.id.head_icon_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Conversation conversation = mConvList.get(position);
        if (conversation.getType() == ConversationType.group) {
            holder.name.setText(conversation.getTitle());
            holder.avatar.setImageResource(R.drawable.group);
        } else {
            UserInfo userInfo = (UserInfo) conversation.getTargetInfo();
            holder.name.setText(userInfo.getDisplayName());
            if (userInfo.getAvatarFile() != null) {
                holder.avatar.setImageBitmap(BitmapFactory.decodeFile(userInfo.getAvatarFile().getAbsolutePath()));
            }else {
                holder.avatar.setImageResource(R.drawable.jmui_head_icon);
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView name;
        ImageView avatar;

    }
}
