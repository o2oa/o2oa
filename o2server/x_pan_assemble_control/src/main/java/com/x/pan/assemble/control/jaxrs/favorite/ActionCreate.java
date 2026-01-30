package com.x.pan.assemble.control.jaxrs.favorite;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityExist;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Favorite;
import com.x.pan.core.entity.Folder3;
import org.apache.commons.lang3.StringUtils;

/**
 * 加入到收藏区
 * @author sword
 */
public class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (StringUtils.isEmpty(wi.getFolder())) {
				throw new ExceptionFieldEmpty(Favorite.folder_FIELDNAME);
			}
			Folder3 folder3 = emc.find(wi.getFolder(), Folder3.class);
			if(folder3 == null){
				throw new ExceptionEntityNotExist(wi.getFolder());
			}
			String name = wi.getName();
			if(StringUtils.isBlank(name)){
				name = folder3.getName();
			}
			Long count = emc.countEqualAndEqual(Favorite.class, Favorite.person_FIELDNAME, effectivePerson.getDistinguishedName(),
					Favorite.folder_FIELDNAME, folder3.getId());
			if(count!=null && count > 0){
				throw new ExceptionEntityExist(name, "收藏区");
			}

			emc.beginTransaction(Favorite.class);
			Favorite favorite = new Favorite(name, effectivePerson.getDistinguishedName(), folder3.getId(), folder3.getZoneId(), wi.getOrderNumber());
			emc.persist(favorite, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setId(favorite.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Favorite {

		static WrapCopier<Wi, Favorite> copier = WrapCopierFactory.wi(Wi.class, Favorite.class,
				ListTools.toList(Favorite.folder_FIELDNAME, Favorite.name_FIELDNAME, Favorite.orderNumber_FIELDNAME),
				null);

	}

	public static class Wo extends WoId {

	}

}
