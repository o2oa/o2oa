package com.x.bbs.assemble.control.jaxrs.replyinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyNotExists;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSReplyInfo replyInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error(exception);
			}
		}
		// 判断主题信息是否存在
		if (check) {
			try {
				replyInfo = replyInfoService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "根据指定ID查询回复信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (replyInfo == null) {
				check = false;
				Exception exception = new ExceptionReplyNotExists(id);
				result.error(exception);
			}
		}
		try {
			replyInfoService.delete(id);

			Wo wo = new Wo();
			wo.setId( id );
			result.setData( wo );
			
			ApplicationCache.notify( BBSSubjectInfo.class );
			ApplicationCache.notify( BBSReplyInfo.class );
			ApplicationCache.notify( BBSSectionInfo.class );
			
			operationRecordService.replyOperation(effectivePerson.getDistinguishedName(), replyInfo, "DELETE", hostIp, hostName);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionReplyInfoProcess(e, "根据指定ID删除回复信息时发生异常.ID:" + id);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}