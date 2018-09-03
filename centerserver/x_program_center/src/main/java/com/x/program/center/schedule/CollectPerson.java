package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Host;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.program.center.Business;

public class CollectPerson extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(CollectPerson.class);

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.debug("start to collect Person.");
		try {
			if (BooleanUtils.isTrue(Config.collect().getEnable())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					if (business.validateCollect()) {
						List<String> mobiles = this.listMobile(emc);
						Req req = new Req();
						req.setName(Config.collect().getName());
						req.setPassword(Config.collect().getPassword());
						req.setMobileList(mobiles);
						req.setCenterProxyHost(Config.centerServer().getProxyHost());
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
						req.setCenterProxyPort(Config.centerServer().getProxyPort());
						req.setHttpProtocol(Config.centerServer().getHttpProtocol());
						try {
							ActionResponse response = ConnectionAction
									.put(Config.collect().url(ADDRESS_COLLECT_TRANSMIT_RECEIVE), null, req);
							response.getData(WrapOutBoolean.class);
						} catch (Exception e) {
							logger.warn("与云服务器连接错误:{}." + e.getMessage());
						}
					} else {
						logger.debug("无法登录到云服务器.");
					}
				}
			} else {
				logger.debug("系统没有启用O2云服务器连接.");
			}
		} catch (

		Exception e) {
			logger.error(e);
		}
	}

	public static class Req extends GsonPropertyObject {

		private String name;

		private String password;

		private List<String> mobileList = new ArrayList<>();

		private String centerProxyHost;

		private Integer centerProxyPort;

		private String httpProtocol;

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

	}

	private List<String> listMobile(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		cq.select(root.get(Person_.mobile));
		List<String> list = em.createQuery(cq).getResultList();
		List<String> mobiles = new ArrayList<>();
		for (String str : list) {
			if (StringUtils.isNotEmpty(str)) {
				mobiles.add(str);
			}
		}
		return mobiles;
	}

}