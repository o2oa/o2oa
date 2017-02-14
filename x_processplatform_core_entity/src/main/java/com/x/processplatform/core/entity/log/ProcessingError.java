package com.x.processplatform.core.entity.log;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Log.ProcessingError.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProcessingError extends SliceJpaObject {

	private static final long serialVersionUID = 6696198642414522481L;

	private static final String TABLE = PersistenceProperties.Log.ProcessingError.table;

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

	@EntityFieldDescribe("数据库主键,自动生成.")
	@Id
				@Column(length = JpaObject.length_id, name = JpaObject.IDCOLUMN)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	private void onPersist() {
	}

	/* 更新运行方法 */

	@EntityFieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = "xwork")
	@Index(name = TABLE + "_work")
	private String work;

	@EntityFieldDescribe("错误消息")
	@Column(length = JpaObject.length_255B, name = "xmessage")
	@Index(name = TABLE + "_message")
	private String message;

	public void setMessage(String message) {
		if (StringTools.utf8Length(message) > JpaObject.length_255B) {
			this.message = message.substring(0, 70);
		} else {
			this.message = message;
		}
	}

	@EntityFieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = "xdata")
	private String data;

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getMessage() {
		return message;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}