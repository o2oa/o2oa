package com.x.file.core.entity.personal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Folder", description = "云文件个人文件夹.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Personal.Folder.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Folder.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Folder extends SliceJpaObject {

	private static final long serialVersionUID = -2266232193925155825L;
	private static final String TABLE = PersistenceProperties.Personal.Folder.table;

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
		/* 如果为顶层，那么将上级目录设置为空 */
		this.superior = StringUtils.trimToEmpty(this.superior);
	}

	/* 更新运行方法 */

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("所属用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("分类名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个目录下不能有重名 */
	@CitationNotExist(fields = { "name", "id" }, type = Folder.class, equals = {
			@Equal(property = "person", field = "person"), @Equal(property = "superior", field = "superior") }))
	private String name;

	public static final String superior_FIELDNAME = "superior";
	@FieldDescribe("上级分类ID,为空代表顶级分类。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + superior_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + superior_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists =
	/* 上级目录必须存在,且不能为自己 */
	@CitationExist(type = Folder.class, equals = @Equal(property = "person", field = "person")))
	private String superior;

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

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

}