package com.x.processplatform.service.processing;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.element.ApplicationDict;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictItem_;
import com.x.processplatform.core.entity.element.ApplicationDict_;

public class ApplicationDictHelper {
	private String application;
	private EntityManagerContainer emc;
	private DataItemConverter<ApplicationDictItem> converter;
	private Gson gson = XGsonBuilder.instance();

	private static String path_separator = ".";
	
	public ApplicationDictHelper(EntityManagerContainer emc, String application) {
		this.emc = emc;
		this.application = application;
		this.converter = new DataItemConverter<>(ApplicationDictItem.class);
	}

	public String select(String applicationDict, String path) throws Exception {
		ApplicationDict dict = this.getApplicationDict(application, applicationDict);
		if (null == dict) {
			throw new Exception("applicationDict name or alias or id :" + applicationDict + " on applicaiton{id:"
					+ application + "} not existed.");
		}
		String[] paths = StringUtils.split(path, path_separator);
		List<ApplicationDictItem> list = this.listWithApplicationDictWithPath(dict.getId(), paths);
		JsonElement jsonElement = converter.assemble(list, paths.length);
		return gson.toJson(jsonElement);
	}

	public void insert(String applicationDict, String json, String path) throws Exception {
		try {
			this.emc.beginTransaction(ApplicationDictItem.class);
			// this.emc.beginTransaction(ApplicationDictLobItem.class);
			ApplicationDict dict = getApplicationDict(application, applicationDict);
			if (null == dict) {
				throw new Exception("applicationDict{id:" + applicationDict + "} not existed.");
			}
			String[] paths = StringUtils.split(path, path_separator);
			JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
			String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
			String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
			for (int i = 0; i < paths.length - 1; i++) {
				parentPaths[i] = paths[i];
				cursorPaths[i] = paths[i];
			}
			cursorPaths[paths.length - 1] = paths[paths.length - 1];
			ApplicationDictItem parent = this.getWithApplicationDictWithPath(dict.getId(), parentPaths[0],
					parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6],
					parentPaths[7]);
			if (null == parent) {
				throw new Exception("parent not existed.");
			}
			ApplicationDictItem cursor = this.getWithApplicationDictWithPath(dict.getId(), cursorPaths[0],
					cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6],
					cursorPaths[7]);
			DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
			if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
				/* 向数组里面添加一个成员对象 */
				Integer index = this.getArrayLastIndexWithApplicationDictWithPath(dict.getId(), paths);
				/* 新的路径开始 */
				String[] ps = new String[paths.length + 1];
				for (int i = 0; i < paths.length; i++) {
					ps[i] = paths[i];
				}
				ps[paths.length] = Integer.toString(index + 1);
				List<ApplicationDictItem> adds = converter.disassemble(jsonElement, ps);
				for (ApplicationDictItem o : adds) {
					o.setApplication(application);
					o.setBundle(dict.getId());
					o.setItemCategory(ItemCategory.pp_dict);
					/** 将数据字典和数据存放在同一个分区 */
					o.setDistributeFactor(dict.getDistributeFactor());
					emc.persist(o);
				}
			} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
				/* 向parent对象添加一个属性值 */
				List<ApplicationDictItem> adds = converter.disassemble(jsonElement, paths);
				for (ApplicationDictItem o : adds) {
					o.setApplication(application);
					o.setBundle(dict.getId());
					o.setItemCategory(ItemCategory.pp_dict);
					/** 将数据字典和数据存放在同一个分区 */
					o.setDistributeFactor(dict.getDistributeFactor());
					/** 将数据字典和数据存放在同一个分区 */
					o.setDistributeFactor(dict.getDistributeFactor());
					emc.persist(o);
				}
			} else {
				throw new Exception("unexpected post with applicationDict{id:" + applicationDict + "} path:"
						+ StringUtils.join(paths, ".") + "json:" + jsonElement);
			}
		} catch (Exception e) {
			throw new Exception("postWithApplicationDict error.", e);
		}
	}

	public void update(String applicationDict, String json, String path) throws Exception {
		try {
			this.emc.beginTransaction(ApplicationDictItem.class);
			JsonElement jsonElement = gson.fromJson(json, JsonElement.class);
			ApplicationDict dict = getApplicationDict(application, applicationDict);
			String[] paths = StringUtils.split(path, path_separator);
			if (null == dict) {
				throw new Exception("applicationDict{id:" + applicationDict + "} not existed.");
			}
			List<ApplicationDictItem> exists = this.listWithApplicationDictWithPath(dict.getId(), paths);
			if (exists.isEmpty()) {
				throw new Exception("applicationDict{id:" + applicationDict + "} on path:"
						+ StringUtils.join(paths, ".") + " is not existed.");
			}
			List<ApplicationDictItem> currents = converter.disassemble(jsonElement, paths);
			List<ApplicationDictItem> removes = converter.subtract(exists, currents);
			List<ApplicationDictItem> adds = converter.subtract(currents, exists);
			for (ApplicationDictItem o : removes) {
				emc.remove(o);
			}
			for (ApplicationDictItem o : adds) {
				o.setBundle(dict.getId());
				o.setApplication(application);
				o.setItemCategory(ItemCategory.pp_dict);
				/** 将数据字典和数据存放在同一个分区 */
				o.setDistributeFactor(dict.getDistributeFactor());
				emc.persist(o);
			}
		} catch (Exception e) {
			throw new Exception("putWithApplicationDictWithPath error.", e);
		}
	}

	private List<ApplicationDictItem> listWithApplicationDictWithPath(String applicationDict, String... paths)
			throws Exception {
		EntityManager em = this.emc.get(ApplicationDictItem.class);
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

	private ApplicationDictItem getWithApplicationDictWithPath(String applicationDict, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		EntityManager em = emc.get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		p = cb.and(p, cb.equal(root.get("path0"), path0));
		p = cb.and(p, cb.equal(root.get("path1"), path1));
		p = cb.and(p, cb.equal(root.get("path2"), path2));
		p = cb.and(p, cb.equal(root.get("path3"), path3));
		p = cb.and(p, cb.equal(root.get("path4"), path4));
		p = cb.and(p, cb.equal(root.get("path5"), path5));
		p = cb.and(p, cb.equal(root.get("path6"), path6));
		p = cb.and(p, cb.equal(root.get("path7"), path7));
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

	private ApplicationDict getApplicationDict(String application, String applicationDict) throws Exception {
		EntityManager em = emc.get(ApplicationDict.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDict> cq = cb.createQuery(ApplicationDict.class);
		Root<ApplicationDict> root = cq.from(ApplicationDict.class);
		Predicate p = cb.equal(root.get(ApplicationDict_.id), applicationDict);
		p = cb.or(p, cb.equal(root.get(ApplicationDict_.name), applicationDict));
		p = cb.or(p, cb.equal(root.get(ApplicationDict_.alias), applicationDict));
		p = cb.and(p, cb.equal(root.get(ApplicationDict_.application), application));
		cq.select(root).where(p);
		List<ApplicationDict> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	private Integer getArrayLastIndexWithApplicationDictWithPath(String applicationDict, String... paths)
			throws Exception {
		EntityManager em = emc.get(ApplicationDictItem.class);
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
}
