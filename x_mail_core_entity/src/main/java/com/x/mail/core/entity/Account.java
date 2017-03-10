package com.x.mail.core.entity;

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

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Account.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Account extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Account.table;

	/**
	 * 获取应用ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置应用ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取信息创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置信息创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 获取信息更新时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 设置信息更新时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 获取信息记录排序号
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * 设置信息记录排序号
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(name = "xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe("创建时间,自动生成.")
	@Index(name = TABLE + "_createTime")
	@Column(name = "xcreateTime")
	private Date createTime;

	@EntityFieldDescribe("修改时间,自动生成.")
	@Index(name = TABLE + "_updateTime")
	@Column(name = "xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe("列表序号, 由创建时间以及ID组成.在保存时自动生成.")
	@Column(name = "xsequence", length = AbstractPersistenceProperties.length_sequence)
	@Index(name = TABLE + "_sequence")
	private String sequence;

	/**
	 * 在执行给定实体的相应 EntityManager 持久操作之前，调用该实体的 @PrePersist 回调方法。
	 */
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

	/**
	 * 在对实体数据进行数据库更新操作之前，调用实体的 @PreUpdate 回调方法。
	 */
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}

	private void onPersist() throws Exception{
	}
	/*
	 * =========================================================================
	 * ========= 以上为 JpaObject 默认字段
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 以下为具体不同的业务及数据表字段要求
	 * =========================================================================
	 * =========
	 */
	@EntityFieldDescribe("账号")
	@Column(name = "xaccount", length = JpaObject.length_96B)
	// @CheckPersist(simplyString = true, citations = {
	// /* 验证不可重名 */
	// @Citation(attribute = "account", exist = false, type = Account.class,
	// notEquals = { @NotEqual(attribute = "account", field = "account") })
	// }, allowEmpty = false)
	private String account;

	@EntityFieldDescribe("邮箱类型")
	@Column(name = "xprotocol", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String protocol;

	@EntityFieldDescribe("发件服务器")
	@Column(name = "xsendMailServer", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String sendMailServer;

	@EntityFieldDescribe("发件服务器端口")
	@Column(name = "xsendMailPort", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String sendMailPort;

	@EntityFieldDescribe("发件服务器是否使用SSL")
	@Column(name = "xsendMailUseSSL", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String sendMailUseSSL;

	@EntityFieldDescribe("收件服务器")
	@Column(name = "xincomingMailServer", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String incomingMailServer;

	@EntityFieldDescribe("收件服务器端口")
	@Column(name = "xincomingMailPort", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String incomingMailPort;

	@EntityFieldDescribe("收件服务器使用是否使用SSL")
	@Column(name = "xincomingMailUseSSL", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String incomingMailUseSSL;

	@EntityFieldDescribe("邮件地址")
	@Column(name = "xmailAddress", length = JpaObject.length_96B)
	// @CheckPersist(simplyString = true, citations = {
	// /* 验证不可重名 */
	// @Citation(attribute = "mailAddress", exist = false, type = Account.class,
	// notEquals = { @NotEqual(attribute = "mailAddress", field = "mailAddress")
	// })
	// }, allowEmpty = false)
	private String mailAddress;

	@EntityFieldDescribe("密码")
	@Column(name = "xpassword", length = JpaObject.length_96B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String password;

	@EntityFieldDescribe("显示名称")
	@Column(name = "xdisplayName", length = JpaObject.length_255B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String displayName;

	@EntityFieldDescribe("发信名称")
	@Column(name = "xsenderName", length = JpaObject.length_255B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String senderName;

	@EntityFieldDescribe("拥有人人，可能为空，如果由系统创建。")
	@Column(name = "xownerName", length = AbstractPersistenceProperties.organization_name_length)
	@Index(name = TABLE + "_ownerName")
	@CheckPersist(allowEmpty = true)
	private String ownerName;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getSendMailServer() {
		return sendMailServer;
	}

	public void setSendMailServer(String sendMailServer) {
		this.sendMailServer = sendMailServer;
	}

	public String getSendMailPort() {
		return sendMailPort;
	}

	public void setSendMailPort(String sendMailPort) {
		this.sendMailPort = sendMailPort;
	}

	public String getSendMailUseSSL() {
		return sendMailUseSSL;
	}

	public void setSendMailUseSSL(String sendMailUseSSL) {
		this.sendMailUseSSL = sendMailUseSSL;
	}

	public String getIncomingMailServer() {
		return incomingMailServer;
	}

	public void setIncomingMailServer(String incomingMailServer) {
		this.incomingMailServer = incomingMailServer;
	}

	public String getIncomingMailPort() {
		return incomingMailPort;
	}

	public void setIncomingMailPort(String incomingMailPort) {
		this.incomingMailPort = incomingMailPort;
	}

	public String getIncomingMailUseSSL() {
		return incomingMailUseSSL;
	}

	public void setIncomingMailUseSSL(String incomingMailUseSSL) {
		this.incomingMailUseSSL = incomingMailUseSSL;
	}

	/**
	 * 获取邮件地址
	 * 
	 * @return
	 */
	public String getMailAddress() {
		return mailAddress;
	}

	/**
	 * 设置邮件地址
	 * 
	 * @return
	 */
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	/**
	 * 获取密码
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置密码
	 * 
	 * @return
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取显示名称
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 设置显示名称
	 * 
	 * @return
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 获取发信名称
	 * 
	 * @return
	 */
	public String getSenderName() {
		return senderName;
	}

	/**
	 * 设置发信名称
	 * 
	 * @return
	 */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	/**
	 * 获取拥有人名称
	 * 
	 * @return
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * 设置拥有人名称
	 * 
	 * @return
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

}