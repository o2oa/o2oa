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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

@Schema(name = "Message", description = "消息消息对象.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
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

	public void onPersist() throws Exception {
		// nothing
	}

	public MessageProperties getProperties() {
		if (null == this.properties) {
			this.properties = new MessageProperties();
		}
		return this.properties;
	}

	public void setProperties(MessageProperties properties) {
		this.properties = properties;
	}

	public static final String TITLE_FIELDNAME = "title";
	@FieldDescribe("通知标题.")
	@Column(length = length_255B, name = ColumnNamePrefix + TITLE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TITLE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String BODY_FIELDNAME = "body";
	@FieldDescribe("内容.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_10M, name = ColumnNamePrefix + BODY_FIELDNAME)
	private String body;

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("消息类型.")
	@Column(length = length_255B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String CONSUMER_FIELDNAME = "consumer";
	@FieldDescribe("消费者.")
	@Column(length = length_255B, name = ColumnNamePrefix + CONSUMER_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + CONSUMER_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String consumer;

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("通知对象.")
	@Column(length = length_255B, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String CONSUMED_FIELDNAME = "consumed";
	@FieldDescribe("是否已经消费掉.")
	@Column(name = ColumnNamePrefix + CONSUMED_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean consumed;

	public static final String INSTANT_FIELDNAME = "instant";
	@FieldDescribe("主体消息id.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + INSTANT_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + INSTANT_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String instant;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private MessageProperties properties;

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