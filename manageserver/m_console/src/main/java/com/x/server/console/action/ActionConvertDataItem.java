//package com.x.server.console.action;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import javax.persistence.criteria.Predicate;
//import javax.persistence.criteria.Root;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.x.base.core.container.EntityManagerContainer;
//import com.x.base.core.container.factory.EntityManagerContainerFactory;
//import com.x.base.core.entity.JpaObject;
//import com.x.base.core.entity.dataitem.ItemCategory;
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.config.DataMappings;
//import com.x.base.core.project.gson.XGsonBuilder;
//import com.x.base.core.project.tools.DateTools;
//import com.x.base.core.project.tools.StringTools;
//import com.x.query.core.entity.Item;
//
//public class ActionConvertDataItem {
//
//	private Date start;
//
//	private void init() throws Exception {
//		this.start = new Date();
//	}
//
//	public boolean execute(String password) throws Exception {
//		if (!StringUtils.equals(Config.token().getPassword(), password)) {
//			throw new Exception("password not match.");
//		}
//		this.init();
//		List<Class<?>> classes = new ArrayList<>();
//		classes.add(Item.class);
//		classes.add(com.x.processplatform.core.entity.content.DataItem.class);
//		classes.add(com.x.processplatform.core.entity.content.DataLobItem.class);
////		classes.add(com.x.cms.core.entity.content.DataLobItem.class);
////		classes.add(com.x.cms.core.entity.content.DataItem.class);
//		System.out.println("convert dataItem, start at " + DateTools.format(start) + ".");
//		DataMappings mappings = Config.dataMappings();
//		File orm = this.createPersistenceXml(classes, mappings);
//		EntityManagerContainerFactory.init(orm.getName());
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			this.convertProcessPlatform(emc);
//			// this.convertCms(emc);
//		}
//		System.out.println(
//				"convert dataItem, elapsed: " + (new Date().getTime() - start.getTime()) / 1000 / 60 + " minutes.");
//		return true;
//	}
//
//	/** 创建临时使用的persistence.xml 并复制到class目录下 */
//	private File createPersistenceXml(List<Class<?>> clsList, DataMappings mappings) throws Exception {
//		File dir = new File(Config.base(), "local/temp/classes");
//		File xml = new File(dir, StringTools.uniqueToken() + "_convertDataItem.xml");
//		PersistenceXmlHelper.createPersistenceXml(clsList, mappings, xml);
//		return xml;
//	}
//
//	private void convertProcessPlatform(EntityManagerContainer emc) throws Exception {
//		List<com.x.processplatform.core.entity.content.DataItem> os = this.list(emc,
//				com.x.processplatform.core.entity.content.DataItem.class, "", 10000);
//		int i = 0;
//		while (!os.isEmpty()) {
//			System.out.println("convert dataItem 正在处理ProcessPlatform的第 " + (++i) + " 个批次.");
//			List<Item> items = new ArrayList<>();
//			for (com.x.processplatform.core.entity.content.DataItem o : os) {
//				Item item = XGsonBuilder.convert(o, Item.class);
//				item.setItemCategory(ItemCategory.pp);
//				item.setBundle(o.getJob());
//				item.setStringValue(o.getStringValue());
//				if (o.isLobItem()) {
//					com.x.processplatform.core.entity.content.DataLobItem lob = emc.find(o.getLobItem(),
//							com.x.processplatform.core.entity.content.DataLobItem.class);
//					if (null != lob) {
//						item.setStringValue(lob.getData());
//					}
//				}
//				items.add(item);
//			}
//			emc.beginTransaction(Item.class);
//			for (Item o : items) {
//				emc.persist(o);
//			}
//			emc.commit();
//			os = this.list(emc, com.x.processplatform.core.entity.content.DataItem.class, os.get(os.size() - 1).getId(),
//					10000);
//		}
//	}
//
//	// private void convertCms(EntityManagerContainer emc) throws Exception {
//	// List<com.x.cms.core.entity.content.DataItem> os = this.list(emc,
//	// com.x.cms.core.entity.content.DataItem.class,
//	// "", 10000);
//	// int i = 0;
//	// while (!os.isEmpty()) {
//	// System.out.println("convert dataItem 正在处理Cms的第 " + (++i) + " 个批次.");
//	// List<Item> items = new ArrayList<>();
//	// for (com.x.cms.core.entity.content.DataItem o : os) {
//	// Item item = XGsonBuilder.convert(o, Item.class);
//	// item.setItemCategory(ItemCategory.cms);
//	// item.setBundle(o.getDocId());
//	// item.setStringValue(o.getStringValue());
//	// if (o.isLobItem()) {
//	// com.x.cms.core.entity.content.DataLobItem lob = emc.find(o.getLobItem(),
//	// com.x.cms.core.entity.content.DataLobItem.class);
//	// if (null != lob) {
//	// item.setStringValue(lob.getData());
//	// }
//	// }
//	// items.add(item);
//	// }
//	// emc.beginTransaction(Item.class);
//	// for (Item o : items) {
//	// emc.persist(o);
//	// }
//	// emc.commit();
//	// os = this.list(emc, com.x.cms.core.entity.content.DataItem.class,
//	// os.get(os.size() - 1).getId(), 10000);
//	// }
//	// }
//
//	private <T extends JpaObject> List<T> list(EntityManagerContainer emc, Class<T> cls, String id, Integer size)
//			throws Exception {
//		EntityManager em = emc.get(cls);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<T> cq = cb.createQuery(cls);
//		Root<T> root = cq.from(cls);
//		Predicate p = cb.conjunction();
//		if (StringUtils.isNotEmpty(id)) {
//			p = cb.greaterThan(root.get(JpaObject.id_FIELDNAME), id);
//		}
//		cq.select(root).where(p).orderBy(cb.asc(root.get(JpaObject.id_FIELDNAME)));
//		return em.createQuery(cq).setMaxResults(size).getResultList();
//	}
//}