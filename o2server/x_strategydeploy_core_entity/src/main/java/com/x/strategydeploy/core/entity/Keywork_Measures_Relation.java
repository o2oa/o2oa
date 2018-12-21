package com.x.strategydeploy.core.entity;

import java.util.Date;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Keywork_Measures_Relation.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Keywork_Measures_Relation.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Keywork_Measures_Relation extends SliceJpaObject {
	private static final long serialVersionUID = -1377589120234766618L;
	private static final String TABLE = PersistenceProperties.Keywork_Measures_Relation.table;

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

	public void onPersist() throws Exception {
	}

	/*
	 * =============================以上为 JpaObject
	 * 默认字段============================================
	 */

	@FieldDescribe("重点工作标题")
	@Column(name = "xkeyworktitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworktitle")
	@CheckPersist(allowEmpty = false)
	private String keyworktitle;

	@FieldDescribe("重点工作ID")
	@Column(name = "xkeyworkid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xkeyworkid")
	@CheckPersist(allowEmpty = false)
	private String keyworkid;

	@FieldDescribe("战略举措标题")
	@Column(name = "xmeasuresinfotitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xmeasuresinfotitle")
	@CheckPersist(allowEmpty = false)
	private String measuresinfotitle;

	@FieldDescribe("战略举措ID")
	@Column(name = "xmeasuresinfoid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_xmeasuresinfoid")
	@CheckPersist(allowEmpty = false)
	private String measuresinfoid;

	@FieldDescribe("战略部署标题")
	@Column(name = "xstrategydeploytitle", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeploytitle")
	@CheckPersist(allowEmpty = false)
	private String strategydeploytitle;

	@FieldDescribe("战略部署ID")
	@Column(name = "xstrategydeployid", length = JpaObject.length_255B)
	@Index(name = TABLE + "_strategydeployid")
	@CheckPersist(allowEmpty = false)
	private String strategydeployid;

	public String getMeasuresinfotitle() {
		return measuresinfotitle;
	}

	public void setMeasuresinfotitle(String measuresinfotitle) {
		this.measuresinfotitle = measuresinfotitle;
	}

	public String getMeasuresinfoid() {
		return measuresinfoid;
	}

	public void setMeasuresinfoid(String measuresinfoid) {
		this.measuresinfoid = measuresinfoid;
	}

	public String getKeyworktitle() {
		return keyworktitle;
	}

	public void setKeyworktitle(String keyworktitle) {
		this.keyworktitle = keyworktitle;
	}

	public String getKeyworkid() {
		return keyworkid;
	}

	public void setKeyworkid(String keyworkid) {
		this.keyworkid = keyworkid;
	}

}
