package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;

public class ActionGetList extends BaseAction {
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String application, String infoId)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Ehcache cache = ApplicationCache.instance().getCache(HotPictureInfo.class);
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		if (application == null || application.isEmpty() || "(0)".equals(application)) {
			throw new InfoApplicationEmptyException();
		}

		if (infoId == null || infoId.isEmpty() || "(0)".equals(infoId)) {
			throw new InfoIdEmptyException();
		}

		String cacheKey = "list#" + application + "#" + infoId;
		Element element = cache.get(cacheKey);

		if (null != element) {
			wraps = (List<Wo>) element.getObjectValue();
			result.setData(wraps);
		} else {

			try {
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(application, infoId);
			} catch (Exception e) {
				throw new InfoListByApplicationException(e, application, infoId);
			}

			if (hotPictureInfos != null && !hotPictureInfos.isEmpty()) {
				try {
					wraps = Wo.copier.copy(hotPictureInfos);
					cache.put(new Element(cacheKey, wraps));
					result.setData(wraps);
				} catch (Exception e) {
					throw new InfoWrapOutException(e);
				}
			}

		}

		return result;
	}

	public static class Wi {

	}

	public static class Wo extends HotPictureInfo {
		private static final long serialVersionUID = 1L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}
}
