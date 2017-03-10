package com.x.file.core.entity.personal;

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
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.utils.DateTools;
import com.x.file.core.entity.PersistenceProperties;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Personal.Folder.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Folder extends SliceJpaObject {

	private static final long serialVersionUID = -2266232193925155825L;
	private static final String TABLE = PersistenceProperties.Personal.Folder.table;

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
	@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	@Index(name = TABLE + "_id")
	private String id = createId();

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

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() throws Exception{
		/* 如果为顶层，那么将上级目录设置为空 */
		this.superior = StringUtils.trimToEmpty(this.superior);
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("所属用户.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String person;

	@EntityFieldDescribe("分类名称.")
	@Column(length = AbstractPersistenceProperties.file_name_length, name = "xname")
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个目录下不能有重名 */
	@CitationNotExist(fields = { "name", "id" }, type = Folder.class, equals = {
			@Equal(property = "person", field = "person"), @Equal(property = "superior", field = "superior") }))
	private String name;

	@EntityFieldDescribe("上级分类ID,为空代表顶级分类。")
	@Column(length = JpaObject.length_id, name = "xsuperior")
	@Index(name = TABLE + "_superior")
	@CheckPersist(allowEmpty = true, citationExists =
	/* 上级目录必须存在,且不能为自己 */
	@CitationExist(type = Folder.class, equals = @Equal(property = "person", field = "person")))
	private String superior;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSuperior() {
		return superior;
	}

	public void setSuperior(String superior) {
		this.superior = superior;
	}

}