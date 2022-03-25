package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

public class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String application, String infoId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
		Wo wo = null;
		List<HotPictureInfo> hotPictureInfos = null;

		if (application == null || application.isEmpty() || "(0)".equals(application)) {
			throw new InfoApplicationEmptyException();
		}

		if (infoId == null || infoId.isEmpty() || "(0)".equals(infoId)) {
			throw new InfoIdEmptyException();
		}

		try {
			hotPictureInfos = hotPictureInfoService.listByApplicationInfoId(application, infoId);
		} catch (Exception e) {
			throw new InfoListByApplicationException(e, application, infoId);
		}

		if (ListTools.isNotEmpty(hotPictureInfos)) {
			for (HotPictureInfo hotPictureInfo : hotPictureInfos) {
				try {
					hotPictureInfoService.deleteWithInfoId(hotPictureInfo.getInfoId());
					wo = new Wo(hotPictureInfo.getInfoId());
					result.setData(wo);
				} catch (Exception e) {
					throw new InfoDeleteException(e, application, infoId);
				}
			}
			try {
				CacheManager.notify(HotPictureInfo.class);
			} catch (Exception e) {
				throw e;
			}
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
