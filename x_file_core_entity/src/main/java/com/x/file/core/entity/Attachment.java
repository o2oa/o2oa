package com.x.file.core.entity;

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

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.utils.DateTools;

@ContainerEntity
@Entity
@Table(name = PersistenceProperties.Attachment.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.file)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = 7706126788445253456L;
	private static final String TABLE = PersistenceProperties.Attachment.table;

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

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
		this.lastUpdateTime = this.updateTime;
		/* 如果为顶层，那么将目录设置为空 */
		this.folder = StringUtils.trimToEmpty(this.folder);
		/* 如果扩展名为空去掉null */
		this.extension = StringUtils.trimToEmpty(extension);
		/* 共享人员去重 */
		if (null != this.shareList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.shareList);
			this.shareList = set.asList();
		}
		/* 可编辑人员必须在共享人员中 */
		if (null != this.editorList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.editorList);
			this.editorList = set.asList();
		}
	}

	/* 更新运行方法 */

	@Override
	public String path() throws Exception {
		if (null == this.person) {
			throw new Exception("person can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = this.person;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@EntityFieldDescribe("所属用户.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xperson")
	@Index(name = TABLE + "_person")
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String person;

	@EntityFieldDescribe("文件名称.")
	@Column(length = AbstractPersistenceProperties.file_name_length, name = "xname")
	@Index(name = TABLE + "_name")
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 同一个用户同一个目录下不能有重名 */
	@CitationNotExist(fields = { "name", "id" }, type = Attachment.class, equals = {
			@Equal(property = "person", field = "person"), @Equal(property = "folder", field = "folder") }))
	private String name;

	@EntityFieldDescribe("扩展名。")
	@Column(length = JpaObject.length_64B, name = "xextension")
	@CheckPersist(allowEmpty = true, fileNameString = true)
	private String extension;

	@EntityFieldDescribe("存储器的名称,也就是多个存放节点的名字.")
	@Column(length = JpaObject.length_64B, name = "xstorage")
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + "_storage")
	private String storage;

	@EntityFieldDescribe("文件大小.")
	@Index(name = TABLE + "_length")
	@Column(name = "xlength")
	@CheckPersist(allowEmpty = true)
	private Long length;

	@EntityFieldDescribe("共享人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_shareList", joinIndex = @Index(name = TABLE + "_shareList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xshareList")
	@ElementIndex(name = TABLE + "_shareList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> shareList;

	@EntityFieldDescribe("可编辑人员")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = PersistenceProperties.orderColumn)
	@ContainerTable(name = TABLE + "_editorList", joinIndex = @Index(name = TABLE + "_editorList_join"))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = "xeditorList")
	@ElementIndex(name = TABLE + "_editorList_element")
	@CheckPersist(allowEmpty = true)
	private List<String> editorList;

	@EntityFieldDescribe("文件所属分类.")
	@Column(length = JpaObject.length_id, name = "xfolder")
	@Index(name = TABLE + "_folder")
	/* 上级目录必须存在 */
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Folder.class, equals = @Equal(property = "person", field = "person")))
	private String folder;

	@EntityFieldDescribe("最后更新时间")
	@Column(name = "xlastUpdateTime")
	@Index(name = TABLE + "_lastUpdateTime")
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	@EntityFieldDescribe("最后更新人员")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = "xlastUpdatePerson")
	@Index(name = TABLE + "_lastUpdatePerson")
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public List<String> getShareList() {
		return shareList;
	}

	public void setShareList(List<String> shareList) {
		this.shareList = shareList;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public List<String> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
	}

}