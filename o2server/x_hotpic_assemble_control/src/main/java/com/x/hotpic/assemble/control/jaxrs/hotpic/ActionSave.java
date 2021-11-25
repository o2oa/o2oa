package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.hotpic.assemble.control.service.HotPictureInfoService;
import com.x.hotpic.entity.HotPictureInfo;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import org.apache.commons.lang3.StringUtils;

/**
 * 热点图片保存
 * @author sword
 */
public class ActionSave extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionSave.class);
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();
		HotPictureInfoService hotPictureInfoService = new HotPictureInfoService();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		if (wi.getTitle() == null || wi.getTitle().isEmpty()) {
			throw new InfoTitleEmptyException();
		}

		if (wi.getUrl() == null || wi.getUrl().isEmpty()) {
			throw new InfoUrlEmptyException();
		}

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			HotPictureInfo hotPictureInfo = null;
			if(StringUtils.isNotBlank(wi.getId())){
				hotPictureInfo = emc.find(wi.getId(), HotPictureInfo.class);
				if(hotPictureInfo == null){
					hotPictureInfo = hotPictureInfoService.getByApplicationInfoId(emc, wi.getApplication(), wi.getInfoId());
				}
			}
			emc.beginTransaction(HotPictureInfo.class);
			if (hotPictureInfo!=null){
				hotPictureInfo.setPicId(wi.getPicId());
				hotPictureInfo.setSummary(wi.getSummary());
				hotPictureInfo.setTitle(wi.getTitle());
				hotPictureInfo.setUrl(wi.getUrl());
				emc.check(hotPictureInfo, CheckPersistType.all);
			}else{
				hotPictureInfo = new HotPictureInfo();
				hotPictureInfo.setInfoId(wi.getInfoId());
				hotPictureInfo.setApplication(wi.getApplication());
				hotPictureInfo.setCreator(wi.getCreator());
				hotPictureInfo.setPicId(wi.getPicId());
				hotPictureInfo.setSummary(wi.getSummary());
				hotPictureInfo.setTitle(wi.getTitle());
				hotPictureInfo.setUrl(wi.getUrl());
				emc.persist(hotPictureInfo, CheckPersistType.all);
			}
			emc.commit();
			Wo wo = new Wo();
			wo.setId(hotPictureInfo.getId());
			result.setData(wo);
			CacheManager.notify(HotPictureInfo.class);
		}
		return result;
	}

	public static class Wi extends HotPictureInfo {

		public static final WrapCopier<HotPictureInfo, Wi> copier = WrapCopierFactory.wo(HotPictureInfo.class, Wi.class, null,
				JpaObject.FieldsInvisible);

	}

	public static class Wo extends WoId {

	}
}
