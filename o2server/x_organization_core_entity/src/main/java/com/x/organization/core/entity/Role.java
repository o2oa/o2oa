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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Role.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Role.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Role extends SliceJpaObject {

	private static final long serialVersionUID = 781968851053681627L;
	private static final String TABLE = PersistenceProperties.Role.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		this.unique = StringUtils.isBlank(this.unique) ? id : unique;
		this.distinguishedName = this.name + PersistenceProperties.distinguishNameSplit + this.unique
				+ PersistenceProperties.distinguishNameSplit + PersistenceProperties.Role.distinguishNameCharacter;
		/** 生成默认排序号 */
		if (null == this.orderNumber) {
			this.orderNumber = DateTools.timeOrderNumber();
		}
	}

	/** 更新运行方法 */

	// public static String[] FLA GS = new String[] { "id", "unique",
	// "distinguishedName" };

	/** flag标志位 */
	/** 默认内容结束 */

	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name拼音,自动生成")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name拼音首字母,自动生成")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	private String pinyinInitial;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("描述.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称,可重名.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	public static final String unique_FIELDNAME = "unique";
	@Flag
	@FieldDescribe("唯一标识,不可重复,为空则使用自动填充值")
	@Column(length = length_255B, name = ColumnNamePrefix + unique_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unique_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = unique_FIELDNAME, type = Role.class))
	private String unique;

	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@Flag
	@FieldDescribe("识别名.")
	@Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String personList_FIELDNAME = "personList";
	@FieldDescribe("角色个人成员,多值,存储 Person ID.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + personList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + personList_FIELDNAME + JoinIndexNameSuffix))
	@ElementIndex(name = TABLE + IndexNameMiddle + personList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Person.class) })
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + personList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	private List<String> personList = new ArrayList<String>();

	public static final String groupList_FIELDNAME = "groupList";
	@FieldDescribe("角色群组成员,多值,存储 Group ID.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + groupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + groupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementIndex(name = TABLE + IndexNameMiddle + groupList_FIELDNAME + ElementIndexNameSuffix)
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + groupList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
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

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}