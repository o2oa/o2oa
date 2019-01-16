package jiguang.chat.database;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "friends", id = "_id")
public class FriendEntry extends Model {

    @Column(name = "Uid")
    public Long uid;

    @Column(name = "Username")
    public String username;

    @Column(name = "AppKey")
    public String appKey;

    @Column(name = "Avatar")
    public String avatar;

    @Column(name = "DisplayName")
    public String displayName;

    @Column(name = "Letter")
    public String letter;

    @Column(name = "NickName")
    public String nickName;

    @Column(name = "NoteName")
    public String noteName;

    @Column(name = "User")
    public UserEntry user;

    public FriendEntry() {
        super();
    }

    public FriendEntry(Long uid, String username, String noteName, String nickName, String appKey, String avatar, String displayName, String letter,
                       UserEntry user) {
        super();
        this.uid = uid;
        this.username = username;
        this.appKey = appKey;
        this.avatar = avatar;
        this.displayName = displayName;
        this.letter = letter;
        this.user = user;
        this.noteName = noteName;
        this.nickName = nickName;
    }

    public static FriendEntry getFriend(UserEntry user, String username, String appKey) {
        return new Select().from(FriendEntry.class)
                .where("Username = ?", username)
                .where("AppKey = ?", appKey)
                .where("User = ?", user.getId())
                .executeSingle();
    }

    public static FriendEntry getFriend(long id) {
        return new Select().from(FriendEntry.class).where("_id = ?", id).executeSingle();
    }


}
