package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.*;

import javax.persistence.OrderColumn;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Schema(name = "Publish", description = "流程平台数据发布.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Publish.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Publish.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Publish extends Activity {

	private static final long serialVersionUID = 588240173459487399L;
	private static final String TABLE = PersistenceProperties.Element.Publish.table;
	public static final String PUBLISH_TARGET_CMS = "cms";
	public static final String PUBLISH_TARGET_TABLE = "table";
	public static final String CMS_CATEGORY_FROM_DATA = "dataPath";

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
		// nothing
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.customData = this.getProperties().getCustomData();
			this.publishTableList = this.getProperties().getPublishTableList();
		}
	}

	@Override
	public List<String> getRouteList() {
		if (StringUtils.isNotEmpty(this.getRoute())) {
			return ListTools.toList(this.getRoute());
		} else {
			return new ArrayList<>();
		}
	}

	public PublishProperties getProperties() {
		if (null == this.properties) {
			this.properties = new PublishProperties();
		}
		return this.properties;
	}

	public void setProperties(PublishProperties properties) {
		this.properties = properties;
	}

	public Publish() {
		this.properties = new PublishProperties();
	}

	public static final String CUSTOMDATA_FIELDNAME = "customData";
	@Transient
	private JsonElement customData;

	@Override
	public JsonElement getCustomData() {
		if (null != customData) {
			return this.customData;
		} else {
			return this.getProperties().getCustomData();
		}
	}

	@Override
	public void setCustomData(JsonElement customData) {
		this.customData = customData;
		this.getProperties().setCustomData(customData);
	}

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
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	@FieldDescribe("待阅群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME
					+ JoinIndexNameSuffix))
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
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + reviewUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + reviewUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + reviewUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> reviewUnitList;

	@FieldDescribe("参与人群组名称,存储group,多值.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ reviewGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + reviewGroupList_FIELDNAME
					+ JoinIndexNameSuffix))
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
	private Boolean allowReroute;

	@FieldDescribe("允许调度到此节点")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRerouteTo_FIELDNAME)
	private Boolean allowRerouteTo;

	public static final String route_FIELDNAME = "route";
	@IdReference(Route.class)
	@FieldDescribe("出口路由.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + route_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String route;

	public static final String publishTarget_FIELDNAME = "publishTarget";
	@FieldDescribe("发布目标：cms|table")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + publishTarget_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String publishTarget;

	public static final String categorySelectType_FIELDNAME = "categorySelectType";
	@FieldDescribe("内容管理分类来源：categoryId|dataPath")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categorySelectType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categorySelectType;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("内容管理分类ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String categoryIdDataPath_FIELDNAME = "categoryIdDataPath";
	@FieldDescribe("内容管理分类ID数据路径")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categoryIdDataPath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryIdDataPath;

	public static final String useProcessForm_FIELDNAME = "useProcessForm";
	@FieldDescribe("是否使用流程表.")
	@Column(name = ColumnNamePrefix + useProcessForm_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean useProcessForm;

	public static final String inheritAttachment_FIELDNAME = "inheritAttachment";
	@FieldDescribe("是否拷贝附件.")
	@Column(name = ColumnNamePrefix + inheritAttachment_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean inheritAttachment;

	public static final String cmsCreatorIdentity_FIELDNAME = "cmsCreatorIdentity";
	@FieldDescribe("内容管理文档创建者身份.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ cmsCreatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cmsCreatorIdentity;

	public static final String publishCmsCreatorType_FIELDNAME = "publishCmsCreatorType";
	@FieldDescribe("内容管理文档创建者来源类型:creator, identity, lastIdentity")
	@Enumerated(EnumType.STRING)
	@Column(length = PublishCmsCreatorType.length, name = ColumnNamePrefix + publishCmsCreatorType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private PublishCmsCreatorType cmsCreatorType;

	public static final String cmsCreatorScript_FIELDNAME = "cmsCreatorScript";
	@IdReference(Script.class)
	@FieldDescribe("内容管理文档创建者脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + cmsCreatorScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cmsCreatorScript;

	public static final String cmsCreatorScriptText_FIELDNAME = "cmsCreatorScriptText";
	@FieldDescribe("内容管理文档创建者脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + cmsCreatorScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cmsCreatorScriptText;

	public static final String titleDataPath_FIELDNAME = "titleDataPath";
	@FieldDescribe("内容管理文档标题数据路径")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + titleDataPath_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String titleDataPath;

	public static final String readerDataPathList_FIELDNAME = "readerDataPathList";
	@FieldDescribe("内容管理文档读者数据路径(多值逗号隔开)")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + readerDataPathList_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readerDataPathList;

	public static final String authorDataPathList_FIELDNAME = "authorDataPathList";
	@FieldDescribe("内容管理文档作者数据路径(多值逗号隔开)")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + authorDataPathList_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String authorDataPathList;

	public static final String pictureDataPathList_FIELDNAME = "pictureDataPathList";
	@FieldDescribe("内容管理文档首页图片数据路径(多值逗号隔开)")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pictureDataPathList_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pictureDataPathList;

	public static final String notifyDataPathList_FIELDNAME = "notifyDataPathList";
	@FieldDescribe("内容管理文档消息推送范围数据路径(多值逗号隔开)")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + notifyDataPathList_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String notifyDataPathList;

	public static final String targetAssignDataScript_FIELDNAME = "targetAssignDataScript";
	@IdReference(Script.class)
	@FieldDescribe("内容管理数据脚本.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ targetAssignDataScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetAssignDataScript;

	public static final String targetAssignDataScriptText_FIELDNAME = "targetAssignDataScriptText";
	@FieldDescribe("内容管理数据脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + targetAssignDataScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String targetAssignDataScriptText;

	@FieldDescribe("发布的数据表")
	@Transient
	private List<PublishTable> publishTableList;

	@IdReference(Script.class)
	@FieldDescribe("生成displayLog脚本.")
	@Column(length = length_255B, name = ColumnNamePrefix + displayLogScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String displayLogScript;

	@FieldDescribe("生成displayLog脚本文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + displayLogScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String displayLogScriptText;

	public static final String edition_FIELDNAME = "edition";
	@FieldDescribe("版本编码.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + edition_FIELDNAME)
	private String edition;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private PublishProperties properties;

	@Override
	public String getDisplayLogScript() {
		return displayLogScript;
	}

	@Override
	public void setDisplayLogScript(String displayLogScript) {
		this.displayLogScript = displayLogScript;
	}

	@Override
	public String getDisplayLogScriptText() {
		return displayLogScriptText;
	}

	@Override
	public void setDisplayLogScriptText(String displayLogScriptText) {
		this.displayLogScriptText = displayLogScriptText;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getProcess() {
		return process;
	}

	@Override
	public void setProcess(String process) {
		this.process = process;
	}

	@Override
	public String getPosition() {
		return position;
	}

	@Override
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

	@Override
	public String getForm() {
		return form;
	}

	@Override
	public void setForm(String form) {
		this.form = form;
	}

	@Override
	public List<String> getReviewIdentityList() {
		return reviewIdentityList;
	}

	@Override
	public void setReviewIdentityList(List<String> reviewIdentityList) {
		this.reviewIdentityList = reviewIdentityList;
	}

	@Override
	public List<String> getReviewUnitList() {
		return reviewUnitList;
	}

	@Override
	public void setReviewUnitList(List<String> reviewUnitList) {
		this.reviewUnitList = reviewUnitList;
	}

	@Override
	public String getReviewScript() {
		return reviewScript;
	}

	@Override
	public void setReviewScript(String reviewScript) {
		this.reviewScript = reviewScript;
	}

	@Override
	public String getReviewScriptText() {
		return reviewScriptText;
	}

	@Override
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

	@Override
	public List<String> getReadIdentityList() {
		return readIdentityList;
	}

	@Override
	public void setReadIdentityList(List<String> readIdentityList) {
		this.readIdentityList = readIdentityList;
	}

	@Override
	public List<String> getReadUnitList() {
		return readUnitList;
	}

	@Override
	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	@Override
	public String getReadScript() {
		return readScript;
	}

	@Override
	public void setReadScript(String readScript) {
		this.readScript = readScript;
	}

	@Override
	public String getReadScriptText() {
		return readScriptText;
	}

	@Override
	public void setReadScriptText(String readScriptText) {
		this.readScriptText = readScriptText;
	}

	@Override
	public Boolean getAllowReroute() {
		return allowReroute;
	}

	@Override
	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	@Override
	public Boolean getAllowRerouteTo() {
		return allowRerouteTo;
	}

	@Override
	public void setAllowRerouteTo(Boolean allowRerouteTo) {
		this.allowRerouteTo = allowRerouteTo;
	}

	@Override
	public String getReadDuty() {
		return readDuty;
	}

	public void setReadDuty(String readDuty) {
		this.readDuty = readDuty;
	}

	@Override
	public List<String> getReadDataPathList() {
		return readDataPathList;
	}

	@Override
	public void setReadDataPathList(List<String> readDataPathList) {
		this.readDataPathList = readDataPathList;
	}

	@Override
	public String getReviewDuty() {
		return reviewDuty;
	}

	@Override
	public void setReviewDuty(String reviewDuty) {
		this.reviewDuty = reviewDuty;
	}

	@Override
	public List<String> getReviewDataPathList() {
		return reviewDataPathList;
	}

	@Override
	public void setReviewDataPathList(List<String> reviewDataPathList) {
		this.reviewDataPathList = reviewDataPathList;
	}

	@Override
	public List<String> getReadGroupList() {
		return readGroupList;
	}

	@Override
	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	@Override
	public List<String> getReviewGroupList() {
		return reviewGroupList;
	}

	@Override
	public void setReviewGroupList(List<String> reviewGroupList) {
		this.reviewGroupList = reviewGroupList;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String getOpinionGroup() {
		return opinionGroup;
	}

	@Override
	public void setOpinionGroup(String opinionGroup) {
		this.opinionGroup = opinionGroup;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getPublishTarget() {
		return publishTarget;
	}

	public void setPublishTarget(String publishTarget) {
		this.publishTarget = publishTarget;
	}

	public String getCategorySelectType() {
		return categorySelectType;
	}

	public void setCategorySelectType(String categorySelectType) {
		this.categorySelectType = categorySelectType;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryIdDataPath() {
		return categoryIdDataPath;
	}

	public void setCategoryIdDataPath(String categoryIdDataPath) {
		this.categoryIdDataPath = categoryIdDataPath;
	}

	public Boolean getUseProcessForm() {
		return useProcessForm;
	}

	public void setUseProcessForm(Boolean useProcessForm) {
		this.useProcessForm = useProcessForm;
	}

	public Boolean getInheritAttachment() {
		return inheritAttachment;
	}

	public void setInheritAttachment(Boolean inheritAttachment) {
		this.inheritAttachment = inheritAttachment;
	}

	public String getTitleDataPath() {
		return titleDataPath;
	}

	public void setTitleDataPath(String titleDataPath) {
		this.titleDataPath = titleDataPath;
	}

	public String getReaderDataPathList() {
		return readerDataPathList;
	}

	public void setReaderDataPathList(String readerDataPathList) {
		this.readerDataPathList = readerDataPathList;
	}

	public String getAuthorDataPathList() {
		return authorDataPathList;
	}

	public void setAuthorDataPathList(String authorDataPathList) {
		this.authorDataPathList = authorDataPathList;
	}

	public String getPictureDataPathList() {
		return pictureDataPathList;
	}

	public void setPictureDataPathList(String pictureDataPathList) {
		this.pictureDataPathList = pictureDataPathList;
	}

	public String getNotifyDataPathList() {
		return notifyDataPathList;
	}

	public void setNotifyDataPathList(String notifyDataPathList) {
		this.notifyDataPathList = notifyDataPathList;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}

	public List<PublishTable> getPublishTableList() {
		return publishTableList;
	}

	public void setPublishTableList(List<PublishTable> publishTableList) {
		this.publishTableList = publishTableList;
		this.getProperties().setPublishTableList(publishTableList);
	}

	public String getCmsCreatorIdentity() {
		return cmsCreatorIdentity;
	}

	public void setCmsCreatorIdentity(String cmsCreatorIdentity) {
		this.cmsCreatorIdentity = cmsCreatorIdentity;
	}

	public PublishCmsCreatorType getCmsCreatorType() {
		return cmsCreatorType;
	}

	public void setCmsCreatorType(
			PublishCmsCreatorType cmsCreatorType) {
		this.cmsCreatorType = cmsCreatorType;
	}

	public String getCmsCreatorScript() {
		return cmsCreatorScript;
	}

	public void setCmsCreatorScript(String cmsCreatorScript) {
		this.cmsCreatorScript = cmsCreatorScript;
	}

	public String getCmsCreatorScriptText() {
		return cmsCreatorScriptText;
	}

	public void setCmsCreatorScriptText(String cmsCreatorScriptText) {
		this.cmsCreatorScriptText = cmsCreatorScriptText;
	}
}
