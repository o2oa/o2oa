package jiguang.chat.entity;

import cn.jpush.im.android.api.model.Conversation;

public class Event {

    private EventType type;
    private Conversation conversation;
    private String draft;
    private long friendId;

    public Event(EventType type, Conversation conv, String draft, long friendId) {
        this.type = type;
        this.conversation = conv;
        this.draft = draft;
        this.friendId = friendId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public EventType getType() {
        return type;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getDraft() {
        return draft;
    }

    public long getFriendId() {
        return friendId;
    }

    public static class Builder {
        private EventType type;
        private Conversation conversation;
        private String draft;
        private long friendId;

        public Builder setType(EventType type) {
            this.type = type;
            return this;
        }

        public Builder setConversation(Conversation conv) {
            this.conversation = conv;
            return this;
        }

        public Builder setDraft(String draft) {
            this.draft = draft;
            return this;
        }

        public Builder setFriendId(long friendId) {
            this.friendId = friendId;
            return this;
        }

        public Event build() {
            return new Event(type, conversation, draft, friendId);
        }

    }

}
