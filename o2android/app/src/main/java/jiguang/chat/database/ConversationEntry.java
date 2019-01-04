package jiguang.chat.database;

/**
 * Created by ${chenyn} on 2017/7/18.
 */

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "conversation", id = "_id")
public class ConversationEntry extends Model{

    @Column(name = "Targetname")
    public String targetname;

    @Column(name = "Orders")
    public Integer order;


    public ConversationEntry(){
        super();
    }

    public ConversationEntry(String targetname, int order) {
        super();
        this.targetname = targetname;
        this.order = order;
    }

    public static ConversationEntry getTopConversation(int order) {
        return new Select().from(ConversationEntry.class)
                .where("Orders = ?", order).executeSingle();
    }
}
