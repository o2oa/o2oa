package com.x.program.center.schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.Application;
import com.x.program.center.core.entity.Attachment;

/**
 * @author sword
 */
public class CollectMarket extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(CollectMarket.class);

	private static ReentrantLock lock = new ReentrantLock();

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		lock.lock();
		try {
			if (pirmaryCenter() && BooleanUtils.isTrue(Config.collect().getEnable())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					String token = Business.loginCollect();
					if (StringUtils.isNotEmpty(token)) {
						LOGGER.info("start sync market data.");
						List<Wi> wiList = null;
						try {
							ActionResponse response = ConnectionAction.get(
									Config.collect().url(Collect.ADDRESS_COLLECT_MARKET),
									ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
							wiList = response.getDataAsList(Wi.class);
						} catch (Exception e) {
							LOGGER.warn("connect o2cloud {}.", e.getMessage());
						}
						if (wiList != null && !wiList.isEmpty()) {
							LOGGER.info("wait sync market app size：{}", wiList.size());
							emc.beginTransaction(Application.class);
							emc.beginTransaction(Attachment.class);
							List<Application> appList = emc.listAll(Application.class);
							Map<String, Application> appMap = new HashMap<>();
							List<String> appIds = ListTools.extractField(wiList, JpaObject.id_FIELDNAME, String.class,
									true, true);
							for (Application app : appList) {
								if (appIds.contains(app.getId())) {
									appMap.put(app.getId(), app);
								} else {
									List<Attachment> attachments = emc.listEqual(Attachment.class,
											Attachment.application_FIELDNAME, app.getId());
									for (Attachment att : attachments) {
										emc.remove(att);
									}
									emc.remove(app);
								}
							}
							for (Wi wi : wiList) {
								Application app = appMap.get(wi.getId());
								if (app != null) {
									if (wi.getLastUpdateTime().compareTo(app.getLastUpdateTime()) == 1) {
										Wi.copier.copy(wi, app);
										emc.persist(app, CheckPersistType.all);
										List<Attachment> attachments = emc.listEqual(Attachment.class,
												Attachment.application_FIELDNAME, app.getId());
										List<String> attIds = ListTools.extractField(wi.getAttList(),
												JpaObject.id_FIELDNAME, String.class, true, true);
										List<String> attIds2 = new ArrayList<>();
										for (Attachment att : attachments) {
											if (attIds.contains(att.getId())) {
												attIds2.add(att.getId());
											} else {
												emc.remove(att);
											}
										}
										if (wi.getAttList() != null) {
											for (Attachment att : wi.getAttList()) {
												if (!attIds2.contains(att.getId())) {
													emc.persist(att, CheckPersistType.all);
												}
											}
										}
									}
								} else {
									app = Wi.copier.copy(wi);
									emc.persist(app, CheckPersistType.all);
									if (wi.attList != null) {
										for (Attachment att : wi.attList) {
											emc.persist(att, CheckPersistType.all);
										}
									}
								}
							}
							emc.commit();
						}
						LOGGER.info("end sync market data.");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
			throw new JobExecutionException(e);
		} finally {
			lock.unlock();
		}
	}

	public static class Wi extends Application {

		static WrapCopier<Wi, Application> copier = WrapCopierFactory.wo(Wi.class, Application.class, null,
				ListTools.toList("attList"));

		private List<Attachment> attList = new ArrayList<>();

		public List<Attachment> getAttList() {
			return attList;
		}

		public void setAttList(List<Attachment> attList) {
			this.attList = attList;
		}
	}

	public static void main(String[] args) throws Exception{
		System.out.println("==========1");
		ActionResponse response1 = ConnectionAction.get(
				"http://collect.o2oa.net:20080/market",
				ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, "DltLNMgB3BaKEeBBjgHVADkfpuTt3sqriL15iT2Cjgk")));
		List<Wi> wiList = response1.getDataAsList(Wi.class);
		System.out.println("wait sync market app size："+ wiList.size());
		System.out.println("==========2");

	}

}
