package com.x.query.assemble.surface.jaxrs.view;

import java.io.ByteArrayOutputStream;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.CmsPlan;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.ProcessPlatformPlan;
import com.x.query.core.express.plan.Row;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.SelectEntry;

import net.sf.ehcache.Element;

abstract class BaseAction extends StandardJaxrsAction {

	protected Plan accessPlan(Business business, View view, Runtime runtime) throws Exception {
		Plan plan = null;
		if (BooleanUtils.isTrue(view.getCacheAccess())) {
			String cacheKey = ApplicationCache.concreteCacheKey(view.getId(), StringTools.sha(gson.toJson(runtime)));
			Element element = business.cache().get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				plan = (Plan) element.getObjectValue();
			} else {
				plan = this.getPlan(view, runtime);
				business.cache().put(new Element(cacheKey, plan));
			}
		} else {
			plan = this.getPlan(view, runtime);
		}
		return plan;
	}

	private Plan getPlan(View view, Runtime runtime) throws Exception {
		Plan plan = null;
		switch (StringUtils.trimToEmpty(view.getType())) {
		case View.TYPE_CMS:
			CmsPlan cmsPlan = gson.fromJson(view.getData(), CmsPlan.class);
			cmsPlan.runtime = runtime;
			cmsPlan.access();
			plan = cmsPlan;
			break;
		default:
			ProcessPlatformPlan processPlatformPlan = gson.fromJson(view.getData(), ProcessPlatformPlan.class);
			processPlatformPlan.runtime = runtime;
			processPlatformPlan.access();
			plan = processPlatformPlan;
			break;
		}
		plan.afterCalculateGridScriptText = null;
		plan.afterGridScriptText = null;
		plan.afterGroupGridScriptText = null;
		return plan;
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

	protected String girdWriteToExcel(EffectivePerson effectivePerson, Business business, Plan plan, View view)
			throws Exception {
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
						c.setCellValue(Objects.toString(row.get(o.column)));
						i++;
					}
//					for (Entry<String, Object> entry : row.data.entrySet()) {
//						c = r.createCell(i);
//						c.setCellValue(Objects.toString(entry.getValue(), ""));
//						i++;
//					}
				}
			}
			String name = view.getName() + ".xlsx";
			workbook.write(os);
			ExcelResultObject obj = new ExcelResultObject();
			obj.setBytes(os.toByteArray());
			obj.setName(name);
			obj.setPerson(effectivePerson.getDistinguishedName());
			String flag = StringTools.uniqueToken();
			business.cache().put(new Element(flag, obj));
			return flag;
		}
	}

	protected <T extends Runtime> void append(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		t.person = effectivePerson.getDistinguishedName();
		t.identityList = business.organization().identity().listWithPerson(effectivePerson);
		t.unitList = business.organization().unit().listWithPerson(effectivePerson);
		t.unitAllList = business.organization().unit().listWithPersonSupNested(effectivePerson);
		t.groupList = business.organization().group().listWithPerson(effectivePerson.getDistinguishedName());
		t.roleList = business.organization().role().listWithPerson(effectivePerson);
	}

}
