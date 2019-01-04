package jiguang.chat.utils;

import android.text.TextUtils;

import java.util.Comparator;

import cn.jpush.im.android.api.model.Conversation;


public class SortTopConvList implements Comparator<Conversation> {
    @Override
    public int compare(Conversation o, Conversation o2) {
        if (!TextUtils.isEmpty(o.getExtra()) && !TextUtils.isEmpty(o2.getExtra())) {
            if (Integer.parseInt(o.getExtra()) > Integer.parseInt(o2.getExtra())) {
                return 1;
            } else if (Integer.parseInt(o.getExtra()) < Integer.parseInt(o2.getExtra())) {
                return -1;
            }
        }
        return 0;
    }
}
