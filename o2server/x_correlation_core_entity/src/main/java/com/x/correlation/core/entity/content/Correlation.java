package com.x.correlation.core.entity.content;

import static com.x.base.core.entity.StorageType.processPlatform;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.correlation.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Correlation", description = "相关内容.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Content.Correlation.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Correlation.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = processPlatform)
public class Correlation extends SliceJpaObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Content.Correlation.TABLE;

	public static final String TYPE_CMS = "cms";
	public static final String TYPE_PROCESSPLATFORM = "processPlatform";

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
		// nothing
	}

	@PostLoad
	public void postLoad() {
		// nothing
	}

	public Correlation() {
		// nothing
	}

	public static final String FROMTYPE_FIELDNAME = "fromType";
	@FieldDescribe("源类型cms,processPlatform.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + FROMTYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMTYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromType;

	public static final String TARGETTYPE_FIELDNAME = "targetType";
	@FieldDescribe("关联目标类型,cms.processPlatform.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + TARGETTYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TARGETTYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetType;

	public static final String FROMBUNDLE_FIELDNAME = "fromBundle";
	@FieldDescribe("源标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + FROMBUNDLE_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + FROMBUNDLE_FIELDNAME)
	private String fromBundle;

	public static final String TARGETBUNDLE_FIELDNAME = "targetBundle";
	@FieldDescribe("关联目标标识.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + TARGETBUNDLE_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + TARGETBUNDLE_FIELDNAME)
	private String targetBundle;

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("文件所有者")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getFromBundle() {
		return fromBundle;
	}

	public void setFromBundle(String fromBundle) {
		this.fromBundle = fromBundle;
	}

	public String getTargetBundle() {
		return targetBundle;
	}

	public void setTargetBundle(String targetBundle) {
		this.targetBundle = targetBundle;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}
