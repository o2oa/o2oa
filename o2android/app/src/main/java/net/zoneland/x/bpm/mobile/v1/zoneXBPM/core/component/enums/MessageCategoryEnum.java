package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums;

/**
 * Created by FancyLou on 2015/12/16.
 */
public enum MessageCategoryEnum {

    chat("聊天", "chat"), notification("通知", "notification"), task("任务", "task");

    private final String name;
    private final String key;
    MessageCategoryEnum(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }
}
