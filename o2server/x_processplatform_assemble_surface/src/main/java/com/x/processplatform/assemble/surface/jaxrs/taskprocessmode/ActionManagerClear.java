package com.x.processplatform.assemble.surface.jaxrs.taskprocessmode;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.content.TaskProcessMode;
import org.apache.commons.lang3.BooleanUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;

class ActionManagerClear extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionManagerClear.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String person) throws Exception {

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			LOGGER.info("{}操作清除【{}】的待办处理方式记录.", effectivePerson.getDistinguishedName(), person);
			ActionResult<Wo> result = new ActionResult<>();

			Business business = new Business(emc);
			if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			EntityManager em = emc.get(TaskProcessMode.class);
			String sql = "DELETE FROM " + TaskProcessMode.class.getName();
			if(!EMPTY_SYMBOL.equals(person)){
				Person personObj = business.organization().person().getObject(person);
				if(personObj != null){
					person = personObj.getUnique();
				}
				sql = sql + " o WHERE o.person = '"+person+"'";
			}
			emc.beginTransaction(TaskProcessMode.class);
			Query query = em.createQuery(sql);
			query.executeUpdate();
			emc.commit();

			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
