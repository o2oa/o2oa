package jiguang.chat.activity.receiptmessage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.application.JGApplication;

/**
 * Created by ${chenyn} on 2017/9/5.
 */

public class NotReadAdapter extends BaseAdapter {

    List<UserInfo> unRead = JGApplication.unRead;
    private Context mContext;
    private LayoutInflater mInflater;


    public NotReadAdapter(MessageNotReadFragment context) {
        this.mContext = context.getContext();
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return unRead.size();
    }

    @Override
    public Object getItem(int position) {
        return unRead.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.receipt_no_read, parent, false);
            holder.iv_noRead = (ImageView) convertView.findViewById(R.id.iv_noRead);
            holder.tv_noRead = (TextView) convertView.findViewById(R.id.tv_noRead);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserInfo info = unRead.get(position);
        String displayName = info.getDisplayName();
        File avatarFile = info.getAvatarFile();
        if (avatarFile != null && avatarFile.exists()) {
            holder.iv_noRead.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
        } else {
            info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    if (i == 0) {
                        holder.iv_noRead.setImageBitmap(bitmap);
                    } else {
                        holder.iv_noRead.setImageResource(R.drawable.jmui_head_icon);
                    }
                }
            });
        }
        holder.tv_noRead.setText(displayName);
        return convertView;
    }

    public static class ViewHolder {
        ImageView iv_noRead;
        TextView tv_noRead;
    }
}
