package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportFilter;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWrapInConvert;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkReportQueryService;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionListDraftPrevWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListDraftPrevWithFilter.class );
	
	protected ActionResult<List<WoOkrWorkReportBaseInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<WoOkrWorkReportBaseInfo>> result = new ActionResult<>();
		OkrWorkReportQueryService okrWorkReportQueryService = new OkrWorkReportQueryService();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		List<WoOkrWorkReportBaseInfo> wraps = null;
		List<OkrWorkReportBaseInfo> okrWorkReportBaseInfoList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrUserCache okrUserCache = null;
		WrapInFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInFilter.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(currentPerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache(e, currentPerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check && okrUserCache == null) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(currentPerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, currentPerson, request, null);
		}

		if (wrapIn == null) {
			wrapIn = new WrapInFilter();
		}

		if (check && okrUserCache.getLoginUserName() == null) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(currentPerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, currentPerson, request, null);
		}

		if (check) {
			wrapIn.addQueryInfoStatus("正常");
			wrapIn.addQueryProcessStatus("草稿");
			wrapIn.setProcessIdentity(okrUserCache.getLoginIdentityName());
		}
		if (check) {
			try {
				okrWorkReportBaseInfoList = okrWorkReportQueryService.listNextWithFilter(id, count, wrapIn);
				// 从数据库中查询符合条件的对象总数
				total = okrWorkReportQueryService.getCountWithFilter(wrapIn);
				wraps = WoOkrWorkReportBaseInfo.copier.copy(okrWorkReportBaseInfoList);
				String workDetail = null;
				if (wraps != null && !wraps.isEmpty()) {
					for (WoOkrWorkReportBaseInfo wrap : wraps) {
						workDetail = okrWorkDetailInfoService.getWorkDetailWithId(wrap.getWorkId());
						if (workDetail != null && !workDetail.isEmpty()) {
							wrap.setTitle(workDetail);
						}
					}
				}
				result.setCount(total);
				result.setData(wraps);
			} catch (Exception e) {
				Exception exception = new ExceptionWorkReportFilter(e);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		} else {
			result.setCount(0L);
			result.setData(new ArrayList<WoOkrWorkReportBaseInfo>());
		}
		return result;
	}
}