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
 * 工作任务信息
 * 
 * @author O2LEE
 *
 */
@ContainerEntity
@Entity
@Table(name = PersistenceProperties.TaskExtField.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.TaskExtField.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskExtField extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.TaskExtField.table;

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
	@CheckPersist(allowEmpty = true)
	private String project;
	
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("工作任务名称（40字）")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist( allowEmpty = true )
	private String name;

	public static final String memoString_1_FIELDNAME = "memoString_1";
	@FieldDescribe("备用属性1（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_1 = "";
	
	public static final String memoString_2_FIELDNAME = "memoString_2";
	@FieldDescribe("备用属性2（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_2 = "";	
	
	public static final String memoString_3_FIELDNAME = "memoString_3";
	@FieldDescribe("备用属性3（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_3 = "";	
	
	public static final String memoString_4_FIELDNAME = "memoString_4";
	@FieldDescribe("备用属性4（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_4_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_4 = "";
	
	public static final String memoString_5_FIELDNAME = "memoString_5";
	@FieldDescribe("备用属性5（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_5_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_5 = "";
	
	public static final String memoString_6_FIELDNAME = "memoString_6";
	@FieldDescribe("备用属性6（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_6_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_6 = "";
	
	public static final String memoString_7_FIELDNAME = "memoString_7";
	@FieldDescribe("备用属性7（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_7_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_7 = "";
	
	public static final String memoString_8_FIELDNAME = "memoString_8";
	@FieldDescribe("备用属性8（最大长度：255）")
	@Column(length = length_255B, name = ColumnNamePrefix  + memoString_8_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memoString_8 = "";
	
	public static final String memoString_1_lob_FIELDNAME = "memoString_1_lob";
	@FieldDescribe("备用长文本1（最大长度：10M）")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_10M, name = ColumnNamePrefix + memoString_1_lob_FIELDNAME)
	private String memoString_1_lob = "";
	
	public static final String memoString_2_lob_FIELDNAME = "memoString_2_lob";
	@FieldDescribe("备用长文本2（最大长度：10M）")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_10M, name = ColumnNamePrefix + memoString_2_lob_FIELDNAME)
	private String memoString_2_lob = "";
	
	public static final String memoString_3_lob_FIELDNAME = "memoString_3_lob";
	@FieldDescribe("备用长文本3（最大长度：10M）")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_10M, name = ColumnNamePrefix + memoString_3_lob_FIELDNAME)
	private String memoString_3_lob = "";
	
	public static final String memoString_4_lob_FIELDNAME = "memoString_4_lob";
	@FieldDescribe("备用长文本4（最大长度：10M）")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column( length = JpaObject.length_10M, name = ColumnNamePrefix + memoString_4_lob_FIELDNAME)
	private String memoString_4_lob = "";

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMemoString_1() {
		return memoString_1;
	}

	public void setMemoString_1(String memoString_1) {
		this.memoString_1 = memoString_1;
	}

	public String getMemoString_2() {
		return memoString_2;
	}

	public void setMemoString_2(String memoString_2) {
		this.memoString_2 = memoString_2;
	}

	public String getMemoString_3() {
		return memoString_3;
	}

	public void setMemoString_3(String memoString_3) {
		this.memoString_3 = memoString_3;
	}

	public String getMemoString_4() {
		return memoString_4;
	}

	public void setMemoString_4(String memoString_4) {
		this.memoString_4 = memoString_4;
	}

	public String getMemoString_5() {
		return memoString_5;
	}

	public void setMemoString_5(String memoString_5) {
		this.memoString_5 = memoString_5;
	}

	public String getMemoString_6() {
		return memoString_6;
	}

	public void setMemoString_6(String memoString_6) {
		this.memoString_6 = memoString_6;
	}

	public String getMemoString_7() {
		return memoString_7;
	}

	public void setMemoString_7(String memoString_7) {
		this.memoString_7 = memoString_7;
	}

	public String getMemoString_8() {
		return memoString_8;
	}

	public void setMemoString_8(String memoString_8) {
		this.memoString_8 = memoString_8;
	}

	public String getMemoString_1_lob() {
		return memoString_1_lob;
	}

	public void setMemoString_1_lob(String memoString_1_lob) {
		this.memoString_1_lob = memoString_1_lob;
	}

	public String getMemoString_2_lob() {
		return memoString_2_lob;
	}

	public void setMemoString_2_lob(String memoString_2_lob) {
		this.memoString_2_lob = memoString_2_lob;
	}

	public String getMemoString_3_lob() {
		return memoString_3_lob;
	}

	public void setMemoString_3_lob(String memoString_3_lob) {
		this.memoString_3_lob = memoString_3_lob;
	}

	public String getMemoString_4_lob() {
		return memoString_4_lob;
	}

	public void setMemoString_4_lob(String memoString_4_lob) {
		this.memoString_4_lob = memoString_4_lob;
	}	
}