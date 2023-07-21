package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.tools.ListTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

public class ActionChangeTitle extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
		Wo wo = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Wi wi = null;
		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			throw new WrapInConvertException(e, jsonElement);
		}

		if (wi.getApplication() == null || wi.getApplication().isEmpty() || "(0)".equals(wi.getApplication())) {
			throw new InfoApplicationEmptyException();
		}

		if (wi.getInfoId() == null || wi.getInfoId().isEmpty() || "(0)".equals(wi.getInfoId())) {
			throw new InfoIdEmptyException();
		}

		if (wi.getTitle() == null || wi.getTitle().isEmpty()) {
			throw new InfoTitleEmptyException();
		}

		try {
			hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(wi.getApplication(), wi.getInfoId());
		} catch (Exception e) {
			throw new InfoListByApplicationException(e, wi.getApplication(), wi.getInfoId());

		}

		if (ListTools.isNotEmpty(hotPictureInfos)) {
			for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
				try {
					hotPictureInfoService.changeTitle(hotPictureInfo.getId(), wi.getTitle());
					wo = new Wo(hotPictureInfo.getId());
				} catch (Exception e) {
					throw new InfoSaveException(e);
				}
			}
		}

		try {
			CacheManager.notify(HotPictureInfo.class);
		} catch (Exception e) {
			throw e;
		}

		result.setData(wo);
		return result;
	}

	public static class Wi extends HotPictureInfo {

		public static WrapCopier<HotPictureInfo, Wi> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wi.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wo extends GsonPropertyObject {

		public Wo() {
		}

		public Wo(String id) throws Exception {
			this.id = id;
		}

		private String id;

		public String getId() {
			return id;
		}

		public static Type collectionType = new TypeToken<ArrayList<WrapOutId>>() {
		}.getType();
	}
}
