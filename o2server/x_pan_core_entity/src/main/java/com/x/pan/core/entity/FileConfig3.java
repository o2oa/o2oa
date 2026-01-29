package com.x.pan.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;
import java.util.List;

/**
 * 网盘系统配置
 * @author sword
 */
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.FileConfig3.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.FileConfig3.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FileConfig3 extends SliceJpaObject {

	private static final long serialVersionUID = 7846823029423714417L;

	private static final String TABLE = PersistenceProperties.FileConfig3.table;
	public static final Integer DEFAULT_RECYCLE_DAYS = 15;
	public static final List<String> DEFAULT_MENU_LIST = List.of("个人文件", "企业文件");

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

	public FileConfig3() {
		this.properties = new FileConfigProperties();
	}

	/* 更新运行方法 */

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("所属用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME, unique = true)
	@CheckPersist(allowEmpty = false)
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

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + properties_FIELDNAME)
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
		return capacity == null ? 0 : capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
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

	public boolean hasEnableOfficeReview(){
		return this.properties == null ? false : StringUtils.isNotBlank(this.properties.getOfficeHome());
	}

	public String getOfficePreviewTools(){
		return this.properties == null ? "" : this.properties.getPreviewTools();
	}

	public String getOfficeViewDownloadUrl(){
		return this.properties == null ? "" : this.properties.getViewDownLoadUrl();
	}

	public boolean getReadPermissionDown() {
		return this.properties == null ? true : this.properties.getZoneReadPermissionDown();
	}

	public Integer getPanRecycleDays(){
		return this.properties == null ? DEFAULT_RECYCLE_DAYS : this.properties.getRecycleDays();
	}

	public List<String> getMenuList() {
		return this.properties == null ? DEFAULT_MENU_LIST : this.properties.getPanMenuList();
	}

	public String getOfficeOnlineUrlFromProperties(){
		return this.properties == null ? "" : this.properties.getOfficeOnlineUrl();
	}

	public boolean getOfficeOpenOfficeEdit(){
		return this.properties != null && this.properties.getOpenOfficeEdit();
	}
}
