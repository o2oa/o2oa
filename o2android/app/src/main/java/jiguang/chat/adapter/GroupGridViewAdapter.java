package jiguang.chat.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
import jiguang.chat.activity.GroupGridViewActivity;
import jiguang.chat.utils.BitmapLoader;

/**
 * Created by ${chenyn} on 2017/5/8.
 */

public class GroupGridViewAdapter extends BaseAdapter {
    private Activity mContext;
    private List<UserInfo> mMemberList;
    private boolean mIsCreator;
    private LayoutInflater mInflater;
    private int mAvatarSize;
    private int mCurrentNum;


    public GroupGridViewAdapter(GroupGridViewActivity context, List<UserInfo> memberInfoList, boolean isCreator, int size) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mMemberList = memberInfoList;
        this.mIsCreator = isCreator;
        this.mAvatarSize = size;
        mCurrentNum = mMemberList.size();

    }

    @Override
    public int getCount() {
        return mIsCreator ? mMemberList.size() + 2 : mMemberList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ItemViewTag viewTag;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_group, null);
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.grid_avatar),
                    (TextView) convertView.findViewById(R.id.grid_name),
                    (ImageView) convertView.findViewById(R.id.grid_delete_icon));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        //群成员
        if (position < mMemberList.size()) {
            UserInfo userInfo = mMemberList.get(position);
            viewTag.icon.setVisibility(View.VISIBLE);
            viewTag.name.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(userInfo.getAvatar())) {
                File file = userInfo.getAvatarFile();
                if (file != null && file.isFile()) {
                    Bitmap bitmap = BitmapLoader.getBitmapFromFile(file.getAbsolutePath(),
                            mAvatarSize, mAvatarSize);
                    viewTag.icon.setImageBitmap(bitmap);
                } else {
                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int status, String desc, Bitmap bitmap) {
                            if (status == 0) {
                                viewTag.icon.setImageBitmap(bitmap);
                            } else {
                                viewTag.icon.setImageResource(R.drawable.jmui_head_icon);
                            }
                        }
                    });
                }
            } else {
                viewTag.icon.setImageResource(R.drawable.jmui_head_icon);
            }

            String displayName = userInfo.getDisplayName();
            viewTag.name.setText(displayName);
        }
        viewTag.deleteIcon.setVisibility(View.INVISIBLE);
        if (position < mCurrentNum) {
            viewTag.icon.setVisibility(View.VISIBLE);
            viewTag.name.setVisibility(View.VISIBLE);
        } else if (position == mCurrentNum) {
            viewTag.icon.setImageResource(R.drawable.chat_detail_add);
            viewTag.icon.setVisibility(View.VISIBLE);
            viewTag.name.setVisibility(View.INVISIBLE);

            //设置删除群成员按钮
        } else if (position == mCurrentNum + 1) {
            if (mIsCreator && mCurrentNum > 1) {
                viewTag.icon.setImageResource(R.drawable.chat_detail_del);
                viewTag.icon.setVisibility(View.VISIBLE);
                viewTag.name.setVisibility(View.INVISIBLE);
            } else {
                viewTag.icon.setVisibility(View.GONE);
                viewTag.name.setVisibility(View.GONE);
            }
            //空白项
        } else {
            viewTag.icon.setVisibility(View.INVISIBLE);
            viewTag.name.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ItemViewTag {

        private ImageView icon;
        private ImageView deleteIcon;
        private TextView name;

        public ItemViewTag(ImageView icon, TextView name, ImageView deleteIcon) {
            this.icon = icon;
            this.deleteIcon = deleteIcon;
            this.name = name;
        }
    }
}
