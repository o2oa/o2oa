package com.x.cms.assemble.control.jaxrs.appdict;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;

abstract class BaseAction extends StandardJaxrsAction {

	static WrapCopier<AppDict, WrapOutAppDict> copier = WrapCopierFactory.wo(AppDict.class,
			WrapOutAppDict.class, null, WrapOutAppDict.Excludes);

	JsonElement get(Business business, AppDict appDict, String... paths) throws Exception {
		List<AppDictItem> list = business.getAppDictItemFactory().listWithAppDictWithPath(appDict.getId(), paths);
		DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
		JsonElement jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	void update(Business business, AppDict appDict, JsonElement jsonElement, String... paths)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
		List<AppDictItem> exists = business.getAppDictItemFactory()
				.listWithAppDictWithPath(appDict.getId(), paths);
		if (exists.isEmpty()) {
			throw new Exception("appDict{id:" + appDict + "} on path:" + StringUtils.join(paths, ".")
					+ " is not existed.");
		}
		emc.beginTransaction(AppDictItem.class);
		// emc.beginTransaction(AppDictLobItem.class);
		List<AppDictItem> currents = converter.disassemble(jsonElement, paths);
		List<AppDictItem> removes = converter.subtract(exists, currents);
		List<AppDictItem> adds = converter.subtract(currents, exists);
		for (AppDictItem o : removes) {
			emc.remove(o);
		}
		for (AppDictItem o : adds) {
			o.setBundle(appDict.getId());
			o.setItemCategory(ItemCategory.pp_dict);
			o.setDistributeFactor(appDict.getDistributeFactor());
			o.setAppId(appDict.getAppId());
			emc.persist(o);
		}
	}

	void create(Business business, AppDict appDict, JsonElement jsonElement, String... paths)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		AppDictItem parent = business.getAppDictItemFactory().getWithAppDictWithPath(
				appDict.getId(), parentPaths[0], parentPaths[1], parentPaths[2], parentPaths[3], parentPaths[4],
				parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		AppDictItem cursor = business.getAppDictItemFactory().getWithAppDictWithPath(
				appDict.getId(), cursorPaths[0], cursorPaths[1], cursorPaths[2], cursorPaths[3], cursorPaths[4],
				cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		DataItemConverter<AppDictItem> converter = new DataItemConverter<>(AppDictItem.class);
		emc.beginTransaction(AppDictItem.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.getAppDictItemFactory()
					.getArrayLastIndexWithAppDictWithPath(appDict.getId(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<AppDictItem> adds = converter.disassemble(jsonElement, ps);
			for (AppDictItem o : adds) {
				o.setBundle(appDict.getId());
				o.setItemCategory(ItemCategory.pp_dict);
				o.setDistributeFactor(appDict.getDistributeFactor());
				o.setAppId(appDict.getAppId());
				emc.persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<AppDictItem> adds = converter.disassemble(jsonElement, paths);
			for (AppDictItem o : adds) {
				o.setBundle(appDict.getId());
				o.setItemCategory(ItemCategory.pp_dict);
				o.setDistributeFactor(appDict.getDistributeFactor());
				o.setAppId(appDict.getAppId());
				emc.persist(o);
			}
		} else {
			throw new Exception("unexpected post with appDict{id:" + appDict + "} path:"
					+ StringUtils.join(paths, ".") + "json:" + jsonElement);
		}
	}

	@SuppressWarnings("deprecation")
	void delete(Business business, AppDict appDict, String... paths) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<AppDictItem> exists = business.getAppDictItemFactory()
				.listWithAppDictWithPath(appDict.getId(), paths);
		if (exists.isEmpty()) {
			throw new Exception("appDict{id:" + appDict + "} on path:" + StringUtils.join(paths, ".")
					+ " is not existed.");
		}
		emc.beginTransaction(AppDictItem.class);
		for (AppDictItem o : exists) {
			emc.remove(o);
		}
		if (NumberUtils.isNumber(paths[paths.length - 1])) {
			int position = paths.length - 1;
			for (AppDictItem o : business.getAppDictItemFactory()
					.listWithAppDictWithPathWithAfterLocation(appDict.getId(),
							NumberUtils.toInt(paths[position]), paths)) {
				o.path(Integer.toString(o.pathLocation(position) - 1), position);
			}
		}
	}
}
