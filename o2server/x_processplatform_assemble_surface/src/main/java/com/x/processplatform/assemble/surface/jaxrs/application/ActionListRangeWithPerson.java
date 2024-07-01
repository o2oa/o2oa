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

import org.apache.commons.collections.ListUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 2022-11-22 此方法用于在系统菜单->流程中显示可用应用,基于应用中设置的可用身份和可用组织确定
 * 
 * @author ray
 *
 */
class ActionListRangeWithPerson extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionListRangeWithPerson.class);

	/**
	 * 1.身份在可使用列表中 <br/>
	 * 2.组织在可使用组织中 <br/>
	 * 4.没有限定身份和组织 <br/>
	 * 5.个人在应用管理员中 <br/>
	 * 6.应用的创建人员 <br/>
	 * 7.个人有Manage权限或者ProcessPlatformManager身份
	 */
	@SuppressWarnings("unchecked")
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName(),
				XGsonBuilder.toJson(jsonElement));
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (optional.isPresent()) {
			wos = (List<Wo>) optional.get();
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
				// 去除部门以及上级部门,如果设置了一级部门可用,那么一级部门下属的二级部门也可用
				List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
				List<String> roles = business.organization().role().listWithPerson(effectivePerson);
				List<String> ids = listFromApplication(business, effectivePerson, roles, identities, units);
				if (ListTools.isNotEmpty(wi.getApplicationList())) {
					ids = ListUtils.intersection(business.application().pick(wi.getApplicationList()).stream()
							.map(Application::getId).collect(Collectors.toList()), ids);
				}
				for (String id : ids) {
					Application o = business.application().pick(id);
					if (null != o) {
						wos.add(Wo.copier.copy(o));
					}
				}
				wos = business.application().sort(wos);
				CacheManager.put(cacheCategory, cacheKey, wos);
			}
		}
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.application.ActionListWithPerson$Wo")
	public static class Wo extends Application {

		private static final long serialVersionUID = -4862564047240738097L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

	private List<String> listFromApplication(Business business, EffectivePerson effectivePerson, List<String> roles,
			List<String> identities, List<String> units) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Application.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Application> root = cq.from(Application.class);
		if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
				OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
			Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
					cb.isEmpty(root.get(Application_.availableUnitList)));
			p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)));
			// p = cb.or(p, cb.equal(root.get(Application_.creatorPerson),
			// effectivePerson.getDistinguishedName()));
			if (ListTools.isNotEmpty(identities)) {
				p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
			}
			if (ListTools.isNotEmpty(units)) {
				p = cb.or(p, root.get(Application_.availableUnitList).in(units));
			}
			cq.where(p);
		}
		return em.createQuery(cq.select(root.get(Application_.id))).getResultList().stream().distinct()
				.collect(Collectors.toList());
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 4212513683929130715L;

		@FieldDescribe("应用列表")
		private List<String> applicationList = new ArrayList<>();

		public List<String> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<String> applicationList) {
			this.applicationList = applicationList;
		}

	}
}