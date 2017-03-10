package com.x.organization.core.entity;

import java.util.ArrayList;
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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Role.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Role extends SliceJpaObject {

	private static final long serialVersionUID = 781968851053681627L;
	private static final String TABLE = PersistenceProperties.Role.table;

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
	public void preUpdate() throws Exception{
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
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		if (StringUtils.isEmpty(this.display)) {
			this.display = this.name;
		}
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("name拼音.")
	@Index(name = TABLE + "_pinyin")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyin")
	private String pinyin;

	@EntityFieldDescribe("name拼音首字母.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyinInitial")
	@Index(name = TABLE + "_pinyinInitial")
	private String pinyinInitial;

	@EntityFieldDescribe("名称,不可重名.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "unique" }, type = Role.class) )
	private String name;

	@EntityFieldDescribe("唯一标识.")
	@Column(length = PersistenceProperties.length_unique, name = "xunique")
	@Index(name = TABLE + "_unique")
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "name", "id", "unique" }, type = Role.class) )
	private String unique;

	@CheckPersist(allowEmpty = true, simplyString = true)
	@EntityFieldDescribe("显示名称,默认为name的值.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xdisplay")
	private String display;

	@EntityFieldDescribe("排序号.")
	@Index(name = TABLE + "_orderNumber")
	@Column(name = "xorderNumber")
	private Integer orderNumber;

	@EntityFieldDescribe("角色个人成员,多值,存储 Person ID.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_personList", joinIndex = @Index(name = TABLE + "_personList_join") )
	@ElementIndex(name = TABLE + "_personList_element")
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Person.class) })
	@ElementColumn(length = JpaObject.length_id, name = "xpersonList")
	@OrderColumn(name = PersistenceProperties.orderColumn)
	private List<String> personList = new ArrayList<String>();

	@EntityFieldDescribe("角色群组成员,多值,存储 Group ID.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + "_groupList", joinIndex = @Index(name = TABLE + "_groupList_join") )
	@ElementIndex(name = TABLE + "_groupList_element")
	@ElementColumn(length = JpaObject.length_id, name = "xgroupList")
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Group.class) })
	private List<String> groupList = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPersonList() {
		return personList;
	}

	public void setPersonList(List<String> personList) {
		this.personList = personList;
	}

	public List<String> getGroupList() {
		return groupList;
	}

	public void setGroupList(List<String> groupList) {
		this.groupList = groupList;
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

}