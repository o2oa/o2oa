package com.x.message.core.entity;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Instant.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Instant.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Instant extends SliceJpaObject {

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.Instant.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("ID,数据库主键.")
	@Id
	@Column(length = length_id, name = IDCOLUMN)
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {
	}

	/* 更新运行方法 */

	/* flag标志位 */
	/* Entity 默认字段结束 */

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("通知标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String body_FIELDNAME = "body";
	@FieldDescribe("内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_10M, name = ColumnNamePrefix + body_FIELDNAME)
	private String body;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("消息类型.")
	@Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("通知对象.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String consumed_FIELDNAME = "consumed";
	@FieldDescribe("是否已经消费掉.")
	@Column(name = ColumnNamePrefix + consumed_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + consumed_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean consumed;

	public static final String consumerList_FIELDNAME = "consumerList";
	@FieldDescribe("消费对象.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ consumerList_FIELDNAME, joinIndex = @Index(name = TABLE + consumerList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + consumerList_FIELDNAME)
	@ElementIndex(name = TABLE + consumerList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> consumerList;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getConsumerList() {
		return consumerList;
	}

	public void setConsumerList(List<String> consumerList) {
		this.consumerList = consumerList;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getConsumed() {
		return consumed;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}

}