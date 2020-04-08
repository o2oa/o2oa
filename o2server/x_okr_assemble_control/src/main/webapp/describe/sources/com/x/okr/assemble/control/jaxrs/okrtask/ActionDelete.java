package com.x.okr.assemble.control.jaxrs.okrtask;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionOkrSystemAdminCheck;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskDelete;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskNotExists;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionTaskQueryById;
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrTask;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrTask okrTask = null;
		Boolean check = true;
		Boolean hasPermission = false;
		OkrUserCache okrUserCache = null;

		if (check) {
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getDistinguishedName());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new ExceptionUserNoLogin(effectivePerson.getDistinguishedName());
			result.error(exception);
		}
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionTaskIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				okrTask = okrTaskService.get(id);
				if (okrTask == null) {
					check = false;
					Exception exception = new ExceptionTaskNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionTaskQueryById(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				if ( !okrUserInfoService.getIsOkrManager( effectivePerson.getDistinguishedName())) {
					check = false;
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.OKRMANAGER);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionOkrSystemAdminCheck(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && hasPermission) {
			try {
				okrTaskService.delete(id);
				result.setData(new Wo(id));
			} catch (Exception e) {
				Exception exception = new ExceptionTaskDelete(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ("工作汇报".equals(okrTask.getDynamicObjectType())) {
				try {
					List<String> workTypeList = new ArrayList<String>();
					workTypeList.add(okrTask.getWorkType());
					okrWorkReportTaskCollectService.checkReportCollectTask(okrTask.getTargetIdentity(), workTypeList);
				} catch (Exception e) {
					logger.warn("待办信息删除成功，但对汇报者进行汇报待办汇总发生异常。", e);
				}
			}
		}
		if (check) {
			if( okrTask != null ) {
				WrapInWorkDynamic.sendWithTask( 
						okrTask, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginUserName(),
						"删除待办待阅",
						"管理员删除待办待阅操作成功！"
				);
			}
		}
		return result;
	}

	public static class Wo extends WoId {
		public Wo( String id ) {
			this.setId( id );
		}
	}
}