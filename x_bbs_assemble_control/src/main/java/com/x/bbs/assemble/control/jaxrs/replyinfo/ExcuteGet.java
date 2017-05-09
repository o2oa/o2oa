package com.x.bbs.assemble.control.jaxrs.replyinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyNotExistsException;
import com.x.bbs.entity.BBSReplyInfo;

public class ExcuteGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutReplyInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutReplyInfo> result = new ActionResult<>();
		
		WrapOutReplyInfo wrap = null;
		BBSReplyInfo replyInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ReplyIdEmptyException();
				result.error( exception );
			}
		}
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
			if( replyInfo != null ){
				try {
					wrap = WrapTools.replyInfo_wrapout_copier.copy( replyInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyInfoProcessException( e, "将查询结果转换成可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new ReplyNotExistsException( id );
				result.error( exception );
			}
		}
		return result;
	}

}