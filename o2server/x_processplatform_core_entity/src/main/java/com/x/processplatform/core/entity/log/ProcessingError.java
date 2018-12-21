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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Log.ProcessingError.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.ProcessingError.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ProcessingError extends SliceJpaObject {

	private static final long serialVersionUID = 6696198642414522481L;

	private static final String TABLE = PersistenceProperties.Log.ProcessingError.table;

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
	}

	public void setMessage(String message) {
		if (StringTools.utf8Length(message) > JpaObject.length_255B) {
			this.message = StringTools.utf8SubString(message, length_255B);
		} else {
			this.message = message;
		}
	}

	/* 更新运行方法 */

	public static final String work_FIELDNAME = "work";
	@FieldDescribe("工作")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	private String work;

	public static final String message_FIELDNAME = "message";
	@FieldDescribe("错误消息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + message_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + message_FIELDNAME)
	private String message;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("文本内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + data_FIELDNAME)
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