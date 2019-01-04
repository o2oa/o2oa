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
 * Created by ${chenyn} on 2017/9/6.
 */

public class AlreadyReadAdapter extends BaseAdapter {
    List<UserInfo> alreadyRead = JGApplication.alreadyRead;
    private Context mContext;
    private LayoutInflater mInflater;

    public AlreadyReadAdapter(MessageAlreadyReadFragment context) {
        this.mContext = context.getContext();
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return alreadyRead.size();
    }

    @Override
    public Object getItem(int position) {
        return alreadyRead.get(position);
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
            convertView = mInflater.inflate(R.layout.receipt_already_read, parent, false);
            holder.iv_alreadyRead = (ImageView) convertView.findViewById(R.id.iv_alreadyRead);
            holder.tv_alreadyRead = (TextView) convertView.findViewById(R.id.tv_alreadyRead);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserInfo info = alreadyRead.get(position);
        String displayName = info.getDisplayName();
        File avatarFile = info.getAvatarFile();
        if (avatarFile != null && avatarFile.exists()) {
            holder.iv_alreadyRead.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getAbsolutePath()));
        } else {
            info.getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    if (i == 0) {
                        holder.iv_alreadyRead.setImageBitmap(bitmap);
                    } else {
                        holder.iv_alreadyRead.setImageResource(R.drawable.jmui_head_icon);
                    }
                }
            });
        }
        holder.tv_alreadyRead.setText(displayName);
        return convertView;
    }

    public static class ViewHolder {
        ImageView iv_alreadyRead;
        TextView tv_alreadyRead;
    }
}
