package com.x.base.core.project.config;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.naming.InitialContext;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jetty.http.MimeTypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.project.x_program_center;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.NumberTools;

public class Config {

	private static Config INSTANCE;

	public Config() {
	}

	public static final String JAVAVERSION_JAVA8 = "java8";
	public static final String JAVAVERSION_JAVA11 = "java11";

	public static final String OS_WINDOWS = "windows";
	public static final String OS_AIX = "aix";
	public static final String OS_LINUX = "linux";
	public static final String OS_MACOS = "macos";
	public static final String OS_RASPI = "raspi";
	public static final String OS_ARM = "arm";
	public static final String OS_MIPS = "mips";

	public static final String PATH_VERSION = "version.o2";
	public static final String PATH_LOCAL_NODE = "local/node.cfg";
	public static final String PATH_CONFIG_TOKEN = "config/token.json";
	public static final String PATH_CONFIG_EXTERNALDATASOURCES = "config/externalDataSources.json";
	public static final String PATH_CONFIG_EXTERNALSTORAGESOURCES = "config/externalStorageSources.json";
	public static final String PATH_CONFIG_PERSON = "config/person.json";
	public static final String PATH_CONFIG_MEETING = "config/meeting.json";
	public static final String PATH_CONFIG_APPSTYLE = "config/appStyle.json";
	public static final String PATH_CONFIG_WORKTIME = "config/workTime.json";
	public static final String PATH_CONFIG_CENTERSERVER = "config/centerServer.json";
	public static final String PATH_CONFIG_COLLECT = "config/collect.json";
	public static final String NAME_CONFIG_COLLECT = "collect.json";
	public static final String PATH_CONFIG_DUMPRESTOREDATA = "config/dumpRestoreData.json";
	public static final String PATH_CONFIG_MESSAGES = "config/messages.json";
	public static final String PATH_CONFIG_SSLKEYSTORE = "config/keystore";
	public static final String PATH_CONFIG_SSLKEYSTORESAMPLE = "config/sample/keystore";
	public static final String PATH_CONFIG_STARTIMAGE = "config/startImage.png";
	public static final String PATH_CONFIG_PUBLICKEY = "config/public.key";
	public static final String PATH_CONFIG_PRIVATEKEY = "config/private.key";
	public static final String PATH_CONFIG_PROCESSPLATFORM = "config/processPlatform.json";
	public static final String PATH_CONFIG_CMS = "config/cms.json";
	public static final String PATH_CONFIG_QUERY = "config/query.json";
	public static final String PATH_CONFIG_DINGDING = "config/dingding.json";
	public static final String PATH_CONFIG_ANDFX = "config/andFx.json";
	public static final String PATH_CONFIG_WELINK = "config/weLink.json";
	public static final String PATH_CONFIG_ZHENGWUDINGDING = "config/zhengwuDingding.json";
	public static final String PATH_CONFIG_QIYEWEIXIN = "config/qiyeweixin.json";
	public static final String PATH_CONFIG_MPWEIXIN = "config/mpweixin.json";
	public static final String PATH_CONFIG_MPWEIXIN2 = "config/mMweixin.json"; // 容错
	public static final String PATH_CONFIG_BINDLOGO = "config/bindLogo.png";
	public static final String PATH_COMMONS_INITIALSCRIPTTEXT = "commons/initialScriptText.js";
	public static final String PATH_COMMONS_INITIALSERVICESCRIPTTEXT = "commons/initialServiceScriptText.js";
	public static final String PATH_COMMONS_COMMONSCRIPT = "commons/commonScript.js";
	public static final String PATH_CONFIG_JPUSH = "config/jpushConfig.json";
	public static final String NAME_CONFIG_JPUSH = "jpushConfig.json";
	public static final String PATH_CONFIG_EXMAIL = "config/exmail.json";
	public static final String PATH_CONFIG_PORTAL = "config/portal.json";
	public static final String PATH_CONFIG_CACHE = "config/cache.json";
	public static final String PATH_CONFIG_COMPONENTS = "config/components.json";
	public static final String PATH_CONFIG_WEB = "config/web.json";
	public static final String PATH_CONFIG_MOCK = "config/mock.json";
	public static final String PATH_CONFIG_TERNARY_MANAGEMENT = "config/ternaryManagement.json";

	public static final String PATH_CONFIG_GENERAL = "config/general.json";

