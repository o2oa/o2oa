package com.x.processplatform.core.entity.element;

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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Route", description = "流程平台路由.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Route.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.Element.Route.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Route extends SliceJpaObject {

    // 路由类型,正常
    public static final String TYPE_NORMAL = "normal";
    // 路由类型,转交
    public static final String TYPE_APPENDTASK = "appendTask";
    // 路由类型,退回
    public static final String TYPE_BACK = "back";

    public static final String APPENDTASKIDENTITYTYPE_SCRIPT = "script";
    public static final String APPENDTASKIDENTITYTYPE_CONFIG = "config";

    private static final long serialVersionUID = -1151288890276589956L;
    private static final String TABLE = PersistenceProperties.Element.Route.table;

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

    public void onPersist() throws Exception {
        // 如果脚本为空，添加默认返回true 的脚本
        if (StringUtils.isEmpty(script) && StringUtils.isEmpty(scriptText)) {
            this.scriptText = "return true;";
        }
    }

    @PostLoad
    public void postLoad() {
        this.asyncSupported = this.getProperties().getAsyncSupported();
        this.soleDirect = this.getProperties().getSoleDirect();
        this.defaultSelected = this.getProperties().getDefaultSelected();
    }

    public Route() {
        this.properties = new RouteProperties();
    }

    public RouteProperties getProperties() {
        if (null == this.properties) {
            this.properties = new RouteProperties();
        }
        return this.properties;
    }

    public void setProperties(RouteProperties properties) {
        this.properties = properties;
    }

    public Boolean getAsyncSupported() {
        return asyncSupported;
    }

    public Boolean getSoleDirect() {
        return soleDirect;
    }

    public Boolean getDefaultSelected() {
        return defaultSelected;
    }

    public void setAsyncSupported(Boolean asyncSupported) {
        this.asyncSupported = asyncSupported;
        this.getProperties().setAsyncSupported(asyncSupported);
    }

    public void setSoleDirect(Boolean soleDirect) {
        this.soleDirect = soleDirect;
        this.getProperties().setSoleDirect(soleDirect);
    }

    public void setDefaultSelected(Boolean defaultSelected) {
        this.defaultSelected = defaultSelected;
        this.getProperties().setDefaultSelected(defaultSelected);
    }

    public static final String ASYNCSUPPORTED_FIELDNAME = "asyncSupported";
    @FieldDescribe("是否启用异步返回.")
    @Transient
    private Boolean asyncSupported;

    public static final String SOLEDIRECT_FIELDNAME = "soleDirect";
    @FieldDescribe("选择优先路由时是否直接执行路由(一票否决),默认null.")
    @Transient
    private Boolean soleDirect;

    public static final String DEFAULTSELECTED_FIELDNAME = "defaultSelected";
    @FieldDescribe("默认选中的路由.")
    @Transient
    private Boolean defaultSelected;

    public static final String name_FIELDNAME = "name";
    @FieldDescribe("名称.")
    @Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
    /* 路由名称可能包含 >= 这样的符号，所以不校验简单字串 */
    @CheckPersist(allowEmpty = true)
    private String name;

    public static final String alias_FIELDNAME = "alias";
    @Flag
    @FieldDescribe("代理节点别名.")
    @Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
    @CheckPersist(allowEmpty = true, simplyString = false)
    private String alias;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("描述.")
    @Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String description;

    public static final String process_FIELDNAME = "process";
    @IdReference(Process.class)
    @FieldDescribe("流程标识符.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String process;

    public static final String activityType_FIELDNAME = "activityType";
    @FieldDescribe("目标类别.")
    @Enumerated(EnumType.STRING)
    @Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private ActivityType activityType;

    public static final String activity_FIELDNAME = "activity";
    @IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
            End.class, Invoke.class, Manual.class, Merge.class, Parallel.class, Service.class, Split.class })
    @FieldDescribe("目标活动节点标识符.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String activity;

    public static final String track_FIELDNAME = "track";
    @FieldDescribe("路由的曲线坐标.")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + track_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String track;

    public static final String orderNumber_FIELDNAME = "orderNumber";
    @FieldDescribe("排序号,升序排列,为空在最后.")
    @Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
    @Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
    private Integer orderNumber;

    public static final String position_FIELDNAME = "position";
    @FieldDescribe("文本位置.")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + position_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String position;

    public static final String script_FIELDNAME = "script";
    @IdReference(Script.class)
    @FieldDescribe("路由脚本")
    @Column(length = length_255B, name = ColumnNamePrefix + script_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String script;

    public static final String scriptText_FIELDNAME = "scriptText";
    @FieldDescribe("路由脚本文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + scriptText_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String scriptText;

    public static final String validationScript_FIELDNAME = "validationScript";
    @IdReference(Script.class)
    @FieldDescribe("校验脚本")
    @Column(length = length_255B, name = ColumnNamePrefix + validationScript_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String validationScript;

    public static final String validationScriptText_FIELDNAME = "validationScriptText";
    @FieldDescribe("校验脚本文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + validationScriptText_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String validationScriptText;

    public static final String passSameTarget_FIELDNAME = "passSameTarget";
    @FieldDescribe("如何与前一个环节处理人相同那么自动执行.")
    @CheckPersist(allowEmpty = true)
    @Column(name = ColumnNamePrefix + passSameTarget_FIELDNAME)
    private Boolean passSameTarget;

    public static final String passExpired_FIELDNAME = "passExpired";
    @FieldDescribe("超时时候的默认路由.")
    @CheckPersist(allowEmpty = true)
    @Column(name = ColumnNamePrefix + passExpired_FIELDNAME)
    private Boolean passExpired;

    public static final String opinion_FIELDNAME = "opinion";
    @FieldDescribe("默认意见.")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String opinion;

    public static final String decisionOpinion_FIELDNAME = "decisionOpinion";
    @FieldDescribe("决策性意见,使用#分割.")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + decisionOpinion_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String decisionOpinion;

    public static final String SOLE_FIELDNAME = "sole";
    @FieldDescribe("唯一路由,当多人处理时,如果有人选择此路由将通过此路由,后续处理人还是需要处理的,不会一票否决.")
    @CheckPersist(allowEmpty = true)
    @Column(name = ColumnNamePrefix + SOLE_FIELDNAME)
    private Boolean sole;

    public static final String opinionRequired_FIELDNAME = "opinionRequired";
    @FieldDescribe("路由意见是否必填.")
    @CheckPersist(allowEmpty = true)
    @Column(name = ColumnNamePrefix + opinionRequired_FIELDNAME)
    private Boolean opinionRequired;

    public static final String hiddenScript_FIELDNAME = "hiddenScript";
    @IdReference(Script.class)
    @FieldDescribe("路由隐藏脚本.")
    @Column(length = length_255B, name = ColumnNamePrefix + hiddenScript_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String hiddenScript;

    public static final String hiddenScriptText_FIELDNAME = "hiddenScriptText";
    @FieldDescribe("路由隐藏脚本文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + hiddenScriptText_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String hiddenScriptText;

    public static final String displayNameScript_FIELDNAME = "displayNameScript";
    @IdReference(Script.class)
    @FieldDescribe("路由显示名称脚本.")
    @Column(length = length_255B, name = ColumnNamePrefix + displayNameScript_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String displayNameScript;

    public static final String displayNameScriptText_FIELDNAME = "displayNameScriptText";
    @FieldDescribe("路由显示名称脚本文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + displayNameScriptText_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String displayNameScriptText;

    public static final String selectConfig_FIELDNAME = "selectConfig";
    @FieldDescribe("路由选人设置文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + selectConfig_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String selectConfig;

    public static final String type_FIELDNAME = "type";
    @FieldDescribe("路由类型")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + type_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String type;

    public static final String appendTaskIdentityType_FIELDNAME = "appendTaskIdentityType";
    @FieldDescribe("添加待办方式.")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + appendTaskIdentityType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String appendTaskIdentityType;

    public static final String appendTaskIdentityScript_FIELDNAME = "appendTaskIdentityScript";
    @IdReference(Script.class)
    @FieldDescribe("添加待办人脚本.")
    @Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
            + appendTaskIdentityScript_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String appendTaskIdentityScript;

    public static final String appendTaskIdentityScriptText_FIELDNAME = "appendTaskIdentityScriptText";
    @FieldDescribe("添加待办人脚本文本.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_1M, name = ColumnNamePrefix + appendTaskIdentityScriptText_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String appendTaskIdentityScriptText;

    public static final String edition_FIELDNAME = "edition";
    @FieldDescribe("版本编码.")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + edition_FIELDNAME)
    private String edition;

    @FieldDescribe("属性对象存储字段.")
    @Persistent(fetch = FetchType.EAGER)
    @Strategy(JsonPropertiesValueHandler)
    @Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private RouteProperties properties;

    public String getAppendTaskIdentityType() {
        return appendTaskIdentityType;
    }

    public void setAppendTaskIdentityType(String appendTaskIdentityType) {
        this.appendTaskIdentityType = appendTaskIdentityType;
    }

    public String getAppendTaskIdentityScript() {
        return appendTaskIdentityScript;
    }

    public void setAppendTaskIdentityScript(String appendTaskIdentityScript) {
        this.appendTaskIdentityScript = appendTaskIdentityScript;
    }

    public static String getAppendtaskidentityscriptFieldname() {
        return appendTaskIdentityScript_FIELDNAME;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getDescription() {
        return description;
    }

    public String getProcess() {
        return process;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public String getActivity() {
        return activity;
    }

    public String getTrack() {
        return track;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getPosition() {
        return position;
    }

    public String getScript() {
        return script;
    }

    public String getScriptText() {
        return scriptText;
    }

    public Boolean getPassSameTarget() {
        return passSameTarget;
    }

    public Boolean getPassExpired() {
        return passExpired;
    }

    public String getOpinion() {
        return opinion;
    }

    public String getDecisionOpinion() {
        return decisionOpinion;
    }

    public Boolean getSole() {
        return sole;
    }

    public Boolean getOpinionRequired() {
        return opinionRequired;
    }

    public String getHiddenScript() {
        return hiddenScript;
    }

    public String getHiddenScriptText() {
        return hiddenScriptText;
    }

    public String getDisplayNameScript() {
        return displayNameScript;
    }

    public String getDisplayNameScriptText() {
        return displayNameScriptText;
    }

    public String getSelectConfig() {
        return selectConfig;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    public void setPassSameTarget(Boolean passSameTarget) {
        this.passSameTarget = passSameTarget;
    }

    public void setPassExpired(Boolean passExpired) {
        this.passExpired = passExpired;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    public void setDecisionOpinion(String decisionOpinion) {
        this.decisionOpinion = decisionOpinion;
    }

    public void setSole(Boolean sole) {
        this.sole = sole;
    }

    public void setOpinionRequired(Boolean opinionRequired) {
        this.opinionRequired = opinionRequired;
    }

    public void setHiddenScript(String hiddenScript) {
        this.hiddenScript = hiddenScript;
    }

    public void setHiddenScriptText(String hiddenScriptText) {
        this.hiddenScriptText = hiddenScriptText;
    }

    public void setDisplayNameScript(String displayNameScript) {
        this.displayNameScript = displayNameScript;
    }

    public void setDisplayNameScriptText(String displayNameScriptText) {
        this.displayNameScriptText = displayNameScriptText;
    }

    public void setSelectConfig(String selectConfig) {
        this.selectConfig = selectConfig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppendTaskIdentityScriptText() {
        return appendTaskIdentityScriptText;
    }

    public void setAppendTaskIdentityScriptText(String appendTaskIdentityScriptText) {
        this.appendTaskIdentityScriptText = appendTaskIdentityScriptText;
    }

    public String getValidationScript() {
        return validationScript;
    }

    public void setValidationScript(String validationScript) {
        this.validationScript = validationScript;
    }

    public String getValidationScriptText() {
        return validationScriptText;
    }

    public void setValidationScriptText(String validationScriptText) {
        this.validationScriptText = validationScriptText;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

}
