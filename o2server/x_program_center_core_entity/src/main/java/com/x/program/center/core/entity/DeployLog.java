package com.x.program.center.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(name = "InstallLog", description = "服务管理更新升级日志.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.DeployLog.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DeployLog.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DeployLog extends SliceJpaObject {

	private static final long serialVersionUID = -1080102795064855623L;

	private static final String TABLE = PersistenceProperties.DeployLog.TABLE;

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

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("标题")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("文件名字")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("版本")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String remark_FIELDNAME = "remark";
	@FieldDescribe("更新说明.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + remark_FIELDNAME)
	private String remark;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
