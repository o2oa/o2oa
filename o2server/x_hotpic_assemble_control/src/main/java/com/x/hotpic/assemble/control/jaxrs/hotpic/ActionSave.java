package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;

public class ActionSave extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();

		Wi wi = null;
		HotPictureInfo hotPictureInfo = null;
		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			throw new WrapInConvertException(e, jsonElement);
		}

		if (wi.getTitle() == null || wi.getTitle().isEmpty()) {
			throw new InfoTitleEmptyException();
		}

		if (wi.getUrl() == null || wi.getUrl().isEmpty()) {
			throw new InfoUrlEmptyException();
		}

		try {
			//hotPictureInfo = Wi.copier.copy(wi);
			hotPictureInfo = new HotPictureInfo();
			hotPictureInfo.setInfoId(wi.getInfoId());
			hotPictureInfo.setApplication(wi.getApplication());
			hotPictureInfo.setCreator(wi.getCreator());
			hotPictureInfo.setPicId(wi.getPicId());
			hotPictureInfo.setSummary(wi.getSummary());
			hotPictureInfo.setTitle(wi.getTitle());
			hotPictureInfo.setUrl(wi.getUrl());
		} catch (Exception e) {
			throw new InfoWrapInException(e);
		}

		try {
			hotPictureInfo = hotPictureInfoService.save(hotPictureInfo);
			Wo wo = new Wo(hotPictureInfo.getId());
			result.setData(wo);
		} catch (Exception e) {
			throw new InfoSaveException(e);

		}

		try {
			ApplicationCache.notify(HotPictureInfo.class);
		} catch (Exception e) {
			throw e;
		}

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
