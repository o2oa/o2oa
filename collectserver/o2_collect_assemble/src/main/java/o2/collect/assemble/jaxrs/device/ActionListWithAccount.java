package o2.collect.assemble.jaxrs.device;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Unit;

public class ActionListWithAccount extends BaseAction {

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String accountId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Account account = emc.find(accountId, Account.class);
			if (null == account) {
				throw new ExceptionAccountNotExist(accountId);
			}
			Unit unit = emc.find(account.getUnit(), Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(account.getUnit());
			}
			if ((!effectivePerson.isManager()) && (!effectivePerson.isUser(unit.getName()))) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			List<String> ids = business.device().listWithAccount(accountId);
			List<Wo> wos = Wo.copier.copy(business.entityManagerContainer().list(Device.class, ids));
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Device {

		private static final long serialVersionUID = 6060455361328632654L;

		static WrapCopier<Device, Wo> copier = WrapCopierFactory.wo(Device.class, Wo.class,
				JpaObject.singularAttributeField(Device.class, true, true), null);

		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}