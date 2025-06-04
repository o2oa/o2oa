package com.x.program.center.jaxrs.apppack;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.jaxrs.apppack.ActionConnectPackServer.AuthTokenData;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);



	private static String cachedPackServerToken;
	private static Date cachedPackServerTokenDate;

	/**
	 * 获取当前sso完成后的token
	 * @return 认证成功返回token，否则返回null
	 */
	public String getPackServerSSOToken() {
		if ((StringUtils.isNotEmpty(cachedPackServerToken) && (null != cachedPackServerTokenDate))
			&& (cachedPackServerTokenDate.after(new Date()))) {
			return cachedPackServerToken;
		}
		String token = login2AppPackServer();
		if (StringUtils.isNotEmpty(token)) {
			cachedPackServerToken = token;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, 24); // token 24小时缓存
			cachedPackServerTokenDate = cal.getTime();
			return cachedPackServerToken;
		}
		return null;
	}

	/**
	 * 登录apppack服务器
	 * @return 认证成功返回token，否则返回null
	 */
	private String login2AppPackServer() {
		try {
			String url = Config.collect().appPackServerApi(Collect.ADDRESS_APPPACK_AUTH);
			Map<String, String> map = new HashMap<>();
			map.put("collectName", Config.collect().getName()); // o2云账号
			map.put("password", Config.collect().getPassword()); // o2云密码
			String result = HttpConnection.postAsString(url, null, XGsonBuilder.instance().toJson(map));
			logger.info("打包服务器认证，结果: " + result);
			Type type = new TypeToken<AppPackResult<AuthTokenData>>() {
			}.getType();
			AppPackResult<AuthTokenData> packResult = XGsonBuilder.instance().fromJson(result, type);
			if (StringUtils.isNotEmpty(packResult.getResult()) && packResult.getResult().equals(AppPackResult.result_success)) {
				return packResult.getData().getToken();
			} else {
				logger.info("打包服务器认证失败");
			}
		} catch (Exception e) {
			logger.info("登录app打包服务器失败");
			logger.error(e);
		}
		return null;
	}



	protected PackInfoFromServer getPackInfo() {
		try {
			String collectNameEncode = URLEncoder.encode(Config.collect().getName(), DefaultCharset.name);
			String url = Config.collect().appPackServerApi(String.format(Collect.ADDRESS_APPPACK_INFO, collectNameEncode));
			logger.info("打包信息请求，url：" + url);
			ArrayList<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair("token", getPackServerSSOToken()));
			String result = HttpConnection.getAsString(url, heads);
			logger.info("获取到打包信息，结果: " + result);
			Type type = new TypeToken<AppPackResult<PackInfoFromServer>>() {
			}.getType();
			AppPackResult<PackInfoFromServer> appPackResult = XGsonBuilder.instance().fromJson(result, type);
			if (appPackResult != null && StringUtils.isNotEmpty(appPackResult.getResult()) && appPackResult.getResult().equals(AppPackResult.result_success)) {
				return appPackResult.getData();
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}



	public class AppPackResult<T> extends GsonPropertyObject {
		public static final String result_failure = "failure";
		public static final String result_success = "success";

		private String result;
		private String message;
		private String time;
		protected T data;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}

	/**
	 * 打包信息对象
	 */
	public static class PackInfoFromServer extends GsonPropertyObject {

		private String id;
		private String appName;
		// o2oa 服务器信息
		private String o2ServerProtocol; // 中心服务器协议 http | https
		private String o2ServerHost; // 中心服务器地址  ip 或 域名
		private String o2ServerPort; // 中心服务器端口 端口号
		private String o2ServerContext; //  /x_program_center
		// collect 账号
		private String collectName;
		private String createTime;
		// 打包状态 0 准备（入库）1 开始打包 2 打包结束 3 反馈结果完成
		private String packStatus;

		private String appLogoPath; // logo图片地址 相对路径
		private String apkPath; // apk下载地址 相对路径

		// 版本名称
		private String versionName;
		// 版本号 100
		private String buildNo;
		// 是否使用外部包名，2: 就是用 net.zoneland.x.bpm.mobile.v1.zoneXBPM.outer 作为apk的applicationId，默认使用老的applicationId，兼容老的版本
		private String isPackAppIdOuter;

		// 代理地址
		private String urlMapping;

		//错误信息
		private String packErrorLogs;

		// 2 表示要删除华为推送包，防止不是华为手机打开app需要安装HMS服务框架
		private String deleteHuawei;


		private AppPackApkFile appFile; // 关联的下载文件


		public String getPackErrorLogs() {
			return packErrorLogs;
		}

		public void setPackErrorLogs(String packErrorLogs) {
			this.packErrorLogs = packErrorLogs;
		}

		public String getDeleteHuawei() {
			return deleteHuawei;
		}

		public void setDeleteHuawei(String deleteHuawei) {
			this.deleteHuawei = deleteHuawei;
		}

		public String getUrlMapping() {
			return urlMapping;
		}

		public void setUrlMapping(String urlMapping) {
			this.urlMapping = urlMapping;
		}

		public String getVersionName() {
			return versionName;
		}

		public void setVersionName(String versionName) {
			this.versionName = versionName;
		}

		public String getBuildNo() {
			return buildNo;
		}

		public void setBuildNo(String buildNo) {
			this.buildNo = buildNo;
		}

		public String getIsPackAppIdOuter() {
			return isPackAppIdOuter;
		}

		public void setIsPackAppIdOuter(String isPackAppIdOuter) {
			this.isPackAppIdOuter = isPackAppIdOuter;
		}

		public AppPackApkFile getAppFile() {
			return appFile;
		}

		public void setAppFile(AppPackApkFile appFile) {
			this.appFile = appFile;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}


		public String getO2ServerProtocol() {
			return o2ServerProtocol;
		}

		public void setO2ServerProtocol(String o2ServerProtocol) {
			this.o2ServerProtocol = o2ServerProtocol;
		}

		public String getO2ServerHost() {
			return o2ServerHost;
		}

		public void setO2ServerHost(String o2ServerHost) {
			this.o2ServerHost = o2ServerHost;
		}

		public String getO2ServerPort() {
			return o2ServerPort;
		}

		public void setO2ServerPort(String o2ServerPort) {
			this.o2ServerPort = o2ServerPort;
		}

		public String getO2ServerContext() {
			return o2ServerContext;
		}

		public void setO2ServerContext(String o2ServerContext) {
			this.o2ServerContext = o2ServerContext;
		}

		public String getCollectName() {
			return collectName;
		}

		public void setCollectName(String collectName) {
			this.collectName = collectName;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getPackStatus() {
			return packStatus;
		}

		public void setPackStatus(String packStatus) {
			this.packStatus = packStatus;
		}

		public String getAppLogoPath() {
			return appLogoPath;
		}

		public void setAppLogoPath(String appLogoPath) {
			this.appLogoPath = appLogoPath;
		}

		public String getApkPath() {
			return apkPath;
		}

		public void setApkPath(String apkPath) {
			this.apkPath = apkPath;
		}
	}
}