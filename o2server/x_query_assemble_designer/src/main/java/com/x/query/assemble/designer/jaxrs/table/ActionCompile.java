package com.x.query.assemble.designer.jaxrs.table;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.openjpa.ant.PCEnhancerTask;
import org.apache.openjpa.enhance.PCEnhancer;

import com.sun.jna.Native.ffi_callback;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.JarTools;
import com.x.query.assemble.designer.DynamicEntity;
import com.x.query.assemble.designer.DynamicEntityBuilder;

class ActionCompile extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DynamicEntity dynamicEntity = new DynamicEntity("test1");

		dynamicEntity.addStringField("abc", "测试");

		File src = new File(Config.dir_local_temp(), "dynamic/src");
		File target = new File(Config.dir_local_temp(), "dynamic/target");
		FileUtils.forceMkdir(src);
		FileUtils.cleanDirectory(src);
		FileUtils.forceMkdir(target);
		FileUtils.cleanDirectory(target);
		DynamicEntityBuilder builder = new DynamicEntityBuilder(dynamicEntity, src);
		builder.build();

		List<File> classPath = new ArrayList<>();
		classPath.addAll(FileUtils.listFiles(Config.dir_commons_ext(), FileFilterUtils.suffixFileFilter(".jar"),
				DirectoryFileFilter.INSTANCE));
		classPath.addAll(FileUtils.listFiles(Config.dir_store_jars(), FileFilterUtils.suffixFileFilter(".jar"),
				DirectoryFileFilter.INSTANCE));

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, DefaultCharset.charset_utf_8);
		fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(target));
		fileManager.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(src));
		fileManager.setLocation(StandardLocation.CLASS_PATH, classPath);
		Iterable<JavaFileObject> res = fileManager.list(StandardLocation.SOURCE_PATH, DynamicEntity.CLASS_PACKAGE,
				EnumSet.of(JavaFileObject.Kind.SOURCE), true);
		compiler.getTask(null, fileManager, null, null, null, res).call();

		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);

		method.invoke(urlClassLoader, new Object[] { target.toURI().toURL() });

		Collection<File> files = FileUtils.listFiles(target, FileFilterUtils.suffixFileFilter(".class"),
				DirectoryFileFilter.INSTANCE);
		for (File f : files) {
			PCEnhancer.main(new String[] { f.getAbsolutePath() });
		}
		fileManager.close();

		File jar = new File(Config.dir_dynamic_jars(true), "x_query_dynamic_entity.jar");
		JarTools.jar(target, jar);

		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;

	}

	public static class Wo extends WrapBoolean {

	}

}