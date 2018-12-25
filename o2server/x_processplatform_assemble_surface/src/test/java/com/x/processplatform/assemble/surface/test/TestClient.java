package com.x.processplatform.assemble.surface.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.junit.Test;

import com.x.base.core.project.bean.NameIdPair;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.processplatform.core.entity.query.CalculateEntry;
import com.x.processplatform.core.entity.query.CalculateType;
import com.x.processplatform.core.entity.query.DateEffectType;
import com.x.processplatform.core.entity.query.DateRangeEntry;
import com.x.processplatform.core.entity.query.DateRangeType;
import com.x.processplatform.core.entity.query.FilterEntry;
import com.x.processplatform.core.entity.query.GroupEntry;
import com.x.processplatform.core.entity.query.OrderEntry;
import com.x.processplatform.core.entity.query.OrderType;
import com.x.processplatform.core.entity.query.Query;
import com.x.processplatform.core.entity.query.SelectEntry;
import com.x.processplatform.core.entity.query.SelectType;

public class TestClient {
	@Test
	public void test1() {
		Query query = new Query();
		query.getWhereEntry().getApplicationList().add(new NameIdPair("测试应用", "0c2c3d76-5301-4918-8869-f88de8d2fe57"));
		query.getRestrictWhereEntry().getApplicationList()
				.add(new NameIdPair("测试应用", "0c2c3d76-5301-4918-8869-f88de8d2fe57"));
		DateRangeEntry dateRangeEntry1 = new DateRangeEntry();
		dateRangeEntry1.setDateEffectType(DateEffectType.start);
		dateRangeEntry1.setYear("2016");
		dateRangeEntry1.setDateRangeType(DateRangeType.year);
		query.setDateRangeEntry(dateRangeEntry1);
		DateRangeEntry dateRangeEntry2 = new DateRangeEntry();
		dateRangeEntry2.setDateEffectType(DateEffectType.start);
		dateRangeEntry2.setYear("2016");
		dateRangeEntry2.setDateRangeType(DateRangeType.year);
		query.setRestrictDateRangeEntry(dateRangeEntry2);
		SelectEntry selectEntry1 = new SelectEntry();
		SelectEntry selectEntry2 = new SelectEntry();
		SelectEntry selectEntry3 = new SelectEntry();
		SelectEntry selectEntry4 = new SelectEntry();
		SelectEntry selectEntry5 = new SelectEntry();
		SelectEntry selectEntry6 = new SelectEntry();
		selectEntry1.setSelectType(SelectType.attribute);
		selectEntry1.setAttribute("title");
		selectEntry1.setColumn("atitle");
		selectEntry1.setDisplayName("标题");
		selectEntry2.setSelectType(SelectType.attribute);
		selectEntry2.setAttribute("creatorPerson");
		selectEntry2.setColumn("acreatorPerson");
		selectEntry2.setDisplayName("创建人");
		selectEntry3.setSelectType(SelectType.path);
		selectEntry3.setPath("subject");
		selectEntry3.setColumn("psubject");
		selectEntry3.setDisplayName("主题");
		selectEntry4.setSelectType(SelectType.path);
		selectEntry4.setPath("amonut");
		selectEntry4.setColumn("pamonut");
		selectEntry4.setDisplayName("金额");
		selectEntry5.setSelectType(SelectType.path);
		selectEntry5.setPath("phone");
		selectEntry5.setColumn("pphone");
		selectEntry5.setDisplayName("电话");
		selectEntry6.setSelectType(SelectType.path);
		selectEntry6.setPath("slDate");
		selectEntry6.setColumn("pslDate");
		selectEntry6.setDisplayName("日期");
		query.getSelectEntryList().add(selectEntry1);
		query.getSelectEntryList().add(selectEntry2);
		query.getSelectEntryList().add(selectEntry3);
		query.getSelectEntryList().add(selectEntry4);
		query.getSelectEntryList().add(selectEntry5);
		query.getSelectEntryList().add(selectEntry6);
		FilterEntry filterEntry1 = new FilterEntry();
		filterEntry1.setComparison("equals");
		filterEntry1.setValue("aaaaa");
		filterEntry1.setPath("city.name");
		filterEntry1.setLogic("and");
		query.getFilterEntryList().add(filterEntry1);
		FilterEntry filterEntry2 = new FilterEntry();
		filterEntry2.setComparison("notEquals");
		filterEntry2.setValue("bbbbb");
		filterEntry2.setPath("city.title");
		filterEntry2.setLogic("and");
		query.getRestrictFilterEntryList().add(filterEntry2);

		CalculateEntry calculateEntry1 = new CalculateEntry();
		CalculateEntry calculateEntry2 = new CalculateEntry();
		CalculateEntry calculateEntry3 = new CalculateEntry();
		CalculateEntry calculateEntry4 = new CalculateEntry();
		CalculateEntry calculateEntry5 = new CalculateEntry();
		CalculateEntry calculateEntry6 = new CalculateEntry();
		calculateEntry1.setCalculateType(CalculateType.sum);
		calculateEntry1.setColumn("pamount");
		calculateEntry2.setCalculateType(CalculateType.average);
		calculateEntry2.setColumn("pamount");
		calculateEntry3.setCalculateType(CalculateType.count);
		calculateEntry3.setColumn("pamount");
		// calculateEntry4.setCalculateType(CalculateType.groupSum);
		// calculateEntry4.setColumn("pamount");
		// calculateEntry5.setCalculateType(CalculateType.groupAverage);
		// calculateEntry5.setColumn("pamount");
		// calculateEntry6.setCalculateType(CalculateType.groupCount);
		calculateEntry6.setColumn("pamount");
		// query.getCalculateEntryList().add(calculateEntry1);
		// query.getCalculateEntryList().add(calculateEntry2);
		// query.getCalculateEntryList().add(calculateEntry3);
		// query.getCalculateEntryList().add(calculateEntry4);
		// query.getCalculateEntryList().add(calculateEntry5);
		// query.getCalculateEntryList().add(calculateEntry6);
		GroupEntry groupEntry = new GroupEntry();
		groupEntry.setColumn("pphone");
		groupEntry.setOrderType(OrderType.desc);
		query.setGroupEntry(groupEntry);
		OrderEntry orderEntry1 = new OrderEntry();
		orderEntry1.setColumn("pphone");
		orderEntry1.setOrderType(OrderType.asc);
		OrderEntry orderEntry2 = new OrderEntry();
		orderEntry2.setColumn("pslDate");
		orderEntry2.setOrderType(OrderType.desc);
		query.getOrderEntryList().add(orderEntry1);
		query.getOrderEntryList().add(orderEntry2);
		System.out.println(query.toString());
	}

