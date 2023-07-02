package com.x.base.core.entity.tools;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;

public class EntityManagerContainerTools {

	public static <T extends JpaObject> Integer batchDelete(EntityManagerContainer emc, Class<T> clz, Integer batchSize,
			String... ids) throws Exception {
		List<String> list = Arrays.asList(ids);
		return batchDelete(emc, clz, batchSize, list);
	}

	public static <T extends JpaObject> Integer batchDelete(EntityManagerContainer emc, Class<T> clz, Integer batchSize,
			List<String> ids) throws Exception {
		if (null == batchSize || batchSize < 1 || null == ids || ids.isEmpty()) {
			return 0;
		}
		Integer count = 0;
		EntityManager em = emc.get(clz);
		for (int i = 0; i < ids.size(); i++) {
			if (i % batchSize == 0) {
				em.getTransaction().begin();
			}
			T t = em.find(clz, ids.get(i));
			if (null != t) {
				em.remove(t);
			}
			if ((i % batchSize == (batchSize - 1)) || (i == ids.size() - 1)) {
				em.getTransaction().commit();
				count++;
			}
		}
		return count;
	}

}
