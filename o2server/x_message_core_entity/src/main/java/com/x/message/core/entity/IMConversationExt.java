package com.x.message.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会话的扩展对象 每个人一个，保存当前用户的会话扩展属性
 */
@Schema(name = "IMConversationExt", description = "消息会话扩展.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.IMConversationExt.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMConversationExt.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMConversationExt extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.IMConversationExt.table;
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

    public static final String conversationId_FIELDNAME = "conversationId";
    @FieldDescribe("会话id， 关联的会话.")
    @Column(length = length_64B, name = ColumnNamePrefix + conversationId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String conversationId;

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("所属人员")
    @Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String person;

    public static final String isTop_FIELDNAME = "isTop";
    @FieldDescribe("是否置顶.")
    @CheckPersist(allowEmpty = false)
    @Column(name = ColumnNamePrefix + isTop_FIELDNAME)
    private Boolean isTop = false;

    public static final String lastReadTime_FIELDNAME = "lastReadTime";
    @FieldDescribe("最后阅读时间")
    @Column(name = ColumnNamePrefix + lastReadTime_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Date lastReadTime;

    public static final String lastDeleteTime_FIELDNAME = "lastDeleteTime";
    @FieldDescribe("当前用户把会话删除的时间")
    @Column(name = ColumnNamePrefix + lastDeleteTime_FIELDNAME)
    private Date lastDeleteTime;

    public static final String isDeleted_FIELDNAME = "isDeleted";
    @FieldDescribe("是否删除，个人删除后不显示到会话列表，如果会话中有新的聊天消息会重置为true.")
    @Column(name = ColumnNamePrefix + isDeleted_FIELDNAME)
    private Boolean isDeleted = false;

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Date getLastReadTime() {
        return lastReadTime;
    }

    public void setLastReadTime(Date lastReadTime) {
        this.lastReadTime = lastReadTime;
    }

    public Date getLastDeleteTime() {
        return lastDeleteTime;
    }

    public void setLastDeleteTime(Date lastDeleteTime) {
        this.lastDeleteTime = lastDeleteTime;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
