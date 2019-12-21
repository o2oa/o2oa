//package com.x.processplatform.assemble.surface.jaxrs.applicationdict;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.google.gson.JsonElement;
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.entity.annotation.CheckPersistType;
//import com.x.base.core.project.annotation.FieldDescribe;
//import com.x.base.core.project.cache.ApplicationCache;
//import com.x.base.core.project.http.ActionResult;
//import com.x.base.core.project.http.EffectivePerson;
//import com.x.base.core.project.jaxrs.WoId;
//import com.x.processplatform.assemble.surface.Business;
//import com.x.processplatform.core.entity.element.Application;
//import com.x.processplatform.core.entity.element.ApplicationDict;
//
//class ActionUpdate extends BaseAction {
//
//	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationDictFlag, String applicationFlag,
//			JsonElement jsonElement) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			ActionResult<Wo> result = new ActionResult<>();
//			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
//			Business business = new Business(emc);
//			Application application = business.application().pick(applicationFlag);
//			if (null == application) {
//				throw new ExceptionApplicationNotExist(applicationFlag);
//			}
//			String id = business.applicationDict().getWithApplicationWithUniqueName(application.getId(),
//					applicationDictFlag);
//			if (StringUtils.isEmpty(id)) {
//				throw new ExceptionApplicationDictNotExist(applicationFlag);
//			}
//			ApplicationDict dict = emc.find(id, ApplicationDict.class);
//			if (!business.application().allowControl(effectivePerson, application)) {
//				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
//			}
//			emc.beginTransaction(ApplicationDict.class);
//			wi.copyTo(dict, JpaObject.id_FIELDNAME, JpaObject.distributeFactor_FIELDNAME,
//					ApplicationDict.application_FIELDNAME);
//			emc.check(dict, CheckPersistType.all);
//			this.update(business, dict, wi.getData());
//			emc.commit();
//			/* 这个Action是更新ApplicationDict需要刷新缓存 */
//			ApplicationCache.notify(ApplicationDict.class);
//			Wo wo = new Wo();
//			wo.setId(dict.getId());
//			result.setData(wo);
//			return result;
//		}
//	}
//
//	public static class Wo extends WoId {
//
//	}
//
//	public class Wi extends ApplicationDict {
//
//		private static final long serialVersionUID = 6419951244780354684L;
//
//		@FieldDescribe("写入数据")
//		private JsonElement data;
//
//		public JsonElement getData() {
//			return data;
//		}
//
//		public void setData(JsonElement data) {
//			this.data = data;
//		}
//	}
//
//}
