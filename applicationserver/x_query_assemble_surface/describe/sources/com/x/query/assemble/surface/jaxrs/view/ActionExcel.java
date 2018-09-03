package com.x.query.assemble.surface.jaxrs.view;

import java.io.ByteArrayOutputStream;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.entity.plan.Plan;
import com.x.query.core.entity.plan.Row;
import com.x.query.core.entity.plan.Runtime;
import com.x.query.core.entity.plan.SelectEntry;

import net.sf.ehcache.Element;

class ActionExcel extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionExcel.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				XSSFWorkbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			View view = business.pick(id, View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(id, View.class);
			}
			Query query = business.pick(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(view.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			/** 有可能前台不传任何参数 */
			if (null == wi) {
				wi = new Wi();
			}
			this.append(effectivePerson, business, wi);
			Plan plan = this.accessPlan(business, view, wi);
			String excelFlag = this.girdWriteToExcel(effectivePerson, business, plan, view);
			Wo wo = new Wo();
			wo.setId(excelFlag);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Runtime {

	}

}