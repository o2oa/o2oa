package jiguang.chat.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;
import jiguang.chat.utils.pinyin.CharacterParser;

/**
 * Created by ${chenyn} on 2017/5/2.
 */

public class SearchGroupListAdapter extends BaseAdapter {
    private List<GroupInfo> filterGroupId;
    private Context mContext;
    private String mFilterString;
    public SearchGroupListAdapter(Context context, List<GroupInfo> filterGroupId, String filterStr) {
        this.mContext = context;
        this.filterGroupId = filterGroupId;
        this.mFilterString = filterStr;
    }

    @Override
    public int getCount() {
        if (filterGroupId != null) {
            return filterGroupId.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (filterGroupId == null)
            return null;

        if (position >= filterGroupId.size())
            return null;

        return filterGroupId.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class GroupViewHolder {
        SelectableRoundedImageView portraitImageView;
        TextView nameSingleTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;
        GroupInfo groupInfo = (GroupInfo) getItem(position);
        String groupName;
        if (convertView == null) {
            viewHolder = new GroupViewHolder();
            convertView = View.inflate(mContext, R.layout.item_filter_group_list, null);
            viewHolder.portraitImageView = (SelectableRoundedImageView) convertView.findViewById(R.id.item_iv_group_image);
            viewHolder.nameSingleTextView = (TextView) convertView.findViewById(R.id.item_tv_group_name_single);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        if (groupInfo != null) {
            viewHolder.nameSingleTextView.setVisibility(View.VISIBLE);

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

            viewHolder.nameSingleTextView.setText(CharacterParser.getInstance().getColoredGroupName(mFilterString, groupName));

        } else {
            viewHolder.nameSingleTextView.setVisibility(View.GONE);
        }
        return convertView;
    }

    private String getGroupName(List<UserInfo> groupMembers, StringBuilder builder) {
        for (UserInfo info : groupMembers) {
            String noteName = info.getNotename();
            if (TextUtils.isEmpty(noteName)) {
                noteName = info.getNickname();
                if (TextUtils.isEmpty(noteName)) {
                    noteName = info.getUserName();
                }
            }
            builder.append(noteName);
            builder.append(",");
        }

        return builder.substring(0, builder.lastIndexOf(","));
    }
}
