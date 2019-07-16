package jiguang.chat.utils.pinyin;


import java.util.Comparator;

import jiguang.chat.database.FriendEntry;

/**
 *
 * @author
 *
 */
public class PinyinComparator implements Comparator<FriendEntry> {

    public int compare(FriendEntry o1, FriendEntry o2) {
        if (o1.letter.equals("@")
                || o2.letter.equals("#")) {
            return -1;
        } else if (o1.letter.equals("#")
                || o2.letter.equals("@")) {
            return 1;
        } else {
            return o1.letter.compareTo(o2.letter);
        }
    }



}
