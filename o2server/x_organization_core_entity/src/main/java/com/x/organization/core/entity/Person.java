package com.x.organization.core.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Person.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Person.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Person extends SliceJpaObject {

	public static final String HIDDENMOBILESYMBOL = "***********";

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.Person.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	@CheckRemove(citationNotExists = {
			/* 角色中没有此人员 */
			@CitationNotExist(type = Role.class, fields = Role.personList_FIELDNAME),
			/* 群组中没有此人员 */
			@CitationNotExist(type = Group.class, fields = Group.personList_FIELDNAME),
			/* 人员的身份为空 */
			@CitationNotExist(type = Identity.class, fields = Identity.person_FIELDNAME),
			/* 不在所有的个人管理员中 */
			@CitationNotExist(type = Person.class, fields = Person.controllerList_FIELDNAME),
			/* 不在所有的组织管理员中 */
			@CitationNotExist(type = Unit.class, fields = Unit.controllerList_FIELDNAME),
			/* 没有人员属性 */
			@CitationNotExist(type = PersonAttribute.class, fields = PersonAttribute.person_FIELDNAME) })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		if (null != this.birthday) {
			this.age = DateUtils.toCalendar(new Date()).get(Calendar.YEAR)
					- DateUtils.toCalendar(this.birthday).get(Calendar.YEAR);
		}
		this.controllerList = StringTools.trimUnique(this.controllerList);
		/** 保证unique不为空 */
		if (StringUtils.isEmpty(this.unique)) {
			this.unique = StringTools.uniqueToken();
		}
		this.distinguishedName = this.name + PersistenceProperties.distinguishNameSplit + this.unique
				+ PersistenceProperties.distinguishNameSplit + PersistenceProperties.Person.distinguishNameCharacter;
		/** 生成默认排序号 */
		if (null == this.orderNumber) {
			this.orderNumber = DateTools.timeOrderNumber();
		}
		// this.signature =
		// StringEscapeUtils.escapeHtml4(Objects.toString(this.signature, ""));
	}

	/* 更新运行方法 */

	/* Entity 默认字段结束 */

	public static final String genderType_FIELDNAME = "genderType";
	@FieldDescribe("性别.男:m,女:f,未知:d")
	@Enumerated(EnumType.STRING)
	@Column(length = GenderType.length, name = ColumnNamePrefix + genderType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + genderType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private GenderType genderType;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	private String icon;

	public static final String icon_mdpi_FIELDNAME = "iconMdpi";
	@FieldDescribe("icon Base64编码后的文本（中等尺寸图像）.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_mdpi_FIELDNAME)
	private String iconMdpi;

	public static final String icon_ldpi_FIELDNAME = "iconLdpi";
	@FieldDescribe("icon Base64编码后的文本（小尺寸图像）.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_ldpi_FIELDNAME)
	private String iconLdpi;

	public static final String signature_FIELDNAME = "signature";
	@FieldDescribe("个性签名.")
	@Column(length = length_255B, name = ColumnNamePrefix + signature_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String signature;

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
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String name;

	public static final String employee_FIELDNAME = "employee";
	@Flag
	@FieldDescribe("工号,不可重复.")
	@Column(length = length_255B, name = ColumnNamePrefix + employee_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + employee_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(fields = employee_FIELDNAME, type = Person.class))
	private String employee;

	public static final String unique_FIELDNAME = "unique";
	@Flag
	@FieldDescribe("唯一标识,不可重复,为空则使用自动填充值")
	@Column(length = length_255B, name = ColumnNamePrefix + unique_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unique_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists = @CitationNotExist(fields = unique_FIELDNAME, type = Person.class))
	private String unique;

	public static final String distinguishedName_FIELDNAME = "distinguishedName";
	@Flag
	@FieldDescribe("识别名,自动填充,@P结尾.")
	@Column(length = length_255B, name = ColumnNamePrefix + distinguishedName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + distinguishedName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("排序号,升序排列,为空在最后")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("个人管理者.默认为创建者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + controllerList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + controllerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Person.class))
	private List<String> controllerList;

	public static final String superior_FIELDNAME = "superior";
	@FieldDescribe("汇报对象.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + superior_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + superior_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Person.class) })
	private String superior;

	public static final String password_FIELDNAME = "password";
	@FieldDescribe("密码.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + password_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String password;

	public static final String passwordExpiredTime_FIELDNAME = "passwordExpiredTime";
	@FieldDescribe("用户密码到期时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + passwordExpiredTime_FIELDNAME)
	private Date passwordExpiredTime;

	public static final String changePasswordTime_FIELDNAME = "changePasswordTime";
	@FieldDescribe("用户密码最后修改时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + changePasswordTime_FIELDNAME)
	private Date changePasswordTime;

	public static final String lastLoginTime_FIELDNAME = "lastLoginTime";
	@FieldDescribe("最后登录时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastLoginTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastLoginTime_FIELDNAME)
	private Date lastLoginTime;

	public static final String lastLoginAddress_FIELDNAME = "lastLoginAddress";
	@FieldDescribe("最后登录地址.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + lastLoginAddress_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastLoginAddress_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String lastLoginAddress;

	public static final String lastLoginClient_FIELDNAME = "lastLoginClient";
	@FieldDescribe("最后登录客户端类型,web,android或者ios.")
	@Index(name = TABLE + IndexNameMiddle + lastLoginClient_FIELDNAME)
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + lastLoginClient_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String lastLoginClient;

	public static final String mail_FIELDNAME = "mail";
	@Flag
	@FieldDescribe("邮件地址.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + mail_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mail_FIELDNAME)
	@CheckPersist(allowEmpty = true, mailString = true, citationNotExists = @CitationNotExist(fields = mail_FIELDNAME, type = Person.class))
	private String mail;

	public static final String weixin_FIELDNAME = "weixin";
	@FieldDescribe("微信号.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + weixin_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationNotExists = @CitationNotExist(fields = weixin_FIELDNAME, type = Person.class))
	@Index(name = TABLE + IndexNameMiddle + weixin_FIELDNAME)
	private String weixin;

	public static final String qq_FIELDNAME = "qq";
	@FieldDescribe("QQ号.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + qq_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationNotExists = @CitationNotExist(fields = qq_FIELDNAME, type = Person.class))
	@Index(name = TABLE + IndexNameMiddle + qq_FIELDNAME)
	private String qq;

	public static final String mobile_FIELDNAME = "mobile";
	@Flag
	@FieldDescribe("必填,手机号.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + mobile_FIELDNAME)
	/** 其他地区手机号不一致,所以这里使用外部校验,不使用mobileString */
	@Index(name = TABLE + IndexNameMiddle + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists = @CitationNotExist(fields = mobile_FIELDNAME, type = Person.class))
	private String mobile;

	public static final String hiddenMobile_FIELDNAME = "hiddenMobile";
	@FieldDescribe("是否隐藏手机号.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + hiddenMobile_FIELDNAME)
	private Boolean hiddenMobile;

	public static final String officePhone_FIELDNAME = "officePhone";
	@FieldDescribe("办公电话.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + officePhone_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String officePhone;

	public static final String boardDate_FIELDNAME = "boardDate";
	@FieldDescribe("入职时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + boardDate_FIELDNAME)
	private Date boardDate;

	public static final String birthday_FIELDNAME = "birthday";
	@FieldDescribe("生日.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + birthday_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + birthday_FIELDNAME)
	private Date birthday;

	public static final String age_FIELDNAME = "age";
	@FieldDescribe("年龄")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + age_FIELDNAME)
	private Integer age;

	public static final String dingdingId_FIELDNAME = "dingdingId";
	@FieldDescribe("钉钉人员ID.")
	@Column(length = length_255B, name = ColumnNamePrefix + dingdingId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dingdingId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dingdingId;

	public static final String dingdingHash_FIELDNAME = "dingdingHash";
	@FieldDescribe("钉钉人员哈希特征.")
	@Column(length = length_255B, name = ColumnNamePrefix + dingdingHash_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dingdingHash_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String dingdingHash;

	public static final String zhengwuDingdingId_FIELDNAME = "zhengwuDingdingId";
	@FieldDescribe("政务钉钉人员ID.")
	@Column(length = length_255B, name = ColumnNamePrefix + zhengwuDingdingId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zhengwuDingdingId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String zhengwuDingdingId;

	public static final String zhengwuDingdingHash_FIELDNAME = "zhengwuDingdingHash";
	@FieldDescribe("政务钉钉人员哈希特征.")
	@Column(length = length_255B, name = ColumnNamePrefix + zhengwuDingdingHash_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + zhengwuDingdingHash_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String zhengwuDingdingHash;

	public static final String qiyeweixinId_FIELDNAME = "qiyeweixinId";
	@FieldDescribe("企业微信人员ID.")
	@Column(length = length_255B, name = ColumnNamePrefix + qiyeweixinId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + qiyeweixinId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String qiyeweixinId;

	public static final String qiyeweixinHash_FIELDNAME = "qiyeweixinHash";
	@FieldDescribe("企业微信人员哈希特征.")
	@Column(length = length_255B, name = ColumnNamePrefix + qiyeweixinHash_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + qiyeweixinHash_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String qiyeweixinHash;

	public static final String open1Id_FIELDNAME = "open1Id";
	@Flag
	@FieldDescribe("oauth登录id1.")
	@Column(length = length_255B, name = ColumnNamePrefix + open1Id_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + open1Id_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String open1Id;

	public static final String open2Id_FIELDNAME = "open2Id";
	@Flag
	@FieldDescribe("oauth登录id2.")
	@Column(length = length_255B, name = ColumnNamePrefix + open2Id_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + open2Id_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String open2Id;

	public static final String open3Id_FIELDNAME = "open3Id";
	@Flag
	@FieldDescribe("oauth登录id3.")
	@Column(length = length_255B, name = ColumnNamePrefix + open3Id_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + open3Id_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String open3Id;

	public static final String open4Id_FIELDNAME = "open4Id";
	@Flag
	@FieldDescribe("oauth登录id4.")
	@Column(length = length_255B, name = ColumnNamePrefix + open4Id_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + open4Id_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String open4Id;

	public static final String open5Id_FIELDNAME = "open5Id";
	@Flag
	@FieldDescribe("oauth登录id5.")
	@Column(length = length_255B, name = ColumnNamePrefix + open5Id_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + open5Id_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String open5Id;

	public static final String failureTime_FIELDNAME = "failureTime";
	@FieldDescribe("登录失败记录时间.")
	@Temporal(TemporalType.TIMESTAMP)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + failureTime_FIELDNAME)
	private Date failureTime;

	public static final String failureCount_FIELDNAME = "failureCount";
	@FieldDescribe("登录失败次数")
	@Column(name = ColumnNamePrefix + failureCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer failureCount;
	/* flag标志位 */

	public static final String topUnitList_FIELDNAME = "topUnitList";
	@FieldDescribe("所属顶层组织.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + topUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + topUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + topUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + topUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Unit.class))
	private List<String> topUnitList;

	public void setLastLoginAddress(String lastLoginAddress) {
		this.lastLoginAddress = StringTools.utf8SubString(this.lastLoginAddress, Person.length_64B);
	}

	public List<String> getTopUnitList() {
		return topUnitList;
	}

	public void setTopUnitList(List<String> topUnitList) {
		this.topUnitList = topUnitList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconMdpi() {
		return iconMdpi;
	}

	public void setIconMdpi(String iconMdpi) {
		this.iconMdpi = iconMdpi;
	}

	public String getIconLdpi() {
		return iconLdpi;
	}

	public void setIconLdpi(String iconLdpi) {
		this.iconLdpi = iconLdpi;
	}

	public GenderType getGenderType() {
		return genderType;
	}

	public void setGenderType(GenderType genderType) {
		this.genderType = genderType;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public Date getBoardDate() {
		return boardDate;
	}

	public void setBoardDate(Date boardDate) {
		this.boardDate = boardDate;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getPasswordExpiredTime() {
		return passwordExpiredTime;
	}

	public void setPasswordExpiredTime(Date passwordExpiredTime) {
		this.passwordExpiredTime = passwordExpiredTime;
	}

	public Date getChangePasswordTime() {
		return changePasswordTime;
	}

	public void setChangePasswordTime(Date changePasswordTime) {
		this.changePasswordTime = changePasswordTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public String getLastLoginAddress() {
		return lastLoginAddress;
	}

	public String getLastLoginClient() {
		return lastLoginClient;
	}

	public void setLastLoginClient(String lastLoginClient) {
		this.lastLoginClient = lastLoginClient;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDingdingId() {
		return dingdingId;
	}

	public void setDingdingId(String dingdingId) {
		this.dingdingId = dingdingId;
	}

	public String getDingdingHash() {
		return dingdingHash;
	}

	public void setDingdingHash(String dingdingHash) {
		this.dingdingHash = dingdingHash;
	}

	public String getQiyeweixinId() {
		return qiyeweixinId;
	}

	public void setQiyeweixinId(String qiyeweixinId) {
		this.qiyeweixinId = qiyeweixinId;
	}

	public String getQiyeweixinHash() {
		return qiyeweixinHash;
	}

	public void setQiyeweixinHash(String qiyeweixinHash) {
		this.qiyeweixinHash = qiyeweixinHash;
	}

	public String getZhengwuDingdingId() {
		return zhengwuDingdingId;
	}

	public void setZhengwuDingdingId(String zhengwuDingdingId) {
		this.zhengwuDingdingId = zhengwuDingdingId;
	}

	public String getZhengwuDingdingHash() {
		return zhengwuDingdingHash;
	}

	public void setZhengwuDingdingHash(String zhengwuDingdingHash) {
		this.zhengwuDingdingHash = zhengwuDingdingHash;
	}

	public String getOpen1Id() {
		return open1Id;
	}

	public void setOpen1Id(String open1Id) {
		this.open1Id = open1Id;
	}

	public String getOpen2Id() {
		return open2Id;
	}

	public void setOpen2Id(String open2Id) {
		this.open2Id = open2Id;
	}

	public String getOpen3Id() {
		return open3Id;
	}

	public void setOpen3Id(String open3Id) {
		this.open3Id = open3Id;
	}

	public String getOpen4Id() {
		return open4Id;
	}

	public void setOpen4Id(String open4Id) {
		this.open4Id = open4Id;
	}

	public String getOpen5Id() {
		return open5Id;
	}

	public void setOpen5Id(String open5Id) {
		this.open5Id = open5Id;
	}

	public Date getFailureTime() {
		return failureTime;
	}

	public void setFailureTime(Date failureTime) {
		this.failureTime = failureTime;
	}

	public Integer getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(Integer failureCount) {
		this.failureCount = failureCount;
	}

	public Boolean getHiddenMobile() {
		return hiddenMobile;
	}

	public void setHiddenMobile(Boolean hiddenMobile) {
		this.hiddenMobile = hiddenMobile;
	}

}