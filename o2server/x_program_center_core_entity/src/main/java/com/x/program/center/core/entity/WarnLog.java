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
import com.x.base.core.project.tools.StringTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "WarnLog", description = "服务管理警告.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.WarnLog.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.WarnLog.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WarnLog extends SliceJpaObject {

	private static final long serialVersionUID = -5283859279413196452L;

	private static final String TABLE = PersistenceProperties.WarnLog.TABLE;

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
		if (null == this.collected) {
			this.collected = false;
		}
		if (StringTools.utf8Length(this.version) > JpaObject.length_32B) {
			this.version = StringTools.utf8SubString(this.version, JpaObject.length_32B);
		}
		/** 不主动去修改时间 */
		// if (null == this.occurTime) {
		// this.occurTime = new Date();
		// }
		if (StringTools.utf8Length(this.loggerName) > JpaObject.length_255B) {
			this.loggerName = StringTools.utf8SubString(this.loggerName, JpaObject.length_255B);
		}
		if (StringTools.utf8Length(this.message) > JpaObject.length_2K) {
			this.message = StringTools.utf8SubString(this.message, JpaObject.length_2K);
		}
	}

	/* 更新运行方法 */

	public static final String collected_FIELDNAME = "collected";
	@FieldDescribe("是否提交到collect服务器.")
	@Column(name = ColumnNamePrefix + collected_FIELDNAME)
	private Boolean collected;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("服务器版本.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String occurTime_FIELDNAME = "occurTime";
	@FieldDescribe("错误发生时间.")
	@Column(name = ColumnNamePrefix + occurTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + occurTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date occurTime;

	public static final String loggerName_FIELDNAME = "loggerName";
	@FieldDescribe("loggerName.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + loggerName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + loggerName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String loggerName;

	public static final String message_FIELDNAME = "message";
	@FieldDescribe("错误消息.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + message_FIELDNAME)
	private String message;

	// public static String[] FLAGS = new String[] { "id" };

	public Boolean getCollected() {
		return collected;
	}

	public void setCollected(Boolean collected) {
		this.collected = collected;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}