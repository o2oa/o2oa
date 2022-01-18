package com.x.program.center.jaxrs.config;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 三元管理配置
 * @author sword
 */
public class ActionSetTernaryManagement extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Map<String,Object> map = XGsonBuilder.instance().fromJson(jsonElement, Map.class);
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (!Config.nodes().centerServers().first().getValue().getConfigApiEnable()) {
			throw new ExceptionModifyConfig();
		}
		Wi.copier = WrapCopierFactory.wi(Wi.class, TernaryManagement.class, new ArrayList<>(map.keySet()), null);
		Wi.copier.copy(wi, Config.ternaryManagement());
		Config.ternaryManagement().save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void deleteDefaultRole() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> roles = ListTools.toList(OrganizationDefinition.SystemManager, OrganizationDefinition.SecurityManager,
					OrganizationDefinition.AuditManager);
			roles = roles.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
			for (String str : roles) {
				List<Role> list = emc.listEqual(Role.class, Role.name_FIELDNAME, str);
				if (ListTools.isNotEmpty(list)) {
					emc.beginTransaction(Role.class);
					emc.remove(list.get(0));
					emc.commit();
				}
			}
		}
	}

	private void saveDefaultRole() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> roles = ListTools.toList(OrganizationDefinition.SystemManager, OrganizationDefinition.SecurityManager,
					OrganizationDefinition.AuditManager);
			roles = roles.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
			for (String str : roles) {
				EntityManager em = emc.get(Role.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<String> cq = cb.createQuery(String.class);
				Root<Role> root = cq.from(Role.class);
				Predicate p = cb.equal(root.get(Role_.name), str);
				cq.select(root.get(Role_.id)).where(p);
				List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
				if (list.isEmpty()) {
					Role o = new Role();
					o.setName(str);
					o.setUnique(str + OrganizationDefinition.RoleDefinitionSuffix);
					o.setDescription(getDescriptionWithName(str));
					emc.beginTransaction(Role.class);
					emc.persist(o, CheckPersistType.all);
					emc.commit();
				}
			}
		}
	}

	private String getDescriptionWithName(String str) {
		if (OrganizationDefinition.SystemManager.equalsIgnoreCase(str)) {
			return OrganizationDefinition.SystemManager_description;
		} else if (OrganizationDefinition.SecurityManager.equalsIgnoreCase(str)) {
			return OrganizationDefinition.SecurityManager_description;
		} else if (OrganizationDefinition.AuditManager.equalsIgnoreCase(str)) {
			return OrganizationDefinition.AuditManager_description;
		}
		return "";
	}

	public static class Wi extends TernaryManagement {

		static WrapCopier<Wi, TernaryManagement> copier = WrapCopierFactory.wi(Wi.class, TernaryManagement.class, null, null);

	}

	public static class Wo extends WrapBoolean {

	}
}
