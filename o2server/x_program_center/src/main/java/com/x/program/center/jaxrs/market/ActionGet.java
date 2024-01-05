package com.x.program.center.jaxrs.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.enums.CommonStatus;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;
import com.x.program.center.core.entity.InstallLog;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(effectivePerson.getDistinguishedName());
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Cache.CacheKey cacheKey = new Cache.CacheKey(ActionGet.class, id);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wo = (Wo) optional.get();
		} else {
			String token = Business.loginCollect();
			if (StringUtils.isNotEmpty(token)) {
				try {
					ActionResponse response = ConnectionAction.get(
							Config.collect().url(COLLECT_MARKET_INFO + id),
							ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
					wo = response.getData(Wo.class);
				} catch (Exception e) {
					logger.warn("get market info form o2cloud error: {}.", e.getMessage());
				}
			}
			if(StringUtils.isNotBlank(wo.getId())) {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					InstallLog installLog = emc.find(wo.getId(), InstallLog.class);
					if (installLog != null && CommonStatus.VALID.getValue().equals(installLog.getStatus())) {
						wo.setInstalledVersion(installLog.getVersion());
					} else {
						wo.setInstalledVersion("");
					}
					result.setData(wo);
				}
				CacheManager.put(cacheCategory, cacheKey, wo);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends Application2 {

		@FieldDescribe("展示图片url列表.")
		private List<String> picList = new ArrayList<>();

		@FieldDescribe("已安装的版本，空表示未安装")
		private String installedVersion;

		public List<String> getPicList() {
			return picList;
		}

		public void setPicList(List<String> picList) {
			this.picList = picList;
		}

		public String getInstalledVersion() {
			return installedVersion;
		}

		public void setInstalledVersion(String installedVersion) {
			this.installedVersion = installedVersion;
		}
	}
}
