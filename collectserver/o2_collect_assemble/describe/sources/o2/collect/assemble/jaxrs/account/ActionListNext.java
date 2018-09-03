package o2.collect.assemble.jaxrs.account;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.core.entity.Account;

class ActionListNext extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String flag, Integer count) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			String id = EMPTY_SYMBOL;
			/** 如果不是空位标志位 */
			if (!StringUtils.equals(EMPTY_SYMBOL, flag)) {
				Account o = emc.find(flag, Account.class);
				if (null == o) {
					throw new ExceptionAccountNotExist(flag);
				}
				id = o.getId();
			}
			result = this.standardListNext(Wo.copier, id, count, "sequence", null, null, null, null, null, null, null,
					true, DESC);
			return result;
		}
	}

	public static class Wo extends Account {

		private static final long serialVersionUID = -125007357898871894L;

		@FieldDescribe("排序号")
		private Long rank;

		static WrapCopier<Account, Wo> copier = WrapCopierFactory.wo(Account.class, Wo.class,
				JpaObject.singularAttributeField(Account.class, true, true), null);

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

	}
}