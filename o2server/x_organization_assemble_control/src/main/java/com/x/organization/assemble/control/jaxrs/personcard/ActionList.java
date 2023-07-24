package com.x.organization.assemble.control.jaxrs.personcard;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersonCard;


class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);



	// 列出创建是当前登录用户的所有线索,并且按照创建时间倒序排列。
	ActionResult<List<Wo>> MyExecute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			List<String> ids = business.personCard().fetchAllIdsByCreator(effectivePerson.getDistinguishedName());
			List<Wo> wos = Wo.copier.copy(emc.list(PersonCard.class,true, ids));
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends WoPersonCardAbstract {
		private static final long serialVersionUID = 1276641320278402941L;
		static WrapCopier<PersonCard, Wo> copier = WrapCopierFactory.wo(PersonCard.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

}
