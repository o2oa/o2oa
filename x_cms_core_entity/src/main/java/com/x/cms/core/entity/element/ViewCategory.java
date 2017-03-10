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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.cms.core.entity.PersistenceProperties;


@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Element.ViewCategory.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ViewCategory extends SliceJpaObject {

	private static final long serialVersionUID = 3263767038182121907L;
	private static final String TABLE = PersistenceProperties.Element.ViewCategory.table;

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
	
	private void onPersist() throws Exception{
		this.editor = StringUtils.trimToEmpty(this.editor);
	}
	
	@PreUpdate
	public void preUpdate() throws Exception{
		this.updateTime = new Date();
		this.onPersist();
	}
	
	@EntityFieldDescribe( "数据库主键,自动生成." )
	@Id
	@Column( name="xid", length = JpaObject.length_id)
	private String id = createId();

	@EntityFieldDescribe( "创建时间,自动生成." )
	@Index(name = TABLE + "_createTime" )
	@Column( name="xcreateTime")
	private Date createTime;

	@EntityFieldDescribe( "修改时间,自动生成." )
	@Index(name = TABLE + "_updateTime" )
	@Column( name="xupdateTime")
	private Date updateTime;

	@EntityFieldDescribe( "列表序号, 由创建时间以及ID组成.在保存时自动生成." )
	@Column( name="xsequence", length = AbstractPersistenceProperties.length_sequence )
	@Index(name = TABLE + "_sequence" )
	private String sequence;
	
	@EntityFieldDescribe("最后的编辑者.")
	@Column(name="xeditor", length = AbstractPersistenceProperties.organization_name_length)
	@CheckPersist(allowEmpty = true)
	private String editor;

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
	
	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}
	/* ==============================================以上为 JpaObject 默认字段 */
	

	/* =====================================更新运行方法 =======================================*/
	@EntityFieldDescribe("视图ID.")
	@Column(name="xviewId", length = JpaObject.length_id )
	@CheckPersist(allowEmpty = false)
	private String viewId;
	
	@EntityFieldDescribe("分类ID.")
	@Column(name="xcategoryId", length = JpaObject.length_id )
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}