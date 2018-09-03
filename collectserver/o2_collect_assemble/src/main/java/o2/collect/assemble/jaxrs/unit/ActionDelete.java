package o2.collect.assemble.jaxrs.unit;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Unit unit = emc.find(id, Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(id);
			}
			if (!effectivePerson.isManager() && (!effectivePerson.isUser(unit.getName()))) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			emc.beginTransaction(Unit.class);
			emc.beginTransaction(Account.class);
			emc.beginTransaction(Device.class);
			List<String> deviceIds = business.device().listWithUnit(unit.getId());
			for (Device o : business.entityManagerContainer().list(Device.class, deviceIds)) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			List<String> accountIds = business.account().listWithUnit(unit.getId());
			for (Account o : business.entityManagerContainer().list(Account.class, accountIds)) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			business.entityManagerContainer().remove(unit, CheckRemoveType.all);
			business.entityManagerContainer().commit();
			if (effectivePerson.isNotManager()) {
				HttpToken httpToken = new HttpToken();
				httpToken.setToken(request, response, EffectivePerson.anonymous());
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
