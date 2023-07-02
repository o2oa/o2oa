package com.x.base.core.entity.dynamic;

import java.io.File;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.KeyColumn;
import org.apache.openjpa.persistence.jdbc.KeyIndex;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dynamic.DynamicEntity.Field;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class DynamicEntityBuilder {

	public static final String FIELDNAME_SUFFIX = "_FIELDNAME";
	public static final String DOT_CLASS = ".class";

	private DynamicEntity dynamicEntity;
	private File dir;

	public DynamicEntityBuilder(DynamicEntity dynamicEntity, File dir) {

		this.dynamicEntity = dynamicEntity;
		this.dir = dir;

	}

	public JavaFile build() throws Exception {

		AnnotationSpec annotationSpec_entity = AnnotationSpec.builder(Entity.class).build();
		AnnotationSpec annotationSpec_containerEntity = AnnotationSpec.builder(ContainerEntity.class)
				.addMember("dumpSize", "500")
				.addMember("type", "com.x.base.core.entity.annotation.ContainerEntity.Type.custom")
				.addMember("reference", "com.x.base.core.entity.annotation.ContainerEntity.Reference.strong").build();
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
		this.createBundleField(builder);
		this.createOnPersistMethod(builder);
		this.createStringFields(builder);
		this.createIntegerFields(builder);
		this.createLongFields(builder);
		this.createDoubleFields(builder);
		this.createBooleanFields(builder);
		this.createDateFields(builder);
		this.createTimeFields(builder);
		this.createDateTimeFields(builder);
		this.createListStringFields(builder);
		this.createListIntegerFields(builder);
		this.createListLongFields(builder);
		this.createListDoubleFields(builder);
		this.createListBooleanFields(builder);
		this.createStringLobFields(builder);
		this.createStringMapFields(builder);

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
		// public String getId() {
		// return id;
		// }
		// public void setId(String id) {
		// this.id = id;
		// }
		// @FieldDescribe("数据库主键,自动生成.")
		// @Id
		// @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
		// private String id = createId();
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

	/**
	 * 自建表默认创建bundle属性用于存储流程实例的job
	 * @param builder
	 */
	private void createBundleField(Builder builder) {
		Field bundleField = new Field();
		bundleField.setName(DynamicEntity.BUNDLE_FIELD);
		bundleField.setDescription("流程实例的JOB");
		bundleField.setType("string");
		this.createField(builder, bundleField, String.class);
	}

	private void createStringFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.stringFields(), true, true)) {
			this.createField(builder, field, String.class);
		}
	}

	private void createIntegerFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.integerFields(), true, true)) {
			this.createField(builder, field, Integer.class);
		}
	}

	private void createLongFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.longFields(), true, true)) {
			this.createField(builder, field, Long.class);
		}
	}

	private void createDoubleFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.doubleFields(), true, true)) {
			this.createField(builder, field, Double.class);
		}
	}

	private void createBooleanFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.booleanFields(), true, true)) {
			this.createField(builder, field, Boolean.class);
		}
	}

	private void createField(Builder builder, Field field, Class<?> typeClass) {
		// public static final String stringValue_FIELDNAME = "stringValue";
		// @FieldDescribe("文本字段.")
		// @Column(length = JpaObject.length_255B, name = ColumnNamePrefix +
		// stringValue_FIELDNAME)
		// @Index(name = TABLE + IndexNameMiddle + stringValue_FIELDNAME)
		// @CheckPersist(allowEmpty = true)
		// private String stringValue;
		AnnotationSpec column = null;
		if (typeClass == String.class) {
			column = AnnotationSpec.builder(Column.class).addMember("length", "length_255B")
					.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();
		} else {
			column = AnnotationSpec.builder(Column.class).addMember("name", "ColumnNamePrefix + " + field.fieldName())
					.build();
		}

		FieldSpec fieldSpec = FieldSpec.builder(typeClass, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(typeClass).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(typeClass, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createDateFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.dateFields(), true, true)) {
			this.createDateField(builder, field);
		}
	}

	private void createDateField(Builder builder, Field field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.TemporalType.DATE").build();
		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createTimeFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.timeFields(), true, true)) {
			this.createTimeField(builder, field);
		}
	}

	private void createTimeField(Builder builder, Field field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.TemporalType.TIME").build();

		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createDateTimeFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.dateTimeFields(), true, true)) {
			this.createDateTimeField(builder, field);
		}
	}

	private void createDateTimeField(Builder builder, Field field) {

		AnnotationSpec column = AnnotationSpec.builder(Column.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		AnnotationSpec temporal = AnnotationSpec.builder(Temporal.class)
				.addMember("value", "javax.persistence.TemporalType.TIMESTAMP").build();

		FieldSpec fieldSpec = FieldSpec.builder(Date.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(this.index(field))
				.addAnnotation(this.checkPersist(field)).addAnnotation(column).addAnnotation(temporal).build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(Date.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(Date.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createListStringFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.stringListFields(), true, true)) {
			this.createListFields(builder, field, String.class);
		}
	}

	private void createListIntegerFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.integerListFields(), true, true)) {
			this.createListFields(builder, field, Integer.class);
		}
	}

	private void createListLongFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.longListFields(), true, true)) {
			this.createListFields(builder, field, Long.class);
		}
	}

	private void createListDoubleFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.doubleListFields(), true, true)) {
			this.createListFields(builder, field, Double.class);
		}
	}

	private void createListBooleanFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.booleanListFields(), true, true)) {
			this.createListFields(builder, field, Boolean.class);
		}
	}

	private void createListFields(Builder builder, Field field, Class<?> typeClass) {

		// public static final String groupList_FIELDNAME = "groupList";
		// @FieldDescribe("群组的群组成员.存放群组 ID.")
		// @ContainerTable(name = TABLE + ContainerTableNameMiddle +
		// groupList_FIELDNAME, joinIndex = @Index(name = TABLE
		// + IndexNameMiddle + groupList_FIELDNAME + JoinIndexNameSuffix))
		// @ElementIndex(name = TABLE + IndexNameMiddle + groupList_FIELDNAME +
		// ElementIndexNameSuffix)
		// @PersistentCollection(fetch = FetchType.EAGER)
		// @OrderColumn(name = ORDERCOLUMNCOLUMN)
		// @ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix +
		// groupList_FIELDNAME)
		// @CheckPersist(allowEmpty = true, citationExists = @CitationExist(type =
		// Group.class))
		// private List<String> groupList;

		AnnotationSpec containerTable = AnnotationSpec.builder(ContainerTable.class)
				.addMember("name", "TABLE + ContainerTableNameMiddle + " + field.fieldName())
				.addMember("joinIndex", "@org.apache.openjpa.persistence.jdbc.Index(name = TABLE + IndexNameMiddle + "
						+ field.fieldName() + " + JoinIndexNameSuffix)")
				.build();

		AnnotationSpec elementIndex = AnnotationSpec.builder(ElementIndex.class)
				.addMember("name", "TABLE + IndexNameMiddle + " + field.fieldName() + " + ElementIndexNameSuffix")
				.build();

		AnnotationSpec persistentCollection = AnnotationSpec.builder(PersistentCollection.class)
				.addMember("fetch", "javax.persistence.FetchType.EAGER")
				.addMember("elementType", typeClass.getSimpleName() + DOT_CLASS).build();

		AnnotationSpec orderColumn = AnnotationSpec.builder(OrderColumn.class).addMember("name", "ORDERCOLUMNCOLUMN")
				.build();

		AnnotationSpec elementColumn = null;
		if (CharSequence.class.isAssignableFrom(typeClass)) {
			elementColumn = AnnotationSpec.builder(ElementColumn.class).addMember("length", "length_255B")
					.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();
		} else {
			elementColumn = AnnotationSpec.builder(ElementColumn.class)
					.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();
		}
		ClassName type = ClassName.get(typeClass);
		ClassName list = ClassName.get(List.class);
		TypeName list_type = ParameterizedTypeName.get(list, type);

		FieldSpec fieldSpec = FieldSpec.builder(list_type, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(containerTable).addAnnotation(elementIndex)
				.addAnnotation(persistentCollection).addAnnotation(orderColumn).addAnnotation(elementColumn).build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(list_type).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(list_type, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createStringLobFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.stringLobFields(), true, true)) {
			this.createStringLobField(builder, field);
		}
	}

	private void createStringLobField(Builder builder, Field field) {

		// public static final String stringLobValue_FIELDNAME = "stringLobValue";
		// @FieldDescribe("长文本.")
		// @Lob
		// @Basic(fetch = FetchType.EAGER)
		// @Column(length = JpaObject.length_10M, name = ColumnNamePrefix +
		// stringLobValue_FIELDNAME)

		AnnotationSpec lob = AnnotationSpec.builder(Lob.class).build();

		AnnotationSpec basic = AnnotationSpec.builder(Basic.class)
				.addMember("fetch", "javax.persistence.FetchType.EAGER").build();

		AnnotationSpec column = AnnotationSpec.builder(Column.class).addMember("length", "length_100M")
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).build();

		FieldSpec fieldSpec = FieldSpec.builder(String.class, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(lob).addAnnotation(basic).addAnnotation(column)
				.build();
		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(String.class).addStatement("return this." + field.getName())
				.build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(String.class, field.getName())
				.addStatement("this." + field.getName() + " = " + field.getName()).build();
		builder.addField(this.fieldName(field)).addField(fieldSpec).addMethod(get).addMethod(set);

	}

	private void createStringMapFields(Builder builder) {
		for (Field field : ListTools.trim(dynamicEntity.stringMapFields(), true, true)) {
			this.createStringMapField(builder, field);
		}
	}

	private void createStringMapField(Builder builder, Field field) {

		// @FieldDescribe("Map类型.")
		// @CheckPersist(allowEmpty = true)
		// @PersistentMap(fetch = FetchType.EAGER, elementType = String.class, keyType =
		// String.class)
		// @ContainerTable(name = TABLE + ContainerTableNameMiddle +
		// mapValueMap_FIELDNAME, joinIndex = @Index(name = TABLE
		// + IndexNameMiddle + mapValueMap_FIELDNAME + JoinIndexNameSuffix))
		// @KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
		// @ElementColumn(length = length_255B, name = ColumnNamePrefix +
		// mapValueMap_FIELDNAME)
		// @ElementIndex(name = TABLE + IndexNameMiddle + mapValueMap_FIELDNAME +
		// ElementIndexNameSuffix)
		// @KeyIndex(name = TABLE + IndexNameMiddle + mapValueMap_FIELDNAME +
		// KeyIndexNameSuffix)

		AnnotationSpec persistentMap = AnnotationSpec.builder(PersistentMap.class)
				.addMember("fetch", " javax.persistence.FetchType.EAGER").addMember("elementType", "String.class")
				.addMember("keyType", "String.class").build();

		AnnotationSpec containerTable = AnnotationSpec.builder(ContainerTable.class)
				.addMember("name", "TABLE + ContainerTableNameMiddle + " + field.fieldName())
				.addMember("joinIndex", "@org.apache.openjpa.persistence.jdbc.Index(name = TABLE + IndexNameMiddle + "
						+ field.fieldName() + " + JoinIndexNameSuffix)")
				.build();

		AnnotationSpec keyColumn = AnnotationSpec.builder(KeyColumn.class)
				.addMember("name", "ColumnNamePrefix + key_FIELDNAME").build();

		AnnotationSpec elementColumn = AnnotationSpec.builder(ElementColumn.class)
				.addMember("name", "ColumnNamePrefix + " + field.fieldName()).addMember("length", "length_255B")
				.build();

		AnnotationSpec elementIndex = AnnotationSpec.builder(ElementIndex.class)
				.addMember("name", "TABLE + IndexNameMiddle + " + field.fieldName() + " + ElementIndexNameSuffix")
				.build();

		AnnotationSpec keyIndex = AnnotationSpec.builder(KeyIndex.class)
				.addMember("name", "TABLE + IndexNameMiddle + " + field.fieldName() + " + KeyIndexNameSuffix").build();

		ClassName type = ClassName.get(String.class);
		ClassName map = ClassName.get(LinkedHashMap.class);
		TypeName map_type = ParameterizedTypeName.get(map, type, type);

		FieldSpec fieldSpec = FieldSpec.builder(map_type, field.getName(), Modifier.PRIVATE)
				.addAnnotation(this.fieldDescribe(field)).addAnnotation(persistentMap).addAnnotation(containerTable)
				.addAnnotation(keyColumn).addAnnotation(elementColumn).addAnnotation(elementIndex)
				.addAnnotation(keyIndex).build();

		MethodSpec get = MethodSpec.methodBuilder(StringTools.getMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(map_type).addStatement("return this." + field.getName()).build();
		MethodSpec set = MethodSpec.methodBuilder(StringTools.setMethodName(field.getName()))
				.addModifiers(Modifier.PUBLIC).returns(void.class).addParameter(map_type, field.getName())
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
