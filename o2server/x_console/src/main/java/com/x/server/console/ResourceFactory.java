package com.x.server.console;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.NamingException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jetty.plus.jndi.Resource;

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
import com.x.base.core.project.tools.ClassLoaderTools;
import com.x.base.core.project.tools.H2Tools;
import com.x.base.core.project.tools.JarTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PathTools;
import com.x.server.console.node.EventQueueExecutor;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

/**
 * 
 * @author ray
 *
 */
public class ResourceFactory {

	private static final int TOKENTHRESHOLDSMAXSIZE = 2000;

	/**
	 * 用于销毁时的对象
	 */
	private static List<DruidDataSourceC3P0Adapter> dataSources = new ArrayList<>();

	private ResourceFactory() {
		// nothing
	}

	public static void init() throws Exception {
		ClassLoader cl = ClassLoaderTools.urlClassLoader(ClassLoader.getSystemClassLoader(), false, true, true, false,
				unzipCustomWar());
		try (ScanResult sr = new ClassGraph().addClassLoader(cl).enableAnnotationInfo().scan()) {
			node();
			containerEntities(cl, sr);
			containerEntityNames(sr);
			stroageContainerEntityNames(sr);
			disableDruidMysqlUsePingMethod();
		}
		initDataSources();
		tokenThresholds();
	}

