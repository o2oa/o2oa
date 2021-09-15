package com.x.base.core.project.annotation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.commons.collections4.list.SetUniqueList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ApiBuilder {

	private static Logger logger = LoggerFactory.getLogger(ApiBuilder.class);

	public static void main(String[] args) throws IOException {
		File basedir = new File(args[0]);

		File dir = new File(basedir, "src/main/webapp/describe");
		FileUtils.forceMkdir(dir);
		ApiBuilder builder = new ApiBuilder();
		builder.scan(dir);
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

	@SuppressWarnings("unchecked")
	private List<Class<?>> scanJaxrsClass()
			throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			InstantiationException, IllegalArgumentException, SecurityException {
		try (ScanResult scanResult = new ClassGraph().disableJarScanning().enableAnnotationInfo().scan()) {
			SetUniqueList<Class<?>> classes = SetUniqueList.setUniqueList(new ArrayList<Class<?>>());
			for (ClassInfo info : scanResult.getClassesWithAnnotation(ApplicationPath.class.getName())) {
				Class<?> applicationPathClass = ClassUtils.getClass(info.getName());
				for (Class<?> o : (Set<Class<?>>) MethodUtils
						.invokeMethod(applicationPathClass.getDeclaredConstructor().newInstance(), "getClasses")) {
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

	private JaxrsClass jaxrsClass(Class<?> clz) {
		logger.print("describe class:{}.", clz.getName());
		JaxrsClass jaxrsClass = new JaxrsClass();
		jaxrsClass.setName(clz.getSimpleName());
		for (Method method : clz.getMethods()) {
			JaxrsMethodDescribe jaxrsMethodDescribe = method.getAnnotation(JaxrsMethodDescribe.class);
			if (null != jaxrsMethodDescribe) {
				jaxrsClass.getMethods().add(this.jaxrsApiMethod(clz, method));
			}
		}
		return jaxrsClass;

	}

	private JaxrsApiMethod jaxrsApiMethod(Class<?> clz, Method method) {
		JaxrsApiMethod jaxrsMethod = new JaxrsApiMethod();
		jaxrsMethod.setName(method.getName());

		setMethod(method, jaxrsMethod);

		if (!jaxrsMethod.getMethod().equalsIgnoreCase("GET")) {
			Consumes consumes = method.getAnnotation(Consumes.class);
			if (null != consumes) {
				if (consumes.value()[0].equals("multipart/form-data")) {
					jaxrsMethod.setEnctype("formData");
				} else {
					jaxrsMethod.setEnctype(consumes.value()[0]);
				}
			} else {
				// nothing
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

	private void setMethod(Method method, JaxrsApiMethod jaxrsMethod) {
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

		public void setName(String name) {
			this.name = name;
		}
	}

	public class JaxrsApiMethod {
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

	public class JaxsApiMethodProperty {
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