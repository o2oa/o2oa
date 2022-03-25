package com.x.cms.assemble.control.factory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.File_;

public class FileFactory extends ElementFactory {

	public FileFactory(Business business) throws Exception {
		super(business);
		this.cacheCategory = new Cache.CacheCategory(File.class);
	}
	
	public List<String> listWithApplication(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.appId), applicationId);
		cq.select(root.get(File_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<File> listWithApplicationObject(String applicationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(File.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<File> cq = cb.createQuery(File.class);
		Root<File> root = cq.from(File.class);
		Predicate p = cb.equal(root.get(File_.appId), applicationId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<File> pick(List<String> flags) throws Exception {
		List<File> list = new ArrayList<>();
		for (String str : flags) {
			Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), str );
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
			if (optional.isPresent()) {
				if (null != optional.get()) {
					list.add((File) optional.get());
				}
			} else {
				File o = this.pickObject(str);
				CacheManager.put(cacheCategory, cacheKey, o );
				if (null != o) {
					list.add(o);
				}
			}
		}
		return list;
	}

	public File pick(String flag) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}
		File o = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), flag );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );
		if (optional.isPresent()) {
			if (null != optional.get()) {
				o = (File) optional.get();
			}
		} else {
			o = this.pickObject(flag);
			CacheManager.put(cacheCategory, cacheKey, o );
		}
		return o;
	}

	private File pickObject(String flag) throws Exception {
		File o = this.business().entityManagerContainer().flag(flag, File.class);
		if (o != null) {
			this.entityManagerContainer().get(File.class).detach(o);
		}
		if (null == o) {
			EntityManager em = this.entityManagerContainer().get(File.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<File> cq = cb.createQuery(File.class);
			Root<File> root = cq.from(File.class);
			Predicate p = cb.equal(root.get(File_.name), flag);
			List<File> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct().collect(Collectors.toList());
			if (os.size() == 1) {
				o = os.get(0);
				em.detach(o);
			}
		}
		return o;
	}

	public <T extends File> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(File::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(File::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}