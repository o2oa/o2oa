package com.x.organization.assemble.control.jaxrs.person;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Custom_;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.PersonExtend;
import com.x.organization.core.entity.PersonExtend_;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import com.x.organization.core.entity.Unit_;

abstract class BaseAction extends StandardJaxrsAction {

	protected static final String PERSON_DELETE_CUSTOM_NAME = "person#delete";

	private static final List<String> KEYWORDS = ListUtils
			.unmodifiableList(Arrays.asList(Token.defaultInitialManager, TernaryManagement.INIT_SYSTEM_MANAGER,
					TernaryManagement.INIT_SECURITY_MANAGER, TernaryManagement.INIT_AUDIT_MANAGER));

	protected boolean editable(Business business, EffectivePerson effectivePerson, String personFlag) throws Exception {
		if (business.hasAnyRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.OrganizationManager)) {
			return true;
		}
		if (StringUtils.isEmpty(personFlag)) {
			return false;
		}
		if (business.hasAnyRole(effectivePerson, OrganizationDefinition.PersonManager)) {
			if (business.sameTopUnit(effectivePerson, personFlag)) {
				return true;
			}
		}
		return false;
	}

	protected boolean editable(Business business, EffectivePerson effectivePerson, Person person) throws Exception {
		if (business.hasAnyRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.OrganizationManager)) {
			return true;
		}
		if (null == person) {
			return false;
		}
		if (business.hasAnyRole(effectivePerson, OrganizationDefinition.PersonManager)) {
			List<String> ids = ListTools.extractProperty(
					business.listTopUnitWithPerson(effectivePerson.getDistinguishedName()), Unit.id_FIELDNAME,
					String.class, true, true);
			return ids.isEmpty() || ListTools.containsAny(ids, person.getTopUnitList());
		}
		return false;
	}

	protected static List<String> person_fieldsInvisible = ListTools.toList(JpaObject.FieldsInvisible,
			Person.password_FIELDNAME, Person.icon_FIELDNAME);

	protected <T extends WoPersonAbstract> void hide(EffectivePerson effectivePerson, Business business, List<T> list)
			throws Exception {
		if (!effectivePerson.isManager() && (!effectivePerson.isCipher())) {
			if (!business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager,
					OrganizationDefinition.PersonManager)) {
				for (WoPersonAbstract o : list) {
					if (BooleanUtils.isTrue(o.getHiddenMobile()) && (!StringUtils
							.equals(effectivePerson.getDistinguishedName(), o.getDistinguishedName()))) {
						o.setMobile(Person.HIDDENMOBILESYMBOL);
					}
				}
			}
		}
	}

	protected <T extends WoPersonAbstract> void hide(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		if (!effectivePerson.isManager() && (!effectivePerson.isCipher())) {
			if (!business.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager,
					OrganizationDefinition.PersonManager)) {
				if (BooleanUtils.isTrue(t.getHiddenMobile())
						&& (!StringUtils.equals(effectivePerson.getDistinguishedName(), t.getDistinguishedName()))) {
					t.setMobile(Person.HIDDENMOBILESYMBOL);
				}
			}
		}
	}

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

		private static final long serialVersionUID = -7663080651519557860L;

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
		if (effectivePerson.isManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager)) {
			for (T t : list) {
				t.getControl().setAllowDelete(true);
				t.getControl().setAllowEdit(true);
			}
		}
	}

	protected <T extends WoPersonAbstract> void updateControl(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		if (effectivePerson.isManager() || business.hasAnyRole(effectivePerson,
				OrganizationDefinition.OrganizationManager, OrganizationDefinition.PersonManager)) {
			t.getControl().setAllowDelete(true);
			t.getControl().setAllowEdit(true);
		} else {
			boolean allowEdit = false;
			boolean allowDelete = false;
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			if (null != person && t.getControllerList().contains(person.getId())) {
				List<Identity> identities = this.listIdentity(business, t);
				List<Unit> units = this.listUnit(business, identities);
				List<Unit> supUnits = new ArrayList<>();
				supUnits.addAll(units);
				for (Unit u : units) {
					supUnits.addAll(business.unit().listSupNestedObject(u));
				}
				if (ListTools.isNotEmpty(supUnits)) {
					allowEdit = false;
					allowDelete = true;
					for (Unit o : supUnits) {
						if (o.getControllerList().contains(person.getId())) {
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

	protected List<Identity> listIdentity(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

	protected List<Unit> listUnit(Business business, List<Identity> identities) throws Exception {
		List<String> ids = ListTools.extractProperty(identities, "unit", String.class, true, true);
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = root.get(Unit_.id).in(ids);
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

	protected void checkName(Business business, String name, String excludeId) throws Exception {
		if (KEYWORDS.contains(name)) {
			throw new ExceptionInitialManagerName();
		}
		if (StringUtils.isEmpty(name) || name.indexOf(OrganizationDefinition.NAME_JOIN_CHAR) > -1
				|| Config.token().isInitialManager(name)) {
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
		if (KEYWORDS.contains(employee)) {
			throw new ExceptionInitialManagerName();
		}
		if (StringUtils.isNotEmpty(business.person().getWithEmployee(employee, excludeId))) {
			throw new ExceptionEmployeeDuplicate(employee, "员工号");
		}
	}

	protected void checkUnique(Business business, String unique, String excludeId) throws Exception {
		if (StringUtils.isBlank(unique)) {
			return;
		}
		if (KEYWORDS.contains(unique)) {
			throw new ExceptionInitialManagerName();
		}
		if (unique.indexOf(OrganizationDefinition.NAME_JOIN_CHAR) > -1) {
			throw new ExceptionInvalidUnique(unique);
		}
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
			Source source = GraalvmScriptingFactory.functionalization(StringEscapeUtils.unescapeJson(matcher.group(1)));
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings();
			bindings.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_PERSON, person);
			Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
			if (opt.isPresent()) {
				str = opt.get();
			}
		}
		return str;
	}

	protected void removeMemberOfUnitDuty(Business business, List<Identity> identities) throws Exception {
		List<String> ids = ListTools.extractProperty(identities, JpaObject.id_FIELDNAME, String.class, true, true);
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = root.get(UnitDuty_.identityList).in(ids);
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
		for (UnitDuty o : os) {
			o.getIdentityList().removeAll(ids);
		}
	}

	protected void removeMemberOfUnitController(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isMember(person.getId(), root.get(Unit_.controllerList));
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
		for (Unit o : os) {
			o.getControllerList().remove(person.getId());
		}
	}

	protected void removeMemberOfPersonController(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.isMember(person.getId(), root.get(Person_.controllerList));
		List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
				.collect(Collectors.toList());
		for (Person o : os) {
			o.getControllerList().remove(person.getId());
		}
	}

	protected void removeMemberOfPersonSuperior(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.equal(root.get(Person_.superior), person.getId());
		List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Person o : os) {
			o.setSuperior("");
		}
	}

	protected void removePersonAttribute(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (PersonAttribute o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	protected void removePersonExtend(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonExtend.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonExtend> cq = cb.createQuery(PersonExtend.class);
		Root<PersonExtend> root = cq.from(PersonExtend.class);
		Predicate p = cb.equal(root.get(PersonExtend_.person), person.getId());
		List<PersonExtend> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (PersonExtend o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	protected void removePersonCustom(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Custom.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Custom> cq = cb.createQuery(Custom.class);
		Root<Custom> root = cq.from(Custom.class);
		Predicate p = cb.equal(root.get(Custom_.person), person.getId());
		p = cb.or(p, cb.equal(root.get(Custom_.person), person.getDistinguishedName()));
		List<Custom> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Custom o : os) {
			business.entityManagerContainer().remove(o, CheckRemoveType.all);
		}
	}

	protected void removeMemberOfGroup(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		Predicate p = cb.isMember(person.getId(), root.get(Group_.personList));
		List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Group o : os) {
			o.getPersonList().remove(person.getId());
		}
	}

	protected void removeMemberOfRole(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		Predicate p = cb.isMember(person.getId(), root.get(Role_.personList));
		List<Role> os = em.createQuery(cq.select(root).where(p)).getResultList();
		for (Role o : os) {
			o.getPersonList().remove(person.getId());
		}
	}

	protected static class CustomPersonInfo extends GsonPropertyObject{
		@FieldDescribe("操作人")
		private String operator;
		@FieldDescribe("操作时间")
		private Date operateTime;
		@FieldDescribe("被操作用户对象")
		private WrapPerson person;
		private List<WrapIdentity> identityList;

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public Date getOperateTime() {
			return operateTime;
		}

		public void setOperateTime(Date operateTime) {
			this.operateTime = operateTime;
		}

		public WrapPerson getPerson() {
			return person;
		}

		public void setPerson(WrapPerson person) {
			this.person = person;
		}

		public List<WrapIdentity> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<WrapIdentity> identityList) {
			this.identityList = identityList;
		}
	}

	public static class WrapPerson extends Person{
		static WrapCopier<Person, WrapPerson> copier = WrapCopierFactory.wo(Person.class, WrapPerson.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
		private List<String> groupList = new ArrayList<>();
		private List<String> roleList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList == null ? new ArrayList<>() : groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getRoleList() {
			return roleList == null ? new ArrayList<>() : roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}

	public static class WrapIdentity extends Identity {
		static WrapCopier<Identity, WrapIdentity> copier = WrapCopierFactory.wo(Identity.class, WrapIdentity.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
		static WrapCopier<WrapIdentity, Identity> copierIn = WrapCopierFactory.wo(WrapIdentity.class, Identity.class, null,
				ListTools.toList(JpaObject.FieldsInvisible));
		private List<String> groupList = new ArrayList<>();
		private List<String> dutyList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList == null ? new ArrayList<>() : groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getDutyList() {
			return dutyList == null ? new ArrayList<>() : dutyList;
		}

		public void setDutyList(List<String> dutyList) {
			this.dutyList = dutyList;
		}
	}

}
