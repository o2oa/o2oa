package com.x.program.center.andfx;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import com.x.organization.core.entity.Unit_;
import com.x.program.center.Business;

public class SyncOrganization {

	private static Logger logger = LoggerFactory.getLogger(SyncOrganization.class);
	private static ReentrantLock lock = new ReentrantLock();

	public PullResult execute(Business business) throws Exception {
		PullResult result = new PullResult();
		List<Unit> units = new ArrayList<>();
		List<Person> people = new ArrayList<>();
		List<PersonAttribute> personAttributes = new ArrayList<>();
		List<Identity> identities = new ArrayList<>();
		if (lock.tryLock()) {
			logger.info("移动办公进行人员组织同步.");
			try {
				AndFxFactory factory = new AndFxFactory();
				for (Department root : factory.roots()) {
					this.check(business, result, units, people, personAttributes, identities, factory, null, root);
				}
				this.clean(business, result, units, people, identities);
				CacheManager.notify(Person.class);
				CacheManager.notify(PersonAttribute.class);
				CacheManager.notify(Unit.class);
				CacheManager.notify(UnitAttribute.class);
				CacheManager.notify(UnitDuty.class);
				CacheManager.notify(Identity.class);
				CacheManager.notify(Role.class);
			} catch (Exception e) {
				logger.warn("移动办公进行人员组织同步时出错：{}", e.getMessage());
				logger.error(e);
			} finally {
				lock.unlock();
			}
			logger.info("移动办公人员组织同步结束.");
		}
		result.end();
		if (!result.getCreateUnitList().isEmpty()) {
			logger.info("创建组织({}):{}.", result.getCreateUnitList().size(),
					StringUtils.join(result.getCreateUnitList(), ","));
		}
		if (!result.getUpdateUnitList().isEmpty()) {
			logger.info("修改组织({}):{}.", result.getUpdateUnitList().size(),
					StringUtils.join(result.getUpdateUnitList(), ","));
		}
		if (!result.getRemoveUnitList().isEmpty()) {
			logger.info("删除组织({}):{}.", result.getRemoveUnitList().size(),
					StringUtils.join(result.getRemoveUnitList(), ","));
		}
		if (!result.getCreatePersonList().isEmpty()) {
			logger.info("创建个人({}):{}.", result.getCreatePersonList().size(),
					StringUtils.join(result.getCreatePersonList(), ","));
		}
		if (!result.getUpdatePersonList().isEmpty()) {
			logger.info("修改个人({}):{}.", result.getUpdatePersonList().size(),
					StringUtils.join(result.getUpdatePersonList(), ","));
		}
		if (!result.getRemovePersonList().isEmpty()) {
			logger.info("删除个人({}):{}.", result.getRemovePersonList().size(),
					StringUtils.join(result.getRemovePersonList(), ","));
		}
		if (!result.getCreateIdentityList().isEmpty()) {
			logger.info("创建身份({}):{}.", result.getCreateIdentityList().size(),
					StringUtils.join(result.getCreateIdentityList(), ","));
		}
		if (!result.getUpdateIdentityList().isEmpty()) {
			logger.info("修改身份({}):{}.", result.getUpdateIdentityList().size(),
					StringUtils.join(result.getUpdateIdentityList(), ","));
		}
		if (!result.getRemoveIdentityList().isEmpty()) {
			logger.info("删除身份({}):{}.", result.getRemoveIdentityList().size(),
					StringUtils.join(result.getRemoveIdentityList(), ","));
		}
		return result;
	}

	private void check(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<PersonAttribute> personAttributes, List<Identity> identities, AndFxFactory factory, Unit sup,
			Department org) throws Exception {
		Unit unit = this.checkUnit(business, result, sup, org);
		units.add(unit);
		for (User o : factory.listUser(org)) {
			Person person = this.checkPerson(business, result, o);
			/* 如果人员没有手机号,那么就先跳过这个人 */
			if (null != person) {
				people.add(person);
				Identity identity = this.checkIdentity(business, result, person, unit, o);
				identities.add(identity);
			}
		}
		for (Department o : factory.listSub(org)) {
			this.check(business, result, units, people, personAttributes, identities, factory, unit, o);
		}
	}

