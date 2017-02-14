package com.x.processplatform.core.entity.element;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.QueryStatTimed.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class QueryStatTimed extends SliceJpaObject {

	private static final long serialVersionUID = -1926258273469924948L;

	private static final String TABLE = PersistenceProperties.Element.QueryStatTimed.table;

	@PrePersist
	public void prePersist() {
		Date date = new Date();
		if (null == this.createTime) {
			this.createTime = date;
		}
		this.updateTime = date;
		if (null == this.sequence) {
			this.sequence = StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId());
		}
		this.onPersist();
	}

	@PreUpdate
	public void preUpdate() {
		this.updateTime = new Date();
		this.onPersist();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号,由创建时间以及ID组成.在保存时自动生成.")
	@Column(length = AbstractPersistenceProperties.length_sequence, name = "xsequence")
	@Index(name = TABLE + "_sequence")
	private String sequence;

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {

	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "id" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("运行统计的queryStat.")
	@Column(length = JpaObject.length_id, name = "xqueryStat")
	@Index(name = TABLE + "_queryStat")
	@CheckPersist(allowEmpty = false)
	private String queryStat;

	@EntityFieldDescribe("运行在应用服务上.")
	@Column(length = JpaObject.length_id, name = "xproject")
	@Index(name = TABLE + "_project")
	@CheckPersist(allowEmpty = false)
	private String project;

	@EntityFieldDescribe("预约的运行时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(length = JpaObject.length_255B, name = "xscheduleTime")
	@Index(name = TABLE + "scheduleTime")
	@CheckPersist(allowEmpty = false)
	private Date scheduleTime;

	@EntityFieldDescribe("过期时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(length = JpaObject.length_255B, name = "xexpiredTime")
	@Index(name = TABLE + "_expiredTime")
	@CheckPersist(allowEmpty = false)
	private Date expiredTime;

	@EntityFieldDescribe("运行间隔,分钟.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xtimingInterval")
	@Index(name = TABLE + "_timingInterval")
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