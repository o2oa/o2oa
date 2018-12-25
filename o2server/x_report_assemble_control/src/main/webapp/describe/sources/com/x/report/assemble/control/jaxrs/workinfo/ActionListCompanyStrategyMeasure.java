package com.x.report.assemble.control.jaxrs.workinfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyMeasure.WoCompanyStrategy;

/**
 * 根据年份获取所有的战略举措配置信息列表
 * @author O2LEE
 *
 */
public class ActionListCompanyStrategyMeasure extends BaseAction {
	
	protected ActionResult<List<WoCompanyStrategy>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year ) throws Exception {
		ActionResult<List<WoCompanyStrategy>> result = new ActionResult<>();
		CompanyStrategyMeasure companyStrategyMeasure = new CompanyStrategyMeasure();
		List<WoCompanyStrategy> wos = null;
		try {
			wos = companyStrategyMeasure.all( year );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setData( wos );
		return result;
	}
}