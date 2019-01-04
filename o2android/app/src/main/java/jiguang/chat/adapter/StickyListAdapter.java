package jiguang.chat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.activity.FriendInfoActivity;
import jiguang.chat.application.JGApplication;
import jiguang.chat.database.FriendEntry;
import jiguang.chat.utils.photochoose.SelectableRoundedImageView;

/**
 * Created by ${chenyn} on 2017/3/16.
 */

public class StickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {
    private Context mContext;
    private boolean mIsSelectMode;
    private LayoutInflater mInflater;
    private List<FriendEntry> mData;
    private int[] mSectionIndices;
    private String[] mSectionLetters;
    private SparseBooleanArray mSelectMap = new SparseBooleanArray();
    private Map<Long, String> map = new HashMap<>();
    private TextView mSelectedNum;
    private float mDensity;
    private boolean mIsSearch;
    private String mFilterStr;


    private HorizontalScrollView scrollViewSelected;
    private GridView imageSelectedGridView;
    private CreateGroupAdapter mGroupAdapter;
    private Long mGroupID;
    private GroupInfo mTargetInfo;

    public StickyListAdapter(Context context, List<FriendEntry> list, boolean isSelectMode) {
        this.mContext = context;
        this.mData = list;
        this.mIsSelectMode = isSelectMode;
        this.mInflater = LayoutInflater.from(context);
        Activity activity = (Activity) mContext;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mSelectedNum = (TextView) activity.findViewById(R.id.selected_num);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    public StickyListAdapter(Context context, List<FriendEntry> list, boolean isSelectMode, HorizontalScrollView scrollView, GridView gridView, CreateGroupAdapter groupAdapter) {
        this.mContext = context;
        this.mData = list;
        this.mIsSelectMode = isSelectMode;
        this.scrollViewSelected = scrollView;
        this.imageSelectedGridView = gridView;
        this.mGroupAdapter = groupAdapter;
        this.mInflater = LayoutInflater.from(context);
        Activity activity = (Activity) mContext;
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mSelectedNum = (TextView) activity.findViewById(R.id.selected_num);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        if (mData.size() > 0) {
            char lastFirstChar = mData.get(0).letter.charAt(0);
            sectionIndices.add(0);
            for (int i = 1; i < mData.size(); i++) {
                if (mData.get(i).letter.charAt(0) != lastFirstChar) {
                    lastFirstChar = mData.get(i).letter.charAt(0);
                    sectionIndices.add(i);
                }
            }
            int[] sections = new int[sectionIndices.size()];
            for (int i = 0; i < sectionIndices.size(); i++) {
                sections[i] = sectionIndices.get(i);
            }
            return sections;
        }
        return null;
    }

    private String[] getSectionLetters() {
        if (null != mSectionIndices) {
            String[] letters = new String[mSectionIndices.length];
            for (int i = 0; i < mSectionIndices.length; i++) {
                letters[i] = mData.get(mSectionIndices[i]).letter;
            }
            return letters;
        }
        return null;
    }

    public void updateListView(List<FriendEntry> list, boolean isSearch, String filterStr) {
        this.mData = list;
        this.mIsSearch = isSearch;
        this.mFilterStr = filterStr;
        notifyDataSetChanged();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        FriendEntry model = mData.get(position);
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            if (Build.VERSION.SDK_INT >= 11) {
                convertView.setAlpha(0.85f);
            }
            holder.text = (TextView) convertView.findViewById(R.id.section_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        holder.text.setText(model.letter);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            holder.text.setText(model.letter);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return mData.get(position).letter.charAt(0);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.friend_item, parent, false);
            holder.itemLl = (LinearLayout) convertView.findViewById(R.id.frienditem);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.selected_cb);
            holder.avatar = (SelectableRoundedImageView) convertView.findViewById(R.id.friend_photo);
            holder.displayName = (TextView) convertView.findViewById(R.id.friendname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //所有好友列表
        final FriendEntry friend = mData.get(position);
        final String user = friend.username;
        if (friend.avatar != null) {
            if (new File(friend.avatar).exists()) {
                holder.avatar.setImageBitmap(BitmapFactory.decodeFile(friend.avatar));
            } else {
                JMessageClient.getUserInfo(friend.username, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i == 0) {
                            userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int i, String s, Bitmap bitmap) {
                                    if (i == 0) {
                                        holder.avatar.setImageBitmap(bitmap);
                                    } else {
                                        holder.avatar.setImageResource(R.drawable.jmui_head_icon);
                                    }
                                }
                            });
                        } else {
                            holder.avatar.setImageResource(R.drawable.jmui_head_icon);
                        }
                    }
                });
            }
        } else {
            holder.avatar.setImageResource(R.drawable.jmui_head_icon);
        }

        final long[] uid = new long[1];
        uid[0] = friend.uid;
        if (!mIsSearch) {
            holder.displayName.setText(friend.displayName);
        } else {
            String noteName = friend.noteName;
            String nickName = friend.nickName;
            if (!TextUtils.isEmpty(mFilterStr)) {
                if (noteName.contains(mFilterStr)) {
                    holder.displayName.setText(noteName);
                } else if (nickName.contains(mFilterStr)) {
                    holder.displayName.setText(nickName);
                } else if (user.contains(mFilterStr)) {
                    holder.displayName.setText(user);
                }
            } else {
                String name = friend.displayName;
                holder.displayName.setText(name);
            }
        }
        boolean flag = false;
        if (mTargetInfo != null) {
            List<UserInfo> groupMembers = mTargetInfo.getGroupMembers();
            for (UserInfo userinfo : groupMembers) {
                if (userinfo.getUserName().equals(friend.username)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                holder.checkBox.setBackgroundResource(R.drawable.already_check);
                holder.itemLl.setEnabled(false);
                holder.checkBox.setEnabled(false);
            } else {
                holder.checkBox.setBackgroundResource(R.drawable.pictures_select_icon);
                holder.itemLl.setEnabled(true);
                holder.checkBox.setEnabled(true);
            }

        }

//        JMessageClient.getUserInfo(user, new GetUserInfoCallback() {
//            @Override
//            public void gotResult(int responseCode, String responseMessage, UserInfo info) {
//                boolean flag = false;
//                if (responseCode == 0) {
//                    if (info.getAvatarFile() != null && info.getAvatarFile().exists()) {
//                        holder.avatar.setImageBitmap(BitmapFactory.decodeFile(info.getAvatarFile().getAbsolutePath()));
//                    } else {
//                        info.getAvatarBitmap(new GetAvatarBitmapCallback() {
//                            @Override
//                            public void gotResult(int i, String s, Bitmap bitmap) {
//                                if (i == 0) {
//                                    holder.avatar.setImageBitmap(bitmap);
//                                } else {
//                                    holder.avatar.setImageResource(R.drawable.jmui_head_icon);
//                                }
//                            }
//                        });
//                    }
//                    uid[0] = info.getUserID();
//                }
//                if (!mIsSearch) {
//                    String name = info.getDisplayName();
//                    holder.displayName.setText(name);
//                } else {
//                    String noteName = info.getNotename();
//                    String nickName = info.getNickname();
//                    if (!TextUtils.isEmpty(mFilterStr)) {
//                        if (noteName.contains(mFilterStr)) {
//                            holder.displayName.setText(noteName);
//                        } else if (nickName.contains(mFilterStr)) {
//                            holder.displayName.setText(nickName);
//                        } else if (user.contains(mFilterStr)) {
//                            holder.displayName.setText(user);
//                        }
//                    } else {
//                        String name = info.getDisplayName();
//                        holder.displayName.setText(name);
//                    }
//                }
//                if (mTargetInfo != null) {
//                    List<UserInfo> groupMembers = mTargetInfo.getGroupMembers();
//                    for (UserInfo userinfo : groupMembers) {
//                        if (userinfo.getUserName().equals(info.getUserName())) {
//                            flag = true;
//                            break;
//                        }
//                    }
//                    if (flag) {
//                        holder.checkBox.setBackgroundResource(R.drawable.already_check);
//                        holder.itemLl.setEnabled(false);
//                        holder.checkBox.setEnabled(false);
//                    } else {
//                        holder.checkBox.setBackgroundResource(R.drawable.pictures_select_icon);
//                        holder.itemLl.setEnabled(true);
//                        holder.checkBox.setEnabled(true);
//                    }
//
//                }
//            }
//        });


        if (mIsSelectMode) {
            holder.checkBox.setVisibility(View.VISIBLE);

            holder.itemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        holder.checkBox.setChecked(false);
                        map.remove(uid[0]);
                        if (mGroupAdapter != null) {
                            mGroupAdapter.setContact(getSelectedUser());
                            notifySelectAreaDataSetChanged();
                        }
                    } else {
                        holder.checkBox.setChecked(true);
                        map.put(uid[0], user);
                        addAnimation(holder.checkBox);
                        //控制创建群组时下面的gridView
                        if (mGroupAdapter != null) {
                            mGroupAdapter.setContact(getSelectedUser());
                            notifySelectAreaDataSetChanged();
                        }
                    }
                    if (map.size() > 0) {
                        mSelectedNum.setVisibility(View.VISIBLE);
                        mSelectedNum.setText(String.format(mContext.getString(R.string.selected_num),
                                map.size() + ""));
                    } else {
                        mSelectedNum.setVisibility(View.GONE);
                    }
                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        map.put(uid[0], user);
                        addAnimation(holder.checkBox);
                        if (mGroupAdapter != null) {
                            mGroupAdapter.setContact(getSelectedUser());
                            notifySelectAreaDataSetChanged();
                        }
                    } else {
                        map.remove(uid[0]);
                        if (mGroupAdapter != null) {
                            mGroupAdapter.setContact(getSelectedUser());
                            notifySelectAreaDataSetChanged();
                        }
                    }
                    if (map.size() > 0) {
                        mSelectedNum.setVisibility(View.VISIBLE);
                        mSelectedNum.setText(String.format(mContext.getString(R.string.selected_num),
                                map.size() + ""));
                    } else {
                        mSelectedNum.setVisibility(View.GONE);
                    }
                }
            });

            //如果gridView中的数据包含list中的,就选择checkedBox
            ArrayList<String> selectedUser = getSelectedUser();
            if (selectedUser.size() > 0) {
                if (selectedUser.contains(mData.get(position).username)) {
                    holder.checkBox.setChecked(true);
                } else {
                    holder.checkBox.setChecked(false);
                }
            } else {
                holder.checkBox.setChecked(false);
            }
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.itemLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, FriendInfoActivity.class);
                    intent.putExtra("fromContact", true);
                    intent.putExtra(JGApplication.TARGET_ID, friend.username);
                    intent.putExtra(JGApplication.TARGET_APP_KEY, friend.appKey);
                    mContext.startActivity(intent);
                }
            });
        }

        //点击gridView删除对应的条目
        if (imageSelectedGridView != null) {
            imageSelectedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int arg, long id) {

                    ArrayList<String> selectedUser = getSelectedUser();
                    JMessageClient.getUserInfo(selectedUser.get(arg), new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int responseCode, String responseMessage, UserInfo info) {
                            if (responseCode == 0) {
                                long userID = info.getUserID();
                                map.remove(userID);
                                if (mGroupAdapter != null) {
                                    mGroupAdapter.setContact(getSelectedUser());
                                    notifySelectAreaDataSetChanged();
                                }
                                notifyDataSetChanged();
                                mSelectedNum.setText(String.format(mContext.getString(R.string.selected_num),
                                        map.size() + ""));
                            }
                        }
                    });
                }
            });
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (null == mSectionIndices || mSectionIndices.length == 0) {
            return 0;
        }

        if (sectionIndex >= mSectionIndices.length) {
            sectionIndex = mSectionIndices.length - 1;
        } else if (sectionIndex < 0) {
            sectionIndex = 0;
        }
        return mSectionIndices[sectionIndex];
    }


    @Override
    public int getSectionForPosition(int position) {
        if (null != mSectionIndices) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (position < mSectionIndices[i]) {
                    return i - 1;
                }
            }
            return mSectionIndices.length - 1;
        }
        return -1;
    }

    public int getSectionForLetter(String letter) {
        if (null != mSectionIndices) {
            for (int i = 0; i < mSectionIndices.length; i++) {
                if (mSectionLetters[i].equals(letter)) {
                    return mSectionIndices[i] + 1;
                }
            }
        }
        return -1;
    }

    private void addAnimation(View view) {
        float[] vaules = new float[] {0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    ArrayList<String> list = new ArrayList<>();

    public ArrayList<String> getSelectedUser() {
        list.clear();
        for (Long key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }

    public void setGroupID(Long groupID) {
        mGroupID = groupID;
        if (mGroupID != 0) {
            Conversation groupConversation = JMessageClient.getGroupConversation(mGroupID);
            mTargetInfo = (GroupInfo) groupConversation.getTargetInfo();
        }

    }

    private static class HeaderViewHolder {
        TextView text;
    }

    private static class ViewHolder {
        LinearLayout itemLl;
        CheckBox checkBox;
        TextView displayName;
        SelectableRoundedImageView avatar;
    }

    private void notifySelectAreaDataSetChanged() {
        int converViewWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 46, mContext.getResources()
                .getDisplayMetrics()));
        ViewGroup.LayoutParams layoutParams = imageSelectedGridView.getLayoutParams();
        layoutParams.width = converViewWidth * getSelectedUser().size();
        layoutParams.height = converViewWidth;
        imageSelectedGridView.setLayoutParams(layoutParams);
        imageSelectedGridView.setNumColumns(getSelectedUser().size());

        try {
            final int x = layoutParams.width;
            final int y = layoutParams.height;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scrollViewSelected.scrollTo(x, y);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }
}
