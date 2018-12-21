package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportDelete;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception.ExceptionWorkReportQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrTask;
import com.x.okr.entity.OkrWorkReportBaseInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		List<OkrTask> taskList = null;
		List<String> taskTargetName = new ArrayList<String>();
		Boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionWorkReportIdEmpty();
				result.error(exception);
			}
		}

		if (check) {
			try {
				okrWorkReportBaseInfo = okrWorkReportQueryService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkReportQueryById(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				okrWorkReportOperationService.delete(id, effectivePerson.getDistinguishedName());
				result.setData(new Wo(id));
			} catch (Exception e) {
				Exception exception = new ExceptionWorkReportDelete(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				taskList = okrTaskService.listIdsByReportId(id);
			} catch (Exception e) {
				check = false;
				logger.warn("system get task by report id got an exception");
				logger.error(e);
			}
		}

		if (check) {
			if (taskList != null && !taskList.isEmpty()) {
				List<String> workTypeList = new ArrayList<String>();
				for (OkrTask task : taskList) {
					if (!taskTargetName.contains(task.getTargetIdentity())) {
						try {
							workTypeList.clear();
							workTypeList.add(task.getWorkType());
							okrWorkReportTaskCollectService.checkReportCollectTask(task.getTargetIdentity(), workTypeList);
						} catch (Exception e) {
							logger.warn("待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。");
							logger.error(e);
						}
					}
				}
			}
		}

		if (check) {
			if ( okrWorkReportBaseInfo != null) {
				WrapInWorkDynamic.sendWithWorkReport( okrWorkReportBaseInfo, 
						effectivePerson.getDistinguishedName(), 
						okrUserCache.getLoginUserName(), 
						okrUserCache.getLoginIdentityName() , 
						"删除工作汇报信息", 
						"工作汇报信息删除成功！"
				);
			}
		}

		return result;
	}
}