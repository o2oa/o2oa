package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

public class ActionCipherList extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer count,
			JsonElement jsonElement) throws Exception {

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps_out = new ArrayList<Wo>();
		List<HotPictureInfo> hotPictureInfoList = null;
		Integer selectTotal = 0;
		Long total = 0L;

		Wi wi = null;
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			throw new WrapInConvertException(e, jsonElement);
		}

		if (page == null) {
			page = 1;
		}

		if (page <= 0) {
			page = 1;
		}

		if (count == null) {
			count = 20;
		}

		if (count <= 0) {
			count = 20;
		}

		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(HotPictureInfo.class);

		selectTotal = page * count;
		String cacheKey1 = "filter#" + page + "#" + count + "#" + wi.getApplication() + "#" + wi.getInfoId() + "#"
				+ wi.getTitle();
		CacheKey cacheKeyObj1 = new Cache.CacheKey(cacheKey1);
		Optional<?> element1 = CacheManager.get(cacheCategory, cacheKeyObj1);

		String cacheKey2 = "total#" + page + "#" + count + "#" + wi.getApplication() + "#" + wi.getInfoId() + "#"
				+ wi.getTitle();
		CacheKey cacheKeyObj2 = new Cache.CacheKey(cacheKey2);
		Optional<?> element2 = CacheManager.get(cacheCategory, cacheKeyObj2);

		if (null != element1 && null != element2) {
			if (element1.isPresent()) {
				wraps_out = (List<Wo>) element1.get();
				result.setCount(Long.parseLong(element2.get().toString()));
				result.setData(wraps_out);
			}
		}

		if (selectTotal > 0) {
			try {
				total = hotPictureInfoService.count(wi.getApplication(), wi.getInfoId(), wi.getTitle());

			} catch (Exception e) {
				throw new InfoListByFilterException(e);
			}
		}

		if (selectTotal > 0 && total > 0) {

			int startIndex = (page - 1) * count;
			int endIndex = count;

			try {
				hotPictureInfoList = hotPictureInfoService.listForPage(wi.getApplication(), wi.getInfoId(),
						wi.getTitle(), startIndex, endIndex);
				if (hotPictureInfoList != null) {
					try {
						wraps_out = Wo.copier.copy(hotPictureInfoList);
						// SortTools.desc(wraps_out, JpaObject.sequence_FIELDNAME);
					} catch (Exception e) {
						throw new InfoWrapOutException(e);
					}
				}
			} catch (Exception e) {
				throw new InfoListByFilterException(e);
			}
		}

		CacheManager.put(cacheCategory, cacheKeyObj1, wraps_out);
		CacheManager.put(cacheCategory, cacheKeyObj2, total.toString());

		result.setData(wraps_out);
		result.setCount(total);
		return result;
	}

	public static class Wi {

		@FieldDescribe("应用名称：CMS|BBS等等.")
		private String application = null;

		@FieldDescribe("信息ID.")
		private String infoId = null;

		@FieldDescribe("信息标题，模糊查询.")
		private String title = null;

		// public static List<String> Excludes = new
		// ArrayList<String>(JpaObject.FieldsUnmodify);

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getInfoId() {
			return infoId;
		}

		public void setInfoId(String infoId) {
			this.infoId = infoId;
		}
	}

	public static class Wo extends HotPictureInfo {

		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<HotPictureInfo, Wo> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
