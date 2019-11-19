package com.x.base.core.project.config;

import java.io.File;
import java.io.FileFilter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.MimeTypes;

import com.google.gson.JsonElement;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.NumberTools;

public class Config {

	private static Config INSTANCE;

	public Config() {
	}

	public static final String PATH_VERSION = "version.o2";
	public static final String PATH_LOCAL_NODE = "local/node.cfg";
	public static final String PATH_CONFIG_TOKEN = "config/token.json";
	public static final String PATH_CONFIG_EXTERNALDATASOURCES = "config/externalDataSources.json";
	public static final String PATH_CONFIG_EXTERNALSTORAGESOURCES = "config/externalStorageSources.json";
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
	public static final String PATH_CONFIG_QUERY = "config/query.json";
	public static final String PATH_CONFIG_DINGDING = "config/dingding.json";
	public static final String PATH_CONFIG_ZHENGWUDINGDING = "config/zhengwuDingding.json";
	public static final String PATH_CONFIG_QIYEWEIXIN = "config/qiyeweixin.json";
	public static final String PATH_CONFIG_LOGLEVEL = "config/logLevel.json";
	public static final String PATH_CONFIG_BINDLOGO = "config/bindLogo.png";
	public static final String PATH_CONFIG_SLICE = "config/slice.json";
	public static final String PATH_COMMONS_INITIALSCRIPTTEXT = "commons/initialScriptText.js";
	public static final String PATH_COMMONS_INITIALSERVICESCRIPTTEXT = "commons/initialServiceScriptText.js";
	public static final String PATH_COMMONS_MOOTOOLSSCRIPTTEXT = "commons/mooToolsScriptText.js";
	public static final String PATH_CONFIG_JPUSH = "config/jpushConfig.json";
	public static final String PATH_CONFIG_COMMUNICATE = "config/communicate.json";

	public static final String DIR_COMMONS = "commons";
	public static final String DIR_COMMONS_TESS4J_TESSDATA = "commons/tess4j/tessdata";
	public static final String DIR_COMMONS_EXT = "commons/ext";
	public static final String DIR_CONFIG = "config";
	public static final String DIR_CONFIGSAMPLE = "configSample";
	public static final String DIR_CUSTOM = "custom";
	public static final String DIR_CUSTOM_JARS = "custom/jars";
	public static final String DIR_DYNAMIC = "dynamic";
	public static final String DIR_DYNAMIC_JARS = "dynamic/jars";
	public static final String DIR_JVM = "jvm";
	public static final String DIR_JVM_AIX = "jvm/aix";
	public static final String DIR_JVM_LINUX = "jvm/linux";
	public static final String DIR_JVM_MACOS = "jvm/macos";
	public static final String DIR_JVM_WINDOWS = "jvm/windows";
	public static final String DIR_JVM_NEOKYLIN_LOONGSON = "jvm/neokylin_loongson";
	public static final String DIR_LOCAL = "local";
	public static final String DIR_LOCAL_BACKUP = "local/backup";
	public static final String DIR_LOCAL_UPDATE = "local/update";
	public static final String DIR_LOCAL_TEMP = "local/temp";
	public static final String DIR_LOCAL_TEMP_CLASSES = "local/temp/classes";
	public static final String DIR_LOCAL_TEMP_DYNAMIC = "local/temp/dynamic";
	public static final String DIR_LOCAL_TEMP_DYNAMIC_SRC = "local/temp/dynamic/src";
	public static final String DIR_LOCAL_TEMP_DYNAMIC_TARGET = "local/temp/dynamic/target";
	public static final String DIR_LOCAL_TEMP_DYNAMIC_RESOURCES = "local/temp/dynamic/resources";
	public static final String DIR_LOCALSAMPLE = "localSample";
	public static final String DIR_LOGS = "logs";
	public static final String DIR_SERVERS = "servers";
	public static final String DIR_SERVERS_APPLICATIONSERVER = "servers/applicationServer";
	public static final String DIR_SERVERS_APPLICATIONSERVER_WEBAPPS = "servers/applicationServer/webapps";
	public static final String DIR_SERVERS_APPLICATIONSERVER_WORK = "servers/applicationServer/work";
	public static final String DIR_SERVERS_CENTERSERVER = "servers/centerServer";
	public static final String DIR_SERVERS_CENTERSERVER_WEBAPPS = "servers/centerServer/webapps";
	public static final String DIR_SERVERS_CENTERSERVER_WORK = "servers/centerServer/work";
	public static final String DIR_SERVERS_WEBSERVER = "servers/webServer";
	public static final String DIR_STORE = "store";
	public static final String DIR_STORE_JARS = "store/jars";

