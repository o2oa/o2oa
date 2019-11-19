package com.x.program.center.qiyeweixin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.Unit_;
import com.x.program.center.Business;
import com.x.program.center.qiyeweixin.User.Extattr.Attr;

public class SyncOrganization {

	private static Logger logger = LoggerFactory.getLogger(SyncOrganization.class);

	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private ScriptEngine engine = scriptEngineManager.getEngineByName(Config.SCRIPTING_ENGINE_NAME);
	private Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);

	public PullResult execute(Business business) throws Exception {
		logger.print("开始与企业微信同步人员,方向:拉入.");
		PullResult result = new PullResult();
		String accessToken = Config.qiyeweixin().corpAccessToken();
		List<Unit> units = new ArrayList<>();
		List<Person> people = new ArrayList<>();
		List<PersonAttribute> personAttributes = new ArrayList<>();
		List<Identity> identities = new ArrayList<>();
		QiyeweixinFactory factory = new QiyeweixinFactory(accessToken);
		for (Department root : factory.roots()) {
			this.check(business, result, units, people, personAttributes, identities, accessToken, factory, null, root);
		}
		this.clean(business, result, units, people, identities);
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(PersonAttribute.class);
		ApplicationCache.notify(Unit.class);
		ApplicationCache.notify(UnitAttribute.class);
		ApplicationCache.notify(UnitDuty.class);
		ApplicationCache.notify(Identity.class);
		result.end();
		if (!result.getCreateUnitList().isEmpty()) {
			logger.print("创建组织({}):{}.", result.getCreateUnitList().size(),
					StringUtils.join(result.getCreateUnitList(), ","));
		}
		if (!result.getUpdateUnitList().isEmpty()) {
			logger.print("修改组织({}):{}.", result.getUpdateUnitList().size(),
					StringUtils.join(result.getUpdateUnitList(), ","));
		}
		if (!result.getRemoveUnitList().isEmpty()) {
			logger.print("删除组织({}):{}.", result.getRemoveUnitList().size(),
					StringUtils.join(result.getRemoveUnitList(), ","));
		}
		if (!result.getCreatePersonList().isEmpty()) {
			logger.print("创建个人({}):{}.", result.getCreatePersonList().size(),
					StringUtils.join(result.getCreatePersonList(), ","));
		}
		if (!result.getUpdatePersonList().isEmpty()) {
			logger.print("修改个人({}):{}.", result.getUpdatePersonList().size(),
					StringUtils.join(result.getUpdatePersonList(), ","));
		}
		if (!result.getRemovePersonList().isEmpty()) {
			logger.print("删除个人({}):{}.", result.getRemovePersonList().size(),
					StringUtils.join(result.getRemovePersonList(), ","));
		}
		if (!result.getCreateIdentityList().isEmpty()) {
			logger.print("创建身份({}):{}.", result.getCreateIdentityList().size(),
					StringUtils.join(result.getCreateIdentityList(), ","));
		}
		if (!result.getUpdateIdentityList().isEmpty()) {
			logger.print("修改身份({}):{}.", result.getUpdateIdentityList().size(),
					StringUtils.join(result.getUpdateIdentityList(), ","));
		}
		if (!result.getRemoveIdentityList().isEmpty()) {
			logger.print("删除身份({}):{}.", result.getRemoveIdentityList().size(),
					StringUtils.join(result.getRemoveIdentityList(), ","));
		}
		logger.print("从企业微信同步人员结束.");
		return result;
	}

	private void check(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<PersonAttribute> personAttributes, List<Identity> identities, String accessToken,
			QiyeweixinFactory factory, Unit sup, Department org) throws Exception {
		Unit unit = this.checkUnit(business, result, sup, org);
		units.add(unit);
		for (User o : factory.listUser(org)) {
			Person person = this.checkPerson(business, result, accessToken, o);
			/* 如果人员没有手机号,那么就先跳过这个人 */
			if (null != person) {
				people.add(person);
				Identity identity = this.checkIdentity(business, result, person, unit, o);
				identities.add(identity);
				if ((null != o.getExtattr()) && (ListTools.isNotEmpty(o.getExtattr().getAttrs()))) {
					for (Attr attr : o.getExtattr().getAttrs()) {
						PersonAttribute personAttribute = this.checkPersonAttribute(business, result, person, o, attr);
						personAttributes.add(personAttribute);
					}
				}
			}
		}
		for (Department o : factory.listSub(org)) {
			this.check(business, result, units, people, personAttributes, identities, accessToken, factory, unit, o);
		}
	}

	private Unit checkUnit(Business business, PullResult result, Unit sup, Department org) throws Exception {
		Unit unit = business.unit().getWithQiyeweixinIdObject(Objects.toString(org.getId()));
		if (null != unit) {
			if ((null == sup) && (StringUtils.isNotEmpty(unit.getSuperior()))) {
				/* 不是一个顶层组织所以只能删除重建 */
				removeUnit(business, result, unit);
				unit = null;
			}
			if ((null != sup) && (!StringUtils.equals(sup.getId(), unit.getSuperior()))) {
				/* 指定的上级部门和预期不符 */
				removeUnit(business, result, unit);
				unit = null;
			}
		}
		if (null == unit) {
			unit = this.createUnit(business, result, sup, org);
		} else {
			if (!StringUtils.equals(unit.getQiyeweixinHash(), DigestUtils.sha256Hex(XGsonBuilder.toJson(org)))) {
				unit = this.updateUnit(business, result, unit, org);
			}
		}
		return unit;
	}

	private Unit createUnit(Business business, PullResult result, Unit sup, Department org) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Unit unit = new Unit();
		emc.beginTransaction(Unit.class);
		unit.setName(org.getName());
		unit.setUnique(org.getId().toString());
		unit.setQiyeweixinId(org.getId().toString());
		unit.setQiyeweixinHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(org)));
		if (null != sup) {
			unit.setSuperior(sup.getId());
		}
		if (null != org.getOrder()) {
			unit.setOrderNumber(org.getOrder().intValue());
		}
		business.unit().adjustInherit(unit);
		emc.persist(unit);
		emc.commit();
		result.getCreateUnitList().add(unit.getDistinguishedName());
		return unit;
	}

	private Unit updateUnit(Business business, PullResult result, Unit unit, Department department) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Unit.class);
		unit.setQiyeweixinHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(department)));
		unit.setName(department.getName());
		emc.check(unit, CheckPersistType.all);
		emc.commit();
		result.getUpdateUnitList().add(unit.getDistinguishedName());
		return unit;
	}

	private void removeUnit(Business business, PullResult result, Unit unit) throws Exception {
		logger.print("正在删除组织{}.", unit.getDistinguishedName());
		List<Unit> os = new ArrayList<>();
		os.add(unit);
		os.addAll(business.unit().listSupNestedObject(unit));
		os = os.stream().sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		for (Unit o : os) {
			this.removeSingleUnit(business, result, o);
		}
	}

	private void removeSingleUnit(Business business, PullResult result, Unit unit) throws Exception {
		logger.print("正在删除单个组织{}.", unit.getDistinguishedName());
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(UnitAttribute.class);
		emc.beginTransaction(UnitDuty.class);
		emc.beginTransaction(Identity.class);
		emc.beginTransaction(Unit.class);
		for (UnitAttribute o : emc.listEqual(UnitAttribute.class, UnitAttribute.unit_FIELDNAME, unit.getId())) {
			emc.remove(o, CheckRemoveType.all);
			result.getRemoveUnitAttributeList().add(o.getDistinguishedName());
		}
		for (UnitDuty o : emc.listEqual(UnitDuty.class, UnitDuty.unit_FIELDNAME, unit.getId())) {
			emc.remove(o, CheckRemoveType.all);
			result.getRemoveUnitDutyList().add(o.getDistinguishedName());
		}
		for (Identity o : emc.listEqual(Identity.class, Identity.unit_FIELDNAME, unit.getId())) {
			emc.remove(o, CheckRemoveType.all);
			result.getRemoveIdentityList().add(o.getDistinguishedName());
		}
		emc.remove(unit, CheckRemoveType.all);
		emc.commit();
		result.getRemoveUnitList().add(unit.getDistinguishedName());
	}

	private Person checkPerson(Business business, PullResult result, String accessToken, User user) throws Exception {
		Person person = business.person().getWithQiyeweixinIdObject(user.getUserid());
		if (null == person) {
			if ((StringUtils.isNotEmpty(user.getMobile())) && StringUtils.isNotEmpty(user.getName())) {
				person = this.createOrLinkPerson(business, result, user);
			}
		} else {
			if (!StringUtils.equals(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)), person.getQiyeweixinHash())) {
				person = this.updatePerson(business, result, person, user);
			}
		}
		return person;
	}

	private Person createOrLinkPerson(Business business, PullResult result, User user) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Person.class);
		Person person = emc.flag(user.getMobile(), Person.class);
		if (null != person) {
			person.setQiyeweixinId(Objects.toString(user.getUserid()));
			person.setQiyeweixinHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setUnique(user.getUserid());
			person.setMail(user.getEmail());
			person.setOfficePhone(user.getTelephone());
			person.setGenderType(Objects.equals("1", user.getGender()) ? GenderType.m : GenderType.f);
			emc.check(person, CheckPersistType.all);
			result.getUpdatePersonList().add(person.getDistinguishedName());
		} else {
			person = new Person();
			person.setQiyeweixinId(Objects.toString(user.getUserid()));
			person.setQiyeweixinHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setUnique(user.getUserid());
			// person.setEmployee(user.getJobNumber());
			person.setMail(user.getEmail());
			person.setOfficePhone(user.getTelephone());
			person.setGenderType(Objects.equals("1", user.getGender()) ? GenderType.m : GenderType.f);
			/* 新增人员需要增加密码 */
			business.person().setPassword(person, this.getPassword(engine, pattern, person));
			emc.persist(person, CheckPersistType.all);
			result.getCreatePersonList().add(person.getDistinguishedName());
		}
		emc.commit();
		return person;
	}

	private String getPassword(ScriptEngine engine, Pattern pattern, Person person) throws Exception {
		String str = Config.person().getPassword();
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			String eval = matcher.group(1);
			engine.put("person", person);
			String pass = engine.eval(eval).toString();
			return pass;
		} else {
			return str;
		}
	}

	private Person updatePerson(Business business, PullResult result, Person person, User user) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Person.class);
		person.setQiyeweixinHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
		// person.setEmployee(user.getJobNumber());
		person.setName(user.getName());
		person.setMobile(user.getMobile());
		person.setMail(user.getEmail());
		person.setOfficePhone(user.getTelephone());
		emc.check(person, CheckPersistType.all);
		emc.commit();
		result.getUpdatePersonList().add(person.getDistinguishedName());
		return person;
	}

	private PersonAttribute checkPersonAttribute(Business business, PullResult result, Person person, User user,
			Attr attr) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(PersonAttribute_.name), attr.getName()));
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		PersonAttribute personAttribute = null;
		if (os.size() == 0) {
			personAttribute = this.createPersonAttribute(business, result, person, attr);
		} else {
			personAttribute = os.get(0);
			if (!StringUtils.equals(personAttribute.getAttributeList().get(0), attr.getValue())) {
				personAttribute = this.updatePersonAttribute(business, result, personAttribute, attr);
			}
		}
		return personAttribute;
	}

	private PersonAttribute createPersonAttribute(Business business, PullResult result, Person person, Attr attr)
			throws Exception {
		business.entityManagerContainer().beginTransaction(PersonAttribute.class);
		PersonAttribute personAttribute = new PersonAttribute();
		personAttribute.setPerson(person.getId());
		personAttribute.setName(attr.getName());
		personAttribute.setAttributeList(ListTools.toList(attr.getValue()));
		business.entityManagerContainer().persist(personAttribute, CheckPersistType.all);
		business.entityManagerContainer().commit();
		result.getCreatePersonAttributeList().add(personAttribute.getDistinguishedName());
		return personAttribute;
	}

	private PersonAttribute updatePersonAttribute(Business business, PullResult result, PersonAttribute personAttribute,
			Attr attr) throws Exception {
		business.entityManagerContainer().beginTransaction(PersonAttribute.class);
		personAttribute.setAttributeList(ListTools.toList(attr.getValue()));
		business.entityManagerContainer().commit();
		result.getUpdatePersonAttributeList().add(personAttribute.getDistinguishedName());
		return personAttribute;
	}

	private void removePerson(Business business, PullResult result, Person person) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Person.class);
		emc.beginTransaction(PersonAttribute.class);
		for (PersonAttribute o : emc.listEqual(PersonAttribute.class, PersonAttribute.person_FIELDNAME,
				person.getId())) {
			result.getRemovePersonAttributeList().add(o.getDistinguishedName());
			emc.remove(o, CheckRemoveType.all);
		}
		emc.remove(person, CheckRemoveType.all);
		emc.commit();
		result.getRemovePersonList().add(person.getDistinguishedName());
	}

	private Identity checkIdentity(Business business, PullResult result, Person person, Unit unit, User user)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(Identity_.unit), unit.getId()));
		List<Identity> os = em.createQuery(cq.select(root).where(p).distinct(true)).setMaxResults(1).getResultList();
		Identity identity = null;
		Long order = null;
		if (ListTools.isNotEmpty(user.getOrder())) {
			order = user.getOrder().get(user.getDepartment().indexOf(Long.parseLong(unit.getQiyeweixinId(), 10)));
		}
		if (os.size() == 0) {
			identity = this.createIdentity(business, result, person, unit, user, order);
		} else {
			identity = os.get(0);
			identity = this.updateIdentity(business, result, unit, identity, user, order);
		}
		return identity;
	}

	private Identity createIdentity(Business business, PullResult result, Person person, Unit unit, User user,
			Long order) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Identity.class);
		Identity identity = new Identity();
		identity.setName(person.getName());
		identity.setPerson(person.getId());
		identity.setUnit(unit.getId());
		identity.setUnitLevel(unit.getLevel());
		identity.setUnitLevelName(unit.getLevelName());
		identity.setUnitName(unit.getName());
		if (order != null) {
			identity.setOrderNumber(order.intValue());
		}
		emc.persist(identity, CheckPersistType.all);
		emc.commit();
		result.getCreateIdentityList().add(identity.getDistinguishedName());
		return identity;
	}

	private Identity updateIdentity(Business business, PullResult result, Unit unit, Identity identity, User user,
			Long order) throws Exception {
		if (null != order) {
			if (!StringUtils.equals(Objects.toString(identity.getOrderNumber(), ""), Objects.toString(order, ""))) {
				EntityManagerContainer emc = business.entityManagerContainer();
				emc.beginTransaction(Identity.class);
				if (order != null) {
					identity.setOrderNumber(order.intValue());
				}
				emc.commit();
				result.getUpdateIdentityList().add(identity.getDistinguishedName());
			}
		}
		return identity;
	}

	private void clean(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<Identity> identities) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Identity> allIdentities = this.listIdentity(business);
		/* 删除身份 */
		for (Identity identity : ListUtils.subtract(allIdentities, identities)) {
			Person person = emc.find(identity.getPerson(), Person.class);
			if (null == person || StringUtils.isNotEmpty(person.getQiyeweixinId())) {
				List<UnitDuty> uds = emc.listIsMember(UnitDuty.class, UnitDuty.identityList_FIELDNAME,
						identity.getId());
				if (ListTools.isNotEmpty(uds)) {
					emc.beginTransaction(UnitDuty.class);
					uds.stream().forEach(o -> {
						o.getIdentityList().remove(identity.getId());
					});
					emc.commit();
				}
				emc.beginTransaction(Identity.class);
				emc.remove(identity, CheckRemoveType.all);
				emc.commit();
			}
		}
		/* 组织单独方法删除 */
		List<Unit> allUnit = this.listUnit(business);
		List<Unit> removeUnits = ListUtils.subtract(allUnit, units).stream()
				.sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		for (Unit unit : removeUnits) {
			this.removeSingleUnit(business, result, unit);
		}
		List<Person> allPeople = this.listPerson(business);
		/* 删除个人 */
		for (Person person : ListUtils.subtract(allPeople, people)) {
			this.removePerson(business, result, person);
		}
	}

	private List<Unit> listUnit(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.notEqual(root.get(Unit_.qiyeweixinId), "");
		p = cb.and(p, cb.isNotNull(root.get(Unit_.qiyeweixinId)));
		List<Unit> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		return os;
	}

	private List<Person> listPerson(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.notEqual(root.get(Person_.qiyeweixinId), "");
		p = cb.and(p, cb.isNotNull(root.get(Person_.qiyeweixinId)));
		List<Person> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		return os;
	}

	private List<Identity> listIdentity(Business business) throws Exception {
		return business.entityManagerContainer().listAll(Identity.class);
	}

	public static class PullResult extends GsonPropertyObject {

		private Date start = new Date();

		private Date end;

		private Long elapsed;

		private List<String> createUnitList = new ArrayList<>();
		private List<String> updateUnitList = new ArrayList<>();
		private List<String> removeUnitList = new ArrayList<>();

		private List<String> createPersonList = new ArrayList<>();
		private List<String> updatePersonList = new ArrayList<>();
		private List<String> removePersonList = new ArrayList<>();

		private List<String> createIdentityList = new ArrayList<>();
		private List<String> updateIdentityList = new ArrayList<>();
		private List<String> removeIdentityList = new ArrayList<>();

		private List<String> createPersonAttributeList = new ArrayList<>();
		private List<String> updatePersonAttributeList = new ArrayList<>();
		private List<String> removePersonAttributeList = new ArrayList<>();

		private List<String> removeUnitDutyList = new ArrayList<>();
		private List<String> removeUnitAttributeList = new ArrayList<>();

		public void end() {
			this.end = new Date();
			this.elapsed = end.getTime() - start.getTime();
		}

		public Date getStart() {
			return start;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public Date getEnd() {
			return end;
		}

		public void setEnd(Date end) {
			this.end = end;
		}

		public List<String> getCreateUnitList() {
			return createUnitList;
		}

		public void setCreateUnitList(List<String> createUnitList) {
			this.createUnitList = createUnitList;
		}

		public List<String> getUpdateUnitList() {
			return updateUnitList;
		}

		public void setUpdateUnitList(List<String> updateUnitList) {
			this.updateUnitList = updateUnitList;
		}

		public List<String> getCreatePersonList() {
			return createPersonList;
		}

		public void setCreatePersonList(List<String> createPersonList) {
			this.createPersonList = createPersonList;
		}

		public List<String> getUpdatePersonList() {
			return updatePersonList;
		}

		public void setUpdatePersonList(List<String> updatePersonList) {
			this.updatePersonList = updatePersonList;
		}

		public List<String> getCreateIdentityList() {
			return createIdentityList;
		}

		public void setCreateIdentityList(List<String> createIdentityList) {
			this.createIdentityList = createIdentityList;
		}

		public List<String> getRemoveUnitList() {
			return removeUnitList;
		}

		public void setRemoveUnitList(List<String> removeUnitList) {
			this.removeUnitList = removeUnitList;
		}

		public List<String> getRemovePersonList() {
			return removePersonList;
		}

		public void setRemovePersonList(List<String> removePersonList) {
			this.removePersonList = removePersonList;
		}

		public List<String> getRemoveIdentityList() {
			return removeIdentityList;
		}

		public void setRemoveIdentityList(List<String> removeIdentityList) {
			this.removeIdentityList = removeIdentityList;
		}

		public List<String> getRemoveUnitDutyList() {
			return removeUnitDutyList;
		}

		public void setRemoveUnitDutyList(List<String> removeUnitDutyList) {
			this.removeUnitDutyList = removeUnitDutyList;
		}

		public List<String> getRemoveUnitAttributeList() {
			return removeUnitAttributeList;
		}

		public void setRemoveUnitAttributeList(List<String> removeUnitAttributeList) {
			this.removeUnitAttributeList = removeUnitAttributeList;
		}

		public List<String> getRemovePersonAttributeList() {
			return removePersonAttributeList;
		}

		public void setRemovePersonAttributeList(List<String> removePersonAttributeList) {
			this.removePersonAttributeList = removePersonAttributeList;
		}

		public Long getElapsed() {
			return elapsed;
		}

		public void setElapsed(Long elapsed) {
			this.elapsed = elapsed;
		}

		public List<String> getUpdateIdentityList() {
			return updateIdentityList;
		}

		public void setUpdateIdentityList(List<String> updateIdentityList) {
			this.updateIdentityList = updateIdentityList;
		}

		public List<String> getCreatePersonAttributeList() {
			return createPersonAttributeList;
		}

		public void setCreatePersonAttributeList(List<String> createPersonAttributeList) {
			this.createPersonAttributeList = createPersonAttributeList;
		}

		public List<String> getUpdatePersonAttributeList() {
			return updatePersonAttributeList;
		}

		public void setUpdatePersonAttributeList(List<String> updatePersonAttributeList) {
			this.updatePersonAttributeList = updatePersonAttributeList;
		}
	}
}
