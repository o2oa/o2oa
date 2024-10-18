package com.x.message.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Schema(name = "IMMsgCollection", description = "消息收藏.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.IMMsgCollection.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMMsgCollection.table + JpaObject.IndexNameMiddle
                                 + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMMsgCollection extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.IMMsgCollection.table;
    private static final long serialVersionUID = 6580664357750105562L;

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


    public static final String messageId_FIELDNAME = "messageId";
    @FieldDescribe("消息id， 关联的消息.")
    @Column(length = length_64B, name = ColumnNamePrefix + messageId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String messageId;

    public static final String createPerson_FIELDNAME = "createPerson";
    @FieldDescribe("消息收藏人")
    @Column(name = ColumnNamePrefix + createPerson_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String createPerson;


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCreatePerson() {
        return createPerson;
    }

    public void setCreatePerson(String createPerson) {
        this.createPerson = createPerson;
    }
}
