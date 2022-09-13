package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Person;
import com.x.program.center.Business;

public class CollectPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectPerson.class);

	private static ReentrantLock lock = new ReentrantLock();

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		lock.lock();
		try {
			if (pirmaryCenter() && BooleanUtils.isTrue(Config.collect().getEnable())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					if (business.validateCollect()) {
						List<String> mobiles = this.listMobile(emc);
						Req req = new Req();
						req.setTitle(Config.collect().getTitle());
						req.setFooter(Config.collect().getFooter());
						req.setName(Config.collect().getName());
						req.setPassword(Config.collect().getPassword());
						req.setSecret(Config.collect().getSecret());
						req.setKey(Config.collect().getKey());
						req.setMobileList(mobiles);
						CenterServer centerServer = Config.nodes().centerServers().first().getValue();
						req.setCenterProxyHost(centerServer.getProxyHost());
						if (StringUtils.isEmpty(req.getCenterProxyHost())) {
							/* 如果没有设置地址,那么使用远程得到的服务器地址 */
							if (Host.ip(Config.node()) && (!Host.isRollback(Config.node()))
									&& (!Host.innerIp(Config.node()))) {
								/* 如果不是外网地址也不是回环地址,那么说明是一个公网地址 */
								req.setCenterProxyHost(Config.node());
							} else {
								ActionResponse respIp = ConnectionAction
										.get(Config.collect().url(ADDRESS_COLLECT_REMOTE_IP), null);
								req.setCenterProxyHost(respIp.getData(WrapString.class).getValue());
							}
						}
						req.setCenterProxyPort(centerServer.getProxyPort());
						req.setHttpProtocol(centerServer.getHttpProtocol());
						if(null != Config.portal().getUrlMapping()){
							req.setUrlMapping(XGsonBuilder.convert(Config.portal().getUrlMapping(), JsonElement.class));
						}
						try {
							ActionResponse response = ConnectionAction
									.put(Config.collect().url(ADDRESS_COLLECT_TRANSMIT_RECEIVE), null, req);
							response.getData(WrapOutBoolean.class);
						} catch (Exception e) {
							LOGGER.warn("与云服务器连接错误:{}." + e.getMessage());
						}
					} else {
						LOGGER.debug("无法登录到云服务器.");
					}
				}
			} else {
				LOGGER.debug("系统没有启用O2云服务器连接.");
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		} finally {
			lock.unlock();
		}
	}

	public static class Req extends GsonPropertyObject {

		private String title;

		private String footer;

		private String name;

		private String password;

		private List<String> mobileList = new ArrayList<>();

		private String centerProxyHost;

		private Integer centerProxyPort;

		private String httpProtocol;

		private String secret;

		private String key;

		private JsonElement urlMapping;

		public JsonElement getUrlMapping() {
			return urlMapping;
		}

		public void setUrlMapping(JsonElement urlMapping) {
			this.urlMapping = urlMapping;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public List<String> getMobileList() {
			return mobileList;
		}

		public void setMobileList(List<String> mobileList) {
			this.mobileList = mobileList;
		}

		public String getCenterProxyHost() {
			return centerProxyHost;
		}

		public void setCenterProxyHost(String centerProxyHost) {
			this.centerProxyHost = centerProxyHost;
		}

		public Integer getCenterProxyPort() {
			return centerProxyPort;
		}

		public void setCenterProxyPort(Integer centerProxyPort) {
			this.centerProxyPort = centerProxyPort;
		}

		public String getHttpProtocol() {
			return httpProtocol;
		}

		public void setHttpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getFooter() {
			return footer;
		}

		public void setFooter(String footer) {
			this.footer = footer;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	private List<String> listMobile(EntityManagerContainer emc) throws Exception {
		List<String> list = emc.select(Person.class, Person.mobile_FIELDNAME, String.class);
		return ListTools.trim(list, true, true);
	}

}
