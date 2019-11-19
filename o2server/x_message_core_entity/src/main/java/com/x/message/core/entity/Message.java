package com.x.message.core.entity;

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
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Message.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Message.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Message extends SliceJpaObject {

	private static final long serialVersionUID = 5733185578089403629L;

	private static final String TABLE = PersistenceProperties.Message.table;

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

	public static final String consumer_FIELDNAME = "consumer";
	@FieldDescribe("消费者.")
	@Column(length = length_255B, name = ColumnNamePrefix + consumer_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + consumer_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String consumer;

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

	public static final String instant_FIELDNAME = "instant";
	@FieldDescribe("主体消息id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + instant_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + instant_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String instant;

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

	public String getConsumer() {
		return consumer;
	}

	public void setConsumer(String consumer) {
		this.consumer = consumer;
	}

	public Boolean getConsumed() {
		return consumed;
	}

	public void setConsumed(Boolean consumed) {
		this.consumed = consumed;
	}

	public String getInstant() {
		return instant;
	}

	public void setInstant(String instant) {
		this.instant = instant;
	}

}