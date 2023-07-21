package com.x.base.core.project.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

//TODO 需要重写
@Deprecated(forRemoval = true, since = "7.2")
public class DescribeWoBuilder {

	private static Logger logger = LoggerFactory.getLogger(DescribeWoBuilder.class);
	private File fileDir = null;

	public static void main(String[] args) throws IOException {

		String filePath = args[0];
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator), filePath.length());
		filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
		filePath = filePath + File.separator + "x_program_center";

		File basedir = new File(args[0]);
		File dir = new File(basedir, "src/main/webapp/describe/jsdoc");

		FileUtils.forceMkdir(dir);

		DescribeWoBuilder builder = new DescribeWoBuilder();

		builder.scan(dir, fileName);
	}

	private void scan(File dir, String fileName) {
		try {
			this.fileDir = dir;
			ArrayList<String> list = new ArrayList<>();
			// x_processplatform_assemble_surface
			list.add("ApplicationDictAction");
			list.add("AttachmentAction");
			list.add("CacheAction");
			list.add("DataAction");
			list.add("JobAction");
			list.add("ReadAction");
			list.add("RecordAction");
			list.add("ReviewAction");
			list.add("TaskAction");
			list.add("TaskCompletedAction");
			list.add("WorkAction");
			list.add("WorkCompletedAction");
			list.add("ReadCompletedAction");
			list.add("WorkLogAction");

			// x_cms_assemble_control
			list.add("AppInfoAction");
			list.add("CategoryInfoAction");
			list.add("DocumentAction");
			list.add("DocumentCommendAction");
			list.add("DocumentCommentInfoAction");
			list.add("DocumentViewRecordAction");
			list.add("FileInfoAction");

			// x_organization_assemble_express
			list.add("GroupAction");
			list.add("IdentityAction");
			list.add("PersonAction");
			list.add("PersonAttributeAction");
			list.add("RoleAction");
			list.add("UnitAction");
			list.add("UnitAttributeAction");
			list.add("UnitDutyAction");

			List<JaxrsClass> jaxrsClasses = new ArrayList<>();
			List<Class<?>> classes = this.scanJaxrsClass();
			for (Class<?> clz : classes) {
				if (list.contains(clz.getSimpleName())) {
					if (StandardJaxrsAction.class.isAssignableFrom(clz)) {
						JaxrsClass jarsClass = this.jaxrsClass(clz);
						List<JaxrsMethod> methods = jarsClass.getMethods();
						if (methods.size() > 0) {
							jaxrsClasses.add(jarsClass);
						}
					}
				}
			}

			jaxrsClasses = jaxrsClasses.stream().sorted(Comparator.comparing(JaxrsClass::getName))
					.collect(Collectors.toList());

			for (JaxrsClass jaxr : jaxrsClasses) {
				File file = new File(dir, jaxr.getName() + ".json");
				FileUtils.writeStringToFile(file, XGsonBuilder.toJson(jaxr), DefaultCharset.charset);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Class<?>> scanJaxrsClass() throws Exception {
		try (ScanResult scanResult = new ClassGraph().disableJarScanning().enableAnnotationInfo().scan()) {
			SetUniqueList<Class<?>> classes = SetUniqueList.setUniqueList(new ArrayList<Class<?>>());
			for (ClassInfo info : scanResult.getClassesWithAnnotation(ApplicationPath.class.getName())) {
				Class<?> applicationPathClass = ClassUtils.getClass(info.getName());
				for (Class<?> o : (Set<Class<?>>) MethodUtils.invokeMethod(applicationPathClass.newInstance(),
						"getClasses")) {
					Path path = o.getAnnotation(Path.class);
					JaxrsDescribe jaxrsDescribe = o.getAnnotation(JaxrsDescribe.class);
					if (null != path && null != jaxrsDescribe) {
						classes.add(o);
					}
				}
			}
			return classes;
		}
	}

	private JaxrsClass jaxrsClass(Class<?> clz) throws Exception {
		logger.print("describe class:{}.", clz.getName());
		JaxrsDescribe jaxrsDescribe = clz.getAnnotation(JaxrsDescribe.class);
		JaxrsClass jaxrsClass = new JaxrsClass();
		jaxrsClass.setClassName(clz.getName());
		jaxrsClass.setName(clz.getSimpleName());
		jaxrsClass.setDescription(jaxrsDescribe.value());

		for (Method method : clz.getMethods()) {
			JaxrsMethodDescribe jaxrsMethodDescribe = method.getAnnotation(JaxrsMethodDescribe.class);
			if (null != jaxrsMethodDescribe) {
				jaxrsClass.getMethods().add(this.jaxrsMethod(clz, method));
			}
		}
		jaxrsClass.setMethods(jaxrsClass.getMethods().stream().sorted(Comparator.comparing(JaxrsMethod::getName))
				.collect(Collectors.toList()));

		return jaxrsClass;
	}

	private JaxrsMethod jaxrsMethod(Class<?> clz, Method method) throws Exception {
		JaxrsMethodDescribe jaxrsMethodDescribe = method.getAnnotation(JaxrsMethodDescribe.class);
		JaxrsMethod jaxrsMethod = new JaxrsMethod();
		jaxrsMethod.setName(method.getName());
		jaxrsMethod.setDescription(jaxrsMethodDescribe.value());
		Class<?> actionClass = jaxrsMethodDescribe.action();
		jaxrsMethod.setClassName(actionClass.getName());
		if (null != method.getAnnotation(GET.class)) {
			jaxrsMethod.setType("GET");
		} else if (null != method.getAnnotation(POST.class)) {
			jaxrsMethod.setType("POST");
		} else if (null != method.getAnnotation(PUT.class)) {
			jaxrsMethod.setType("PUT");
		} else if (null != method.getAnnotation(DELETE.class)) {
			jaxrsMethod.setType("DELETE");
		} else if (null != method.getAnnotation(OPTIONS.class)) {
			jaxrsMethod.setType("OPTIONS");
		} else if (null != method.getAnnotation(HEAD.class)) {
			jaxrsMethod.setType("HEAD");
		}

		Class<?> woClass = this.getWoClass(actionClass);

		if (null != woClass) {
			jaxrsMethod.setOuts(this.jaxrsOutField(woClass));
		}

		return jaxrsMethod;
	}

//	private JaxrsFormParameter jaxrsFormDataParameter(Class<?> clz, Method method, Parameter parameter) {
//		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
//		FormDataParam formDataParam = parameter.getAnnotation(FormDataParam.class);
//		if (StringUtils.equalsIgnoreCase("file", formDataParam.value())) {
//			if (parameter.getType() == FormDataContentDisposition.class) {
//				/** 单独处理附件 */
//				JaxrsFormParameter o = new JaxrsFormParameter();
//				o.setType("File");
//				o.setName(formDataParam.value());
//				if (null != jaxrsParameterDescribe) {
//					o.setDescription(jaxrsParameterDescribe.value());
//				} else {
//					logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
//							formDataParam.value());
//					o.setDescription("");
//				}
//				return o;
//			}
//		} else {
//			JaxrsFormParameter o = new JaxrsFormParameter();
//			o.setType(this.simpleType(parameter.getType().toString()));
//			o.setName(formDataParam.value());
//			if (null != jaxrsParameterDescribe) {
//				o.setDescription(jaxrsParameterDescribe.value());
//			} else {
//				logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
//						formDataParam.value());
//				o.setDescription("");
//			}
//			return o;
//		}
//		return null;
//	}

//	private JaxrsFormParameter jaxrsFormParameter(Class<?> clz, Method method, Parameter parameter) {
//		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
//		FormParam formParam = parameter.getAnnotation(FormParam.class);
//		JaxrsFormParameter o = new JaxrsFormParameter();
//		o.setType(this.simpleType(parameter.getType().toString()));
//		o.setName(formParam.value());
//		if (null != jaxrsParameterDescribe) {
//			o.setDescription(jaxrsParameterDescribe.value());
//		} else {
//			logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
//					formParam.value());
//			o.setDescription("");
//		}
//		return o;
//	}
//
//	private JaxrsQueryParameter jaxrsQueryParameter(Class<?> clz, Method method, Parameter parameter) {
//		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
//		QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
//		JaxrsQueryParameter o = new JaxrsQueryParameter();
//		if (null != jaxrsParameterDescribe) {
//			o.setDescription(jaxrsParameterDescribe.value());
//		} else {
//			logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
//					queryParam.value());
//			o.setDescription("");
//		}
//		o.setName(queryParam.value());
//		o.setType(this.simpleType(parameter.getType().getName()));
//		return o;
//	}

	private JaxrsPathParameter jaxrsPathParameter(Class<?> clz, Method method, Parameter parameter) throws Exception {
		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
		PathParam pathParam = parameter.getAnnotation(PathParam.class);
		JaxrsPathParameter o = new JaxrsPathParameter();
		o.setName(pathParam.value());
		if (null != jaxrsParameterDescribe) {
			o.setDescription(jaxrsParameterDescribe.value());
		} else {
			logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
					pathParam.value());
			o.setDescription("");
		}
		o.setType(this.getJaxrsParameterType(parameter));
		return o;
	}

//	private Class<?> getWiClass(Class<?> actionClass) {
//		for (Class<?> c : actionClass.getDeclaredClasses()) {
//			if (StringUtils.equals(c.getSimpleName(), "Wi")) {
//				return c;
//			}
//		}
//		return null;
//	}

	private Class<?> getWoClass(Class<?> actionClass) {
		for (Class<?> c : actionClass.getDeclaredClasses()) {
			if ((null != c.getAnnotation(WrapOut.class)) || StringUtils.equals(c.getSimpleName(), "Wo")) {
				return c;
			}
		}
		return null;
	}

//	private List<JaxrsField> jaxrsInField(Class<?> clz) throws Exception {
//		List<JaxrsField> list = new ArrayList<>();
//		List<Field> fields = FieldUtils.getAllFieldsList(clz);
//		List<String> copierCopyFields = this.listCopierCopyFields(clz);
//		if (ListTools.isNotEmpty(copierCopyFields)) {
//			List<Field> os = new ArrayList<>();
//			for (Field o : fields) {
//				FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
//				if ((null != fieldDescribe)
//						&& (copierCopyFields.contains(o.getName()) || this.inWiNotInEntity(o.getName(), clz))) {
//					os.add(o);
//				}
//				fields = os;
//			}
//		}
//		for (Field o : fields) {
//			FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
//			if (null != fieldDescribe) {
//				JaxrsField jaxrsField = new JaxrsField();
//				jaxrsField.setName(o.getName());
//				jaxrsField.setDescription(fieldDescribe.value());
//				String className = getClassName(o);
//				jaxrsField.setType(this.getJaxrsFieldType(o, className));
//				jaxrsField.setIsBaseType(false);
//				if (Collection.class.isAssignableFrom(o.getType())) {
//					jaxrsField.setIsCollection(true);
//					if (StringUtils.containsAny(jaxrsField.getType(), "<String>", "<Boolean>", "<Date>", "<Integer>",
//							"<Double>", "<Long>", "<Float>")) {
//						jaxrsField.setIsBaseType(true);
//					} else {
//						// wwx add 获取类信息
//						FieldTypeDescribe fieldTypeDescribe = o.getAnnotation(FieldTypeDescribe.class);
//						if (null != fieldTypeDescribe) {
//							jaxrsField.setFieldType(fieldTypeDescribe.fieldType());
//							jaxrsField.setFieldValue(fieldTypeDescribe.fieldValue());
//							jaxrsField.setFieldTypeName(fieldTypeDescribe.fieldTypeName());
//							jaxrsField.setFieldSample(fieldTypeDescribe.fieldSample());
//						}
//
//					}
//				} else {
//					// O2LEE，String[]未被判断为collection导致组织的JSON格式不符合wrapIn要求
//					if (StringUtils.equalsAnyIgnoreCase("String[]", jaxrsField.getType())) {
//						jaxrsField.setIsCollection(true);
//					} else {
//						jaxrsField.setIsCollection(false);
//					}
//					if (StringUtils.startsWithAny(jaxrsField.getType(), "String", "Boolean", "Date", "Integer",
//							"Double", "Long", "Float")) {
//						jaxrsField.setIsBaseType(true);
//					} else {
//						FieldTypeDescribe fieldTypeDescribe = o.getAnnotation(FieldTypeDescribe.class);
//						if (null != fieldTypeDescribe) {
//							jaxrsField.setFieldType(fieldTypeDescribe.fieldType());
//							jaxrsField.setFieldValue(fieldTypeDescribe.fieldValue());
//							jaxrsField.setFieldTypeName(fieldTypeDescribe.fieldTypeName());
//						}
//
//					}
//				}
//				list.add(jaxrsField);
//			}
//		}
//		return list;
//	}

	private List<JaxrsField> jaxrsOutField(Class<?> clz) throws Exception {

		List<JaxrsField> list = new ArrayList<>();
		List<Field> fields = FieldUtils.getAllFieldsList(clz);
		List<String> copierEraseFields = this.listCopierEraseFields(clz);
		if (ListTools.isNotEmpty(copierEraseFields)) {
			List<Field> os = new ArrayList<>();
			for (Field o : fields) {
				FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
				if ((null != fieldDescribe) && (!copierEraseFields.contains(o.getName()))) {
					os.add(o);
				}
			}
			fields = os;
		}
		for (Field o : fields) {
			FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
			if (null != fieldDescribe) {
				JaxrsField jaxrsField = new JaxrsField();
				jaxrsField.setName(o.getName());
				jaxrsField.setDescription(fieldDescribe.value());
				String className = getClassName(o);
				jaxrsField.setType(this.getJaxrsFieldType(o, className));

				if (Collection.class.isAssignableFrom(o.getType())) {
					jaxrsField.setIsCollection(true);
				} else {
					jaxrsField.setIsCollection(false);
				}
				list.add(jaxrsField);
			}
		}
		return list;
	}

	private String getClassName(Field o) {
		String typeName = o.getGenericType().getTypeName();
		String value = this.simpleType(typeName);
		List<String> list = new ArrayList<>();
		list.add("Date");
		list.add("String");
		list.add("Boolean");
		list.add("Long");
		list.add("long");
		list.add("int");
		list.add("Integer");
		list.add("Double");
		list.add("List");
		list.add("List<K>");
		list.add("Map");
		list.add("Map<String,String>");
		list.add("Map<String,Object>");
		list.add("Map<K,V>");
		list.add("Map<?,?>");
		list.add("byte[]");
		list.add("Class");
		list.add("Class[]");
		list.add("Object");
		list.add("String[]");
		list.add("List<String>");
		list.add("List<Date>");
		list.add("List<Boolean>");
		list.add("List<Long>");
		list.add("List<Integer>");
		list.add("List<Double>");
		list.add("List<byte[]>");
		if (!list.contains(value)) {
			if (typeName.indexOf("java.util.List<") > -1) {
				String[] ss = typeName.split("[,|<|>]");
				typeName = ss[ss.length - 1];
				value = typeName;
			}
		}
		return value;
	}

	private String getJaxrsFieldType(Field o, String classNameParent) {
		String typeName = o.getGenericType().getTypeName();
		String value = this.simpleType(typeName);
		ArrayList<String> list = new ArrayList<>();
		list.add("Date");
		list.add("String");
		list.add("Boolean");
		list.add("Long");
		list.add("long");
		list.add("int");
		list.add("Integer");
		list.add("Double");
		list.add("List");
		list.add("List<K>");
		list.add("Map");
		list.add("Map<String,String>");
		list.add("Map<String,Object>");
		list.add("Map<K,V>");
		list.add("Map<?,?>");
		list.add("byte[]");
		list.add("Class");
		list.add("Class[]");
		list.add("Object");
		list.add("String[]");
		list.add("List<String>");
		list.add("List<Date>");
		list.add("List<Boolean>");
		list.add("List<Long>");
		list.add("List<Integer>");
		list.add("List<Double>");
		list.add("List<byte[]>");
		boolean listParam = false;
		if (!list.contains(value)) {
			try {
				if (typeName.indexOf("java.util.List<") > -1) {
					String[] ss = typeName.split("[,|<|>]");
					typeName = ss[ss.length - 1];
					listParam = true;
				}
				Class<?> clz = null;
				try {
					clz = Thread.currentThread().getContextClassLoader().loadClass(typeName);
				} catch (Exception e) {
					// nothing
				}
				if (null != clz) {
					if (!clz.isEnum()) {
						// 不是枚举类型
						List<Field> fields = FieldUtils.getAllFieldsList(clz);
						List<JaxrsField> jaxrsFieldList = new ArrayList<>();
						for (Field field : fields) {
							if (!listParam) {
								FieldDescribe fieldDescribe = field.getAnnotation(FieldDescribe.class);
								if (null != fieldDescribe) {
									JaxrsField jaxrsField = new JaxrsField();
									jaxrsField.setName(field.getName());
									jaxrsField.setDescription(fieldDescribe.value());

									String className = getClassName(field);
									if (classNameParent.equalsIgnoreCase(getClassName(field))) {
										jaxrsField.setType(className); // 防止死循环
									} else {
										jaxrsField.setType(this.getJaxrsFieldType(field, className));
									}

									if (Collection.class.isAssignableFrom(field.getType())) {
										jaxrsField.setIsCollection(true);
									} else {
										jaxrsField.setIsCollection(false);
									}
									jaxrsFieldList.add(jaxrsField);
								} else {
									JaxrsField jaxrsField = new JaxrsField();
									jaxrsField.setName(field.getName());
									jaxrsField.setDescription("");
									String className = getClassName(field);
									jaxrsField.setType(this.getJaxrsFieldType(field, className));
									if (Collection.class.isAssignableFrom(field.getType())) {
										jaxrsField.setIsCollection(true);
									} else {
										jaxrsField.setIsCollection(false);
									}
									jaxrsFieldList.add(jaxrsField);
								}
							} else {
								// 创建List参数中的类型
								FieldDescribe fieldDescribe = field.getAnnotation(FieldDescribe.class);
								JaxrsField jaxrsField = new JaxrsField();
								jaxrsField.setName(field.getName());
								if (null != fieldDescribe) {
									jaxrsField.setDescription(fieldDescribe.value());
								} else {
									jaxrsField.setDescription("");
								}
								String className = getClassName(o);
								if (classNameParent.equalsIgnoreCase(getClassName(field))) {
									jaxrsField.setType(className); // 防止死循环
								} else {
									jaxrsField.setType(this.getJaxrsFieldType(field, className));
								}
								if (Collection.class.isAssignableFrom(field.getType())) {
									jaxrsField.setIsCollection(true);
								} else {
									jaxrsField.setIsCollection(false);
								}
								jaxrsFieldList.add(jaxrsField);
							}
						}
						File file = new File(this.fileDir, this.simpleType(typeName) + ".json");
						FileUtils.writeStringToFile(file, XGsonBuilder.toJson(list), DefaultCharset.charset);
					} else {
						List<JaxrsField> jaxrsFieldList = new ArrayList<>();
						Enum<?>[] enumConstants = (Enum[]) clz.getEnumConstants();
						String enumName = "";
						if (enumConstants != null) {
							for (Enum<?> enumConstant : enumConstants) {
								if (enumName.equalsIgnoreCase("")) {
									enumName = enumConstant.name();
								} else {
									enumName = enumName + "," + enumConstant.name();
								}
							}
							JaxrsField jaxrsField = new JaxrsField();
							jaxrsField.setName(clz.getSimpleName());
							jaxrsField.setDescription(enumName);
							jaxrsField.setType("Enum");
							jaxrsField.setIsCollection(false);
							jaxrsFieldList.add(jaxrsField);
							File file = new File(this.fileDir, value + ".json");
							FileUtils.writeStringToFile(file, XGsonBuilder.toJson(list), DefaultCharset.charset);
						}
					}
				}
			} catch (Exception e) {
				logger.info("getJaxrsFieldType error:" + e.getMessage());
			}
		}
		return value;
	}

	private String getJaxrsParameterType(Parameter o) {
		String value = o.getType().getTypeName();
		return this.simpleType(value);
	}

	private String simpleType(String value) {
		value = value.replaceAll(" ", "");
		String[] ss = value.split("[,|<|>]");
		for (String s : ss) {
			String[] ns = s.split("[.|\\$]");
			value = value.replace(s, ns[ns.length - 1]);
		}
		return value;
	}

	private List<String> listCopierEraseFields(Class<?> clz) {
		try {
			Object o = FieldUtils.readStaticField(clz, "copier", true);
			WrapCopier copier = (WrapCopier) o;
			return copier.getEraseFields();
		} catch (Exception e) {
			return null;
		}
	}

	private class JaxrsClass {

		private String name;
		private String className;
		private String description;
		private List<JaxrsMethod> methods = new ArrayList<>();

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public List<JaxrsMethod> getMethods() {
			return methods;
		}

		public void setMethods(List<JaxrsMethod> methods) {
			this.methods = methods;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private class JaxrsMethod {
		private String name;
		private String className;
		private String description;
		private String type;
		private String path;

		private List<JaxrsField> outs = new ArrayList<>();

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<JaxrsField> getOuts() {
			return outs;
		}

		public void setOuts(List<JaxrsField> outs) {
			this.outs = outs;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

	}

	private class JaxrsField {

		private String name;
		private String type;
		private Boolean isCollection;
		private String description;
		private Boolean isBaseType;

		// 当参数不是基础类型时，记录类型信息
		private String fieldType;
		private String fieldValue;
		private String fieldTypeName;
		private String fieldSample;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Boolean getIsCollection() {
			return isCollection;
		}

		public void setIsCollection(Boolean isCollection) {
			this.isCollection = isCollection;
		}

		public Boolean getIsBaseType() {
			return isBaseType;
		}

		public void setIsBaseType(Boolean isBaseType) {
			this.isBaseType = isBaseType;
		}

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldTyp) {
			this.fieldType = fieldTyp;
		}

		public String getFieldValue() {
			return fieldValue;
		}

		public void setFieldValue(String fieldValue) {
			this.fieldValue = fieldValue;
		}

		public String getFieldTypeName() {
			return fieldTypeName;
		}

		public void setFieldTypeName(String fieldTypeName) {
			this.fieldTypeName = fieldTypeName;
		}

		public String getFieldSample() {
			return fieldSample;
		}

		public void setFieldSample(String fieldSample) {
			this.fieldSample = fieldSample;
		}
	}

	private class JaxrsPathParameter {

		private String name;
		private String type;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	private class JaxrsFormParameter {

		private String name;
		private String type;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	private class JaxrsQueryParameter {

		private String name;
		private String type;
		private String description;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

}