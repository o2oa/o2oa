package com.x.organization.assemble.control.jaxrs.person;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.enums.PersonStatusEnum;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionListFilterPaging extends BaseAction {
	private static final Logger logger = LoggerFactory.getLogger(ActionListFilterPaging.class);
	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			page = (page == null || page < 1) ? 1 : page;
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = new ArrayList<>();
			Long count  = 0L;
			List<String> conUnitList = new ArrayList<>();
            if(BooleanUtils.isTrue(wi.getController()) && !business.isPersonManager(effectivePerson)){
				Person person = business.person().pick(effectivePerson.getDistinguishedName());
				List<Unit> unitList = business.unit().listControlUnitWithPerson(person.getId());
				if (ListTools.isNotEmpty(unitList)) {
					conUnitList = getControlUnitList(business, unitList, effectivePerson);
				}else{
					result.setData(wos);
					result.setCount(count);
					return result;
				}
			}
			if(ListTools.isEmpty(conUnitList)){
				Predicate p = toFilterPredicate(business, wi, effectivePerson);
				wos.addAll(emc.fetchDescPaging(Person.class, Wo.copier, p, page, size, JpaObject.sequence_FIELDNAME));

				this.updateControl(effectivePerson, business, wos);
				count = emc.count(Person.class, p);
			}else{
				wos.addAll(filterWithUnit(business, wi, conUnitList, page, size));
				wos.forEach(wo -> {
					wo.getControl().setAllowDelete(true);
					wo.getControl().setAllowEdit(true);
				});
				count = countWithUnit(business, wi, conUnitList);
			}
			wos.forEach(wo -> {
				if (wo.getName().startsWith(Person.ENCRYPT)) {
					wo.setName(Crypto.base64Decode(wo.getName().substring(Person.ENCRYPT.length())));
				}
				if (wo.getMobile().startsWith(Person.ENCRYPT)) {
					wo.setMobile(Crypto.base64Decode(wo.getMobile().substring(Person.ENCRYPT.length())));
				}
			});

			result.setData(wos);
			result.setCount(count);
			return result;
		}
	}

	private List<String> getControlUnitList(Business business, List<Unit> unitList, EffectivePerson effectivePerson) throws Exception {
		List<String> conUnitList = new ArrayList<>();
		if(unitList.stream().noneMatch(u -> Unit.TOP_LEVEL.equals(u.getLevel()))) {
			for (Unit unit : unitList) {
				if (!conUnitList.contains(unit.getId())) {
					conUnitList.add(unit.getId());
					conUnitList.addAll(business.unit().listSubNested(unit.getId()));
				}
			}
			logger.info("{} 管理组织总数：{}",  effectivePerson.getDistinguishedName(), conUnitList.size());
		}
		return conUnitList;
	}

	private List<Wo> filterWithUnit(Business business, Wi wi, List<String> conUnitList, Integer page, Integer size) throws Exception {
		List<Wo> wos = new ArrayList<>();
		int max = adjustSize(size);
		int startPosition = (page - 1) * max;
		EntityManager em = business.entityManagerContainer().get(Person.class);
		EntityManager subEm = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaBuilder subCb = subEm.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Person> root = cq.from(Person.class);
		Predicate predicate = cb.conjunction();
		if (StringUtils.isNotBlank(wi.getKey())) {
			String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));

			Predicate p = cb.like(cb.lower(root.get(Person_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyin)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyinInitial)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.mobile)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.equal(root.get(Person_.distinguishedName), wi.getKey()));
			if(BooleanUtils.isTrue(Config.person().getPersonEncryptEnable())){
				String enStr = Crypto.base64Encode(wi.getKey());
				p = cb.or(p, cb.like(root.get(Person_.name), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
				p = cb.or(p, cb.like(root.get(Person_.mobile), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			predicate = cb.and(predicate, p);
		}
		if(StringUtils.isNotBlank(wi.getStatus())){
			if(PersonStatusEnum.NORMAL.getValue().equals(wi.getStatus())){
				predicate = cb.and(predicate, cb.or(cb.isNull(root.get(Person_.status)), cb.equal(root.get(Person_.status), PersonStatusEnum.NORMAL.getValue())));
			}else{
				predicate = cb.and(predicate, cb.equal(root.get(Person_.status), wi.getStatus()));
			}
		}

		Subquery<Identity> subQuery = cq.subquery(Identity.class);
		Root<Identity> subRoot = subQuery.from(subEm.getMetamodel().entity(Identity.class));
		subQuery.select(subRoot);
		Predicate subP = subCb.equal(subRoot.get(Identity_.person), root.get(Person_.id));
		subP = subCb.and(subP, subRoot.get(Identity_.unit).in(conUnitList));
		subQuery.where(subP);

		predicate = cb.and(predicate, cb.exists(subQuery));

		List<String> fields = Wo.copier.getCopyFields();
		List<Selection<?>> selections = new ArrayList<>();
		for (String str : Wo.copier.getCopyFields()) {
			selections.add(root.get(str));
		}

		cq.multiselect(selections).where(predicate).orderBy(cb.desc(root.get(JpaObject.sequence_FIELDNAME)));

		for (Tuple o : em.createQuery(cq).setFirstResult(startPosition).setMaxResults(max).getResultList()) {
			Wo wo = new Wo();
			for (int i = 0; i < fields.size(); i++) {
				PropertyUtils.setProperty(wo, fields.get(i), o.get(selections.get(i)));
			}
			wos.add(wo);
		}

		return wos;
	}

	private Long countWithUnit(Business business, Wi wi, List<String> conUnitList) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		EntityManager subEm = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaBuilder subCb = subEm.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> root = cq.from(Person.class);
		Predicate predicate = cb.conjunction();
		if (StringUtils.isNotBlank(wi.getKey())) {
			String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
			Predicate p = cb.like(cb.lower(root.get(Person_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyin)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyinInitial)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.mobile)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			if(BooleanUtils.isTrue(Config.person().getPersonEncryptEnable())){
				String enStr = Crypto.base64Encode(wi.getKey());
				p = cb.or(p, cb.like(root.get(Person_.name), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
				p = cb.or(p, cb.like(root.get(Person_.mobile), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			p = cb.or(p, cb.equal(root.get(Person_.distinguishedName), wi.getKey()));
			predicate = cb.and(predicate, p);
		}
		if(StringUtils.isNotBlank(wi.getStatus())){
			if(PersonStatusEnum.NORMAL.getValue().equals(wi.getStatus())){
				predicate = cb.and(predicate, cb.or(cb.isNull(root.get(Person_.status)), cb.equal(root.get(Person_.status), PersonStatusEnum.NORMAL.getValue())));
			}else{
				predicate = cb.and(predicate, cb.equal(root.get(Person_.status), wi.getStatus()));
			}
		}

		Subquery<Identity> subQuery = cq.subquery(Identity.class);
		Root<Identity> subRoot = subQuery.from(subEm.getMetamodel().entity(Identity.class));
		subQuery.select(subRoot);
		Predicate subP = subCb.equal(subRoot.get(Identity_.person), root.get(Person_.id));
		subP = subCb.and(subP, subRoot.get(Identity_.unit).in(conUnitList));
		subQuery.where(subP);

		predicate = cb.and(predicate, cb.exists(subQuery));

		cq.select(cb.count(root)).where(predicate);
		return em.createQuery(cq).getSingleResult();
	}

	private Predicate toFilterPredicate(Business business,  Wi wi, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Person> root = cq.from(Person.class);
		Predicate predicate = business.personPredicateWithTopUnit(effectivePerson, true);
		if (StringUtils.isNotBlank(wi.getKey())) {
			String str = StringUtils.lowerCase(StringTools.escapeSqlLikeKey(wi.getKey()));
			Predicate p = cb.like(cb.lower(root.get(Person_.name)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR);
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.unique)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyin)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.pinyinInitial)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			p = cb.or(p, cb.like(cb.lower(root.get(Person_.mobile)), "%" + str + "%", StringTools.SQL_ESCAPE_CHAR));
			if(BooleanUtils.isTrue(Config.person().getPersonEncryptEnable())){
				String enStr = Crypto.base64Encode(wi.getKey());
				p = cb.or(p, cb.like(root.get(Person_.name), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
				p = cb.or(p, cb.like(root.get(Person_.mobile), "%" + enStr + "%", StringTools.SQL_ESCAPE_CHAR));
			}
			p = cb.or(p, cb.equal(root.get(Person_.distinguishedName), wi.getKey()));
			predicate = cb.and(predicate, p);
		}
		if(StringUtils.isNotBlank(wi.getStatus())){
			if(PersonStatusEnum.NORMAL.getValue().equals(wi.getStatus())){
				predicate = cb.and(predicate, cb.or(cb.isNull(root.get(Person_.status)), cb.equal(root.get(Person_.status), PersonStatusEnum.NORMAL.getValue())));
			}else{
				predicate = cb.and(predicate, cb.equal(root.get(Person_.status), wi.getStatus()));
			}
		}
		return predicate;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("搜索关键字")
		private String key;
		@FieldDescribe("状态：0|正常、1|锁定、2|禁用.")
		private String status;
		@FieldDescribe("是否依据组织管理员管理权限展现")
		private Boolean controller;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Boolean getController() {
			return controller;
		}

		public void setController(Boolean controller) {
			this.controller = controller;
		}
	}

	public static class Wo extends WoPersonAbstract {

		private static final long serialVersionUID = 1847108296662273067L;

		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class,
				JpaObject.singularAttributeField(Person.class, true, true), null);

	}
}
