package com.x.message.assemble.communicate.jaxrs.org;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.Org;

public class ActionCreate extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);
	
	
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		
		   logger.debug("receive{}.", jsonElement);
		
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Org org = this.convertToWrapIn(jsonElement, Org.class);
			
			//ProcessOpinion processOpinion = this.convertToWrapIn(jsonElement, ProcessOpinion.class);
			//Org org = Wi.copier.copy(wi);
			
			emc.beginTransaction(Org.class);
			emc.persist(org, CheckPersistType.all);
			emc.commit();
			
			
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = Wo.copier.copy(org);
			result.setData(wo);
			return result;
		}
		
	}
	
	
	
	
	
	
	public static class Wi extends Org {
		private static final long serialVersionUID = -7940036098463672571L;
		static WrapCopier<Wi, Org> copier = WrapCopierFactory.wi(Wi.class, Org.class, null, JpaObject.FieldsUnmodify);
	}

	public static class Wo extends Org {
		private static final long serialVersionUID = -7777196608701722718L;
		static WrapCopier<Org, Wo> copier = WrapCopierFactory.wo(Org.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
	
}
