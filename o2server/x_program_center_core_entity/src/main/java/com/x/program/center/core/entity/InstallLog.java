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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "InstallLog", description = "服务管理安装日志.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.InstallLog.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.InstallLog.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class InstallLog extends SliceJpaObject {

	private static final long serialVersionUID = -1080102795064855623L;

	private static final String TABLE = PersistenceProperties.InstallLog.TABLE;

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
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("分类")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String category;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("版本")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态：valid|invalid.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("安装概要内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	private String data;

	public static final String installPerson_FIELDNAME = "installPerson";
	@FieldDescribe("安装用户.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + installPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String installPerson;

	public static final String installTime_FIELDNAME = "installTime";
	@FieldDescribe("安装时间")
	@Column(name = ColumnNamePrefix + installTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + installTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date installTime;

	public static final String unInstallPerson_FIELDNAME = "unInstallPerson";
	@FieldDescribe("卸载用户.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unInstallPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unInstallPerson;

	public static final String unInstallTime_FIELDNAME = "unInstallTime";
	@FieldDescribe("卸载时间")
	@Column(name = ColumnNamePrefix + unInstallTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unInstallTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date unInstallTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getInstallPerson() {
		return installPerson;
	}

	public void setInstallPerson(String installPerson) {
		this.installPerson = installPerson;
	}

	public Date getInstallTime() {
		return installTime;
	}

	public void setInstallTime(Date installTime) {
		this.installTime = installTime;
	}

	public String getUnInstallPerson() {
		return unInstallPerson;
	}

	public void setUnInstallPerson(String unInstallPerson) {
		this.unInstallPerson = unInstallPerson;
	}

	public Date getUnInstallTime() {
		return unInstallTime;
	}

	public void setUnInstallTime(Date unInstallTime) {
		this.unInstallTime = unInstallTime;
	}
}
