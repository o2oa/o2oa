package com.x.organization.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = PersistenceProperties.Company.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Company extends SliceJpaObject {

	private static final long serialVersionUID = -4115399782853963899L;
	private static final String TABLE = PersistenceProperties.Company.table;

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
	@CheckRemove(citationNotExists = {
			/* 公司已经没有下属部门 */
			@CitationNotExist(type = Department.class, fields = "company"),
			/* 公司已经没有下属公司 */
			@CitationNotExist(type = Company.class, fields = "superior"),
			/* 没有公司属性 */
			@CitationNotExist(type = CompanyAttribute.class, fields = "company"),
			/* 没有公司职务 */
			@CitationNotExist(type = CompanyDuty.class, fields = "company") })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		this.superior = StringUtils.trimToEmpty(this.superior);
		if (StringUtils.isEmpty(this.display)) {
			this.display = this.name;
		}
	}

	/* 更新运行方法 */

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

	@EntityFieldDescribe("名称,不可重名.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "unique", "shortName" }, type = Company.class))
	private String name;

	@EntityFieldDescribe("公司简称。")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xshortName")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "id", "name", "unique", "shortName" }, type = Company.class))
	private String shortName;

	@EntityFieldDescribe("唯一标识.")
	@Column(length = PersistenceProperties.length_unique, name = "xunique")
	@Index(name = TABLE + "_unique")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "id", "name", "unique", "shortName" }, type = Company.class))
	private String unique;

	@EntityFieldDescribe("显示名称,默认为name的值.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xdisplay")
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String display;

	@EntityFieldDescribe("上级公司主键值.")
	@Column(length = JpaObject.length_id, name = "xsuperior")
	@Index(name = TABLE + "_superior")
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Company.class) })
	private String superior;

	@EntityFieldDescribe("公司级别,1为最上层公司.自动计算.")
	@Index(name = TABLE + "_level")
	@Column(name = "xlevel")
	@CheckPersist(allowEmpty = false)
	private Integer level;

	@EntityFieldDescribe("排序编号")
	@Index(name = TABLE + "_orderNumber")
	@Column(name = "xorderNumber")
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber;

	@EntityFieldDescribe("管理者.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Person.class))
	private List<String> controllerList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnique() {
		return unique;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
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

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
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