	@Test
	public void test2() throws Exception {
		Map<String, Object> map1 = new HashMap<>();
		map1.put("title", "aaaa");
		map1.put("order", 1);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("title", "aaaa");
		map2.put("order", 2);
		Map<String, Object> map3 = new HashMap<>();
		map3.put("title", "bbbb");
		map3.put("order", 3);
		Map<String, Object> map4 = new HashMap<>();
		map4.put("title", "bbbb");
		map4.put("order", 4);
		List<Map<String, Object>> list = new ArrayList<>();
		list.add(map1);
		list.add(map2);
		list.add(map3);
		list.add(map4);
		List<OrderEntry> attributes = new ArrayList<>();
		OrderEntry order1 = new OrderEntry();
		OrderEntry order2 = new OrderEntry();
		order1.setColumn("title");
		order1.setOrderType(OrderType.desc);
		order2.setColumn("order");
		order2.setOrderType(OrderType.asc);
		attributes.add(order1);
		attributes.add(order2);
		list = list.stream().sorted((o1, o2) -> compareWith(o1, o2, attributes)).collect(Collectors.toList());
		System.out.println(XGsonBuilder.toJson(list));
	}

	public static int compareWith(Map<String, Object> o1, Map<String, Object> o2, List<OrderEntry> orderEntries) {
		CompareToBuilder compareToBuilder = new CompareToBuilder();
		for (OrderEntry en : orderEntries) {
			if (Objects.equals(OrderType.asc, en.getOrderType())) {
				compareToBuilder.append(o1.get(en.getColumn()), o2.get(en.getColumn()));
			} else {
				compareToBuilder.append(o2.get(en.getColumn()), o1.get(en.getColumn()));
			}
		}
		return compareToBuilder.toComparison();
	}

	@Test
	public void test3() throws Exception {
		String addr = "http://dev.ray.local:20020/x_processplatform_assemble_surface/jaxrs/work/process/3fb84f9b-0b04-49d2-9e6d-db44abca3528?v=0.4.2&jgj06idw";
		NameValuePair p = new NameValuePair("x-token", "TGzu9RzlNSLTGmXpL-MtJWt3UM2XzDf1lLLI6RVFFlXvQ_2S-AT8jQ");
		List<NameValuePair> heads = new ArrayList<>();
		heads.add(p);
		for (int i = 0; i < 100000; i++) {
			System.out.println(i);
			HttpConnection.postAsString(addr, heads, "{title: \"数据测试流程一-无标题\", identity: \"zr\"}");
		}
	}

	@Test
	public void test4() throws Exception {
		File file = new File("D:/导出");
		for (File o : FileUtils.listFiles(file, FalseFileFilter.FALSE, TrueFileFilter.TRUE)) {
			System.out.println(o.getAbsolutePath());
		}

	}
}