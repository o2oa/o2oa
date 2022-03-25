package com.x.hotpic.assemble.control.jaxrs.hotpic;

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

public class ActionCipherGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		HotPictureInfo hotPictureInfo = null;

		if (id == null || id.isEmpty() || "(0)".equals(id)) {
			throw new InfoIdEmptyException();
		}

		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(HotPictureInfo.class);

		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		String cacheKey = "base64#" + id;
		CacheKey cacheKeyObj = new Cache.CacheKey(cacheKey);
		Optional<?> element = CacheManager.get(cacheCategory, cacheKeyObj);

		if (null != element) {
			if (element.isPresent()) {
				wo = (Wo) element.get();
				result.setData(wo);
				return result;
			}
		}
		try {
			hotPictureInfo = hotPictureInfoService.get(id);
			if (hotPictureInfo == null) {
				throw new InfoNotExistsException(id);
			} else {
				wo = Wo.copier.copy(hotPictureInfo);
				CacheManager.put(cacheCategory, cacheKeyObj, wo);
				result.setData(wo);
			}
		} catch (Exception e) {
			throw new InfoQueryByIdException(e, id);
		}

		return result;
	}



	public static class Wo extends HotPictureInfo {
		public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
