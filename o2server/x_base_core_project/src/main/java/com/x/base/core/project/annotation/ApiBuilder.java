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
import java.util.stream.Stream;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiBuilder.class);

	private static final String DESCRIBE_DIR = "src/main/webapp/describe";

	public static void main(String[] args) throws IOException {
		File basedir = new File(args[0]);
		File dir = new File(basedir, DESCRIBE_DIR);
		FileUtils.forceMkdir(dir);
		ApiBuilder builder = new ApiBuilder();
		builder.scan(dir);
	}

	private void scan(File dir) {
		try {
			new ArrayList<>();
			List<JaxrsClass> jaxrsClasses = this.scanJaxrsClass().stream()
					.filter(StandardJaxrsAction.class::isAssignableFrom).map(this::jaxrsClass)
					.sorted(Comparator.comparing(JaxrsClass::getName)).collect(Collectors.toList());
			LinkedHashMap<String, List<?>> map = new LinkedHashMap<>();
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
		LOGGER.print("describe class:{}.", clz.getName());
		JaxrsClass jaxrsClass = new JaxrsClass();
		jaxrsClass.setName(clz.getSimpleName());
		Stream.of(clz.getMethods()).filter(o -> null != o.getAnnotation(JaxrsMethodDescribe.class))
				.map(o -> this.jaxrsApiMethod(clz, o)).forEach(jaxrsClass.getMethods()::add);
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

	private class JaxrsClass {

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

	private class JaxrsApiMethod {

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

}