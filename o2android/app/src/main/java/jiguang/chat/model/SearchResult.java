package jiguang.chat.model;

import java.util.List;

import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;


public class SearchResult {


    private String filterStr;
    private List<GroupInfo> mGroupList;
    private List<UserInfo> mFriendList;

    public SearchResult() {
    }

    public void setFilterStr(String filterStr) {
        this.filterStr = filterStr;
    }
    public String getFilterStr() {
        return filterStr;
    }

    public void setGroupList(List<GroupInfo> groupList) {
        mGroupList = groupList;
    }
    public List<GroupInfo> getGroupList() {
        return mGroupList;
    }

    public void setFriendList(List<UserInfo> friendList) {
        mFriendList = friendList;
    }

    public List<UserInfo> getFriendList() {
        return mFriendList;
    }
}
