package com.x.query.core.entity.index;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;

@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@javax.persistence.Table(name = PersistenceProperties.Index.State.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Index.State.TABLE + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class State extends SliceJpaObject {

    private static final long serialVersionUID = -5610293696763235753L;

    private static final String TABLE = PersistenceProperties.Index.State.TABLE;

    public static final String TYPE_WORKCOMPLETED = "workCompleted";
    public static final String TYPE_WORK = "work";
    public static final String TYPE_DOCUMENT = "document";

    public static final String FREQ_LOW = "low";
    public static final String FREQ_HIGH = "high";

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
        // nothing
    }

    public static final String LATESTUPDATEID_FIELDNAME = "latestUpdateId";
    @FieldDescribe("最后更新对象标识.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + LATESTUPDATEID_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String latestId;

    public static final String LATESTUPDATETIME_FIELDNAME = "latestUpdateTime";
    @FieldDescribe("最后更新对象时间.")
    @Column(name = ColumnNamePrefix + LATESTUPDATETIME_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date latestUpdateTime;

    public static final String TYPE_FIELDNAME = "type";
    @Flag
    @FieldDescribe("类型.")
    @Column(length = length_32B, name = ColumnNamePrefix + TYPE_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String type;

    public static final String NODE_FIELDNAME = "node";
    @Flag
    @FieldDescribe("节点.")
    @Column(length = length_255B, name = ColumnNamePrefix + NODE_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String node;

    public static final String FREQ_FIELDNAME = "freq";
    @FieldDescribe("频率.")
    @Column(length = length_8B, name = ColumnNamePrefix + FREQ_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String freq;

    public String getLatestId() {
        return latestId;
    }

    public void setLatestId(String latestId) {
        this.latestId = latestId;
    }

    public Date getLatestUpdateTime() {
        return latestUpdateTime;
    }

    public void setLatestUpdateTime(Date latestUpdateTime) {
        this.latestUpdateTime = latestUpdateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

}
