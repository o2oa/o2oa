package com.x.organization.core.entity.log;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.organization.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TokenThreshold", description = "组织令牌阈值.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Log.TokenThreshold.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.TokenThreshold.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TokenThreshold extends SliceJpaObject {

	private static final long serialVersionUID = -7688990884958313153L;

	private static final String TABLE = PersistenceProperties.Log.TokenThreshold.table;

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

	public TokenThreshold() {
	}

	public TokenThreshold(String person, Date threshold) {

		this.person = person;
		this.threshold = threshold;

	}

	/* 更新运行方法 */

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("用户.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String threshold_FIELDNAME = "threshold";
	@FieldDescribe("阀值.")
	@Column(name = ColumnNamePrefix + threshold_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + threshold_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date threshold;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Date getThreshold() {
		return threshold;
	}

	public void setThreshold(Date threshold) {
		this.threshold = threshold;
	}

}