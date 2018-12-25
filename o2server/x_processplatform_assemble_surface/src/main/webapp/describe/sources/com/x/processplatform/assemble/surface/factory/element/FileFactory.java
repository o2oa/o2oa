package com.x.processplatform.assemble.surface.factory.element;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.File;
import com.x.processplatform.core.entity.element.File_;

import net.sf.ehcache.Element;

public class FileFactory extends ElementFactory {

	public FileFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
		this.cache = ApplicationCache.instance().getCache(File.class);
	}

	public List<File> pick(List<String> flags) throws Exception {
		List<File> list = new ArrayList<>();
		for (String str : flags) {
			Element element = cache.get(str);
			if (null != element) {
				if (null != element.getObjectValue()) {
					list.add((File) element.getObjectValue());
				}
			} else {
				File o = this.pickObject(str);
				cache.put(new Element(str, o));
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
		Element element = cache.get(flag);
		if (null != element) {
			if (null != element.getObjectValue()) {
				o = (File) element.getObjectValue();
			}
		} else {
			o = this.pickObject(flag);
			cache.put(new Element(flag, o));
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
			List<File> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
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