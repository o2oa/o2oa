package o2.collect.assemble.jaxrs.account;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Unit;

class ActionListWithUnit extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(AccountAction.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitId) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Business business = new Business(emc);
			Unit unit = emc.find(unitId, Unit.class);
			if (null == unit) {
				throw new ExceptionUnitNotExist(unitId);
			}
			if ((effectivePerson.isNotManager()) && effectivePerson.isNotUser(unit.getName())) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			List<String> ids = business.account().listWithUnit(unitId);
			List<Wo> wos = emc.fetch(ids, Wo.copier);
			result.setData(wos);
			return result;
		}
	}

	public static class Wo extends Account {

		private static final long serialVersionUID = -606629658721902809L;

		static WrapCopier<Account, Wo> copier = WrapCopierFactory.wo(Account.class, Wo.class,
				JpaObject.singularAttributeField(Account.class, true, true), null);

		@FieldDescribe("排序号")
		private Long rank;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}

}
