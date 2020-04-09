package com.x.organization.assemble.control.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.organization.assemble.control.AbstractFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.PermissionSetting;
import com.x.organization.core.entity.PermissionSetting_;
import com.x.organization.core.entity.PersistenceProperties;

import net.sf.ehcache.Element;

public class PermissionSettingFactory extends AbstractFactory {

	public PermissionSettingFactory(Business business) throws Exception {
		super(business);
		cache = ApplicationCache.instance().getCache(PermissionSetting.class);
	}

	public PermissionSetting pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		PermissionSetting o = null;
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (PermissionSetting) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
		}
		return o;
	}
	public List<PermissionSetting> pick(List<String> flags) throws Exception {
		List<PermissionSetting> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((PermissionSetting) element.getObjectValue());
				}
			} else {
				PermissionSetting o = this.pickObject(str);
				cache.put(new Element(str, o));
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}
	
	private PermissionSetting pickObject(String flag) throws Exception {
		PermissionSetting o = this.entityManagerContainer().flag(flag, PermissionSetting.class); 
		if (o != null) {
			this.entityManagerContainer().get(PermissionSetting.class).detach(o);
		} else {
			String name = flag;
			Matcher matcher = PersistenceProperties.PermissionSetting.distinguishedName_pattern.matcher(flag);
			if (matcher.find()) {
				name = matcher.group(1);
				String unique = matcher.group(2);
				o = this.entityManagerContainer().flag(unique, PermissionSetting.class);
				if (null != o) {
					this.entityManagerContainer().get(PermissionSetting.class).detach(o);
				}
			}
			if (null == o) {
				EntityManager em = this.entityManagerContainer().get(PermissionSetting.class);
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PermissionSetting> cq = cb.createQuery(PermissionSetting.class);
				Root<PermissionSetting> root = cq.from(PermissionSetting.class);
				Predicate p = cb.equal(root.get(PermissionSetting_.id), name);
				List<PermissionSetting> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
				if (os.size() == 1) {
					o = os.get(0);
					em.detach(o);
				}
			}
		}
		return o;
	}
	public List<String> fetchAllIdsByCreator() throws Exception {
		EntityManager em = this.entityManagerContainer().get(PermissionSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PermissionSetting> root = cq.from(PermissionSetting.class);
		cq.select(root.get(PermissionSetting_.id)).orderBy(cb.asc(root.get(PermissionSetting_.createTime))); 
		return em.createQuery(cq).getResultList();
	}
	/*
	public List<String> fetchAllGroupTypeByCreator(String distinguishName) throws Exception {
		EntityManager em = this.entityManagerContainer().get(PermissionSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<PermissionSetting> root = cq.from(PermissionSetting.class);
		Predicate p = cb.equal(root.get(PermissionSetting_.distinguishedName), distinguishName);
		cq.select(root.get(PermissionSetting_.groupType)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}*/
}