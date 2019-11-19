package com.x.base.core.project.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.ApiBuilder.JaxrsApiMethod;
import com.x.base.core.project.annotation.DescribeBuilder.JaxrsMethod;
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

public class ApiBuilder {

	private static Logger logger = LoggerFactory.getLogger(ApiBuilder.class);

	public static void main(String[] args) throws IOException {
		//System.out.println("ApiBuilder......");
		File basedir = new File(args[0]);
		File sourcedir = new File(args[1]);
		
		File dir = new File(basedir, "src/main/webapp/describe");

		FileUtils.forceMkdir(dir);

		ApiBuilder builder = new ApiBuilder();

		builder.scan(dir);

		FileUtils.copyDirectory(sourcedir, new File(dir, "sources"));

	}

	private void scan(File dir) {
		try {
			List<JaxrsClass> jaxrsClasses = new ArrayList<>();
			List<Class<?>> classes = this.scanJaxrsClass();
			for (Class<?> clz : classes) {
				if (StandardJaxrsAction.class.isAssignableFrom(clz)) {
					jaxrsClasses.add(this.jaxrsClass(clz));
				}
			}
			
			
			LinkedHashMap<String, List<?>> map = new LinkedHashMap<>();
			jaxrsClasses = jaxrsClasses.stream().sorted(Comparator.comparing(JaxrsClass::getName))
					.collect(Collectors.toList());
			map.put("jaxrs", jaxrsClasses);
			File file = new File(dir, "api.json");
			FileUtils.writeStringToFile(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		//jaxrsClass.setClassName(clz.getName());
		jaxrsClass.setName(clz.getSimpleName());
		//jaxrsClass.setDescription(jaxrsDescribe.value());
		for (Method method : clz.getMethods()) {
			JaxrsMethodDescribe jaxrsMethodDescribe = method.getAnnotation(JaxrsMethodDescribe.class);
			if (null != jaxrsMethodDescribe) {
				/*
				Map<String, JaxrsApiMethod> map = new HashMap<String,JaxrsApiMethod>();
				map.put(method.getName(), this.jaxrsApiMethod(clz, method));
				jaxrsClass.getMethods().add(map);
				*/
				jaxrsClass.getMethods().add(this.jaxrsApiMethod(clz, method));
			}
		}
		
		//jaxrsClass.setMethods(this.getSortData("name",jaxrsClass.getMethods()));
		
		return jaxrsClass;
		
	}
   

   private List<Map<String, JaxrsApiMethod>> getSortData(String indicator, List<Map<String, JaxrsApiMethod>> data) {
        class MapSort implements Comparator<Map<String, JaxrsApiMethod>> {
            private String keyName = "";
            private MapSort(String keyName) {
                this.keyName = keyName;
            }
            public int compare(Map<String, JaxrsApiMethod> mp1, Map<String, JaxrsApiMethod> mp2) {
            	 System.out.println("this.keyName=" + mp1.keySet().toArray()[0]);
                 String d1 = mp1.keySet().toArray()[0].toString();
                 String d2 = mp2.keySet().toArray()[0].toString();
                return d2.compareTo(d1);
            }
        }
        MapSort mapSort = new MapSort(indicator);
        Collections.sort(data, mapSort);
        return data;
    }

	private JaxrsApiMethod jaxrsApiMethod(Class<?> clz, Method method) throws Exception {
		JaxrsMethodDescribe jaxrsMethodDescribe = method.getAnnotation(JaxrsMethodDescribe.class);
		JaxrsApiMethod jaxrsMethod = new JaxrsApiMethod();
		jaxrsMethod.setName(method.getName());
		//jaxrsMethod.setDescription(jaxrsMethodDescribe.value());
		Class<?> actionClass = jaxrsMethodDescribe.action();
		//jaxrsMethod.setClassName(actionClass.getName());
		if (null != method.getAnnotation(GET.class)) {
			jaxrsMethod.setMethod("GET");
		} else if (null != method.getAnnotation(POST.class)) {
			jaxrsMethod.setMethod("POST");
		} else if (null != method.getAnnotation(PUT.class)) {
			jaxrsMethod.setMethod("PUT");
		} else if (null != method.getAnnotation(DELETE.class)) {
			jaxrsMethod.setMethod("DELETE");
		} else if (null != method.getAnnotation(OPTIONS.class)) {
			jaxrsMethod.setMethod("OPTIONS");
		} else if (null != method.getAnnotation(HEAD.class)) {
			jaxrsMethod.setMethod("HEAD");
		}
		
		if (!jaxrsMethod.getMethod().equalsIgnoreCase("GET")) {
			Consumes consumes = method.getAnnotation(Consumes.class);
			if (null != consumes) {
				if(consumes.value()[0].equals("multipart/form-data")) {
				   jaxrsMethod.setEnctype("formData");
				}else {
					jaxrsMethod.setEnctype(consumes.value()[0]);
				}
			} else {
				//jaxrsMethod.setEnctype(MediaType.APPLICATION_JSON);
			}
		}
		
		
		Path path = method.getAnnotation(Path.class);
		if (null == path) {
			jaxrsMethod.setUri("jaxrs/" + clz.getAnnotation(Path.class).value());
		} else {
			jaxrsMethod.setUri("jaxrs/" + clz.getAnnotation(Path.class).value() + "/" + path.value());
		}

		return jaxrsMethod;
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
		Class<?> wiClass = this.getWiClass(actionClass);
		if (null != wiClass) {
			jaxrsMethod.setIns(this.jaxrsInField(wiClass));
		} else {
			if (StringUtils.equals("POST", jaxrsMethod.getType()) || StringUtils.equals("PUT", jaxrsMethod.getType())) {
				/** 如果没有定义Wi对象,那么有可能使用的是jsonElement对象 */
				if (ArrayUtils.contains(method.getParameterTypes(), JsonElement.class)) {
					jaxrsMethod.setUseJsonElementParameter(true);
				} else {
					jaxrsMethod.setUseStringParameter(true);
				}
			}
		}
		Consumes consumes = method.getAnnotation(Consumes.class);
		if (null != consumes) {
			jaxrsMethod.setContentType(consumes.value()[0]);
		} else {
			jaxrsMethod.setContentType(MediaType.APPLICATION_JSON);
		}
		Produces produces = method.getAnnotation(Produces.class);
		if (null != produces) {
			jaxrsMethod.setResultContentType(produces.value()[0]);
			jaxrsMethod.setResultContentType(produces.value()[0]);
		}
		Path path = method.getAnnotation(Path.class);
		if (null == path) {
			jaxrsMethod.setPath("jaxrs/" + clz.getAnnotation(Path.class).value());
		} else {
			jaxrsMethod.setPath("jaxrs/" + clz.getAnnotation(Path.class).value() + "/" + path.value());
		}
		for (Parameter o : method.getParameters()) {
			FormDataParam formDataParam = o.getAnnotation(FormDataParam.class);
			FormParam formParam = o.getAnnotation(FormParam.class);
			PathParam pathParam = o.getAnnotation(PathParam.class);
			QueryParam queryParam = o.getAnnotation(QueryParam.class);
			if (null != formDataParam) {
				jaxrsMethod.getFormParameters().add(this.jaxrsFormDataParameter(clz, method, o));
			} else if (null != formParam) {
				jaxrsMethod.getFormParameters().add(this.jaxrsFormParameter(clz, method, o));
			} else if (null != queryParam) {
				jaxrsMethod.getQueryParameters().add(this.jaxrsQueryParameter(clz, method, o));
			} else if (null != pathParam) {
				jaxrsMethod.getPathParameters().add(this.jaxrsPathParameter(clz, method, o));
			}
		}
		jaxrsMethod.setFormParameters(jaxrsMethod.getFormParameters().stream().filter(Objects::nonNull)
				.sorted(Comparator.comparing(JaxrsFormParameter::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList()));
		jaxrsMethod.setQueryParameters(jaxrsMethod.getQueryParameters().stream().filter(Objects::nonNull)
				.sorted(Comparator.comparing(JaxrsQueryParameter::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList()));
		jaxrsMethod.setPathParameters(jaxrsMethod.getPathParameters().stream().filter(Objects::nonNull)
				.sorted(Comparator.comparing(JaxrsPathParameter::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList()));
		return jaxrsMethod;
	}

	private JaxrsFormParameter jaxrsFormDataParameter(Class<?> clz, Method method, Parameter parameter) {
		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
		FormDataParam formDataParam = parameter.getAnnotation(FormDataParam.class);
		if (StringUtils.equalsIgnoreCase("file", formDataParam.value())) {
			if (parameter.getType() == FormDataContentDisposition.class) {
				/** 单独处理附件 */
				JaxrsFormParameter o = new JaxrsFormParameter();
				o.setType("File");
				o.setName(formDataParam.value());
				if (null != jaxrsParameterDescribe) {
					o.setDescription(jaxrsParameterDescribe.value());
				} else {
					logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
							formDataParam.value());
					o.setDescription("");
				}
				return o;
			}
		} else {
			JaxrsFormParameter o = new JaxrsFormParameter();
			o.setType(this.simpleType(parameter.getType().toString()));
			o.setName(formDataParam.value());
			if (null != jaxrsParameterDescribe) {
				o.setDescription(jaxrsParameterDescribe.value());
			} else {
				logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
						formDataParam.value());
				o.setDescription("");
			}
			return o;
		}
		return null;
	}

	private JaxrsFormParameter jaxrsFormParameter(Class<?> clz, Method method, Parameter parameter) {
		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
		FormParam formParam = parameter.getAnnotation(FormParam.class);
		JaxrsFormParameter o = new JaxrsFormParameter();
		o.setType(this.simpleType(parameter.getType().toString()));
		o.setName(formParam.value());
		if (null != jaxrsParameterDescribe) {
			o.setDescription(jaxrsParameterDescribe.value());
		} else {
			logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
					formParam.value());
			o.setDescription("");
		}
		return o;
	}

	private JaxrsQueryParameter jaxrsQueryParameter(Class<?> clz, Method method, Parameter parameter) {
		JaxrsParameterDescribe jaxrsParameterDescribe = parameter.getAnnotation(JaxrsParameterDescribe.class);
		QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
		JaxrsQueryParameter o = new JaxrsQueryParameter();
		if (null != jaxrsParameterDescribe) {
			o.setDescription(jaxrsParameterDescribe.value());
		} else {
			logger.print("类: {}, 方法: {} ,未设置参数 {} 的JaxrsParameterDescribe.", clz.getName(), method.getName(),
					queryParam.value());
			o.setDescription("");
		}
		o.setName(queryParam.value());
		o.setType(this.simpleType(parameter.getType().getName()));
		return o;
	}

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

	private Class<?> getWiClass(Class<?> actionClass) {
		for (Class<?> c : actionClass.getDeclaredClasses()) {
			if (StringUtils.equals(c.getSimpleName(), "Wi")) {
				return c;
			}
		}
		return null;
	}

	private Class<?> getWoClass(Class<?> actionClass) {
		for (Class<?> c : actionClass.getDeclaredClasses()) {
			if (StringUtils.equals(c.getSimpleName(), "Wo")) {
				return c;
			}
		}
		return null;
	}

	private List<JaxrsField> jaxrsInField(Class<?> clz) throws Exception {
		List<JaxrsField> list = new ArrayList<>();
		List<Field> fields = FieldUtils.getAllFieldsList(clz);
		List<String> copierCopyFields = this.listCopierCopyFields(clz);
		if (ListTools.isNotEmpty(copierCopyFields)) {
			List<Field> os = new ArrayList<>();
			for (Field o : fields) {
				FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
				if ((null != fieldDescribe)
						&& (copierCopyFields.contains(o.getName()) || this.inWiNotInEntity(o.getName(), clz))) {
					os.add(o);
				}
				fields = os;
			}
		}
		for (Field o : fields) {
			FieldDescribe fieldDescribe = o.getAnnotation(FieldDescribe.class);
			if (null != fieldDescribe) {
				JaxrsField jaxrsField = new JaxrsField();
				jaxrsField.setName(o.getName());
				jaxrsField.setDescription(fieldDescribe.value());
				jaxrsField.setType(this.getJaxrsFieldType(o));
				jaxrsField.setIsBaseType(false);
				if (Collection.class.isAssignableFrom(o.getType())) {
					jaxrsField.setIsCollection(true);
					if (StringUtils.containsAny(jaxrsField.getType(), "<String>", "<Boolean>", "<Date>", "<Integer>",
							"<Double>", "<Long>", "<Float>")) {
						jaxrsField.setIsBaseType(true);
					}
				} else {
					// O2LEE，String[]未被判断为collection导致组织的JSON格式不符合wrapIn要求
					if (StringUtils.equalsAnyIgnoreCase("String[]", jaxrsField.getType())) {
						jaxrsField.setIsCollection(true);
					} else {
						jaxrsField.setIsCollection(false);
					}
					if (StringUtils.startsWithAny(jaxrsField.getType(), "String", "Boolean", "Date", "Integer",
							"Double", "Long", "Float")) {
						jaxrsField.setIsBaseType(true);
					}
				}
				list.add(jaxrsField);
			}
		}
		return list;
	}

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
				jaxrsField.setType(this.getJaxrsFieldType(o));
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

	private String getJaxrsFieldType(Field o) {
		String value = o.getGenericType().getTypeName();
		return this.simpleType(value);
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

	private List<String> listCopierCopyFields(Class<?> clz) {
		try {
			Object o = FieldUtils.readStaticField(clz, "copier", true);
			WrapCopier copier = (WrapCopier) o;
			return copier.getCopyFields();
		} catch (Exception e) {
			return null;
		}
	}

	/** 判断字段是否在Wi中但是没有在Entity类中说明是Wi新增字段,需要进行描述 */
	private Boolean inWiNotInEntity(String field, Class<?> clz) {
		try {
			Object o = FieldUtils.readStaticField(clz, "copier", true);
			WrapCopier copier = (WrapCopier) o;
			if ((null != FieldUtils.getField(copier.getOrigClass(), field, true))
					&& (null == FieldUtils.getField(copier.getDestClass(), field, true))) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return null;
		}
	}

	public class JaxrsClass {

		private String name;
		private List<JaxrsApiMethod> methods = new ArrayList<>();
	
		public List<JaxrsApiMethod> getMethods() {
			return methods;
		}

		public void setMethods(List<JaxrsApiMethod> methods) {
			this.methods = methods;
		}
     
		public String getName() {
			return name;
		}
  /*
		private List<Map<String,JaxrsApiMethod>> methods = new ArrayList<Map<String,JaxrsApiMethod>>();
		public List<Map<String, JaxrsApiMethod>> getMethods() {
			return methods;
		}

		public void setMethods(List<Map<String, JaxrsApiMethod>> methods) {
			this.methods = methods;
		}
  */
		public void setName(String name) {
			this.name = name;
		}
	}
	
    public class JaxrsApiMethod{
		//private List<JaxsApiMethodProperty> name = new ArrayList<>();
    	private String name;
		private String uri;
		private String method;
		private String enctype;
		
	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getEnctype() {
			return enctype;
		}
		public void setEnctype(String enctype) {
			this.enctype = enctype;
		}

    }
    public class JaxsApiMethodProperty{
    	private String name;
		private String uri;
		private String method;
		private String enctype;
		
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getEnctype() {
			return enctype;
		}
		public void setEnctype(String enctype) {
			this.enctype = enctype;
		}
    	
    }
	public class JaxrsMethod {
		private String name;
		private String className;
		private String description;
		private String type;
		private String path;
		private String contentType;
		private String resultContentType;
		private Boolean useJsonElementParameter = false;
		private Boolean useStringParameter = false;
		private List<JaxrsPathParameter> pathParameters = new ArrayList<>();
		private List<JaxrsFormParameter> formParameters = new ArrayList<>();
		private List<JaxrsQueryParameter> queryParameters = new ArrayList<>();
		private List<JaxrsField> ins = new ArrayList<>();
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

		public List<JaxrsField> getIns() {
			return ins;
		}

		public void setIns(List<JaxrsField> ins) {
			this.ins = ins;
		}

		public List<JaxrsField> getOuts() {
			return outs;
		}

		public void setOuts(List<JaxrsField> outs) {
			this.outs = outs;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
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

		public List<JaxrsPathParameter> getPathParameters() {
			return pathParameters;
		}

		public void setPathParameters(List<JaxrsPathParameter> pathParameters) {
			this.pathParameters = pathParameters;
		}

		public List<JaxrsFormParameter> getFormParameters() {
			return formParameters;
		}

		public void setFormParameters(List<JaxrsFormParameter> formParameters) {
			this.formParameters = formParameters;
		}

		public List<JaxrsQueryParameter> getQueryParameters() {
			return queryParameters;
		}

		public void setQueryParameters(List<JaxrsQueryParameter> queryParameters) {
			this.queryParameters = queryParameters;
		}

		public Boolean getUseJsonElementParameter() {
			return useJsonElementParameter;
		}

		public void setUseJsonElementParameter(Boolean useJsonElementParameter) {
			this.useJsonElementParameter = useJsonElementParameter;
		}

		public String getResultContentType() {
			return resultContentType;
		}

		public void setResultContentType(String resultContentType) {
			this.resultContentType = resultContentType;
		}

		public Boolean getUseStringParameter() {
			return useStringParameter;
		}

		public void setUseStringParameter(Boolean useStringParameter) {
			this.useStringParameter = useStringParameter;
		}

	}

	public class JaxrsField {

		private String name;
		private String type;
		private Boolean isCollection;
		private String description;
		private Boolean isBaseType;

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

	}

	public class JaxrsPathParameter {

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

	public class JaxrsFormParameter {

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

	public class JaxrsQueryParameter {

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