package com.x.processplatform.core.entity.temporary;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Temporary.TriggerWorkRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Temporary.TriggerWorkRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TriggerWorkRecord extends SliceJpaObject {

	private static final long serialVersionUID = -9130723320348772653L;

	private static final String TABLE = PersistenceProperties.Temporary.TriggerWorkRecord.table;

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

	/* 更新运行方法 */

	@FieldDescribe("最后更新的sequence.")
	@Column(length = AbstractPersistenceProperties.length_sequence, name = "xlastSequence")
	private String lastSequence;

	public String getLastSequence() {
		return lastSequence;
	}

	public void setLastSequence(String lastSequence) {
		this.lastSequence = lastSequence;
	}

}
