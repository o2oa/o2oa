package com.x.teamwork.core.entity;

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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 项目信息
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TaskDetail.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TaskDetail.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskDetail extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TaskDetail.table;
	
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
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	public static final String project_FIELDNAME = "project";
	@FieldDescribe("所属项目ID.")
	@Column(length = length_id, name = ColumnNamePrefix + project_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + project_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String project;
	
	public static final String detail_FIELDNAME = "detail";
	@FieldDescribe("工作内容")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_128K, name = ColumnNamePrefix + detail_FIELDNAME)
	private String detail;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明信息")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_128K, name = ColumnNamePrefix + description_FIELDNAME)
	private String description;
	
	public static final String memoLob1_FIELDNAME = "memoLob1";
	@FieldDescribe("备用LOB信息1")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_128K, name = ColumnNamePrefix + memoLob1_FIELDNAME)
	private String memoLob1;
	
	public static final String memoLob2_FIELDNAME = "memoLob2";
	@FieldDescribe("备用LOB信息2")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_128K, name = ColumnNamePrefix + memoLob2_FIELDNAME)
	private String memoLob2;
	
	public static final String memoLob3_FIELDNAME = "memoLob3";
	@FieldDescribe("备用LOB信息3")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_128K, name = ColumnNamePrefix + memoLob3_FIELDNAME)
	private String memoLob3;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMemoLob1() {
		return memoLob1;
	}

	public void setMemoLob1(String memoLob1) {
		this.memoLob1 = memoLob1;
	}

	public String getMemoLob2() {
		return memoLob2;
	}

	public void setMemoLob2(String memoLob2) {
		this.memoLob2 = memoLob2;
	}

	public String getMemoLob3() {
		return memoLob3;
	}

	public void setMemoLob3(String memoLob3) {
		this.memoLob3 = memoLob3;
	}
}