	public static final String DIR_COMMONS = "commons";
	public static final String DIR_COMMONS_H2 = "commons/h2";
	public static final String DIR_COMMONS_TESS4J_TESSDATA = "commons/tess4j/tessdata";
	public static final String DIR_COMMONS_EXT = "commons/ext";
	public static final String DIR_COMMONS_LANGUAGE = "commons/language";
	public static final String DIR_COMMONS_FONTS = "commons/fonts";
	public static final String DIR_COMMONS_HADOOP = DIR_COMMONS + "/hadoop";
	public static final String DIR_COMMONS_HADOOP_WINDOWS = DIR_COMMONS_HADOOP + "/" + OS_WINDOWS;
	public static final String DIR_COMMONS_HADOOP_AIX = DIR_COMMONS + "/ " + OS_AIX;
	public static final String DIR_COMMONS_HADOOP_LINUX = DIR_COMMONS + "/" + OS_LINUX;
	public static final String DIR_COMMONS_HADOOP_MACOS = DIR_COMMONS + "/" + OS_MACOS;
	public static final String DIR_COMMONS_HADOOP_RASPI = DIR_COMMONS + "/" + OS_RASPI;
	public static final String DIR_COMMONS_HADOOP_ARM = DIR_COMMONS + "/" + OS_ARM;
	public static final String DIR_COMMONS_HADOOP_MIPS = DIR_COMMONS + "/" + OS_MIPS;
	public static final String DIR_CONFIG = "config";
	public static final String DIR_CONFIG_COVERTOWEBSERVER = "config/coverToWebServer";
	public static final String DIR_CONFIGSAMPLE = "configSample";
	public static final String DIR_CUSTOM = "custom";
	public static final String DIR_CUSTOM_JARS = "custom/jars";
	public static final String DIR_DYNAMIC = "dynamic";
	public static final String DIR_DYNAMIC_JARS = "dynamic/jars";
	public static final String DIR_JVM = "jvm";
	public static final String DIR_LOCAL = "local";
	public static final String DIR_LOCAL_BACKUP = "local/backup";
	public static final String DIR_LOCAL_DUMP = "local/dump";
	public static final String DIR_LOCAL_REPOSITORY = "local/repository";
	public static final String DIR_LOCAL_REPOSITORY_INDEX = "local/repository/index";
	public static final String DIR_LOCAL_REPOSITORY_DATA = "local/repository/data";
	public static final String DIR_LOCAL_UPDATE = "local/update";
	public static final String DIR_LOCAL_TEMP = "local/temp";
	public static final String DIR_LOCAL_TEMP_CLASSES = "local/temp/classes";
	public static final String DIR_LOCAL_TEMP_CUSTOM = "local/temp/custom";
	public static final String DIR_LOCAL_TEMP_SQL = "local/temp/sql";
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
	public static final String DIR_SERVERS_WEBSERVER_X_INIT = "servers/webServer/x_init";
	public static final String DIR_SERVERS_WEBSERVER_X_DESKTOP_RES_CONFIG = "servers/webServer/x_desktop/res/config";
	public static final String DIR_STORE = "store";
	public static final String DIR_STORE_JARS = "store/jars";
	public static final String DIR_WEBROOT = "webroot";
	public static final String DIR_SERVERS_INITSERVER_WORK = "servers/initServer/work";

	public static final String RESOURCE_CONTAINERENTITIES = "containerEntities";

	public static final String RESOURCE_CONTAINERENTITYNAMES = "containerEntityNames";

	public static final String RESOURCE_STORAGECONTAINERENTITYNAMES = "storageContainerEntityNames";

	public static final String RESOURCE_INITSERVERSTOPSIGNALQUEUE = "initServerStopSignalQueue";

	public static final String RESOURCE_COMMANDQUEUE = "commandQueue";

	public static final String RESOURCE_JDBC_PREFIX = "jdbc/";

	public static final String RESOURCE_AUDITLOGPRINTSTREAM = "auditLogPrintStream";

	public static final String SCRIPTING_ENGINE_NAME = "JavaScript";

	public static final String RESOURCE_NODE_PREFIX = "node/";
	public static final String RESOURCE_NODE_EVENTQUEUE = RESOURCE_NODE_PREFIX + "eventQueue";
	public static final String RESOURCE_NODE_EVENTQUEUEEXECUTOR = RESOURCE_NODE_PREFIX + "eventQueueExecutor";
	public static final String RESOURCE_NODE_APPLICATIONS = RESOURCE_NODE_PREFIX + "applications";
	public static final String RESOURCE_NODE_APPLICATIONSTIMESTAMP = RESOURCE_NODE_PREFIX + "applicationsTimestamp";
	public static final String RESOURCE_NODE_CENTERSPRIMARYNODE = RESOURCE_NODE_PREFIX + "centersPrimaryNode";
	public static final String RESOURCE_NODE_CENTERSPRIMARYPORT = RESOURCE_NODE_PREFIX + "centersPrimaryPort";
	public static final String RESOURCE_NODE_CENTERSPRIMARYSSLENABLE = RESOURCE_NODE_PREFIX + "centersPrimarySslEnable";
	public static final String RESOURCE_NODE_TOKENTHRESHOLDS = RESOURCE_NODE_PREFIX + "tokenThresholds";

	public static final String RESOURCE_COMMANDTERMINATEDSIGNAL_PREFIX = "commandTerminatedSignal/";
	public static final String RESOURCE_COMMANDTERMINATEDSIGNAL_CTL_RD = RESOURCE_COMMANDTERMINATEDSIGNAL_PREFIX
			+ "ctl -rd";

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

	public static Path dir_commons_fonts() throws Exception {
		return Paths.get(base()).resolve(DIR_COMMONS_FONTS);
	}

