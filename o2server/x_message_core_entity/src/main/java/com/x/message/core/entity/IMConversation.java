package com.x.message.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@ContainerEntity
@Table(name = PersistenceProperties.IMConversation.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMConversation.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMConversation extends SliceJpaObject  {


    private static final String TABLE = PersistenceProperties.IMConversation.table;
    private static final long serialVersionUID = 1928069073101719523L;


    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("数据库主键,自动生成.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();


    //会话类型 single 单人会话、 group 群聊
    public static final String CONVERSATION_TYPE_SINGLE = "single";
    public static final String CONVERSATION_TYPE_GROUP = "group";

    public static final String type_FIELDNAME = "type";
    @FieldDescribe("会话类型 单人 、 群.")
    @Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String type; //会话类型 单人 、 群


    public static final String title_FIELDNAME = "title";
    @FieldDescribe("会话标题 人名、群名")
    @Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String title; //会话标题 人名、群名


    public static final String personList_FIELDNAME = "personList";
    @FieldDescribe("会话对象、群成员.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + personList_FIELDNAME, joinIndex = @Index(name = TABLE + personList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + personList_FIELDNAME)
    @ElementIndex(name = TABLE + personList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> personList;

    public static final String adminPerson_FIELDNAME = "adminPerson";
    @FieldDescribe("管理员，群会话需要管理员")
    @Column(length = length_255B, name = ColumnNamePrefix + adminPerson_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + adminPerson_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String adminPerson; //管理员

    public static final String note_FIELDNAME = "note";
    @FieldDescribe("群公告")
    @Column(length = length_255B, name = ColumnNamePrefix + note_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + adminPerson_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String note;


    public static final String lastMessageTime_FIELDNAME = "lastMessageTime";
    @FieldDescribe("会话最后一条消息时间")
    @Column(name = ColumnNamePrefix + lastMessageTime_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + lastMessageTime_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Date lastMessageTime;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPersonList() {
        return personList;
    }

    public void setPersonList(List<String> personList) {
        this.personList = personList;
    }

    public String getAdminPerson() {
        return adminPerson;
    }

    public void setAdminPerson(String adminPerson) {
        this.adminPerson = adminPerson;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
