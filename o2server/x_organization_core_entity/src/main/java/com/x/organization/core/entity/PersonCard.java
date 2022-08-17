package com.x.organization.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "PersonCard", description = "组织人员名片.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.PersonCard.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.PersonCard.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonCard extends SliceJpaObject {

	public static final String HIDDENMOBILESYMBOL = "***********";

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.PersonCard.table;

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
		/** 生成默认排序号 */
		if (null == this.orderNumber) {
			this.orderNumber = DateTools.timeOrderNumber();
		}
		/*if (null == this.groupType || "" == this.groupType) {
			this.groupType = "默认分组";
		}*/
	}

	/* 更新运行方法 */

	/* Entity 默认字段结束 */
	
	public static final String name_FIELDNAME = "name";
	@FieldDescribe("名称,可重名.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String name;

	public static final String genderType_FIELDNAME = "genderType";
	@FieldDescribe("性别.男:m,女:f,未知:d")
	@Enumerated(EnumType.STRING)
	@Column(length = GenderType.length, name = ColumnNamePrefix + genderType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + genderType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private GenderType genderType;
	
	public static final String groupType_FIELDNAME = "groupType";
	@FieldDescribe("所属分组.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + groupType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + groupType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String groupType;


	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name拼音,自动生成")
	@Column(length = length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name拼音首字母,自动生成")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;
	
	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@Flag
	@FieldDescribe("录入人,@P结尾.")
	@Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber;

	public static final String mobile_FIELDNAME = "mobile";
	@Flag
	@FieldDescribe("必填,手机号.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + mobile_FIELDNAME)
	/** 其他地区手机号不一致,所以这里使用外部校验,不使用mobileString */
	@Index(name = TABLE + IndexNameMiddle + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobile;

	public static final String officePhone_FIELDNAME = "officePhone";
	@FieldDescribe("办公电话.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + officePhone_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String officePhone;
	
	public static final String address_FIELDNAME = "address";
	@FieldDescribe("地址.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + address_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + address_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String address;
	
	public static final String description_FIELDNAME = "description";
	@FieldDescribe("备注.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;
	
	public static final String extend1_FIELDNAME = "extend1";
	@Flag
	@FieldDescribe("extend1扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend1_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend1;

	public static final String extend2_FIELDNAME = "extend2";
	@Flag
	@FieldDescribe("extend2扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend2_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend2;
	
	public static final String extend3_FIELDNAME = "extend3";
	@Flag
	@FieldDescribe("extend3扩展字段.")
	@Column(length = length_255B, name = ColumnNamePrefix + extend3_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + extend3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extend3;

	public static final String inputTime_FIELDNAME = "inputTime";
	@FieldDescribe("录入时间.")
	@Index(name = TABLE + IndexNameMiddle + inputTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Column(length = length_255B, name = ColumnNamePrefix + inputTime_FIELDNAME)
	private String inputTime;
	
	public static final String status_FIELDNAME = "status";
	@FieldDescribe("状态.草稿,发布")
	@Column(length = length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
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
	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}	

	public String getExtend1() {
		return extend1;
	}

	public void setExtend1(String extend1) {
		this.extend1 = extend1;
	}
	public String getExtend2() {
		return extend2;
	}
	public void setExtend2(String extend2) {
		this.extend2 = extend2;
	}
	public String getExtend3() {
		return extend3;
	}
	public void setExtend3(String extend3) {
		this.extend3 = extend3;
	}
	

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}