	private Unit checkUnit(Business business, PullResult result, Unit sup, Department org) throws Exception {

		Unit unit = business.unit().getWithAndFxIdObject(Objects.toString(org.getDepartmentId()));
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
			if (!StringUtils.equals(unit.getAndFxHash(), DigestUtils.sha256Hex(XGsonBuilder.toJson(org)))) {
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
		unit.setUnique(org.getDepartmentId().toString());
		unit.setAndFxId(org.getDepartmentId().toString());
		unit.setAndFxHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(org)));
		if (null != sup) {
			unit.setSuperior(sup.getId());
		}
		if (null != org.getSequence()) {
			unit.setOrderNumber(org.getSequence().intValue());
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
		unit.setAndFxHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(department)));
		unit.setName(department.getName());
		if (null != department.getSequence()) {
			unit.setOrderNumber(department.getSequence().intValue());
		}
		business.unit().adjustInherit(unit);
		emc.check(unit, CheckPersistType.all);
		emc.commit();
		this.updateIdentityUnitNameAndUnitLevelName(business, unit);
		result.getUpdateUnitList().add(unit.getDistinguishedName());
		return unit;
	}

	private void removeUnit(Business business, PullResult result, Unit unit) throws Exception {
		logger.info("正在删除组织{}.", unit.getDistinguishedName());
		List<Unit> os = new ArrayList<>();
		os.add(unit);
		// unit本身要删除 把所有子组织查询出来一起删除
		os.addAll(business.unit().listSubNestedObject(unit));
		// level大的 先删除
		os = os.stream()
				.sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)).reversed())
				.collect(Collectors.toList());
		for (Unit o : os) {
			this.removeSingleUnit(business, result, o);
		}
	}

	private void removeSingleUnit(Business business, PullResult result, Unit unit) throws Exception {
		logger.info("正在删除单个组织{}.", unit.getDistinguishedName());
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(UnitAttribute.class);
		emc.beginTransaction(UnitDuty.class);
		emc.beginTransaction(Identity.class);
		emc.beginTransaction(Group.class);
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

			for (Group group : business.group().listSupDirectWithIdentityObject(o.getId())) {
				group.getIdentityList().remove(o.getId());
				group.setIdentityList(group.getIdentityList());
			}
		}
		emc.commit();

		emc.beginTransaction(Unit.class);
		emc.remove(unit, CheckRemoveType.all);
		emc.commit();
		result.getRemoveUnitList().add(unit.getDistinguishedName());
	}

	private Person checkPerson(Business business, PullResult result, User user) throws Exception {
		Person person = business.person().getWithAndFxIdObject(user.getUid());
		if (null == person) {
			if ((StringUtils.isNotEmpty(user.getMobile())) && StringUtils.isNotEmpty(user.getName())) {
				person = this.createOrLinkPerson(business, result, user);
			}
		} else {
			if (!StringUtils.equals(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)), person.getAndFxHash())) {
				person = this.updatePerson(business, result, person, user);
			}
		}
		if (person != null && BooleanUtils.isTrue(user.getIsAdmin())) {
			EntityManagerContainer emc = business.entityManagerContainer();
			Role role = emc.firstEqual(Role.class, Role.name_FIELDNAME, OrganizationDefinition.Manager);
			if (!role.getPersonList().contains(person.getId())) {
				emc.beginTransaction(Role.class);
				role.getPersonList().add(person.getId());
				role.setPersonList(role.getPersonList());
				emc.commit();
			}
		}
		return person;
	}

	private Person createOrLinkPerson(Business business, PullResult result, User user) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Person.class);
		Person person = emc.flag(user.getMobile(), Person.class);
		if (null != person) {
			person.setAndFxId(user.getUid());
			person.setAndFxHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setUnique(user.getUid());
			person.setGenderType(GenderType.d);
			emc.check(person, CheckPersistType.all);
			result.getUpdatePersonList().add(person.getDistinguishedName());
		} else {
			person = new Person();
			person.setAndFxId(user.getUid());
			person.setAndFxHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
			person.setName(user.getName());
			person.setMobile(user.getMobile());
			person.setUnique(user.getUid());
			person.setGenderType(GenderType.d);
			/* 新增人员需要增加密码 */
			business.person().setPassword(person, this.initPassword(business, person));
			emc.persist(person, CheckPersistType.all);
			result.getCreatePersonList().add(person.getDistinguishedName());
		}
		emc.commit();
		return person;
	}

	private String initPassword(Business business, Person person) throws Exception {
		String str = Config.person().getPassword();
		Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			Source source = GraalvmScriptingFactory.functionalization(StringEscapeUtils.unescapeJson(matcher.group(1)));
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings()
					.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_PERSON, person);
			Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
			if (opt.isPresent()) {
				str = opt.get();
			}
		}
		return str;
	}

	private Person updatePerson(Business business, PullResult result, Person person, User user) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(Person.class);
		person.setAndFxHash(DigestUtils.sha256Hex(XGsonBuilder.toJson(user)));
		person.setName(user.getName());
		person.setMobile(user.getMobile());
		emc.check(person, CheckPersistType.all);
		emc.commit();
		result.getUpdatePersonList().add(person.getDistinguishedName());
		return person;
	}

	private void removePerson(Business business, PullResult result, Person person) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();

		List<Identity> identities = this.listIdentity(business, person);
		// 删除身份组织职务成员,提交后才可以删除身份
		emc.beginTransaction(UnitDuty.class);
		this.removeMemberOfUnitDuty(business, identities);
		emc.commit();
		// 删除身份
		emc.beginTransaction(Identity.class);
		for (Identity o : identities) {
			emc.remove(o, CheckRemoveType.all);
		}
		emc.commit();

		emc.beginTransaction(PersonAttribute.class);
		for (PersonAttribute o : emc.listEqual(PersonAttribute.class, PersonAttribute.person_FIELDNAME,
				person.getId())) {
			result.getRemovePersonAttributeList().add(o.getDistinguishedName());
			emc.remove(o, CheckRemoveType.all);
		}
		emc.commit();

		// 删除角色成员
		emc.beginTransaction(Role.class);
		this.removeMemberOfRole(business, person);
		emc.commit();

		// 删除组织的管理个人以及继承的管理个人
		emc.beginTransaction(Unit.class);
		this.removeMemberOfUnitController(business, person);
		emc.commit();

		emc.beginTransaction(Group.class);
		for (Group group : business.group().listSupDirectWithPersonObject(person.getId())) {
			group.getPersonList().remove(person.getId());
			group.setPersonList(group.getPersonList());
		}
		emc.commit();

		emc.beginTransaction(Person.class);
		emc.remove(person, CheckRemoveType.all);
		emc.commit();
		result.getRemovePersonList().add(person.getDistinguishedName());
	}

	private void removeMemberOfUnitDuty(Business business, List<Identity> identities) throws Exception {
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

	private void removeMemberOfRole(Business business, Person person) throws Exception {
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

	private void removeMemberOfUnitController(Business business, Person person) throws Exception {
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

	@SuppressWarnings("unchecked")
	private Identity checkIdentity(Business business, PullResult result, Person person, Unit unit, User user)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		p = cb.and(p, cb.equal(root.get(Identity_.unit), unit.getId()));
		List<Identity> os = em.createQuery(cq.select(root).where(p)).setMaxResults(1).getResultList();
		Identity identity = null;
		Long order = user.getSeq();
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
		identity.setUnique(unit.getUnique() + "_" + person.getUnique());
		identity.setName(person.getName());
		identity.setPerson(person.getId());
		identity.setUnit(unit.getId());
		identity.setUnitLevel(unit.getLevel());
		identity.setUnitLevelName(unit.getLevelName());
		identity.setUnitName(unit.getName());
		if (order != null) {
			identity.setOrderNumber(order.intValue());
		}
		// 人员有多个身份不能设置多个主身份
		identity.setMajor(false);
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

	private void updateIdentityUnitNameAndUnitLevelName(Business business, Unit unit) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Unit> os = new ArrayList<>();
		os.add(unit);
		os.addAll(business.unit().listSubNestedObject(unit));

		for (Unit u : os) {
			List<Identity> identityList = this.pickIdentitiesByUnit(business, u.getId());
			if (ListTools.isNotEmpty(identityList)) {
				String _unitName = u.getName();
				String _unitLevelName = u.getLevelName();

				emc.beginTransaction(Identity.class);
				for (Identity i : identityList) {
					i.setUnitName(_unitName);
					i.setUnitLevelName(_unitLevelName);
					emc.check(i, CheckPersistType.all);
				}
				emc.commit();
			}

		}
	}

	private List<Identity> pickIdentitiesByUnit(Business business, String unit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.unit), unit);
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

	private void clean(Business business, PullResult result, List<Unit> units, List<Person> people,
			List<Identity> identities) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		/* 删除身份 */
		/*
		 * List<Identity> allIdentities = this.listIdentity(business); for (Identity
		 * identity : ListUtils.subtract(allIdentities, identities)) { Person person =
		 * emc.find(identity.getPerson(), Person.class); if (null == person ||
		 * StringUtils.isNotEmpty(person.getAndFxId())) { List<UnitDuty> uds =
		 * emc.listIsMember(UnitDuty.class, UnitDuty.identityList_FIELDNAME,
		 * identity.getId()); if (ListTools.isNotEmpty(uds)) {
		 * emc.beginTransaction(UnitDuty.class); uds.stream().forEach(o -> {
		 * o.getIdentityList().remove(identity.getId()); }); emc.commit(); }
		 * emc.beginTransaction(Identity.class); emc.remove(identity,
		 * CheckRemoveType.all); emc.commit(); } }
		 */
		/* 组织单独方法删除 */
		List<Unit> allUnit = this.listUnit(business);
		List<Unit> removeUnits = ListUtils.subtract(allUnit, units).stream()
				.sorted(Comparator.comparing(Unit::getLevel, Comparator.nullsLast(Integer::compareTo)).reversed())
				.collect(Collectors.toList());
		for (Unit unit : removeUnits) {
			this.removeSingleUnit(business, result, unit);
		}

		// 删除个人
		if (BooleanUtils.isTrue(Config.andFx().getSyncDeleteUser())) {
			List<Person> allPeople = this.listPerson(business);
			for (Person person : ListUtils.subtract(allPeople, people)) {
				this.removePerson(business, result, person);
			}
		}
	}

	private List<Unit> listUnit(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.notEqual(root.get(Unit_.andFxId), "");
		p = cb.and(p, cb.isNotNull(root.get(Unit_.andFxId)));
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Person> listPerson(Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.notEqual(root.get(Person_.andFxId), "");
		p = cb.and(p, cb.isNotNull(root.get(Person_.andFxId)));
		List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Identity> listIdentity(Business business) throws Exception {
		return business.entityManagerContainer().listAll(Identity.class);
	}

	private List<Identity> listIdentity(Business business, Person person) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), person.getId());
		return em.createQuery(cq.select(root).where(p)).getResultList();
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