	/**
	 * 生成DataSources并绑定到jndi
	 * 
	 * @throws Exception
	 */
	private static void initDataSources() throws Exception {
		if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
			external();
		} else {
			internal();
		}
	}

	/**
	 * 使用mysql连接服务器端提醒WARN,是由于druid是使用了mysql ping 检测连接导致的 druid 1.2.2版本以上有这个问题.
	 * 2021-11-03 15:51:23.398 [com.x.program.center.LogQueue] WARN
	 * com.alibaba.druid.pool.DruidAbstractDataSource - discard long time none
	 * received connection. , jdbcUrl :
	 * jdbc:mysql://127.0.0.1:3306/X?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8,
	 * version : 1.2.5, lastPacketReceivedIdleMillis : 119991
	 */
	private static void disableDruidMysqlUsePingMethod() {
		System.setProperty("druid.mysql.usePingMethod", "false");
	}

	/**
	 * 需要将custom模块中的entity加入到扫描目录中,
	 * 所以需要对custom中的web-inf/classes目录下的实体类进行扫描,先把war解压到临时目录然后读取classes目录下的类
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Path[] unzipCustomWar() throws Exception {
		FileUtils.cleanDirectory(Config.dir_local_temp_custom(true));
		List<String> list = new ArrayList<>();
		File dir = Config.dir_custom(true);
		String[] wars = dir.list(new WildcardFileFilter("*" + PathTools.DOT_WAR));
		if (null != wars) {
			for (String str : wars) {
				list.add(FilenameUtils.getBaseName(str));
			}
		}
		list = ListTools.includesExcludesWildcard(list, Config.currentNode().getApplication().getIncludes(),
				Config.currentNode().getApplication().getExcludes());
		List<Path> paths = new ArrayList<>();
		for (String str : list) {
			Path path = Paths.get(Config.dir_custom().toString(), str + PathTools.DOT_WAR);
			JarTools.unjar(path, "", Config.dir_local_temp_custom().toPath().resolve(str), true);
			paths.add(Config.dir_local_temp_custom().toPath().resolve(str).resolve(PathTools.WEB_INF_CLASSES));
		}
		return paths.toArray(new Path[paths.size()]);
	}

	private static void node() throws Exception {
		LinkedBlockingQueue<JsonElement> eventQueue = new LinkedBlockingQueue<>();
		EventQueueExecutor eventQueueExecutor = new EventQueueExecutor(eventQueue);
		eventQueueExecutor.start();
		
		new Resource(Config.RESOURCE_NODE_EVENTQUEUE, eventQueue);
		new Resource(Config.RESOURCE_NODE_EVENTQUEUEEXECUTOR, eventQueueExecutor);
		new Resource(Config.RESOURCE_NODE_APPLICATIONS, new ConcurrentHashMap<String, Object>(10));
		Entry<String, CenterServer> entry = Config.nodes().centerServers().first();
		Config.resource_node_centersPirmaryNode(entry.getKey());
		Config.resource_node_centersPirmaryPort(entry.getValue().getPort());
		Config.resource_node_centersPirmarySslEnable(entry.getValue().getSslEnable());
	}

	private static void containerEntityNames(ScanResult sr) throws NamingException {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOURCE_CONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void stroageContainerEntityNames(ScanResult sr) throws NamingException {
		List<String> list = new ArrayList<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Storage.class.getName())) {
			list.add(info.getName());
		}
		list = ListTools.trim(list, true, true);
		new Resource(Config.RESOURCE_STORAGECONTAINERENTITYNAMES, ListUtils.unmodifiableList(list));
	}

	private static void containerEntities(ClassLoader classLoader, ScanResult sr)
			throws NamingException, ClassNotFoundException {
		Map<String, List<String>> map = new TreeMap<>();
		for (ClassInfo info : sr.getClassesWithAnnotation(Module.class.getName())) {
			Class<?> cls = classLoader.loadClass(info.getName());
			List<String> os = ListTools.toList(cls.getAnnotation(Module.class).containerEntities());
			map.put(info.getName(), ListUtils.unmodifiableList(os));
		}
		new Resource(Config.RESOURCE_CONTAINERENTITIES, MapUtils.unmodifiableMap(map));
	}

	private static void external() throws Exception {
		dataSources.addAll(externalDruidC3p0());
	}

	private static List<DruidDataSourceC3P0Adapter> externalDruidC3p0() throws Exception {
		List<DruidDataSourceC3P0Adapter> list = new ArrayList<>();
		for (ExternalDataSource ds : Config.externalDataSources()) {
			if (BooleanUtils.isNotTrue(ds.getEnable())) {
				continue;
			}
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			dataSource.setJdbcUrl(ds.getUrl());
			dataSource.setDriverClass(ds.getDriverClassName());
			//dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(ds.getUrl()));
			dataSource.setUser(ds.getUsername());
			dataSource.setPassword(ds.getPassword());
			dataSource.setMaxPoolSize(ds.getMaxTotal());
			dataSource.setMinPoolSize(ds.getMaxIdle());
			// 增加校验
			//dataSource.setTestConnectionOnCheckin(ds.getTestConnectionOnCheckin());
			//dataSource.setTestConnectionOnCheckout(ds.getTestConnectionOnCheckout());
			dataSource.setMaxIdleTime(ds.getMaxIdleTime());
			dataSource.setAcquireIncrement(2);
			DruidDataSource druidDataSource = (DruidDataSource) FieldUtils.readField(dataSource, "dataSource", true);
			if (BooleanUtils.isTrue(ds.getStatEnable())) {
				dataSource.setFilters(ds.getStatFilter());
				if (BooleanUtils.isTrue(ds.getSlowSqlEnable())) {
					Properties properties = new Properties();
					properties.setProperty("druid.stat.slowSqlMillis", ds.getSlowSqlThreshold().toString());
					properties.setProperty("druid.stat.logSlowSql", "true");
					dataSource.setProperties(properties);
				}
				if (BooleanUtils.isTrue(ds.getLogStatEnable())) {
					druidDataSource.setStatLogger(new DruidStatLogger());
					druidDataSource.setTimeBetweenLogStatsMillis(60000L * ds.getLogStatInterval());
				}
			}
			// 增加autoCommit设置
			dataSource.setAutoCommitOnClose(ds.getAutoCommit());
			String name = Config.externalDataSources().name(ds);
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
			list.add(dataSource);
		}
		return list;
	}

	/**
	 * internal 使用的是H2 server,在执行close dataserver已经完成了数据库关闭,dataSource无法destory.
	 * 
	 * @throws Exception
	 */
	private static void internal() throws Exception {
		internalDriudC3p0();
	}

	/**
	 * @author ray Druid DataSource 是需要close的.
	 */
	public static void destory() {
		for (DruidDataSourceC3P0Adapter dataSource : dataSources) {
			dataSource.close();
		}
	}

	private static List<DruidDataSourceC3P0Adapter> internalDriudC3p0() throws Exception {
		List<DruidDataSourceC3P0Adapter> list = new ArrayList<>();
		for (Entry<String, DataServer> entry : Config.nodes().dataServers().entrySet()) {
			DruidDataSourceC3P0Adapter dataSource = new DruidDataSourceC3P0Adapter();
			String url = "jdbc:h2:tcp://" + entry.getKey() + ":" + entry.getValue().getTcpPort() + "/"
					+ H2Tools.DATABASE + ";LOCK_MODE=0;DEFAULT_LOCK_TIMEOUT=" + entry.getValue().getLockTimeout()
					+ ";JMX=" + (BooleanUtils.isTrue(entry.getValue().getJmxEnable()) ? "TRUE" : "FALSE")
					+ ";CACHE_SIZE=" + (entry.getValue().getCacheSize() * 1024);
			dataSource.setJdbcUrl(url);
			dataSource.setDriverClass(SlicePropertiesBuilder.driver_h2);
			//dataSource.setPreferredTestQuery(SlicePropertiesBuilder.validationQueryOfUrl(url));
			dataSource.setUser(H2Tools.USER);
			dataSource.setPassword(Config.token().getPassword());
			dataSource.setMaxPoolSize(entry.getValue().getMaxTotal());
			dataSource.setMinPoolSize(entry.getValue().getMaxIdle());
			dataSource.setAcquireIncrement(2);
			DruidDataSource druidDataSource = (DruidDataSource) FieldUtils.readField(dataSource, "dataSource", true);
			if (BooleanUtils.isTrue(entry.getValue().getStatEnable())) {
				dataSource.setFilters(entry.getValue().getStatFilter());
				if (BooleanUtils.isTrue(entry.getValue().getSlowSqlEnable())) {
					Properties properties = new Properties();
					properties.setProperty("druid.stat.slowSqlMillis",
							entry.getValue().getSlowSqlThreshold().toString());
					properties.setProperty("druid.stat.logSlowSql", "true");
					dataSource.setProperties(properties);
				}
				if (BooleanUtils.isTrue(entry.getValue().getLogStatEnable())) {
					druidDataSource.setStatLogger(new DruidStatLogger());
					druidDataSource.setTimeBetweenLogStatsMillis(60000L * entry.getValue().getLogStatInterval());
				}
			}
			// 增加autoCommit设置
			dataSource.setAutoCommitOnClose(false);
			String name = Config.nodes().dataServers().name(entry.getValue());
			new Resource(Config.RESOURCE_JDBC_PREFIX + name, dataSource);
			list.add(dataSource);
		}
		return list;
	}

	private static void tokenThresholds() throws NamingException {
		// java8中无法将 <> 与匿名内部类一起使用,所以这里需要进行类型的申明
		Map<String, Date> linkedHashMap = new LinkedHashMap<String, Date>() {
			private static final long serialVersionUID = 2324816564609476854L;

			@Override
			protected boolean removeEldestEntry(Entry<String, Date> entry) {
				return size() > TOKENTHRESHOLDSMAXSIZE;
			}
		};
		new Resource(Config.RESOURCE_NODE_TOKENTHRESHOLDS, Collections.synchronizedMap(linkedHashMap));
	}

}
