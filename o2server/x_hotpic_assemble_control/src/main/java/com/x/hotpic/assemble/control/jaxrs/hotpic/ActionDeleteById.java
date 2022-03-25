package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

public class ActionDeleteById extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
		Wo wo = null;
		HotPictureInfo hotPictureInfo = null;

		if (id == null || id.isEmpty() || "(0)".equals(id)) {
			throw new InfoIdEmptyException();
		}

		try {
			hotPictureInfo = hotPictureInfoService.get(id);
		} catch (Exception e) {
			throw new InfoQueryByIdException(e, id);
		}

		if (hotPictureInfo != null) {
			try {
				hotPictureInfoService.delete(id);
				wo = new Wo(id);
			} catch (Exception e) {
				throw new InfoDeleteException(e, id);

			}
			try {
				CacheManager.notify(HotPictureInfo.class);
			} catch (Exception e) {
				throw e;
			}
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