	public static File dir_config() throws Exception {
		return new File(base(), DIR_CONFIG);
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

	public static File dir_custom_jars(Boolean force) throws IOException, URISyntaxException {
		File dir = new File(base(), DIR_CUSTOM_JARS);
		if (BooleanUtils.isTrue(force) && ((!dir.exists()) || dir.isFile())) {
			FileUtils.forceMkdir(dir);
		}
		return dir;
	}

	public static File dir_dynamic() throws Exception {
		return new File(base(), DIR_DYNAMIC);
	}

	public static File dir_dynamic_jars() throws Exception {
		return dir_dynamic_jars(false);
	}

	public static File dir_dynamic_jars(Boolean force) throws IOException, URISyntaxException {
		File dir = new File(base(), DIR_DYNAMIC_JARS);
		if (BooleanUtils.isTrue(force) && ((!dir.exists()) || dir.isFile())) {
			FileUtils.forceMkdir(dir);
		}
		return dir;
	}

	public static File dir_jvm() throws Exception {
		return new File(base(), DIR_JVM);
	}

	public static Path command_java_path() {
		Path dir = Paths.get(System.getProperty("java.home"));
		return SystemUtils.IS_OS_WINDOWS ? dir.resolve("bin/java.exe") : dir.resolve("bin/java");
	}

	public static Path command_jstack_path() {
		Path dir = Paths.get(System.getProperty("java.home"));
		return SystemUtils.IS_OS_WINDOWS ? dir.resolve("bin/jstack.exe") : dir.resolve("bin/jstack");
	}

	public static Path command_jmap_path() {
		Path dir = Paths.get(System.getProperty("java.home"));
		return SystemUtils.IS_OS_WINDOWS ? dir.resolve("bin/jmap.exe") : dir.resolve("bin/jmap");
	}

	public static File dir_local() throws IOException, URISyntaxException {
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

	public static File dir_local_temp_classes() throws IOException, URISyntaxException {
		return new File(base(), DIR_LOCAL_TEMP_CLASSES);
	}

	public static File dir_local_temp_custom() throws Exception {
		return new File(base(), DIR_LOCAL_TEMP_CUSTOM);
	}

	public static File dir_local_temp_custom(Boolean force) throws Exception {
		File dir = new File(base(), DIR_LOCAL_TEMP_CUSTOM);
		if (force) {
			if ((!dir.exists()) || dir.isFile()) {
				FileUtils.forceMkdir(dir);
			}
		}
		return dir;
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

	public static File dir_local_temp_sql() throws Exception {
		return new File(base(), DIR_LOCAL_TEMP_SQL);
	}

	public static File dir_local_temp_sql(Boolean force) throws Exception {
		File dir = new File(base(), DIR_LOCAL_TEMP_SQL);
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

	public static File dir_store(boolean force) throws Exception {
		File dir = new File(base(), DIR_STORE);
		if (force && ((!dir.exists()) || dir.isFile())) {
			FileUtils.forceMkdir(dir);
		}
		return dir;

	}

	public static File dir_store_jars() throws Exception {
		return new File(base(), DIR_STORE_JARS);
	}

	public static File dir_store_jars(Boolean force) throws IOException, URISyntaxException {
		File dir = new File(base(), DIR_STORE_JARS);
		if (BooleanUtils.isTrue(force) && ((!dir.exists()) || dir.isFile())) {
			FileUtils.forceMkdir(dir);
		}
		return dir;
	}

	/**
	 * 重置Config对象,不更新externalDataSources,externalStorageSources.
	 * 部分对象不能直接刷新,会导致已有链接断开.
	 */
	public static synchronized void flush() {
		Config newInstance = new Config();
		if (INSTANCE != null) {
			newInstance.nodes = INSTANCE.nodes;
			newInstance.externalStorageSources = INSTANCE.externalStorageSources;
			newInstance.externalDataSources = INSTANCE.externalDataSources;
		}
		INSTANCE = newInstance;
	}

	/**
	 * 重新生成Config对象,销毁所有配置对象.
	 */
	public static synchronized void regenerate() {
		INSTANCE = null;
	}

	private static synchronized Config instance() {
		if (null == INSTANCE) {
			INSTANCE = new Config();
		}
		return INSTANCE;
	}

	private String version;

	public static synchronized String version() throws Exception {
		if (null == instance().version) {
			String text = BaseTools.readString(PATH_VERSION);
			if (XGsonBuilder.isJsonObject(text)) {
				JsonObject obj = XGsonBuilder.instance().fromJson(text, JsonObject.class);
				instance().version = obj.get("version").getAsString();
			} else {
				instance().version = text;
			}
		}
		return instance().version;
	}

	private String node;

	public static synchronized String node() throws Exception {
		if (null == instance().node) {
			instance().node = BaseTools.readCfg(PATH_LOCAL_NODE, Host.ROLLBACK_IPV4);
		}
		return instance().node;
	}

	private String base;

	public static synchronized String base() {
		if (null == instance().base) {
			instance().base = BaseTools.getBasePath();
		}
		return instance().base;
	}

	private Nodes nodes;

	public static synchronized Nodes nodes() throws Exception {
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
		return instance().nodes;
	}

	private Token token;

	public static synchronized Token token() throws Exception {
		if (null == instance().token) {
			Token o = BaseTools.readConfigObject(PATH_CONFIG_TOKEN, Token.class);
			if (null == o) {
				o = Token.defaultInstance();
			}
			instance().token = o;
		}
		return instance().token;
	}

	private TernaryManagement ternaryManagement;

	public static synchronized TernaryManagement ternaryManagement() throws Exception {
		if (null == instance().ternaryManagement) {
			TernaryManagement o = BaseTools.readConfigObject(PATH_CONFIG_TERNARY_MANAGEMENT, TernaryManagement.class);
			if (null == o) {
				o = TernaryManagement.defaultInstance();
			}
			instance().ternaryManagement = o;
		}
		return instance().ternaryManagement;
	}

	private ExternalDataSources externalDataSources;

	public static synchronized ExternalDataSources externalDataSources() throws Exception {
		if (null == instance().externalDataSources) {
			ExternalDataSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALDATASOURCES,
					ExternalDataSources.class);
			if (null == obj) {
				obj = ExternalDataSources.defaultInstance();
			}
			instance().externalDataSources = obj;
		}
		return instance().externalDataSources;
	}

	private ExternalStorageSources externalStorageSources;

	public static synchronized ExternalStorageSources externalStorageSources() throws Exception {
		if (null == instance().externalStorageSources) {
			ExternalStorageSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALSTORAGESOURCES,
					ExternalStorageSources.class);
			if (null == obj) {
				obj = ExternalStorageSources.defaultInstance();
			}
			instance().externalStorageSources = obj;
		}
		return instance().externalStorageSources;
	}

	private String publicKey;

	public static synchronized String publicKey() throws IOException, URISyntaxException {
		if (null == instance().publicKey) {
			File file = new File(Config.base(), PATH_CONFIG_PUBLICKEY);
			if (file.exists() && file.isFile()) {
				instance().publicKey = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			} else {
				instance().publicKey = DEFAULT_PUBLIC_KEY;
			}
		}
		return instance().publicKey;
	}

	private String privateKey;

	public static synchronized String privateKey() throws IOException, URISyntaxException {
		if (null == instance().privateKey) {
			File file = new File(Config.base(), PATH_CONFIG_PRIVATEKEY);
			if (file.exists() && file.isFile()) {
				instance().privateKey = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			} else {
				instance().privateKey = DEFAULT_PRIVATE_KEY;
			}
		}
		return instance().privateKey;
	}

	private Person person = null;

	public static synchronized Person person() throws Exception {
		if (null == instance().person) {
			Person obj = BaseTools.readConfigObject(PATH_CONFIG_PERSON, Person.class);
			if (null == obj) {
				obj = Person.defaultInstance();
			}
			instance().person = obj;
		}
		return instance().person;
	}

	private Meeting meeting;

	public static synchronized Meeting meeting() throws Exception {
		if (null == instance().meeting) {
			Meeting obj = BaseTools.readConfigObject(PATH_CONFIG_MEETING, Meeting.class);
			if (null == obj) {
				obj = Meeting.defaultInstance();
			}
			instance().meeting = obj;
		}
		return instance().meeting;
	}

	private com.x.base.core.project.utils.time.WorkTime workTime;

	public static synchronized com.x.base.core.project.utils.time.WorkTime workTime() throws Exception {
		if (null == instance().workTime) {
			com.x.base.core.project.config.WorkTime obj = BaseTools.readConfigObject(PATH_CONFIG_WORKTIME,
					com.x.base.core.project.config.WorkTime.class);
			if (null == obj) {
				obj = com.x.base.core.project.config.WorkTime.defaultInstance();
			}
			instance().workTime = new com.x.base.core.project.utils.time.WorkTime(obj.getAmStart(), obj.getAmEnd(),
					obj.getPmStart(), obj.getPmEnd(), obj.getHolidays(), obj.getWorkdays(), obj.getWeekends());
		}
		return instance().workTime;
	}

	public Collect collect;

	public static synchronized Collect collect() throws Exception {
		if (null == instance().collect) {
			Collect obj = BaseTools.readConfigObject(PATH_CONFIG_COLLECT, Collect.class);
			if (null == obj) {
				obj = Collect.defaultInstance();
			}
			instance().collect = obj;
		}
		return instance().collect;
	}

	/**
	 * dumpRestoreData配置不考虑进行缓存,每次直接取值
	 *
	 * @return
	 * @throws Exception
	 */
	public static synchronized DumpRestoreData dumpRestoreData() {
		DumpRestoreData obj = BaseTools.readConfigObject(PATH_CONFIG_DUMPRESTOREDATA, DumpRestoreData.class);
		if (null == obj) {
			obj = DumpRestoreData.defaultInstance();
		}
		return obj;
	}

	private String initialScriptText;

	public static synchronized String initialScriptText() throws IOException {
		if (null == instance().initialScriptText) {
			instance().initialScriptText = BaseTools.readString(PATH_COMMONS_INITIALSCRIPTTEXT);
		}
		return instance().initialScriptText;
	}

	private String initialServiceScriptText;

	public static synchronized String initialServiceScriptText() throws IOException {
		if (null == instance().initialServiceScriptText) {
			instance().initialServiceScriptText = BaseTools.readString(PATH_COMMONS_INITIALSERVICESCRIPTTEXT);
		}
		return instance().initialServiceScriptText;
	}

	private String commonScript;

	public static synchronized String commonScript() throws IOException {
		if (null == instance().commonScript) {
			instance().commonScript = BaseTools.readString(PATH_COMMONS_COMMONSCRIPT);
		}
		instance().commonScript = BaseTools.readString(PATH_COMMONS_COMMONSCRIPT);
		return instance().commonScript;
	}

	private MimeTypes mimeTypes;

	public static synchronized MimeTypes mimeTypes() throws Exception {
		if (null == instance().mimeTypes) {
			MimeTypes mimeTypes = new MimeTypes();
			/* 添加o2自定义格式 */
			mimeTypes.addMimeMapping("wcss", "application/json");
			/* TXT和HTML文件以utf-8输出，解决在线打开乱码问题 */
			mimeTypes.addMimeMapping("txt", "text/plain; charset=UTF-8");
			mimeTypes.addMimeMapping("html", "text/html; charset=UTF-8");
			/* 添加默认格式 */
			mimeTypes.addMimeMapping("", "application/octet-stream");
			/* 添加新版office格式 */
			mimeTypes.addMimeMapping("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			mimeTypes.addMimeMapping("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			mimeTypes.addMimeMapping("pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation");
			/* 名片 */
			mimeTypes.addMimeMapping("vcf", "text/x-vcard");
			/* 流媒体都改为stream输出，不支持浏览器在线播放，如需在线播放请用第三方插件 */
			mimeTypes.addMimeMapping("mov", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("movie", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mp2", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mp3", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mp4", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mpe", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mpeg", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mpg", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mpga", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("wav", MediaType.APPLICATION_OCTET_STREAM);
			mimeTypes.addMimeMapping("mid", MediaType.APPLICATION_OCTET_STREAM);
			instance().mimeTypes = mimeTypes;
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

	public static synchronized StorageMappings storageMappings() throws Exception {
		if (null == instance().storageMappings) {
			ExternalStorageSources obj = BaseTools.readConfigObject(PATH_CONFIG_EXTERNALSTORAGESOURCES,
					ExternalStorageSources.class);
			if ((obj != null) && BooleanUtils.isTrue(obj.getEnable())) {
				instance().storageMappings = new StorageMappings(obj);
			} else {
				instance().storageMappings = new StorageMappings(nodes());
			}
		}
		return instance().storageMappings;
	}

	private File sslKeyStore;

	public static synchronized File sslKeyStore() throws IOException, URISyntaxException {
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
		return instance().sslKeyStore;
	}

	public static Node currentNode() throws Exception {
		return nodes().get(node());
	}

	public static String url_x_program_center_jaxrs(String... paths) throws Exception {
		String n = resource_node_centersPirmaryNode();
		Integer p = resource_node_centersPirmaryPort();
		Boolean s = resource_node_centersPirmarySslEnable();
		StringBuilder buffer = new StringBuilder();
		if (BooleanUtils.isTrue(s)) {
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

	public static String url_x_program_center_jaxrs(Entry<String, CenterServer> entry, String... paths) {
		String n = entry.getKey();
		Integer p = entry.getValue().getPort();
		boolean s = entry.getValue().getSslEnable();
		StringBuilder buffer = new StringBuilder();
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
			os.add(URLEncoder.encode(StringUtils.strip(path, "/"), StandardCharsets.UTF_8));
		}
		buffer.append(StringUtils.join(os, "/"));
		return buffer.toString();
	}

	private Messages messages;

	public static synchronized Messages messages() throws Exception {
		if (null == instance().messages) {
			Messages obj = BaseTools.readConfigObject(PATH_CONFIG_MESSAGES, Messages.class);
			if (null == obj) {
				obj = Messages.defaultInstance();
			}
			instance().messages = obj;
		}
		return instance().messages;
	}

	private JpushConfig pushConfig;

	public static synchronized JpushConfig pushConfig() throws Exception {
		if (null == instance().pushConfig) {
			JpushConfig custom = BaseTools.readConfigObject(PATH_CONFIG_JPUSH, JpushConfig.class);
			if (null != custom) {
				instance().pushConfig = custom;
			} else {
				instance().pushConfig = JpushConfig.defaultInstance();
			}
		}
		return instance().pushConfig;
	}

	private Cms cms;

	public static synchronized Cms cms() throws Exception {
		if (null == instance().cms) {
			Cms obj = BaseTools.readConfigObject(PATH_CONFIG_CMS, Cms.class);
			if (null == obj) {
				obj = Cms.defaultInstance();
			}
			instance().cms = obj;
		}
		return instance().cms;
	}

	private ProcessPlatform processPlatform;

	public static synchronized ProcessPlatform processPlatform() throws Exception {
		if (null == instance().processPlatform) {
			ProcessPlatform obj = BaseTools.readConfigObject(PATH_CONFIG_PROCESSPLATFORM, ProcessPlatform.class);
			if (null == obj) {
				obj = ProcessPlatform.defaultInstance();
			}
			instance().processPlatform = obj;
		}
		return instance().processPlatform;
	}

	private Query query;

	public static synchronized Query query() throws Exception {
		if (null == instance().query) {
			Query obj = BaseTools.readConfigObject(PATH_CONFIG_QUERY, Query.class);
			if (null == obj) {
				obj = Query.defaultInstance();
			}
			instance().query = obj;
		}
		return instance().query;
	}

	private Dingding dingding;

	public static synchronized Dingding dingding() throws Exception {
		if (null == instance().dingding) {
			Dingding obj = BaseTools.readConfigObject(PATH_CONFIG_DINGDING, Dingding.class);
			if (null == obj) {
				obj = Dingding.defaultInstance();
			}
			instance().dingding = obj;
		}
		return instance().dingding;
	}

	private AndFx andFx;

	public static synchronized AndFx andFx() throws Exception {
		if (null == instance().andFx) {
			AndFx obj = BaseTools.readConfigObject(PATH_CONFIG_ANDFX, AndFx.class);
			if (null == obj) {
				obj = AndFx.defaultInstance();
			}
			instance().andFx = obj;
		}
		return instance().andFx;
	}

	private WeLink weLink;

	public static synchronized WeLink weLink() throws Exception {
		if (null == instance().weLink) {
			WeLink obj = BaseTools.readConfigObject(PATH_CONFIG_WELINK, WeLink.class);
			if (null == obj) {
				obj = WeLink.defaultInstance();
			}
			instance().weLink = obj;
		}
		return instance().weLink;
	}

	private Mpweixin mPweixin;

	public static synchronized Mpweixin mpweixin() throws Exception {
		if (null == instance().mPweixin) {
			Mpweixin obj = BaseTools.readConfigObject(PATH_CONFIG_MPWEIXIN, Mpweixin.class);
			if (obj == null) { // 容错 因为生成的配置文件名称有大小写问题
				obj = BaseTools.readConfigObject(PATH_CONFIG_MPWEIXIN2, Mpweixin.class);
			}
			if (null == obj) {
				obj = Mpweixin.defaultInstance();
			}
			instance().mPweixin = obj;
		}
		return instance().mPweixin;
	}

	private Qiyeweixin qiyeweixin;

	public static synchronized Qiyeweixin qiyeweixin() throws Exception {
		if (null == instance().qiyeweixin) {
			Qiyeweixin obj = BaseTools.readConfigObject(PATH_CONFIG_QIYEWEIXIN, Qiyeweixin.class);
			if (null == obj) {
				obj = Qiyeweixin.defaultInstance();
			}
			instance().qiyeweixin = obj;
		}
		return instance().qiyeweixin;
	}

	private ZhengwuDingding zhengwuDingding;

	public static synchronized ZhengwuDingding zhengwuDingding() throws Exception {
		if (null == instance().zhengwuDingding) {
			ZhengwuDingding obj = BaseTools.readConfigObject(PATH_CONFIG_ZHENGWUDINGDING, ZhengwuDingding.class);
			if (null == obj) {
				obj = ZhengwuDingding.defaultInstance();
			}
			instance().zhengwuDingding = obj;
		}
		return instance().zhengwuDingding;
	}

	private AppStyle appStyle;

	public static synchronized AppStyle appStyle() throws Exception {
		if (null == instance().appStyle) {
			AppStyle obj = BaseTools.readConfigObject(PATH_CONFIG_APPSTYLE, AppStyle.class);
			if (null == obj) {
				obj = AppStyle.defaultInstance();
			}
			instance().appStyle = obj;
		}
		return instance().appStyle;
	}

	private File startImage;

	public static synchronized File startImage() throws Exception {
		if (null == instance().startImage) {
			File file = new File(BaseTools.getBasePath(), PATH_CONFIG_STARTIMAGE);
			if (file.exists() && file.isFile()) {
				instance().startImage = file;
			} else {
				instance().startImage = null;
			}
		}
		return instance().startImage;
	}

	private byte[] bindLogo;

	public static synchronized byte[] bindLogo() throws Exception {
		if (null == instance().bindLogo) {
			File file = new File(Config.base(), PATH_CONFIG_BINDLOGO);
			if (file.exists() && file.isFile()) {
				instance().bindLogo = FileUtils.readFileToByteArray(file);
			} else {
				instance().bindLogo = DEFAULT_BINDLOGO;
			}
		}
		return instance().bindLogo;
	}

	private InitialContext initialContext;

	private static synchronized InitialContext initialContext() throws Exception {
		if (null == instance().initialContext) {
			instance().initialContext = new InitialContext();
		}
		return instance().initialContext;
	}

	public Exmail exmail;

	public static synchronized Exmail exmail() throws Exception {
		if (null == instance().exmail) {
			Exmail obj = BaseTools.readConfigObject(PATH_CONFIG_EXMAIL, Exmail.class);
			if (null == obj) {
				obj = Exmail.defaultInstance();
			}
			instance().exmail = obj;
		}
		return instance().exmail;
	}

	public Portal portal;

	public static synchronized Portal portal() throws Exception {
		if (null == instance().portal) {
			Portal obj = BaseTools.readConfigObject(PATH_CONFIG_PORTAL, Portal.class);
			if (null == obj) {
				obj = Portal.defaultInstance();
			}
			// 兼容7.2之前person.json设置登录页配置
			if (null == obj.getLoginPage() || (BooleanUtils.isFalse(obj.getLoginPage().getEnable())
					&& StringUtils.isBlank(obj.getLoginPage().getPortal()))) {
				JsonObject personJsonObject = BaseTools.readConfigObject(PATH_CONFIG_PERSON, JsonObject.class);
				if (null != personJsonObject && personJsonObject.has("loginPage")) {
					obj.setLoginPage(XGsonBuilder.convert(personJsonObject.get("loginPage"), Portal.LoginPage.class));
				}
			}
			instance().portal = obj;
		}
		return instance().portal;
	}

	public Cache cache;

	public static synchronized Cache cache() throws Exception {
		if (null == instance().cache) {
			Cache obj = BaseTools.readConfigObject(PATH_CONFIG_CACHE, Cache.class);
			if (null == obj) {
				obj = Cache.defaultInstance();
			}
			instance().cache = obj;
		}
		return instance().cache;
	}

	private Components components = null;

	public static synchronized Components components() throws Exception {
		if (null == instance().components) {
			Components obj = BaseTools.readConfigObject(PATH_CONFIG_COMPONENTS, Components.class);
			if (null == obj) {
				obj = Components.defaultInstance();
			}
			instance().components = obj;
		}
		return instance().components;
	}

	public JsonObject web;

	public static synchronized JsonObject web() throws Exception {
		if (null == instance().web) {
			JsonObject obj = BaseTools.readConfigObject(PATH_CONFIG_WEB, JsonObject.class);
			if (null == obj) {
				obj = new JsonObject();
			}
			instance().web = obj;
		}
		return instance().web;
	}

	public General general;

	public static synchronized General general() {
		if (null == instance().general) {
			General obj = BaseTools.readConfigObject(PATH_CONFIG_GENERAL, General.class);
			if (null == obj) {
				obj = General.defaultInstance();
			}
			instance().general = obj;
		}
		return instance().general;
	}

	public JsonObject mock;

	public static synchronized JsonObject mock() throws Exception {
		if (null == instance().mock) {
			JsonObject obj = BaseTools.readConfigObject(PATH_CONFIG_MOCK, JsonObject.class);
			if (null == obj) {
				obj = new JsonObject();
			}
			instance().mock = obj;
		}
		return instance().mock;
	}

	public Map<String, JsonObject> customConfig = new HashMap<>();

	public static synchronized JsonObject customConfig(String configName) throws Exception {
		if (StringUtils.isBlank(configName)) {
			return null;
		} else {
			if (instance().customConfig.get(configName) == null) {
				JsonObject obj = BaseTools.readConfigObject(DIR_CONFIG + "/" + configName + ".json", JsonObject.class);
				if (obj != null) {
					instance().customConfig.put(configName, obj);
				} else {
					obj = BaseTools.readConfigObject(DIR_CONFIGSAMPLE + "/" + configName + ".json", JsonObject.class);
					if (obj != null) {
						instance().customConfig.put(configName, obj);
					}
				}
			}
			return instance().customConfig.get(configName);
		}
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

	@SuppressWarnings("unchecked")
	public static JsonElement resource_node_applications() throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		return (JsonElement) map.get(RESOURCE_NODE_APPLICATIONS);
	}

	@SuppressWarnings("unchecked")
	public static void resource_node_applications(JsonElement jsonElement) throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		map.put(RESOURCE_NODE_APPLICATIONS, jsonElement);
	}

	@SuppressWarnings("unchecked")
	public static Date resource_node_applicationsTimestamp() throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		return (Date) map.get(RESOURCE_NODE_APPLICATIONSTIMESTAMP);
	}

	@SuppressWarnings("unchecked")
	public static void resource_node_applicationsTimestamp(Date date) throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		map.put(RESOURCE_NODE_APPLICATIONSTIMESTAMP, date);
	}

	@SuppressWarnings("unchecked")
	public static String resource_node_centersPirmaryNode() throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		return (String) map.get(RESOURCE_NODE_CENTERSPRIMARYNODE);
	}

	@SuppressWarnings("unchecked")
	public static void resource_node_centersPirmaryNode(String node) throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		map.put(RESOURCE_NODE_CENTERSPRIMARYNODE, node);
	}

	@SuppressWarnings("unchecked")
	public static Integer resource_node_centersPirmaryPort() throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		return (Integer) map.get(RESOURCE_NODE_CENTERSPRIMARYPORT);
	}

	@SuppressWarnings("unchecked")
	public static void resource_node_centersPirmaryPort(Integer port) throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		map.put(RESOURCE_NODE_CENTERSPRIMARYPORT, port);
	}

	@SuppressWarnings("unchecked")
	public static Boolean resource_node_centersPirmarySslEnable() throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		return (Boolean) map.get(RESOURCE_NODE_CENTERSPRIMARYSSLENABLE);
	}

