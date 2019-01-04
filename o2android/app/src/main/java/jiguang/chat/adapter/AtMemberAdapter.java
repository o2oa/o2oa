package jiguang.chat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import jiguang.chat.utils.ViewHolder;
import jiguang.chat.utils.pinyin.HanziToPinyin;

public class AtMemberAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

    private Context mContext;
    private List<UserInfo> mList = new ArrayList<UserInfo>();
    private String letter;
    private int[] mSectionIndices;
    private String[] mSectionLetters;

    public AtMemberAdapter(Context context, List<UserInfo> list) {
        this.mContext = context;
        this.mList = list;
        //数量从0开始
        mSectionIndices = getSectionIndices();
        //所有名字的首字母
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        if (mList.size() > 0) {
            char lastFirstChar = getLetter(mList.get(0)).charAt(0);

            sectionIndices.add(0);
            for (int i = 1; i < mList.size(); i++) {
                if (getLetter(mList.get(i)).charAt(0) != lastFirstChar) {
                    lastFirstChar = getLetter(mList.get(i)).charAt(0);
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
                letters[i] = getLetter(mList.get(mSectionIndices[i]));
            }
            return letters;
        }
        return null;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        UserInfo userInfo = mList.get(position);
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.section_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        String letters = getLetter(userInfo);
        holder.text.setText(letters);
        return convertView;
    }

    private static class HeaderViewHolder {
        TextView text;
    }

    @Override
    public long getHeaderId(int position) {
        return getLetter(mList.get(position)).charAt(0);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private String getLetter(UserInfo userInfo) {
        String displayName = userInfo.getDisplayName();
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance()
                .get(displayName);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        String sortString = sb.toString().substring(0, 1).toUpperCase();
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase();
        } else {
            letter = "#";
        }
        return letter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_contact, null);
        }
        final ImageView headIcon = ViewHolder.get(convertView, R.id.head_icon_iv);
        TextView name = ViewHolder.get(convertView, R.id.name);

        final UserInfo userInfo = mList.get(position);

        userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int status, String desc, Bitmap bitmap) {
                if (status == 0) {
                    headIcon.setImageBitmap(bitmap);
                } else {
                    headIcon.setImageResource(R.drawable.jmui_head_icon);
                }
            }
        });

        String displayName = userInfo.getNotename();
        if (TextUtils.isEmpty(displayName)) {
            displayName = userInfo.getNickname();
            if (TextUtils.isEmpty(displayName)) {
                displayName = userInfo.getUserName();
            }
        }
        name.setText(displayName);

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
}
