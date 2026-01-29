package com.x.pan.assemble.control.jaxrs.favorite;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Favorite;
import org.apache.commons.lang3.StringUtils;

class ActionUpdate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isBlank(wi.getName()) && wi.getOrderNumber() == null) {
				result.setData(wo);
				return result;
			}
			Favorite favorite = emc.find(id, Favorite.class);
			if (favorite == null) {
				throw new ExceptionEntityNotExist(id);
			}

			if(!favorite.getPerson().equals(effectivePerson.getDistinguishedName())){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			emc.beginTransaction(Favorite.class);
			if(StringUtils.isNotBlank(wi.getName())){
				favorite.setName(wi.getName());
			}
			if(wi.getOrderNumber() != null){
				favorite.setOrderNumber(wi.getOrderNumber());
			}
			emc.check(favorite, CheckPersistType.all);
			emc.commit();

			wo.setValue(true);
			result.setData(wo);
			return result;

		}
	}

	public static class Wi extends Favorite {

		static WrapCopier<Wi, Favorite> copier = WrapCopierFactory.wi(Wi.class, Favorite.class,
				ListTools.toList(Favorite.name_FIELDNAME, Favorite.orderNumber_FIELDNAME),
				null);

	}

	public static class Wo extends WrapBoolean {

	}

}
