package com.x.program.center.jaxrs.market;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.program.center.Business;

class ActionListCategory extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCategory.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(effectivePerson.getDistinguishedName());
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Cache.CacheKey cacheKey = new Cache.CacheKey(ActionListCategory.class);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wo = (Wo) optional.get();
		} else {
			String token = Business.loginCollect();
			if (StringUtils.isNotEmpty(token)) {
				try {
					ActionResponse response = ConnectionAction.get(
							Config.collect().url(COLLECT_MARKET_CATEGORY),
							ListTools.toList(new NameValuePair(Collect.COLLECT_TOKEN, token)));
					wo = response.getData(Wo.class);
					CacheManager.put(cacheCategory, cacheKey, wo);
				} catch (Exception e) {
					logger.warn("get market Category form o2cloud error: {}.", e.getMessage());
				}
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapStringList {

	}
}
