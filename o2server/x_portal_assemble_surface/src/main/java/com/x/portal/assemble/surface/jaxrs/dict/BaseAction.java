package com.x.portal.assemble.surface.jaxrs.dict;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.ApplicationDictItem;
import com.x.portal.assemble.surface.Business;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

	protected static final String PATH_SPLIT = "\\.";

	JsonElement get(Business business, ApplicationDict applicationDict, String... paths) throws Exception {
		List<ApplicationDictItem> list = business.applicationDictItem()
				.listWithApplicationDictWithPath(applicationDict.getId(), paths);
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		JsonElement jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	void update(Business business, ApplicationDict applicationDict, JsonElement jsonElement, String... paths)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		List<ApplicationDictItem> exists = business.applicationDictItem()
				.listWithApplicationDictWithPath(applicationDict.getId(), paths);
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
			o.setItemCategory(ItemCategory.portal_dict);
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
		ApplicationDictItem parent = business.applicationDictItem().getWithApplicationDictWithPath(
				applicationDict.getId(), parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4],
				parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		ApplicationDictItem cursor = business.applicationDictItem().getWithApplicationDictWithPath(
				applicationDict.getId(), cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4],
				cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		DataItemConverter<ApplicationDictItem> converter = new DataItemConverter<>(ApplicationDictItem.class);
		emc.beginTransaction(ApplicationDictItem.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.applicationDictItem()
					.getArrayLastIndexWithApplicationDictWithPath(applicationDict.getId(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<ApplicationDictItem> adds = converter.disassemble(jsonElement, ps);
			for (ApplicationDictItem o : adds) {
				o.setBundle(applicationDict.getId());
				o.setItemCategory(ItemCategory.portal_dict);
				o.setDistributeFactor(applicationDict.getDistributeFactor());
				o.setApplication(applicationDict.getApplication());
				emc.persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<ApplicationDictItem> adds = converter.disassemble(jsonElement, paths);
			for (ApplicationDictItem o : adds) {
				o.setBundle(applicationDict.getId());
				o.setItemCategory(ItemCategory.portal_dict);
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
		List<ApplicationDictItem> exists = business.applicationDictItem()
				.listWithApplicationDictWithPath(applicationDict.getId(), paths);
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
			for (ApplicationDictItem o : business.applicationDictItem()
					.listWithApplicationDictWithPathWithAfterLocation(applicationDict.getId(),
							NumberUtils.toInt(paths[position]), paths)) {
				o.path(Integer.toString(o.pathLocation(position) - 1), position);
			}
		}
	}
}
