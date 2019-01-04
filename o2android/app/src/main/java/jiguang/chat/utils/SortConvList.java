package jiguang.chat.utils;

import java.util.Comparator;

import cn.jpush.im.android.api.model.Conversation;


public class SortConvList implements Comparator<Conversation> {
    @Override
    public int compare(Conversation o, Conversation o2) {
        if (o.getLastMsgDate() > o2.getLastMsgDate()) {
            return -1;
        } else if (o.getLastMsgDate() < o2.getLastMsgDate()) {
            return 1;
        }
        return 0;
    }
}
