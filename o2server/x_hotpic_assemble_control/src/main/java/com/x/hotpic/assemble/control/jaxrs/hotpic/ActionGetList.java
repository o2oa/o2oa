package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.List;
import java.util.Optional;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

public class ActionGetList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String application, String infoId)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(HotPictureInfo.class);

		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		if (application == null || application.isEmpty() || "(0)".equals(application)) {
			throw new InfoApplicationEmptyException();
		}

		if (infoId == null || infoId.isEmpty() || "(0)".equals(infoId)) {
			throw new InfoIdEmptyException();
		}

		String cacheKey = "list#" + application + "#" + infoId;
		CacheKey cacheKeyObj = new Cache.CacheKey(cacheKey);
		Optional<?> element = CacheManager.get(cacheCategory, cacheKeyObj);

		if (null != element) {
			if (element.isPresent()) {
				wraps = (List<Wo>) element.get();
				result.setData(wraps);
				return result;
			}
		}

		try {
			hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(application, infoId);
		} catch (Exception e) {
			throw new InfoListByApplicationException(e, application, infoId);
		}

		if (hotPictureInfos != null && !hotPictureInfos.isEmpty()) {
			try {
				wraps = Wo.copier.copy(hotPictureInfos);
				CacheManager.put(cacheCategory, cacheKeyObj, wraps);
				result.setData(wraps);
			} catch (Exception e) {
				throw new InfoWrapOutException(e);
			}
		}

		return result;
	}


	public static class Wo extends HotPictureInfo {
		private static final long serialVersionUID = 1L;
		public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
