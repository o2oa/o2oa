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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Person.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Person extends SliceJpaObject {

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.Person.table;

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

	@EntityFieldDescribe("ID,数据库主键.")
	@Id
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	@CheckRemove(citationNotExists = {
			/* 角色中没有此人员 */
			@CitationNotExist(type = Role.class, fields = "personList"),
			/* 群组中没有此人员 */
			@CitationNotExist(type = Group.class, fields = "personList"),
			/* 人员的身份为空 */
			@CitationNotExist(type = Identity.class, fields = "person"),
			/* 不在所有的个人管理员中 */
			@CitationNotExist(type = Person.class, fields = "controllerList"),
			/* 不在所有的公司管理员中 */
			@CitationNotExist(type = Company.class, fields = "controllerList"),
			/* 没有人员属性 */
			@CitationNotExist(type = PersonAttribute.class, fields = "person") })
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(name));
		if (StringUtils.isEmpty(this.display)) {
			this.display = this.name;
		}
		if (null != this.birthday) {
			this.age = DateUtils.toCalendar(new Date()).get(Calendar.YEAR)
					- DateUtils.toCalendar(this.birthday).get(Calendar.YEAR);
		}
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("性别.")
	@Enumerated(EnumType.STRING)
	@Column(length = GenderType.length, name = "xgenderType")
	@Index(name = TABLE + "_genderType")
	@CheckPersist(allowEmpty = false)
	private GenderType genderType;

	@EntityFieldDescribe("icon Base64编码后的文本.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = "xicon")
	private String icon;

	@EntityFieldDescribe("个性签名.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xsignature")
	@CheckPersist(allowEmpty = true)
	private String signature;

	@EntityFieldDescribe("name拼音,自动翻译,用于快速搜索")
	@Index(name = TABLE + "_pinyin")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyin")
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	@EntityFieldDescribe("name拼音首字母.自动翻译,用于快速搜索")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xpinyinInitial")
	@Index(name = TABLE + "_pinyinInitial")
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	@EntityFieldDescribe("名称,不可重名.不能与id,unique,employee重复.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "id", "name", "unique", "employee", "mobile", "mail" }, type = Person.class))
	private String name;

	@EntityFieldDescribe("工号,不为空,不重复.不能与id,name,unique重复")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xemployee")
	@Index(name = TABLE + "_employee")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "id", "name", "unique", "employee", "mobile", "mail" }, type = Person.class))
	private String employee;

	@EntityFieldDescribe("唯一标识.可以为空但不能重名,不能与id,name,employee重复.")
	@Column(length = PersistenceProperties.length_unique, name = "xunique")
	@Index(name = TABLE + "_unique")
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 验证不可重名 */
	@CitationNotExist(fields = { "id", "name", "unique", "employee", "mobile", "mail" }, type = Person.class))
	private String unique;

	@EntityFieldDescribe("显示名称,默认为name的值.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xdisplay")
	@CheckPersist(allowEmpty = true, simplyString = true)
	private String display;

	@EntityFieldDescribe("排序号.")
	@Index(name = TABLE + "_orderNumber")
	@Column(name = "xorderNumber")
	private Integer orderNumber;

	@EntityFieldDescribe("个人管理者.默认为创建者。")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_controllerList", joinIndex = @Index(name = TABLE + "_controllerList_join"))
	@ElementColumn(length = JpaObject.length_id, name = "xcontrollerList")
	@ElementIndex(name = TABLE + "_controllerList_element")
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Person.class))
	private List<String> controllerList;

	@EntityFieldDescribe("密码.")
	@Column(length = JpaObject.length_255B, name = "xpassword")
	@CheckPersist(allowEmpty = true)
	private String password;

	@EntityFieldDescribe("用户密码到期时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = "xpasswordExpiredTime")
	private Date passwordExpiredTime;

	@EntityFieldDescribe("用户密码到期时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = "xchangePasswordTime")
	private Date changePasswordTime;

	@EntityFieldDescribe("最后登录时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = "xlastLoginTime")
	@Index(name = TABLE + "_lastLoginTime")
	private Date lastLoginTime;

	@EntityFieldDescribe("邮件地址.")
	@Column(length = JpaObject.length_64B, name = "xmail")
	@Index(name = TABLE + "_mail")
	@CheckPersist(allowEmpty = true, citationNotExists = @CitationNotExist(fields = { "id", "name", "unique",
			"employee", "mobile", "mail" }, type = Person.class))
	private String mail;

	@EntityFieldDescribe("微信号.")
	@Column(length = JpaObject.length_64B, name = "xweixin")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + "_weixin")
	private String weixin;

	@EntityFieldDescribe("QQ号.")
	@Column(length = JpaObject.length_64B, name = "xqq")
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + "_qq")
	private String qq;

	@EntityFieldDescribe("手机号.")
	@Column(length = JpaObject.length_32B, name = "xmobile")
	@CheckPersist(allowEmpty = true, citationNotExists = @CitationNotExist(fields = { "id", "name", "unique",
			"employee", "mobile", "mail" }, type = Person.class))
	@Index(name = TABLE + "_mobile")
	private String mobile;

	@EntityFieldDescribe("办公电话.")
	@Column(length = JpaObject.length_32B, name = "xofficePhone")
	@CheckPersist(allowEmpty = true)
	private String officePhone;

	@EntityFieldDescribe("入职时间.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = "xboardDate")
	private Date boardDate;

	@EntityFieldDescribe("生日.")
	@Temporal(TemporalType.DATE)
	@CheckPersist(allowEmpty = true)
	@Column(name = "xbirthday")
	private Date birthday;

	@EntityFieldDescribe("年龄")
	@CheckPersist(allowEmpty = true)
	@Column(name = "xage")
	private Integer age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
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

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
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

}