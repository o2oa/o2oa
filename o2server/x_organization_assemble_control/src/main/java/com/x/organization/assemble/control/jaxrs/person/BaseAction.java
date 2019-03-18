package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.Scripting;
import com.x.base.core.project.scripting.ScriptingEngine;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

abstract class BaseAction extends StandardJaxrsAction {

	protected static List<String> person_fieldsInvisible = ListTools.toList(JpaObject.FieldsInvisible,
			Person.password_FIELDNAME, Person.icon_FIELDNAME);

	public static class WoPersonAbstract extends Person {
		private static final long serialVersionUID = -8698017750369215370L;

		@FieldDescribe("对个人的操作权限")
		private Control control = new Control();

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}
	}

	public static class Control extends GsonPropertyObject {

		private Boolean allowEdit = false;
		private Boolean allowDelete = false;

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowDelete() {
			return allowDelete;
		}

		public void setAllowDelete(Boolean allowDelete) {
			this.allowDelete = allowDelete;
		}

	}

	protected <T extends WoPersonAbstract> void updateControl(EffectivePerson effectivePerson, Business business,
			List<T> list) throws Exception {
		if (effectivePerson.isManager()
				|| business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager)) {
			for (T t : list) {
				t.getControl().setAllowDelete(true);
				t.getControl().setAllowEdit(true);
			}
		}
	}

	protected <T extends WoPersonAbstract> void updateControl(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		if (effectivePerson.isManager()
				|| business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager)) {
			t.getControl().setAllowDelete(true);
			t.getControl().setAllowEdit(true);
		} else {
			boolean allowEdit = false;
			boolean allowDelete = false;
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null != person && t.getControllerList().contains(person.getId())) {
				List<Identity> identities = this.listIdentity(business, t);
				List<Unit> units = this.listUnit(business, identities);
				if (ListTools.isNotEmpty(units)) {
					allowEdit = false;
					allowDelete = true;
					for (Unit o : units) {
						if (o.getControllerList().contains(person.getId())
								|| o.getInheritedControllerList().contains(person.getId())) {
							allowEdit = true;
						} else {
							allowDelete = false;
						}
					}
				} else {
					allowEdit = true;
					allowDelete = true;
				}
			}
			t.getControl().setAllowEdit(allowEdit);
			t.getControl().setAllowDelete(allowDelete);
		}
	}

	private List<Identity> listIdentity(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Unit> listUnit(Business business, List<Identity> identities) throws Exception {
		List<String> ids = ListTools.extractProperty(identities, "unit", String.class, true, true);
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = root.get(Unit_.id).in(ids);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	protected void checkName(Business business, String name, String excludeId) throws Exception {
		if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name)) || Config.token().isInitialManager(name)) {
			throw new ExceptionInvalidName(name);
		}
	}

	protected void checkMobile(Business business, String mobile, String excludeId) throws Exception {
		if (!Config.person().isMobile(mobile)) {
			throw new ExceptionInvalidMobile(mobile);
		}
		if (StringUtils.isNotEmpty(business.person().getWithMobile(mobile, excludeId))) {
			throw new ExceptionMobileDuplicate(mobile, "手机号");
		}
	}

	protected void checkEmployee(Business business, String employee, String excludeId) throws Exception {
		if (StringUtils.isNotEmpty(business.person().getWithEmployee(employee, excludeId))) {
			throw new ExceptionEmployeeDuplicate(employee, "员工号");
		}
	}

	protected void checkUnique(Business business, String unique, String excludeId) throws Exception {
		if (StringUtils.isNotEmpty(business.person().getWithUnique(unique, excludeId))) {
			throw new ExceptionUniqueDuplicate(unique, "唯一编码");
		}
	}

	protected void checkMail(Business business, String mail, String excludeId) throws Exception {
		if (StringUtils.isNotEmpty(mail)) {
			if (!StringTools.isMail(mail)) {
				throw new ExceptionInvalidMail(mail);
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(mail, excludeId))) {
				throw new ExceptionEmployeeDuplicate(mail, "邮件地址");
			}
		}
	}

	protected String initPassword(Business business, Person person) throws Exception {
		String str = Config.person().getPassword();
		Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			String eval = matcher.group(1);
			ScriptingEngine engine = Scripting.getEngine();
			engine.binding("person", person);
			String pass = engine.evalAsString(eval);
			return pass;
		} else {
			return str;
		}
	}

}