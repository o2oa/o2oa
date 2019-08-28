package com.x.processplatform.core.entity.element;

import java.util.Objects;

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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Mapping.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Mapping.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Mapping extends SliceJpaObject {

	private static final long serialVersionUID = -9130723320348772653L;

	private static final String TABLE = PersistenceProperties.Element.Mapping.table;

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
		this.process = Objects.toString(this.process, "");
		this.application = Objects.toString(this.application, "");
		this.enable = (this.enable == null) ? false : this.enable;
	}

	public String getProcess() {
		return Objects.toString(this.process, "");
	}

	public void setProcess(String process) {
		this.process = Objects.toString(process, "");
	}

	public String getApplication() {
		return Objects.toString(this.application, "");
	}

	public void setApplication(String application) {
		this.application = Objects.toString(application, "");
	}

	/* 更新运行方法 */

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean enable;

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("所属应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Application.class) })
	private String application;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("所属流程.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Process.class) })
	private String process;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("映射方案.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String tableName_FIELDNAME = "tableName";
	@FieldDescribe("映射表名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + tableName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + tableName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String tableName;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public static class Item {

		private String path = "";
		private String column = "";
		private String type = "";
		private String name = "";
		private String scriptText = "";

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getScriptText() {
			return scriptText;
		}

		public void setScriptText(String scriptText) {
			this.scriptText = scriptText;
		}

	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
