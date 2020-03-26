package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

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
import com.x.okr.assemble.control.jaxrs.okrtask.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionInsufficientPermissions;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionOkrSystemAdminCheck;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledDelete;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledNotExists;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.ExceptionTaskHandledQueryById;
import com.x.okr.assemble.control.jaxrs.queue.WrapInWorkDynamic;
import com.x.okr.entity.OkrTaskHandled;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OkrTaskHandled okrTaskHandled = null;
		Boolean check = true;
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
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionTaskHandledIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				okrTaskHandled = okrTaskHandledService.get(id);
				if (okrTaskHandled == null) {
					check = false;
					Exception exception = new ExceptionTaskHandledNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionTaskHandledQueryById(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				okrTaskHandledService.delete(id);
				result.setData(new Wo(id));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionTaskHandledDelete(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ("工作汇报".equals(okrTaskHandled.getDynamicObjectType())) {
				try {
					List<String> workTypeList = new ArrayList<String>();
					workTypeList.add(okrTaskHandled.getWorkType());
					okrWorkReportTaskCollectService.checkReportCollectTask(okrTaskHandled.getTargetIdentity(),
							workTypeList);
				} catch (Exception e) {
					logger.warn("已办信息删除成功，但对汇报者进行汇报已办汇总发生异常。");
					logger.error(e);
				}
			}
		}
		if (check) {
			if( okrTaskHandled != null ) {
				WrapInWorkDynamic.sendWithTaskHandled( 
						okrTaskHandled, 
						effectivePerson.getDistinguishedName(),
						okrUserCache.getLoginUserName(),
						okrUserCache.getLoginUserName(),
						"删除已办已阅",
						"管理员删除已办已阅操作成功！"
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