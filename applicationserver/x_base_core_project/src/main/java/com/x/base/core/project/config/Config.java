package com.x.base.core.project.config;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.MimeTypes;

import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.Packages;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class Config {

	private static Config INSTANCE;

	private Config() {
	}

	public static final String PATH_VERSION = "version.o2";
	public static final String PATH_CONFIG = "config";
	public static final String PATH_LOCAL_NODE = "local/node.cfg";
	public static final String PATH_CONFIG_TOKEN = "config/token.json";
	public static final String PATH_CONFIG_EXTERNALDATASOURCES = "config/externalDataSources.json";
	public static final String PATH_CONFIG_ADMINISTRATOR = "config/administrator.json";
	public static final String PATH_CONFIG_PERSON = "config/person.json";
	public static final String PATH_CONFIG_MEETING = "config/meeting.json";
	public static final String PATH_CONFIG_APPSTYLE = "config/appStyle.json";
	public static final String PATH_CONFIG_VFS = "config/vfs.json";
	public static final String PATH_CONFIG_WORKTIME = "config/workTime.json";
	public static final String PATH_CONFIG_CENTERSERVER = "config/centerServer.json";
	public static final String PATH_CONFIG_COLLECT = "config/collect.json";
	public static final String PATH_CONFIG_DUMPRESTOREDATA = "config/dumpRestoreData.json";
	public static final String PATH_CONFIG_DUMPRESTORESTORAGE = "config/dumpRestoreStorage.json";
	public static final String PATH_CONFIG_MESSAGES = "config/messages.json";
	public static final String PATH_CONFIG_SSLKEYSTORE = "config/keystore";
	public static final String PATH_CONFIG_SSLKEYSTORESAMPLE = "config/sample/keystore";
	public static final String PATH_CONFIG_STARTIMAGE = "config/startImage.png";
	public static final String PATH_CONFIG_PUBLICKEY = "config/public.key";
	public static final String PATH_CONFIG_PRIVATEKEY = "config/private.key";
	public static final String PATH_CONFIG_PROCESSPLATFORM = "config/processPlatform.json";
	public static final String PATH_CONFIG_DINGDING = "config/dingding.json";
	public static final String PATH_CONFIG_ZHENGWUDINGDING = "config/zhengwuDingding.json";
	public static final String PATH_CONFIG_QIYEWEIXIN = "config/qiyeweixin.json";
	public static final String PATH_CONFIG_LOGLEVEL = "config/logLevel.json";

	private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWcVZIS57VeOUzi8c01WKvwJK9uRe6hrGTUYmF6J/pI6/UvCbdBWCoErbzsBZOElOH8Sqal3vsNMVLjPYClfoDyYDaUlakP3ldfnXJzAFJVVubF53KadG+fwnh9ZMvxdh7VXVqRL3IQBDwGgzX4rmSK+qkUJjc3OkrNJPB7LLD8QIDAQAB";
	private static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJZxVkhLntV45TOLxzTVYq/Akr25F7qGsZNRiYXon+kjr9S8Jt0FYKgStvOwFk4SU4fxKpqXe+w0xUuM9gKV+gPJgNpSVqQ/eV1+dcnMAUlVW5sXncpp0b5/CeH1ky/F2HtVdWpEvchAEPAaDNfiuZIr6qRQmNzc6Ss0k8HsssPxAgMBAAECgYAWtRy05NUgm5Lc6Og0jVDL/mEnydxPBy2ectwzHh2k7wIHNi8XhUxFki2TMqzrM9Dv3/LySpMl4AE3mhs34LNPy6F+MwyF5X7j+2Y6MflJyeb9HNyT++viysQneoOEiOk3ghxF2/GPjpiEF79wSp+1YKTxRAyq7ypV3t35fGOOEQJBANLDPWl8b5c3lrcz/dTamMjHbVamEyX43yzQOphzkhYsz4pruATzTxU+z8/zPdEqHcWWV39CP3xu3EYNcAhxJW8CQQC2u7PF5Xb1xYRCsmIPssFxil64vvdUadSxl7GLAgjQ9ULyYWB24KObCEzLnPcT8Pf2Q0YQOixxa/78FuzmgbyfAkA7ZFFV/H7lugB6t+f7p24OhkRFep9CwBMD6dnZRBgSr6X8d8ZvfrD2Z7DgBMeSva+OEoOtlNmXExZ3lynO9zN5AkAVczEmIMp3DSl6XtAuAZC9kD2QODJ2QToLYsAfjiyUwsWKCC43piTuVOoW2KUUPSwOR1VZIEsJQWEcHGDQqhgHAkAeZ7a6dVRZFdBwKA0ADjYCufAW2cIYiVDQBJpgB+kiLQflusNOCBK0FT3lg8BdUSy2D253Ih6l3lbaM/4M7DFQ";

	public static void flush() {
		if (null != INSTANCE) {
			synchronized (Config.class) {
				if (null != INSTANCE) {
					INSTANCE = null;
				}
			}
		}
	}

	private static Config instance() throws Exception {
		if (null == INSTANCE) {
			synchronized (Config.class) {
				if (null == INSTANCE) {
					INSTANCE = new Config();
				}
			}
		}
		return INSTANCE;
	}

	private String version;

	public static String version() throws Exception {
		if (null == instance().version) {
			synchronized (Config.class) {
				if (null == instance().version) {
					instance().version = BaseTools.readCfg(PATH_VERSION);
				}
			}
		}
		return instance().version;
	}

	private String node;

	public static String node() throws Exception {
		if (null == instance().node) {
			synchronized (Config.class) {
				if (null == instance().node) {
					instance().node = BaseTools.readCfg(PATH_LOCAL_NODE, Host.ROLLBACK_IPV4);
				}
			}
		}
		return instance().node;
	}

	private String base;

	public static String base() throws Exception {
		if (null == instance().base) {
			synchronized (Config.class) {
				if (null == instance().base) {
					instance().base = BaseTools.getBasePath();
				}
			}
		}
		return instance().base;
	}

	private Nodes nodes;

	public static Nodes nodes() throws Exception {
		if (null == instance().nodes) {
			synchronized (Config.class) {
				if (null == instance().nodes) {
					Nodes nodes = new Nodes();
					String base = BaseTools.getBasePath();
					FileFilter fileFilter = new WildcardFileFilter("node_*.json");
					File dir = new File(base, PATH_CONFIG);
					File[] files = dir.listFiles(fileFilter);
					if (null != files && files.length > 0) {
						for (File o : files) {
							String name = StringUtils.substringBetween(o.getName(), "node_", ".json");
							Node node = BaseTools.readObject(PATH_CONFIG + "/" + o.getName(), Node.class);
							if (StringUtils.isNotEmpty(name) && BooleanUtils.isTrue(node.getEnable())) {
								nodes.put(name, node);
							}
						}
					} else {
						Node o = Node.defaultInstance();
						nodes.put(node(), o);
					}
					instance().nodes = nodes;
				}
			}
		}
		return instance().nodes;
	}

	private Token token;

	public static Token token() throws Exception {
		if (null == instance().token) {
			synchronized (Config.class) {
				if (null == instance().token) {
					Token o = BaseTools.readObject(PATH_CONFIG_TOKEN, Token.class);
					if (null == o) {
						o = Token.defaultInstance();
					}
					instance().token = o;
				}
			}
		}
		return instance().token;
	}

	private ExternalDataSources externalDataSources;

	public static ExternalDataSources externalDataSources() throws Exception {
		if (null == instance().externalDataSources) {
			synchronized (Config.class) {
				if (null == instance().externalDataSources) {
					ExternalDataSources obj = BaseTools.readObject(PATH_CONFIG_EXTERNALDATASOURCES,
							ExternalDataSources.class);
					if (null == obj) {
						obj = ExternalDataSources.defaultInstance();
					}
					instance().externalDataSources = obj;
				}
			}
		}
		return instance().externalDataSources;
	}

	private String publicKey;

	public static String publicKey() throws Exception {
		if (null == instance().publicKey) {
			synchronized (Config.class) {
				if (null == instance().publicKey) {
					File file = new File(Config.base(), PATH_CONFIG_PUBLICKEY);
					if (file.exists() && file.isFile()) {
						instance().publicKey = FileUtils.readFileToString(file);
					} else {
						instance().publicKey = DEFAULT_PUBLIC_KEY;
					}
				}
			}
		}
		return instance().publicKey;
	}

	private String privateKey;

	public static String privateKey() throws Exception {
		if (null == instance().privateKey) {
			synchronized (Config.class) {
				if (null == instance().privateKey) {
					File file = new File(Config.base(), PATH_CONFIG_PRIVATEKEY);
					if (file.exists() && file.isFile()) {
						instance().privateKey = FileUtils.readFileToString(file);
					} else {
						instance().privateKey = DEFAULT_PRIVATE_KEY;
					}
				}
			}
		}
		return instance().privateKey;
	}

	private Person person = null;

	public static Person person() throws Exception {
		if (null == instance().person) {
			synchronized (Config.class) {
				if (null == instance().person) {
					Person obj = BaseTools.readObject(PATH_CONFIG_PERSON, Person.class);
					if (null == obj) {
						obj = Person.defaultInstance();
					}
					instance().person = obj;
				}
			}
		}
		return instance().person;
	}

	private Meeting meeting;

	public static Meeting meeting() throws Exception {
		if (null == instance().meeting) {
			synchronized (Config.class) {
				if (null == instance().meeting) {
					Meeting obj = BaseTools.readObject(PATH_CONFIG_MEETING, Meeting.class);
					if (null == obj) {
						obj = Meeting.defaultInstance();
					}
					instance().meeting = obj;
				}
			}
		}
		return instance().meeting;
	}

	private com.x.base.core.project.utils.time.WorkTime workTime;

	public static com.x.base.core.project.utils.time.WorkTime workTime() throws Exception {
		if (null == instance().workTime) {
			synchronized (Config.class) {
				if (null == instance().workTime) {
					com.x.base.core.project.config.WorkTime obj = BaseTools.readObject(PATH_CONFIG_WORKTIME,
							com.x.base.core.project.config.WorkTime.class);
					if (null == obj) {
						obj = com.x.base.core.project.config.WorkTime.defaultInstance();
					}
					instance().workTime = new com.x.base.core.project.utils.time.WorkTime(obj.getAmStart(),
							obj.getAmEnd(), obj.getPmStart(), obj.getPmEnd(), obj.getHolidays(), obj.getWorkdays(),
							obj.getWeekends());
				}
			}
		}
		return instance().workTime;
	}

	public CenterServer centerServer;

	public static CenterServer centerServer() throws Exception {
		if (null == instance().centerServer) {
			synchronized (Config.class) {
				if (null == instance().centerServer) {
					CenterServer obj = BaseTools.readObject(PATH_CONFIG_CENTERSERVER, CenterServer.class);
					if (null == obj) {
						obj = CenterServer.defaultInstance();
					}
					instance().centerServer = obj;
				}
			}
		}
		return instance().centerServer;
	}

	public Collect collect;

	public static Collect collect() throws Exception {
		if (null == instance().collect) {
			synchronized (Config.class) {
				if (null == instance().collect) {
					Collect obj = BaseTools.readObject(PATH_CONFIG_COLLECT, Collect.class);
					if (null == obj) {
						obj = Collect.defaultInstance();
					}
					instance().collect = obj;

				}
			}
		}
		return instance().collect;
	}

	public DumpRestoreData dumpRestoreData;

	public static DumpRestoreData dumpRestoreData() throws Exception {
		if (null == instance().dumpRestoreData) {
			synchronized (Config.class) {
				if (null == instance().dumpRestoreData) {
					DumpRestoreData obj = BaseTools.readObject(PATH_CONFIG_DUMPRESTOREDATA, DumpRestoreData.class);
					if (null == obj) {
						obj = DumpRestoreData.defaultInstance();
					}
					instance().dumpRestoreData = obj;
				}
			}
		}
		return instance().dumpRestoreData;
	}

	public DumpRestoreStorage dumpRestoreStorage;

	public static DumpRestoreStorage dumpRestoreStorage() throws Exception {
		if (null == instance().dumpRestoreStorage) {
			synchronized (Config.class) {
				if (null == instance().dumpRestoreStorage) {
					DumpRestoreStorage obj = BaseTools.readObject(PATH_CONFIG_DUMPRESTORESTORAGE,
							DumpRestoreStorage.class);
					if (null == obj) {
						obj = DumpRestoreStorage.defaultInstance();
					}
					instance().dumpRestoreStorage = obj;
				}
			}
		}
		return instance().dumpRestoreStorage;
	}

	public String initialScriptText;

	public static String initialScriptText() throws Exception {
		if (null == instance().initialScriptText) {
			synchronized (Config.class) {
				if (null == instance().initialScriptText) {
					instance().initialScriptText = BaseTools.readString("commons/initialScriptText.js");
				}
			}
		}
		return instance().initialScriptText;
	}

	private MimeTypes mimeTypes;

	public static MimeTypes mimeTypes() throws Exception {
		if (null == instance().mimeTypes) {
			synchronized (Config.class) {
				if (null == instance().mimeTypes) {
					MimeTypes mimeTypes = new MimeTypes();
					/* 添加o2自定义格式 */
					mimeTypes.addMimeMapping("wcss", "application/json");
					/* 添加默认格式 */
					mimeTypes.addMimeMapping("", "application/octet-stream");
					instance().mimeTypes = mimeTypes;
				}
			}
		}
		return instance().mimeTypes;
	}

	private DataMappings dataMappings;

	public static DataMappings dataMappings() throws Exception {
		if (null == instance().dataMappings) {
			synchronized (Config.class) {
				if (null == instance().dataMappings) {
					instance().dataMappings = dataMappingsInit();
				}
			}
		}
		return instance().dataMappings;
	}

	private StorageMappings storageMappings;

	public static StorageMappings storageMappings() throws Exception {
		if (null == instance().storageMappings) {
			synchronized (Config.class) {
				if (null == instance().storageMappings) {
					instance().storageMappings = new StorageMappings(nodes());
				}
			}
		}
		return instance().storageMappings;
	}

	private static DataMappings dataMappingsInit() throws Exception {
		DataMappings dataMappings = new DataMappings();
		List<Class<?>> classes = dataMappingsScanEntities();
		for (Class<?> clz : classes) {
			dataMappings.put(clz.getName(), new CopyOnWriteArrayList<DataMapping>());
		}
		if (externalDataSources().enable()) {
			return dataMappingsInitExternal(dataMappings, classes);
		} else {
			return dataMappingsInitInternal(dataMappings, classes);
		}
	}

	private static DataMappings dataMappingsInitExternal(DataMappings dataMappings, List<Class<?>> classes)
			throws Exception {
		ExternalDataSources externalDataSources = Config.externalDataSources();
		if (externalDataSources.size() == 0) {
			throw new Exception("externalDataSources is empty.");
		}
		if (externalDataSources.size() == 1) {
			// 如果只有一个数据源那么不用考虑includes 和 excludes
			ExternalDataSource source = externalDataSources.get(0);
			for (Class<?> cls : classes) {
				DataMapping o = new DataMapping();
				o.setUrl(source.getUrl());
				o.setUsername(source.getUsername());
				o.setPassword(source.getPassword());
				dataMappings.get(cls.getName()).add(o);
				if (null != source.getToolLevel()) {
					o.setToolLevel(source.getToolLevel());
				}
				if (null != source.getRuntimeLevel()) {
					o.setRuntimeLevel(source.getRuntimeLevel());
				}
				if (null != source.getDataCacheLevel()) {
					o.setDataCacheLevel(source.getDataCacheLevel());
				}
				if (null != source.getMetaDataLevel()) {
					o.setMetaDataLevel(source.getMetaDataLevel());
				}
				if (null != source.getEnhanceLevel()) {
					o.setEnhanceLevel(source.getEnhanceLevel());
				}
				if (null != source.getQueryLevel()) {
					o.setQueryLevel(source.getQueryLevel());
				}
				if (null != source.getSqlLevel()) {
					o.setSqlLevel(source.getSqlLevel());
				}
				if (null != source.getJdbcLevel()) {
					o.setJdbcLevel(source.getJdbcLevel());
				}
			}
		} else {
			// 如果有多个数据源那么要考虑includes 和 excludes
			for (ExternalDataSource source : externalDataSources) {
				List<String> names = new ArrayList<>();
				for (Class<?> cls : classes) {
					names.add(cls.getName());
				}
				if (ListTools.isNotEmpty(source.getIncludes())) {
					names = ListUtils.intersection(names, source.getIncludes());
				}
				if (ListTools.isNotEmpty(source.getExcludes())) {
					names = ListUtils.subtract(names, source.getExcludes());
				}
				for (String str : names) {
					DataMapping o = new DataMapping();
					o.setUrl(source.getUrl());
					o.setUsername(source.getUsername());
					o.setPassword(source.getPassword());
					dataMappings.get(str).add(o);
					if (null != source.getToolLevel()) {
						o.setToolLevel(source.getToolLevel());
					}
					if (null != source.getRuntimeLevel()) {
						o.setRuntimeLevel(source.getRuntimeLevel());
					}
					if (null != source.getDataCacheLevel()) {
						o.setDataCacheLevel(source.getDataCacheLevel());
					}
					if (null != source.getMetaDataLevel()) {
						o.setMetaDataLevel(source.getMetaDataLevel());
					}
					if (null != source.getEnhanceLevel()) {
						o.setEnhanceLevel(source.getEnhanceLevel());
					}
					if (null != source.getQueryLevel()) {
						o.setQueryLevel(source.getQueryLevel());
					}
					if (null != source.getSqlLevel()) {
						o.setSqlLevel(source.getSqlLevel());
					}
					if (null != source.getJdbcLevel()) {
						o.setJdbcLevel(source.getJdbcLevel());
					}
				}
			}
		}
		return dataMappings;
	}

	private static DataMappings dataMappingsInitInternal(DataMappings dataMappings, List<Class<?>> classes)
			throws Exception {
		DataServers dataServers = Config.nodes().dataServers();
		if (dataServers.size() == 0) {
			throw new Exception("dataServers is empty.");
		}
		if (dataServers.size() == 1) {
			for (Class<?> cls : classes) {
				DataMapping o = new DataMapping();
				String url = "jdbc:h2:tcp://" + dataServers.firstKey() + ":"
						+ dataServers.firstEntry().getValue().getTcpPort() + "/X;JMX="
						+ (dataServers.firstEntry().getValue().getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
						+ (dataServers.firstEntry().getValue().getCacheSize() * 1024);
				o.setUrl(url);
				o.setUsername("sa");
				o.setPassword(Config.token().getPassword());
				dataMappings.get(cls.getName()).add(o);
			}
		} else {
			for (Entry<String, DataServer> entry : dataServers.entrySet()) {
				String node = entry.getKey();
				DataServer server = entry.getValue();
				List<String> names = new ArrayList<>();
				for (Class<?> cls : classes) {
					names.add(cls.getName());
				}
				if (ListTools.isNotEmpty(server.getIncludes())) {
					names = ListUtils.intersection(names, server.getIncludes());
				}
				if (ListTools.isNotEmpty(server.getExcludes())) {
					names = ListUtils.subtract(names, server.getExcludes());
				}
				for (String str : names) {
					DataMapping o = new DataMapping();
					String url = "jdbc:h2:tcp://" + node + ":" + server.getTcpPort() + "/X;JMX="
							+ (server.getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
							+ (server.getCacheSize() * 1024);
					o.setUrl(url);
					o.setUsername("sa");
					o.setPassword(Config.token().getPassword());
					dataMappings.get(str).add(o);
				}
			}
		}
		return dataMappings;
	}

	private File sslKeyStore;

	public static File sslKeyStore() throws Exception {
		if (null == instance().sslKeyStore) {
			synchronized (Config.class) {
				if (null == instance().sslKeyStore) {
					File file = new File(BaseTools.getBasePath(), PATH_CONFIG_SSLKEYSTORE);
					if ((!file.exists()) || file.isDirectory()) {
						file = new File(BaseTools.getBasePath(), PATH_CONFIG_SSLKEYSTORESAMPLE);
					}
					instance().sslKeyStore = file;
				}
			}
		}
		return instance().sslKeyStore;
	}

	private static List<Class<?>> dataMappingsScanEntities() throws Exception {
		ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
		List<String> names = scanResult.getNamesOfClassesWithAnnotation(ContainerEntity.class);
		List<Class<?>> list = new ArrayList<>();
		for (String str : names) {
			list.add(Class.forName(str));
		}
		return list;
	}

	public static Node currentNode() throws Exception {
		return nodes().get(node());
	}

	public static String x_program_centerUrlRoot() throws Exception {
		String primary = nodes().primaryCenterNode();
		StringBuffer buffer = new StringBuffer();
		if (centerServer().getSslEnable()) {
			buffer.append("https://").append(primary);
			if (!NumberTools.valueEuqals(Config.centerServer().getPort(), 443)) {
				buffer.append(":").append(Config.centerServer().getPort());
			}
		} else {
			buffer.append("http://").append(primary);
			if (!NumberTools.valueEuqals(Config.centerServer().getPort(), 80)) {
				buffer.append(":").append(Config.centerServer().getPort());
			}
		}
		buffer.append("/").append(x_program_center.class.getSimpleName());
		buffer.append("/jaxrs/");
		return buffer.toString();
	}

	private Messages messages;

	public static Messages messages() throws Exception {
		if (null == instance().messages) {
			synchronized (Config.class) {
				if (null == instance().messages) {
					Messages obj = Messages.defaultInstance();
					Messages custom = BaseTools.readObject(PATH_CONFIG_MESSAGES, Messages.class);
					if (null != custom) {
						custom.entrySet().stream().forEach(o -> {
							List<String> consumers = obj.getConsumers(o.getKey());
							consumers = ListUtils.union(consumers,
									ListTools.trim(o.getValue().getConsumers(), true, true));
							obj.put(o.getKey(), new Message(consumers));
						});
					}
					instance().messages = obj;
				}
			}
		}
		return instance().messages;
	}

	private ProcessPlatform processPlatform;

	public static ProcessPlatform processPlatform() throws Exception {
		if (null == instance().processPlatform) {
			synchronized (Config.class) {
				if (null == instance().processPlatform) {
					ProcessPlatform obj = BaseTools.readObject(PATH_CONFIG_PROCESSPLATFORM, ProcessPlatform.class);
					if (null == obj) {
						obj = ProcessPlatform.defaultInstance();
					}
					instance().processPlatform = obj;
				}
			}
		}
		return instance().processPlatform;
	}

	private Dingding dingding;

	public static Dingding dingding() throws Exception {
		if (null == instance().dingding) {
			synchronized (Config.class) {
				if (null == instance().dingding) {
					Dingding obj = BaseTools.readObject(PATH_CONFIG_DINGDING, Dingding.class);
					if (null == obj) {
						obj = Dingding.defaultInstance();
					}
					instance().dingding = obj;
				}
			}
		}
		return instance().dingding;
	}

	private Qiyeweixin qiyeweixin;

	public static Qiyeweixin qiyeweixin() throws Exception {
		if (null == instance().qiyeweixin) {
			synchronized (Config.class) {
				if (null == instance().qiyeweixin) {
					Qiyeweixin obj = BaseTools.readObject(PATH_CONFIG_QIYEWEIXIN, Qiyeweixin.class);
					if (null == obj) {
						obj = Qiyeweixin.defaultInstance();
					}
					instance().qiyeweixin = obj;
				}
			}
		}
		return instance().qiyeweixin;
	}

	private ZhengwuDingding zhengwuDingding;

	public static ZhengwuDingding zhengwuDingding() throws Exception {
		if (null == instance().zhengwuDingding) {
			synchronized (Config.class) {
				if (null == instance().zhengwuDingding) {
					ZhengwuDingding obj = BaseTools.readObject(PATH_CONFIG_ZHENGWUDINGDING, ZhengwuDingding.class);
					if (null == obj) {
						obj = ZhengwuDingding.defaultInstance();
					}
					instance().zhengwuDingding = obj;
				}
			}
		}
		return instance().zhengwuDingding;
	}

	private Vfs vfs;

	public static Vfs vfs() throws Exception {
		if (null == instance().vfs) {
			synchronized (Config.class) {
				if (null == instance().vfs) {
					Vfs obj = BaseTools.readObject(PATH_CONFIG_VFS, Vfs.class);
					if (null == obj) {
						obj = Vfs.defaultInstance();
					}
					instance().vfs = obj;
				}
			}
		}
		return instance().vfs;
	}

	private AppStyle appStyle;

	public static AppStyle appStyle() throws Exception {
		if (null == instance().appStyle) {
			synchronized (Config.class) {
				if (null == instance().appStyle) {
					AppStyle obj = BaseTools.readObject(PATH_CONFIG_APPSTYLE, AppStyle.class);
					if (null == obj) {
						obj = AppStyle.defaultInstance();
					}
					instance().appStyle = obj;
				}
			}
		}
		return instance().appStyle;
	}

	private File startImage;

	public static File startImage() throws Exception {
		if (null == instance().startImage) {
			synchronized (Config.class) {
				if (null == instance().startImage) {
					File file = new File(BaseTools.getBasePath(), PATH_CONFIG_STARTIMAGE);
					if (file.exists() && file.isFile()) {
						instance().startImage = file;
					} else {
						instance().startImage = null;
					}
				}
			}
		}
		return instance().startImage;
	}

	private LogLevel logLevel;

	public static LogLevel logLevel() throws Exception {
		if (null == instance().logLevel) {
			synchronized (Config.class) {
				if (null == instance().logLevel) {
					LogLevel obj = BaseTools.readObject(PATH_CONFIG_LOGLEVEL, LogLevel.class);
					if (null == obj) {
						obj = LogLevel.defaultInstance();
					}
					instance().logLevel = obj;
				}
			}
		}
		return instance().logLevel;
	}

}