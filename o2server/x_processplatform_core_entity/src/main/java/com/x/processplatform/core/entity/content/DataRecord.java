package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.query.core.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;
import java.util.List;

@Schema(name = "DataRecord", description = "流程平台业务数据变更记录.")
@Entity
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.DataRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.DataRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.Content.DataRecord.table + JpaObject.IndexNameMiddle
		+ DataRecord.job_FIELDNAME, columnNames = { JpaObject.ColumnNamePrefix + DataRecord.job_FIELDNAME,
				JpaObject.ColumnNamePrefix + DataRecord.path_FIELDNAME }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DataRecord extends SliceJpaObject {

	private static final long serialVersionUID = 4161117889391157310L;

	private static final String TABLE = PersistenceProperties.Content.DataRecord.table;

	public DataRecord(){}

	public DataRecord(String application, String process, String job, String path){
		this.application = application;
		this.process = process;
		this.job = job;
		this.path = path;
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
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.dataRecordItemList = this.getProperties().getDataRecordItemList();
		}
	}

	public DataRecordProperties getProperties() {
		if (null == this.properties) {
			this.properties = new DataRecordProperties();
		}
		return this.properties;
	}

	public void setProperties(DataRecordProperties properties) {
		this.properties = properties;
	}

	public List<DataRecordItem> getDataRecordItemList() {
		return dataRecordItemList;
	}

	public void setDataRecordItemList(List<DataRecordItem> dataRecordItemList) {
		this.dataRecordItemList = dataRecordItemList;
		this.getProperties().setDataRecordItemList(dataRecordItemList);
	}

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("流程ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("工单标识")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String path_FIELDNAME = "path";
	@FieldDescribe("字段")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + path_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + path_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String path;

	public static final String updateNum_FIELDNAME = "updateNum";
	@FieldDescribe("变更次数.")
	@Column(name = ColumnNamePrefix + updateNum_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer updateNum;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private DataRecordProperties properties;

	public static final String dataRecordItemList_FIELDNAME = "dataRecordItemList";
	@FieldDescribe("字段变更记录.")
	@Transient
	private List<DataRecordItem> dataRecordItemList;

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getUpdateNum() {
		return updateNum;
	}

	public void setUpdateNum(Integer updateNum) {
		this.updateNum = updateNum;
	}
}
