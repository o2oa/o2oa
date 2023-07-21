package com.x.file.core.entity.open;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 云文件系统配置
 * @author sword
 */
@Schema(name = "FileConfig", description = "云文件配置.")
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Open.FileConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Open.FileConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FileConfig extends SliceJpaObject {

	private static final long serialVersionUID = -2266232193925155825L;
	private static final String TABLE = PersistenceProperties.Open.FileConfig.table;
	public static final Integer DEFAULT_RECYCLE_DAYS = 30;

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

	/* 以上为 JpaObject 默认字段 */

	@Override
	public void onPersist() throws Exception {
		if (this.properties == null) {
			this.properties = new FileConfigProperties();
		}
	}

	public FileConfig() {
		this.properties = new FileConfigProperties();
	}

	/* 更新运行方法 */

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("所属用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists = @CitationNotExist(fields = person_FIELDNAME, type = FileConfig.class))
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("分类名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String capacity_FIELDNAME = "capacity";
	@FieldDescribe("容量(单位M)，0表示无限大.")
	@Column(name = ColumnNamePrefix + capacity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer capacity;

	public static final String recycleDays_FIELDNAME = "recycleDays";
	@FieldDescribe("回收站数据保留天数")
	@Column(name = ColumnNamePrefix + recycleDays_FIELDNAME)
	private Integer recycleDays;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private FileConfigProperties properties;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getRecycleDays() {
		return recycleDays;
	}

	public void setRecycleDays(Integer recycleDays) {
		this.recycleDays = recycleDays;
	}

	public FileConfigProperties getProperties() {
		if (null == this.properties) {
			this.properties = new FileConfigProperties();
		}
		return this.properties;
	}

	public void setProperties(FileConfigProperties properties) {
		this.properties = properties;
	}

}
