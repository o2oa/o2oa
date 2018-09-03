package com.x.program.center.dingding;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.Unit_;
import com.x.program.center.Business;

public class PullSyncOrganizationDingding {

	private static Logger logger = LoggerFactory.getLogger(PullSyncOrganizationDingding.class);

	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	private ScriptEngine engine = scriptEngineManager.getEngineByName("nashorn");
	private Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.RegularExpression_Script);

	public PullResult execute(Business business) throws Exception {
		logger.print("开始与钉钉同步人员,方向:拉入.");
		PullResult result = new PullResult();
		String accessToken = Config.dingding().corp_access_token();
		List<Unit> units = new ArrayList<>();
		List<Person> people = new ArrayList<>();
		List<Identity> identities = new ArrayList<>();
		DepartmentFactory departmentFactory = new DepartmentFactory(
				DingdingHelper.listDepartment(accessToken));
		Department root = departmentFactory.root();
		this.check(business, result, units, people, identities, accessToken, departmentFactory, null, root);
		this.clean(business, result, units, people, identities);
		result.setEnd(new Date());
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(PersonAttribute.class);
		ApplicationCache.notify(Unit.class);
		ApplicationCache.notify(UnitAttribute.class);
		ApplicationCache.notify(UnitDuty.class);
		ApplicationCache.notify(Identity.class);
		logger.print("从钉钉同步人员结束,结果:{}.", result);
		return result;
	}

	private void check(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<Identity> identities, String accessToken, DepartmentFactory departmentFactory, Unit sup,
			Department department) throws Exception {
		Unit unit = this.checkUnit(business, result, sup, department);
		units.add(unit);
		for (User o : DingdingHelper.listDepartmentUser(accessToken, department)) {
			Person person = this.checkPerson(business, result, accessToken, unit, o);
			/* 如果人员没有手机号,那么就先跳过这个人 */
			if (null != person) {
				people.add(person);
				Identity identity = this.checkIdentity(business, result, person, unit);
				identities.add(identity);
			}
		}
		for (Department o : departmentFactory.listSub(department)) {
			this.check(business, result, units, people, identities, accessToken, departmentFactory, unit, o);
		}
	}

	private Unit checkUnit(Business business, PullResult result, Unit sup, Department department) throws Exception {
		Unit unit = business.unit().getWithDingdingIdObject(department.getId().toString());
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
			unit = this.createUnit(business, result, sup, department);
		} else {
			if (!StringUtils.equals(unit.getDingdingHash(), DigestUtils.sha256Hex(XGsonBuilder.toJson(department)))) {
				unit = this.updateUnit(business, result, unit, department);
			}
		}
		return unit;
	}

	private Unit createUnit(Business business, PullResult result, Unit sup, Department department) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Unit unit = new Unit();
		emc.beginTransaction(Unit.class);
		unit.setName(department.getName());
		unit.setDingdingId(department.getId().toString());
		unit.setDingdingHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(department)));
		if (null != sup) {
			unit.setSuperior(sup.getId());
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
		unit.setDingdingHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(department)));
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
			result.getRemoveUnitAttributeList().add(o.getDistinguishedName());
			emc.remove(o, CheckRemoveType.all);
		}
		for (UnitDuty o : emc.listEqual(UnitDuty.class, UnitDuty.unit_FIELDNAME, unit.getId())) {
			result.getRemoveUnitDutyList().add(o.getDistinguishedName());
			emc.remove(o, CheckRemoveType.all);
		}
		for (Identity o : emc.listEqual(Identity.class, Identity.unit_FIELDNAME, unit.getId())) {
			result.getRemoveIdentityList().add(o.getDistinguishedName());
			emc.remove(o, CheckRemoveType.all);
		}
		result.getRemoveUnitList().add(unit.getDistinguishedName());
		emc.remove(unit, CheckRemoveType.all);
		emc.commit();
	}

	private Person checkPerson(Business business, PullResult result, String accessToken, Unit unit, User user)
			throws Exception {
		Person person = business.person().getWithDingdingIdObject(user.getUserid());
		if (null == person) {
			if ((StringUtils.isNotEmpty(user.getMobile())) && StringUtils.isNotEmpty(user.getName())) {
				person = this.createOrLinkPerson(business, result, user);
			}
		} else {
			if (!StringUtils.equals(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)), person.getDingdingHash())) {
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
			person.setDingdingId(user.getUserid());
			person.setDingdingHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			if (StringUtils.isNotEmpty(user.getAvatar())) {
				person.setIcon(Base64.encodeBase64String(IOUtils.toByteArray(new URL(user.getAvatar()))));
			} else {
				person.setIcon("");
			}
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setEmployee(user.getJobnumber());
			person.setMail(user.getEmail());
			emc.check(person, CheckPersistType.all);
		} else {
			person = new Person();
			person.setDingdingId(user.getUserid());
			person.setDingdingHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			if (StringUtils.isNotEmpty(user.getAvatar())) {
				person.setIcon(Base64.encodeBase64String(IOUtils.toByteArray(new URL(user.getAvatar()))));
			} else {
				person.setIcon("");
			}
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setEmployee(user.getJobnumber());
			person.setMail(user.getEmail());
			/* 新增人员需要增加密码 */
			business.person().setPassword(person, this.getPassword(engine, pattern, person));
			emc.persist(person, CheckPersistType.all);
		}
		emc.commit();
		result.getCreatePersonList().add(person.getDistinguishedName());
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
		person.setDingdingHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
		if (StringUtils.isNotEmpty(user.getAvatar())) {
			person.setIcon(Base64.encodeBase64String(IOUtils.toByteArray(new URL(user.getAvatar()))));
		} else {
			person.setIcon("");
		}
		person.setName(user.getName());
		person.setMobile(user.getMobile());
		person.setEmployee(user.getJobnumber());
		person.setMail(user.getEmail());
		emc.check(person, CheckPersistType.all);
		emc.commit();
		result.getUpdatePersonList().add(person.getDistinguishedName());
		return person;
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
		result.getRemovePersonList().add(person.getDistinguishedName());
		emc.remove(person, CheckRemoveType.all);
		emc.commit();
	}

	private Identity checkIdentity(Business business, PullResult result, Person person, Unit unit) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(Identity_.unit), unit.getId()));
		List<Identity> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		Identity identity = null;
		if (os.size() == 1) {
			identity = os.get(0);
		} else {
			identity = this.createIdentity(business, result, person, unit);
		}
		return identity;
	}

	private Identity createIdentity(Business business, PullResult result, Person person, Unit unit) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Identity.class);
		Identity identity = new Identity();
		identity.setName(person.getName());
		identity.setPerson(person.getId());
		identity.setUnit(unit.getId());
		identity.setUnitLevel(unit.getLevel());
		identity.setUnitLevelName(unit.getLevelName());
		identity.setUnitName(unit.getName());
		emc.persist(identity, CheckPersistType.all);
		emc.commit();
		result.getCreateIdentityList().add(identity.getDistinguishedName());
		return identity;
	}

	private void clean(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<Identity> identities) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Identity> allIdentities = this.listIdentity(business);
		emc.beginTransaction(Identity.class);
		/* 删除身份 */
		for (Identity identity : ListUtils.subtract(allIdentities, identities)) {
			Person person = emc.find(identity.getPerson(), Person.class);
			if (null == person || StringUtils.isNotEmpty(person.getDingdingId())) {
				emc.remove(identity, CheckRemoveType.all);
			}
		}
		emc.commit();
		/* 组织单独方法删除 */
		List<Unit> allUnit = this.listDingdingUnit(business);
		List<Unit> removeUnits = ListUtils.subtract(allUnit, units).stream()
				.sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		for (Unit unit : removeUnits) {
			this.removeSingleUnit(business, result, unit);
		}
		List<Person> allPeople = this.listDingdingPerson(business);
		/* 删除个人 */
		for (Person person : ListUtils.subtract(allPeople, people)) {
			this.removePerson(business, result, person);
		}
	}

	private List<Unit> listDingdingUnit(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.notEqual(root.get(Unit_.dingdingId), "");
		p = cb.and(p, cb.isNotNull(root.get(Unit_.dingdingId)));
		List<Unit> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
		return os;
	}

	private List<Person> listDingdingPerson(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.notEqual(root.get(Person_.dingdingId), "");
		p = cb.and(p, cb.isNotNull(root.get(Person_.dingdingId)));
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
		private List<String> removeIdentityList = new ArrayList<>();

		private List<String> removeUnitDutyList = new ArrayList<>();
		private List<String> removeUnitAttributeList = new ArrayList<>();
		private List<String> removePersonAttributeList = new ArrayList<>();

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
	}
}
