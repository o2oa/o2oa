package com.x.query.assemble.surface.jaxrs.view;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.core.entity.GeneralFile;
import com.x.processplatform.core.entity.element.Process;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Row;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.SelectEntry;

abstract class BaseAction extends StandardJaxrsAction {

	protected Plan accessPlan(Business business, View view, Runtime runtime, ExecutorService threadPool)
			throws Exception {
		Plan plan = null;
		if (BooleanUtils.isTrue(view.getCacheAccess())) {
			CacheKey cacheKey = new CacheKey("accessPlan", view.getId(), StringTools.sha(gson.toJson(runtime)));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				plan = (Plan) optional.get();
			} else {
				plan = this.dealPlan(view, runtime, threadPool);
				CacheManager.put(business.cache(), cacheKey, plan);
			}
		} else {
			plan = this.dealPlan(view, runtime, threadPool);
		}
		return plan;
	}

	private Plan dealPlan(View view, Runtime runtime, ExecutorService threadPool) throws Exception {
		Plan plan = null;
		switch (StringUtils.trimToEmpty(view.getType())) {
		case View.TYPE_CMS:
			CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
			cmsPlan.init(runtime, threadPool);
			cmsPlan.access();
			plan = cmsPlan;
			break;
		default:
			ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
			this.setProcessEdition(processPlatformPlan);
			processPlatformPlan.init(runtime, threadPool);
			processPlatformPlan.access();
			plan = processPlatformPlan;
			break;
		}
		plan.afterCalculateGridScriptText = null;
		plan.afterGridScriptText = null;
		plan.afterGroupGridScriptText = null;
		return plan;
	}

	private void setProcessEdition(ProcessPlatformPlan processPlatformPlan) throws Exception {
		if (!processPlatformPlan.where.processList.isEmpty()) {
			List<String> processIds = ListTools.extractField(processPlatformPlan.where.processList,
					JpaObject.id_FIELDNAME, String.class, true, true);
			List<Process> processList;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				processList = business.process().listObjectWithProcess(processIds, true);
			}
			List<ProcessPlatformPlan.WhereEntry.ProcessEntry> listProcessEntry = gson.fromJson(gson.toJson(processList),
					new TypeToken<List<ProcessPlatformPlan.WhereEntry.ProcessEntry>>() {
					}.getType());
			if (!listProcessEntry.isEmpty()) {
				processPlatformPlan.where.processList = listProcessEntry;
			}
		}
	}

	private List<String> dealBundle(Business business, View view, Runtime runtime, ExecutorService threadPool)
			throws Exception {
		List<String> os = null;
		switch (StringUtils.trimToEmpty(view.getType())) {
		case View.TYPE_CMS:
			CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
			cmsPlan.init(runtime, threadPool);
			os = cmsPlan.fetchBundles();
			break;
		default:
			ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
			this.setProcessEdition(processPlatformPlan);
			processPlatformPlan.init(runtime, threadPool);
			os = processPlatformPlan.fetchBundles();
			break;
		}
		return os;
	}

	protected List<String> fetchBundle(Business business, View view, Runtime runtime, ExecutorService threadPool)
			throws Exception {
		List<String> os = null;
		if (BooleanUtils.isTrue(view.getCacheAccess())) {
			CacheKey cacheKey = new CacheKey("fetchBundle", view.getId(), StringTools.sha(gson.toJson(runtime)));
			Optional<?> optional = CacheManager.get(business.cache(), cacheKey);
			if (optional.isPresent()) {
				os = (List<String>) optional.get();
			} else {
				os = this.dealBundle(business, view, runtime, threadPool);
				CacheManager.put(business.cache(), cacheKey, os);
			}
		} else {
			os = this.dealBundle(business, view, runtime, threadPool);
		}
		return os;
	}

	public static class ExcelResultObject extends GsonPropertyObject {

		private byte[] bytes;
		private String name;
		private String person;

		public byte[] getBytes() {
			return bytes;
		}

		public void setBytes(byte[] bytes) {
			this.bytes = bytes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	protected String girdWriteToExcel(EffectivePerson effectivePerson, Business business, Plan plan, View view,
			String excelName) throws Exception {
		try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			XSSFSheet sheet = workbook.createSheet("grid");
			if (ListTools.isNotEmpty(plan.selectList)) {
				XSSFRow r = sheet.createRow(0);
				XSSFCell c = null;
				int i = 0;
				for (SelectEntry o : plan.selectList) {
					c = r.createCell(i);
					c.setCellValue(o.getDisplayName());
					i++;
				}
			}
			if (null != plan.grid) {
				Row row = null;
				XSSFRow r = null;
				XSSFCell c = null;
				int i = 0;
				for (int j = 0; j < plan.grid.size(); j++) {
					row = plan.grid.get(j);
					r = sheet.createRow(j + 1);
					i = 0;
					for (SelectEntry o : plan.selectList) {
						c = r.createCell(i);
						c.setCellValue(objectToString(row.find(o.column)));
						i++;
					}
				}
			}
			if (StringUtils.isEmpty(excelName)) {
				excelName = view.getName() + ".xlsx";
			}
			if (!excelName.toLowerCase().endsWith(".xlsx")) {
				excelName = excelName + ".xlsx";
			}
			workbook.write(os);
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
				GeneralFile generalFile = new GeneralFile(gfMapping.getName(), excelName,
						effectivePerson.getDistinguishedName());
				generalFile.saveContent(gfMapping, os.toByteArray(), excelName);
				emc.beginTransaction(GeneralFile.class);
				emc.persist(generalFile, CheckPersistType.all);
				emc.commit();
				return generalFile.getId();
			}
		}
	}

	protected String objectToString(Object object) {
		String str = "";
		if (object instanceof Integer) {
			str = object.toString();
		} else if (object instanceof Double) {
			str = object.toString();
		} else if (object instanceof Float) {
			str = object.toString();
		} else if (object instanceof Boolean) {
			str = String.valueOf(object);
		} else if (object instanceof Date) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			str = formatter.format(object);
		} else if (object instanceof List){
			str = StringUtils.join((List)object, ",");
		} else {
			str = object.toString();
		}
		return str;
	}

	protected Runtime runtime(EffectivePerson effectivePerson, Business business, View view,
			List<FilterEntry> filterList, Map<String, String> parameter, Integer count, boolean isBundle)
			throws Exception {
		Runtime runtime = new Runtime();
		runtime.person = effectivePerson.getDistinguishedName();
		runtime.identityList = business.organization().identity().listWithPerson(effectivePerson);
		List<String> list = new ArrayList<>();
		if (runtime.identityList != null) {
			for (String identity : runtime.identityList) {
				if (identity.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(identity, "@"));
				}
			}
			runtime.identityList.addAll(list);
			list.clear();
		}
		runtime.unitList = business.organization().unit().listWithPerson(effectivePerson);
		if (runtime.unitList != null) {
			for (String item : runtime.unitList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.unitList.addAll(list);
			list.clear();
		}
		runtime.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		if (runtime.unitAllList != null) {
			for (String item : runtime.unitAllList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.unitAllList.addAll(list);
			list.clear();
		}
		runtime.groupList = business.organization().group()
				.listWithPersonReference(ListTools.toList(effectivePerson.getDistinguishedName()), true, true, true);
		if (runtime.groupList != null) {
			for (String item : runtime.groupList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.groupList.addAll(list);
			list.clear();
		}
		runtime.roleList = business.organization().role().listWithPerson(effectivePerson);
		if (runtime.roleList != null) {
			for (String item : runtime.roleList) {
				if (item.indexOf("@") > -1) {
					list.add(StringUtils.substringAfter(item, "@"));
				}
			}
			runtime.roleList.addAll(list);
			list.clear();
		}
		runtime.parameter = parameter;
		runtime.filterList = filterList;
		runtime.count = this.getCount(view, count, isBundle);
		return runtime;
	}

	protected Integer getCount(View view, Integer count, boolean isBundle) {
		Integer viewCount = view.getCount();
		if (isBundle) {
			if (viewCount == null || viewCount < 1) {
				viewCount = View.MAX_COUNT;
			}
			return ((count == null) || (count < 1)) ? viewCount : count;
		} else {
			Integer wiCount = ((count == null) || (count < 1) || (count > View.MAX_COUNT)) ? View.MAX_COUNT : count;
			return NumberUtils.min(viewCount, wiCount);
		}
	}

}
