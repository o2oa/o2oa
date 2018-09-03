package o2.collect.assemble.jaxrs.account;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Account account = emc.find(id, Account.class);
			if (null == account) {
				throw new ExceptionAccountNotExist(id);
			}
			Unit unit = emc.find(account.getUnit(), Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(account.getUnit());
			}
			emc.beginTransaction(Account.class);
			emc.beginTransaction(Device.class);
			List<String> deviceIds = business.device().listWithAccount(account.getId());
			for (Device o : business.entityManagerContainer().list(Device.class, deviceIds)) {
				business.entityManagerContainer().remove(o, CheckRemoveType.all);
			}
			business.entityManagerContainer().remove(account, CheckRemoveType.all);
			business.entityManagerContainer().commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}
