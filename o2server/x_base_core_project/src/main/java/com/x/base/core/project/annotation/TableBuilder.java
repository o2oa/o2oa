package com.x.base.core.project.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import 	org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class TableBuilder {

	private static Logger logger = LoggerFactory.getLogger(TableBuilder.class);

	public static void main(String[] args) throws IOException {
		String filePath = args[0];
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
		filePath = filePath + File.separator+"x_program_center";
		
		File basedir = new File(args[0]);
		File sourcedir = new File(args[1]);
		File dir = new File(filePath ,"src/main/webapp/describe/table");

		FileUtils.forceMkdir(dir);
		TableBuilder builder = new TableBuilder();

		builder.scan(dir,fileName);
	}

	private void scan(File dir,String fileName) {
		try {
			List<JaxrsClass> jaxrsClasses = new ArrayList<>();
			List<Class<?>> classes = this.scanJaxrsClass();
			for (Class<?> clz : classes) {
					jaxrsClasses.add(this.jaxrsClass(clz));
			}
			
			LinkedHashMap<String, List<?>> map = new LinkedHashMap<>();
			map.put("tables", jaxrsClasses);
			File file = new File(dir, fileName + ".json");
			FileUtils.writeStringToFile(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
    
			if (dir.isDirectory()) {
            	LinkedHashMap<String,String> mapList = new LinkedHashMap<>();
                File[] fs = dir.listFiles();
        		for(File f:fs){
        			if(f.isFile()) {
        				String fileNameJosn = f.getName();
        				if(!fileNameJosn.equalsIgnoreCase("tableList.json")) {
        				   mapList.put(fileNameJosn.substring(0,fileNameJosn.lastIndexOf(".")), fileNameJosn);
        				}
        			}
        		}
        	 File fileList = new File(dir, "tableList.json");
    	     FileUtils.writeStringToFile(fileList, XGsonBuilder.toJson(mapList), DefaultCharset.charset);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Class<?>> scanJaxrsClass() throws Exception {
		try (ScanResult scanResult = new ClassGraph().disableJarScanning().enableAnnotationInfo().scan()) {
			SetUniqueList<Class<?>> classes = SetUniqueList.setUniqueList(new ArrayList<Class<?>>());
		    for (ClassInfo info : scanResult.getClassesWithAnnotation(Entity.class.getName())) {
				Class<?> o = ClassUtils.getClass(info.getName());
					 Entity entity = o.getAnnotation(Entity.class);
					if (null != entity) {
						classes.add(o);
					}
				}
			return classes;
		}
	}

	private JaxrsClass jaxrsClass(Class<?> clz) throws Exception {
		logger.print("describe class:{}.", clz.getName());
		Table table = clz.getAnnotation(Table.class);
		JaxrsClass jaxrsClass = new JaxrsClass();
		jaxrsClass.setModuleName(clz.getSimpleName());
		jaxrsClass.setTableName(table.name());
		for (Field field : clz.getDeclaredFields()) {
			
			Column column = field.getAnnotation(Column.class);
			FieldDescribe fieldDescribe = field.getAnnotation(FieldDescribe.class);
			String  strFieldDescribe = "";
			if(fieldDescribe != null) {
				strFieldDescribe = fieldDescribe.value();
			}
			Lob lob = field.getAnnotation(Lob.class); 
			ContainerTable containerTable = field.getAnnotation(ContainerTable.class);
			
			if (null != column) {
				ColumnProperty	columnElement  = new ColumnProperty();
				columnElement.setName(column.name());
				if(lob != null) {
					columnElement.setType("Lob");
				}else {
					columnElement.setType(javaTypeToSqlType(field.getType().getCanonicalName()));
				}
				columnElement.setLength(column.length()+"");
				columnElement.setRemark(strFieldDescribe);
				jaxrsClass.getColumn().add(columnElement);
			}else {
				//关联表
				 if(null != containerTable) {
					 ColumnProperty	columnElement  = new ColumnProperty();
					 columnElement.setName(field.getName());
					 columnElement.setType("ContainerTable");
					 columnElement.setLength("");
					 columnElement.setRemark(strFieldDescribe);
					 
					 ContainerTableProperty containerTableProperty = new ContainerTableProperty();
					 containerTableProperty.setName(containerTable.name());
					
					 ContainerTableColumnProperty  idColumnProperty= new ContainerTableColumnProperty();
					 idColumnProperty.setName(clz.getSimpleName() + "_XID");
					 idColumnProperty.setType("VARCHAR");
					 idColumnProperty.setRemark("主键");;
					 containerTableProperty.getContainerTableColumnProperty().add(idColumnProperty);
					 
					 OrderColumn orderColumn = field.getAnnotation(OrderColumn.class);
				     if(null != orderColumn) {
						 ContainerTableColumnProperty  orderColumnProperty= new ContainerTableColumnProperty();
						 orderColumnProperty.setName(orderColumn.name());
						 orderColumnProperty.setType("INTEGER");
						 orderColumnProperty.setRemark("排序");;
						 containerTableProperty.getContainerTableColumnProperty().add(orderColumnProperty);
				     }
				     
				     ElementColumn elementColumn = field.getAnnotation(ElementColumn.class);
				     if(null != elementColumn) {
						 ContainerTableColumnProperty  elementColumnProperty= new ContainerTableColumnProperty();
						 elementColumnProperty.setName(elementColumn.name());
						 elementColumnProperty.setType("VARCHAR");
						 elementColumnProperty.setRemark("值");
						 elementColumnProperty.setLength(elementColumn.length()+"");
						 containerTableProperty.getContainerTableColumnProperty().add(elementColumnProperty);
				     }
					 columnElement.setContainerTable(containerTableProperty);
					 jaxrsClass.getColumn().add(columnElement);
				 }
				 
				
			}
		}
	   return jaxrsClass;
	}
   
	private String  javaTypeToSqlType(String javaType) {
		String sqlTye = javaType;
		if(javaType.equalsIgnoreCase("java.lang.String")) {
			sqlTye = "VARCHAR";
		}else if(javaType.equalsIgnoreCase("java.lang.byte[]")) {
			sqlTye = "BLOB";
		}else if(javaType.equalsIgnoreCase("java.lang.Long")) {
			sqlTye = "INTEGER";
		}else if(javaType.equalsIgnoreCase("java.lang.Integer")) {
			sqlTye = "INTEGER";
		}else if(javaType.equalsIgnoreCase("java.math.BigInteger")) {
			sqlTye = "BIGINT";
		}else if(javaType.equalsIgnoreCase("java.lang.Float")) {
			sqlTye = "FLOAT";
		}else if(javaType.equalsIgnoreCase("java.lang.Double")) {
			sqlTye = "DOUBLE";
		}else if(javaType.equalsIgnoreCase("java.math.BigDecimal")) {
			sqlTye = "DECIMAL";
		}else if(javaType.equalsIgnoreCase("java.lang.Integer")) {
			sqlTye = "DECIMAL";
		}else if(javaType.equalsIgnoreCase("java.sql.Date") ||  javaType.equalsIgnoreCase("java.util.Date")) {
			sqlTye = "DATE";
		}else if(javaType.equalsIgnoreCase("java.sql.Time")) {
			sqlTye = "TIME";
		}else if(javaType.equalsIgnoreCase("java.sql.Timestamp")) {
			sqlTye = "DATETIME";
		}else if(javaType.equalsIgnoreCase("java.lang.Boolean")) {
			sqlTye = "BOOLEAN";
		}
		return sqlTye;
	}
	

	public class JaxrsClass {

		private String moduleName;
		private String tableName;
		
		private List<ColumnProperty> columnProperty = new ArrayList<>();

		public String getModuleName() {
			return moduleName;
		}

		public void setModuleName(String moduleName) {
			this.moduleName = moduleName;
		}

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public List<ColumnProperty> getColumn() {
			return columnProperty;
		}

		public void setColumn(List<ColumnProperty> columnProperty) {
			this.columnProperty = columnProperty;
		}
	
	}
	
	
    public class ColumnProperty{
    	private String name;
		private String type;
		private String length;
		private String remark;
		private  ContainerTableProperty containerTableProperty;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLength() {
			return length;
		}
		public void setLength(String length) {
			this.length = length;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		
		public ContainerTableProperty getContainerTable() {
			return containerTableProperty;
		}
		public void setContainerTable(ContainerTableProperty containerTableProperty) {
			this.containerTableProperty = containerTableProperty;
		}
    }
    
    public class ContainerTableProperty{
    	private String name;
		private String remark;
		private List<ContainerTableColumnProperty> containerTableColumnProperty = new ArrayList<>();
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}

		public List<ContainerTableColumnProperty> getContainerTableColumnProperty() {
			return containerTableColumnProperty;
		}

		public void setContainerTableColumnProperty(List<ContainerTableColumnProperty> containerTableColumnProperty) {
			this.containerTableColumnProperty = containerTableColumnProperty;
		}
    }
    
    
    public class ContainerTableColumnProperty{
    	private String name;
		private String type;
		private String length;
		private String remark;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLength() {
			return length;
		}
		public void setLength(String length) {
			this.length = length;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
    }
   
}