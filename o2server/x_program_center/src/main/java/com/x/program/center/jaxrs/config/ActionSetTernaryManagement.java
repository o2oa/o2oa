package com.x.program.center.jaxrs.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

/**
 * 三元管理配置
 * 
 * @author sword
 */
public class ActionSetTernaryManagement extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		if (StringUtils.isNotBlank(wi.getSystemManagerPassword())) {
			if (!wi.getSystemManagerPassword().matches(Config.person().getPasswordRegex())) {
				throw new ExceptionInvalidPassword(TernaryManagement.INIT_SYSTEM_MANAGER_NAME,
						Config.person().getPasswordRegexHint());
			} else {
				Config.ternaryManagement().setSystemManagerPassword(wi.getSystemManagerPassword());
			}
		}
		if (StringUtils.isNotBlank(wi.getSecurityManagerPassword())) {
			if (!wi.getSecurityManagerPassword().matches(Config.person().getPasswordRegex())) {
				throw new ExceptionInvalidPassword(TernaryManagement.INIT_SECURITY_MANAGER_NAME,
						Config.person().getPasswordRegexHint());
			} else {
				Config.ternaryManagement().setSecurityManagerPassword(wi.getSecurityManagerPassword());
			}
		}
		if (StringUtils.isNotBlank(wi.getAuditManagerPassword())) {
			if (!wi.getAuditManagerPassword().matches(Config.person().getPasswordRegex())) {
				throw new ExceptionInvalidPassword(TernaryManagement.INIT_AUDIT_MANAGER_NAME,
						Config.person().getPasswordRegexHint());
			} else {
				Config.ternaryManagement().setAuditManagerPassword(wi.getAuditManagerPassword());
			}
		}
		if (wi.getEnable() != null) {
			Config.ternaryManagement().setEnable(wi.getEnable());
		}
		Config.ternaryManagement().save();
		this.configFlush(effectivePerson);
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

//	private void deleteDefaultRole() throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			List<String> roles = ListTools.toList(OrganizationDefinition.SystemManager,
//					OrganizationDefinition.SecurityManager, OrganizationDefinition.AuditManager);
//			roles = roles.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
//			for (String str : roles) {
//				List<Role> list = emc.listEqual(Role.class, Role.name_FIELDNAME, str);
//				if (ListTools.isNotEmpty(list)) {
//					emc.beginTransaction(Role.class);
//					emc.remove(list.get(0));
//					emc.commit();
//				}
//			}
//		}
//	}
//
//	private void saveDefaultRole() throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			List<String> roles = ListTools.toList(OrganizationDefinition.SystemManager,
//					OrganizationDefinition.SecurityManager, OrganizationDefinition.AuditManager);
//			roles = roles.stream().sorted(Comparator.comparing(String::toString)).collect(Collectors.toList());
//			for (String str : roles) {
//				EntityManager em = emc.get(Role.class);
//				CriteriaBuilder cb = em.getCriteriaBuilder();
//				CriteriaQuery<String> cq = cb.createQuery(String.class);
//				Root<Role> root = cq.from(Role.class);
//				Predicate p = cb.equal(root.get(Role_.name), str);
//				cq.select(root.get(Role_.id)).where(p);
//				List<String> list = em.createQuery(cq).setMaxResults(1).getResultList();
//				if (list.isEmpty()) {
//					Role o = new Role();
//					o.setName(str);
//					o.setUnique(str + OrganizationDefinition.RoleDefinitionSuffix);
//					o.setDescription(getDescriptionWithName(str));
//					emc.beginTransaction(Role.class);
//					emc.persist(o, CheckPersistType.all);
//					emc.commit();
//				}
//			}
//		}
//	}

//	private String getDescriptionWithName(String str) {
//		if (OrganizationDefinition.SystemManager.equalsIgnoreCase(str)) {
//			return OrganizationDefinition.SystemManager_description;
//		} else if (OrganizationDefinition.SecurityManager.equalsIgnoreCase(str)) {
//			return OrganizationDefinition.SecurityManager_description;
//		} else if (OrganizationDefinition.AuditManager.equalsIgnoreCase(str)) {
//			return OrganizationDefinition.AuditManager_description;
//		}
//		return "";
//	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5015449132270026780L;

		@FieldDescribe("是否启用三元管理.")
		private Boolean enable;

		@FieldDescribe("系统管理员账号密码.")
		private String systemManagerPassword;

		@FieldDescribe("安全管理员账号密码.")
		private String securityManagerPassword;

		@FieldDescribe("安全审计员账号密码.")
		private String auditManagerPassword;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getSystemManagerPassword() {
			return systemManagerPassword;
		}

		public void setSystemManagerPassword(String systemManagerPassword) {
			this.systemManagerPassword = systemManagerPassword;
		}

		public String getSecurityManagerPassword() {
			return securityManagerPassword;
		}

		public void setSecurityManagerPassword(String securityManagerPassword) {
			this.securityManagerPassword = securityManagerPassword;
		}

		public String getAuditManagerPassword() {
			return auditManagerPassword;
		}

		public void setAuditManagerPassword(String auditManagerPassword) {
			this.auditManagerPassword = auditManagerPassword;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -3840239695167638103L;

	}

}
