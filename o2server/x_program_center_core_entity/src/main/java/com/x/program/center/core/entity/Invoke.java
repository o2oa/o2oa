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
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author sword
 * 服务管理接口
 */
@Schema(name = "Invoke", description = "服务管理接口.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Invoke.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Invoke.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Invoke extends SliceJpaObject {

	private static final long serialVersionUID = 8877822163007579542L;
	private static final String TABLE = PersistenceProperties.Invoke.TABLE;

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
	public void onPersist() throws Exception {

	}

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	private String name;

	public static final String enableToken_FIELDNAME = "enableToken";
	@FieldDescribe("是否启用token验证.")
	@Column(name = ColumnNamePrefix + enableToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean enableToken = false;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("别名.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	// 检查在同一应用下不能重名
	@CitationNotExist(fields = { "name", "id", "alias" }, type = Invoke.class))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String validated_FIELDNAME = "validated";
	@FieldDescribe("代码格式是否正确.")
	@Column(name = ColumnNamePrefix + validated_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean validated;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("是否启用.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public static final String text_FIELDNAME = "text";
	@FieldDescribe("脚本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + text_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String text;

	public static final String lastStartTime_FIELDNAME = "lastStartTime";
	@FieldDescribe("最近开始时间.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastStartTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastStartTime_FIELDNAME)
	private Date lastStartTime;

	public static final String lastEndTime_FIELDNAME = "lastEndTime";
	@FieldDescribe("最近结束时间.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastEndTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastEndTime_FIELDNAME)
	private Date lastEndTime;

	public static final String remoteAddrRegex_FIELDNAME = "remoteAddrRegex";
	@FieldDescribe("访问地址的表达式.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + remoteAddrRegex_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	@Index(name = TABLE + IndexNameMiddle + remoteAddrRegex_FIELDNAME)
	private String remoteAddrRegex;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("自定义字段.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

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

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public Date getLastEndTime() {
		return lastEndTime;
	}

	public void setLastEndTime(Date lastEndTime) {
		this.lastEndTime = lastEndTime;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getRemoteAddrRegex() {
		return remoteAddrRegex;
	}

	public void setRemoteAddrRegex(String remoteAddrRegex) {
		this.remoteAddrRegex = remoteAddrRegex;
	}

	public Boolean getEnableToken() {
		return enableToken;
	}

	public void setEnableToken(Boolean enableToken) {
		this.enableToken = enableToken;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
