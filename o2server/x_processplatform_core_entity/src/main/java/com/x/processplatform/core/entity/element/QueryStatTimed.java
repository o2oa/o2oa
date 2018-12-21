package com.x.processplatform.core.entity.element;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryStatTimed.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.QueryStatTimed.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryStatTimed extends SliceJpaObject {

	private static final long serialVersionUID = -1926258273469924948L;

	private static final String TABLE = PersistenceProperties.Element.QueryStatTimed.table;

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

	/* 更新运行方法 */

	// public static String[] FLA GS = new String[] { "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String queryStat_FIELDNAME = "queryStat";
	@FieldDescribe("运行统计的queryStat.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + queryStat_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + queryStat_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String queryStat;

	public static final String project_FIELDNAME = "project";
	@FieldDescribe("运行在应用服务上.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + project_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + project_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String project;

	public static final String scheduleTime_FIELDNAME = "scheduleTime";
	@FieldDescribe("预约的运行时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + scheduleTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + scheduleTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date scheduleTime;

	public static final String expiredTime_FIELDNAME = "expiredTime";
	@FieldDescribe("过期时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + expiredTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expiredTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date expiredTime;

	public static final String timingInterval_FIELDNAME = "timingInterval";
	@FieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + timingInterval_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timingInterval_FIELDNAME)
	private Integer timingInterval;

	public Date getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Date scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public Integer getTimingInterval() {
		return timingInterval;
	}

	public void setTimingInterval(Integer timingInterval) {
		this.timingInterval = timingInterval;
	}

	public String getQueryStat() {
		return queryStat;
	}

	public void setQueryStat(String queryStat) {
		this.queryStat = queryStat;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

}