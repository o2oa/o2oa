package com.x.teamwork.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;

/**
 * 项目和任务权限
 * @author sword
 */
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.ProjectPermission.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ProjectPermission.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.ProjectPermission.table + JpaObject.IndexNameMiddle
		+ ProjectPermission.targetId_FIELDNAME, columnNames = {JpaObject.ColumnNamePrefix+ ProjectPermission.targetId_FIELDNAME,
				JpaObject.ColumnNamePrefix+ ProjectPermission.name_FIELDNAME,
				JpaObject.ColumnNamePrefix+ ProjectPermission.role_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProjectPermission extends SliceJpaObject {

	private static final long serialVersionUID = -7449468545133765366L;
	private static final String TABLE = PersistenceProperties.ProjectPermission.table;

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
	public void onPersist() {
	}

	public ProjectPermission() {
	}

	public ProjectPermission(String name, String role, String targetId, String projectId) {
		this.name = name;
		this.role = role;
		this.targetId = targetId;
		this.projectId = projectId;
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("权限对象（人、组织或群组）.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String role_FIELDNAME = "role";
	@FieldDescribe("角色.")
	@Column(length = length_255B, name = ColumnNamePrefix + role_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String role;

	public static final String targetId_FIELDNAME = "targetId";
	@FieldDescribe("项目ID或任务ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + targetId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetId;

	public static final String projectId_FIELDNAME = "projectId";
	@FieldDescribe("项目ID。")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + projectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + projectId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String projectId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
}
