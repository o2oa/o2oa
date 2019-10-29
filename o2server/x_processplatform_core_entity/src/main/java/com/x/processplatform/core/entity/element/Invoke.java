package com.x.processplatform.core.entity.element;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.Invoke.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Invoke.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Invoke extends Activity {

	private static final long serialVersionUID = -8825559455909207046L;
	private static final String TABLE = PersistenceProperties.Element.Invoke.table;

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

	@FieldDescribe("分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + group_FIELDNAME)
	private String group;

	@FieldDescribe("意见分组")
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinionGroup_FIELDNAME)
	private String opinionGroup;

	@FieldDescribe("节点名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String name;

	@Flag
	@FieldDescribe("节点别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String alias;

	@FieldDescribe("描述.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	@IdReference(Process.class)
	@FieldDescribe("流程标识,不为空.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	@FieldDescribe("节点位置.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + position_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String position;

	@FieldDescribe("前端自定内容.")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + extension_FIELDNAME)
	@Basic(fetch = FetchType.EAGER)
	@Lob
	@CheckPersist(allowEmpty = true)
	private String extension;

	@IdReference(Form.class)
	@FieldDescribe("节点使用的表单.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	@FieldDescribe("待阅人名称,存储 Identity,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readIdentityList;

	@FieldDescribe("待阅组织名称,存储unit,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	@FieldDescribe("待阅群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + readGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + readGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readGroupList;

	@IdReference(Script.class)
	@FieldDescribe("待阅人脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + readScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readScript;

	@FieldDescribe("待阅人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + readScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readScriptText;

	@FieldDescribe("待阅角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + readDuty_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readDuty;

	@FieldDescribe("活动待阅人员data数据路径.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readDataPathList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readDataPathList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + readDataPathList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readDataPathList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readDataPathList;

	@FieldDescribe("参与人名称,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reviewIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewIdentityList;

	@FieldDescribe("参与人组织名称,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + reviewUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + reviewUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewUnitList;

	@FieldDescribe("参与人群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + reviewGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + reviewGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewGroupList;

	@IdReference(Script.class)
	@FieldDescribe("参与人脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + reviewScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewScript;

	@FieldDescribe("参与人脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + reviewScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewScriptText;

	@FieldDescribe("参阅角色定义内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + reviewDuty_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String reviewDuty;

	@FieldDescribe("参阅人员data数据路径.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewDataPathList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ reviewDataPathList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + reviewDataPathList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewDataPathList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewDataPathList;

	@IdReference(Script.class)
	@FieldDescribe("活动到达前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	@FieldDescribe("活动到达前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动到达后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	@FieldDescribe("活动到达后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动执行前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	@FieldDescribe("活动执行前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	@IdReference(Script.class)
	@FieldDescribe("活动执行后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	@FieldDescribe("活动执行后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	@IdReference(Script.class)
	@FieldDescribe("路由查询前事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	@FieldDescribe("路由查询前事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	@IdReference(Script.class)
	@FieldDescribe("路由查询后事件脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	@FieldDescribe("路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	@FieldDescribe("允许调度")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowReroute_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + allowReroute_FIELDNAME)
	private Boolean allowReroute;

	@FieldDescribe("允许调度到此节点")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRerouteTo_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + allowRerouteTo_FIELDNAME)
	private Boolean allowRerouteTo;

	public static final String route_FIELDNAME = "route";
	@IdReference(Route.class)
	@FieldDescribe("出口路由.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + route_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String route;

	public static final String invokeMode_FIELDNAME = "invokeMode";
	@FieldDescribe("调用方式.")
	@Enumerated(EnumType.STRING)
	@Column(length = InvokeMode.length, name = ColumnNamePrefix + invokeMode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private InvokeMode invokeMode;

	public static final String jaxwsAddress_FIELDNAME = "jaxwsAddress";
	@FieldDescribe("jaxws地址")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + jaxwsAddress_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsAddress;

	public static final String jaxwsMethod_FIELDNAME = "jaxwsMethod";
	@FieldDescribe("jaxws调用方法")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + jaxwsMethod_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsMethod;

	public static final String jaxwsParameterScript_FIELDNAME = "jaxwsParameterScript";
	@IdReference(Script.class)
	@FieldDescribe("jaxws参数脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxwsParameterScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsParameterScript;

	public static final String jaxwsParameterScriptText_FIELDNAME = "jaxwsParameterScriptText";
	@FieldDescribe("jaxws参数脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxwsParameterScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsParameterScriptText;

	public static final String jaxwsResponseScript_FIELDNAME = "jaxwsResponseScript";
	@IdReference(Script.class)
	@FieldDescribe("路由查询后事件脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxwsResponseScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsResponseScript;

	public static final String jaxwsResponseScriptText_FIELDNAME = "jaxwsResponseScriptText";
	@FieldDescribe("路由查询后事件脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxwsResponseScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxwsResponseScriptText;

	public static final String jaxrsAddress_FIELDNAME = "jaxrsAddress";
	@FieldDescribe("jaxrs地址")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + jaxrsAddress_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsAddress;

	public static final String jaxrsMethod_FIELDNAME = "jaxrsMethod";
	@FieldDescribe("jaxrs方式")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + jaxrsMethod_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsMethod;

	public static final String jaxrsParameterScript_FIELDNAME = "jaxrsParameterScript";
	@IdReference(Script.class)
	@FieldDescribe("jaxrs参数脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxrsParameterScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsParameterScript;

	public static final String jaxrsParameterScriptText_FIELDNAME = "jaxrsParameterScriptText";
	@FieldDescribe("jaxrs参数脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxrsParameterScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsParameterScriptText;

	public static final String jaxrsBodyScript_FIELDNAME = "jaxrsBodyScript";
	@IdReference(Script.class)
	@FieldDescribe("jaxrs数据脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxrsBodyScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsBodyScript;

	public static final String jaxrsBodyScriptText_FIELDNAME = "jaxrsBodyScriptText";
	@FieldDescribe("jaxrs参数脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxrsBodyScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsBodyScriptText;

	public static final String jaxrsHeadScript_FIELDNAME = "jaxrsHeadScript";
	@IdReference(Script.class)
	@FieldDescribe("jaxrs请求头脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxrsHeadScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsHeadScript;

	public static final String jaxrsHeadScriptText_FIELDNAME = "jaxrsHeadScriptText";
	@FieldDescribe("jaxrs请求头脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxrsHeadScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsHeadScriptText;

	public static final String jaxrsContentType_FIELDNAME = "jaxrsContentType";
	@FieldDescribe("发送请求的类型")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + jaxrsContentType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsContentType;

	public static final String jaxrsResponseScript_FIELDNAME = "jaxrsResponseScript";
	@IdReference(Script.class)
	@FieldDescribe("jaxrs响应脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ jaxrsResponseScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsResponseScript;

	public static final String jaxrsResponseScriptText_FIELDNAME = "jaxrsResponseScriptText";
	@FieldDescribe("jaxrs响应脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + jaxrsResponseScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String jaxrsResponseScriptText;

	public static final String jaxrsWithCipher_FIELDNAME = "jaxrsWithCipher";
	@FieldDescribe("是否附带cipher内容")
	@Column(name = ColumnNamePrefix + jaxrsWithCipher_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean jaxrsWithCipher;

	public static final String internal_FIELDNAME = "internal";
	@FieldDescribe("是否是内部请求.")
	@Column(name = ColumnNamePrefix + internal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean internal;

	public static final String internalProject_FIELDNAME = "internalProject";
	@FieldDescribe("内部的项目简称")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + internalProject_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String internalProject;

	public static final String async_FIELDNAME = "async";
	@FieldDescribe("是否是异步调用")
	@Column(name = ColumnNamePrefix + async_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean async;

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

	public List<String> getReviewUnitList() {
		return reviewUnitList;
	}

	public void setReviewUnitList(List<String> reviewUnitList) {
		this.reviewUnitList = reviewUnitList;
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

	public InvokeMode getInvokeMode() {
		return invokeMode;
	}

	public void setInvokeMode(InvokeMode invokeMode) {
		this.invokeMode = invokeMode;
	}

	public String getJaxwsAddress() {
		return jaxwsAddress;
	}

	public void setJaxwsAddress(String jaxwsAddress) {
		this.jaxwsAddress = jaxwsAddress;
	}

	public String getJaxwsMethod() {
		return jaxwsMethod;
	}

	public void setJaxwsMethod(String jaxwsMethod) {
		this.jaxwsMethod = jaxwsMethod;
	}

	public String getJaxwsParameterScript() {
		return jaxwsParameterScript;
	}

	public void setJaxwsParameterScript(String jaxwsParameterScript) {
		this.jaxwsParameterScript = jaxwsParameterScript;
	}

	public String getJaxwsParameterScriptText() {
		return jaxwsParameterScriptText;
	}

	public void setJaxwsParameterScriptText(String jaxwsParameterScriptText) {
		this.jaxwsParameterScriptText = jaxwsParameterScriptText;
	}

	public String getJaxwsResponseScript() {
		return jaxwsResponseScript;
	}

	public void setJaxwsResponseScript(String jaxwsResponseScript) {
		this.jaxwsResponseScript = jaxwsResponseScript;
	}

	public String getJaxwsResponseScriptText() {
		return jaxwsResponseScriptText;
	}

	public void setJaxwsResponseScriptText(String jaxwsResponseScriptText) {
		this.jaxwsResponseScriptText = jaxwsResponseScriptText;
	}

	public String getJaxrsAddress() {
		return jaxrsAddress;
	}

	public void setJaxrsAddress(String jaxrsAddress) {
		this.jaxrsAddress = jaxrsAddress;
	}

	public String getJaxrsMethod() {
		return jaxrsMethod;
	}

	public void setJaxrsMethod(String jaxrsMethod) {
		this.jaxrsMethod = jaxrsMethod;
	}

	public String getJaxrsParameterScript() {
		return jaxrsParameterScript;
	}

	public void setJaxrsParameterScript(String jaxrsParameterScript) {
		this.jaxrsParameterScript = jaxrsParameterScript;
	}

	public String getJaxrsParameterScriptText() {
		return jaxrsParameterScriptText;
	}

	public void setJaxrsParameterScriptText(String jaxrsParameterScriptText) {
		this.jaxrsParameterScriptText = jaxrsParameterScriptText;
	}

	public String getJaxrsResponseScript() {
		return jaxrsResponseScript;
	}

	public void setJaxrsResponseScript(String jaxrsResponseScript) {
		this.jaxrsResponseScript = jaxrsResponseScript;
	}

	public String getJaxrsResponseScriptText() {
		return jaxrsResponseScriptText;
	}

	public void setJaxrsResponseScriptText(String jaxrsResponseScriptText) {
		this.jaxrsResponseScriptText = jaxrsResponseScriptText;
	}

	public String getJaxrsBodyScript() {
		return jaxrsBodyScript;
	}

	public void setJaxrsBodyScript(String jaxrsBodyScript) {
		this.jaxrsBodyScript = jaxrsBodyScript;
	}

	public String getJaxrsBodyScriptText() {
		return jaxrsBodyScriptText;
	}

	public void setJaxrsBodyScriptText(String jaxrsBodyScriptText) {
		this.jaxrsBodyScriptText = jaxrsBodyScriptText;
	}

	public String getJaxrsContentType() {
		return jaxrsContentType;
	}

	public void setJaxrsContentType(String jaxrsContentType) {
		this.jaxrsContentType = jaxrsContentType;
	}

	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	public List<String> getReadUnitList() {
		return readUnitList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
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

	public Boolean getJaxrsWithCipher() {
		return jaxrsWithCipher;
	}

	public void setJaxrsWithCipher(Boolean jaxrsWithCipher) {
		this.jaxrsWithCipher = jaxrsWithCipher;
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

	public String getReadDuty() {
		return readDuty;
	}

	public void setReadDuty(String readDuty) {
		this.readDuty = readDuty;
	}

	public List<String> getReadDataPathList() {
		return readDataPathList;
	}

	public void setReadDataPathList(List<String> readDataPathList) {
		this.readDataPathList = readDataPathList;
	}

	public String getReviewDuty() {
		return reviewDuty;
	}

	public void setReviewDuty(String reviewDuty) {
		this.reviewDuty = reviewDuty;
	}

	public List<String> getReviewDataPathList() {
		return reviewDataPathList;
	}

	public void setReviewDataPathList(List<String> reviewDataPathList) {
		this.reviewDataPathList = reviewDataPathList;
	}

	public Boolean getInternal() {
		return internal;
	}

	public void setInternal(Boolean internal) {
		this.internal = internal;
	}

	public String getInternalProject() {
		return internalProject;
	}

	public void setInternalProject(String internalProject) {
		this.internalProject = internalProject;
	}

	public List<String> getReadGroupList() {
		return readGroupList;
	}

	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	public List<String> getReviewGroupList() {
		return reviewGroupList;
	}

	public void setReviewGroupList(List<String> reviewGroupList) {
		this.reviewGroupList = reviewGroupList;
	}

	public Boolean getAsync() {
		return async;
	}

	public void setAsync(Boolean async) {
		this.async = async;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOpinionGroup() {
		return opinionGroup;
	}

	public void setOpinionGroup(String opinionGroup) {
		this.opinionGroup = opinionGroup;
	}

	public String getJaxrsHeadScript() {
		return jaxrsHeadScript;
	}

	public void setJaxrsHeadScript(String jaxrsHeadScript) {
		this.jaxrsHeadScript = jaxrsHeadScript;
	}

	public String getJaxrsHeadScriptText() {
		return jaxrsHeadScriptText;
	}

	public void setJaxrsHeadScriptText(String jaxrsHeadScriptText) {
		this.jaxrsHeadScriptText = jaxrsHeadScriptText;
	}

}