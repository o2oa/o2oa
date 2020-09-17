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
import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.ClassLoaderTools;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.node.EventQueueExecutor;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ResourceFactory {

	private static Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	private ResourceFactory() {
		// nothing
	}

	public static void bind() throws Exception {
		try (ScanResult sr = new ClassGraph()
				.addClassLoader(ClassLoaderTools.urlClassLoader(true, false, true, true, true)).enableAnnotationInfo()
				.scan()) {
			node(sr);
			containerEntities(sr);
			containerEntityNames(sr);
			stroageContainerEntityNames(sr);
		}
		if (BooleanUtils.isTrue(Config.logLevel().audit().enable())) {
			auditLog();
		}
		if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
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

	private static void external_druid_c3p0() throws Exception {
		for (ExternalDataSource ds : Config.externalDataSources()) {
			if (BooleanUtils.isNotTrue(ds.getEnable())) {
				continue;
			}
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			dataSource.setJdbcUrl(ds.getUrl());
			dataSource.setDriverClass(ds.getDriverClassName());
			dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(ds.getUrl()));
			dataSource.setUser(ds.getUsername());
			dataSource.setPassword(ds.getPassword());
			dataSource.setMaxPoolSize(ds.getMaxTotal());
			dataSource.setMinPoolSize(ds.getMaxIdle());
			// 增加校验
			dataSource.setTestConnectionOnCheckin(true);
			dataSource.setTestConnectionOnCheckout(true);
			dataSource.setAcquireIncrement(2);
			if (BooleanUtils.isTrue(ds.getStatEnable())) {
				dataSource.setFilters(ds.getStatFilter());
				Properties properties = new Properties();
				properties.setProperty("druid.stat.slowSqlMillis", ds.getSlowSqlMillis().toString());
				dataSource.setProperties(properties);
			}
			String name = Config.externalDataSources().name(ds);
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
		}
	}

	private static void internal() throws Exception {
		internal_driud_c3p0();
	}

	private static void internal_driud_c3p0() throws Exception {
		for (Entry<String, DataServer> entry : Config.nodes().dataServers().entrySet()) {
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			String url = "jdbc:h2:tcp://" + entry.getKey() + ":" + entry.getValue().getTcpPort()
					+ "/X;LOCK_MODE=0;DEFAULT_LOCK_TIMEOUT=" + entry.getValue().getLockTimeout() + ";JMX="
					+ (BooleanUtils.isTrue(entry.getValue().getJmxEnable()) ? "TRUE" : "FALSE") + ";CACHE_SIZE="
					+ (entry.getValue().getCacheSize() * 1024);
			dataSource.setJdbcUrl(url);
			dataSource.setDriverClass(SlicePropertiesBuilder.driver_h2);
			dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(url));
			dataSource.setUser("sa");
			dataSource.setPassword(Config.token().getPassword());
			dataSource.setMaxPoolSize(entry.getValue().getMaxTotal());
			dataSource.setMinPoolSize(entry.getValue().getMaxIdle());
			dataSource.setAcquireIncrement(2);
			if (BooleanUtils.isTrue(entry.getValue().getStatEnable())) {
				dataSource.setFilters(entry.getValue().getStatFilter());
				Properties properties = new Properties();
				properties.setProperty("druid.stat.slowSqlMillis", entry.getValue().getSlowSqlMillis().toString());
				dataSource.setProperties(properties);
			}
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
			services[i] = Executors.newFixedThreadPool(1);
		}

		new Resource(Config.RESOURCE_NODE_PROCESSPLATFORMEXECUTORS, services);
	}

}
