package com.x.organization.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Department.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Department extends SliceJpaObject {

	private static final long serialVersionUID = 2582551859401812729L;
	private static final String TABLE = PersistenceProperties.Department.table;

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

	@EntityFieldDescribe("ID,数据库主键.")
	@Id
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	@CheckRemove(citationNotExists = {
			/* 部门没有下级部门 */
			@CitationNotExist(type = Department.class, fields = "superior"),
			/* 部门没有身份成员 */
			@CitationNotExist(type = Identity.class, fields = "department"),
			/* 部门没有角色 */
			@CitationNotExist(type = DepartmentAttribute.class, fields = "department"),
			/* 部门没有职务 */
			@CitationNotExist(type = DepartmentDuty.class, fields = "department") })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception {
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		this.superior = StringUtils.trimToEmpty(this.superior);
		if (StringUtils.isEmpty(this.display)) {
			this.display = this.name;
		}
	}

	@EntityFieldDescribe("名称,不可重名.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "unique" }, type = Department.class))
	private String name;

	@EntityFieldDescribe("唯一标识.")
	@Column(length = PersistenceProperties.length_unique, name = "xunique")
	@Index(name = TABLE + "_unique")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id", "unique" }, type = Department.class))
	private String unique;

	@EntityFieldDescribe("name拼音.")
	@Index(name = TABLE + "_pinyin")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyin")
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	@EntityFieldDescribe("name拼音首字母.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyinInitial")
	@Index(name = TABLE + "_pinyinInitial")
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	@CheckPersist(allowEmpty = true, simplyString = true)
	@EntityFieldDescribe("显示名称,默认为name的值.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xdisplay")
	private String display;

	@CheckPersist(allowEmpty = true, simplyString = true)
	@EntityFieldDescribe("部门简称。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xshortName")
	private String shortName;

	@EntityFieldDescribe("部门所属的公司 ID,不可为空.")
	@Column(length = JpaObject.length_id, name = "xcompany")
	@Index(name = TABLE + "_company")
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Company.class) })
	private String company;

	@EntityFieldDescribe("部门级别,1为最上层部门.自动计算.")
	@Index(name = TABLE + "_level")
	@Column(name = "xlevel")
	@CheckPersist(allowEmpty = false)
	private Integer level;

	@EntityFieldDescribe("上级部门ID.")
	@Column(length = JpaObject.length_id, name = "xsuperior")
	@Index(name = TABLE + "_superior")
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Department.class) })
	private String superior;

	@EntityFieldDescribe("排序编号")
	@Index(name = TABLE + "_orderNumber")
	@Column(name = "xorderNumber")
	private Integer orderNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

}