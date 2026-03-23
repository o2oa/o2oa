package com.x.pan.assemble.control.jaxrs.zone;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.Folder3;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ActionManagerList extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger( ActionManagerList.class );

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			if(!business.controlAble(effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
			}
			List<Wo> wos = emc.fetchEqual(Folder3.class, Wo.copier, Folder3.superior_FIELDNAME, Business.TOP_FOLD);
			for (Wo wo : wos){
				wo.setUsedCapacity(business.attachment3().statZoneCapacity(wo.getZoneId()));
			}
			wos = wos.stream().sorted(Comparator.comparing(Folder3::getName, Comparator.nullsLast(String::compareTo)))
					.collect(Collectors.toList());
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Zone {

		protected static WrapCopier<Folder3, Wo> copier = WrapCopierFactory.wo(Folder3.class, Wo.class,
				JpaObject.singularAttributeField(Folder3.class, true, true), null);

	}

}
