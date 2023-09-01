package com.x.processplatform.service.processing.jaxrs.applicationdict;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ProcessPlatformKeyClassifyExecutorFactory;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

		Callable<ActionResult<Wo>> callable = new Callable<ActionResult<Wo>>() {
			public ActionResult<Wo> call() throws Exception {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					ActionResult<Wo> result = new ActionResult<>();
					Business business = new Business(emc);
					ApplicationDict dict = emc.find(id, ApplicationDict.class);
					if (null == dict) {
						throw new ExceptionEntityNotExist(id, ApplicationDict.class);
					}
					emc.beginTransaction(ApplicationDict.class);
					wi.copyTo(dict, JpaObject.id_FIELDNAME, JpaObject.distributeFactor_FIELDNAME,
							ApplicationDict.application_FIELDNAME);
					emc.check(dict, CheckPersistType.all);
					update(business, dict, wi.getData());
					emc.commit();
					// 这个Action是更新ApplicationDict需要刷新缓存
					CacheManager.notify(ApplicationDict.class);
					Wo wo = new Wo();
					wo.setId(dict.getId());
					result.setData(wo);
					return result;
				}
			}
		};

		return ProcessPlatformKeyClassifyExecutorFactory.get(id).submit(callable).get(300, TimeUnit.SECONDS);

	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 5709352378321901902L;

	}

	public class Wi extends ApplicationDict {

		private static final long serialVersionUID = 6419951244780354684L;

		@FieldDescribe("写入数据")
		private JsonElement data;

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}
	}

}