	public static final String RESOURCE_CONTAINERENTITIES = "containerEntities";

	public static final String RESOURCE_CONTAINERENTITYNAMES = "containerEntityNames";

	public static final String RESOURCE_STORAGECONTAINERENTITYNAMES = "storageContainerEntityNames";

	public static final String RESOURCE_JDBC_PREFIX = "jdbc/";

	public static final String RESOURCE_AUDITLOGPRINTSTREAM = "auditLogPrintStream";

	public static final String SCRIPTING_ENGINE_NAME = "JavaScript";

	// public static final String RESOUCE_CONFIG = "config";

	public static final String RESOURCE_NODE_PREFIX = "node/";
	public static final String RESOURCE_NODE_EVENTQUEUE = RESOURCE_NODE_PREFIX + "eventQueue";
	public static final String RESOURCE_NODE_EVENTQUEUEEXECUTOR = RESOURCE_NODE_PREFIX + "eventQueueExecutor";
	public static final String RESOURCE_NODE_APPLICATIONS = RESOURCE_NODE_PREFIX + "applications";
	public static final String RESOURCE_NODE_APPLICATIONSTIMESTAMP = RESOURCE_NODE_PREFIX + "applicationsTimestamp";
	public static final String RESOURCE_NODE_CENTERSPRIMARYNODE = RESOURCE_NODE_PREFIX + "centersPrimaryNode";
	public static final String RESOURCE_NODE_CENTERSPRIMARYPORT = RESOURCE_NODE_PREFIX + "centersPrimaryPort";
	public static final String RESOURCE_NODE_CENTERSPRIMARYSSLENABLE = RESOURCE_NODE_PREFIX + "centersPrimarySslEnable";

	private static final String DEFAULT_PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCWcVZIS57VeOUzi8c01WKvwJK9uRe6hrGTUYmF6J/pI6/UvCbdBWCoErbzsBZOElOH8Sqal3vsNMVLjPYClfoDyYDaUlakP3ldfnXJzAFJVVubF53KadG+fwnh9ZMvxdh7VXVqRL3IQBDwGgzX4rmSK+qkUJjc3OkrNJPB7LLD8QIDAQAB";
	private static final String DEFAULT_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJZxVkhLntV45TOLxzTVYq/Akr25F7qGsZNRiYXon+kjr9S8Jt0FYKgStvOwFk4SU4fxKpqXe+w0xUuM9gKV+gPJgNpSVqQ/eV1+dcnMAUlVW5sXncpp0b5/CeH1ky/F2HtVdWpEvchAEPAaDNfiuZIr6qRQmNzc6Ss0k8HsssPxAgMBAAECgYAWtRy05NUgm5Lc6Og0jVDL/mEnydxPBy2ectwzHh2k7wIHNi8XhUxFki2TMqzrM9Dv3/LySpMl4AE3mhs34LNPy6F+MwyF5X7j+2Y6MflJyeb9HNyT++viysQneoOEiOk3ghxF2/GPjpiEF79wSp+1YKTxRAyq7ypV3t35fGOOEQJBANLDPWl8b5c3lrcz/dTamMjHbVamEyX43yzQOphzkhYsz4pruATzTxU+z8/zPdEqHcWWV39CP3xu3EYNcAhxJW8CQQC2u7PF5Xb1xYRCsmIPssFxil64vvdUadSxl7GLAgjQ9ULyYWB24KObCEzLnPcT8Pf2Q0YQOixxa/78FuzmgbyfAkA7ZFFV/H7lugB6t+f7p24OhkRFep9CwBMD6dnZRBgSr6X8d8ZvfrD2Z7DgBMeSva+OEoOtlNmXExZ3lynO9zN5AkAVczEmIMp3DSl6XtAuAZC9kD2QODJ2QToLYsAfjiyUwsWKCC43piTuVOoW2KUUPSwOR1VZIEsJQWEcHGDQqhgHAkAeZ7a6dVRZFdBwKA0ADjYCufAW2cIYiVDQBJpgB+kiLQflusNOCBK0FT3lg8BdUSy2D253Ih6l3lbaM/4M7DFQ";

