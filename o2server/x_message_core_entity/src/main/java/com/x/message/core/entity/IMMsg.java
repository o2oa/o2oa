package com.x.message.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;


@Entity
@ContainerEntity
@Table(name = PersistenceProperties.IMMsg.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMMsg.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMMsg extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.IMMsg.table;
    private static final long serialVersionUID = -6822547645557111823L;


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
    @Index(name = TABLE + IndexNameMiddle + conversationId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String conversationId;

    public static final String body_FIELDNAME = "body";
    @FieldDescribe("消息内容.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_10M, name = ColumnNamePrefix + body_FIELDNAME)
    private String body;


    public static final String createPerson_FIELDNAME = "createPerson";
    @FieldDescribe("创建人，消息发送人")
    @Column(length = length_255B, name = ColumnNamePrefix + createPerson_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + createPerson_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String createPerson;


    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }
}
