package com.x.report.assemble.control.jaxrs.workinfo;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks;
import com.x.report.assemble.control.dataadapter.strategy.CompanyStrategyWorks.WoCompanyStrategyWorks;

/**
 * 根据年份获取所有的战略举措配置信息列表
 * @author O2LEE
 *
 */
public class ActionListCompanyStrategyWorks extends BaseAction {
	
	protected ActionResult<List<WoCompanyStrategyWorks>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String year, String month ) throws Exception {
		ActionResult<List<WoCompanyStrategyWorks>> result = new ActionResult<>();
		CompanyStrategyWorks companyStrategyWorks = new CompanyStrategyWorks();
		List<WoCompanyStrategyWorks> wos = null;
		try {
			wos = companyStrategyWorks.all( year, month );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.setData( wos );
		return result;
	}
}