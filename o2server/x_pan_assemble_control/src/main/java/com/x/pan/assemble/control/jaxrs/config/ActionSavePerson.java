package com.x.pan.assemble.control.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sword
 */
public class ActionSavePerson extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);

			/* 判断当前用户是否有权限访问 */
			if(!business.controlAble(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}

			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if(StringUtils.isBlank(wi.getPerson())){
				throw new ExceptionFieldEmpty(FileConfig3.person_FIELDNAME);
			}
			if(wi.getCapacity() == null){
				throw new ExceptionFieldEmpty(FileConfig3.capacity_FIELDNAME);
			}
			String person = business.organization().person().get(wi.getPerson());
			if(StringUtils.isBlank(person)){
				throw new ExceptionPersonNotExist(wi.getPerson());
			}
			FileConfig3 fileConfig = emc.firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, person);
			emc.beginTransaction(FileConfig3.class);
			if(fileConfig!=null){
				if(wi.getCapacity()==null || wi.getCapacity() < 1){
					fileConfig.setCapacity(0);
				}else{
					fileConfig.setCapacity(wi.getCapacity());
				}
				emc.check(fileConfig, CheckPersistType.all);
			}else{
				fileConfig = new FileConfig3();
				if(wi.getCapacity()==null || wi.getCapacity() < 1){
					fileConfig.setCapacity(0);
				}else{
					fileConfig.setCapacity(wi.getCapacity());
				}
				fileConfig.setPerson(person);
				emc.persist(fileConfig, CheckPersistType.all);
			}
			emc.commit();
			CacheManager.notify(FileConfig3.class);
			Wo wo = new Wo();
			wo.setId(fileConfig.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends FileConfig3 {

		private static final long serialVersionUID = 539578853314049178L;

		static WrapCopier<Wi, FileConfig3> copier = WrapCopierFactory.wi(Wi.class, FileConfig3.class,
				ListTools.toList(FileConfig3.person_FIELDNAME, FileConfig3.capacity_FIELDNAME),null);

	}

	public static class Wo extends WoId {

	}

}
