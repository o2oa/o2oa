package com.x.query.core.entity.index;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
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

    public static final String MODE_LOCALDIRECTORY = "localDirectory";
    public static final String MODE_HDFSDIRECTORY = "hdfsDirectory";
    public static final String MODE_SHAREDDIRECTORY = "sharedDirectory";

    public State() {
        this.properties = new StateProperties();
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

    @Override
    public void onPersist() throws Exception {
        // nothing
    }

    public StateProperties getProperties() {
        if (null == this.properties) {
            this.properties = new StateProperties();
        }
        return this.properties;
    }

    public void setProperties(StateProperties properties) {
        this.properties = properties;
    }

    public void setLatestIdList(List<String> latestIdList) {
        this.latestIdList = latestIdList;
        this.getProperties().setLatestIdList(latestIdList);
    }

    public List<String> getLatestIdList() {
        if ((null != this.properties) && ListTools.isNotEmpty(this.properties.getLatestIdList())) {
            return this.properties.getLatestIdList();
        } else {
            return this.latestIdList;
        }
    }

    public void logLatestIds(Date updateTime, List<String> ids) {
        if ((!Objects.isNull(this.latestUpdateTime)) && (!Objects.isNull(updateTime))
                && DateUtils.truncatedEquals(this.latestUpdateTime, updateTime, Calendar.SECOND)) {
            this.setLatestIdList(ListUtils.sum(this.getLatestIdList(), ids));
        } else {
            this.latestUpdateTime = DateUtils.truncate(updateTime, Calendar.SECOND);
            this.setLatestIdList(ids);
        }
    }

    public static final String LATESTIDLIST_FIELDNAME = "latestIdList";
    @FieldDescribe("最后更新对象标识.")
    @Transient
    private List<String> latestIdList;

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

    public static final String MODE_FIELDNAME = "mode";
    @FieldDescribe("模式.")
    @Column(length = length_16B, name = ColumnNamePrefix + MODE_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String mode;

    public static final String PROPERTIES_FIELDNAME = "properties";
    @FieldDescribe("属性对象存储字段.")
    @Persistent(fetch = FetchType.EAGER)
    @Strategy(JsonPropertiesValueHandler)
    @Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private StateProperties properties;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
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
