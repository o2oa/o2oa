package com.x.processplatform.service.processing.jaxrs.applicationdict;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictItem_;
import com.x.processplatform.service.processing.Business;

abstract class BaseAction extends StandardJaxrsAction {

	JsonElement get(Business business, ApplicationDict applicationDict, String... paths) throws Exception {
		List<ApplicationDictItem> list = this.listWithApplicationDictWithPath(business, applicationDict.getId(), paths);
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		JsonElement jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	void update(Business business, ApplicationDict applicationDict, JsonElement jsonElement, String... paths)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		List<ApplicationDictItem> exists = this.listWithApplicationDictWithPath(business, applicationDict.getId(),
				paths);
		if (exists.isEmpty()) {
			throw new Exception("applicationDict{id:" + applicationDict + "} on path:" + StringUtils.join(paths, ".")
					+ " is not existed.");
		}
		emc.beginTransaction(ApplicationDictItem.class);
		// emc.beginTransaction(ApplicationDictLobItem.class);
		List<ApplicationDictItem> currents = converter.disassemble(jsonElement, paths);
		List<ApplicationDictItem> removes = converter.subtract(exists, currents);
		List<ApplicationDictItem> adds = converter.subtract(currents, exists);
		for (ApplicationDictItem o : removes) {
			emc.remove(o);
		}
		for (ApplicationDictItem o : adds) {
			o.setBundle(applicationDict.getId());
			o.setItemCategory(ItemCategory.pp_dict);
			o.setDistributeFactor(applicationDict.getDistributeFactor());
			o.setApplication(applicationDict.getApplication());
			emc.persist(o);
		}
	}

	void create(Business business, ApplicationDict applicationDict, JsonElement jsonElement, String... paths)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		ApplicationDictItem parent = this.getWithApplicationDictWithPath(business, applicationDict.getId(),
				parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5],
				parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		ApplicationDictItem cursor = this.getWithApplicationDictWithPath(business, applicationDict.getId(),
				cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5],
				cursorPaths[6], cursorPaths[7]);
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		emc.beginTransaction(ApplicationDictItem.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = this.getArrayLastIndexWithApplicationDictWithPath(business, applicationDict.getId(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<ApplicationDictItem> adds = converter.disassemble(jsonElement, ps);
			for (ApplicationDictItem o : adds) {
				o.setBundle(applicationDict.getId());
				o.setItemCategory(ItemCategory.pp_dict);
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(applicationDict.getApplication());
				emc.persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<ApplicationDictItem> adds = converter.disassemble(jsonElement, paths);
			for (ApplicationDictItem o : adds) {
				o.setBundle(applicationDict.getId());
				o.setItemCategory(ItemCategory.pp_dict);
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(applicationDict.getApplication());
				emc.persist(o);
			}
		} else {
			throw new Exception("unexpected post with applicationDict{id:" + applicationDict + "} path:"
					+ StringUtils.join(paths, ".") + "json:" + jsonElement);
		}
	}

	void delete(Business business, ApplicationDict applicationDict, String... paths) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<ApplicationDictItem> exists = this.listWithApplicationDictWithPath(business, applicationDict.getId(),
				paths);
		if (exists.isEmpty()) {
			throw new Exception("applicationDict{id:" + applicationDict + "} on path:" + StringUtils.join(paths, ".")
					+ " is not existed.");
		}
		emc.beginTransaction(ApplicationDictItem.class);
		for (ApplicationDictItem o : exists) {
			emc.remove(o);
		}
		if (NumberUtils.isCreatable(paths[paths.length - 1])) {
			int position = paths.length - 1;
			for (ApplicationDictItem o : this.listWithApplicationDictWithPathWithAfterLocation(business,
					applicationDict.getId(), NumberUtils.toInt(paths[position]), paths)) {
				o.path(Integer.toString(o.pathLocation(position) - 1), position);
			}
		}
	}

	private List<ApplicationDictItem> listWithApplicationDictWithPath(Business business, String applicationDict,
			String... paths) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}

	private ApplicationDictItem getWithApplicationDictWithPath(Business business, String applicationDict, String path0,
			String path1, String path2, String path3, String path4, String path5, String path6, String path7)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		/*
		p = cb.and(p, cb.equal(root.get("path0"), path0));
		p = cb.and(p, cb.equal(root.get("path1"), path1));
		p = cb.and(p, cb.equal(root.get("path2"), path2));
		p = cb.and(p, cb.equal(root.get("path3"), path3));
		p = cb.and(p, cb.equal(root.get("path4"), path4));
		p = cb.and(p, cb.equal(root.get("path5"), path5));
		p = cb.and(p, cb.equal(root.get("path6"), path6));
		p = cb.and(p, cb.equal(root.get("path7"), path7));
		*/
		if(path0.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path0)), cb.equal(root.get(ApplicationDictItem_.path0), path0)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path0), path0));
		}
	 
		if(path1.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path1)), cb.equal(root.get(ApplicationDictItem_.path1), path1)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path1), path1));
		}
		
		if(path2.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path2)), cb.equal(root.get(ApplicationDictItem_.path2), path2)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path2), path2));
		}
		
		if(path3.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path3)), cb.equal(root.get(ApplicationDictItem_.path3), path3)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path3), path3));
		}
		
		
		if(path4.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path4)), cb.equal(root.get(ApplicationDictItem_.path4), path4)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path4), path4));
		}
		
		if(path5.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path5)), cb.equal(root.get(ApplicationDictItem_.path5), path5)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path5), path5));
		}
		
		if(path6.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path6)), cb.equal(root.get(ApplicationDictItem_.path6), path6)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path6), path6));
		}
		if(path7.equals("")) {
			p =cb.and(p, cb.or(cb.isNull(root.get(ApplicationDictItem_.path7)), cb.equal(root.get(ApplicationDictItem_.path7), path7)));
		}else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path7), path7));
		}
		
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("error mulit applicationDictItem{id:" + applicationDict + ", path0:" + path0 + ", path1:"
				+ path1 + ", path2:" + path2 + ", path3:" + path3 + ", path4:" + path4 + ", path5:" + path5 + ", path6:"
				+ path6 + ", path7:" + path7 + "}");
	}

	private Integer getArrayLastIndexWithApplicationDictWithPath(Business business, String applicationDict,
			String... paths) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; ((i < paths.length) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		for (int i = paths.length + 1; (i < 8); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), ""));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get("path" + paths.length + "Location")));
		List<ApplicationDictItem> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0).get("path" + paths.length + "Location", Integer.class);
		}
	}

	private List<ApplicationDictItem> listWithApplicationDictWithPathWithAfterLocation(Business business,
			String applicationDict, Integer index, String... paths) throws Exception {
		EntityManager em = business.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; ((i < (paths.length - 1)) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		Path<Integer> locationPath = root.get("path" + (paths.length - 1) + "Location");
		p = cb.and(p, cb.greaterThan(locationPath, index));
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}
}
