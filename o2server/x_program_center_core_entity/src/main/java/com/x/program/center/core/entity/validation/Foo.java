package com.x.program.center.core.entity.validation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.program.center.core.entity.PersistenceProperties;

@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Validation.Foo.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Validation.Foo.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Foo extends SliceJpaObject {

	private static final long serialVersionUID = 6387104721461689291L;

	public Foo() {

	}

	public Foo(String className, String application, String cron) {

	}

	private static final String TABLE = PersistenceProperties.Validation.Foo.TABLE;

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

	public FooProperties getProperties() {
		if (null == this.properties) {
			this.properties = new FooProperties();
		}
		return this.properties;
	}

	public void setProperties(FooProperties properties) {
		this.properties = properties;
	}

	public static final String NAME_FIELDNAME = "name";
	@FieldDescribe("名称.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + NAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String S1_FIELDNAME = "s1";
	@FieldDescribe("String值1.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + S1_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + S1_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String s1;

	public static final String S2_FIELDNAME = "s2";
	@FieldDescribe("String值2.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + S2_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + S2_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String s2;

	public static final String S3_FIELDNAME = "s3";
	@FieldDescribe("String值3.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + S3_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + S3_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String s3;

	public static final String S4_FIELDNAME = "s4";
	@FieldDescribe("String值4.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + S4_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + S4_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String s4;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("属性对象存储字段.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private FooProperties properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	public String getS3() {
		return s3;
	}

	public void setS3(String s3) {
		this.s3 = s3;
	}

	public String getS4() {
		return s4;
	}

	public void setS4(String s4) {
		this.s4 = s4;
	}

}