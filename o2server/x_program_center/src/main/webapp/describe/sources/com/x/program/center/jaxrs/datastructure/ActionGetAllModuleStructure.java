package com.x.program.center.jaxrs.datastructure;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取所有应用的实体依赖以及数据表依赖
 */
class ActionGetAllModuleStructure extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionGetAllModuleStructure.class);

	ActionResult<List<Wo>> execute( EffectivePerson effectivePerson, HttpServletRequest request ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wo wo = null;

		//遍历所有的模块
		for( String className : OFFICIAL_MODULE_SORTED_TEMPLATE ){
			wo = getEntityStructure( className );
			wos.add( wo );
		}
		result.setData( wos );
		return result;
	}

	/**
	 * 获取指定模块的实体依赖信息
	 * @param className
	 * @return
	 */
	private Wo getEntityStructure(String className) {
		Wo wo = new Wo();
		List<WoTable> woTables = new ArrayList<>();
		wo.setModuleName( className );
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
							woTables.add( getTableStructure( containerEntity ));
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
		wo.setTables( woTables );
		return wo;
	}

	/**
	 * 获取指定实体对应的表名信息
	 * @param containerEntity
	 * @return
	 */
	private WoTable getTableStructure( String containerEntity ) {
		WoTable woTable = new WoTable();
		woTable.setEntityName( containerEntity );
		try {
			Class cls_entity = Class.forName( containerEntity );
			Class cls_annotation_table = Class.forName( "javax.persistence.Table" );
			Method cls_annotation_table_method_name = cls_annotation_table.getMethod("name");

			Annotation annotation_table = cls_entity.getAnnotation(cls_annotation_table);
			Object result = null;
			if( annotation_table != null ){
				result = cls_annotation_table_method_name.invoke(annotation_table);
				woTable.setTableName( result.toString() );
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

	public static class Wo {

		@FieldDescribe("应用模块名称")
		private String moduleName;

		@FieldDescribe("数据库表信息")
		private List<WoTable> tables;

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		public List<WoTable> getTables() {
			return tables;
		}

		public void setTables(List<WoTable> tables) {
			this.tables = tables;
		}
	}

	public static class WoTable {

		@FieldDescribe("数据库表名")
		private String tableName;

		@FieldDescribe("实体类名")
		private String entityName;

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
	}
}