	private static final byte[] DEFAULT_BINDLOGO = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72,
			68, 82, 0, 0, 0, 64, 0, 0, 0, 64, 8, 6, 0, 0, 0, -86, 105, 113, -34, 0, 0, 0, 4, 103, 65, 77, 65, 0, 0, -79,
			-113, 11, -4, 97, 5, 0, 0, 0, 32, 99, 72, 82, 77, 0, 0, 122, 38, 0, 0, -128, -124, 0, 0, -6, 0, 0, 0, -128,
			-24, 0, 0, 117, 48, 0, 0, -22, 96, 0, 0, 58, -104, 0, 0, 23, 112, -100, -70, 81, 60, 0, 0, 0, 6, 98, 75, 71,
			68, 0, 0, 0, 0, 0, 0, -7, 67, -69, 127, 0, 0, 0, 9, 112, 72, 89, 115, 0, 0, 11, 18, 0, 0, 11, 18, 1, -46,
			-35, 126, -4, 0, 0, 3, 36, 73, 68, 65, 84, 120, -38, -19, -101, -51, 79, 19, 65, 24, -58, -97, 89, 49, -107,
			-125, -75, 74, 37, 86, 69, 18, 67, -62, 87, -119, 7, 77, -76, -127, -58, -125, 127, 0, -31, 35, 106, 60,
			-101, 72, 52, 24, 14, 4, 57, 122, -82, 112, -63, 24, 76, -4, 7, -60, -92, -59, -60, -77, 94, 10, 42, 28,
			-107, 34, 120, 48, -126, 32, 6, -86, -108, 30, -124, 30, -24, 122, 104, 119, 83, 118, 27, 29, 103, 63, -34,
			118, 119, 127, -57, -39, -39, -23, -13, 60, -23, -68, -99, -66, -39, 101, 40, 65, -106, -27, 65, 0, 93, 0,
			-94, 0, 66, 112, 22, 27, 0, -110, 0, 102, 24, 99, -113, -107, 65, 86, 52, 30, 0, -16, 20, -64, 13, 106,
			-107, 54, 49, 5, 96, -128, 49, -106, 81, 2, 120, -18, 34, -13, 106, 8, -116, -79, -101, -84, -8, -75, -97,
			-96, 86, 67, -60, -3, 26, 20, -10, -68, -54, -20, -89, 77, -116, 37, 82, 72, 103, 115, -44, -30, 76, 37,
			-24, -9, 97, -72, -73, 29, -99, -83, -11, -91, -61, 93, 18, 10, 5, 79, -59, -119, -26, 1, 32, -99, -51, 97,
			44, -111, -46, 14, 71, 37, 104, -86, -67, 19, -51, -1, -59, 91, 72, -94, 22, 69, -115, 23, 0, -75, 0, 106,
			106, 120, 39, 62, 27, -116, -96, -27, -20, 49, 106, -67, 92, 44, -81, 103, 113, 123, -30, 45, -41, 92, -18,
			111, 64, -75, -104, 7, -128, -26, 51, 126, -18, -71, -82, -33, 2, -36, 1, 124, -8, -70, 77, -83, -43, 18,
			-72, 107, -64, -67, -55, 57, 106, -83, 101, 9, -6, 125, 24, -23, 11, 35, -46, 114, 82, -24, -2, -86, -33, 2,
			-23, 108, 14, -79, -8, -126, -16, -3, 85, 31, -128, 18, -126, -85, 3, 48, -126, -124, 66, -89, 68, 37, -24,
			-9, 81, 107, -78, 61, -128, 100, -23, -64, 104, 127, 7, 66, 39, 106, -87, 117, -39, 70, 13, -128, 25, 0,
			-41, -107, -127, -53, -51, 65, -68, 120, 112, -107, 90, 87, 89, 22, 87, 51, -72, -13, -28, -67, -87, 107,
			74, -59, 6, -31, 20, -75, 57, 30, -38, -50, 5, 76, 95, -45, 43, -126, -59, -98, -96, -37, 26, -94, 42, -70,
			-98, -32, -69, -91, 45, 60, 74, -92, -80, -75, -77, 7, 0, 56, 117, -68, 22, -93, -3, 97, 92, 108, -86, -125,
			118, -34, -40, 116, 10, -101, -103, 61, -31, 15, -105, 36, -122, 75, 77, 117, 120, 120, -21, 2, -114, -42,
			30, 46, -67, 116, -115, 49, -10, 6, 0, 100, 89, -106, -83, 12, 64, -41, 19, -116, -59, 23, 84, -13, 0, -16,
			99, 123, -73, 92, 47, 13, -79, -8, -126, 33, -13, 0, -112, -49, -53, -104, -1, -100, 70, 124, 118, 69, 123,
			41, 42, -78, -98, 104, 0, -1, -20, 9, -82, -3, -4, -83, 27, 51, -77, 119, -8, -3, -41, -82, 118, -24, -68,
			-99, 1, -72, 26, -31, 0, -22, 3, 71, -88, -75, -45, 6, 48, -46, 27, 118, -60, -119, -111, -69, 31, -96, -27,
			127, 79, -116, -5, 121, 25, -29, -45, -117, 120, 53, -1, -115, -38, -13, 1, 108, -85, 1, -121, 36, -122,
			-114, -58, 0, -75, 95, -70, 0, 42, 21, -37, 2, -40, -49, -53, -8, -72, -110, -95, -10, -85, 67, -72, 6, -52,
			45, -89, 49, -2, 50, -123, 13, -3, 111, 120, 85, 33, 28, 64, 44, 97, -4, 36, 88, 9, 8, 111, 1, 39, -104, 55,
			20, -128, 83, -88, -120, 0, 78, -21, 15, 84, 95, -20, -6, 108, -31, 26, 96, 6, -54, -33, -31, -66, -50, 70,
			-19, -91, -92, -56, 122, 21, 17, -64, -28, -35, 43, 8, 27, 63, -16, -68, -74, -72, 13, -96, 98, -6, 22, 48,
			-63, -68, -83, 84, 68, 13, 112, 84, 0, -117, -85, 25, -53, -60, 90, -79, -74, -23, 53, -64, -20, -66, -67,
			-43, 120, 91, -128, 90, 0, 53, 94, 0, -62, 55, 50, 70, -83, -35, 20, 45, -62, 1, -12, 68, 26, 42, 34, 4,
			-119, 49, -12, 68, 26, -124, -17, 23, -2, 21, 24, -22, 110, -61, 80, 119, 27, -75, 127, -61, 120, 53, -128,
			90, 0, 53, -36, 1, 44, -81, 103, -87, -75, 114, -77, -76, -74, -61, 61, -105, -69, 6, -16, 62, 123, 91, 109,
			120, 91, -128, 90, 0, 53, 94, 0, 112, -47, 115, -126, 101, -68, 109, -24, -98, 19, 28, -18, 109, 119, 100,
			8, -54, 107, 115, 26, -110, -82, 127, 113, -46, -11, -81, -50, 42, 69, 112, 0, 85, -14, -80, -92, 89, -26,
			-117, -98, 113, -32, -17, -100, 27, 95, -97, -1, 3, 15, 32, -32, 95, -60, -127, 21, 87, 0, 0, 0, 0, 73, 69,
			78, 68, -82, 66, 96, -126 };

	public static File dir_commons() throws Exception {
		return new File(base(), DIR_COMMONS);
	}

	public static File dir_commons_tess4j_tessdata() throws Exception {
		return new File(base(), DIR_COMMONS_TESS4J_TESSDATA);
	}

	public static File dir_commons_ext() throws Exception {
		return new File(base(), DIR_COMMONS_EXT);
	}

	public static File dir_config() throws Exception {
		return new File(base(), DIR_CONFIG);
	}

	public static File dir_configSample() throws Exception {
		return new File(base(), DIR_CONFIGSAMPLE);
	}

	public static File dir_custom() throws Exception {
		return dir_custom(true);
	}

	public static File dir_custom(Boolean force) throws Exception {
		File dir = new File(base(), DIR_CUSTOM);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_custom_jars() throws Exception {
		return new File(base(), DIR_CUSTOM_JARS);
	}

	public static File dir_custom_jars(Boolean force) throws Exception {
		File dir = new File(base(), DIR_CUSTOM_JARS);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_dynamic() throws Exception {
		return new File(base(), DIR_DYNAMIC);
	}

	public static File dir_dynamic_jars() throws Exception {
		return dir_dynamic_jars(false);
	}

	public static File dir_dynamic_jars(Boolean force) throws Exception {
		File dir = new File(base(), DIR_DYNAMIC_JARS);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_jvm() throws Exception {
		return new File(base(), DIR_JVM);
	}

	public static File dir_jvm_aix() throws Exception {
		return new File(base(), DIR_JVM_AIX);
	}

	public static File dir_jvm_linux() throws Exception {
		return new File(base(), DIR_JVM_LINUX);
	}

	public static File dir_jvm_neokylin_loongson() throws Exception {
		return new File(base(), DIR_JVM_NEOKYLIN_LOONGSON);
	}

	public static File dir_jvm_macos() throws Exception {
		return new File(base(), DIR_JVM_MACOS);
	}

	public static File dir_jvm_windows() throws Exception {
		return new File(base(), DIR_JVM_WINDOWS);
	}

	public static File dir_local() throws Exception {
		return new File(base(), DIR_LOCAL);
	}

	public static File dir_local_backup() throws Exception {
		return new File(base(), DIR_LOCAL_BACKUP);
	}

	public static File dir_local_backup(boolean force) throws Exception {
		File dir = new File(base(), DIR_LOCAL_BACKUP);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_local_update() throws Exception {
		return new File(base(), DIR_LOCAL_UPDATE);
	}

	public static File dir_local_update(boolean force) throws Exception {
		File dir = new File(base(), DIR_LOCAL_UPDATE);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_localSample() throws Exception {
		return new File(base(), DIR_LOCALSAMPLE);
	}

	public static File dir_local_temp() throws Exception {
		return new File(base(), DIR_LOCAL_TEMP);
	}

	public static File dir_local_temp_classes() throws Exception {
		return new File(base(), DIR_LOCAL_TEMP_CLASSES);
	}

	public static File dir_local_temp_dynamic() throws Exception {
		return new File(base(), DIR_LOCAL_TEMP_DYNAMIC);
	}

	public static File dir_local_temp_dynamic(Boolean force) throws Exception {
		File dir = new File(base(), DIR_LOCAL_TEMP_DYNAMIC);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_logs() throws Exception {
		return new File(base(), DIR_LOGS);
	}

	public static File dir_logs(Boolean force) throws Exception {
		File dir = new File(base(), DIR_LOGS);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_servers() throws Exception {
		return new File(base(), DIR_SERVERS);
	}

	public static File dir_servers_applicationServer() throws Exception {
		return new File(base(), DIR_SERVERS_APPLICATIONSERVER);
	}

	public static File dir_servers_applicationServer_webapps() throws Exception {
		return new File(base(), DIR_SERVERS_APPLICATIONSERVER_WEBAPPS);
	}

	public static File dir_servers_applicationServer_work() throws Exception {
		return new File(base(), DIR_SERVERS_APPLICATIONSERVER_WORK);
	}

	public static File dir_servers_applicationServer_work(Boolean force) throws Exception {
		File dir = new File(base(), DIR_SERVERS_APPLICATIONSERVER_WORK);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_servers_centerServer() throws Exception {
		return new File(base(), DIR_SERVERS_CENTERSERVER);
	}

	public static File dir_servers_centerServer_webapps() throws Exception {
		return new File(base(), DIR_SERVERS_CENTERSERVER_WEBAPPS);
	}

	public static File dir_servers_centerServer_work() throws Exception {
		return new File(base(), DIR_SERVERS_CENTERSERVER_WORK);
	}

	public static File dir_servers_centerServer_work(Boolean force) throws Exception {
		File dir = new File(base(), DIR_SERVERS_CENTERSERVER_WORK);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_servers_webServer() throws Exception {
		return new File(base(), DIR_SERVERS_WEBSERVER);
	}

	public static File dir_store() throws Exception {
		return new File(base(), DIR_STORE);
	}

	public static File dir_store(Boolean force) throws Exception {
		File dir = new File(base(), DIR_STORE);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
	}

	public static File dir_store_jars() throws Exception {
		return new File(base(), DIR_STORE_JARS);
	}

	public static File dir_store_jars(Boolean force) throws Exception {
		File dir = new File(base(), DIR_STORE_JARS);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
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
					FileFilter fileFilter = new WildcardFileFilter("node_*.json");
					File[] files = dir_config().listFiles(fileFilter);
					if (null != files && files.length > 0) {
						for (File o : files) {
							String name = StringUtils.substringBetween(o.getName(), "node_", ".json");
							Node node = BaseTools.readConfigObject(DIR_CONFIG + "/" + o.getName(), Node.class);
							if (StringUtils.isNotEmpty(name) && BooleanUtils.isTrue(node.getEnable())) {
								nodes.put(name, node);
							}
						}
					} else {
						Node o = Node.defaultInstance();
						nodes.put(node(), o);
					}
					/* 20191009兼容centerServer */
					CenterServer c = BaseTools.readConfigObject(PATH_CONFIG_CENTERSERVER, CenterServer.class);
					if (null != c) {
						for (Node n : nodes.values()) {
							n.setCenter(c);
						}
					}
					/* 20191009兼容centerServer end */
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
					Token o = BaseTools.readConfigObject(PATH_CONFIG_TOKEN, Token.class);
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
					ExternalDataSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALDATASOURCES,
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

	private ExternalStorageSources externalStorageSources;

	public static ExternalStorageSources externalStorageSources() throws Exception {
		if (null == instance().externalStorageSources) {
			synchronized (Config.class) {
				if (null == instance().externalStorageSources) {
					ExternalStorageSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALSTORAGESOURCES,
							ExternalStorageSources.class);
					if (null == obj) {
						obj = ExternalStorageSources.defaultInstance();
					}
					instance().externalStorageSources = obj;
				}
			}
		}
		return instance().externalStorageSources;
	}

	private String publicKey;

	public static String publicKey() throws Exception {
		if (null == instance().publicKey) {
			synchronized (Config.class) {
				if (null == instance().publicKey) {
					File file = new File(Config.base(), PATH_CONFIG_PUBLICKEY);
					if (file.exists() && file.isFile()) {
						instance().publicKey = FileUtils.readFileToString(file, "utf-8");
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
						instance().privateKey = FileUtils.readFileToString(file, "utf-8");
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
					Person obj = BaseTools.readConfigObject(PATH_CONFIG_PERSON, Person.class);
					if (null == obj) {
						obj = Person.defaultInstance();
					}
					instance().person = obj;
				}
			}
		}
		return instance().person;
	}

	private Communicate communicate = null;

	public static Communicate communicate() throws Exception {
		if (null == instance().communicate) {
			synchronized (Config.class) {
				if (null == instance().communicate) {
					Communicate obj = BaseTools.readConfigObject(PATH_CONFIG_COMMUNICATE, Communicate.class);
					if (null == obj) {
						obj = Communicate.defaultInstance();
					}
					instance().communicate = obj;
				}
			}
		}
		return instance().communicate;
	}

	private Meeting meeting;

	public static Meeting meeting() throws Exception {
		if (null == instance().meeting) {
			synchronized (Config.class) {
				if (null == instance().meeting) {
					Meeting obj = BaseTools.readConfigObject(PATH_CONFIG_MEETING, Meeting.class);
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
					com.x.base.core.project.config.WorkTime obj = BaseTools.readConfigObject(PATH_CONFIG_WORKTIME,
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

	public Collect collect;

	public static Collect collect() throws Exception {
		if (null == instance().collect) {
			synchronized (Config.class) {
				if (null == instance().collect) {
					Collect obj = BaseTools.readConfigObject(PATH_CONFIG_COLLECT, Collect.class);
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
					DumpRestoreData obj = BaseTools.readConfigObject(PATH_CONFIG_DUMPRESTOREDATA,
							DumpRestoreData.class);
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
					DumpRestoreStorage obj = BaseTools.readConfigObject(PATH_CONFIG_DUMPRESTORESTORAGE,
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
					instance().initialScriptText = BaseTools.readString(PATH_COMMONS_INITIALSCRIPTTEXT);
				}
			}
		}
		return instance().initialScriptText;
	}

	public String initialServiceScriptText;

	public static String initialServiceScriptText() throws Exception {
		if (null == instance().initialServiceScriptText) {
			synchronized (Config.class) {
				if (null == instance().initialServiceScriptText) {
					instance().initialServiceScriptText = BaseTools.readString(PATH_COMMONS_INITIALSERVICESCRIPTTEXT);
				}
			}
		}
		return instance().initialServiceScriptText;
	}

	public String mooToolsScriptText;

	public static String mooToolsScriptText() throws Exception {
		if (null == instance().mooToolsScriptText) {
			synchronized (Config.class) {
				if (null == instance().mooToolsScriptText) {
					instance().mooToolsScriptText = BaseTools.readString(PATH_COMMONS_MOOTOOLSSCRIPTTEXT);
				}
			}
		}
		return instance().mooToolsScriptText;
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

	public static String mimeTypes(String name) throws Exception {
		String type = "";
		if (StringUtils.isNotEmpty(name)) {
			String value = FilenameUtils.getExtension(name);
			if (StringUtils.isEmpty(value)) {
				value = "." + name;
			} else {
				value = "." + value;
			}
			type = mimeTypes().getMimeByExtension(value);
		}
		if (StringUtils.isEmpty(type)) {
			type = MediaType.APPLICATION_OCTET_STREAM;
		}
		return type;
	}

	private StorageMappings storageMappings;

	public static StorageMappings storageMappings() throws Exception {
		if (null == instance().storageMappings) {
			synchronized (Config.class) {
				if (null == instance().storageMappings) {
					ExternalStorageSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALSTORAGESOURCES,
							ExternalStorageSources.class);
					if ((obj != null)) {
						instance().storageMappings = new StorageMappings(obj);
					} else {
						instance().storageMappings = new StorageMappings(nodes());
					}
				}
			}
		}
		return instance().storageMappings;
	}

	private File sslKeyStore;

	public static File sslKeyStore() throws Exception {
		if (null == instance().sslKeyStore) {
			synchronized (Config.class) {
				if (null == instance().sslKeyStore) {
					File file = new File(BaseTools.getBasePath(), PATH_CONFIG_SSLKEYSTORE);
					if ((!file.exists()) || file.isDirectory()) {
						file = new File(BaseTools.getBasePath(), PATH_CONFIG_SSLKEYSTORE + ".jks");
					}
					if ((!file.exists()) || file.isDirectory()) {
						file = new File(BaseTools.getBasePath(), PATH_CONFIG_SSLKEYSTORESAMPLE);
					}
					instance().sslKeyStore = file;
				}
			}
		}
		return instance().sslKeyStore;
	}

	public static Node currentNode() throws Exception {
		return nodes().get(node());
	}

	public static String url_x_program_center_jaxrs(String... paths) throws Exception {
		String n = resource_node_centersPirmaryNode();
		Integer p = resource_node_centersPirmaryPort();
		Boolean s = resource_node_centersPirmarySslEnable();
		StringBuffer buffer = new StringBuffer();
		if (s) {
			buffer.append("https://").append(n);
			if (!NumberTools.valueEuqals(p, 443)) {
				buffer.append(":").append(p);
			}
		} else {
			buffer.append("http://").append(n);
			if (!NumberTools.valueEuqals(p, 80)) {
				buffer.append(":").append(p);
			}
		}
		buffer.append("/").append(x_program_center.class.getSimpleName());
		buffer.append("/jaxrs/");
		List<String> os = new ArrayList<>();
		for (String path : paths) {
			os.add(URLEncoder.encode(StringUtils.strip(path, "/"), DefaultCharset.name));
		}
		buffer.append(StringUtils.join(os, "/"));
		return buffer.toString();
	}

	public static String url_x_program_center_jaxrs(Entry<String, CenterServer> entry, String... paths)
			throws Exception {
		String n = entry.getKey();
		Integer p = entry.getValue().getPort();
		Boolean s = entry.getValue().getSslEnable();
		StringBuffer buffer = new StringBuffer();
		if (s) {
			buffer.append("https://").append(n);
			if (!NumberTools.valueEuqals(p, 443)) {
				buffer.append(":").append(p);
			}
		} else {
			buffer.append("http://").append(n);
			if (!NumberTools.valueEuqals(p, 80)) {
				buffer.append(":").append(p);
			}
		}
		buffer.append("/").append(x_program_center.class.getSimpleName());
		buffer.append("/jaxrs/");
		List<String> os = new ArrayList<>();
		for (String path : paths) {
			os.add(URLEncoder.encode(StringUtils.strip(path, "/"), DefaultCharset.name));
		}
		buffer.append(StringUtils.join(os, "/"));
		return buffer.toString();
	}

	private Messages messages;

	public static Messages messages() throws Exception {
		if (null == instance().messages) {
			synchronized (Config.class) {
				if (null == instance().messages) {
					Messages obj = Messages.defaultInstance();
					Messages custom = BaseTools.readConfigObject(PATH_CONFIG_MESSAGES, Messages.class);
					if (null != custom) {
						custom.entrySet().stream().forEach(o -> {
							obj.put(o.getKey(), new Message(o.getValue().getConsumers()));
						});
					}
					instance().messages = obj;
				}
			}
		}
		return instance().messages;
	}

	private PushConfig pushConfig;

	public static PushConfig pushConfig() throws Exception {
		if (null == instance().pushConfig) {
			synchronized (Config.class) {
				if (null == instance().pushConfig) {
					PushConfig custom = BaseTools.readConfigObject(PATH_CONFIG_JPUSH, PushConfig.class);
					if (null != custom) {
						instance().pushConfig = custom;
					} else {
						instance().pushConfig = PushConfig.defaultInstance();
					}
				}
			}
		}
		return instance().pushConfig;
	}

	private ProcessPlatform processPlatform;

	public static ProcessPlatform processPlatform() throws Exception {
		if (null == instance().processPlatform) {
			synchronized (Config.class) {
				if (null == instance().processPlatform) {
					ProcessPlatform obj = BaseTools.readConfigObject(PATH_CONFIG_PROCESSPLATFORM,
							ProcessPlatform.class);
					if (null == obj) {
						obj = ProcessPlatform.defaultInstance();
					}
					instance().processPlatform = obj;
				}
			}
		}
		return instance().processPlatform;
	}

	private Query query;

	public static Query query() throws Exception {
		if (null == instance().query) {
			synchronized (Config.class) {
				if (null == instance().query) {
					Query obj = BaseTools.readConfigObject(PATH_CONFIG_QUERY, Query.class);
					if (null == obj) {
						obj = Query.defaultInstance();
					}
					instance().query = obj;
				}
			}
		}
		return instance().query;
	}

	private Dingding dingding;

	public static Dingding dingding() throws Exception {
		if (null == instance().dingding) {
			synchronized (Config.class) {
				if (null == instance().dingding) {
					Dingding obj = BaseTools.readConfigObject(PATH_CONFIG_DINGDING, Dingding.class);
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
					Qiyeweixin obj = BaseTools.readConfigObject(PATH_CONFIG_QIYEWEIXIN, Qiyeweixin.class);
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
					ZhengwuDingding obj = BaseTools.readConfigObject(PATH_CONFIG_ZHENGWUDINGDING,
							ZhengwuDingding.class);
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
					Vfs obj = BaseTools.readConfigObject(PATH_CONFIG_VFS, Vfs.class);
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
					AppStyle obj = BaseTools.readConfigObject(PATH_CONFIG_APPSTYLE, AppStyle.class);
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
					LogLevel obj = BaseTools.readConfigObject(PATH_CONFIG_LOGLEVEL, LogLevel.class);
					if (null == obj) {
						obj = LogLevel.defaultInstance();
					}
					instance().logLevel = obj;
				}
			}
		}
		return instance().logLevel;
	}

	private byte[] bindLogo;

	public static byte[] bindLogo() throws Exception {
		if (null == instance().bindLogo) {
			synchronized (Config.class) {
				if (null == instance().bindLogo) {
					File file = new File(Config.base(), PATH_CONFIG_BINDLOGO);
					if (file.exists() && file.isFile()) {
						instance().bindLogo = FileUtils.readFileToByteArray(file);
					} else {
						instance().bindLogo = DEFAULT_BINDLOGO;
					}
				}
			}
		}
		return instance().bindLogo;
	}

	private InitialContext initialContext;

	private static InitialContext initialContext() throws Exception {
		if (null == instance().initialContext) {
			synchronized (Config.class) {
				if (null == instance().initialContext) {
					instance().initialContext = new InitialContext();
				}
			}
		}
		return instance().initialContext;
	}

	public Slice slice;

	public static Slice slice() throws Exception {
		if (null == instance().slice) {
			synchronized (Config.class) {
				if (null == instance().slice) {
					Slice obj = BaseTools.readConfigObject(PATH_CONFIG_SLICE, Slice.class);
					if (null == obj) {
						obj = Slice.defaultInstance();
					}
					instance().slice = obj;
				}
			}
		}
		return instance().slice;
	}

	public static Object resource(String name) throws Exception {
		return initialContext().lookup(name);
	}

	public static Object resource_jdbc(String name) throws Exception {
		return initialContext().lookup(RESOURCE_JDBC_PREFIX + name);
	}

	public static Object resource_node(String name) throws Exception {
		return initialContext().lookup(RESOURCE_NODE_PREFIX + name);
	}

	@SuppressWarnings("unchecked")
	public static LinkedBlockingQueue<JsonElement> resource_node_eventQueue() throws Exception {
		return (LinkedBlockingQueue<JsonElement>) initialContext().lookup(RESOURCE_NODE_EVENTQUEUE);
	}

	public static synchronized JsonElement resource_node_applications() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_APPLICATIONS);
		if (null != o) {
			return (JsonElement) o;
		}
		return null;
	}

	public static synchronized void resource_node_applications(JsonElement jsonElement) throws Exception {
		initialContext().rebind(RESOURCE_NODE_APPLICATIONS, jsonElement);
	}

	public static synchronized Date resource_node_applicationsTimestamp() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_APPLICATIONSTIMESTAMP);
		if (null != o) {
			return (Date) o;
		}
		return null;
	}

	public static synchronized void resource_node_applicationsTimestamp(Date date) throws Exception {
		initialContext().rebind(RESOURCE_NODE_APPLICATIONSTIMESTAMP, date);
	}

	public static synchronized String resource_node_centersPirmaryNode() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_CENTERSPRIMARYNODE);
		if (null != o) {
			return (String) o;
		}
		return null;
	}

	public static synchronized void resource_node_centersPirmaryNode(String node) throws Exception {
		initialContext().rebind(RESOURCE_NODE_CENTERSPRIMARYNODE, node);
	}

	public static synchronized Integer resource_node_centersPirmaryPort() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_CENTERSPRIMARYPORT);
		if (null != o) {
			return (Integer) o;
		}
		return null;
	}

	public static synchronized void resource_node_centersPirmaryPort(Integer port) throws Exception {
		initialContext().rebind(RESOURCE_NODE_CENTERSPRIMARYPORT, port);
	}

	public static synchronized Boolean resource_node_centersPirmarySslEnable() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_CENTERSPRIMARYSSLENABLE);
		if (null != o) {
			return (Boolean) o;
		}
		return null;
	}

	public static synchronized void resource_node_centersPirmarySslEnable(Boolean sslEnable) throws Exception {
		initialContext().rebind(RESOURCE_NODE_CENTERSPRIMARYSSLENABLE, sslEnable);
	}

}