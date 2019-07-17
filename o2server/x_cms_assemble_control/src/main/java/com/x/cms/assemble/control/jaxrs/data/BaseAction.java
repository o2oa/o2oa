package com.x.cms.assemble.control.jaxrs.data;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.entity.dataitem.ItemType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.Document;
import com.x.query.core.entity.Item;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	private static final String title_path = "title";
	private static final String subject_path = "subject";
	
	protected Ehcache cache = ApplicationCache.instance().getCache( Item.class);
	protected UserManagerService userManagerService = new UserManagerService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentQueryService documentServiceAdv = new DocumentQueryService();
	
	JsonElement getData(Business business, String job, String... paths) throws Exception {
		JsonElement jsonElement = null;
		List<Item> list = business.itemFactory().listWithDocmentWithPath(job, paths);
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		jsonElement = converter.assemble(list, paths.length);
		return jsonElement;
	}

	/** 将data中的Title 和 serial 字段同步到document中 */
	void updateTitleSerial(Business business, Document document, JsonElement jsonElement) throws Exception {
		String title = XGsonBuilder.extractString(jsonElement, title_path);
		if (null == title) {
			title = XGsonBuilder.extractString(jsonElement, subject_path);
		}
		if (null != title && !Objects.equals(title, document.getTitle())) {
			business.entityManagerContainer().beginTransaction(Document.class);
			business.entityManagerContainer().beginTransaction(Item.class);
			if ((null != title) && (!Objects.equals(title, document.getTitle()))) {
				document.setTitle(title);
			}
			business.entityManagerContainer().commit();
		}
	}

	void updateData(Business business, Document document, JsonElement jsonElement, String... paths) throws Exception {
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		List<Item> exists = business.itemFactory().listWithDocmentWithPath(document.getId(), paths);
		List<Item> currents = converter.disassemble(jsonElement, paths);
		List<Item> removes = converter.subtract(exists, currents);
		List<Item> adds = converter.subtract(currents, exists);
		
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			business.entityManagerContainer().beginTransaction(Item.class);
			for (Item _o : removes) {
				business.entityManagerContainer().remove(_o);
			}
			for (Item _o : adds) {
				this.fill(_o, document);
				business.entityManagerContainer().persist(_o);
			}
			/* 基于前面的原因,这里进行单独提交 */
			business.entityManagerContainer().commit();
		}
	}

	void createData(Business business, Document document, JsonElement jsonElement, String... paths) throws Exception {
		String[] parentPaths = new String[] { "", "", "", "", "", "", "", "" };
		String[] cursorPaths = new String[] { "", "", "", "", "", "", "", "" };
		for (int i = 0; i < paths.length - 1; i++) {
			parentPaths[i] = paths[i];
			cursorPaths[i] = paths[i];
		}
		cursorPaths[paths.length - 1] = paths[paths.length - 1];
		Item parent = business.itemFactory().getWithDocmentWithPath(document.getId(), parentPaths[0], parentPaths[1], parentPaths[2],
				parentPaths[3], parentPaths[4], parentPaths[5], parentPaths[6], parentPaths[7]);
		if (null == parent) {
			throw new Exception("parent not existed.");
		}
		Item cursor = business.itemFactory().getWithDocmentWithPath(document.getId(), cursorPaths[0], cursorPaths[1], cursorPaths[2],
				cursorPaths[3], cursorPaths[4], cursorPaths[5], cursorPaths[6], cursorPaths[7]);
		DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
		business.entityManagerContainer().beginTransaction(Item.class);
		if ((null != cursor) && cursor.getItemType().equals(ItemType.a)) {
			/* 向数组里面添加一个成员对象 */
			Integer index = business.itemFactory().getArrayLastIndexWithDocmentWithPath(document.getId(), paths);
			/* 新的路径开始 */
			String[] ps = new String[paths.length + 1];
			for (int i = 0; i < paths.length; i++) {
				ps[i] = paths[i];
			}
			ps[paths.length] = Integer.toString(index + 1);
			List<Item> adds = converter.disassemble(jsonElement, ps);
			for (Item o : adds) {
				this.fill(o, document);
				business.entityManagerContainer().persist(o);
			}
		} else if ((cursor == null) && parent.getItemType().equals(ItemType.o)) {
			/* 向parent对象添加一个属性值 */
			List<Item> adds = converter.disassemble(jsonElement, paths);
			for (Item o : adds) {
				this.fill(o, document);
				business.entityManagerContainer().persist(o);
			}
		} else {
			throw new Exception("unexpected post data with document" + document + ".path:" + StringUtils.join(paths, ".")
					+ "json:" + jsonElement);
		}
	}

	@SuppressWarnings("deprecation")
	void deleteData(Business business, Document document, String... paths) throws Exception {
		List<Item> exists = business.itemFactory().listWithDocmentWithPath(document.getId(), paths);
		if (exists.isEmpty()) {
			throw new Exception( "data{docId:" + document.getId() + "} on path:" + StringUtils.join(paths, ".") + " is not existed.");
		}
		business.entityManagerContainer().beginTransaction(Item.class);
		for (Item o : exists) {
			business.entityManagerContainer().remove(o);
		}
		if ( paths.length > 0 ) {
			if ( NumberUtils.isNumber(paths[paths.length - 1])) {
				int position = paths.length - 1;
				for (Item o : business.itemFactory().listWithDocmentWithPathWithAfterLocation(document.getId(),
						NumberUtils.toInt(paths[position]), paths)) {
					o.path(Integer.toString(o.pathLocation(position) - 1), position);
				}
			}
		}
	}

	void fill(Item o, Document document) {
		/** 将DateItem与Document放在同一个分区 */
		o.setDistributeFactor(document.getDistributeFactor());
		o.setBundle(document.getId());
		o.setItemCategory(ItemCategory.cms);
	}
	
}
