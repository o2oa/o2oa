package com.x.organization.assemble.control.jaxrs.personcard;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.PersonCard;
import com.x.organization.core.entity.PersonCard_;


class ActionListPagingWithGroup extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListPagingWithGroup.class);
	//SingularAttribute<JpaObject, Date> defaultOrder = PersonCard_.createTime; 

	// 线索分页查询，具备多个字段的like查询(无权限控制)
	ActionResult<List<Wo>> Execute_Paging_groupType(EffectivePerson effectivePerson, Integer adjustPage, Integer adjustPageSize, JsonElement jsonElement)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<PersonCard> os = this.list(effectivePerson, business, adjustPage, adjustPageSize,wi.getGroupType(),wi.getKey());
			List<Wo> wos = Wo.copier.copy(os);
			result.setData(wos);
			result.setCount(this.count(effectivePerson, business,wi.getGroupType(), wi.getKey()));
			return result;
		}
	}

	private List<PersonCard> list(EffectivePerson effectivePerson, Business business, Integer adjustPage, Integer adjustPageSize, String grouptype, String keyString) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonCard.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonCard> cq = cb.createQuery(PersonCard.class);
		Root<PersonCard> root = cq.from(PersonCard.class);

		Order _order = cb.asc(root.get(PersonCard_.orderNumber));
		Predicate pe = cb.equal(root.get(PersonCard_.distinguishedName), effectivePerson.getDistinguishedName());
		if(StringUtils.isNotEmpty(grouptype)){
			pe = cb.and(pe,cb.equal(root.get(PersonCard_.groupType),grouptype));
		}
		
		if (StringUtils.isNotEmpty(keyString)) {
			String key = StringUtils.trim(StringUtils.replaceEach(keyString, new String[] { "\u3000", "?", "%" }, new String[] { " ", "", "" }));
			if (StringUtils.isNotEmpty(key)) {
				Predicate p = cb.or(cb.like(root.get(PersonCard_.name), "%" + key + "%"), cb.like(root.get(PersonCard_.status), "%" + key + "%"),
						cb.like(root.get(PersonCard_.mobile), "%" + key + "%"), cb.like(root.get(PersonCard_.officePhone), "%" + key + "%"),
						cb.like(root.get(PersonCard_.pinyin), "%" + key + "%"), cb.like(root.get(PersonCard_.pinyinInitial), "%" + key + "%"));
				p = cb.and(p,pe);
					cq.select(root).where(p).orderBy(cb.asc(root.get(PersonCard_.orderNumber)));
			} else {
					cq.select(root).where(pe).orderBy(cb.asc(root.get(PersonCard_.orderNumber)));				
			} 

		} else {
				cq.select(root).where(pe).orderBy(cb.asc(root.get(PersonCard_.orderNumber)));
		}
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize).getResultList();
	}

	private Long count(EffectivePerson effectivePerson, Business business,String grouptype ,String keyString) throws Exception {
		EntityManager em = business.entityManagerContainer().get(PersonCard.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PersonCard> root = cq.from(PersonCard.class);

		// cq.select(cb.count(root));
		Predicate pe = cb.equal(root.get(PersonCard_.distinguishedName), effectivePerson.getDistinguishedName());
		if(StringUtils.isNotEmpty(grouptype)){
			pe = cb.and(pe,cb.equal(root.get(PersonCard_.groupType),grouptype));
		}
		if (StringUtils.isNotEmpty(keyString)) {
			String key = StringUtils.trim(StringUtils.replace(keyString, "\u3000", " "));
			if (StringUtils.isNotEmpty(key)) {
				Predicate p = cb.or(cb.like(root.get(PersonCard_.name), "%" + key + "%"), cb.like(root.get(PersonCard_.status), "%" + key + "%"),
						cb.like(root.get(PersonCard_.mobile), "%" + key + "%"), cb.like(root.get(PersonCard_.officePhone), "%" + key + "%"),
						cb.like(root.get(PersonCard_.pinyin), "%" + key + "%"), cb.like(root.get(PersonCard_.pinyinInitial), "%" + key + "%"));
				p = cb.and(p,pe);
				cq.select(cb.count(root)).where(p);
			} else {
				cq.select(cb.count(root)).where(pe);
			}
		} else {
			cq.select(cb.count(root)).where(pe);
		}
		return em.createQuery(cq).getSingleResult();
	}

	public static class Wo extends WoPersonCardAbstract {
		private static final long serialVersionUID = 1276641320278402941L;
		static WrapCopier<PersonCard, Wo> copier = WrapCopierFactory.wo(PersonCard.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("匹配关键字")
		private String key;
		
		@FieldDescribe("分组名称")
		private String groupType;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		public String getGroupType() {
			return groupType;
		}

		public void setGroupType(String groupType) {
			this.groupType = groupType;
		}

	}

}
