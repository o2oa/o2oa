package com.x.program.center.jaxrs.datastructure;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取所有应用的数据表结构
 */
class ActionGetAllTableStructure extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetAllTableStructure.class);

	/**
	 * 已经分析过的数据表
	 */
	private List<String> analyzedEntityNames = new ArrayList<>();

	ActionResult<List<Wo>> execute( EffectivePerson effectivePerson, HttpServletRequest request ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		List<Wo> wos_newTables = null;
		Wo wo = null;

		//遍历所有的模块
		for( String className : OFFICIAL_MODULE_SORTED_TEMPLATE ){
			wos_newTables = getDataStructure( className );
			wos.addAll( wos_newTables );
		}
		result.setData( wos );
		return result;
	}

	private List<Wo> getDataStructure(String className) {
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;
		try {
			Class cls = Class.forName( className );
			Class cls_annotation_moudle = Class.forName( "com.x.base.core.project.annotation.Module" );
			Method method_containerEntities = cls_annotation_moudle.getMethod("containerEntities");

			Annotation annotation = cls.getAnnotation(cls_annotation_moudle);
			Object result = null;
			if( annotation != null ){
				result = method_containerEntities.invoke(annotation);
				if( result != null ){
					String[] containerEntities = (String[])result;
					if( containerEntities != null && containerEntities.length > 0 ){
						for( String containerEntity : containerEntities ){
							if( !analyzedEntityNames.contains( containerEntity ) ){
								wo = getTableStructure( containerEntity );
								if( wo != null ){
									wos.add( wo );
									analyzedEntityNames.add( containerEntity );
								}
							}
						}
					}
				}
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return wos;
	}

	private Wo getTableStructure( String containerEntity ) {
		Wo woTable = null;
		List<WoField> woFields = new ArrayList<>();
		try {
			Class cls_entity = Class.forName( containerEntity );
			Class cls_annotation_table = Class.forName( "javax.persistence.Table" );
			Method cls_annotation_table_method_name = cls_annotation_table.getMethod("name");

			Annotation annotation_table = cls_entity.getAnnotation(cls_annotation_table);
			Object result = null;
			if( annotation_table != null ){
				woTable = new Wo();
				woTable.setEntityName( containerEntity );
				result = cls_annotation_table_method_name.invoke(annotation_table);
				woTable.setTableName( result.toString() );

				//遍历所有的有@Column的属性
				Field[] fileds = cls_entity.getDeclaredFields();
				if( fileds != null && fileds.length > 0 ){
					for( Field field : fileds ){
						WoField wofield = getFieldStructure( field );
						if( wofield != null ) {
							woFields.add( wofield );
						}
					}
				}
				woTable.setFields( woFields );
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			logger.info("无法解析实体类" + containerEntity + ",请检查类依赖情况。" );
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return woTable;
	}

	private WoField getFieldStructure(Field field) {
		WoField woField = null;
		Object result = null;
		try {
			Class cls_annotation_field_column = Class.forName("javax.persistence.Column");
			Class cls_annotation_field_describe = Class.forName("com.x.base.core.project.annotation.FieldDescribe");
			Method cls_annotation_field_method_name = cls_annotation_field_describe.getMethod("value");
			Method cls_annotation_field_column_legnth = cls_annotation_field_column.getMethod("length");
			Method cls_annotation_field_column_name = cls_annotation_field_column.getMethod("name");

			Annotation annotation_field_column = field.getAnnotation(cls_annotation_field_column);
			Annotation annotation_field_describe = null;

			if (annotation_field_column != null) {//说明有@Column这个注解，是一个列，开始
				annotation_field_describe = field.getAnnotation(cls_annotation_field_describe);
				if (annotation_field_describe != null) {
					woField = new WoField();
					result = cls_annotation_field_method_name.invoke(annotation_field_describe);
					if (result != null) {
						woField.setDescription( result.toString() );
						woField.setFieldType( field.getType().getSimpleName() );
					}

					result = cls_annotation_field_column_legnth.invoke(annotation_field_column);
					if (result != null) {
						woField.setFieldLength( result.toString() );
					}

					result = cls_annotation_field_column_name.invoke(annotation_field_column);
					if (result != null) {
						woField.setFieldName(result.toString());
					}
				}
			}
		} catch (ClassNotFoundException | NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return woField;
	}

	public static class Wo {

		@FieldDescribe("数据库表名")
		private String tableName;

		@FieldDescribe("实体类名")
		private String entityName;

		@FieldDescribe("数据表列信息")
		private List<WoField> fields;

		public String getEntityName() {
			return entityName;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public List<WoField> getFields() {
			return fields;
		}

		public void setFields(List<WoField> fields) {
			this.fields = fields;
		}
	}

	public static class WoField {

		@FieldDescribe("数据表列名")
		private String fieldName;

		@FieldDescribe("数据表列类型")
		private String fieldType;

		@FieldDescribe("数据表列长度")
		private String fieldLength;

		@FieldDescribe("数据表列说明")
		private String description;

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
		}

		public String getFieldLength() {
			return fieldLength;
		}

		public void setFieldLength(String fieldLength) {
			this.fieldLength = fieldLength;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}
}

