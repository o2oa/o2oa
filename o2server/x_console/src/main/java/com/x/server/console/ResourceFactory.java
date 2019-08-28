package com.x.server.console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.util.RolloverFileOutputStream;

import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.DataServer;
import com.x.base.core.project.config.ExternalDataSource;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ResourceFactory {

	private static Logger logger = LoggerFactory.getLogger(ResourceFactory.class);

	public static void bind() throws Exception {
		try (ScanResult sr = new ClassGraph().enableAnnotationInfo().scan()) {
			node();
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
	}

	private static void node() throws Exception {
		ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
		new Resource(Config.RESOUCE_NODE, map);
	}

	private static void external() throws Exception {

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
			new Resource(Config.RESOUCE_JDBC_PREFIX + name, dataSource);
		}

	}

	private static void internal() throws Exception {

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
			new Resource(Config.RESOUCE_JDBC_PREFIX + name, dataSource);

		}

	}

	private static void containerEntityNames(ScanResult sr) throws Exception {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOUCE_CONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void stroageContainerEntityNames(ScanResult sr) throws Exception {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Storage.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOUCE_STORAGECONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void containerEntities(ScanResult sr) throws Exception {
		Map<String, List<String>> map = new TreeMap<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Module.class.getName())) {
			Class<?> cls = Class.forName(info.getName());
			List<String> os = ListTools.toList(cls.getAnnotation(Module.class).containerEntities());
			map.put(info.getName(), ListUtils.unmodifiableList(os));
		}
		new Resource(Config.RESOUCE_CONTAINERENTITIES, MapUtils.unmodifiableMap(map));
	}

	private static void auditLog() throws Exception {
		RolloverFileOutputStream rolloverFileOutputStream = new RolloverFileOutputStream(
				Config.dir_logs(true).getAbsolutePath() + "/yyyy_mm_dd.audit.log", true,
				Config.logLevel().audit().logSize());
		new Resource(Config.RESOUCE_AUDITLOGPRINTSTREAM,
				new PrintStream(rolloverFileOutputStream, true, DefaultCharset.name_iso_utf_8));
	}

}
