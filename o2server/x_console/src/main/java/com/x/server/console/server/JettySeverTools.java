package com.x.server.console.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.x.base.core.project.x_base_core_project;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;

public abstract class JettySeverTools {

	protected static void addHttpsConnector(Server server, Integer port) throws Exception {
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath(Config.sslKeyStore().getAbsolutePath());
		sslContextFactory.setKeyStorePassword(Config.token().getSslKeyStorePassword());
		sslContextFactory.setKeyManagerPassword(Config.token().getSslKeyManagerPassword());
		sslContextFactory.setTrustAll(true);
		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setOutputBufferSize(32768);
		config.setRequestHeaderSize(8192 * 2);
		config.setResponseHeaderSize(8192 * 2);
		config.setSendServerVersion(true);
		config.setSendDateHeader(false);
		ServerConnector sslConnector = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(config));
		sslConnector.setPort(port);
		server.addConnector(sslConnector);
	}

	protected static void addHttpConnector(Server server, Integer port) throws Exception {
		HttpConfiguration config = new HttpConfiguration();
		config.setOutputBufferSize(32768);
		config.setRequestHeaderSize(8192 * 2);
		config.setResponseHeaderSize(8192 * 2);
		config.setSendServerVersion(true);
		config.setSendDateHeader(false);
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(config));
		http.setIdleTimeout(30000);
		http.setPort(port);
		server.addConnector(http);
	}

	protected static void cleanDirectory(File dir) throws Exception {
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
	}

	protected static String calculateExtraClassPath(Class<?> cls) throws Exception {
		List<String> jars = new ArrayList<>();
		jars.addAll(calculateExtraClassPathDefault());
		Module module = cls.getAnnotation(Module.class);
		for (String str : module.storeJars()) {
			File file = new File(Config.dir_store_jars(), str + ".jar");
			if (file.exists()) {
				jars.add(file.getAbsolutePath());
			}
		}
		for (String str : module.customJars()) {
			File file = new File(Config.dir_custom_jars(), str + ".jar");
			if (file.exists()) {
				jars.add(file.getAbsolutePath());
			}
		}
		for (String str : module.dynamicJars()) {
			File file = new File(Config.dir_dynamic_jars(), str + ".jar");
			if (file.exists()) {
				jars.add(file.getAbsolutePath());
			}
		}
		return StringUtils.join(jars, ";");
	}

	private static List<String> calculateExtraClassPathDefault() throws Exception {
		List<String> jars = new ArrayList<>();
		IOFileFilter filter = new WildcardFileFilter(x_base_core_project.class.getSimpleName() + "*.jar");
		for (File o : FileUtils.listFiles(Config.dir_store_jars(), filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		filter = new WildcardFileFilter("slf4j-api-*.jar");
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("slf4j-simple-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jul-to-slf4j-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("openjpa-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("ehcache-*.jar"));
		/* 如果不单独导入会导致java.lang.NoClassDefFoundError: org/eclipse/jetty/http/MimeTypes */
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jetty-all-*.jar"));
		/* jersey从AppClassLoader加载 */
		// filter = FileFilterUtils.or(filter, new WildcardFileFilter("jersey-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("quartz-*.jar"));
		for (File o : FileUtils.listFiles(Config.dir_commons_ext(), filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		return jars;
	}

}