	@SuppressWarnings("unchecked")
	public static void resource_node_centersPirmarySslEnable(Boolean sslEnable) throws Exception {
		ConcurrentHashMap<String, Object> map = (ConcurrentHashMap<String, Object>) initialContext()
				.lookup(RESOURCE_NODE_APPLICATIONS);
		map.put(RESOURCE_NODE_CENTERSPRIMARYSSLENABLE, sslEnable);
	}

	@SuppressWarnings("unchecked")
	public static synchronized Map<String, Date> resource_node_tokenThresholds() throws Exception {
		Object o = initialContext().lookup(RESOURCE_NODE_TOKENTHRESHOLDS);
		if (null != o) {
			return (Map<String, Date>) o;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static synchronized LinkedBlockingQueue<String> resource_commandQueue() throws Exception {
		Object o = initialContext().lookup(RESOURCE_COMMANDQUEUE);
		return (LinkedBlockingQueue<String>) o;
	}

	@SuppressWarnings("unchecked")
	public static synchronized LinkedBlockingQueue<String> resource_commandTerminatedSignal_ctl_rd() throws Exception {
		Object o = initialContext().lookup(RESOURCE_COMMANDTERMINATEDSIGNAL_CTL_RD);
		return (LinkedBlockingQueue<String>) o;
	}

	public static boolean isWindowsJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_WINDOWS));
	}

	public static boolean isLinuxJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_LINUX));
	}

	public static boolean isRaspiJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_RASPI));
	}

	public static boolean isArmJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_ARM));
	}

	public static boolean isMipsJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_MIPS));
	}

	public static boolean isAixJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_AIX));
	}

	public static boolean isMacosJava8() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_MACOS));
	}

	public static boolean isWindowsJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_WINDOWS + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isLinuxJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_LINUX + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isRaspiJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_RASPI + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isArmJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_ARM + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isMipsJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_MIPS + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isAixJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_AIX + "_" + JAVAVERSION_JAVA11));
	}

	public static boolean isMacosJava11() throws Exception {
		return command_java_path().startsWith(dir_jvm().toPath().resolve(OS_MACOS + "_" + JAVAVERSION_JAVA11));
	}

	public static Path path_commons_hadoop_windows(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_WINDOWS);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_linux(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_LINUX);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_aix(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_AIX);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_macos(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_MACOS);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_raspi(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_RASPI);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_arm(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_ARM);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_hadoop_mips(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_COMMONS_HADOOP_MIPS);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_local_dump(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_LOCAL_DUMP);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_local_repository_index(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_LOCAL_REPOSITORY_INDEX);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

