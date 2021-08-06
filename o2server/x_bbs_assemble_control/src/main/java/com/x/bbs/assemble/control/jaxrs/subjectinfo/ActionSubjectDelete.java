package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectQueryById;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSubjectDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSubjectDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Wo wo = new Wo();
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		// 判断主题信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectQueryById(e, id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (subjectInfo == null) {
				check = false;
				Exception exception = new ExceptionSubjectNotExists(id);
				result.error(exception);
			}
		}
		try {
			subjectInfoServiceAdv.delete(id);// 删除主题同时要将所有的回复内容全部删除			
			wo.setId( id );

			CacheManager.notify( BBSSubjectInfo.class );
			CacheManager.notify( BBSSectionInfo.class );
			CacheManager.notify( BBSForumInfo.class );
			
			// 记录操作日志
			operationRecordService.subjectOperation(effectivePerson.getDistinguishedName(), subjectInfo, "DELETE", hostIp, hostName);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionSubjectInfoProcess(e, "根据指定ID删除主题信息时发生异常.ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {

	}
}