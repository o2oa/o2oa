package com.x.base.core.project.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.BaseTools;
import com.x.base.core.Packages;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.utils.ListTools;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class Config {

	private static Config INSTANCE;

	private Config() {
	}

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

	private String node;

	public static String node() throws Exception {
		if (null == instance().node) {
			synchronized (Config.class) {
				if (null == instance().node) {
					instance().node = BaseTools.readCfg("local/node.cfg", "127.0.0.1");
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

	// private String password;
	//
	// public static String password() throws Exception {
	// if (null == instance().password) {
	// synchronized (Config.class) {
	// if (null == instance().password) {
	// instance().password = BaseTools.readCfg("config/password.cfg",
	// "o2server");
	// // if (StringUtils.isEmpty(instance().password)) {
	// // throw new Exception("read a empty password.");
	// // }
	// }
	// }
	// }
	// return instance().password;
	// }

	// public static String passwordKey() throws Exception {
	// if (null == instance().passwordKey) {
	// synchronized (Config.class) {
	// if (null == instance().passwordKey) {
	// instance().passwordKey = BaseTools.readCfg("config/passwordKey.cfg",
	// "o2platform");
	// }
	// }
	// }
	// return instance().passwordKey;
	// }

	// private String cipher;
	//
	// public static String cipher() throws Exception {
	// if (null == instance().cipher) {
	// synchronized (Config.class) {
	// if (null == instance().cipher) {
	// instance().cipher = password() + "o2platform";
	// }
	// }
	// }
	// return instance().cipher;
	// }

	private Nodes nodes;

	public static Nodes nodes() throws Exception {
		if (null == instance().nodes) {
			synchronized (Config.class) {
				if (null == instance().nodes) {
					Nodes nodes = new Nodes();
					String base = BaseTools.getBasePath();
					File dir = new File(base, "config");
					FileFilter fileFilter = new WildcardFileFilter("node_*.json");
					File[] files = dir.listFiles(fileFilter);
					if (null == files) {
						throw new Exception("can not find any node file.");
					}
					for (File o : files) {
						String name = StringUtils.substringBetween(o.getName(), "node_", ".json");
						Node node = BaseTools.readObject("config/" + o.getName(), Node.class);
						if (StringUtils.isNotEmpty(name) && BooleanUtils.isTrue(node.getEnable())) {
							nodes.put(name, node);
						}
					}
					instance().nodes = nodes;
				}
			}
		}
		return instance().nodes;
	}

	// private Boolean externalDataSourceEnable;
	//
	// public static Boolean externalDataSourceEnable() throws Exception {
	// if (null == instance().externalDataSourceEnable) {
	// synchronized (Config.class) {
	// if (null == instance().externalDataSourceEnable) {
	// String val = BaseTools.readCfg("config/externalDataSourceEnable.cfg",
	// "false");
	// instance().externalDataSourceEnable = BooleanUtils.toBooleanObject(val);
	// }
	// }
	// }
	// return instance().externalDataSourceEnable;
	// }

	private Token token;

	public static Token token() throws Exception {
		if (null == instance().token) {
			synchronized (Config.class) {
				if (null == instance().token) {
					instance().token = BaseTools.readObject("config/token.json", Token.class);
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
					ExternalDataSources obj = BaseTools.readObject("config/externalDataSources.json",
							ExternalDataSources.class);
					instance().externalDataSources = obj;
				}
			}
		}
		return instance().externalDataSources;
	}

	private Administrator administrator;

	public static Administrator administrator() throws Exception {
		if (null == instance().administrator) {
			synchronized (Config.class) {
				if (null == instance().administrator) {
					Administrator obj = BaseTools.readObject("config/administrator.json", Administrator.class);
					instance().administrator = obj;
				}
			}
		}
		return instance().administrator;
	}

	private PersonTemplate personTemplate;

	public static PersonTemplate personTemplate() throws Exception {
		if (null == instance().personTemplate) {
			synchronized (Config.class) {
				if (null == instance().personTemplate) {
					PersonTemplate obj = BaseTools.readObject("config/personTemplate.json", PersonTemplate.class);
					instance().personTemplate = obj;
				}
			}
		}
		return instance().personTemplate;
	}

	private OpenMeetingJunction openMeetingJunction;

	public static OpenMeetingJunction openMeetingJunction() throws Exception {
		if (null == instance().openMeetingJunction) {
			synchronized (Config.class) {
				if (null == instance().openMeetingJunction) {
					OpenMeetingJunction obj = BaseTools.readObject("config/openMeetingJunction.json",
							OpenMeetingJunction.class);
					instance().openMeetingJunction = obj;
				}
			}
		}
		return instance().openMeetingJunction;
	}

	private WorkTimeConfig workTimeConfig;

	public static WorkTimeConfig workTimeConfig() throws Exception {
		if (null == instance().workTimeConfig) {
			synchronized (Config.class) {
				if (null == instance().workTimeConfig) {
					WorkTimeConfig obj = BaseTools.readObject("config/workTimeConfig.json", WorkTimeConfig.class);
					instance().workTimeConfig = obj;
				}
			}
		}
		return instance().workTimeConfig;
	}

	public CenterServer centerServer;

	public static CenterServer centerServer() throws Exception {
		if (null == instance().centerServer) {
			synchronized (Config.class) {
				if (null == instance().centerServer) {
					CenterServer obj = BaseTools.readObject("config/centerServer.json", CenterServer.class);
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
					Collect obj = BaseTools.readObject("config/collect.json", Collect.class);
					instance().collect = obj;
				}
			}
		}
		return instance().collect;
	}

	public DumpRestoreDataConfig dumpRestoreDataConfig;

	public static DumpRestoreDataConfig dumpRestoreDataConfig() throws Exception {
		if (null == instance().dumpRestoreDataConfig) {
			synchronized (Config.class) {
				if (null == instance().dumpRestoreDataConfig) {
					DumpRestoreDataConfig obj = BaseTools.readObject("config/dumpRestoreDataConfig.json",
							DumpRestoreDataConfig.class);
					instance().dumpRestoreDataConfig = obj;
				}
			}
		}
		return instance().dumpRestoreDataConfig;
	}

	public DumpRestoreStorageConfig dumpRestoreStorageConfig;

	public static DumpRestoreStorageConfig dumpRestoreStorageConfig() throws Exception {
		if (null == instance().dumpRestoreStorageConfig) {
			synchronized (Config.class) {
				if (null == instance().dumpRestoreStorageConfig) {
					DumpRestoreStorageConfig obj = BaseTools.readObject("config/dumpRestoreStorageConfig.json",
							DumpRestoreStorageConfig.class);
					instance().dumpRestoreStorageConfig = obj;
				}
			}
		}
		return instance().dumpRestoreStorageConfig;
	}

	public String initialScriptText;

	public static String initialScriptText() throws Exception {
		if (null == instance().initialScriptText) {
			synchronized (Config.class) {
				if (null == instance().initialScriptText) {
					instance().initialScriptText = BaseTools.readString("config/initialScriptText.js");
				}
			}
		}
		return instance().initialScriptText;
	}

	// public String passwordKey;
	//
	// public String ssoKey;
	//
	// public static String ssoKey() throws Exception {
	// if (null == instance().ssoKey) {
	// synchronized (Config.class) {
	// if (null == instance().ssoKey) {
	// instance().ssoKey = BaseTools.readCfg("config/ssoKey.cfg", "o2platform");
	// }
	// }
	// }
	// return instance().ssoKey;
	// }

	private MimetypesFileTypeMap mimeTypes;

	public static MimetypesFileTypeMap mimeTypes() throws Exception {
		if (null == instance().mimeTypes) {
			synchronized (Config.class) {
				if (null == instance().mimeTypes) {
					byte[] bytes = BaseTools.readBytes("config/mime.types");
					try (InputStream is = new ByteArrayInputStream(bytes)) {
						instance().mimeTypes = new MimetypesFileTypeMap(is);
					}
				}
			}
		}
		return instance().mimeTypes;
	}

	public DataMappings dataMappings;

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
						+ dataServers.firstEntry().getValue().getTcpPort() + "/X";
				o.setUrl(url);
				o.setUsername("sa");
				o.setPassword(dataServers.firstEntry().getValue().getCalculatedPassword());
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
					String url = "jdbc:h2:tcp://" + node + ":" + server.getTcpPort() + "/X";
					o.setUrl(url);
					o.setUsername("sa");
					String password = server.getCalculatedPassword();
					if (StringUtils.isEmpty(password)) {
						password = Config.token().getPassword();
					}
					o.setPassword(password);
					dataMappings.get(str).add(o);
				}
			}
		}
		return dataMappings;
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

	public StorageMappings storageMappings;

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

	public static Node currentNode() throws Exception {
		return nodes().get(node());
	}

}
