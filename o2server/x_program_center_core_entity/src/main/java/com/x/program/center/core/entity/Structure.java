package com.x.program.center.core.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.*;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Structure.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Structure.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.structure)
public class Structure extends StorageObject {

	private static final long serialVersionUID = -5766284757128874055L;

	private static final String TABLE = PersistenceProperties.Structure.table;

	public static final String default_extension = "xapp";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		this.lastUpdateTime = new Date();
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	public Structure() {

	}

	public Structure(String storage, String name) throws Exception {
		if (StringUtils.isEmpty(storage)) {
			throw new Exception("storage can not be empty.");
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = default_extension;
	}

	@Override
	public String path() throws Exception {
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = "";
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? ("." + default_extension) : ("." + this.extension);
		return str;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	/* 更新运行方法 */

	public String getDescription() {
		if (StringUtils.isNotEmpty(this.descriptionLob)) {
			return this.descriptionLob;
		} else {
			return this.description;
		}
	}

	public void setDescription(String description) {
		if (StringTools.utf8Length(description) > length_255B) {
			this.description = StringTools.utf8SubString(description, length_255B);
			this.descriptionLob = description;
		} else {
			this.description = description;
			this.descriptionLob = null;
		}
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("扩展名,必须要有扩展名的文件才允许上传.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("文件大小.")
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String descriptionLob_FIELDNAME = "descriptionLob";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + descriptionLob_FIELDNAME)
	private String descriptionLob;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("导出结构内容")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("最后更新时间")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("是否使用更深的路径.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + deepPath_FIELDNAME)
	private Boolean deepPath;

	// public static String[] FLAGS = new String[] { id_FIELDNAME };

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescriptionLob() {
		return descriptionLob;
	}

	public void setDescriptionLob(String descriptionLob) {
		this.descriptionLob = descriptionLob;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	/* flag标志位 */
	/* Entity 默认字段结束 */

}
