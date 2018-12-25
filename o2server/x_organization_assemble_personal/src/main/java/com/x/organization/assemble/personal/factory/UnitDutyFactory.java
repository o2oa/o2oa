package com.x.organization.assemble.personal.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.organization.assemble.personal.AbstractFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.PersistenceProperties;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

import net.sf.ehcache.Element;

public class UnitDutyFactory extends AbstractFactory {

	public UnitDutyFactory(Business business) throws Exception {
		super(business);
	}

	public UnitDuty pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		UnitDuty o = null;
		Element element = this.business.cache().get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (UnitDuty) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			this.business.cache().put(new Element(flag, o));
		}
		return o;
	}

	private UnitDuty pickObject(String flag) throws Exception {
		UnitDuty o = this.entityManagerContainer().flag(flag, UnitDuty.class);
		if (o != null) {
			this.entityManagerContainer().get(UnitDuty.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.UnitDuty.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, UnitDuty.class);
				if (null != o) {
					this.entityManagerContainer().get(UnitDuty.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(UnitDuty.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
				Root<UnitDuty> root = cq.from(UnitDuty.class);
				Predicate p = cb.equal(root.get(UnitDuty_.name), name);
				List<UnitDuty> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}

	public List<UnitDuty> pick(List<String> flags) throws Exception {
		List<UnitDuty> list = new ArrayList<>();
		for (String str : flags) {
			Element element = this.business.cache().get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((UnitDuty) element.getObjectValue());
				}
			} else {
				UnitDuty o = this.pickObject(str);
				this.business.cache().put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public <T extends UnitDuty> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(UnitDuty::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
						.thenComparing(
								Comparator.comparing(UnitDuty::getName, Comparator.nullsLast(String::compareTo))))
				.collect(Collectors.toList());
		return list;
	}
}