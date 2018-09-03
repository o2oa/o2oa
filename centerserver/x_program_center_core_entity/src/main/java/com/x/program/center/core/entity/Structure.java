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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
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
public class Structure extends SliceJpaObject {

	private static final long serialVersionUID = -5766284757128874055L;

	private static final String TABLE = PersistenceProperties.Structure.table;

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
