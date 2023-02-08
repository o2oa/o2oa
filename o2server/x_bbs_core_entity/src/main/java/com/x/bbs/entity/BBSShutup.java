package com.x.bbs.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import java.util.Date;

/**
 * 禁言人员
 *
 * @author sword
 * @date 2022/05/20 10:53
 **/
@Schema(name = "BBSShutup", description = "论坛禁言人员.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSShutup.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.BBSShutup.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSShutup extends SliceJpaObject {

    private static final long serialVersionUID = 5779755036387970393L;

    private static final String TABLE = PersistenceProperties.BBSShutup.table;

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

    @Override
    public void onPersist() throws Exception {
    }

    public static final String operator_FIELDNAME = "operator";
    @FieldDescribe("操作者")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operator_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String operator;

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("被禁言用户")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + person_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + person_FIELDNAME, unique = true)
    @CheckPersist(allowEmpty = false)
    private String person;

    public static final String unmuteDate_FIELDNAME = "unmuteDate";
    @FieldDescribe("解封时间")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unmuteDate_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String unmuteDate;

    public static final String unmuteDateTime_FIELDNAME = "unmuteDateTime";
    @FieldDescribe("解封时间date类型")
    @Column(name = ColumnNamePrefix + unmuteDateTime_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + unmuteDateTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date unmuteDateTime;

    public static final String reason_FIELDNAME = "reason";
    @FieldDescribe("禁言原因")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + reason_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String reason;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getUnmuteDate() {
        return unmuteDate;
    }

    public void setUnmuteDate(String unmuteDate) {
        this.unmuteDate = unmuteDate;
    }

    public Date getUnmuteDateTime() {
        return unmuteDateTime;
    }

    public void setUnmuteDateTime(Date unmuteDateTime) {
        this.unmuteDateTime = unmuteDateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
