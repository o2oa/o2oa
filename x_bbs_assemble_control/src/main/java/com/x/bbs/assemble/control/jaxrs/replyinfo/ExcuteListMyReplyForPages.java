package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.CountEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.PageEmptyException;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ReplyInfoProcessException;
import com.x.bbs.entity.BBSReplyInfo;

public class ExcuteListMyReplyForPages extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListMyReplyForPages.class );
	
	protected ActionResult<List<WrapOutReplyInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn, Integer page, Integer count ) throws Exception {
		ActionResult<List<WrapOutReplyInfo>> result = new ActionResult<>();
		List<WrapOutReplyInfo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		
		if( check ){
			if( page == null ){
				check = false;
				Exception exception = new PageEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			if( count == null ){
				check = false;
				Exception exception = new CountEmptyException();
				result.error( exception );
			}
		}
		if( check ){
			try{
				total = replyInfoService.countReplyByUserName( effectivePerson.getName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ReplyInfoProcessException( e, "根据个人查询主题内所有的回复数量时发生异常。Person:" + effectivePerson.getName());
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listReplyByUserNameForPage( effectivePerson.getName(), page * count );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyInfoProcessException( e, "根据个人查询主题内所有的回复列表时发生异常。Person:" + effectivePerson.getName());
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			for( int i=0; replyInfoList != null && i< replyInfoList.size(); i++ ){
				if( i < replyInfoList.size() && i >= startIndex && i < endIndex ){
					replyInfoList_out.add( replyInfoList.get( i ) );
				}
			}
			if( replyInfoList_out != null && !replyInfoList_out.isEmpty() ){
				try {
					wraps = WrapTools.replyInfo_wrapout_copier.copy( replyInfoList_out );
					result.setData( wraps );
					result.setCount( total );
				} catch (Exception e) {
					check = false;
					Exception exception = new ReplyInfoProcessException( e, "将查询结果转换成可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

}