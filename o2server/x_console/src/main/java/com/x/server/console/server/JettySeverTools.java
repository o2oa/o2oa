package com.x.server.console.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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

	public static final int THREAD_POOL_SIZE_MIN = 20;
	public static final int THREAD_POOL_SIZE_MAX = 2000;
	public static final int MAX_FORM_CONTENT_SIZE = 1024 * 1024 * 1024 * 20;

	public static final String LOG_FORMAT = "%{client}a - %u %{yyyy-MM-dd HH:mm:ss.SSS ZZZ|"
			+ DateFormatUtils.format(new Date(), "z") + "}t \"%r\" %s %O %{ms}T";

	/**
	 * jetty-all-*.jar 如果不单独导入会导致java.lang.NoClassDefFoundError:
	 * org/eclipse/jetty/http/MimeTypes JaxWsDynamicClientFactory
	 * 需要在WebAppClassLoader加载 jakarta.xml.bind-api-*.jar
	 */
	private static final Collection<String> FILTER_STRINGS = Arrays.asList("openjpa-*.jar", "ehcache-*.jar",
			"jetty-all-*.jar", "jetty-proxy-*.jar", "quartz-*.jar", "filters-*.jar", "jakarta.xml.bind-api-*.jar",
			"swagger-*.jar");

	private static final Optional<IOFileFilter> JARS_FILTER = FILTER_STRINGS.stream().map(WildcardFileFilter::new)
			.map(FileFilterUtils::or).reduce(FileFilterUtils::or);

	protected JettySeverTools() {
		// nothing
	}

	protected static void addHttpsConnector(Server server, Integer port, boolean persistentConnectionsEnable)
			throws Exception {
		SslContextFactory sslContextFactory = new SslContextFactory.Server();
		sslContextFactory.setKeyStorePath(Config.sslKeyStore().getAbsolutePath());
		sslContextFactory.setKeyStorePassword(Config.token().getSslKeyStorePassword());
		sslContextFactory.setKeyManagerPassword(Config.token().getSslKeyManagerPassword());
		sslContextFactory.setTrustAll(true);
		HttpConfiguration config = new HttpConfiguration();
		config.setSecureScheme("https");
		config.setPersistentConnectionsEnabled(persistentConnectionsEnable);
		config.setRequestHeaderSize(8192 * 2);
		config.setResponseHeaderSize(8192 * 2);
		config.setSendServerVersion(false);
		config.setSendDateHeader(false);
		ServerConnector https = new ServerConnector(server,
				new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
				new HttpConnectionFactory(config));
		https.setAcceptQueueSize(-1);
		https.setIdleTimeout(30000);
		https.setPort(port);
		server.addConnector(https);
	}

	protected static void addHttpConnector(Server server, Integer port, boolean persistentConnectionsEnable) {
		HttpConfiguration config = new HttpConfiguration();
		config.setPersistentConnectionsEnabled(persistentConnectionsEnable);
		config.setRequestHeaderSize(8192 * 2);
		config.setResponseHeaderSize(8192 * 2);
		config.setSendServerVersion(false);
		config.setSendDateHeader(false);
		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(config));
		http.setAcceptQueueSize(-1);
		http.setIdleTimeout(30000);
		http.setPort(port);
		server.addConnector(http);
	}

	protected static void cleanDirectory(File dir) throws IOException {
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
	}

	protected static String calculateExtraClassPath(Class<?> cls, Path... paths) throws Exception {
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
		for (Path path : paths) {
			if (Files.exists(path) && Files.isDirectory(path)) {
				try (Stream<Path> stream = Files.walk(path, FileVisitOption.FOLLOW_LINKS)) {
					stream.filter(Files::isRegularFile)
							.filter(p -> p.toAbsolutePath().toString().toLowerCase().endsWith(".jar"))
							.forEach(p -> jars.add(p.toAbsolutePath().toString()));
				}
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
		for (File o : FileUtils.listFiles(Config.pathCommonsExt(true).toFile(), JARS_FILTER.get(), null)) {
			jars.add(o.getAbsolutePath());
		}
		return jars;
	}

}
