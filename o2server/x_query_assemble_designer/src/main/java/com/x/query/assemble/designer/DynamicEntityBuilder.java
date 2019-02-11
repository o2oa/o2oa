package com.x.query.assemble.designer;

import java.io.File;
import java.util.Date;

import javax.lang.model.element.Modifier;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.DynamicEntity.BooleanField;
import com.x.query.assemble.designer.DynamicEntity.DateField;
import com.x.query.assemble.designer.DynamicEntity.DateTimeField;
import com.x.query.assemble.designer.DynamicEntity.DoubleField;
import com.x.query.assemble.designer.DynamicEntity.Field;
import com.x.query.assemble.designer.DynamicEntity.IntegerField;
import com.x.query.assemble.designer.DynamicEntity.LongField;
import com.x.query.assemble.designer.DynamicEntity.StringField;
import com.x.query.assemble.designer.DynamicEntity.TimeField;

public class DynamicEntityBuilder {

	public static final String FIELDNAME_SUFFIX = "_FIELDNAME";

	private DynamicEntity dynamicEntity;
	private File dir;

	public DynamicEntityBuilder(DynamicEntity dynamicEntity, File dir) {

		this.dynamicEntity = dynamicEntity;
		this.dir = dir;

	}

	public JavaFile build() throws Exception {

		AnnotationSpec annotationSpec_entity = AnnotationSpec.builder(Entity.class).build();
		AnnotationSpec annotationSpec_containerEntity = AnnotationSpec.builder(ContainerEntity.class).build();
		AnnotationSpec annotationSpec_table = AnnotationSpec.builder(Table.class)
				.addMember("name", "\"" + dynamicEntity.tableName() + "\"")
				.addMember("uniqueConstraints", "{@javax.persistence.UniqueConstraint(name = \""
						+ dynamicEntity.tableName()
						+ "\" + com.x.base.core.entity.JpaObject.IndexNameMiddle  + com.x.base.core.entity.JpaObject.DefaultUniqueConstraintSuffix, columnNames = { com.x.base.core.entity.JpaObject.IDCOLUMN, com.x.base.core.entity.JpaObject.CREATETIMECOLUMN, com.x.base.core.entity.JpaObject.UPDATETIMECOLUMN, com.x.base.core.entity.JpaObject.SEQUENCECOLUMN }) }")
				.build();
		AnnotationSpec annotationSpec_inheritance = AnnotationSpec.builder(Inheritance.class)
				.addMember("strategy", "javax.persistence.InheritanceType.TABLE_PER_CLASS").build();

		// private static final long serialVersionUID = 6387104721461689291L;
		FieldSpec fieldSpec_serialVersionUID = FieldSpec
				.builder(long.class, "serialVersionUID", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("1L").build();
		// private static final String TABLE =
		// PersistenceProperties.Validation.Meta.table;
		FieldSpec fieldSpec_TABLE = FieldSpec
				.builder(String.class, "TABLE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
				.initializer("\"" + dynamicEntity.tableName() + "\"").build();

		Builder builder = TypeSpec.classBuilder(dynamicEntity.classSimpleName()).superclass(SliceJpaObject.class)
				.addModifiers(Modifier.PUBLIC).addAnnotation(annotationSpec_entity)
				.addAnnotation(annotationSpec_containerEntity).addAnnotation(annotationSpec_inheritance)
				.addAnnotation(annotationSpec_table).addField(fieldSpec_serialVersionUID).addField(fieldSpec_TABLE);

		this.createIdField(builder);
		this.createOnPersistMethod(builder);
		this.createStringFields(builder);
		this.createIntegerFields(builder);
		this.createLongFields(builder);
		this.createDoubleFields(builder);
		this.createBooleanFields(builder);
		this.createDateFields(builder);
		this.createTimeFields(builder);
		this.createDateTimeFields(builder);

		TypeSpec typeSpec = builder.build();

		JavaFile javaFile = JavaFile.builder(DynamicEntity.CLASS_PACKAGE, typeSpec).build();

		javaFile.writeTo(dir);
		return javaFile;
	}

	private void createOnPersistMethod(Builder builder) {
		MethodSpec method = MethodSpec.methodBuilder("onPersist").addModifiers(Modifier.PUBLIC).returns(void.class)
				.addException(Exception.class).build();
		builder.addMethod(method);
	}

	private void createIdField(Builder builder) {
//		public String getId() {
//			return id;
//		}
//		public void setId(String id) {
//			this.id = id;
//		}
//		@FieldDescribe("数据库主键,自动生成.")
//		@Id
//		@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
//		private String id = createId();
		AnnotationSpec fieldDescribe = AnnotationSpec.builder(FieldDescribe.class).addMember("value", "\"数据库主键,自动生成.\"")
				.build();
		AnnotationSpec id = AnnotationSpec.builder(Id.class).build();
		AnnotationSpec column = AnnotationSpec.builder(Column.class).addMember("length", "length_id")
				.addMember("name", "ColumnNamePrefix + id_FIELDNAME").build();
		MethodSpec get = MethodSpec.methodBuilder("getId").addModifiers(Modifier.PUBLIC).returns(String.class)
				.addStatement("return this.id").build();
		MethodSpec set = MethodSpec.methodBuilder("setId").addModifiers(Modifier.PUBLIC).returns(void.class)
				.addParameter(String.class, "id").addStatement("this.id = id").build();
		FieldSpec fieldSpec = FieldSpec.builder(String.class, JpaObject.id_FIELDNAME, Modifier.PRIVATE)
				.initializer("createId()").addAnnotation(fieldDescribe).addAnnotation(id).addAnnotation(column).build();
		builder.addField(fieldSpec).addMethod(set).addMethod(get);
	}

	private void createStringFields(Builder builder) {
		for (StringField field : ListTools.trim(dynamicEntity.getStringFields(), true, true)) {
			this.createStringField(builder, field);
		}
	}

	private void createStringField(Builder builder, StringField field) {
//		public static final String stringValue_FIELDNAME = "stringValue";
//		@FieldDescribe("文本字段.")
//		@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + stringValue_FIELDNAME)
//		@Index(name = TABLE + IndexNameMiddle + stringValue_FIELDNAME)
//		@CheckPersist(allowEmpty = true)
//		private String stringValue;

		AnnotationSpec column = AnnotationSpec.builder(Column.class).addMember("length", "length_255B")
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(String.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(String.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(String.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createIntegerFields(Builder builder) {
		for (IntegerField field : ListTools.trim(dynamicEntity.getIntegerFields(), true, true)) {
			this.createIntegerField(builder, field);
		}
	}

	private void createIntegerField(Builder builder, IntegerField field) {
//		public static final String integerValue_FIELDNAME = "integerValue";
//		@FieldDescribe("整型.")
//		@CheckPersist(allowEmpty = true)
//		@Index(name = TABLE + IndexNameMiddle + integerValue_FIELDNAME)
//		@Column(name = ColumnNamePrefix + integerValue_FIELDNAME)

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(Integer.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Integer.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Integer.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createLongFields(Builder builder) {
		for (LongField field : ListTools.trim(dynamicEntity.getLongFields(), true, true)) {
			this.createLongField(builder, field);
		}
	}

	private void createLongField(Builder builder, LongField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(Long.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Long.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Long.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createDoubleFields(Builder builder) {
		for (DoubleField field : ListTools.trim(dynamicEntity.getDoubleFields(), true, true)) {
			this.createDoubleField(builder, field);
		}
	}

	private void createDoubleField(Builder builder, DoubleField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(Double.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Double.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Double.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createBooleanFields(Builder builder) {
		for (BooleanField field : ListTools.trim(dynamicEntity.getBooleanFields(), true, true)) {
			this.createBooleanField(builder, field);
		}
	}

	private void createBooleanField(Builder builder, BooleanField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(Boolean.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Boolean.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Boolean.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createDateFields(Builder builder) {
		for (DateField field : ListTools.trim(dynamicEntity.getDateFields(), true, true)) {
			this.createDateField(builder, field);
		}
	}

	private void createDateField(Builder builder, DateField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.Temporal.DATE").build();

		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createTimeFields(Builder builder) {
		for (TimeField field : ListTools.trim(dynamicEntity.getTimeFields(), true, true)) {
			this.createTimeField(builder, field);
		}
	}

	private void createTimeField(Builder builder, TimeField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.Temporal.TIME").build();

		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createDateTimeFields(Builder builder) {
		for (DateTimeField field : ListTools.trim(dynamicEntity.getDateTimeFields(), true, true)) {
			this.createDateTimeField(builder, field);
		}
	}

	private void createDateTimeField(Builder builder, DateTimeField field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.Temporal.TIMESTAMP").build();

		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder("get" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder("set" + StringUtils.capitalize(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private FieldSpec fieldName(Field field) {
		FieldSpec spec = FieldSpec
				.builder(String.class, field.fieldName(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("\"" + field.getName() + "\"").build();
		return spec;
	}

	private AnnotationSpec fieldDescribe(Field field) {
		AnnotationSpec spec = AnnotationSpec.builder(FieldDescribe.class)
				.addMember("value", "\"" + field.getDescription() + "\"").build();
		return spec;
	}

	private AnnotationSpec checkPersist(Field field) {
		AnnotationSpec spec = AnnotationSpec.builder(CheckPersist.class).addMember("allowEmpty", "true").build();
		return spec;
	}

	private AnnotationSpec index(Field field) {
		AnnotationSpec spec = AnnotationSpec.builder(Index.class)
				.addMember("name", "TABLE + IndexNameMiddle + " + field.fieldName()).build();
		return spec;
	}

}