//	public static Path path_local_repository_data(boolean force) throws IOException {
//		Path path = Paths.get(base(), DIR_LOCAL_REPOSITORY_DATA);
//		if ((!Files.exists(path)) && force) {
//			Files.createDirectories(path);
//		}
//		return path;
//	}

	public static Path path_commons(boolean force) throws IOException {
		Path path = Paths.get(base(), DIR_COMMONS);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_commons_h2(boolean force) throws IOException {
		Path path = Paths.get(base(), DIR_COMMONS_H2);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_webroot(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_WEBROOT);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_servers_initServer_work(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_SERVERS_INITSERVER_WORK);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_configSample(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_CONFIGSAMPLE);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_local_temp(boolean force) throws IOException, URISyntaxException {
		Path path = Paths.get(base(), DIR_LOCAL_TEMP);
		if ((!Files.exists(path)) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_servers_webServer_x_desktop_res_config(boolean force) throws Exception {
		Path path = Paths.get(base(), DIR_SERVERS_WEBSERVER_X_DESKTOP_RES_CONFIG);
		if (!Files.exists(path) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_config_coverToWebServer(boolean force) throws Exception {
		Path path = Paths.get(base(), DIR_CONFIG_COVERTOWEBSERVER);
		if (!Files.exists(path) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_servers_webServer(boolean force) throws Exception {
		Path path = Paths.get(base(), DIR_SERVERS_WEBSERVER);
		if (!Files.exists(path) && force) {
			Files.createDirectories(path);
		}
		return path;
	}

	public static Path path_servers_webServer_x_init(boolean force) {
		Path path = Paths.get(base(), DIR_SERVERS_WEBSERVER_X_INIT);
		if (!Files.exists(path) && force) {
			createDirectories(path);
		}
		return path;
	}

	public static Path pathLocalRepository(boolean force) {
		Path path = Paths.get(base(), DIR_LOCAL_REPOSITORY);
		if (!Files.exists(path) && force) {
			createDirectories(path);
		}
		return path;
	}

	public static Path pathConfig(boolean force) {
		Path path = Paths.get(base(), DIR_CONFIG);
		if ((!Files.exists(path)) && force) {
			createDirectories(path);
		}
		return path;
	}

	public static Path pathCommonsExt(boolean force) {
		Path path = null;
		if (SystemUtils.IS_JAVA_11) {
			path = Paths.get(base()).resolve(DIR_COMMONS_EXT + "_java11");
		} else {
			path = Paths.get(base()).resolve(DIR_COMMONS_EXT);
		}
		if (!Files.exists(path) && force) {
			createDirectories(path);
		}
		return path;
	}

	public static Path pathLocalRepositoryData(boolean force) {
		Path path = Paths.get(base(), DIR_LOCAL_REPOSITORY_DATA);
		if ((!Files.exists(path)) && force) {
			createDirectories(path);
		}
		return path;
	}

	/**
	 * 创建层级目录,将IOException转换为UncheckedIOException
	 *
	 * @param path
	 */
	private static void createDirectories(Path path) {
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
