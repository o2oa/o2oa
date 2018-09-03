package com.x.server.console.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.DefaultCharset;

public abstract class JettySeverTools {

	protected static void addHttpsConnector(Server server, Integer port) throws Exception {
		SslContextFactory sslContextFactory = new SslContextFactory();
		// File file = new File(configDir, "o2.keystore");
		// if (!file.exists() || file.isDirectory()) {
		// file = new File(new File(configDir, "sample"), "o2.keystore");
		// }
		sslContextFactory.setKeyStorePath(Config.sslKeyStore().getAbsolutePath());
		sslContextFactory.setKeyStorePassword(Config.token().getSslKeyStorePassword());
		sslContextFactory.setKeyManagerPassword(Config.token().getSslKeyManagerPassword());
		sslContextFactory.setTrustAll(true);
		// sslContextFactory.setTrustStorePath(Config.sslKeyStore().getAbsolutePath());
		// sslContextFactory.setTrustStorePassword(Config.token().getTrustStorePassword());
		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setOutputBufferSize(32768);
		config.setRequestHeaderSize(8192);
		config.setResponseHeaderSize(8192);
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
		config.setRequestHeaderSize(8192);
		config.setResponseHeaderSize(8192);
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

	protected static void createDeployDescriptor(Class<?> clazz, File webappsDir, File workDir, File storeDir,
			File extDir, File jarsDir) throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		if (Config.currentNode().getQuickStartWebApp()) {
			buffer.append("<Configure class=\"org.eclipse.jetty.quickstart.QuickStartWebApp\">");
			buffer.append("<Set name=\"autoPreconfigure\">true</Set>");
			/*
			 * 使用org.eclipse.jetty.quickstart.QuickStartWebApp 启动速度大约快50% 需要设置
			 * autoPreconfigure=true
			 */
		} else {
			buffer.append("<Configure class=\"org.eclipse.jetty.webapp.WebAppContext\">");
		}
		buffer.append("<Set name=\"contextPath\">/" + clazz.getSimpleName() + "</Set>");
		File war = new File(storeDir, clazz.getSimpleName() + ".war");
		buffer.append("<Set name=\"war\">" + war.getAbsolutePath() + "</Set>");
		String extraClasspath = calculateExtraClassPath(clazz, extDir, jarsDir);
		buffer.append("	<Set name=\"extraClasspath\">" + extraClasspath + "</Set>");
		String tempDirectory = new File(workDir, clazz.getSimpleName()).getAbsolutePath();
		buffer.append("	<Set name=\"tempDirectory\">" + tempDirectory + "</Set>");
		buffer.append("	</Configure>");
		File file = new File(webappsDir, clazz.getSimpleName() + ".xml");
		FileUtils.write(file, buffer.toString(), DefaultCharset.charset);
	}

	private static String calculateExtraClassPath(Class<?> clazz, File extDir, File jarsDir) throws Exception {
		List<String> jars = new ArrayList<>();
		jars.addAll(calculateExtraClassPathExt(extDir));
		jars.addAll(calculateExtraClassPathDependents(clazz, jarsDir));
		return StringUtils.join(jars, ";");
	}

	private static List<String> calculateExtraClassPathExt(File extDir) {
		List<String> jars = new ArrayList<>();
		IOFileFilter filter = new WildcardFileFilter("slf4j-api-*.jar");
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("slf4j-simple-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jul-to-slf4j-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("openjpa-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("ehcache-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("jetty-all-*.jar"));
		filter = FileFilterUtils.or(filter, new WildcardFileFilter("quartz-*.jar"));
		for (File o : FileUtils.listFiles(extDir, filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		return jars;
	}

	private static List<String> calculateExtraClassPathDependents(Class<?> clazz, File jarsDir) throws Exception {
		List<String> jars = new ArrayList<>();
		IOFileFilter filter = FalseFileFilter.FALSE;
		@SuppressWarnings("unchecked")
		List<Class<?>> list = (List<Class<?>>) FieldUtils.readStaticField(clazz, "dependents");
		for (Class<?> o : list) {
			filter = FileFilterUtils.or(filter, new WildcardFileFilter(o.getSimpleName() + ".jar"));
		}
		for (File o : FileUtils.listFiles(jarsDir, filter, null)) {
			jars.add(o.getAbsolutePath());
		}
		return jars;
	}

}
