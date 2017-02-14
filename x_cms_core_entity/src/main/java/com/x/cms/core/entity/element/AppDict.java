package com.x.cms.core.entity.element;

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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.PersistenceProperties;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Element.AppDict.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppDict extends SliceJpaObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Element.AppDict.table;

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

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("应用ID.")
	@Column(name = "xappId", length = JpaObject.length_id)
	@Index(name = TABLE + "_appId")
	@CheckPersist(citationExists = {
			/* 检查关联的Application需存在 */
			@CitationExist(type = AppInfo.class) }, allowEmpty = true)
	private String appId;

	@EntityFieldDescribe("文件名称.")
	@Column(name = "xname", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_name")
	@CheckPersist(simplyString = true, citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias", "name" }, type = AppDict.class) , allowEmpty = true)
	private String name;

	@EntityFieldDescribe("别名.")
	@Column(name = "xalias", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_alias")
	@CheckPersist(simplyString = true, citationNotExists =
	/* 同一个应用下不能有重名 */
	@CitationNotExist(fields = { "id", "alias", "name" }, type = AppDict.class) , allowEmpty = true)
	private String alias;

	@EntityFieldDescribe("说明.")
	@Column(name = "xdescription", length = AbstractPersistenceProperties.processPlatform_name_length)
	@Index(name = TABLE + "_description")
	@CheckPersist(allowEmpty = true)
	private String description;

	@EntityFieldDescribe("信息创建人UID")
	@Column(name = "xcreatorUid", length = JpaObject.length_64B)
	@CheckPersist(simplyString = true, allowEmpty = true)
	private String creatorUid;

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

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCreatorUid() {
		return creatorUid;
	}

	public void setCreatorUid(String creatorUid) {
		this.creatorUid = creatorUid;
	}

}