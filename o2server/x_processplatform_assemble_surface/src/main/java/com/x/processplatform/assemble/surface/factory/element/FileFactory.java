package com.x.processplatform.assemble.surface.factory.element;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.File;

public class FileFactory extends ElementFactory {

	public FileFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<File> pick(List<String> flags) throws Exception {
		return this.pick(flags, File.class);
	}

	public File pick(String flag) throws Exception {
		return this.pick(flag, File.class);
	}

//	private File pickObject(String flag) throws Exception {
//		File o = this.business().entityManagerContainer().flag(flag, File.class);
//		if (o != null) {
//			this.entityManagerContainer().get(File.class).detach(o);
//		}
//		if (null == o) {
//			EntityManager em = this.entityManagerContainer().get(File.class);
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<File> cq = cb.createQuery(File.class);
//			Root<File> root = cq.from(File.class);
//			Predicate p = cb.equal(root.get(File_.name), flag);
//			List<File> os = em.createQuery(cq.select(root).where(p).distinct(true)).getResultList();
//			if (os.size() == 1) {
//				o = os.get(0);
//				em.detach(o);
//			}
//		}
//		return o;
//	}

	public <T extends File> List<T> sort(List<T> list) {
		list = list.stream()
				.sorted(Comparator.comparing(File::getAlias, Comparator.nullsLast(String::compareTo))
						.thenComparing(File::getName, Comparator.nullsLast(String::compareTo)))
				.collect(Collectors.toList());
		return list;
	}
}