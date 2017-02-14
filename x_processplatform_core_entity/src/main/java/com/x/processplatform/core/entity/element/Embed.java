package com.x.processplatform.core.entity.element;

import java.util.Date;
import java.util.List;

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
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Embed.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Embed extends Activity {

	private static final long serialVersionUID = 5507535271081728076L;
	private static final String TABLE = PersistenceProperties.Element.Embed.table;

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

	public static String[] FLAGS = new String[] { "id", "alias" };

	/* flag标志位 */
	/* Entity 默认字段结束 */

	@EntityFieldDescribe("代理节点名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xname")
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String name;

	@EntityFieldDescribe("代理节点别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xalias")
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String alias;

	@EntityFieldDescribe("描述.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xdescription")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("流程ID,不为空.")
	@Column(length = JpaObject.length_id, name = "xprocess")
	@Index(name = TABLE + "_process")
	@CheckPersist(allowEmpty = false)
	private String process;

	@EntityFieldDescribe("节点位置.")
	@Column(length = JpaObject.length_32B, name = "xposition")
	@CheckPersist(allowEmpty = true)
	private String position;

	@EntityFieldDescribe("前端自定内容.")
	@Column(length = JpaObject.length_1M, name = "xextension")
	@Basic(fetch = FetchType.EAGER)
	@Lob
	@CheckPersist(allowEmpty = true)
	private String extension;

	@EntityFieldDescribe("节点使用的表单.")
	@Column(length = JpaObject.length_id, name = "xform")
	@Index(name = TABLE + "_form")
	@CheckPersist(allowEmpty = true)
	private String form;

	@EntityFieldDescribe("人工节点的待阅人名称,存储 Identity name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_readIdentityList", joinIndex = @Index(name = TABLE + "_readIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreadIdentityList")
	@ElementIndex(name = TABLE + "_readIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> readIdentityList;

	@EntityFieldDescribe("人工节点的待阅部门名称,存储 Department name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_readDepartmentList", joinIndex = @Index(name = TABLE + "_readDepartmentList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreadDepartmentList")
	@ElementIndex(name = TABLE + "_readDepartmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> readDepartmentList;

	@EntityFieldDescribe("待阅人脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xreadScript")
	@CheckPersist(allowEmpty = true)
	private String readScript;

	@EntityFieldDescribe("待阅人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xreadScriptText")
	@CheckPersist(allowEmpty = true)
	private String readScriptText;
	
	@EntityFieldDescribe("待阅角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xreadDuty")
	@CheckPersist(allowEmpty = true)
	private String readDuty;

	@EntityFieldDescribe("参与人名称,存储 Identity name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_reviewIdentityList", joinIndex = @Index(name = TABLE + "_reviewIdentityList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreviewIdentityList")
	@ElementIndex(name = TABLE + "_reviewIdentityList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reviewIdentityList;

	@EntityFieldDescribe("参与人部门名称,存储 Department name,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE
			+ "_reviewDepartmentList", joinIndex = @Index(name = TABLE + "_reviewDepartmentList_join"))
	@OrderColumn(name = AbstractPersistenceProperties.orderColumn)
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xreviewDepartmentList")
	@ElementIndex(name = TABLE + "_reviewDepartmentList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> reviewDepartmentList;

	@EntityFieldDescribe("参与人脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xreviewScript")
	@CheckPersist(allowEmpty = true)
	private String reviewScript;

	@EntityFieldDescribe("参与人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xreviewScriptText")
	@CheckPersist(allowEmpty = true)
	private String reviewScriptText;

	@EntityFieldDescribe("活动到达前事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xbeforeArriveScript")
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	@EntityFieldDescribe("活动到达前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xbeforeArriveScriptText")
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	@EntityFieldDescribe("活动到达后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xafterArriveScript")
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	@EntityFieldDescribe("活动到达后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterArriveScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	@EntityFieldDescribe("活动执行前事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xbeforeExecuteScript")
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	@EntityFieldDescribe("活动执行前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xbeforeExecuteScriptText")
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	@EntityFieldDescribe("活动执行后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xafterExecuteScript")
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	@EntityFieldDescribe("活动执行后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterExecuteScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	@EntityFieldDescribe("路由查询前事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xbeforeInquireScript")
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	@EntityFieldDescribe("路由查询前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xbeforeInquireScriptText")
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	@EntityFieldDescribe("路由查询后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xafterInquireScript")
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	@EntityFieldDescribe("路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xafterInquireScriptText")
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	@EntityFieldDescribe("出口路由.")
	@Column(length = JpaObject.length_id, name = "xroute")
	@CheckPersist(allowEmpty = true)
	private String route;

	@EntityFieldDescribe("允许调度")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xallowReroute")
	@Index(name = TABLE + "_allowReroute")
	private Boolean allowReroute;

	@EntityFieldDescribe("允许调度到此节点")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xallowRerouteTo")
	@Index(name = TABLE + "_allowRerouteTo")
	private Boolean allowRerouteTo;

	@EntityFieldDescribe("目标应用")
	@Column(length = JpaObject.length_id, name = "xtargetApplication")
	@Index(name = TABLE + "_targetApplication")
	@CheckPersist(allowEmpty = true)
	private String targetApplication;

	@EntityFieldDescribe("目标应用名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtargetApplicationName")
	@Index(name = TABLE + "targetApplicationName")
	@CheckPersist(allowEmpty = true)
	private String targetApplicationName;

	@EntityFieldDescribe("目标应用别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtargetApplicationAlias")
	@Index(name = TABLE + "_targetApplicationAlias")
	@CheckPersist(allowEmpty = true)
	private String targetApplicationAlias;

	@EntityFieldDescribe("目标流程ID.")
	@Column(length = JpaObject.length_id, name = "xtargetProcess")
	@Index(name = TABLE + "_targetProcess")
	@CheckPersist(allowEmpty = true)
	private String targetProcess;

	@EntityFieldDescribe("目标流程名称.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtargetProcessName")
	@Index(name = TABLE + "_targetProcessName")
	@CheckPersist(allowEmpty = true)
	private String targetProcessName;

	@EntityFieldDescribe("目标流程别名.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtargetProcessAlias")
	@Index(name = TABLE + "_targetProcessAlias")
	@CheckPersist(allowEmpty = true)
	private String targetProcessAlias;

	@EntityFieldDescribe("指定的启动者.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xtargetIdentity")
	@Index(name = TABLE + "_targetIdentity")
	@CheckPersist(allowEmpty = true)
	private String targetIdentity;

	@EntityFieldDescribe("是否继承Data")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xinheritData")
	@Index(name = TABLE + "_inheritData")
	private Boolean inheritData;

	@EntityFieldDescribe("嵌入启动流程的启动人员类型:creator, identity, lastIdentity")
	@Enumerated(EnumType.STRING)
	@Column(length = EmbedCreatorType.length, name = "xembedCreatorType")
	@CheckPersist(allowEmpty = true)
	private EmbedCreatorType embedCreatorType;

	@EntityFieldDescribe("产生标题脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtitleScript")
	@CheckPersist(allowEmpty = true)
	private String titleScript;

	@EntityFieldDescribe("产生标题脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xtitleScriptText")
	@CheckPersist(allowEmpty = true)
	private String titleScriptText;

	@EntityFieldDescribe("产生标题脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = "xtargetIdentityScript")
	@CheckPersist(allowEmpty = true)
	private String targetIdentityScript;

	@EntityFieldDescribe("产生标题脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xtargetIdentityScriptText")
	@CheckPersist(allowEmpty = true)
	private String targetIdentityScriptText;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getBeforeArriveScript() {
		return beforeArriveScript;
	}

	public void setBeforeArriveScript(String beforeArriveScript) {
		this.beforeArriveScript = beforeArriveScript;
	}

	public String getAfterArriveScript() {
		return afterArriveScript;
	}

	public void setAfterArriveScript(String afterArriveScript) {
		this.afterArriveScript = afterArriveScript;
	}

	public String getBeforeExecuteScript() {
		return beforeExecuteScript;
	}

	public void setBeforeExecuteScript(String beforeExecuteScript) {
		this.beforeExecuteScript = beforeExecuteScript;
	}

	public String getAfterExecuteScript() {
		return afterExecuteScript;
	}

	public void setAfterExecuteScript(String afterExecuteScript) {
		this.afterExecuteScript = afterExecuteScript;
	}

	public String getRoute() {
		return route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public List<String> getReviewIdentityList() {
		return reviewIdentityList;
	}

	public void setReviewIdentityList(List<String> reviewIdentityList) {
		this.reviewIdentityList = reviewIdentityList;
	}

	public List<String> getReviewDepartmentList() {
		return reviewDepartmentList;
	}

	public void setReviewDepartmentList(List<String> reviewDepartmentList) {
		this.reviewDepartmentList = reviewDepartmentList;
	}

	public String getReviewScript() {
		return reviewScript;
	}

	public void setReviewScript(String reviewScript) {
		this.reviewScript = reviewScript;
	}

	public String getReviewScriptText() {
		return reviewScriptText;
	}

	public void setReviewScriptText(String reviewScriptText) {
		this.reviewScriptText = reviewScriptText;
	}

	public String getBeforeArriveScriptText() {
		return beforeArriveScriptText;
	}

	public void setBeforeArriveScriptText(String beforeArriveScriptText) {
		this.beforeArriveScriptText = beforeArriveScriptText;
	}

	public String getAfterArriveScriptText() {
		return afterArriveScriptText;
	}

	public void setAfterArriveScriptText(String afterArriveScriptText) {
		this.afterArriveScriptText = afterArriveScriptText;
	}

	public String getBeforeExecuteScriptText() {
		return beforeExecuteScriptText;
	}

	public void setBeforeExecuteScriptText(String beforeExecuteScriptText) {
		this.beforeExecuteScriptText = beforeExecuteScriptText;
	}

	public String getAfterExecuteScriptText() {
		return afterExecuteScriptText;
	}

	public void setAfterExecuteScriptText(String afterExecuteScriptText) {
		this.afterExecuteScriptText = afterExecuteScriptText;
	}

	public String getBeforeInquireScript() {
		return beforeInquireScript;
	}

	public void setBeforeInquireScript(String beforeInquireScript) {
		this.beforeInquireScript = beforeInquireScript;
	}

	public String getBeforeInquireScriptText() {
		return beforeInquireScriptText;
	}

	public void setBeforeInquireScriptText(String beforeInquireScriptText) {
		this.beforeInquireScriptText = beforeInquireScriptText;
	}

	public String getAfterInquireScript() {
		return afterInquireScript;
	}

	public void setAfterInquireScript(String afterInquireScript) {
		this.afterInquireScript = afterInquireScript;
	}

	public String getAfterInquireScriptText() {
		return afterInquireScriptText;
	}

	public void setAfterInquireScriptText(String afterInquireScriptText) {
		this.afterInquireScriptText = afterInquireScriptText;
	}

	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	public List<String> getReadDepartmentList() {
		return readDepartmentList;
	}

	public void setReadDepartmentList(List<String> readDepartmentList) {
		this.readDepartmentList = readDepartmentList;
	}

	public String getReadScript() {
		return readScript;
	}

	public void setReadScript(String readScript) {
		this.readScript = readScript;
	}

	public String getReadScriptText() {
		return readScriptText;
	}

	public void setReadScriptText(String readScriptText) {
		this.readScriptText = readScriptText;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowRerouteTo() {
		return allowRerouteTo;
	}

	public void setAllowRerouteTo(Boolean allowRerouteTo) {
		this.allowRerouteTo = allowRerouteTo;
	}

	public String getTargetProcess() {
		return targetProcess;
	}

	public void setTargetProcess(String targetProcess) {
		this.targetProcess = targetProcess;
	}

	public String getTargetApplication() {
		return targetApplication;
	}

	public void setTargetApplication(String targetApplication) {
		this.targetApplication = targetApplication;
	}

	public String getTargetApplicationName() {
		return targetApplicationName;
	}

	public void setTargetApplicationName(String targetApplicationName) {
		this.targetApplicationName = targetApplicationName;
	}

	public String getTargetApplicationAlias() {
		return targetApplicationAlias;
	}

	public void setTargetApplicationAlias(String targetApplicationAlias) {
		this.targetApplicationAlias = targetApplicationAlias;
	}

	public String getTargetProcessName() {
		return targetProcessName;
	}

	public void setTargetProcessName(String targetProcessName) {
		this.targetProcessName = targetProcessName;
	}

	public String getTargetProcessAlias() {
		return targetProcessAlias;
	}

	public void setTargetProcessAlias(String targetProcessAlias) {
		this.targetProcessAlias = targetProcessAlias;
	}

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public Boolean getInheritData() {
		return inheritData;
	}

	public void setInheritData(Boolean inheritData) {
		this.inheritData = inheritData;
	}

	public EmbedCreatorType getEmbedCreatorType() {
		return embedCreatorType;
	}

	public void setEmbedCreatorType(EmbedCreatorType embedCreatorType) {
		this.embedCreatorType = embedCreatorType;
	}

	public String getTitleScript() {
		return titleScript;
	}

	public void setTitleScript(String titleScript) {
		this.titleScript = titleScript;
	}

	public String getTitleScriptText() {
		return titleScriptText;
	}

	public void setTitleScriptText(String titleScriptText) {
		this.titleScriptText = titleScriptText;
	}

	public String getTargetIdentityScript() {
		return targetIdentityScript;
	}

	public void setTargetIdentityScript(String targetIdentityScript) {
		this.targetIdentityScript = targetIdentityScript;
	}

	public String getTargetIdentityScriptText() {
		return targetIdentityScriptText;
	}

	public void setTargetIdentityScriptText(String targetIdentityScriptText) {
		this.targetIdentityScriptText = targetIdentityScriptText;
	}

	public String getReadDuty() {
		return readDuty;
	}

	public void setReadDuty(String readDuty) {
		this.readDuty = readDuty;
	}

}