package com.x.strategydeploy.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Strategy_Attachment.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Strategy_Attachment.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.strategyDeploy)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = 4536021692181719176L;
	private static final String TABLE = PersistenceProperties.Strategy_Attachment.table;

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
	}

	@FieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@FieldDescribe("关联的存储名称.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + "_storage")
	private String storage;

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	public Attachment() {

	}

	public Attachment(KeyworkInfo keywork, String person, String site) {
		this.setWorkyear(keywork.getKeyworkyear());
		this.setPerson(person);
		this.setSite(site);
	}

	public Attachment(StrategyDeploy strategydeploy, String person, String site) {
		this.setWorkyear(strategydeploy.getStrategydeployyear());
		this.setPerson(person);
		this.setSite(site);
	}

	public Attachment(MeasuresInfo measuresinfo, String person, String site) {
		this.setWorkyear(measuresinfo.getMeasuresinfoyear());
		this.setPerson(person);
		this.setSite(site);
	}

	@Override
	public String path() throws Exception {
		if (null == this.site) {
			throw new Exception("site can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		if (null == this.workyear || this.workyear.isEmpty()) {
			throw new Exception("workyear can not blank");
		}

		// String str = DateTools.format(this.createTime,
		// DateTools.formatCompact_yyyyMMdd);
		String str = workyear;
		str += PATHSEPARATOR;
		str += this.site;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求。其中文件“文件真实名称，文件大小，扩展名”3项，是必要的业务字段。
	 * =========================================================================
	 * =========
	 */

	@FieldDescribe("文件真实名称")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String name;

	@FieldDescribe("文件大小.")
	@Column(name = "xlength")
	@Index(name = TABLE + "_length")
	@CheckPersist(allowEmpty = true)
	private Long length;

	@FieldDescribe("扩展名")
	@Column(name = "xextension", length = JpaObject.length_16B)
	@CheckPersist(fileNameString = true, allowEmpty = true)
	private String extension;

	@FieldDescribe("文件所有者")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = false)
	private String person;

	@FieldDescribe("附件框分类.战略部署的类型：1公司战略，2举措，3部门五项重点工作")
	@Column(name = "xsite", length = JpaObject.length_64B)
	@CheckPersist(allowEmpty = true)
	private String site;

	@FieldDescribe("关联的keywork创建年份，用于分类目录。")
	@Column(name = "xworkyear", length = JpaObject.length_16B)
	@CheckPersist(allowEmpty = false)
	private String workyear;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getWorkyear() {
		return workyear;
	}

	public void setWorkyear(String workyear) {
		this.workyear = workyear;
	}

}