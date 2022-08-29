package com.x.processplatform.core.entity.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApplicationDict", description = "流程平台数据字典.")
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Element.ApplicationDict.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.ApplicationDict.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ApplicationDict extends SliceJpaObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Element.ApplicationDict.table;

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
		//nothing
	}

	/* 更新运行方法 */

	// public static String[] FLA GS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String application_FIELDNAME = "application";
	@IdReference(Application.class)
	@FieldDescribe("应用.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists =
	/* 检查关联的Application需存在 */
	@CitationExist(type = Application.class))
	private String application;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("字典名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias",
			"name" }, type = ApplicationDict.class, equals = @Equal(property = "application", field = "application")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias",
			"name" }, type = ApplicationDict.class, equals = @Equal(property = "application", field = "application")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}