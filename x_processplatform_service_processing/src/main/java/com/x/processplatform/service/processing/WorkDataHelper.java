package com.x.processplatform.service.processing;

import java.util.ArrayList;
import java.util.Date;
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
import com.x.base.core.entity.item.ItemConverter;
import com.x.base.core.gson.XGsonBuilder;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataLobItem;
import com.x.processplatform.core.entity.content.Work;

public class WorkDataHelper {

	private String job;
	private String title;
	private String creatorPerson;
	private String creatorIdentity;
	private String creatorDepartment;
	private String creatorCompany;
	private String application;
	private String applicationName;
	private String applicationAlias;
	private String process;
	private String processName;
	private String processAlias;
	private String serial;
	private Date startTime;
	private String startTimeMonth;
	private Date completedTime;
	private String completedTimeMonth;

	private EntityManagerContainer emc;
	private ItemConverter<DataItem> converter;
	private List<DataItem> items;
	private Gson gson;

	public WorkDataHelper(EntityManagerContainer emc, Work work) throws Exception {
		if ((null == emc) || (null == work)) {
			throw new Exception("create instance error.");
		}
		this.job = work.getJob();
		this.title = work.getTitle();
		this.creatorPerson = work.getCreatorPerson();
		this.creatorIdentity = work.getCreatorIdentity();
		this.creatorDepartment = work.getCreatorDepartment();
		this.creatorCompany = work.getCreatorCompany();
		this.application = work.getApplication();
		this.applicationName = work.getApplicationName();
		this.applicationAlias = work.getApplicationAlias();
		this.process = work.getProcess();
		this.processName = work.getProcessName();
		this.processAlias = work.getProcessAlias();
		this.serial = work.getSerial();
		this.startTime = work.getStartTime();
		this.startTimeMonth = work.getStartTimeMonth();
		this.completedTime = null;
		this.completedTimeMonth = null;
		// this.completed = false;
		if (StringUtils.isEmpty(this.job)) {
			throw new Exception("can not create DataHelper job is empty.");
		}
		if (StringUtils.isEmpty(this.process)) {
			throw new Exception("can not create DataHelper process is empty.");
		}
		if (StringUtils.isEmpty(this.application)) {
			throw new Exception("can not create DataHelper application is empty.");
		}
		if (null == this.startTime) {
			throw new Exception("can not create DataHelper startTime is null.");
		}
		this.emc = emc;
		this.converter = new ItemConverter<DataItem>(DataItem.class);
		this.gson = XGsonBuilder.instance();
		this.items = this.load();
	}

	private List<DataItem> load() throws Exception {
		EntityManager em = emc.get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Path<String> path = root.get("job");
		Predicate p = cb.equal(path, this.job);
		List<DataItem> list = em.createQuery(cq.where(p)).getResultList();
		for (DataItem o : list) {
			if (o.isLobItem()) {
				DataLobItem lob = emc.find(o.getLobItem(), DataLobItem.class);
				if (null != lob) {
					o.setStringLobValue(lob.getData());
				}
			}
		}
		return list;
	}

	public Data get() throws Exception {
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
	}

	public void update(JsonElement jsonElement) throws Exception {
		List<DataItem> currents = converter.disassemble(jsonElement);
		List<DataItem> removes = converter.subtract(items, currents);
		List<DataItem> adds = converter.subtract(currents, items);
		if ((!removes.isEmpty()) || (!adds.isEmpty())) {
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			if ((!removes.isEmpty())) {
				for (DataItem o : removes) {
					if (o.isLobItem()) {
						DataLobItem lob = emc.find(o.getLobItem(), DataLobItem.class);
						if (null != lob) {
							emc.remove(lob);
						}
					}
					emc.remove(o);
				}
			}
			if ((!adds.isEmpty())) {
				for (DataItem o : adds) {
					this.fill(o);
					if (o.isLobItem()) {
						DataLobItem lob = new DataLobItem();
						lob.setData(o.getStringLobValue());
						lob.setDistributeFactor(o.getDistributeFactor());
						o.setLobItem(lob.getId());
						emc.persist(lob);
					}
					emc.persist(o);
				}
			}
			List<DataItem> list = new ArrayList<>();
			list = converter.subtract(items, removes);
			list.addAll(adds);
			converter.sort(list);
			items = list;
		}
	}

	public void completed(Date date) throws Exception {
		emc.beginTransaction(DataItem.class);
		for (DataItem o : items) {
			o.setCompleted(true);
			o.setCompletedTime(date);
		}
	}

	public void update(Data data) throws Exception {
		JsonElement jsonElement = gson.toJsonTree(data);
		this.update(jsonElement);
	}

	public void remove() throws Exception {
		if ((!items.isEmpty())) {
			emc.beginTransaction(DataItem.class);
			emc.beginTransaction(DataLobItem.class);
			for (DataItem o : items) {
				if (o.isLobItem()) {
					DataLobItem lob = emc.find(o.getLobItem(), DataLobItem.class);
					if (null != lob) {
						emc.remove(lob);
					}
				}
				emc.remove(o);
			}
		}
	}

	public void fill(DataItem o) throws Exception {
		o.setJob(this.job);
		o.setSerial(this.serial);
		o.setTitle(this.title);
		o.setApplication(this.application);
		o.setApplicationName(this.applicationName);
		o.setApplicationAlias(this.applicationAlias);
		o.setProcess(this.process);
		o.setProcessName(this.processName);
		o.setProcessAlias(this.processAlias);
		o.setCreatorCompany(this.creatorCompany);
		o.setCreatorDepartment(this.creatorDepartment);
		o.setCreatorIdentity(this.creatorIdentity);
		o.setCreatorPerson(this.creatorPerson);
		o.setStartTime(this.startTime);
		o.setStartTimeMonth(this.startTimeMonth);
		o.setCompletedTime(this.completedTime);
		o.setCompletedTimeMonth(this.completedTimeMonth);
		o.setCompleted(false);
	}
}
