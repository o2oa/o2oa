package com.x.query.core.entity.segment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.query.core.entity.PersistenceProperties;
import com.x.query.core.entity.Stat;

@Entity
@ContainerEntity
@Table(name = PersistenceProperties.Segment.Word.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Segment.Word.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Word extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Segment.Word.table;

	public static final String TAG_TITLE = "title";
	public static final String TAG_BODY = "body";
	public static final String TAG_ATTACHMENT = "attachment";
	public static final String TAG_KEYWORD = "keyword";
	public static final String TAG_PHRASE = "phrase";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	@CheckRemove(citationNotExists =
	/* 已经没有Stat使用View了 */
	@CitationNotExist(type = Stat.class, fields = Stat.view_FIELDNAME))
	private String id = createId();

	/* 以上为 JpaObject 默认字段 */

	public void onPersist() throws Exception {

	}

	public Word() {

	}

	public Word(Entry entry) {
		this.setEntry(entry.getId());
		this.setBundle(entry.getBundle());
		this.setType(entry.getType());
	}

	/* 更新运行方法 */

	public static final String value_FIELDNAME = "value";
	@FieldDescribe("值.")
	@Column(length = length_255B, name = ColumnNamePrefix + value_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + value_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String value;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("类型,此type类型完全复制entry的type类型.")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String type;

	public static final String label_FIELDNAME = "label";
	@FieldDescribe("词性")
	@Column(length = length_32B, name = ColumnNamePrefix + label_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + label_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String label;

	public static final String entry_FIELDNAME = "entry";
	@FieldDescribe("条目对象")
	@Column(length = length_id, name = ColumnNamePrefix + entry_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + entry_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String entry;

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("唯一标识的bundle值,在porcessPlatform中对应job,在cms中对应doucment的id")
	@Column(length = length_id, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	public static final String count_FIELDNAME = "count";
	@FieldDescribe("数量")
	@Column(name = ColumnNamePrefix + count_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + count_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer count;

	public static final String tag_FIELDNAME = "tag";
	@FieldDescribe("类型.")
	@Index(name = TABLE + IndexNameMiddle + tag_FIELDNAME)
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + tag_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String tag;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("porcessPlatform中对应application应用,在cms中对应application栏目")
	@Column(length = length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String application;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

}
