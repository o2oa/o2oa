package com.x.okr.assemble.control.jaxrs.okrtaskhandled;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.InsufficientPermissionsException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.OkrSystemAdminCheckException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledDeleteException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrtaskhandled.exception.TaskHandledQueryByIdException;
import com.x.okr.entity.OkrTaskHandled;
import com.x.organization.core.express.Organization;

public class ExcuteDelete extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger(ExcuteDelete.class);

	protected ActionResult<WrapOutId> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrTaskHandled okrTaskHandled = null;
		Boolean check = true;
		Organization organization = new Organization(ThisApplication.context());
		Boolean hasPermission = false;
		try {
			hasPermission = organization.role().hasAny(effectivePerson.getName(), "OkrSystemAdmin");
			if (!hasPermission) {
				check = false;
				Exception exception = new InsufficientPermissionsException(effectivePerson.getName(), "OkrSystemAdmin");
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new OkrSystemAdminCheckException(e, effectivePerson.getName());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new TaskHandledIdEmptyException();
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				okrTaskHandled = okrTaskHandledService.get(id);
				if (okrTaskHandled == null) {
					check = false;
					Exception exception = new TaskHandledNotExistsException(id);
					result.error(exception);
					// logger.error( e, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskHandledQueryByIdException(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				okrTaskHandledService.delete(id);
				result.setData(new WrapOutId(id));
			} catch (Exception e) {
				check = false;
				Exception exception = new TaskHandledDeleteException(e, id);
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
			try {
				okrWorkDynamicsService.taskHandledDynamic(okrTaskHandled.getCenterId(), okrTaskHandled.getCenterTitle(),
						okrTaskHandled.getWorkId(), okrTaskHandled.getWorkTitle(), okrTaskHandled.getTitle(), id,
						"删除已办已阅", effectivePerson.getName(), "删除已办已阅：" + okrTaskHandled.getTitle(), "管理员删除已办已阅操作成功！",
						okrTaskHandled.getTargetName(), okrTaskHandled.getTargetIdentity());
			} catch (Exception e) {
				logger.warn("system record taskHandledDynamic get an exception");
				logger.error(e);
			}
		}
		return result;
	}

}