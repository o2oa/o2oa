package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
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
import com.x.okr.assemble.control.service.OkrWorkReportPersonLinkService;
import com.x.okr.entity.OkrWorkReportPersonLink;

public class ActionListMyProcessPrevWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListMyProcessPrevWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
		OkrWorkReportPersonLinkService okrWorkReportPersonLinkService = new OkrWorkReportPersonLinkService();
		List<Wo> wraps = null;
		List<OkrWorkReportPersonLink> okrWorkReportPersonLinkList = null;
		Long total = 0L;
		EffectivePerson currentPerson = this.effectivePerson(request);
		OkrUserCache okrUserCache = null;
		com.x.okr.assemble.control.jaxrs.WorkPersonSearchFilter wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn(jsonElement,
					com.x.okr.assemble.control.jaxrs.WorkPersonSearchFilter.class);
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
			wrapIn = new com.x.okr.assemble.control.jaxrs.WorkPersonSearchFilter();
		}

		if (check && okrUserCache.getLoginUserName() == null) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(currentPerson.getDistinguishedName());
			result.error(exception);
			// logger.error( e, currentPerson, request, null);
		}

		if (check) {
			wrapIn.addQueryInfoStatus("正常");
			// 待处理|处理中|已处理
			wrapIn.addQueryProcessStatus("已处理");
			wrapIn.setProcessIdentity(okrUserCache.getLoginIdentityName());
		}
		if (check) {
			try {
				okrWorkReportPersonLinkList = okrWorkReportPersonLinkService.listPrevWithFilter(id, count, wrapIn);
				// 从数据库中查询符合条件的对象总数
				total = okrWorkReportPersonLinkService.getCountWithFilter(wrapIn);
				wraps = Wo.copier.copy(okrWorkReportPersonLinkList);
				String workDetail = null;
				if (wraps != null && !wraps.isEmpty()) {
					for (Wo wrap : wraps) {
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
			result.setData(new ArrayList<Wo>());
		}
		return result;
	}
	
	public static class Wo extends OkrWorkReportPersonLink{

		private static final long serialVersionUID = -5076990764713538973L;


		public static WrapCopier<OkrWorkReportPersonLink, Wo> copier = WrapCopierFactory.wo(OkrWorkReportPersonLink.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}