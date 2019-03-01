package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class WorkDataHelper {

	private String job;
	private Integer distributeFactor;

	private EntityManagerContainer emc;
	private DataItemConverter<Item> converter;
	private List<Item> items;
	private Gson gson;

	public WorkDataHelper(EntityManagerContainer emc, Work work) throws Exception {
		if ((null == emc) || (null == work)) {
			throw new Exception("create instance error.");
		}
		this.job = work.getJob();
		this.distributeFactor = work.getDistributeFactor();
		if (StringUtils.isEmpty(this.job)) {
			throw new Exception("can not create DataHelper job is empty.");
		}
		this.emc = emc;
		this.converter = new DataItemConverter<Item>(Item.class);
		this.gson = XGsonBuilder.instance();
		this.items = this.load();
	}

	private List<Item> load() throws Exception {
		EntityManager em = emc.get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Path<String> path = root.get(Item.bundle_FIELDNAME);
		Predicate p = cb.equal(path, this.job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		List<Item> list = em.createQuery(cq.where(p)).getResultList();
		return list;
	}

	public Data get() throws Exception {
		try {
			if (this.items.isEmpty()) {
				return new Data();
			} else {
				JsonElement jsonElement = this.converter.assemble(items);
				if (jsonElement.isJsonObject()) {
					return gson.fromJson(jsonElement, Data.class);
				} else {
					/* 如果不是Object强制返回一个Map对象 */
					return new Data();
				}
			}
		} catch (Exception e) {
			throw new ExceptionDataConvert(e, this.job);
		}
	}

	public boolean update(JsonElement jsonElement) throws Exception {
		if (jsonElement.isJsonNull()) {
			// throw new Exception("can not update data null.");
			/** 如果是空数据就不更新,避免数据被清空 */
			return false;
		}
		if (jsonElement.isJsonPrimitive()) {
			// throw new Exception("can not update data primitive.");
			/** 如果是空数据就不更新,避免数据被清空 */
			return false;
		}
		if (jsonElement.isJsonObject()) {
			if (jsonElement.getAsJsonObject().size() == 0) {
				// throw new Exception("can not update data object size ==0.");
				/** 如果是空数据就不更新,避免数据被清空 */
				return false;
			}
		}
		if (jsonElement.isJsonArray()) {
			if (jsonElement.getAsJsonArray().size() == 0) {
				// throw new Exception("can not update data array size ==0.");
				/** 如果是空数据就不更新,避免数据被清空 */
				return false;
			}
		}
		List<Item> currents = converter.disassemble(jsonElement);
		List<Item> removes = converter.subtract(items, currents);
		List<Item> adds = converter.subtract(currents, items);
		if ((currents.size() != 0) && (currents.size() == removes.size()) && adds.size() == 0) {
			throw new ExceptionWorkDataWillBeEmpty(job);
		}
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			emc.beginTransaction(Item.class);
			if ((!removes.isEmpty())) {
				for (Item o : removes) {
					emc.remove(o);
				}
			}
			if ((!adds.isEmpty())) {
				for (Item o : adds) {
					this.fill(o);
					emc.persist(o);
				}
			}
			List<Item> list = new ArrayList<>();
			list = converter.subtract(items, removes);
			list.addAll(adds);
			converter.sort(list);
			items = list;
			return true;
		} else {
			return false;
		}
	}

	public boolean update(Data data) throws Exception {
		JsonElement jsonElement = gson.toJsonTree(data);
		return this.update(jsonElement);
	}

	public void remove() throws Exception {
		if ((!items.isEmpty())) {
			emc.beginTransaction(Item.class);
			for (Item o : items) {
				emc.remove(o);
			}
		}
	}

	public void fill(Item o) throws Exception {
		o.setDistributeFactor(this.distributeFactor);
		o.setBundle(this.job);
		o.setItemCategory(ItemCategory.pp);
	}

}