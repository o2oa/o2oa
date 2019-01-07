package jiguang.chat.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;


@Table(name = "friend_recommends", id = "_id")
public class FriendRecommendEntry extends Model {

    @Column(name = "Uid")
    public Long uid;

    @Column(name = "Username")
    public String username;

    @Column(name = "NoteName")
    public String noteName;

    @Column(name = "NickName")
    public String nickName;

    @Column(name = "AppKey")
    public String appKey;

    @Column(name = "Avatar")
    public String avatar;

    @Column(name = "DisplayName")
    public String displayName;

    @Column(name = "Reason")
    public String reason;

    @Column(name = "State")
    public String state;

    @Column(name = "User")
    public UserEntry user;

    @Column(name = "BtnState")
    public int btnState;


    public FriendRecommendEntry() {
        super();
    }


    public FriendRecommendEntry(Long uid, String username, String noteName, String nickName, String appKey, String avatar,
                                String displayName, String reason, String state, UserEntry user, int btnState) {
        super();
        this.uid = uid;
        this.username = username;
        this.nickName = nickName;
        this.noteName = noteName;
        this.appKey = appKey;
        this.avatar = avatar;
        this.displayName = displayName;
        this.reason = reason;
        this.state = state;
        this.user = user;
        this.btnState = btnState;
    }


    public static FriendRecommendEntry getEntry(UserEntry user, String username, String appKey) {
        return new Select().from(FriendRecommendEntry.class).where("Username = ?", username)
                .where("AppKey = ?", appKey)
                .where("User = ?", user.getId()).executeSingle();
    }

    public static FriendRecommendEntry getEntry(long id) {
        return new Select().from(FriendRecommendEntry.class).where("_id = ?", id).executeSingle();
    }

    public static void deleteEntry(FriendRecommendEntry entry) {
        new Delete().from(FriendRecommendEntry.class).where("_id = ?", entry.getId()).execute();
    }
}