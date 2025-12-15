package com.x.ai.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

/**
 * AI模型配置
 * @author sword
 */
@Schema(name = "AiModel", description = "AI模型配置.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AiModel.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AiModel.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AiModel extends SliceJpaObject {

	private static final long serialVersionUID = 5424940251448214931L;

	private static final String TABLE = PersistenceProperties.AiModel.table;
	public static final String TYPE_DEEP_SEEK = "deepSeek";
	public static final String TYPE_ALI  = "ali";

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

	@Override
	public void onPersist() {
		if(asDefault == null){
			asDefault = false;
		}
		if(enable == null){
			enable = true;
		}
		if(StringUtils.isEmpty(name)){
			this.name = this.type + ":" + this.model;
		}
	}

	@PostLoad
	public void postLoad() {
		this.createDateTime = this.getCreateTime();
		this.updateDateTime = this.getUpdateTime();
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("模型名称.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME, unique = true)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("模型服务商：ali(阿里云)|deepSeek|openAi(本地模型).")
	@Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String model_FIELDNAME = "model";
	@FieldDescribe("模型版本.")
	@Column(length = length_255B, name = ColumnNamePrefix + model_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String model;

	public static final String completionUrl_FIELDNAME = "completionUrl";
	@FieldDescribe("模型服务地址.")
	@Column(length = 500, name = ColumnNamePrefix + completionUrl_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String completionUrl;

	public static final String apiKey_FIELDNAME = "apiKey";
	@FieldDescribe("模型秘钥.")
	@Column(length = length_255B, name = ColumnNamePrefix + apiKey_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String apiKey;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean enable;

	public static final String asDefault_FIELDNAME = "asDefault";
	@FieldDescribe("是否为默认服务模型.")
	@Column(name = ColumnNamePrefix + asDefault_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean asDefault;

	public static final String desc_FIELDNAME = "desc";
	@FieldDescribe("模型描述.")
	@Column(length = 500, name = ColumnNamePrefix + desc_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String desc;

	public static final String proxyEnable_FIELDNAME = "proxyEnable";
	@FieldDescribe("是否启用代理.")
	@Column(name = ColumnNamePrefix + proxyEnable_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean proxyEnable;

	public static final String proxyHost_FIELDNAME = "proxyHost";
	@FieldDescribe("代理主机.")
	@Column(length = length_255B, name = ColumnNamePrefix + proxyHost_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String proxyHost;

	public static final String proxyPort_FIELDNAME = "proxyPort";
	@FieldDescribe("代理端口.")
	@Column(name = ColumnNamePrefix + proxyPort_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer proxyPort;

	public static final String proxyUser_FIELDNAME = "proxyUser";
	@FieldDescribe("代理认证用户.")
	@Column(length = length_255B, name = ColumnNamePrefix + proxyUser_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String proxyUser;

	public static final String proxyPass_FIELDNAME = "proxyPass";
	@FieldDescribe("代理认证密码.")
	@Column(length = length_255B, name = ColumnNamePrefix + proxyPass_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String proxyPass;

	@FieldDescribe("创建时间.")
	@Transient
	private Date createDateTime;

	@FieldDescribe("修改时间.")
	@Transient
	private Date updateDateTime;

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Date getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(Date updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getCompletionUrl() {
		return completionUrl;
	}

	public void setCompletionUrl(String completionUrl) {
		this.completionUrl = completionUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public Boolean getAsDefault() {
		return asDefault;
	}

	public void setAsDefault(Boolean asDefault) {
		this.asDefault = asDefault;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Boolean getProxyEnable() {
		return proxyEnable;
	}

	public void setProxyEnable(Boolean proxyEnable) {
		this.proxyEnable = proxyEnable;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public Integer getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUser() {
		return proxyUser;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public String getProxyPass() {
		return proxyPass;
	}

	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}
}
