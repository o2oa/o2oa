package com.x.program.center.jaxrs.market;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
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

class ActionTopThree extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTopThree.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Cache.CacheKey cacheKey = new Cache.CacheKey(ActionTopThree.class);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
		} else {
			String token = Business.loginCollect();
			if (StringUtils.isNotEmpty(token)) {
				try {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("orderBy", "recommend");
					jsonObject.addProperty("isAsc", Boolean.FALSE);
					jsonObject.addProperty("status", "publish");
					String url = StringUtils.replaceEach(COLLECT_MARKET_LIST_INFO, new String[]{"{page}", "{size}"},
							new String[]{"1", "3"});
					ActionResponse response = ConnectionAction.post(
							Config.collect().url(url),
							ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)), jsonObject);
					wos = response.getDataAsList(Wo.class);
					result.setCount(response.getCount());
				} catch (Exception e) {
					logger.warn("list market form o2cloud error: {}.", e.getMessage());
				}
			}

			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				for (Wo wo : wos) {
					InstallLog installLog = emc.find(wo.getId(), InstallLog.class);
					if (installLog != null && CommonStatus.VALID.getValue().equals(installLog.getStatus())) {
						wo.setInstalledVersion(installLog.getVersion());
					} else {
						wo.setInstalledVersion("");
					}
				}
			}

			if(ListTools.isNotEmpty(wos)){
				CacheManager.put(cacheCategory, cacheKey, wos);
			}
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends Application2 {

		@FieldDescribe("已安装的版本，空表示未安装")
		private String installedVersion;

		public String getInstalledVersion() {
			return installedVersion;
		}

		public void setInstalledVersion(String installedVersion) {
			this.installedVersion = installedVersion;
		}


	}

}
