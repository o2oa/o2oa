package com.x.portal.assemble.surface.factory;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.File_;

public class FileFactory extends AbstractFactory {

	static CacheCategory cache = new CacheCategory(File.class);

	public FileFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public File pick(String id) throws Exception {
		CacheKey cacheKey = new CacheKey(id);
		Optional<?> optional = CacheManager.get(cache, cacheKey);
		if (optional.isPresent()) {
			return (File) optional.get();
		} else {
			File o = this.business().entityManagerContainer().find(id, File.class);
			if (null != o) {
				this.business().entityManagerContainer().get(File.class).detach(o);
				CacheManager.put(cache, cacheKey, o);
				return o;
			}
			return null;
		}
	}

	public List<File> listWithPortalObject(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<File> cq = cb.createQuery(File.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.portal), portalId);
		List<File> list = em.createQuery(cq.select(root).where(p)).getResultList();
		return list;
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(File_.id)).where(p)).getResultList();
		return list;
	}
	
	public <T extends File> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(File::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(File::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}