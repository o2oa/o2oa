package com.x.server.console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.util.RolloverFileOutputStream;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;
import com.google.gson.JsonElement;
import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.ExternalDataSource;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.node.EventQueueExecutor;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ResourceFactory {

	private static Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	public static void bind() throws Exception {
		try (ScanResult sr = new ClassGraph().enableAnnotationInfo().scan()) {
			node(sr);
			containerEntities(sr);
			containerEntityNames(sr);
			stroageContainerEntityNames(sr);
		}
		if (Config.logLevel().audit().enable()) {
			auditLog();
		}
		if (Config.externalDataSources().enable()) {
			external();
		} else {
			internal();
		}
		processPlatformExecutors();
	}

	private static void node(ScanResult sr) throws Exception {
		LinkedBlockingQueue<JsonElement> eventQueue = new LinkedBlockingQueue<>();
		EventQueueExecutor eventQueueExecutor = new EventQueueExecutor(eventQueue);
		eventQueueExecutor.start();
		new Resource(Config.RESOURCE_NODE_EVENTQUEUE, eventQueue);
		new Resource(Config.RESOURCE_NODE_EVENTQUEUEEXECUTOR, eventQueueExecutor);
		new Resource(Config.RESOURCE_NODE_APPLICATIONS, null);
		new Resource(Config.RESOURCE_NODE_APPLICATIONSTIMESTAMP, null);
		Entry<String, CenterServer> entry = Config.nodes().centerServers().first();
		new Resource(Config.RESOURCE_NODE_CENTERSPRIMARYNODE, entry.getKey());
		new Resource(Config.RESOURCE_NODE_CENTERSPRIMARYPORT, entry.getValue().getPort());
		new Resource(Config.RESOURCE_NODE_CENTERSPRIMARYSSLENABLE, entry.getValue().getSslEnable());
	}

	private static void external() throws Exception {
		external_druid_c3p0();
	}

	private static void external_dbcp2() throws Exception {
		for (ExternalDataSource ds : Config.externalDataSources()) {
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(ds.getDriverClassName());
			dataSource.setUrl(ds.getUrl());
			dataSource.setInitialSize(0);
			dataSource.setMinIdle(0);
			dataSource.setMaxTotal(ds.getMaxTotal());
			dataSource.setMaxIdle(ds.getMaxTotal());
			dataSource.setTestOnCreate(false);
			dataSource.setTestWhileIdle(false);
			dataSource.setTestOnReturn(false);
			dataSource.setTestOnBorrow(false);
			dataSource.setUsername(ds.getUsername());
			dataSource.setPassword(ds.getPassword());
			String name = Config.externalDataSources().name(ds);
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
		}
	}

	private static void external_druid_c3p0() throws Exception {
		for (ExternalDataSource ds : Config.externalDataSources()) {
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			dataSource.setJdbcUrl(ds.getUrl());
			dataSource.setDriverClass(ds.getDriverClassName());
			dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(ds.getUrl()));
			dataSource.setUser(ds.getUsername());
			dataSource.setPassword(ds.getPassword());
			dataSource.setMaxPoolSize(ds.getMaxTotal());
			dataSource.setMinPoolSize(ds.getMaxIdle());
			/* 增加校验 */
			dataSource.setTestConnectionOnCheckin(true);
			dataSource.setAcquireIncrement(0);
			if (ds.getStatEnable()) {
				dataSource.setFilters(ds.getStatFilter());
				Properties properties = new Properties();
				// property name="connectionProperties" value="druid.stat.slowSqlMillis=5000
				properties.setProperty("druid.stat.slowSqlMillis", ds.getSlowSqlMillis().toString());
				dataSource.setProperties(properties);
			}
			String name = Config.externalDataSources().name(ds);
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
		}
	}

	private static void internal() throws Exception {
		internal_driud_c3p0();
		// internal_driud();
		// internal_dbcp2();
	}

	private static void internal_driud_c3p0() throws Exception {
		for (Entry<String, DataServer> entry : Config.nodes().dataServers().entrySet()) {
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			String url = "jdbc:h2:tcp://" + entry.getKey() + ":" + entry.getValue().getTcpPort() + "/X;JMX="
					+ (entry.getValue().getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
					+ (entry.getValue().getCacheSize() * 1024);
			dataSource.setJdbcUrl(url);
			dataSource.setDriverClass(SlicePropertiesBuilder.driver_h2);
			dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(url));
			dataSource.setUser("sa");
			dataSource.setPassword(Config.token().getPassword());
			dataSource.setMaxPoolSize(entry.getValue().getMaxTotal());
			dataSource.setMinPoolSize(entry.getValue().getMaxIdle());
			dataSource.setAcquireIncrement(0);
			if (entry.getValue().getStatEnable()) {
				dataSource.setFilters(entry.getValue().getStatFilter());
				Properties properties = new Properties();
				// property name="connectionProperties" value="druid.stat.slowSqlMillis=5000
				properties.setProperty("druid.stat.slowSqlMillis", entry.getValue().getSlowSqlMillis().toString());
				dataSource.setProperties(properties);
			}
			String name = Config.nodes().dataServers().name(entry.getValue());
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
		}
	}

	private static void internal_dbcp2() throws Exception {

		for (Entry<String, DataServer> entry : Config.nodes().dataServers().entrySet()) {

			BasicDataSource dataSource = new BasicDataSource();

			String url = "jdbc:h2:tcp://" + entry.getKey() + ":" + entry.getValue().getTcpPort() + "/X;JMX="
					+ (entry.getValue().getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
					+ (entry.getValue().getCacheSize() * 1024);
			dataSource.setDriverClassName(SlicePropertiesBuilder.driver_h2);
			dataSource.setUrl(url);
			dataSource.setInitialSize(0);
			dataSource.setMinIdle(0);
			dataSource.setMaxTotal(50);
			dataSource.setMaxIdle(50);
			dataSource.setUsername("sa");
			dataSource.setTestOnCreate(false);
			dataSource.setTestWhileIdle(false);
			dataSource.setTestOnReturn(false);
			dataSource.setTestOnBorrow(false);
			dataSource.setPassword(Config.token().getPassword());
			String name = Config.nodes().dataServers().name(entry.getValue());
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);

		}
	}

	private static void internal_driud() throws Exception {
		for (Entry<String, DataServer> entry : Config.nodes().dataServers().entrySet()) {
			DruidDataSource dataSource = new DruidDataSource();
			String url = "jdbc:h2:tcp://" + entry.getKey() + ":" + entry.getValue().getTcpPort() + "/X;JMX="
					+ (entry.getValue().getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
					+ (entry.getValue().getCacheSize() * 1024);
			dataSource.setDriverClassName(SlicePropertiesBuilder.driver_h2);
			dataSource.setUrl(url);
			dataSource.setInitialSize(0);
			dataSource.setMinIdle(0);
			dataSource.setMaxActive(50);
			dataSource.setUsername("sa");
			dataSource.setTestWhileIdle(false);
			dataSource.setTestOnReturn(false);
			dataSource.setTestOnBorrow(false);
			dataSource.setFilters("stat");
			dataSource.setPassword(Config.token().getPassword());
			dataSource.init();
			String name = Config.nodes().dataServers().name(entry.getValue());
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
		}
	}

	private static void containerEntityNames(ScanResult sr) throws Exception {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOURCE_CONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void stroageContainerEntityNames(ScanResult sr) throws Exception {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Storage.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOURCE_STORAGECONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void containerEntities(ScanResult sr) throws Exception {
		Map<String, List<String>> map = new TreeMap<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Module.class.getName())) {
			Class<?> cls = Class.forName(info.getName());
			List<String> os = ListTools.toList(cls.getAnnotation(Module.class).containerEntities());
			map.put(info.getName(), ListUtils.unmodifiableList(os));
		}
		new Resource(Config.RESOURCE_CONTAINERENTITIES, MapUtils.unmodifiableMap(map));
	}

	private static void auditLog() throws Exception {
		RolloverFileOutputStream rolloverFileOutputStream = new RolloverFileOutputStream(
				Config.dir_logs(true).getAbsolutePath() + "/yyyy_mm_dd.audit.log", true,
				Config.logLevel().audit().logSize());
		new Resource(Config.RESOURCE_AUDITLOGPRINTSTREAM,
				new PrintStream(rolloverFileOutputStream, true, DefaultCharset.name_iso_utf_8));
	}

	private static void processPlatformExecutors() throws Exception {
		ExecutorService[] services = new ExecutorService[Config.processPlatform().getExecutorCount()];
		for (int i = 0; i < Config.processPlatform().getExecutorCount(); i++) {
			services[i] = Executors.newSingleThreadExecutor();
		}
		new Resource(Config.RESOURCE_NODE_PROCESSPLATFORMEXECUTORS, services);
	}

}
