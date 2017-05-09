package com.x.bbs.assemble.control.jaxrs.replyinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyNotExistsException;
import com.x.bbs.entity.BBSReplyInfo;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSReplyInfo replyInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ReplyIdEmptyException();
				result.error( exception );
			}
		}		
		//判断主题信息是否存在
		if( check ){
			try {
				replyInfo = replyInfoService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "根据指定ID查询回复信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( replyInfo == null ){
				check = false;
				Exception exception = new ReplyNotExistsException( id );
				result.error( exception );
			}
		}		
		try {
			replyInfoService.delete( id );
			result.setData( new WrapOutId(id) );
			operationRecordService.replyOperation( effectivePerson.getName(), replyInfo, "DELETE", hostIp, hostName );
		} catch (Exception e) {
			check = false;
			Exception exception = new ReplyInfoProcessException( e, "根据指定ID删除回复信息时发生异常.ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}

}