package com.x.message.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 聊天 会话对象
 */
@Schema(name = "IMConversation", description = "消息聊天.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
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
    @CheckPersist(allowEmpty = false)
    private String type; //会话类型 单人 、 群


    public static final String title_FIELDNAME = "title";
    @FieldDescribe("会话标题 人名、群名")
    @Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
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
    @CheckPersist(allowEmpty = true)
    private String adminPerson; //管理员

    public static final String note_FIELDNAME = "note";
    @FieldDescribe("群公告")
    @Column(length = length_255B, name = ColumnNamePrefix + note_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String note;


    public static final String lastMessageTime_FIELDNAME = "lastMessageTime";
    @FieldDescribe("会话最后一条消息时间")
    @Column(name = ColumnNamePrefix + lastMessageTime_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Date lastMessageTime;




    public static final String businessId_FIELDNAME = "businessId";
    @FieldDescribe("业务对象Id. （流程的jobId）")
    @Column(length = length_128B, name = ColumnNamePrefix + businessId_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String businessId;

    public static final String businessBody_FIELDNAME = "businessBody";
    @FieldDescribe("业务对象内容.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_10M, name = ColumnNamePrefix + businessBody_FIELDNAME)
    private String businessBody;

    //业务类型 process 流程
    public static final String CONVERSATION_BUSINESS_TYPE_PROCESS = "process";
    public static final String CONVERSATION_BUSINESS_TYPE_CMS = "cms";

    public static final String businessType_FIELDNAME = "businessType";
    @FieldDescribe("业务类型. process")
    @Column(length = length_16B, name = ColumnNamePrefix + businessType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String businessType;


    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessBody() {
        return businessBody;
    }

    public void setBusinessBody(String businessBody) {
        this.businessBody = businessBody;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

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
