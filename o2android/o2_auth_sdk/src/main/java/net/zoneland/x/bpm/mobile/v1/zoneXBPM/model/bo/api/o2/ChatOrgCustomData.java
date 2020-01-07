package net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2;

import java.util.List;

/**
 * Created by FancyLou on 2015/11/19.
 */
public class ChatOrgCustomData {

    private List<String> onlineList;//在线列表 用户姓名 加过好友的
    private List<String> chatList;//聊天列表 用户姓名  聊过天的人 没加好友

    public List<String> getOnlineList() {
        return onlineList;
    }

    public void setOnlineList(List<String> onlineList) {
        this.onlineList = onlineList;
    }

    public List<String> getChatList() {
        return chatList;
    }

    public void setChatList(List<String> chatList) {
        this.chatList = chatList;
    }
}
