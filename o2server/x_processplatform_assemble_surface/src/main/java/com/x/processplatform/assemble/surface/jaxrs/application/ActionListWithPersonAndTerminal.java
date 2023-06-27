package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionListWithPersonAndTerminal extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPersonAndTerminal.class);

	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String terminal) throws Exception {
		LOGGER.debug("execute:{}, terminal:{}.", effectivePerson::getDistinguishedName, () -> terminal);
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
				// 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用
				List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
				List<String> roles = business.organization().role().listWithPerson(effectivePerson);
				List<String> groups = business.organization().group().listWithIdentity(identities);
				List<String> ids = this.listFromProcess(business, effectivePerson, roles, identities, units, groups,
						terminal);
				for (String id : ids) {
					Application o = business.application().pick(id);
					if (null != o) {
						Wo wo = Wo.copier.copy(o);
						wo.setProcessList(this.referenceProcess(business, effectivePerson, identities, units, groups, o,
								terminal));
						wos.add(wo);
					}
				}
				wos = business.application().sort(wos);
				CacheManager.put(cacheCategory, cacheKey, wos);
			}
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.application.ActionListWithPersonTerminal$Wo")
	public static class Wo extends Application {

		private static final long serialVersionUID = -4862564047240738097L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("流程对象.")
		@Schema(description = "流程对象.")
		private List<WoProcess> processList;

		@FieldDescribe("是否可编辑.")
		@Schema(description = "是否可编辑.")
		private Boolean allowControl;

		public Boolean getAllowControl() {
			return allowControl;
		}

		public void setAllowControl(Boolean allowControl) {
			this.allowControl = allowControl;
		}

		public List<WoProcess> getProcessList() {
			return processList;
		}

		public void setProcessList(List<WoProcess> processList) {
			this.processList = processList;
		}

	}

	public static class WoProcess extends Process {

		private static final long serialVersionUID = 1521228691441978462L;

		static WrapCopier<Process, WoProcess> copier = WrapCopierFactory.wo(Process.class, WoProcess.class, null,
				JpaObject.FieldsInvisible);

	}

	/**
	 * 从Process中获取可以启动的Process的application.不考虑创建者.
	 * 
	 * @param business
	 * @param effectivePerson
	 * @param roles
	 * @param identities
	 * @param units
	 * @param groups
	 * @param terminal
	 * @return
	 * @throws Exception
	 */
	private List<String> listFromProcess(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units, List<String> groups, String terminal) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Process.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Process> root = cq.from(Process.class);
		Predicate p = cb.conjunction();
		if (BooleanUtils.isNotTrue(business.ifPersonCanManageApplicationOrProcess(effectivePerson, "", ""))) {
			p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
					cb.isEmpty(root.get(Process_.startableUnitList)),
					cb.isEmpty(root.get(Process_.startableGroupList)));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Process_.controllerList)));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Process_.startableUnitList).in(units));
			}
			if (ListTools.isNotEmpty(groups)) {
				p = cb.or(p, root.get(Process_.startableGroupList).in(groups));
			}
			p = cb.and(p,
					cb.and(cb.or(cb.equal(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_ALL),
							cb.equal(root.get(Process_.startableTerminal), terminal)),
							cb.notEqual(root.get(Process_.startableTerminal), Process.STARTABLETERMINAL_NONE)));
		}
		cq.select(root.get(Process_.application)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	private List<WoProcess> referenceProcess(Business business, EffectivePerson effectivePerson,
			List<String> identities, List<String> units, List<String> groups, Application application, String terminal)
			throws Exception {
		List<String> ids = business.process().listStartableWithApplication(effectivePerson, identities, units, groups,
				application, terminal);
		List<WoProcess> wos = new ArrayList<>();
		for (String id : ids) {
			wos.add(WoProcess.copier.copy(business.process().pick(id)));
		}
		wos = business.process().sort(wos);
		return wos;
	}
}
