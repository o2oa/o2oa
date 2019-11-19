package com.x.base.core.project.build;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.junit.Test;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.Module;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class CheckCore {

	public static void main(String... args) throws Exception {
		try (ScanResult sr = new ClassGraph().disableJarScanning().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = sr.getClassesWithAnnotation(ContainerEntity.class.getName());
			List<Class<?>> classes = new ArrayList<>();
			for (ClassInfo info : classInfos) {
				classes.add(Class.forName(info.getName()));
			}
			checkColumnName(classes);
			checkColumnLength(classes);
			checkLobIndex(classes);
			checkListFieldContainerTableName(classes);
			checkFieldDescribeOnStatic(classes);
			checkTableNameUniqueConstraintName(classes);
			checkIdCreateTimeUpdateTimeSequenceIndex(classes);
			checkEnum(classes);
		}
	}

	public static String packageName(String name) throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				if (StringUtils.equals(info.getSimpleName(), name)) {
					Class<?> cls = Class.forName(info.getName());
					Module module = cls.getAnnotation(Module.class);
					return module.packageName();
				}
			}
		}
		return null;
	}

	/*
	 * 检查数据库字段名是否是ColumnNamePrefix + fieldName
	 */
	@Test
	public static void checkColumnName(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				Column col = field.getAnnotation(Column.class);
				if (null != col) {
					if (!StringUtils.equals(JpaObject.ColumnNamePrefix + field.getName(), col.name())) {
						System.err.println(String.format("checkColumnName error: class: %s, field: %s.", cls.getName(),
								field.getName()));
					}
				}
			}
		}
	}

	/*
	 * 检查是否有将Lob类型字段增加索引
	 */
	@Test
	public static void checkLobIndex(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				Lob lob = field.getAnnotation(Lob.class);
				Index index = field.getAnnotation(Index.class);
				if ((null != lob) && (null != index)) {
					System.err.println(String.format("checkLobIndex error: class: %s, field: %s.", cls.getName(),
							field.getName()));
				}
			}
		}
	}

	/*
	 * 检查StringList从表名
	 * 
	 * @FieldDescribe("群组的个人成员.存放个人 ID.")
	 * 
	 * @PersistentCollection(fetch = FetchType.EAGER)
	 * 
	 * @OrderColumn(name = ORDERCOLUMNCOLUMN)
	 * 
	 * @ContainerTable(name = TABLE + ContainerTableNameMiddle +
	 * personList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle +
	 * personList_FIELDNAME + JoinIndexNameSuffix))
	 * 
	 * @ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix +
	 * personList_FIELDNAME)
	 * 
	 * @ElementIndex(name = TABLE + IndexNameMiddle + personList_FIELDNAME +
	 * ElementIndexNameSuffix)
	 * 
	 * @CheckPersist(allowEmpty = true, citationExists = @CitationExist(type =
	 * Person.class)) private List<String> personList;
	 */
	@Test
	public static void checkListFieldContainerTableName(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getAllFieldsList(cls);
			for (Field field : fields) {
				if (List.class.isAssignableFrom(field.getType())) {
					ContainerTable containerTable = field.getAnnotation(ContainerTable.class);
					if (null != containerTable) {
						String name = FieldUtils.readStaticField(cls, "TABLE", true).toString()
								+ JpaObject.ContainerTableNameMiddle + field.getName();
						if (!StringUtils.equals(name, containerTable.name())) {
							System.err.println(
									String.format("checkListFieldContainerTableName error: class: %s, field: %s.",
											cls.getName(), field.getName()));
						}
					}
				}
			}
		}
	}

	/*
	 * 检查是否将@FieldDescribe注解到static字段上,如果是意味着上下行搞错了
	 */
	@Test
	public static void checkFieldDescribeOnStatic(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class);
			for (Field field : fields) {
				if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
					System.err.println(String.format("checkFieldDescribeOnStatic error: class: %s, field: %s.",
							cls.getName(), field.getName()));
				}
			}
		}
	}

	/*
	 * 检查约束名中的table名称和entity类中的TABLE名称是否一致
	 */
	@Test
	public static void checkTableNameUniqueConstraintName(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			Table table = cls.getAnnotation(Table.class);
			String name = Objects.toString(FieldUtils.readStaticField(cls, "TABLE", true));
			if (!StringUtils.equals(table.name(), name)) {
				System.out.println("table name not match:" + cls);
			}
			for (UniqueConstraint u : table.uniqueConstraints()) {
				if (!StringUtils.startsWith(u.name(), table.name())) {
					System.err.println(
							String.format("checkTableNameUniqueConstraintName error: class: %s.", cls.getName()));
				}
			}
		}
	}

	/*
	 * 检查类中是否有在createTime,updateTime和sequence上的索引,这几个索引已经用约束在类上了
	 */
	@Test
	public static void checkIdCreateTimeUpdateTimeSequenceIndex(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			Field idField = FieldUtils.getField(cls, JpaObject.id_FIELDNAME, true);
			Field createTimeField = FieldUtils.getField(cls, JpaObject.createTime_FIELDNAME, true);
			Field updateTimeField = FieldUtils.getField(cls, JpaObject.updateTime_FIELDNAME, true);
			Field sequenceField = FieldUtils.getField(cls, JpaObject.sequence_FIELDNAME, true);
			if ((null != idField.getAnnotation(Index.class)) || (null != createTimeField.getAnnotation(Index.class))
					|| (null != updateTimeField.getAnnotation(Index.class))
					|| (null != sequenceField.getAnnotation(Index.class))) {
				System.err.println(
						String.format("checkIdCreateTimeUpdateTimeSequenceIndex error: class: %s.", cls.getName()));
			}
		}
	}

	/*
	 * 检查entity是否有重复的字段
	 */
	@Test
	public static void checkEnum(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class);
			for (Field field : fields) {
				if (field.getType().isEnum()) {
					Enumerated enumerated = field.getAnnotation(Enumerated.class);
					Column column = field.getAnnotation(Column.class);
					if (null == enumerated || (!Objects.equals(EnumType.STRING, enumerated.value())) || (null == column)
							|| column.length() > 200) {
						System.err.println(String.format("checkEnum error: class: %s, field: %s.", cls.getName(),
								field.getName()));
					}
				}
			}
		}
	}

	/* 检查是否有对String lob 之外的字段设定长度 */
	@Test
	public static void checkColumnLength(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			List<Field> fields = FieldUtils.getFieldsListWithAnnotation(cls, Column.class);
			for (Field field : fields) {
				if ((!String.class.isAssignableFrom(field.getType())) && (!field.getType().isEnum())) {
					Column column = field.getAnnotation(Column.class);
					if (column.length() != 255) {
						System.err.println(String.format("checkColumnLength error: class: %s, field: %s.",
								cls.getName(), field.getName()));
					}
				}
			}
		}
	}

	@Test
	public static void checkIdUnique(List<Class<?>> classes) throws Exception {
		for (Class<?> cls : classes) {
			Field idField = FieldUtils.getField(cls, JpaObject.id_FIELDNAME, true);
			Column column = idField.getAnnotation(Column.class);
			if (BooleanUtils.isNotTrue(column.unique())) {
				System.err.println(String.format("checkIdUnique error: class: %s.", cls.getName()));
			}
		}
	}
}
