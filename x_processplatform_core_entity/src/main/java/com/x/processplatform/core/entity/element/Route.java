package com.x.processplatform.core.entity.element;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

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
@Table(name = PersistenceProperties.Element.Route.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Route extends SliceJpaObject {

	private static final long serialVersionUID = -1151288890276589956L;
	private static final String TABLE = PersistenceProperties.Element.Route.table;

	@PrePersist
	public void prePersist() throws Exception {
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
	public void preUpdate() throws Exception {
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

	private void onPersist() throws Exception {
		/* 如果脚本为空，添加默认返回true 的脚本 */
		if (StringUtils.isEmpty(script) && StringUtils.isEmpty(scriptText)) {
			this.scriptText = "return true;";
		}
	}

	/* 更新运行方法 */

	public static String[] FLAGS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	/* 路由名称可能包含 >= 这样的符号，所以不校验简单字串 */
	@CheckPersist(allowEmpty = true)
	private String name;

	@EntityFieldDescribe("代理节点别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("流程标识符.")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("目标类别.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = "xactivityType")
	@Index(name = TABLE + "_activityType")
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	@EntityFieldDescribe("目标活动节点标识符.")
	@Column(length = JpaObject.length_id, name = "xactivity")
	@Index(name = TABLE + "_activity")
	@CheckPersist(allowEmpty = true)
	private String activity;

	@EntityFieldDescribe("路由的曲线坐标.")
	@Column(length = JpaObject.length_255B, name = "xtrack")
	@CheckPersist(allowEmpty = true)
	private String track;

	@EntityFieldDescribe("文本位置.")
	@Column(length = JpaObject.length_64B, name = "xposition")
	@CheckPersist(allowEmpty = true)
	private String position;

	@EntityFieldDescribe("路由脚本")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xscript")
	@CheckPersist(allowEmpty = true)
	private String script;

	@EntityFieldDescribe("路由脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xscriptText")
	@CheckPersist(allowEmpty = true)
	private String scriptText;

	@EntityFieldDescribe("如何与前一个环节处理人相同那么自动执行.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xpassSameTarget")
	private Boolean passSameTarget;

	@EntityFieldDescribe("超时时候的默认路由.")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xpassExpired")
	private Boolean passExpired;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getScriptText() {
		return scriptText;
	}

	public void setScriptText(String scriptText) {
		this.scriptText = scriptText;
	}

	public Boolean getPassSameTarget() {
		return passSameTarget;
	}

	public void setPassSameTarget(Boolean passSameTarget) {
		this.passSameTarget = passSameTarget;
	}

	public Boolean getPassExpired() {
		return passExpired;
	}

	public void setPassExpired(Boolean passExpired) {
		this.passExpired = passExpired;
	}

}
