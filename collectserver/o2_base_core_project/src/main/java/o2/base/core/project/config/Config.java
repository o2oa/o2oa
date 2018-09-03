package o2.base.core.project.config;

import org.eclipse.jetty.http.MimeTypes;

import com.x.base.core.project.tools.BaseTools;

public class Config {

	private static Config INSTANCE;

	private Config() {
	}

	public static final String PATH_VERSION = "version.o2";
	public static final String PATH_CONFIG = "config";
	public static final String PATH_CONFIG_TOKEN = "config/token.json";

	public static final String PATH_CONFIG_UPDATE = "config/update.json";
	public static final String PATH_CONFIG_APPLICATIONSERVER = "config/applicationServer.json";
	public static final String PATH_CONFIG_WEBSERVER = "config/webServer.json";
	public static final String PATH_CONFIG_DATASERVER = "config/dataServer.json";

	public static final String PATH_CONFIG_DEMOSITE = "config/demoSite.sjon";

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

	private ApplicationServer applicationServer;

	public static ApplicationServer applicationServer() throws Exception {
		if (null == instance().applicationServer) {
			synchronized (Config.class) {
				if (null == instance().applicationServer) {
					ApplicationServer o = BaseTools.readObject(PATH_CONFIG_APPLICATIONSERVER, ApplicationServer.class);
					if (null == o) {
						o = ApplicationServer.defaultInstance();
					}
					instance().applicationServer = o;
				}
			}
		}
		return instance().applicationServer;
	}

	private Update update;

	public static Update update() throws Exception {
		if (null == instance().update) {
			synchronized (Config.class) {
				if (null == instance().update) {
					Update o = BaseTools.readObject(PATH_CONFIG_UPDATE, Update.class);
					if (null == o) {
						o = Update.defaultInstance();
					}
					instance().update = o;
				}
			}
		}
		return instance().update;
	}

	private WebServer webServer;

	public static WebServer webServer() throws Exception {
		if (null == instance().webServer) {
			synchronized (Config.class) {
				if (null == instance().webServer) {
					WebServer o = BaseTools.readObject(PATH_CONFIG_WEBSERVER, WebServer.class);
					if (null == o) {
						o = WebServer.defaultInstance();
					}
					instance().webServer = o;
				}
			}
		}
		return instance().webServer;
	}

	private DataServer dataServer;

	public static DataServer dataServer() throws Exception {
		if (null == instance().dataServer) {
			synchronized (Config.class) {
				if (null == instance().dataServer) {
					DataServer o = BaseTools.readObject(PATH_CONFIG_DATASERVER, DataServer.class);
					if (null == o) {
						o = DataServer.defaultInstance();
					}
					instance().dataServer = o;
				}
			}
		}
		return instance().dataServer;
	}

	private DemoSite demoSite;

	public static DemoSite demoSite() throws Exception {
		if (null == instance().demoSite) {
			synchronized (Config.class) {
				if (null == instance().demoSite) {
					DemoSite o = BaseTools.readObject(PATH_CONFIG_DEMOSITE, DemoSite.class);
					if (null == o) {
						o = DemoSite.defaultInstance();
					}
					instance().demoSite = o;
				}
			}
		}
		return instance().demoSite;
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

}