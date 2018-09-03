package o2.collect.assemble.jaxrs.device;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

import o2.collect.assemble.Business;
import o2.collect.core.entity.Account;
import o2.collect.core.entity.Account_;
import o2.collect.core.entity.Device;
import o2.collect.core.entity.Device_;
import o2.collect.core.entity.Unit;

public class ActionListWithUnitAccount extends BaseAction {

	public ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String unitFlag, String accountFlag)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String unitId = business.unit().flag(unitFlag);
			if (StringUtils.isEmpty(unitId)) {
				throw new ExceptionUnitNotExist(unitFlag);
			}
			Unit unit = emc.find(unitId, Unit.class);
			if ((!effectivePerson.isManager()) && (!effectivePerson.isUser(unit.getName()))) {
				throw new ExceptionAccessDenied(effectivePerson.getName());
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			Account account = this.getAccount(business, unit, accountFlag);
			if (null == account) {
				throw new ExceptionAccountNotExist(accountFlag);
			}
			List<Wo> wos = Wo.copier.copy(this.listDevice(business, account));
			result.setData(wos);
			return result;
		}
	}

	private Account getAccount(Business business, Unit unit, String accountFlag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Account.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> cq = cb.createQuery(Account.class);
		Root<Account> root = cq.from(Account.class);
		Predicate p = cb.equal(root.get(Account_.unit), unit.getId());
		p = cb.and(p,
				cb.or(cb.equal(root.get(Account_.id), accountFlag), cb.equal(root.get(Account_.name), accountFlag)));
		cq.select(root).where(p);
		List<Account> list = em.createQuery(cq).getResultList();
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

	private List<Device> listDevice(Business business, Account account) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Device.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Device> cq = cb.createQuery(Device.class);
		Root<Device> root = cq.from(Device.class);
		Predicate p = cb.equal(root.get(Device_.account), account.getId());
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public static class Wo extends Device {

		private static final long serialVersionUID = 6060455361328632654L;

		static WrapCopier<Device, Wo> copier = WrapCopierFactory.wo(Device.class, Wo.class,
				JpaObject.singularAttributeField(Device.class, true, true), null);

	}
}